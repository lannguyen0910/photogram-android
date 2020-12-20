package com.KLK.photogallery.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.Permissions;

public class PhotoFragment extends Fragment {
    // For debugging
    private static final String TAG = "PhotoFragment";

    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int  CAMERA_REQUEST_CODE = 10;


    Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo,container,false);
        Log.d(TAG, "onCreateView: Start!");

        Button btnLaunchCamera = (Button) view.findViewById(R.id.btnLaunchCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launch camera!");
                if(((CameraActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if( ((CameraActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0]) ){
                        Log.d(TAG, "onClick: starting camera");
//                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE); // Start camera
                        dispatchTakePictureIntent();
                    }
                    else {
                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                        //clear activities stack, attach flag for sharing task
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

        });

        return view;
    }



    /** Capture image **/
    private void dispatchTakePictureIntent() {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getActivity().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }


    /** Capture request code and retrieve photo **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            Log.d(TAG, "onActivityResult: taking a photo!");
//            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
//                    getContentResolver(), imageUri);
//            thumbnail = rotateImageIfRequired(this, thumbnail, imageUri);
//            thumbnail = resizeBitmap(thumbnail, maxImageSize);
            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager_container);
            viewPager.setCurrentItem(0);
        }
    }
}
