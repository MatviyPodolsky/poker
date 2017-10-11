package com.kubatatami.poker.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by jstasinski on 16/12/14.
 */
public class ClipLinearLayout extends LinearLayout {
    private float progress;

    public ClipLinearLayout(Context context) {
        super(context);
    }

    public ClipLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public float getProgress(){
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getBackground().setBounds(0, 0, getMeasuredWidth(), (int) (getMeasuredHeight() * progress));
        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() * progress);
        super.onDraw(canvas);
    }


}
