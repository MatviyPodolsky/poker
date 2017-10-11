package com.kubatatami.poker.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.AvatarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_statistics)
public class StatisticsActivity extends BaseActivity {

    @Pref
    Settings_ settings;

    @ViewById(R.id.statistics_avatar)
    protected ImageView avatar;
    @ViewById(R.id.statistics_name)
    protected TextView name;


    @AfterViews
    protected void start() {
        animateBackground(R.drawable.settings_bgr, findViewById(R.id.main_background));
        name.setText(settings.name().get());
        AvatarUtils.showThumb(avatar, settings.image().get());

    }

}
