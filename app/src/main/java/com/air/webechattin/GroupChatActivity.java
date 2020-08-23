package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

public class GroupChatActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ImageButton mSendMessageButton;
    EditText mUserMessageInput;
    ScrollView mScrollView;
    TextView mDisplayTextMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        InitializeFields();
    }

    private void InitializeFields() {
        mToolbar = (Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Group name");

        mSendMessageButton = (ImageButton)findViewById(R.id.group_chat_send_message_button);
        mUserMessageInput = (EditText)findViewById(R.id.group_chat_input_message);
        mDisplayTextMessages = (TextView)findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView)findViewById(R.id.group_chat_scroll_view);
    }
}