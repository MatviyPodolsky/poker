package com.kubatatami.poker.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.GameType;
import com.kubatatami.poker.utils.GameHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_holdem_setup)
public class HoldemSetupActivity extends BaseActivity {

    @Pref
    protected Settings_ settings;

    @Bean
    protected GameHelper gameHelper;

    @ViewById(R.id.holdem_setup_my_deck_image)
    protected ImageView deck;

    @ViewById(R.id.holdem_setup_small_blind_value)
    protected TextView smallBlindValue;

    @ViewById(R.id.holdem_setup_big_blind_value)
    protected TextView bigBlindValue;

    @ViewById(R.id.holdem_setup_entry_fee_value)
    protected TextView entryFeeValue;

    public static final int REQUEST_CODE = 10002;

    private int smallBlind;
    private int entryFee;


    @AfterViews
    public void start() {
        deck.setImageLevel(settings.deck().get());
        smallBlind = settings.smallBlind().get();
        entryFee = settings.entryFee().get();
        smallBlindValue.setText(getString(R.string.cash_, smallBlind));
        bigBlindValue.setText(getString(R.string.cash_, smallBlind * 2));
        entryFeeValue.setText(getString(R.string.cash_, entryFee));

        setAnimationButton(findViewById(R.id.holdem_setup_play_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.smallBlind().put(smallBlind);
                settings.entryFee().put(entryFee);
                HostActivity_.intent(HoldemSetupActivity.this).gameType(GameType.TEXAS_HOLDEM).start();
            }
        });

        setAnimationButton(findViewById(R.id.holdem_setup_my_deck_button), findViewById(R.id.holdem_setup_my_deck_image), false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDeckActivity_.intent(HoldemSetupActivity.this).startForResult(REQUEST_CODE);
            }
        });
        animateBackground(R.drawable.host_game_bgr, findViewById(R.id.main_background));

    }

    @Click(R.id.holdem_setup_small_blind_decrease)
    public void smallBlindDecrease() {
        if(smallBlind == 1){
            return;
        }else{
            smallBlind--;
            smallBlindValue.setText(getString(R.string.cash_, smallBlind));
            bigBlindValue.setText(getString(R.string.cash_, smallBlind * 2));
        }
    }

    @Click(R.id.holdem_setup_small_blind_increase)
    public void smallBlindIncrease() {
        if(smallBlind == 5){
            return;
        }else{
            smallBlind++;
            smallBlindValue.setText(getString(R.string.cash_, smallBlind));
            bigBlindValue.setText(getString(R.string.cash_, smallBlind * 2));
        }
    }

    @Click(R.id.holdem_setup_entry_fee_decrease)
    public void entryFeeDecrease() {
        if(entryFee == 100){
            return;
        }else{
            entryFee -= 100;
            entryFeeValue.setText(getString(R.string.cash_, entryFee));
        }
    }

    @Click(R.id.holdem_setup_entry_fee_increase)
    public void entryFeeIncrease() {
        if(entryFee == 1000){
            return;
        }else{
            entryFee += 100;
            entryFeeValue.setText(getString(R.string.cash_, entryFee));
        }
    }


    @OnActivityResult(REQUEST_CODE)
    public void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int deckInt = data.getIntExtra("deckView", 1);
            settings.deck().put(deckInt);
            deck.setImageLevel(deckInt);
        }
    }

}
