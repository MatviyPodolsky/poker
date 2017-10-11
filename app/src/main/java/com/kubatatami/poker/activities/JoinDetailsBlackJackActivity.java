package com.kubatatami.poker.activities;

import android.widget.TextView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.adapters.ClientDiscoveryAdapter;
import com.kubatatami.poker.connection.ClientSession;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.ServerUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Kuba on 30/09/14.
 */
@EActivity(R.layout.activity_join_details)
public class JoinDetailsBlackJackActivity extends BaseActivity {

    @Bean
    protected ClientSession clientSession;
    @ViewById(R.id.join_detail_status)
    protected TextView statusView;

    protected boolean accept = false;

    @AfterViews
    protected void afterViews() {
        animateBackground(R.drawable.join_game_bgr, findViewById(R.id.main_background));
        statusView.setText("Wait for accept...");
        clientSession.setClientDiscoveryListener(new ClientDiscoveryAdapter() {

            @Override
            public void onServerLost(ServerUser user) {
                finish();
            }

            @Override
            public void onServerMessage(PokerCommand command) {
                if (command.type.equals(CommandType.ACCEPT)) {
                    accept = true;
                    statusView.setText("Wait for start...");
                } else if (command.type.equals(CommandType.START)) {
                    if (accept) {
                        GameActivity_.intent(JoinDetailsBlackJackActivity.this).start();
                        finish();
                    } else {
                        finish();
                    }
                } else if (command.type.equals(CommandType.DISACCEPT)) {
                    accept = false;
                    statusView.setText("Wait for accept...");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        clientSession.sendToServer(new PokerCommand(CommandType.JOIN_CANCEL));
        super.onBackPressed();
    }


}
