package com.KLK.photogallery;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AccountSettingActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "AccountSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        Log.d(TAG, "Start onCreate()!");
    }
}
