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
import androidx.fragment.app.FragmentTransaction;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.UniversalImageLoader;
import com.KLK.photogallery.helper.ViewPostFragment;
import com.KLK.photogallery.model.Post;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/** Profile Information **/
public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener{
    // For debugging
    private static final String TAG = "ProfileActivity";

    // private static final int ACTIVITY_NUM = 4;
    private Context context = ProfileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "Start onCreate()!");

        initProfileFragment();
    }

    // navigate to profileFragment
    public void initProfileFragment(){
        Log.d(TAG, "inflating " + getString(R.string.profile_fragment));

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        // swap profile_container with profileFragment
        fragmentTransaction.replace(R.id.profile_container, profileFragment);
        // fragment unlike activity, it don't keep track of their stack, need to add it manually
        fragmentTransaction.addToBackStack(getString(R.string.profile_fragment));
        fragmentTransaction.commit();
    }

    /** implement interface in ProfileActivity (override from Profile Fragment) **/
    @Override
    public void onGridImageSelected(Post post, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image GridView: " + post.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle(); // wrap data and easy to transfer
        // attach in Bundle
        args.putParcelable(getString(R.string.photo), post);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);
        // head to new fragment
        FragmentTransaction transaction  = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
