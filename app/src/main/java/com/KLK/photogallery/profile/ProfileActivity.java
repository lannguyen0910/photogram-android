package com.KLK.photogallery.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.GridImageAdapter;
import com.KLK.photogallery.helper.ServerRequest;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import java.util.ArrayList;

/** Profile Information **/

public class ProfileActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context context = ProfileActivity.this;
//
//    private ServerRequest server;
//    private static final int NUM_GRID_COLUMNS = 3;
//    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "Start onCreate()!");
        configBottomNavigationView();

        //initToolBar();

        // Send request to server when user click on profile tab
//        server = new ServerRequest(this);
//        //sendGalleryRequest();
//
//        //gridView = (GridView) findViewById(R.id.gridView);
//        Button test_btn = (Button) findViewById(R.id.test_btn);
//        test_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //setupGridView();
//            }
//        });
    }
/*
    // little toolbar on the right
    private void initToolBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView)findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigate to account setting!");
                Intent intent = new Intent(context, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    Send image request to server, run this on another thread
     */
    /*
    private void sendGalleryRequest(){
        String url = getResources().getString(R.string.gallery_url);
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
**/
    /*
    Setup images received from database
     */
    /*
    private void setupGridView() {
        final ArrayList<String> imgBase64Strings = server.getImageBase64Strings();
        Log.e(TAG, "number of images " + String.valueOf(imgBase64Strings.size()));

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridView
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview, imgBase64Strings);
        gridView.setAdapter(adapter);
    }
    */
    private void configBottomNavigationView(){
        Log.d(TAG, "Config Bottom Navigation View!");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewUtils.navigating(context,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
