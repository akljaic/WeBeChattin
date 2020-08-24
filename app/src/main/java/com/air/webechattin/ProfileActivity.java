package com.air.webechattin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    String receiverUserId;
    CircleImageView mUserProfileImage;
    TextView mUserProfileName;
    TextView mUserProfileStatus;
    Button mSendRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserId = getIntent().getExtras().get("visitUserId").toString();

        Toast.makeText(this, "User ID: " + receiverUserId, Toast.LENGTH_SHORT).show();

        InitializeFields();

    }

    private void InitializeFields() {
        mUserProfileImage = (CircleImageView)findViewById(R.id.visit_profile_image);
        mUserProfileName = (TextView)findViewById(R.id.visit_profile_name);
        mUserProfileStatus = (TextView)findViewById(R.id.visit_profile_status);
    }
}