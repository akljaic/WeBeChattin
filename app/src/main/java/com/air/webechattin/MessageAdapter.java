package com.air.webechattin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<Messages> userMessagesList;

    FirebaseAuth mAuth;
    DatabaseReference usersReference;

    public MessageAdapter (List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends  RecyclerView.ViewHolder{
        TextView mSenderMessageText;
        TextView mReceiverMessageText;
        CircleImageView mReceiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mSenderMessageText = (TextView)itemView.findViewById(R.id.sender_message_text);
            mReceiverMessageText = (TextView)itemView.findViewById(R.id.receiver_message_text);
            mReceiverProfileImage = (CircleImageView)itemView.findViewById(R.id.message_profile_image);
        }
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
        String fromMessageType = messages.getType();

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

        if (fromUserId.equals(messageSenderId)){
            holder.mSenderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
            holder.mSenderMessageText.setText(messages.getMessage());
        }
        else{
            holder.mSenderMessageText.setVisibility(View.INVISIBLE);

            holder.mReceiverProfileImage.setVisibility(View.VISIBLE);
            holder.mReceiverMessageText.setVisibility(View.VISIBLE);

            holder.mReceiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
            holder.mReceiverMessageText.setText(messages.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

}
