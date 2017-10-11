package com.kubatatami.poker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kubatatami.poker.R;
import com.kubatatami.poker.ui.HardwareAccelerationListener;
import com.kubatatami.poker.utils.SoundManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Kuba on 28/09/14.
 */
@EBean
public class BaseActivity extends Activity {

    @Bean
    protected SoundManager soundManager;
    protected AnimatorSet backgroundAnimatorSet;
    protected AnimatorSet buttonAnimatorSet;
    protected Set<View> animationButtonsSet = new HashSet<View>();
    protected Handler handler = new Handler();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.startMusic();
        System.gc();
        for (View view : animationButtonsSet) {
            view.setTag(null);
        }
    }

    protected void animateBackground(int drawable, View backgroundView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap background = BitmapFactory.decodeResource(backgroundView.getResources(),
                drawable, options);
        if (background.getConfig() != Bitmap.Config.RGB_565) {
            Bitmap newBitmap = background.copy(Bitmap.Config.RGB_565, background.isMutable());
            background.recycle();
            background = newBitmap;
            System.gc();
        }
        backgroundView.setBackgroundDrawable(new BitmapDrawable(backgroundView.getResources(), background));
        backgroundAnimatorSet = new AnimatorSet();
        ObjectAnimator rotateStart = ObjectAnimator.ofFloat(backgroundView, "rotation", 0, 4);
        ObjectAnimator scaleXStart = ObjectAnimator.ofFloat(backgroundView, "scaleX", 1.0f, 1.2f);
        ObjectAnimator scaleYStart = ObjectAnimator.ofFloat(backgroundView, "scaleY", 1.0f, 1.2f);
        rotateStart.setDuration(3000).setRepeatCount(ValueAnimator.INFINITE);
        rotateStart.setRepeatMode(ValueAnimator.REVERSE);
        scaleXStart.setDuration(3000).setRepeatCount(ValueAnimator.INFINITE);
        scaleXStart.setRepeatMode(ValueAnimator.REVERSE);
        scaleYStart.setDuration(3000).setRepeatCount(ValueAnimator.INFINITE);
        scaleYStart.setRepeatMode(ValueAnimator.REVERSE);
        backgroundAnimatorSet.setInterpolator(new LinearInterpolator());
        backgroundAnimatorSet.playTogether(
                rotateStart,
                scaleXStart,
                scaleYStart);
        backgroundAnimatorSet.addListener(new HardwareAccelerationListener(backgroundView));
        backgroundAnimatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundAnimatorSet != null) {
            backgroundAnimatorSet.cancel();
        }
    }

    public void setAnimationButtonEnabled(View button, boolean enabled) {
        if (enabled) {
            button.setTag(null);
        } else {
            button.setTag("");
        }
    }

    public void setAnimationButton(View button, final View.OnClickListener onClickListener) {
        setAnimationButton(button, button, false, R.raw.menu_touch_any_button, onClickListener);
    }

    public void setAnimationButton(View button, final boolean rotation, final View.OnClickListener onClickListener) {
        setAnimationButton(button, button, rotation, R.raw.menu_touch_any_button, onClickListener);
    }

    public void setAnimationButton(View button, final boolean rotation, int sound, final View.OnClickListener onClickListener) {
        setAnimationButton(button, button, rotation, sound, onClickListener);
    }

    public void setAnimationButton(View button, final View image, final boolean rotation, final View.OnClickListener onClickListener) {
        setAnimationButton(button, image, rotation, R.raw.menu_touch_any_button, onClickListener);
    }

    public void setAnimationButton(final View button, final View image, final boolean rotation, final int sound, final View.OnClickListener onClickListener) {
        animationButtonsSet.add(button);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (button.getTag() != null) {
                    return false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    startAnimation(image, rotation);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    backAnimation(image, rotation, null);
                }
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (button.getTag() != null) {
                    return;
                }
                button.setTag(true);
                if (sound > 0) {
                    soundManager.playSound(sound);
                }
                backAnimation(image, rotation, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onClickListener.onClick(v);
                    }
                });
            }
        });
    }

    protected void startAnimation(View view, boolean rotation) {
        if (buttonAnimatorSet != null) {
            buttonAnimatorSet.cancel();
        }
        buttonAnimatorSet = new AnimatorSet();
        List<Animator> objectAnimators = new ArrayList<Animator>();
        objectAnimators.add(ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.8f));
        objectAnimators.add(ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.8f));
        if (rotation) {
            objectAnimators.add(ObjectAnimator.ofFloat(view, "rotation", 0, 360));
        }
        buttonAnimatorSet.playTogether(objectAnimators);
        buttonAnimatorSet.addListener(new HardwareAccelerationListener(view));
        buttonAnimatorSet.setDuration(100);
        buttonAnimatorSet.start();
    }

    protected void backAnimation(View view, boolean rotation, Animator.AnimatorListener listener) {

        buttonAnimatorSet = new AnimatorSet();
        List<Animator> objectAnimators = new ArrayList<Animator>();
        objectAnimators.add(ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1.0f));
        objectAnimators.add(ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.0f));
        if (rotation) {
            objectAnimators.add(ObjectAnimator.ofFloat(view, "rotation", 360, 0));
        }
        buttonAnimatorSet.playTogether(objectAnimators);
        buttonAnimatorSet.addListener(new HardwareAccelerationListener(view, listener));
        //buttonAnimatorSet.setStartDelay(100);
        buttonAnimatorSet.setDuration(100);
        buttonAnimatorSet.start();
    }
}
