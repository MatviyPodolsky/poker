package com.kubatatami.poker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.AvatarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.net.URI;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    protected final static int SELECT_DECK = 10001;

    @Pref
    protected Settings_ settings;

    @ViewById(R.id.settings_sounds)
    protected ImageView soundImageView;

    @ViewById(R.id.settings_name_text)
    protected TextView nameText;

    @ViewById(R.id.settings_my_coins_text)
    protected TextView myCoinsText;

    @ViewById(R.id.settings_my_deck_image)
    protected ImageView deckView;

    @ViewById(R.id.settings_avatar)
    protected ImageView avatarView;

    @AfterViews
    protected void start() {
        deckView.setImageLevel(settings.deck().get());
        soundImageView.setImageLevel(settings.sound().get() ? 1 : 0);
        nameText.setText(settings.name().get());
        setCoins();
        setAnimationButton(findViewById(R.id.settings_my_deck_button), findViewById(R.id.settings_my_deck_image), false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDeckActivity_.intent(SettingsActivity.this).startForResult(SELECT_DECK);
            }
        });
        animateBackground(R.drawable.settings_bgr, findViewById(R.id.main_background));
    }


    private void setCoins(){
        myCoinsText.setText(getString(R.string.cash_, settings.cash().get() + ""));
    }
    @Click(R.id.settings_sounds)
    protected void soundClick() {
        if (settings.sound().get()) {
            settings.sound().put(false);
        } else {
            settings.sound().put(true);
        }
        soundImageView.setImageLevel(settings.sound().get() ? 1 : 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AvatarUtils.showThumb(avatarView, settings.image().get());
    }


    @OnActivityResult(SELECT_DECK)
    protected void onDeckResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int deckInt = data.getIntExtra("deckView", 1);
            settings.deck().put(deckInt);
            deckView.setImageLevel(deckInt);
        }
    }

    @Click(R.id.settings_edit_button)
    protected void editClick() {
        WelcomeActivity_.intent(this).edit(true).start();
    }

    @Click(R.id.settings_get_more_button)
    protected void getMoreButton() {

        int cash = settings.cash().get();
        cash += 1000;
        settings.cash().put(cash);
        setCoins();
    }

    @Click(R.id.settings_view_button)
    protected void statisticsClick() {
        StatisticsActivity_.intent(this).start();
    }
}
