package com.kubatatami.poker.activities;

import android.app.Application;
import android.os.Bundle;

import com.kubatatami.poker.R;
import com.squareup.leakcanary.LeakCanary;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LeakCanary.install(super.getApplication());
    }

    @AfterViews
    protected void afterViews() {
        closeSplash();
    }

    @UiThread(delay = 3000)
    protected void closeSplash() {
        finish();
        MenuActivity_.intent(this).start();
    }

}
