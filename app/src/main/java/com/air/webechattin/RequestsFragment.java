package com.air.webechattin;

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

public class RequestsFragment extends Fragment {

    View requestsFragmentView;
    RecyclerView mRequestList;

    FirebaseAuth mAuth;
    DatabaseReference requestReference;
    DatabaseReference usersReference;
    DatabaseReference contactsReference;

    String currentUserId;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        requestReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts");

        InitializeFields();

        return requestsFragmentView;
    }

    private void InitializeFields() {
        mRequestList = (RecyclerView)requestsFragmentView.findViewById(R.id.chat_requests_list);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(requestReference.child(currentUserId), Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {
                holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_decline_button).setVisibility(View.VISIBLE);

                final String listUserId = getRef(position).getKey();

                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String type = snapshot.getValue().toString();

                            if (type.equals("received")){
                                usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("image")){
                                            String profileImage = snapshot.child("image").getValue().toString();
                                            String profileStatus = snapshot.child("status").getValue().toString();
                                            String profileName = snapshot.child("name").getValue().toString();

                                            holder.userName.setText(profileName);
                                            holder.userStatus.setText("wants to connect with you.");
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                RequestsViewHolder viewHolder = new RequestsViewHolder(view);
                return viewHolder;
            }
        };

        mRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView userStatus;
        CircleImageView userImage;
        Button acceptButton;
        Button declineButton;


        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);
            userImage = itemView.findViewById(R.id.users_profile_image);
            acceptButton = itemView.findViewById(R.id.request_accept_button);
            declineButton = itemView.findViewById(R.id.request_decline_button);
        }
    }
}