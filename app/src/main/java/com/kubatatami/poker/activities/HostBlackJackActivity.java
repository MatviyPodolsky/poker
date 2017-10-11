package com.kubatatami.poker.activities;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kubatatami.judonetworking.observers.HolderView;
import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.kubatatami.poker.Listener.ServerListener;
import com.kubatatami.poker.R;
import com.kubatatami.poker.connection.ServerSession;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.GameType;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.User;
import com.kubatatami.poker.utils.AvatarUtils;
import com.kubatatami.poker.utils.GameHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_host)
public class HostBlackJackActivity extends BaseActivity implements ServerListener {


    protected final static int TIME_TICK = 400;
    @Extra
    protected GameType gameType;
    @Pref
    protected Settings_ settings;
    @Bean
    protected ServerSession serverSession;
    @Bean
    protected GameHelper gameHelper;
    @ViewById(R.id.host_all_players)
    protected ListView playersList;
    @ViewById(R.id.accepted_players)
    protected ListView acceptedList;

    @ViewById(R.id.host_tap_to_add)
    protected View tapToAddView;

    @ViewById(R.id.host_tap_to_remove)
    protected View tapToRemoveView;

    @ViewById(R.id.host_state_container)
    protected View hostStateContainer;
    @ViewById(R.id.host_state_dots)
    protected TextView dotsContainer;

    @ViewById(R.id.host_play)
    protected View hostPlayView;

    protected ObserverAdapter<User> allUserAdapter;
    protected ObserverAdapter<User> acceptedAdapter;


    int delayCounter;

    static class Holder {
        @HolderView(R.id.user_avatar)
        protected ImageView avatarView;
    }

    static class UserAdapter extends ObserverAdapter<User> {

        public UserAdapter(Context context, int layout) {
            super(context, layout);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent, Holder.class);
            Holder holder = (Holder) view.getTag();
            User user = getItem(position);
            try {
                holder.avatarView.setImageBitmap(AvatarUtils.loadThumbnail(user.image));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return view;
        }

    }

    @AfterViews
    protected void afterViews() {
        settings.deckTmp().put(settings.deck().get());
        allUserAdapter = new UserAdapter(this, R.layout.item_user);
        acceptedAdapter = new UserAdapter(this,  R.layout.item_user_accepted);
        acceptedAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                setAnimationButtonEnabled(hostPlayView, acceptedAdapter.getCount() > 0);
            }

            @Override
            public void onInvalidated() {
                setAnimationButtonEnabled(hostPlayView, acceptedAdapter.getCount() > 0);
            }
        });
        playersList.setAdapter(allUserAdapter);
        acceptedList.setAdapter(acceptedAdapter);
        serverSession.startServer(this, settings.entryFee().get(), gameType);
        tapToAddView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tapToAddView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                tapToAddView.measure(tapToAddView.getMeasuredHeight(), tapToAddView.getMeasuredWidth());
                tapToRemoveView.measure(tapToRemoveView.getMeasuredHeight(), tapToRemoveView.getMeasuredWidth());
                tapToAddView.requestLayout();
            }
        });
        setAnimationButton(hostPlayView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
            }
        });
        animateBackground(R.drawable.host_game_bgr, findViewById(R.id.main_background));
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameHelper.clearGameHelper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(serverSession.isEnabled()) {
            List<User> users = serverSession.getAllUsers();
            for (int i = 0; i < acceptedAdapter.getCount(); i++) {
                if (!users.contains(acceptedAdapter.getItem(i))) {
                    acceptedAdapter.remove(acceptedAdapter.getItem(i));
                }
            }
            for (int i = 0; i < allUserAdapter.getCount(); i++) {
                if (!users.contains(allUserAdapter.getItem(i))) {
                    allUserAdapter.remove(allUserAdapter.getItem(i));
                }
            }

            serverSession.setGameMode(false);
            serverSession.setServerListener(this);
        }else{
            acceptedAdapter.clear();
            allUserAdapter.clear();
            serverSession.startServer(this, settings.entryFee().get(), gameType);
        }

        //adding runnable
        handler.post(animationButtonRunnable);
        handler.post(tickRunnable);
    }

    @ItemClick(R.id.host_all_players)
    protected void hostAllClick(User user) {
        allUserAdapter.remove(user);
        acceptedAdapter.add(user);
        serverSession.accept(user);
    }

    @ItemClick(R.id.accepted_players)
    protected void acceptedClick(User user) {
        acceptedAdapter.remove(user);
        allUserAdapter.add(user);
        serverSession.disaccept(user);
    }

    @Override
    public void onStop(){
        handler.removeCallbacks(animationButtonRunnable);
        handler.removeCallbacks(tickRunnable);
//        serverSession.setServerListener(null);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        serverSession.close();
        super.onBackPressed();
    }

    protected void playClick() {
        if (acceptedAdapter.getCount() > 0) {
            serverSession.sendAllAccept(new PokerCommand(CommandType.START));
            serverSession.setGameMode(true);
            GameActivityBlackJack_.intent(this).host(true).start();
        } else {
            Toast.makeText(this, R.string.no_connected_users, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onServerError() {
        Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClientConnect(User user) {
        allUserAdapter.add(user);
        hostStateContainer.setVisibility(View.GONE);

    }

    @Override
    public void onClientDisconnect(User user) {

        allUserAdapter.remove(user);
        acceptedAdapter.remove(user);
        if ((allUserAdapter.getCount() + acceptedAdapter.getCount()) == 0) {
            hostStateContainer.setVisibility(View.GONE);
        }
    }

    protected Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {


            String dots = "";
            for (int i = 0; i < (delayCounter % 4); i++) {
                dots += ".";
            }
            dotsContainer.setText(dots);

            delayCounter++;
            handler.postDelayed(this, TIME_TICK);
        }

    };


    protected Runnable animationButtonRunnable = new Runnable() {
        @Override
        public void run() {
            setAnimationButtonEnabled(hostPlayView, acceptedAdapter.getCount() > 0);
        }
    };

    @Override
    public void onServerStart() {
    }

    @Override
    public void onServerClose() {
    }

    @Override
    public void onMessage(User user, PokerCommand command) {

    }
}
