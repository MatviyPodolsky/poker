package com.kubatatami.poker.utils;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author skad
 * @date 08/03/16
 */
public class deviceUtils {

    public static String getDeviceId(Activity activity){
        String id =  Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Helper", "getDeviceId: "+id);
        return id;
    }

    public static String getMd5DeviceId(Activity activity){
        String md5id = md5(getDeviceId(activity));
        Log.d("Helper", "getMd5DeviceId: "+md5id);
        return md5id;
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
