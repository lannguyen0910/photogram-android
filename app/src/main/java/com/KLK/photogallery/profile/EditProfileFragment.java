package com.KLK.photogallery.profile;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EditProfileFragment extends Fragment {
    // For debugging
    private static final String TAG = "EditProfileFragment";

    private ImageView profileImage;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileImage = (ImageView)view.findViewById(R.id.profile_photo);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);


        initImageLoader();
        setActivityWidgets();
        setProfileImage();

        // navigate back to ProfileActivity
        ImageView backArrow = (ImageView)view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to Profile Activity!");
                getActivity().finish();
            }
        });

        return view;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        // retrieve configuration from UniversalImageLoader.java
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setActivityWidgets(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: change profile image!");
        String imgURL = "upload.wikimedia.org/wikipedia/commons/5/56/Donald_Trump_official_portrait.jpg";
        UniversalImageLoader.setImage(imgURL, profileImage, mProgressBar , "https://");
    }

}
