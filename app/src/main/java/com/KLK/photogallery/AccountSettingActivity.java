package com.KLK.photogallery;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "AccountSettingActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        context = AccountSettingActivity.this;
        Log.d(TAG, "Start onCreate()!");

        createSettingList();

        //navigate back to profile activity
        ImageView backArrow = (ImageView)findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigate back to profile activity!");
                finish();
            }
        });
    }

    // options for Settings
    private void createSettingList(){
        Log.d(TAG, "create setting list!");
        ListView listView = (ListView)findViewById(R.id.lvAccountSettings);

        ArrayList<String>options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add(getString(R.string.sign_out));

        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1,options);
        listView.setAdapter(arrayAdapter);
    }
}
