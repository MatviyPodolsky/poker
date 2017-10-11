package com.kubatatami.poker.activities;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kubatatami.judonetworking.observers.HolderView;
import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.kubatatami.poker.Listener.ClientDiscoveryListener;
import com.kubatatami.poker.R;
import com.kubatatami.poker.adapters.ClientDiscoveryAdapter;
import com.kubatatami.poker.connection.ClientSession;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.ServerUser;
import com.kubatatami.poker.utils.AvatarUtils;
import com.kubatatami.poker.utils.GameHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_join)
public class JoinBlackJackActivity extends BaseActivity {


    protected final static int TIME_TICK = 400;
    protected final static int SEARCH_PERIOD = 13000;


    @Pref
    protected Settings_ settings;
    @Bean
    protected ClientSession clientSession;

    @Bean
    protected GameHelper gameHelper;

    @ViewById(R.id.join_list)
    protected ListView serverListView;
    @ViewById(R.id.join_state)
    protected TextView stateView;
    @ViewById(R.id.join_state_dots)
    protected TextView stateDotsView;
    @ViewById(R.id.join_list_empty)
    protected TextView emptyView;

    protected Handler handler = new Handler();
    protected ObserverAdapter<ServerUser> adapter;
    protected List<ServerUser> serverList = new ArrayList<ServerUser>();

    int delayCounter;

    protected Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {

            if (delayCounter > SEARCH_PERIOD / TIME_TICK) {
                stopAfterDelay();
            } else {
                String dots = "";
                for (int i = 0; i < (delayCounter % 4); i++) {
                    dots += ".";
                }
                stateDotsView.setText(dots);

                delayCounter++;
                handler.postDelayed(this, TIME_TICK);
            }
        }
    };

    protected ClientDiscoveryListener clientDiscoveryListener = new ClientDiscoveryAdapter() {
        @Override
        public void onStart() {
            Log.i("client", "onStart");
            delayCounter=0;
            setAnimationButtonEnabled(stateView,false);
            stateView.setText(R.string.looking_for_games);
            handler.post(tickRunnable);
        }

        @Override
        public void onStop() {
            Log.i("client", "onStop");
            setAnimationButtonEnabled(stateView,true);
            stateDotsView.setText("");
            stateView.setText(R.string.try_again);
        }

        @Override
        public void onServerFound(ServerUser user) {
            Log.i("client", "onServerFound");
            int i = serverList.indexOf(user);
            if (i == -1) {
                serverList.add(user);
            } else {
                serverList.set(i, user);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onServerLost(ServerUser user) {
            Log.i("client", "onServerLost");
            adapter.remove(user);
        }
    };

    protected void stopAfterDelay() {
        clientSession.stop();
    }

    @ItemClick(R.id.join_list)
    protected void serverClick(ServerUser serverUser) {
        clientSession.chooseServer(serverUser);
        if(serverUser.gameActive){
            GameActivity_.intent(this).start();
        }else {
            JoinDetailsActivity_.intent(this).start();
        }
    }

    static class Holder{
        @HolderView(R.id.user_avatar)
        protected ImageView avatarView;
    }



    @AfterViews
    protected void afterViews() {
        settings.deckTmp().put(settings.deck().get());
        adapter = new ObserverAdapter<ServerUser>(this, serverList, R.layout.item_server_user) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent,Holder.class);
                Holder holder = (Holder) view.getTag();
                ServerUser user=getItem(position);
                try {
                    holder.avatarView.setImageBitmap(AvatarUtils.loadThumbnail(user.image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //TODO
                if(user.gameActive) {
                    view.setBackgroundColor(Color.RED);
                }else{
                    view.setBackgroundResource(R.drawable.rounded_black_bgr);

                }
                return view;
            }


        };
        serverListView.setEmptyView(emptyView);
        serverListView.setAdapter(adapter);
        animateBackground(R.drawable.join_game_bgr, findViewById(R.id.main_background));
        setAnimationButton(stateView,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                clientSession.startDiscovery(clientDiscoveryListener);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.clear();
        adapter.addAll(clientSession.getUsers());
    }

    @Override
    protected void onResume() {
        super.onResume();
        clientSession.startDiscovery(clientDiscoveryListener);
        gameHelper.clearGameHelper();
    }

    @Override
    public void onBackPressed() {
        clientSession.close();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(tickRunnable);
    }
}
