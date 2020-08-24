package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView mFindFriendsRecycler;

    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeFields();

    }

    private void InitializeFields() {
        mFindFriendsRecycler = (RecyclerView)findViewById(R.id.find_friends_recycler_list);
        mFindFriendsRecycler.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar)findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find friends");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(usersReference, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visitUserId = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindFriendActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("visitUserId", visitUserId);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        mFindFriendsRecycler.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView userStatus;
        CircleImageView profileImage;


        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}