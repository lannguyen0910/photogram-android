package com.KLK.photogallery.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.model.Post;
import com.KLK.photogallery.profile.ProfileActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewPostFragment extends Fragment {
    // For debugging
    private static final String TAG = "ViewPostFragment";

    //vars
    private Post mPhoto;
    private int mActivityNumber = 0; // default at MainActivity

    // widgets
    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mUsername;
    private ImageView mBackArrow, mEllipses, mProfileImage;
    private ImageView mHeartRed, mHeartWhite, mDownload, mDelete;

    private ServerRequest server;
    private SharedPref sharedPref;
    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreteView: set grid image ");
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        mPostImage = (SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mBackLabel = (TextView) view.findViewById(R.id.tvBackLabel);
        mUsername = (TextView) view.findViewById(R.id.username);
        mEllipses = (ImageView) view.findViewById(R.id.ivEllipses);
        mHeartRed = (ImageView) view.findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView) view.findViewById(R.id.image_heart);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_photo);
        mDownload = (ImageView) view.findViewById(R.id.download);
        mDelete = (ImageView) view.findViewById(R.id.delete);
        sharedPref = new SharedPref(getActivity().getApplicationContext());
        server = new ServerRequest((ProfileActivity)getActivity());

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to Profile Activity!");
                //((ProfileActivity) getActivity()).initProfileFragment();
                Fragment viewPostFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.profile_container);
                ((ProfileActivity) getActivity()).destroyViewPostFragment(viewPostFragment);
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: delete image!");
                String curImageID = getCurrentImageID();
                deleteImageByID(curImageID);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() { 
                        if (verifyDelete()) {mBackArrow.performClick(); }}
                }, 1500);

            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadImageToLocal();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        setUsername();
        try{
            Log.e(TAG, "onCreteView: set grid image ");
            mPhoto = getPhotoFromBundle();
            assert mPhoto != null;
            String photoBase64 = mPhoto.getImageBase64();
            String photoID = mPhoto.getPhoto_id();
            Log.e(TAG,"Photo ID: "+ photoID);
            Bitmap avatar_bm = ImageEncoderDecoder.decodeBase64ToBitmap(photoBase64);
            mPostImage.setImageBitmap(avatar_bm);
            mPostImage.setTag(photoID);
            //UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();

        } catch(NullPointerException e){
            Log.e(TAG, "onCreteView: NullPointerException (the received bundle is null)! ");
        }

        configBottomNavigationView();
        return view;
    }

    private boolean verifyDelete(){
        int response = server.getResponse();
        String message = server.getMessage();
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT);
        if (response == 1) {
            return true;
        }
        else {
            return false;
        }
    }


    private void setUsername(){
        Log.d(TAG, "setUserName: set user name!");
        String username = sharedPref.getString("username");
        mUsername.setText(username);
    }

    /** retrieve the post from the incoming bundle from ProfileActivity interface **/
    private Post getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

    private String getDateString(){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        String timestamp = simpleDateFormat.format(now);
        return timestamp;
    }

    private void downloadImageToLocal() throws IOException {
        mPhoto = getPhotoFromBundle();
        assert mPhoto != null;
        String photoBase64 = mPhoto.getImageBase64();
        String photoID = mPhoto.getPhoto_id();

        Bitmap photo_bm = ImageEncoderDecoder.decodeBase64ToBitmap(photoBase64);
        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), photo_bm, "upload image" , "no description");

        Log.e(TAG,"Downloaded image, ID: " + photoID);
    }

    /** retrieve the activity number from the incoming bundle from ProfileActivity interface **/
    private int getActivityNumFromBundle(){
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    private String getCurrentImageID(){
        String imageID = (String) mPostImage.getTag();
        return imageID;
    }

    private void deleteImageByID(String imageID){
        String url = getResources().getString(R.string.delete_url);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("image", imageID);
                server.sendRequestToServer(url, params); }});
        try {
            thread.start();
            thread.join();
            Log.e(TAG,"Thread joined");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void configBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationView);
        BottomNavigationViewUtils.navigating(getActivity(), getActivity() ,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }

}
