package com.kubatatami.poker.activities;

import android.view.View;

import com.kubatatami.poker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_choose_game)
public class ChooseGameActivity extends BaseActivity {

    @AfterViews
    protected void start() {

        setAnimationButton(findViewById(R.id.texas_holdem_button), findViewById(R.id.texas_holdem_image), false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoldemSetupActivity_.intent(ChooseGameActivity.this).start();
            }
        });


        setAnimationButton(findViewById(R.id.blackjack_button), findViewById(R.id.blackjack_image), false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivityBlackJack_.intent(ChooseGameActivity.this).start();
            }
        });

        setAnimationButton(findViewById(R.id.hearts_button), findViewById(R.id.hearts_image), false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setAnimationButton(findViewById(R.id.war_button), findViewById(R.id.war_image), false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        animateBackground(R.drawable.host_game_bgr, findViewById(R.id.main_background));
    }


}
