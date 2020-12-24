package com.KLK.photogallery.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.GridImageAdapter;
import com.KLK.photogallery.helper.ImageEncoderDecoder;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.helper.SharedPref;
import com.KLK.photogallery.model.Post;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/** Khoi modifies this **/
public class ProfileFragment extends Fragment {
    // For debugging
    private static final String TAG = "ProfileFragment";

    private static final int ACTIVITY_NUM = 4;
    private final int NUM_GRID_COLUMNS = 3;
    private final int NUM_REQUEST_RETRIES = 5;

    //widgets
    private TextView mPosts, mFullName, mUsername, mPhoneNumber, mEmail;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private ServerRequest server;
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        Log.d(TAG, "onCreateView: Start!");

        mFullName = (TextView) view.findViewById(R.id.full_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mPhoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        mEmail = (TextView) view.findViewById(R.id.website);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        server = new ServerRequest((ProfileActivity)mContext);
        sharedPref = new SharedPref(getActivity().getApplicationContext());

        configBottomNavigationView();
        initToolBar();

        sendGalleryRequest();
        setActivityWidgets();

        TextView editProfile = (TextView) view.findViewById(R.id.textEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        setUserInfo();
        fetchImagefromServer(0);

        return view;
    }

    // little toolbar on the right
    private void initToolBar(){
        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigate to account setting!");
                Intent intent = new Intent(mContext, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }




    /** -----------------------------------------------------------------------------------
     * Interface of ProfileFragment and ProfileActivity
     * Each time it is called, it will take the parameter in the function and pass it on where to implement this interface.
     * activityNumber is used for calling this interface in different activity
     **/
    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Post post, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;


    @Override
    public void onAttach(@NonNull Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener)getActivity();
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException!");
        }
        super.onAttach(context);
    }
    /** ----------------------------------------------------------------------------------- **/


    private void fetchImagefromServer(int times){
        mProgressBar.setVisibility(View.VISIBLE);
        if (times == NUM_REQUEST_RETRIES){
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    setProfileImage();
                    setupGridView();
                }
                catch (NullPointerException e){
                    //Toast.makeText(getActivity(),"Not able to connect server. Reloading...", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    fetchImagefromServer(times + 1);
                }
                mProgressBar.setVisibility(View.GONE);
            }
        }, 2000);
    }


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

    private void setupGridView() {
        final ArrayList<String> imgBase64Strings = server.getImageBase64Strings();
        final ArrayList<String> imgBaseNameStrings = server.getImageNameStrings();
        Log.e(TAG, "number of images " + String.valueOf(imgBase64Strings.size()));

        final ArrayList<Post> posts = new ArrayList<>();
        for (int i =0;i<imgBase64Strings.size();i++) {
            Post post = new Post(imgBase64Strings.get(i),imgBaseNameStrings.get(i), true,true);
            posts.add(post);
        }

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridView
        GridImageAdapter adapter = new GridImageAdapter(getContext(), R.layout.layout_grid_imageview, imgBase64Strings);
        gridView.setAdapter(adapter);

        // attach onClickListener to GridViewItem
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // use interface above to navigate to new fragment (ViewPostFragment)
                mOnGridImageSelectedListener.onGridImageSelected(posts.get(position), ACTIVITY_NUM);
            }
        });
    }
    /** Modifies or delete this part when add database
     * -----------------------------------------------------------------------------------
     **/
    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: set profile avatar!");
        String avatar  = server.getAvatarBase64String();
        Bitmap avatar_bm = ImageEncoderDecoder.decodeBase64ToBitmap(avatar);
        mProfilePhoto.setImageBitmap(avatar_bm);
    }

    private void setUserInfo(){
        Log.d(TAG, "setUserInfo: set user info!");
        String username = sharedPref.getString("username");
        String phone_number = sharedPref.getString("phone_number");
        String email = sharedPref.getString("email");
        String fullname = sharedPref.getString("fullname");

        mUsername.setText(username);
        mPhoneNumber.setText(phone_number);
        mEmail.setText(email);
        mFullName.setText(fullname);
    }

    private void setActivityWidgets(){
        mProgressBar.setVisibility(View.GONE);
    }
    /** ----------------------------------------------------------------------------------- **/


    /** fix this method **/
//    private void setupGridView2(String selectedDirectory){
//        final ArrayList<String> imgBase64Strings = server.getImageBase64Strings();
//        Log.e(TAG, "number of images " + String.valueOf(imgBase64Strings.size()));
//        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
//        final ArrayList<Post> posts = new ArrayList<>();
//
//
//        /** --------------------------------------------------------
//         * ------ see Post.java in model --------
//         * for every photo in directory (different from GalleryFragment, not a specific directory but every images):
//         *      create new Post object (Post post = new Post() )
//         *      add information in Post
//         *      post.add(post)
//         *
//
//
//
//         --------------------------------------------------------**/
//
//        //set the grid column width
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(imageWidth);
//
//        ArrayList<String> imgUrls = new ArrayList<String>();
//        for(int i = 0; i < posts.size(); i++){
//            imgUrls.add(posts.get(i).getImage_path());
//        }
//
//        //use the grid adapter to adapt the images to gridView
//        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview,
//                "", imgUrls);
//        gridView.setAdapter(adapter);
//
//        // attach onClickListener to GridViewItem
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // use interface above to navigate to new fragment (ViewPostFragment)
//                mOnGridImageSelectedListener.onGridImageSelected(posts.get(position), ACTIVITY_NUM);
//            }
//        });
//    }
    /** ----------------------------------------------------------------------------------- **/


    private void configBottomNavigationView(){
        Log.d(TAG, "Config Bottom Navigation View!");
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationView);
        BottomNavigationViewUtils.navigating(mContext, getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
