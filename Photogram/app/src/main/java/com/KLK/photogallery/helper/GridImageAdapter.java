package com.KLK.photogallery.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.KLK.photogallery.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgArray;
    private int type;
    private final int IMG_STRING = 1;
    private final int IMG_URL = 2;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgArray = imgURLs;
        this.type = IMG_URL;
    }

    public GridImageAdapter(Context context, int layoutResource,  ArrayList<String> imgBase64Strings) {
        super(context, layoutResource, imgBase64Strings);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        this.imgArray = imgBase64Strings;
        this.type = IMG_STRING;
    }

    private static class ViewHolder{
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // ViewHolder build pattern (Similar to recyclerview)
        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        switch (this.type){
            case IMG_URL:
                String imgURL = getItem(position);
                String imgURI = mAppend + imgURL;

                ImageLoader imageLoader = ImageLoader.getInstance();

                imageLoader.displayImage(imgURI, holder.image, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if(holder.mProgressBar != null){
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if(holder.mProgressBar != null){
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if(holder.mProgressBar != null){
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        if(holder.mProgressBar != null){
                            holder.mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
                break;

            case IMG_STRING:
                String imgString = getItem(position);
                Bitmap bm = ImageEncoderDecoder.decodeBase64ToBitmap(imgString);
                holder.image.setImageBitmap(bm);
                holder.mProgressBar.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }
}