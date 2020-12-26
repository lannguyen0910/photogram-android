package com.KLK.photogallery.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.KLK.photogallery.camera.CameraActivity;
import com.KLK.photogallery.R;
import com.KLK.photogallery.search.SearchActivity;
import com.KLK.photogallery.home.MainActivity;
import com.KLK.photogallery.love.LoveActivity;
import com.KLK.photogallery.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


/** https://github.com/ittianyu/BottomNavigationViewEx **/
public class BottomNavigationViewUtils {
    private static final String TAG = "BottomNavigationView";

    public static void configBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "Config Bottom Navigation View!");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
    }

    //Navigate between activities
    public static void navigating(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId()){
                    case R.id.ic_home:  // Activity 0
                        Intent intent1 = new Intent(context, MainActivity.class);
                        // prevent create new instance of the home activity every time press the home button more than once
                        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_search:    // Activity 1
                        Intent intent2 = new Intent(context, SearchActivity.class);
                        // prevent create new instance of the search activity every time press the search button more than once
                        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        context.startActivity(intent2);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_camera:    // Activity 2
                        Intent intent3 = new Intent(context, CameraActivity.class);
                        // prevent create new instance of the camera activity every time press the camera button more than once
                        intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_heart:     // Activity 3
                        Intent intent4 = new Intent(context, LoveActivity.class);
                        // prevent create new instance of the love activity every time press the love button more than once
                        intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        context.startActivity(intent4);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_profile:   // Activity 4
                        Intent intent5 = new Intent(context, ProfileActivity.class);
                        // prevent create new instance of the profile activity every time press the profile button more than once
                        intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        context.startActivity(intent5);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                }
                return false;
            }
        });
    }
}
