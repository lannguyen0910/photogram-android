package com.KLK.photogallery.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.KLK.photogallery.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SharedPref {
    SharedPreferences pref;
    private static final String TAG = "Shared Reference";

    public SharedPref(Context context){
        String prefName = context.getString(R.string.preference_file_key);
        this.pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public String getString(String name){
        String result = this.pref.getString(name, "None");
        return result;
    }

    public void setString(String name, String value){
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public void storeJSONObject(JSONObject jsonObject){
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = jsonObject.get(key);
                setString(key, value.toString());
            } catch (JSONException e) {
                // Something went wrong!
                e.printStackTrace();
            }
        }
        Log.e(TAG, "Shared reference saved");
    }
}
