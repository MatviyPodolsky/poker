package com.kubatatami.poker.activities;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.AvatarUtils;
import com.kubatatami.poker.utils.GameHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_gameover)
public class GameOverActivity extends BaseActivity {

    @Pref
    Settings_ settings;

    @Bean
    GameHelper gameHelper;

    @ViewById(R.id.gameover_avatar)
    protected ImageView avatar;
    @ViewById(R.id.gameover_name)
    protected TextView name;

    @ViewById(R.id.gameover_title)
    protected TextView title;

    @Extra
    protected Boolean won = false;

    @AfterViews
    protected void start() {
        if (won) {
            animateBackground(R.drawable.join_game_bgr, findViewById(R.id.main_background));
            title.setText(getString(R.string.you_won_game));
        } else {
            animateBackground(R.drawable.host_game_bgr, findViewById(R.id.main_background));
            title.setText(getString(R.string.you_lost_game));
        }

        name.setText(settings.name().get());
        AvatarUtils.showThumb(avatar, settings.image().get());

    }

    @Click(R.id.main_background)
    protected void viewClick() {
        openMenuActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openMenuActivity();
    }

    private void openMenuActivity() {
        MenuActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        gameHelper.clearGameHelper();
        finish();
    }
}
