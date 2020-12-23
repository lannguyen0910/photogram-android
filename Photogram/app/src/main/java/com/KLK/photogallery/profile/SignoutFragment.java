package com.KLK.photogallery.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.login.LoginActivity;

/** Khôi modifies this
 * # Add SQLite
 * # Sau khi làm sign out về login page xong, nhớ kt nút quay về phía dưới:
 * - Tránh TH nó quay lại trang Signout này (setFlags cho Intent để xóa trong activity stack)
 **/
public class SignoutFragment extends Fragment {
    // For debugging
    private static final String TAG = "SignOutFragment";

    private ProgressBar progressBar;
    private TextView tvSignOut, tvSigningOut;
    ServerRequest server;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);
        server = new ServerRequest((AccountSettingActivity)getActivity());
        tvSignOut = (TextView)view.findViewById(R.id.tvConfirmSignout);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        tvSigningOut = (TextView)view.findViewById(R.id.tvSigningOut);

        Button btnConfirmSignOut = (Button)view.findViewById(R.id.btnConfirmSignout);
        Button btnCancelSignOut = (Button)view.findViewById(R.id.btnCancelSignout);

        progressBar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);

        btnCancelSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Cancel Sign Out!");
                getActivity().finish();
            }
        });

        btnConfirmSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Signing Out...!");
                progressBar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendLogOutRequest();
                    }}).start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (verifyLogout()){ goToLoginActivity();}
                        tvSigningOut.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1500);


            }

        });
//

        return view;
    }

    private boolean verifyLogout(){
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

    private void sendLogOutRequest(){
        String url = getResources().getString(R.string.logout_url);
        server.sendRequestToServer(url);

    }

    private void goToLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
