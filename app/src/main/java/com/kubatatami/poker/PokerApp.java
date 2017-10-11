package com.kubatatami.poker;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.SoundManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Kuba on 16/09/14.
 */
@EApplication
public class PokerApp extends Application {

    @Pref
    protected Settings_ settings;
    protected DisplayImageOptions profileThumbDisplayOptions;
    protected DisplayImageOptions assetsDisplayOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("fonts/Roboto-Light.ttf");
    }


    @AfterInject
    public void createImageLoader() {
        profileThumbDisplayOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).build();

        assetsDisplayOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).build();

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getApplicationContext());

        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().handleSlowNetwork(true);
    }

    public int getDeck(){
        return  settings.deckTmp().get();
    }

    public String getDimensionFolder(){
        return  getString(R.string.dimension);
    }


}
