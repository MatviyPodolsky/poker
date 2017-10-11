package com.kubatatami.poker.connection;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubatatami.poker.Listener.ClientDiscoveryListener;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.ServerUser;
import com.kubatatami.poker.model.User;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.BackgroundExecutor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * Created by Kuba on 28/09/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ClientSession {

    @RootContext
    protected Context context;
    @Pref
    protected Settings_ settings;

    protected final static String READ_THREAD = "READ_THREAD";

    protected WifiManager wifi;
    protected WifiManager.MulticastLock lock;
    protected JmDNS jmDNS;
    protected Pair<ServerUser, Socket> serverInfo;

    protected ClientDiscoveryListener clientDiscoveryListener;
    protected final Map<ServerUser, Socket> serversSockets = new HashMap<ServerUser, Socket>();
    protected final ObjectMapper mapper = new ObjectMapper();
    protected Handler handler = new Handler();
    protected boolean searching;

    @AfterInject
    protected void switchContext() {
        context = context.getApplicationContext();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    protected final ServiceListener serviceListener = new ServiceListener() {
        @Override
        public void serviceAdded(final ServiceEvent event) {
            if (event.getName().contains("Poker")) {
                Log.i("client", "serviceAdded");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jmDNS.requestServiceInfo(event.getType(), event.getName());
                    }
                },500);
            }
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            Log.i("client", "serviceRemoved");

        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            if (event.getName().contains("Poker")) {
                Log.i("client", "serviceResolved");
                startConnections(event.getInfo().getInet4Addresses()[0], ServerSession.PORT == 0 ? event.getInfo().getPort() : ServerSession.PORT);
            }
        }
    };




    protected void send(Socket socket, PokerCommand command) throws IOException {
        synchronized (socket) {
            String msg = mapper.writeValueAsString(command);
            byte[] sendMsg = msg.getBytes();
            Log.i("WriteMessage", "writeByte:" + sendMsg.length + " msg:" + msg);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(sendMsg.length);
            dataOutputStream.flush();
            outputStream.write(sendMsg);
            outputStream.flush();
        }
    }

    protected void sleep(int sleep){
        try {

            Thread.sleep(sleep);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    @Background(id = READ_THREAD)
    protected void startConnections(InetAddress address, int port) {
        int i = 0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            wifi.setTdlsEnabled(address, true);
        }

        Socket socket = null;
        while (socket == null && i < 3) {
            try {
                socket = new Socket(address, port);
            } catch (Exception e) {
                e.printStackTrace();
                i++;
                sleep(500);
            }
        }
        if (socket != null) {
            try {
                socket.setTcpNoDelay(true);
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                send(socket, new PokerCommand(androidId, settings.name().get(), settings.image().get(), settings.cash().get()));
                readSocket(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    @Background(id = READ_THREAD)
    protected void readSocket(Socket socket) {
        ServerUser user = null;
        byte[] byteSize = new byte[4];

        try {
            socket.setSoTimeout(ServerSession.PING_DELAY*2);
            InputStream inputStream = new BufferedInputStream(socket.getInputStream());
            OutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            while (!Thread.currentThread().isInterrupted()) {
                int readByte = 0;
                while(readByte<4) {
                    int currentRead=inputStream.read(byteSize,readByte,byteSize.length-readByte);
                    if(currentRead<0){
                        throw new Exception("Lost");
                    }
                    readByte+=currentRead;
                }
                int msgLength = byteArrayToInt(byteSize);
                if (msgLength != 0) {
                    byte[] msg = new byte[msgLength];
                    readByte = 0;
                    while(readByte<msgLength) {
                        int currentRead=inputStream.read(msg,readByte,msg.length-readByte);
                        if(currentRead<0){
                            throw new Exception("Lost");
                        }
                        readByte+=currentRead;
                    }
                    Log.i("ReadMessage", "readByte:" + readByte + " msg:" + new String(msg));

                    if (readByte == msgLength) {
                        PokerCommand message = mapper.readValue(msg, PokerCommand.class);
                        if (message.type.equals(CommandType.HELLO)) {
                            if (serversSockets.containsKey(message.user)) {
                                throw new Exception("Already exists");
                            }
                            user = new ServerUser(message.user);
                        }
                        if (message.type.equals(CommandType.CLOSE) && user != null) {
                            throw new Exception("Lost");
                        } else if (message.type.equals(CommandType.USER_LIST) && user != null) {
                            user.userList = message.users;
                            user.entryFee=message.entryFee;
                            user.gameType=message.gameType;
                            user.gameActive=message.gameActive;
                            synchronized (serversSockets) {
                                serversSockets.put(user, socket);
                            }
                            onServerFound(user);
                        } else if (serverInfo != null && serverInfo.first.equals(user)) {
                            onServerMessage(message);
                        }

                    } else {
                        throw new Exception("Wrong message size:"+readByte+" expected:"+msgLength);
                    }
                } else {
                    synchronized (socket) {
                        outputStream.write(new byte[4]);
                        outputStream.flush();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            synchronized (serversSockets) {
                serversSockets.remove(user);
            }
            if (user != null) {
                onServerLost(user);
            }
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void chooseServer(ServerUser user) {
        Socket socket = serversSockets.get(user);
        if (socket != null) {
            closeInternal();
            serverInfo = new Pair<ServerUser, Socket>(user, socket);
            sendToServer(new PokerCommand(CommandType.JOIN));
        } else {
            throw new RuntimeException("No user found");
        }
    }

    @Background
    public void sendToServer(PokerCommand pokerCommand) {
        try {
            send(serverInfo.second, pokerCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getServerUser() {
        return serverInfo.first;
    }

    public Set<ServerUser> getUsers() {
        return serversSockets.keySet();
    }

    public void startDiscovery(ClientDiscoveryListener discoveryListener) {
        closeInternal();
        lock.acquire();
        this.clientDiscoveryListener = discoveryListener;

        WifiInfo wifiinfo = wifi.getConnectionInfo();
        int intaddr = wifiinfo.getIpAddress();
        byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
        startDiscoveryInternal(byteaddr);
    }

    public void stop(){
        closeInternal();
        onStop();
    }

    @Background
    public void startDiscoveryInternal(byte[] byteaddr) {
        try {
            onStart();
            InetAddress inetAddress = InetAddress.getByAddress(byteaddr);
            String hostAddress = inetAddress.getHostAddress();
            Log.i("CREATE JmDNS", hostAddress);
            jmDNS = JmDNS.create(inetAddress);
            jmDNS.addServiceListener(ServerSession.SERVICE_TYPE, serviceListener);
            if(hostAddress.equals("0.0.0.0")) {
                connectToHotSpotClient();
            }
            else if(hostAddress.contains("192.168.43")) {
                startConnections(InetAddress.getByName("192.168.43.1"), ServerSession.PORT);
            }
        } catch (Exception e) {
            onStop();
            e.printStackTrace();
        }
    }

    public void connectToHotSpotClient() {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length>0) {
                    try {
                        startConnections(InetAddress.getByName(splitted[0]), ServerSession.PORT);
                    }catch (UnknownHostException ex){

                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void close() {
        closeAllInternal();
    }

    @Background
    public void closeInternal() {
        if (lock.isHeld()) {
            lock.release();
        }
        if (jmDNS != null) {
            jmDNS.removeServiceListener(ServerSession.SERVICE_TYPE, serviceListener);
        }
        try {
            if (jmDNS != null) {
                jmDNS.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void closeAllInternal() {
        closeInternal();
        closeSockets(null);
    }

    @Background
    public void closeSockets(Socket exceptSocket) {
        synchronized (serversSockets) {
            for (Socket socket : serversSockets.values()) {
                if (socket != exceptSocket) {
                    try {
                        send(socket, new PokerCommand(CommandType.CLOSE));
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            serversSockets.clear();
        }
    }

    @UiThread
    protected void onStart() {
        searching=true;
        clientDiscoveryListener.onStart();
    }

    @UiThread
    protected void onStop() {
        searching=false;
        clientDiscoveryListener.onStop();
    }

    public boolean isSearching() {
        return searching;
    }

    @UiThread
    protected void onServerFound(ServerUser user) {
        clientDiscoveryListener.onServerFound(user);
    }

    @UiThread
    protected void onServerLost(ServerUser user) {
        if(clientDiscoveryListener != null){
            clientDiscoveryListener.onServerLost(user);
        }
    }

    @UiThread
    protected void onServerMessage(PokerCommand command) {
        clientDiscoveryListener.onServerMessage(command);
    }

    @AfterInject
    protected void afterInject() {
        wifi = (android.net.wifi.WifiManager)
                context.getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("HeeereDnssdLock");

        BackgroundExecutor.setExecutor(Executors.newCachedThreadPool());
    }

    public void setClientDiscoveryListener(ClientDiscoveryListener clientDiscoveryListener) {
        this.clientDiscoveryListener = clientDiscoveryListener;
    }
}
