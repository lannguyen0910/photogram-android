package com.KLK.photogallery.camera;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.KLK.photogallery.R;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class NextActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "NextActivity";
    RadioRealButton button1, button2, button3;
    RadioRealButtonGroup groupButtons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        groupButtons = (RadioRealButtonGroup)findViewById(R.id.groupButtons);
        button1 = (RadioRealButton)findViewById(R.id.button1);
        button2 = (RadioRealButton)findViewById(R.id.button2);
        button3 = (RadioRealButton)findViewById(R.id.button3);

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });

        /** print noti **/
        groupButtons.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                //Toast.makeText(NextActivity.this, "Clicked: " + button.getText(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "button: onClickedButton!");
            }
        });

        groupButtons.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int currentPosition, int lastPosition) {
                //Toast.makeText(NextActivity.this,"Clicked: " + button.getText(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "button: onPositionChanged!");
            }
        });
    }
}
