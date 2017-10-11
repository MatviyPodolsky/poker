package com.kubatatami.poker.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by Kuba on 25/11/14.
 */
public class HardwareAccelerationListener extends AnimatorListenerAdapter {

    View view;
    Animator.AnimatorListener listener;

    public HardwareAccelerationListener(View view) {
        this.view = view;
    }

    public HardwareAccelerationListener(View view, Animator.AnimatorListener listener) {
        this.view = view;
        this.listener = listener;
        view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if(listener!=null) {
            listener.onAnimationCancel(animation);
        }
        view.setLayerType(View.LAYER_TYPE_NONE,null);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(listener!=null) {
            listener.onAnimationEnd(animation);
        }
        view.setLayerType(View.LAYER_TYPE_NONE,null);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if(listener!=null) {
            listener.onAnimationStart(animation);
        }
    }
}
