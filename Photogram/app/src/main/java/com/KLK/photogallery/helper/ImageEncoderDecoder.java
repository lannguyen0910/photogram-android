package com.KLK.photogallery.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class ImageEncoderDecoder {
    public static Bitmap decodeBase64ToBitmap(String base64image){
        byte[] decodedString = Base64.decode(base64image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static Uri decodeBase64ToURI(String base64image) throws UnsupportedEncodingException {
        byte[] decodedString = Base64.decode(base64image, Base64.DEFAULT);
        String s = new String(decodedString, "UTF-8");
        Uri uri = Uri.parse(s);
        return uri;
    }

    public static String encodeBitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }
}