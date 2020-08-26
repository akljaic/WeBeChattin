package com.air.webechattin;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
                                            final String profileImage = snapshot.child("image").getValue().toString();

                                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.userImage);
                                        }

                                        final String profileName = snapshot.child("name").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(" send you a request!");

                                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                contactsReference.child(currentUserId).child(listUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            contactsReference.child(listUserId).child(currentUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        requestReference.child(currentUserId).child(listUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    requestReference.child(listUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                Toast.makeText(getContext(), "New contact added!", Toast.LENGTH_SHORT).show();
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
                                                    }
                                                });
                                            }
                                        });

                                        holder.declineButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                requestReference.child(currentUserId).child(listUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            requestReference.child(listUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(getContext(), "Request removed!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                            else if(type.equals("sent")){
                                Button requestSentButton = holder.itemView.findViewById(R.id.request_accept_button);
                                //requestSentButton.setText("Request sent");
                                requestSentButton.setVisibility(View.INVISIBLE);

                                usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("image")){
                                            final String profileImage = snapshot.child("image").getValue().toString();

                                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.userImage);
                                        }

                                        final String profileName = snapshot.child("name").getValue().toString();

                                        holder.userName.setText(profileName);
                                        holder.userStatus.setText(" You have sent a request to: " + profileName);

                                        holder.declineButton.setText("Cancel request");

                                        holder.declineButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                requestReference.child(currentUserId).child(listUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            requestReference.child(listUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(getContext(), "Request canceled!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });
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