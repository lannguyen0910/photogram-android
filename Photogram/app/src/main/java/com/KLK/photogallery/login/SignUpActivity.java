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
import com.KLK.photogallery.helper.SharedPref;
import com.KLK.photogallery.home.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "SignUpActivity";

    private Context mContext;
    private EditText mPassword, mUsername;
    private EditText mPassword2, mFullname;
    private EditText mEmail, mPhonenumber;
    private TextView loadingPleaseWait;
    private Button btnRegister;
    private ProgressBar mProgressBar;
    ServerRequest server;
    SharedPref sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: Start!");

        mContext = this;
        mUsername = (EditText) findViewById(R.id.reg_username);
        mPassword = (EditText) findViewById(R.id.reg_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mFullname = (EditText) findViewById(R.id.reg_fullname);
        mPassword2 = (EditText) findViewById(R.id.reg_password2);
        mEmail = (EditText) findViewById(R.id.reg_email);
        mPhonenumber = (EditText) findViewById(R.id.reg_phonenumber);
        btnRegister = (Button) findViewById(R.id.btn_register);
        loadingPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        server = new ServerRequest(this);
        sharedPref = new SharedPref(this.getApplicationContext());

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String fullname = mFullname.getText().toString();
                String password2 = mPassword2.getText().toString();
                String email = mEmail.getText().toString();
                String phonenumber = mPhonenumber.getText().toString();

                loadingPleaseWait.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendSignUpRequest(
                                username, password, fullname,
                                password2, email, phonenumber);
                    }
                });
                thread.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (verifyLogin()){
                            fetchUserInfoToSP();
                            goToMainActivity();
                        }
                        loadingPleaseWait.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }, 1500);
            }
        });
        /**
         * Remember to set View.VISIBLE when click SignUp Button (in Authentication)
         * And set View.GONE when success Login
         **/
        loadingPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);


    }


    private void fetchUserInfoToSP(){
        JSONObject userInfo = server.getUserInfo();
        sharedPref.storeJSONObject(userInfo);
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

    private void sendSignUpRequest(
            String username, String password,
            String fullname, String password2,
            String email, String phonenumber) {
        String url = getResources().getString(R.string.signup_url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("name", fullname);
        params.put("password1", password);
        params.put("password2", password2);
        params.put("email", email);
        params.put("phone_number", phonenumber);
        server.sendRequestToServer(url, params);
    }


    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}