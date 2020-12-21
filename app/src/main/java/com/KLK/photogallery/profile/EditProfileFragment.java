package com.KLK.photogallery.profile;

import android.content.ContentProvider;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
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
import com.KLK.photogallery.helper.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {
    // For debugging
    private static final String TAG = "EditProfileFragment";
    private final int REQUEST_CHANGE_AVATAR = 1;
    private ImageView profileImage;
    private ProgressBar mProgressBar;
    private TextView tvChangePhoto;

    // EditProfile Fragment widgets
    private EditText mFullName, mEmail, mPhoneNumber, mPassword;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileImage = (ImageView)view.findViewById(R.id.profile_photo);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        tvChangePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);

        // widgets
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mFullName = (EditText) view.findViewById(R.id.full_name);
        mPassword = (EditText) view.findViewById(R.id.changePassword);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);

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

        tvChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfileImage();
            }
        });

        ImageView checkMark = (ImageView) view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
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

    private void chooseProfileImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHANGE_AVATAR);
    }

    /** --------------------------------------------------------------------------------
     * Retrieves the data contained in the widgets and submits it to the database
     * Make sure the username chosen is unique before submitting:
     * Step 1: If (make change to Email or Password or both):
     *              // Pop up a dialog, confirm the password or email (Use below script)
     *             ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
     *             dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
     *             dialog.setTargetFragment(EditProfileFragment.this, 1);
     *
     * Step 2: Check email or password that was already used (database)
     *
     * Step 3: Change the email or password
     *
     * Step 4: change the rest of the settings that do not require uniqueness (phone, avatar, full name?,...)
     *
     * !!!!! Remember to display the result of EditProfile and ProfileActivity after SignUp or Edit !!!!!


     **/

    private void saveProfileSettings() {

    }

    /** -------------------------------------------------------------------------------- **/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_AVATAR){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.e(TAG, "Change profile avatar");
            Uri path = Uri.parse(new File("" + picturePath).toString());
            UniversalImageLoader.setImage(path.toString(), profileImage, mProgressBar , "file://");

        }
    }


}
