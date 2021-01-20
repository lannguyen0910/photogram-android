package com.KLK.photogallery.profile;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.ImageEncoderDecoder;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.helper.SharedPref;
import com.KLK.photogallery.helper.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {
    // For debugging
    private static final String TAG = "EditProfileFragment";
    private final int REQUEST_CHANGE_AVATAR = 1;
    private ImageView profileImage, checkMark;
    private ProgressBar mProgressBar;

    // EditProfile Fragment widgets
    private EditText mFullName, mEmail, mPhoneNumber, mPassword;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    private SharedPref sharedPref;
    ServerRequest server;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileImage = (ImageView)view.findViewById(R.id.profile_photo);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);

        // widgets
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mFullName = (EditText) view.findViewById(R.id.full_name);
        mPassword = (EditText) view.findViewById(R.id.changePassword);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        sharedPref = new SharedPref(getActivity().getApplicationContext());
        checkMark = (ImageView) view.findViewById(R.id.saveChanges);
        server = new ServerRequest(getActivity());

        initImageLoader();
        setActivityWidgets();

        setDefaultInfo();


        // navigate back to ProfileActivity
        ImageView backArrow = (ImageView)view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to Profile Activity!");
                getActivity().finish();
            }
        });


        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfileImage();
            }
        });

        
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                mProgressBar.setVisibility(View.VISIBLE);
                sendChangeRequestToServer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (verifyUpdate()) {
                            saveProfileSettings();
                        }
                        mProgressBar.setVisibility(View.GONE);
                        getActivity().finish();
                    }
                }, 1000);

            }
        });

        return view;
    }

    private void setDefaultInfo() {
        String fullname = sharedPref.getString("fullname");
        String phone_number = sharedPref.getString("phone_number");
        String email = sharedPref.getString("email");
        String avatarBase64 = sharedPref.getString("avatar");
        Bitmap avatar_bm = ImageEncoderDecoder.decodeBase64ToBitmap(avatarBase64);
        profileImage.setImageBitmap(avatar_bm);
        mFullName.setText(fullname);
        mPassword.setText("None");
        mEmail.setText(email);
        mPhoneNumber.setText(phone_number);
    }

    private boolean verifyUpdate() {
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

    private void sendChangeRequestToServer() {
        String url = getResources().getString(R.string.update_url);
        String fullname = mFullName.getText().toString();
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();
        String phone_number = mPhoneNumber.getText().toString();
        String username = sharedPref.getString("username");
        BitmapDrawable drawable = (BitmapDrawable) mProfilePhoto.getDrawable();
        Bitmap avatar_bm = drawable.getBitmap();
        String avatarBase64 = ImageEncoderDecoder.encodeBitmapToBase64(avatar_bm);
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("name", fullname);
        params.put("password1", password);
        params.put("password2", password);
        params.put("email", email);
        params.put("phone_number", phone_number);
        params.put("avatar", avatarBase64);
        server.sendRequestToServer(url,params);
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        // retrieve configuration from UniversalImageLoader.java
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setActivityWidgets(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void chooseProfileImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHANGE_AVATAR);
    }


    private void saveProfileSettings() {
        String fullname = mFullName.getText().toString();
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();
        String phone_number = mPhoneNumber.getText().toString();
        BitmapDrawable drawable = (BitmapDrawable) mProfilePhoto.getDrawable();
        Bitmap avatar_bm = drawable.getBitmap();
        String avatarBase64 = ImageEncoderDecoder.encodeBitmapToBase64(avatar_bm);

        sharedPref.setString("fullname",fullname);
        sharedPref.setString("avatar",avatarBase64);
        sharedPref.setString("password",password);
        sharedPref.setString("email",email);
        sharedPref.setString("phone_number",phone_number);
    }

    /** -------------------------------------------------------------------------------- **/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_AVATAR){
            Uri selectedImage = data.getData();


            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            profileImage.setImageBitmap(bitmap);
        }
    }


}
