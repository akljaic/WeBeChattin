package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String receiverId;
    String receiverName;
    String receiverImage;

    String senderId;

    TextView mUserName;
    CircleImageView mUserImage;
    ImageButton mSendMessage;
    EditText mInputMessage;

    Toolbar chatToolbar;

    FirebaseAuth mAuth;
    DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference();

        receiverId = getIntent().getExtras().get("visit_user_id").toString();
        receiverName = getIntent().getExtras().get("visit_user_name").toString();
        receiverImage = getIntent().getExtras().get("visit_user_image").toString();

        InitializeFields();

        mUserName.setText(receiverName);
        Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(mUserImage);

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
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

    private void SendMessage() {
        String messageText = mInputMessage.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Write a message", Toast.LENGTH_SHORT).show();
        }
        else {
            String messageSenderRef = "Messages/" + senderId + "/" + receiverId;
            String messageReceiverRef = "Messages/" + receiverId + "/" + senderId;

            DatabaseReference userMessageKeyReference = rootReference.child("Messages").child(messageSenderRef).child(messageReceiverRef).push();

            String messagePushId = userMessageKeyReference.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", senderId);
            //messageTextBody.put("from", senderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushId, messageTextBody);

            rootReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "msg sent ", Toast.LENGTH_SHORT).show();
                    }
                    mInputMessage.setText("");
                }
            });
        }
    }
}