package com.air.webechattin;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    String receiverUserId;
    String senderUserId;
    String currentState;

    CircleImageView mUserProfileImage;
    TextView mUserProfileName;
    TextView mUserProfileStatus;
    Button mSendRequestButton;
    Button mDeclineRequestButton;

    DatabaseReference usersReference;
    DatabaseReference chatRequestReference;
    DatabaseReference contactsReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserId = getIntent().getExtras().get("visitUserId").toString();
        senderUserId = mAuth.getCurrentUser().getUid();

        InitializeFields();

        RetrieveUserInfo();
    }

    private void InitializeFields() {
        mUserProfileImage = (CircleImageView)findViewById(R.id.visit_profile_image);
        mUserProfileName = (TextView)findViewById(R.id.visit_profile_name);
        mUserProfileStatus = (TextView)findViewById(R.id.visit_profile_status);
        mSendRequestButton = (Button)findViewById(R.id.send_message_request_button);
        mDeclineRequestButton = (Button)findViewById(R.id.decline_message_request_button);

        currentState = "new";
    }

    private void RetrieveUserInfo() {
        usersReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image"))){
                    String userImage = snapshot.child("image").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(mUserProfileImage);
                    mUserProfileName.setText(userName);
                    mUserProfileStatus.setText(userStatus);

                    ManageChatRequests();
                }
                else {
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    mUserProfileName.setText(userName);
                    mUserProfileStatus.setText(userStatus);

                    ManageChatRequests();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequests() {
        chatRequestReference.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(receiverUserId)){
                    String requestType = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                    if (requestType.equals("sent")){
                        currentState = "request_sent";
                        mSendRequestButton.setText("Cancel chat request");
                    }
                    else if (requestType.equals("received")){
                        currentState = "request_received";
                        mSendRequestButton.setText("Accept chat request");

                        mDeclineRequestButton.setVisibility(View.VISIBLE);
                        mDeclineRequestButton.setEnabled(true);

                        mDeclineRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelChatRequest();
                            }
                        });
                    }
                }
                else{
                    contactsReference.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserId)){
                                currentState = "friends";
                                mSendRequestButton.setText("Remove this contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (!senderUserId.equals(receiverUserId)){
            mSendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSendRequestButton.setEnabled(false);

                    if (currentState.equals("new")){
                        SendChatRequest();
                    }
                    if(currentState.equals("request_sent")){
                        CancelChatRequest();
                    }
                    if(currentState.equals("request_received")){
                        AcceptChatRequest();
                    }
                }
            });
        }
        else {
            mSendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void SendChatRequest() {
        chatRequestReference.child(senderUserId).child(receiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chatRequestReference.child(receiverUserId).child(senderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mSendRequestButton.setEnabled(true);
                                currentState = "request_sent";
                                mSendRequestButton.setText("Cancel chat request ");
                            }
                        }
                    });

                }
            }
        });
    }

    private void AcceptChatRequest() {
        contactsReference.child(senderUserId).child(receiverUserId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    contactsReference.child(receiverUserId).child(senderUserId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                    chatRequestReference.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatRequestReference.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mSendRequestButton.setEnabled(true);
                                                        currentState = "friends";
                                                        mSendRequestButton.setText("Remove this contact");

                                                        mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                        mDeclineRequestButton.setEnabled(false);
                                                    }
                                                });
                                            }
                                        }
                                    });
                            }
                        }
                    });
                }
            }
        });
    }

    private void CancelChatRequest() {
        chatRequestReference.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chatRequestReference.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mSendRequestButton.setEnabled(true);
                                currentState = "new";
                                mSendRequestButton.setText("Send message");

                                mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                mDeclineRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }
}