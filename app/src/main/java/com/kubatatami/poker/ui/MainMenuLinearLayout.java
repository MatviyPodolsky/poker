package com.kubatatami.poker.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Kuba on 25/11/14.
 */
public class MainMenuLinearLayout extends LinearLayout {

    Paint paint;
    RectF ovalRect;
    RectF rectRect;
    int padding;
    public MainMenuLinearLayout(Context context) {
        super(context);
        init();
    }

    public MainMenuLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainMenuLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MainMenuLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    protected void init(){
        setWillNotDraw(false);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(100,0,0,0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width=getWidth();
        int height = getHeight();
        padding= (int) (0.07f*(float)getWidth());
        ovalRect=new RectF(padding,padding,width-padding,(width-padding));
        int ovalHeight=(int)ovalRect.height();
        rectRect=new RectF(padding,padding + ovalHeight/2,width-padding,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipRect(0, 0, getWidth(), rectRect.top);
        canvas.drawArc(ovalRect, 0, -180, true, paint);
        canvas.restore();
        canvas.drawRect(rectRect,paint);
    }
}
