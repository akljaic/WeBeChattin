package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        //CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            //messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            //messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            //messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            //messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private GoogleSignInClient mSignInClient;

    String mPhone, mPhotoURL, mUsername;
    final String ANONYMOUS = "anonymous";

    DatabaseReference mFirebaseDatabaseReference;
    FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        else {
            mPhone = mFirebaseUser.getDisplayName();
            if(mFirebaseUser.getPhotoUrl() != null){
                mPhotoURL = mFirebaseUser.getPhotoUrl().toString();
            }

            startActivity(new Intent(this, UsersActivity.class));
            finish();
        }

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @NonNull
            @Override
            public FriendlyMessage parseSnapshot(@NonNull DataSnapshot snapshot) {
                return null;
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                mFirebaseAuth.signOut();
                mSignInClient.signOut();

                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}