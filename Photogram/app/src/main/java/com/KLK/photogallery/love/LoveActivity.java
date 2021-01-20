package com.KLK.photogallery.love;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.GridImageAdapter;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.model.Post;
import com.KLK.photogallery.profile.ProfileFragment;
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
    GridView gridView;
    ProgressBar progressBar;
    private int NUM_GRID_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love);
        Log.d(TAG, "Start onCreate()!");
        configBottomNavigationView();

        server = new ServerRequest(LoveActivity.this);
        sendFavoriteRequest();

        gridView = (GridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.favProgressBar);

        fetchImagefromServer(0);
    }

    /** -----------------------------------------------------------------------------------
     * Interface of ProfileFragment and ProfileActivity
     * Each time it is called, it will take the parameter in the function and pass it on where to implement this interface.
     * activityNumber is used for calling this interface in different activity
     **/
    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Post post, int activityNumber);
    }
    ProfileFragment.OnGridImageSelectedListener mOnGridImageSelectedListener = null;


    /** ----------------------------------------------------------------------------------- **/


    private void setupGridView(){
        final ArrayList<String> imgBase64Strings = server.getImageBase64Strings();
        final ArrayList<String> imgBaseNameStrings = server.getImageNameStrings();

        final ArrayList<Post> posts = new ArrayList<>();
        for (int i = 0; i < imgBase64Strings.size(); i++) {
            Post post = new Post(imgBase64Strings.get(i),imgBaseNameStrings.get(i), true,true);
            posts.add(post);
        }

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridView
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview, imgBase64Strings);
        gridView.setAdapter(adapter);
    }

    private void fetchImagefromServer(int times){
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    setupGridView();
                }
                catch (NullPointerException e){
                    //Toast.makeText(getActivity(),"Not able to connect server. Reloading...", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    fetchImagefromServer(times + 1);
                }
                progressBar.setVisibility(View.GONE);
            }
        }, 2000);
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
