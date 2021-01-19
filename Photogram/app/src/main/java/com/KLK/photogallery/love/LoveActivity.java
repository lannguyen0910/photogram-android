package com.KLK.photogallery.love;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.ServerRequest;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Contains favorite images **/
public class LoveActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "LoveActivity";

    private static final int ACTIVITY_NUM = 3;
    private Context context = LoveActivity.this;
    ServerRequest server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love);
        Log.d(TAG, "Start onCreate()!");
        configBottomNavigationView();

        server = new ServerRequest(LoveActivity.this);
        sendFavoriteRequest();
    }

    private void setImage(){
        final ArrayList<String> imgBase64Strings = server.getImageBase64Strings();
        final ArrayList<String> imgBaseNameStrings = server.getImageNameStrings();
    }

    private void sendFavoriteRequest(){
        String url = getResources().getString(R.string.fav_url);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {server.sendRequestToServer(url); }});
        try {
            thread.start();
            thread.join();
            Log.e(TAG,"Thread joined");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void configBottomNavigationView(){
        Log.d(TAG, "Config Bottom Navigation View!");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewUtils.navigating(context,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
