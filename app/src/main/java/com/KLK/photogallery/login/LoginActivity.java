package com.KLK.photogallery.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.ServerRequest;
import com.KLK.photogallery.home.MainActivity;

import java.util.HashMap;
import java.util.Map;

/** Khoi add authentication in this **/
public class LoginActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "LoginActivity";

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mUsername, mPassword;
    private TextView mPleaseWait;
    ServerRequest server = new ServerRequest(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Start!");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;



        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                mPleaseWait.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendLoginRequest(username, password);
                    }
                });
                thread.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (verifyLogin()){ goToMainActivity();}
                        mPleaseWait.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }, 1500);

            }
        });


        /**
         * Remember to set View.VISIBLE when click Login Button (in Authentication)
         * And set View.GONE when success Login
         **/

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

    }

    private boolean verifyLogin(){
        int response = server.getResponse();
        String message = server.getMessage();
        Toast.makeText(this,message,Toast.LENGTH_SHORT);
        if (response == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    private void sendLoginRequest(String username, String password){
        String url = getResources().getString(R.string.login_url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", username);
        params.put("pwd", password);
        server.sendRequestToServer(url, params);
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
