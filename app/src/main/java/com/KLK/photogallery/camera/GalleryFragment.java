package com.KLK.photogallery.camera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.FilePaths;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.helper.FileSearch;
import com.KLK.photogallery.helper.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    // For debugging
    private static final String TAG = "GalleryFragment";
    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar progressBar;

    private final String mAppend = "file:/";

    private static Uri[] mUrls = null;
    private static String[] strUrls = null;
    private String[] mNames = null;
    private ProgressDialog myProgressDialog = null;
    private Cursor cc = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        gridView = (GridView) view.findViewById(R.id.gridView);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Log.d(TAG, "onCreateView: start!");

        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment!");
                getActivity().finish();
            }
        });

        TextView nextScreen = (TextView) view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen!");

            }
        });


        fetchImagefromLocal();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchImagefromLocal();
    }


    private void fetchImagefromLocal(){
        init();
        try {
            Thread.sleep(100);
            setupGridView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            Toast.makeText(getActivity(),"Not able to find directory", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void init() {
        cc = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);
        if (cc != null) {
            myProgressDialog = new ProgressDialog(getActivity());
            myProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myProgressDialog.setMessage("Wait");
            myProgressDialog.show();

            new Thread() {
                public void run() {
                    try {
                        cc.moveToFirst();
                        mUrls = new Uri[cc.getCount()];
                        strUrls = new String[cc.getCount()];
                        mNames = new String[cc.getCount()];
                        for (int i = 0; i < cc.getCount(); i++) {
                            cc.moveToPosition(i);
                            mUrls[i] = Uri.parse(cc.getString(1));
                            strUrls[i] = cc.getString(1);
                            mNames[i] = cc.getString(3);
                            //Log.e("mNames[i]",mNames[i]+":"+cc.getColumnCount()+ " : " +cc.getString(3));
                        }

                    } catch (Exception e) {
                    }
                    myProgressDialog.dismiss();
                }
            }.start();
        }
    }

    private ArrayList<String> getUrlList(){
        ArrayList<String> imgURLs = new ArrayList<>();
        for (int i =strUrls.length-1;i>=0;i--){
            File file = new File(strUrls[i]);
            if(file.exists()) {imgURLs.add(strUrls[i]);}
            else { Log.e(TAG, "filename" + strUrls[i] + " not exists");}
        }
        return imgURLs;
    }

    private void setupGridView(){
        Log.e(TAG,"Start gridview");
        ArrayList<String> imgURLs = getUrlList();
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridView

        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

//        //set the first image to be displayed when the activity fragment view is inflated
        try{
            setImage(imgURLs.get(0), galleryImage, mAppend);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
            }
        });
    }

    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


}