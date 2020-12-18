package com.KLK.photogallery.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ImageViewCompat;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/** Profile Information **/
public class ProfileActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "ProfileActivity";

    private static final int ACTIVITY_NUM = 4;
    private Context context = ProfileActivity.this;

    private ProgressBar progressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "Start onCreate()!");

        configBottomNavigationView();

        initToolBar();

        setActivityWidgets();

        setProfileImage();
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: set profile avatar!");
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ProfileActivity.this));
        String imgURL = "upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Android_robot.svg/1200px-Android_robot.svg.png";
        UniversalImageLoader.setImage(imgURL, profilePhoto, progressBar , "https://");
    }

    private void setActivityWidgets(){
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
        progressBar = (ProgressBar)findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(View.GONE);
    }

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
