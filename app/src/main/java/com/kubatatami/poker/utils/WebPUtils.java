package com.kubatatami.poker.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.google.webp.libwebp;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Kuba on 24/11/14.
 */
public class WebPUtils {

    static {
        System.loadLibrary("webp");
    }

    public static Bitmap webpToBitmap(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return webpToBitmap(buffer.toByteArray());

    }
    public static Bitmap webpToBitmap(byte[] encoded) {

        int[] width = new int[] { 0 };
        int[] height = new int[] { 0 };
        byte[] decoded = libwebp.WebPDecodeARGB(encoded, encoded.length, width, height);

        int[] pixels = new int[decoded.length / 4];
        ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);

        return Bitmap.createBitmap(pixels, width[0], height[0], Bitmap.Config.ARGB_8888);

    }

    public static ContextWrapper getContext(Context context){

        return new WebpContextWrapper(context);
    }


    protected static class WebpContextWrapper extends ContextWrapper{


        public WebpContextWrapper(Context base) {
            super(base);
        }

        @Override
        public Resources getResources() {
            return new WebpResources(super.getResources());
        }
    }

    protected static class WebpResources extends Resources{

        Resources baseResources;

        public WebpResources(Resources baseResources) {
            super(baseResources.getAssets(), baseResources.getDisplayMetrics(), baseResources.getConfiguration());
            this.baseResources = baseResources;
        }


        @Override
        public Drawable getDrawable(int id) throws NotFoundException {
            final String name = getResourceEntryName(id);
            Log.i("getDrawable",name);
            Drawable drawable = baseResources.getDrawable(id);
            Log.i("getDrawable",drawable.getIntrinsicHeight()+"");
            return drawable;
        }

//        @Override
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
//            final String name = getResourceName(id);
//            //Log.i("getDrawable",name);
//            return baseResources.getDrawable(id, theme);
//        }
//
//        @Override
//        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//        public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
//            final String name = getResourceEntryName(id);
//            Log.i("getDrawable",name);
//            return baseResources.getDrawableForDensity(id, density);
//        }
//
//        @Override
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public Drawable getDrawableForDensity(int id, int density, Theme theme) {
//            final String name = getResourceName(id);
//            //Log.i("getDrawable",name);
//            return baseResources.getDrawableForDensity(id, density, theme);
//        }




    }

}
