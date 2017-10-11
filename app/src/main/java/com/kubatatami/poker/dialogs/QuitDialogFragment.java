package com.kubatatami.poker.dialogs;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.ui.HardwareAccelerationListener;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 25/11/14.
 */
@EFragment(R.layout.fragment_dialog_quit)
public class QuitDialogFragment extends DialogFragment {

    @ViewById(R.id.dialog_quit)
    protected View quitView;
    @ViewById(R.id.dialog_quit_no)
    protected TextView quitNoView;
    @ViewById(R.id.dialog_quit_yes)
    protected TextView quitYesView;
    @ViewById(R.id.dialog_quit_text)
    protected TextView quitTextView;

    @ViewById(R.id.dialog_dont_show_it_again)
    protected View tickView;

    @ViewById(R.id.dialog_tick_image)
    protected ImageView tickImage;

    boolean dontShowItAgain = false;

    @FragmentArg
    protected String message;


    @FragmentArg
    protected String yesText;


    @FragmentArg
    protected Integer requestCode = 0;
    @FragmentArg
    protected Boolean tick = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(message!=null){
            quitTextView.setText(message);
        }
        if(tick){
            tickView.setVisibility(View.VISIBLE);
        }else{
            tickView.setVisibility(View.GONE);
        }
        if(yesText != null){
            quitYesView.setText(yesText);
        }
        AnimatorSet dialogAnimatorSet = new AnimatorSet();
        List<Animator> objectAnimators = new ArrayList<Animator>();
        objectAnimators.add(ObjectAnimator.ofFloat(quitView, "scaleX", 0f, 1.0f));
        objectAnimators.add(ObjectAnimator.ofFloat(quitView, "scaleY", 0f, 1.0f));
        dialogAnimatorSet.setInterpolator(new LinearInterpolator());
        dialogAnimatorSet.playTogether(objectAnimators);
        dialogAnimatorSet.addListener(new HardwareAccelerationListener(quitView, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);


                final AnimatorSet dialogContentAnimatorSet = new AnimatorSet();
                dialogContentAnimatorSet.setInterpolator(new LinearInterpolator());

                AnimatorSet noAnimatorSet = new AnimatorSet();
                noAnimatorSet.setInterpolator(new LinearInterpolator());

                List<Animator> objectAnimators = new ArrayList<Animator>();
                objectAnimators.add(ObjectAnimator.ofFloat(quitNoView, "scaleX", 0.9f, 1.0f));
                objectAnimators.add(ObjectAnimator.ofFloat(quitNoView, "scaleY", 0.9f, 1.0f));
                noAnimatorSet.setStartDelay(0);
                noAnimatorSet.setDuration(150);
                noAnimatorSet.playTogether(objectAnimators);
                noAnimatorSet.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        quitNoView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        quitYesView.setVisibility(View.VISIBLE);
                    }
                });
                AnimatorSet quitAnimatorSet = new AnimatorSet();
                quitAnimatorSet.setInterpolator(new LinearInterpolator());

                quitAnimatorSet.setStartDelay(0);
                quitAnimatorSet.setDuration(150);
                objectAnimators = new ArrayList<Animator>();
                objectAnimators.add(ObjectAnimator.ofFloat(quitYesView, "scaleX", 0.9f, 1.0f));
                objectAnimators.add(ObjectAnimator.ofFloat(quitYesView, "scaleY", 0.9f, 1.0f));
                quitAnimatorSet.playTogether(objectAnimators);

                objectAnimators = new ArrayList<Animator>();

                objectAnimators.add(noAnimatorSet);
                objectAnimators.add(quitAnimatorSet);

                dialogContentAnimatorSet.playSequentially(objectAnimators);
                dialogContentAnimatorSet.setStartDelay(0);
                dialogContentAnimatorSet.setDuration(300);
                dialogContentAnimatorSet.start();


            }
        }));
        dialogAnimatorSet.setDuration(300);
        dialogAnimatorSet.start();
    }

    @Click(R.id.dialog_dont_show_it_again)
    protected void tickClick(){
        dontShowItAgain = !dontShowItAgain;
        tickImage.setImageLevel(dontShowItAgain ? 1 : 0);

    }

    @Click(R.id.dialog_quit_no)
    protected void noClick() {
        dismiss();
    }

    @Click(R.id.dialog_quit_yes)
    protected void quitClick() {
        dismiss();
        ((OnQuitListener)getActivity()).onQuit(requestCode, dontShowItAgain);
    }

    public interface OnQuitListener {
        public void onQuit(int requestCode, boolean tickOption);
    }
}
