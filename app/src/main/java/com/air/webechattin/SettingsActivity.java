package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    Button mUpdateAccountSettings;
    EditText mUsername;
    EditText mUserStatus;
    CircleImageView mUserProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        InitializeFields();
    }

    private void InitializeFields() {
        mUpdateAccountSettings = (Button)findViewById(R.id.update_settings_button);
        mUsername = (EditText)findViewById(R.id.set_user_name);
        mUserStatus = (EditText)findViewById(R.id.set_profile_status);
        mUserProfileImage = (CircleImageView)findViewById(R.id.set_profile_image);

    }
}