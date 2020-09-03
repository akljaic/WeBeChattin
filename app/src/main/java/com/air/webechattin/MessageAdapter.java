package com.air.webechattin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.air.encryption2.AESCryptography;
import com.air.encryption2.IEncryption;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.startActivity;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<Messages> userMessagesList;

    FirebaseAuth mAuth;
    DatabaseReference usersReference;

    IEncryption encryption;

    public MessageAdapter (List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends  RecyclerView.ViewHolder{
        TextView mSenderMessageText;
        TextView mReceiverMessageText;
        CircleImageView mReceiverProfileImage;
        TextView mSentTimestampText;
        TextView mReceivedTimestampText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mSenderMessageText = (TextView)itemView.findViewById(R.id.sender_message_text);
            mReceiverMessageText = (TextView)itemView.findViewById(R.id.receiver_message_text);
            mReceiverProfileImage = (CircleImageView)itemView.findViewById(R.id.message_profile_image);
            mSentTimestampText = (TextView)itemView.findViewById(R.id.sent_timestamp_text);
            mReceivedTimestampText = (TextView)itemView.findViewById(R.id.received_timestamp_text);
        }
    }

    private IEncryption setEncryption(IEncryption enc) {
        enc = new AESCryptography();
        return enc;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent,false);
        MessageViewHolder viewHolder = new MessageViewHolder(view);

        mAuth = FirebaseAuth.getInstance();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        //String fromMessageType = messages.getType();
        String fromMessage = messages.getMessage();

        encryption = setEncryption(encryption);

        String decryptedMessage = encryption.DecryptMessage(fromMessage);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("image")){
                    String receiverImage = snapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(holder.mReceiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.mReceiverMessageText.setVisibility(View.INVISIBLE);
        holder.mReceiverProfileImage.setVisibility(View.INVISIBLE);
        holder.mSenderMessageText.setVisibility(View.INVISIBLE);

        holder.mSentTimestampText.setVisibility(View.INVISIBLE);
        holder.mReceivedTimestampText.setVisibility(View.INVISIBLE);

        if (fromUserId.equals(messageSenderId)){
            holder.mSenderMessageText.setVisibility(View.VISIBLE);
            holder.mSentTimestampText.setVisibility(View.VISIBLE);

            holder.mSenderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
            holder.mSenderMessageText.setText(decryptedMessage);
            holder.mSentTimestampText.setText(messages.getTime() + " " + messages.getDate());
        }
        else{
            holder.mReceiverProfileImage.setVisibility(View.VISIBLE);
            holder.mReceiverMessageText.setVisibility(View.VISIBLE);
            holder.mReceivedTimestampText.setVisibility(View.VISIBLE);

            holder.mReceiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
            holder.mReceiverMessageText.setText(decryptedMessage);
            holder.mReceivedTimestampText.setText(messages.getTime() + " " + messages.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

}
