package com.KLK.photogallery.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.widget.GridView;

import java.util.List;

/** data model for a single post **/
public class Post implements Parcelable {

    private String image;
    private String photo_id;
//    private String user_id;
    private boolean likes;
    private boolean download;

    public Post() {
    }

    public Post(String imgBase64, String photo_id, boolean likes, boolean download) {
//        this.image_path = image_path;
        this.photo_id = photo_id;
        this.image = imgBase64;
        this.likes = likes;
//        this.user_id = user_id;
        this.download = download;
    }

    protected Post(Parcel in) {
        image = in.readString();
        photo_id = in.readString();
//        user_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(photo_id);
//        dest.writeString(user_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public static Creator<Post> getCREATOR() {
        return CREATOR;
    }

    public String getImageBase64() {
        return image;
    }

    public void setImageBase64(String image) {
        this.image = image;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }
//
//    public String getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(String user_id) {
//        this.user_id = user_id;
//    }

    public boolean getLikes() {
        return false;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }

    public boolean getDownload() {
        return false;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    @Override
    public String toString() {
        return "Post{" +
//                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
//                ", user_id='" + user_id + '\'' +
                ", likes=" + likes +
                ", download=" + download +
                '}';
    }
}

