package com.KLK.photogallery;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

// https://github.com/ittianyu/BottomNavigationViewEx
public class BottomNavigationViewUtils {
    private static final String TAG = "BottomNavigationView";

    public static void configBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "Config Bottom Navigation View!");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
    }

    //Navigate between activites
    public static void navigating(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId()){
                    case R.id.ic_home:  // Activity 0
                        Intent intent1 = new Intent(context,MainActivity.class);
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_search:    // Activity 1
                        Intent intent2 = new Intent(context,SearchActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_camera:    // Activity 2
                        Intent intent3 = new Intent(context,CameraActivity.class);
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_heart:     // Activity 3
                        Intent intent4 = new Intent(context,LoveActivity.class);
                        context.startActivity(intent4);
                        break;
                    case R.id.ic_profile:   // Activity 4
                        Intent intent5 = new Intent(context,ProfileActivity.class);
                        context.startActivity(intent5);
                        break;
                }
                return false;
            }

        });
    }
}
