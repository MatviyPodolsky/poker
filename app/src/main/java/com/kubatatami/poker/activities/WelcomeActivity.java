package com.kubatatami.poker.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.AvatarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {
    @Pref
    protected Settings_ settings;

    @ViewById(R.id.welcome_avatar)
    protected ImageView avatarView;
    @ViewById(R.id.welcome_name)
    protected TextView nameView;
    @ViewById(R.id.welcome_title)
    protected TextView title;
    @ViewById(R.id.welcome_fb)
    protected LoginButton fbLoginView;
    @ViewById(R.id.welcome_done)
    protected TextView doneButton;

    private String randAsset;

    private String image = "";
    @Extra
    protected Class<? extends Activity> activityClass;

    @Extra
    protected Boolean edit = false;

    private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = settings.image().get();
        uiHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (state.isOpened()) {
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            nameView.setText(user.getName());
                            String photoUrl = "https://graph.facebook.com/" + user.getId() + "/picture?type=normal";
                            getBitmapFromURL(photoUrl);
                        }
                    }).executeAsync();
                }
            }
        });
        uiHelper.onCreate(savedInstanceState);
    }


    @Background
    protected void getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            updateAvatarAfterFacebook(myBitmap);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @UiThread
    protected void updateAvatarAfterFacebook(Bitmap bitmap) {
        String image = "";
        try {
            image = AvatarUtils.createThumbnail(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        settings.image().put(image);
        AvatarUtils.showThumb(avatarView, settings.image().get());
    }


    @AfterViews
    protected void start() {
        animateBackground(edit ? R.drawable.settings_bgr : R.drawable.join_game_bgr, findViewById(R.id.main_background));
        if (edit) {
            title.setText(getString(R.string.my_profile));
            nameView.setText(settings.name().get());
        } else {
            Random rand = new Random();
            randAsset = "avatar_" + (rand.nextInt(getNumberOfAvatars()) + 1) + ".png";
            ImageLoader.getInstance().displayImage("assets://avatar_assets/" + randAsset, avatarView);
        }
        fbLoginView.setReadPermissions("email");

    }

    @TextChange(R.id.welcome_name)
    protected void nameChanged() {
        if (nameView.getText().length() > 0) {
            doneButton.setEnabled(true);
        } else {
            doneButton.setEnabled(false);
        }
    }

    private int getNumberOfAvatars() {
        try {
            //TODO background
            return getAssets().list("avatar_assets").length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        image = settings.image().get();
        if (!image.equals("")) {
            AvatarUtils.showThumb(avatarView, settings.image().get());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Click(R.id.welcome_done)
    protected void doneClick() {
        String name = nameView.getText().toString();
        if (name.equals("")) {

        } else {
            settings.name().put(name);
            finish();
            if (!edit) {
                startActivity(new Intent(this, activityClass));
            }
        }
    }


    @Click(R.id.welcome_avatar)
    protected void avatarClick() {
        ChooseAvatarActivity_.intent(this).start();
    }


}
