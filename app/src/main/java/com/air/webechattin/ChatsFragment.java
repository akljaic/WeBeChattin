package com.air.webechattin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

     View privateChatFragmentView;
     RecyclerView mChatList;

     DatabaseReference chatsReference;
     DatabaseReference usersReference;
     DatabaseReference contactsReference;
     FirebaseAuth mAuth;

    String currentUserId;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        //chatsReference = FirebaseDatabase.getInstance().getReference().child("Chats")
        chatsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts");

        InitializeFields();

        return privateChatFragmentView;
    }

    private void InitializeFields() {
        mChatList = (RecyclerView)privateChatFragmentView.findViewById(R.id.chats_list);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatsReference, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                final String profileId = getRef(position).getKey();
                final String[] profileImage = {"default_image"};

                usersReference.child(profileId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if(snapshot.hasChild("image")){
                                profileImage[0] = snapshot.child("image").getValue().toString();
                                Picasso.get().load(profileImage[0]).placeholder(R.drawable.profile_image).into(holder.userImage);
                            }
                            String profileStatus = snapshot.child("status").getValue().toString();
                            final String profileName = snapshot.child("name").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", profileId);
                                    chatIntent.putExtra("visit_user_name", profileName);
                                    chatIntent.putExtra("visit_user_image", profileImage[0]);
                                    startActivity(chatIntent);

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                ChatsViewHolder viewHolder = new ChatsViewHolder(view);
                return viewHolder;
            }
        };

        mChatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userStatus;
        CircleImageView userImage;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);
            userImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}