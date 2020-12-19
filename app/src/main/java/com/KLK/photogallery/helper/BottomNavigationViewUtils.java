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
    public static void navigating(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId()){
                    case R.id.ic_home:  // Activity 0
                        Activity activity1 = (Activity) context;
                        Intent intent1 = new Intent(activity1, MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity1.startActivity(intent1);
                        activity1.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_search:    // Activity 1
                        Activity activity2 = (Activity) context;
                        Intent intent2 = new Intent(activity2, SearchActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity2.startActivity(intent2);
                        activity2.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_camera:    // Activity 2
                        Activity activity3 = (Activity) context;
                        Intent intent3 = new Intent(activity3, CameraActivity.class);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity3.startActivity(intent3);
                        activity3.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_heart:     // Activity 3
                        Activity activity4 = (Activity) context;
                        Intent intent4 = new Intent(activity4, LoveActivity.class);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity4.startActivity(intent4);
                        activity4.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_profile:   // Activity 4
                        Activity activity5 = (Activity) context;
                        Intent intent5 = new Intent(activity5, ProfileActivity.class);
                        intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity5.startActivity(intent5);
                        activity5.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                }

                return false;
            }
        });
    }
}
