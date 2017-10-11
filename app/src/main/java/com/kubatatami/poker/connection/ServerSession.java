package com.kubatatami.poker.connection;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubatatami.poker.Listener.ServerListener;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.GameType;
import com.kubatatami.poker.model.PokerCommand;
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 * Created by Kuba on 28/09/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ServerSession {


    protected final static String LISTENER_THREAD = "LISTENER_THREAD";
    protected final static String READ_THREAD = "READ_THREAD";
    public final static String SERVICE_TYPE = "_fcgpoker._tcp.local.";
    public final static int PORT = 5542;
    public final static int PING_DELAY = 5000000;

    @RootContext
    protected Context context;
    @Pref
    protected Settings_ settings;

    protected ServerSocket serverSocket;
    protected JmDNS jmDNS;
    protected final Map<User, Socket> userSockets = new HashMap<>();
    protected final Set<User> acceptedUsers = new HashSet<>();
    protected final ObjectMapper mapper = new ObjectMapper();
    protected ServerListener serverListener;
    protected WifiManager wifi;
    protected WifiManager.MulticastLock multicastLock;
    protected int entryFee;
    protected GameType gameType;
    protected boolean gameMode;

    public void startServer(ServerListener serverListener, int entryFee, GameType gameType) {
        this.serverListener = serverListener;
        this.entryFee = entryFee;
        this.gameType = gameType;
        gameMode=false;
        userSockets.clear();
        acceptedUsers.clear();
        startServerInternal();
    }

    @AfterInject
    protected void switchContext(){
        context=context.getApplicationContext();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Background
    protected void startServerInternal(){
        try {
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true);
            int localPort = serverSocket.getLocalPort();
            ServiceInfo serviceInfo = ServiceInfo.create(
                    SERVICE_TYPE,
                    "Poker_" + settings.name().get(),
                    localPort,
                    "Poker game");
            WifiInfo wifiinfo = wifi.getConnectionInfo();
            int intaddr = wifiinfo.getIpAddress();
            byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
            Log.i("registerService",InetAddress.getByAddress(byteaddr).getHostAddress()+":"+localPort);
            startServerInternal(byteaddr,serviceInfo);
        } catch (IOException e) {
            e.printStackTrace();
            onServerError();
        }
    }

    @Background
    protected void startServerInternal(byte[] byteaddr,ServiceInfo serviceInfo) {
        try {

            jmDNS = JmDNS.create(InetAddress.getByAddress(byteaddr));
            jmDNS.registerService(serviceInfo);
            startListen();
            onStart();
        } catch (IOException e) {
            e.printStackTrace();
            onServerError();
        }
    }

    public boolean isEnabled() {
        return serverSocket!=null && !serverSocket.isClosed();
    }

    public void close() {
        closeInternal();
    }

    public void setServerListener(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void sendCommand(User user, PokerCommand command) {
        sendInternalCommand(user, command);
    }

    public Collection<User> getAcceptedUsers() {
        List<User> result = new ArrayList<>();
        for(User user : acceptedUsers){
            if(userSockets.containsKey(user)){
                result.add(user);
            }
        }
        return result;
    }

    public Collection<User> getDisconnectedUsers() {
        List<User> result = new ArrayList<>();
        for(User user : acceptedUsers){
            if(!userSockets.containsKey(user)){
                result.add(user);
            }
        }
        return result;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userSockets.keySet());
    }

    @AfterInject
    protected void afterInject() {
        wifi = (android.net.wifi.WifiManager)
                context.getSystemService(android.content.Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("HeeereDnssdLock");
        BackgroundExecutor.setExecutor(Executors.newCachedThreadPool());
    }

    @Background(id = LISTENER_THREAD)
    protected void startListen() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
                    wifi.setTdlsEnabled(socket.getInetAddress(), true);
                }
                readSocket(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Background
    public void sendAll(PokerCommand command) {
        for(Socket socket : userSockets.values()) {
            try {
                send(socket,command);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Background
    public void sendAllAccept(PokerCommand command) {
        for(User user : acceptedUsers) {
            Socket socket = userSockets.get(user);
            try {
                send(socket,command);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void forgetInactiveUsers(){
        for (Iterator<User> iterator = acceptedUsers.iterator(); iterator.hasNext();) {
            User user = iterator.next();
            if (!userSockets.containsKey(user)) {
                iterator.remove();
            }
        }
    }

    public void accept(User user){
        acceptedUsers.add(user);
        sendUser(user, new PokerCommand(CommandType.ACCEPT));
        sendAll(new PokerCommand(acceptedUsers,entryFee,gameType,gameMode));
    }

    public void disaccept(User user){
        acceptedUsers.remove(user);
        sendUser(user, new PokerCommand(CommandType.DISACCEPT));
        sendAll(new PokerCommand(acceptedUsers,entryFee,gameType,gameMode));
    }

    @Background
    public void sendUser(User user, PokerCommand command) {
        try {
            Socket socket = userSockets.get(user);
            if(socket!=null) {
                send(socket, command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void send(Socket socket, PokerCommand command) throws IOException {
        String msg = mapper.writeValueAsString(command);
        byte[] sendMsg = msg.getBytes();
        Log.i("WriteMessage", "writeByte:" + sendMsg.length + " msg:" + msg);
        synchronized (socket) {
            OutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(sendMsg.length);
            dataOutputStream.flush();
            outputStream.write(sendMsg);
            outputStream.flush();
        }
    }

    @Background(id = READ_THREAD)
    protected void readSocket(Socket socket) {

        User user = null;
        try {
            Thread.sleep(200);
            socket.setSoTimeout(PING_DELAY);
            String androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            send(socket, new PokerCommand(androidId, settings.name().get(), settings.image().get(), settings.cash().get()));
            InputStream inputStream = new BufferedInputStream(socket.getInputStream());
            OutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            byte[] byteSize = new byte[4];

            boolean ping=false;
            boolean join=false;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int readByte = 0;
                    while(readByte<4) {
                        int currentRead=inputStream.read(byteSize,readByte,byteSize.length-readByte);
                        if(currentRead<0){
                            throw new Exception("Lost");
                        }
                        readByte+=currentRead;
                    }
                    int msgLength = ClientSession.byteArrayToInt(byteSize);
                    ping=false;
                    if(msgLength>0) {
                        byte[] msg = new byte[msgLength];
                        readByte = 0;
                        while(readByte<msgLength) {
                            int currentRead=inputStream.read(msg,readByte,msg.length-readByte);
                            if(currentRead<0){
                                throw new Exception("Lost");
                            }
                            readByte+=currentRead;
                        }
                        if (readByte == msgLength) {
                            PokerCommand message = mapper.readValue(msg, PokerCommand.class);
                            if (message.type.equals(CommandType.HELLO)) {
                                if(gameMode && !acceptedUsers.contains(message.user)){
                                    send(socket,new PokerCommand(CommandType.CLOSE));
                                    throw new Exception("New user under game mode, socket close");
                                }
                                synchronized (userSockets) {
                                    userSockets.put(message.user, socket);
                                }
                                user = message.user;
                                sendAll(new PokerCommand(acceptedUsers,entryFee,gameType,gameMode));
                            } else if (message.type.equals(CommandType.CLOSE) && user != null) {
                                synchronized (userSockets) {
                                    userSockets.remove(user);
                                }
                                onClientDisconnect(user);
                                socket.close();
                            } else if (message.type.equals(CommandType.JOIN) && user != null) {
                                join=true;
                                onClientConnect(user);
                            } else if (message.type.equals(CommandType.JOIN_CANCEL) && user != null) {
                                if(join) {
                                    onClientDisconnect(user);
                                }
                                join=false;
                            } else if (user != null) {
                                onMessage(user, message);
                            }

                        } else {

                            throw new Exception("Wrong message");
                        }
                    }
                } catch (SocketTimeoutException socketTimeoutException) {
                    if(ping){
                        throw new Exception("Ping timeout");
                    }else {
                        ping = true;
                        synchronized (socket) {
                            outputStream.write(new byte[4]);
                            outputStream.flush();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            synchronized (userSockets) {
                userSockets.remove(user);
            }
            if (user != null) {
                onClientDisconnect(user);
            }
        }
    }


    @Background
    protected void closeInternal() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (multicastLock.isHeld()) {
            multicastLock.release();
        }

        BackgroundExecutor.cancelAll(LISTENER_THREAD, true);
        BackgroundExecutor.cancelAll(READ_THREAD, true);
        synchronized (userSockets) {
            for (Socket socket : userSockets.values()) {
                try {
                    send(socket,new PokerCommand(CommandType.CLOSE));
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        onClose();
    }

    @UiThread
    protected void unregisterService() {
        try {
            if (jmDNS != null) {
                jmDNS.unregisterAllServices();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Background
    protected void sendInternalCommand(User user, PokerCommand command) {
            Socket socket = userSockets.get(user);
            if (socket != null) {
                try {
                    send(socket, command);
                } catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
    }


    @UiThread
    protected void onStart() {
        if(serverListener != null){
            serverListener.onServerStart();
        }
    }

    @UiThread
    protected void onServerError() {
        unregisterService();
        serverListener.onServerError();
    }

    @UiThread
    protected void onClientConnect(User user) {
        serverListener.onClientConnect(user);
    }

    @UiThread
    protected void onClientDisconnect(User user) {
        //acceptedUsers.remove(user);
        if (isEnabled()) {
            serverListener.onClientDisconnect(user);
        }
    }

    @UiThread
    protected void onClose() {
        unregisterService();
        if(serverListener != null){
            serverListener.onServerClose();
        }
    }

    @UiThread
    protected void onMessage(User user, PokerCommand command) {
        serverListener.onMessage(user, command);
    }

    public boolean isGameMode() {
        return gameMode;
    }

    public void setGameMode(boolean gameMode) {
        this.gameMode = gameMode;
    }

}
