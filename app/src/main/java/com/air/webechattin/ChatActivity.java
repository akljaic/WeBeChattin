package com.air.webechattin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String receiverId;
    String receiverName;
    String receiverImage;

    TextView mUserName;
    CircleImageView mUserImage;
    ImageButton mSendMessage;
    EditText mInputMessage;

    Toolbar chatToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverId = getIntent().getExtras().get("visit_user_id").toString();
        receiverName = getIntent().getExtras().get("visit_user_name").toString();
        receiverImage = getIntent().getExtras().get("visit_user_image").toString();

        InitializeFields();

        mUserName.setText(receiverName);
        Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(mUserImage);

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    private void InitializeFields() {

        chatToolbar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        mUserImage = (CircleImageView)findViewById(R.id.custom_profile_image);
        mUserName = (TextView)findViewById(R.id.custom_profile_name);
        mSendMessage = (ImageButton)findViewById(R.id.chat_send_message_button);
        mInputMessage = (EditText)findViewById(R.id.chat_input_message);
    }
}