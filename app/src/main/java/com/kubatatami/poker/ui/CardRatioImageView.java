package com.kubatatami.poker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Kuba on 07/10/14.
 */
public class CardRatioImageView extends ImageView {

    public CardRatioImageView(Context context) {
        super(context);
    }

    public CardRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        setLayerType(LAYER_TYPE_HARDWARE,null);
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int calculatedHeight = originalWidth * 830 / 520 ;

        int finalWidth, finalHeight;

        if (calculatedHeight > originalHeight)
        {
            finalWidth = originalHeight * 520 / 830;
            finalHeight = originalHeight;
        }
        else
        {
            finalWidth = originalWidth;
            finalHeight = calculatedHeight;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }

}
