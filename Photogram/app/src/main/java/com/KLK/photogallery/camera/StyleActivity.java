package com.KLK.photogallery.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.helper.SlideViewPagerAdapter;

/** User can choose a style they want **/
public class StyleActivity extends AppCompatActivity {
    public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;
    ServerRequest server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_slide);

        viewPager = findViewById(R.id.viewpager);

        server = new ServerRequest(this);


        adapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

//        if (isOpenAlready())
//        {
//            Intent intent = new Intent(StyleActivity.this, NextActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } else {
//            SharedPreferences.Editor editor = getSharedPreferences("slide", MODE_PRIVATE).edit();
//            editor.putBoolean("slide",true);
//            editor.commit();
//        }

    }

    private boolean isOpenAlready() {
        SharedPreferences sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("slide",false);
        return result;

    }

}