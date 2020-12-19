package com.KLK.photogallery.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.KLK.photogallery.R;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);

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

                /**
                 * Remember to add this!!!
                 * Call database sign out method: signOut()
                 * Ex: sqlite.signOut()?
                 **/

                getActivity().finish();
            }

        });


        return view;
    }
}
