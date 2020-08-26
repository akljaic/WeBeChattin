package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String receiverId;
    String receiverName;
    String receiverImage;
    String senderId;
    String currentDate;
    String currentTime;

    TextView mUserName;
    CircleImageView mUserImage;
    ImageButton mSendMessage;
    EditText mInputMessage;
    RecyclerView mUserMessagesList;

    Toolbar chatToolbar;


    final List<Messages> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;

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

        mUserMessagesList = (RecyclerView)findViewById(R.id.private_messages_list_of_users);
        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        mUserMessagesList.setLayoutManager(linearLayoutManager);
        mUserMessagesList.setAdapter(messageAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootReference.child("Messages").child(senderId).child(receiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);

                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();


                mUserMessagesList.smoothScrollToPosition(mUserMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendMessage() {
        String messageText = mInputMessage.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Write a message", Toast.LENGTH_SHORT).show();
        }
        else {
            String messageSenderRef = "Messages/" + senderId + "/" + receiverId;
            String messageReceiverRef = "Messages/" + receiverId + "/" + senderId;

            Calendar calendarForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            currentDate = currentDateFormat.format(calendarForDate.getTime());

            Calendar calendarForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
            currentTime = currentTimeFormat.format(calendarForTime.getTime());

            DatabaseReference userMessageKeyReference = rootReference.child("Messages").child(messageSenderRef).child(messageReceiverRef).push();

            String messagePushId = userMessageKeyReference.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", senderId);
            messageTextBody.put("date", currentDate);
            messageTextBody.put("time", currentTime);
            //messageTextBody.put("from", senderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushId, messageTextBody);

            rootReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    mInputMessage.setText("");
                }
            });
        }
    }
}