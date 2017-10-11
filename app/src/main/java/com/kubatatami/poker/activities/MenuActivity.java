package com.kubatatami.poker.activities;

import android.view.View;

import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.dialogs.QuitDialogFragment;
import com.kubatatami.poker.dialogs.QuitDialogFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Kuba on 16/09/14.
 */
@EActivity(R.layout.activity_menu)
public class MenuActivity extends BaseActivity implements QuitDialogFragment.OnQuitListener {

    @Pref
    protected Settings_ settings;

//    private AdView mAdView;


    @AfterViews

    /**
     * Adding init method for instating ads sdk
     *
     */
    protected void init(){
//        mAdView = (AdView) findViewById(R.id.bannerChoseGameView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(deviceUtils.getMd5DeviceId(this))
//                .build();
//        mAdView.loadAd(adRequest);
    }

    @AfterViews
    protected void afterViews() {
        setAnimationButton(findViewById(R.id.menu_join),
                findViewById(R.id.menu_join_img), true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (settings.name().get().equals("")) {
                            WelcomeActivity_.intent(MenuActivity.this).activityClass(JoinActivity_.class).start();
                        } else {
                            JoinActivity_.intent(MenuActivity.this).start();
                        }
                    }
                });

        setAnimationButton(findViewById(R.id.menu_host),
                findViewById(R.id.menu_host_img),
                true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (settings.name().get().equals("")) {
                            WelcomeActivity_.intent(MenuActivity.this).activityClass(ChooseGameActivity_.class).start();
                        } else {
                            ChooseGameActivity_.intent(MenuActivity.this).start();
                        }
                    }
                });

        setAnimationButton(findViewById(R.id.menu_settings),
                findViewById(R.id.menu_settings_img),
                true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (settings.name().get().equals("")) {
                            WelcomeActivity_.intent(MenuActivity.this).activityClass(SettingsActivity_.class).start();
                        } else {
                            SettingsActivity_.intent(MenuActivity.this).start();
                        }
                    }
                });

        animateBackground(R.drawable.join_game_bgr, findViewById(R.id.menu_bgr));
    }

    @Override
    public void onBackPressed() {
        QuitDialogFragment_.builder().build().show(getFragmentManager(), "quit");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume the AdView.
//        mAdView.resume();
    }

    @Override
    public void onPause() {
        // Pause the AdView.
//        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
//        mAdView.destroy();
//        mAdView = null;
        super.onDestroy();
    }

    @Override
    public void onQuit(int requestCode, boolean tickOption) {
        finish();
    }
}
