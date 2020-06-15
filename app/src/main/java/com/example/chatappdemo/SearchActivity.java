package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
    private ImageButton imgBtnBack;
    private RecyclerView recycler_find_friend;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recycler_find_friend = findViewById(R.id.recycler_find_friend);
        recycler_find_friend.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(userRef, User.class)
                .build();

        FirebaseRecyclerAdapter<User, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder,final int i, @NonNull User user) {
                        findFriendViewHolder.tv_username.setText(user.getName());
                        findFriendViewHolder.tv_status_item.setText(user.getStatus());
                        Picasso.with(SearchActivity.this).load(user.getImgAnhDD()).placeholder(R.drawable.user_profile).into(findFriendViewHolder.profileImage);

                        findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_userId = getRef(i).getKey();
                                Intent profileIntent = new Intent(SearchActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_userId", visit_userId);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };
        recycler_find_friend.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username, tv_status_item;
        CircleImageView profileImage;
        CircleImageView img_On_Off;
        public FindFriendViewHolder(View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_user_name);
            profileImage = (CircleImageView) itemView.findViewById(R.id.user_profile);
            img_On_Off = (CircleImageView) itemView.findViewById(R.id.user_on_off);
            tv_status_item = itemView.findViewById(R.id.tv_status_item);
        }
    }
}