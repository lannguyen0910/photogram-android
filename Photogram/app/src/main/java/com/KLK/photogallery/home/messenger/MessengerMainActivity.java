package com.KLK.photogallery.home.messenger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.ImageEncoderDecoder;
import com.KLK.photogallery.helper.SharedPref;
import com.google.android.material.tabs.TabLayout;

public class MessengerMainActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "MessengerMainActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsControllerAdapter tabsControllerAdapter;
    private SharedPref sharedPref;
    private ImageView mProfilePhoto;
    private TextView mUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        sharedPref = new SharedPref(getApplicationContext());
        mProfilePhoto = findViewById(R.id.profile_image);
        mUsername = findViewById(R.id.username);

        viewPager = findViewById(R.id.view_pager);
        tabsControllerAdapter = new TabsControllerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsControllerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        setProfileImage();
        setUserInfo();
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: set profile avatar!");
        String avatar  = sharedPref.getString("avatar");
        Bitmap avatar_bm = ImageEncoderDecoder.decodeBase64ToBitmap(avatar);
        mProfilePhoto.setImageBitmap(avatar_bm);
    }

    private void setUserInfo(){
        Log.d(TAG, "setUserInfo: set user info!");
        String username = sharedPref.getString("username");

        mUsername.setText(username);
    }
}

