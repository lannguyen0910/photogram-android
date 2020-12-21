package com.KLK.photogallery.helper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.model.Post;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

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
    private ImageView mBackArrow, mEllipses, mHeartRed, mHeartWhite, mProfileImage, mDownload;

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();

        } catch(NullPointerException e){
            Log.e(TAG, "onCreteView: NullPointerException (the received bundle is null)! ");
        }

        configBottomNavigationView();
        return view;
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

    private void configBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationView);
        BottomNavigationViewUtils.navigating(getActivity(), getActivity() ,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }
}
