package com.kubatatami.poker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.kubatatami.poker.R;

public class VerticleTextView extends TextView {


    public VerticleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();
        canvas.translate(0, getHeight());
        canvas.rotate(-90);


        canvas.translate(getCompoundPaddingLeft(), getCompoundPaddingTop());
        if (getLayout() != null) {
            getLayout().draw(canvas);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }
}