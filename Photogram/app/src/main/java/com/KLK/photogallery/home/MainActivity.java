package com.KLK.photogallery.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.UniversalImageLoader;
import com.KLK.photogallery.home.messenger.MessengerMainActivity;
import com.KLK.photogallery.home.messenger.TabsControllerAdapter;
import com.KLK.photogallery.profile.AccountSettingActivity;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;
    private ImageView sendMessage;
    private Context context = MainActivity.this;
    private ViewPager viewPager;
    private  ViewPagerAdapter adapter;

    // Source: Google Image
    private String[] imageUrls = new String[]{
            "https://i.pinimg.com/originals/ff/f6/fa/fff6fa7b1d480f2a298630cb789a4fe1.jpg",
            "https://previews.123rf.com/images/nguyenthanhtrong/nguyenthanhtrong2001/nguyenthanhtrong200100067/141533444-happy-new-year-2021-with-firework-background-firework-display-colorful-for-holidays-.jpg",
            "https://previews.123rf.com/images/nguyenthanhtrong/nguyenthanhtrong2001/nguyenthanhtrong200100089/136892288-happy-new-year-2021-with-firework-background-firework-display-colorful-for-holidays-.jpg",
            "https://previews.123rf.com/images/nguyenthanhtrong/nguyenthanhtrong2001/nguyenthanhtrong200100072/141533446-happy-new-year-2021-with-firework-background-firework-display-colorful-for-holidays-.jpg",
            "https://previews.123rf.com/images/nguyenthanhtrong/nguyenthanhtrong2001/nguyenthanhtrong200100110/136897247-happy-new-year-2021-with-firework-background-firework-display-colorful-for-holidays-.jpg",
            "https://static.toiimg.com/thumb/msid-73028722,imgsize-1432792,width-800,height-600,resizemode-75/73028722.jpg",
            "https://img.freepik.com/free-vector/happy-new-year-2021-numbers-green-fir-branches-holiday-ornaments-white-background-greeting-card-promotion-poster-template_145666-1191.jpg?size=626&ext=jpg",
            "https://thumbs.dreamstime.com/b/happy-new-year-golden-d-numbers-ribbons-confetti-white-background-happy-new-year-167245897.jpg",
            "https://happynewyear2021.net/wp-content/uploads/2019/09/happy-new-year-2021-new-820x554.png",
            "https://image.winudf.com/v2/image1/Y29tLmhhcHB5bmV3eWVhcjIwMjAuaGFwcHluZXd5ZWFyLm5ld3llYXIyMDIwLkdpZjIwMjBfc2NyZWVuXzBfMTU5ODcxOTk3OF8wNzc/screen-0.jpg?h=355&fakeurl=1&type=.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Start onCreate()!");

        sendMessage = findViewById(R.id.send_message);
        viewPager = findViewById(R.id.viewpager_container);

        adapter = new ViewPagerAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        initToolbar();

        configBottomNavigationView();
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
//    private void setupViewPager(){
//        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new CameraFragment()); //index 0
//        adapter.addFragment(new HomeFragment()); //index 1
//        adapter.addFragment(new MessagesFragment()); //index 2
//        mViewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);
//
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram_black);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
//    }

    private void configBottomNavigationView(){
        Log.d(TAG, "Config Bottom Navigation View!");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewUtils.navigating(context,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void initToolbar() {
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + context.getString(R.string.messenger_activity));

                Intent intent = new Intent(context, MessengerMainActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.home_activity));
                startActivity(intent);
            }
        });
    }

}