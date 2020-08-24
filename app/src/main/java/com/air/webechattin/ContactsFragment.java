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
import android.widget.TextView;

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

public class ContactsFragment extends Fragment {
    View contactsFragmentView;
    RecyclerView mContactsList;

    DatabaseReference contactsReference;
    DatabaseReference usersReference;
    FirebaseAuth mAuth;

    String currentUserId;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsFragmentView = inflater.inflate(R.layout.fragment_contacts, container, false);

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsFragmentView;
    }

    private void InitializeFields() {
        mContactsList = (RecyclerView) contactsFragmentView.findViewById(R.id.contacts_list);
        mContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactsReference, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull final Contacts model) {
                String userIds = getRef(position).getKey();
                usersReference.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("image")){
                            String profileImage = snapshot.child("image").getValue().toString();
                            String profileStatus = snapshot.child("status").getValue().toString();
                            String profileName = snapshot.child("name").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.userImage);
                        }
                        else {
                            String profileStatus = snapshot.child("status").getValue().toString();
                            String profileName = snapshot.child("name").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        mContactsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView userStatus;
        CircleImageView userImage;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);
            userImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}