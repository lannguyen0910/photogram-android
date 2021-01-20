package com.KLK.photogallery.love;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.KLK.photogallery.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.CustomViewHolder> {

    private final List<String> mFileList;
    private final Activity mActivity;

    public GalleryAdapter(Activity activity, List<String> fileList) {
        mActivity = activity;
        mFileList = fileList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_imageview, parent, false);
        return new GalleryAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Glide
                .with(mActivity)
                .load(mFileList.get(position))
                .override(200, 200)
                .centerCrop()
                .into(holder.imageResource);

        final int itemPosition = holder.getAdapterPosition();
        holder.imageResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, mFileList.get(itemPosition), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageResource;
        CustomViewHolder(View itemView) {
            super(itemView);
            this.imageResource = (ImageView) itemView.findViewById(R.id.gridImageView);
        }
    }
}

