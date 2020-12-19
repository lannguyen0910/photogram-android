package com.KLK.photogallery.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.KLK.photogallery.R;

/** Khoi add authentication in this **/
public class LoginActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "LoginActivity";

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Start!");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;

        /**
         * Remember to set View.VISIBLE when click Login Button (in Authentication)
         * And set View.GONE when success Login
         **/
        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

    }


}
