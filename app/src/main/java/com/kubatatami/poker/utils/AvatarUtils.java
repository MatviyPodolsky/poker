package com.kubatatami.poker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import org.androidannotations.annotations.UiThread;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Kuba on 30/11/14.
 */
public class AvatarUtils {

    protected static final int THUMBNAIL_SIZE = 200;

    public static String createThumbnail(Bitmap image) throws IOException {
        int originalSize = (image.getHeight() > image.getWidth()) ? image.getHeight() : image.getWidth();
        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        Bitmap bitmap = Bitmap.createScaledBitmap(image,(int)(image.getWidth()*ratio),(int)(image.getHeight()*ratio),true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    public static String createThumbnail(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap loadThumbnail(String base64) throws IOException {
        byte[] array = Base64.decode(base64,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(array,0,array.length);
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public static void showThumb(ImageView view, String image){
        try {
            view.setImageBitmap(AvatarUtils.loadThumbnail(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
