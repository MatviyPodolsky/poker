package com.kubatatami.poker.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import com.kubatatami.poker.data.Settings_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Random;

/**
 * Created by Kuba on 14/10/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class SoundManager {

    @Pref
    protected Settings_ settings;

    @RootContext
    protected Context context;
    protected MediaPlayer mediaPlayer;
    protected Handler handler;
    protected Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }
    };


    @AfterInject
    protected void switchContext() {
        context = context.getApplicationContext();
        handler = new Handler();
    }

    public void playMusic(int music) {
        stopAndReleaseMusic();
        mediaPlayer = MediaPlayer.create(context, music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopAndReleaseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


    public void pauseMusic() {
        handler.postDelayed(closeRunnable, 100);
    }

    public void startMusic() {
        handler.removeCallbacks(closeRunnable);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void playSound(int sound) {
        if (!settings.sound().get()) {
            return;
        }
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
        mediaPlayer.start();
    }

    public void playRandomSound(int... sounds) {
        if (!settings.sound().get()) {
            return;
        }
        Random random = new Random();
        MediaPlayer mediaPlayer = MediaPlayer.create(context, sounds[random.nextInt(sounds.length)]);
        mediaPlayer.start();
    }

}
