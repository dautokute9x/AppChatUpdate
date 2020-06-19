package com.example.chatappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappdemo.fragment.ChatsFragment;
import com.example.chatappdemo.fragment.ContactsFragment;
import com.example.chatappdemo.fragment.GroupsFragment;
import com.example.chatappdemo.R;
import com.example.chatappdemo.fragment.RequestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private CircleImageView profile_image;
    private ImageButton btnSearch;
    private TextView txtTitle;
    private View notificationBadge;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        txtTitle = toolbar.findViewById(R.id.txt_Title);
//        txtTitle.setText("Me Chat");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //loadFragment(new ChatsFragment());
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListener);

        //Anh dai dien
        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_imgUser = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent_imgUser);
            }
        });

        //Search
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_search = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent_search);
            }
        });

        addBadgeView();
    }

    private void addBadgeView() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(0);

        notificationBadge = LayoutInflater.from(this).inflate(R.layout.custom_badge_layout, menuView, false);

        itemView.addView(notificationBadge);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.navigation_chat:
                    fragment = new ChatsFragment();
                    loadFragment(fragment);
                    refreshBadgeView();
                    return true;
                case R.id.navigation_contact:
                    fragment = new ContactsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_group:
                    fragment = new GroupsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_request:
                    fragment = new RequestFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void refreshBadgeView() {
        boolean badgeIsVisible = notificationBadge.getVisibility() != VISIBLE;
        notificationBadge.setVisibility(badgeIsVisible ? VISIBLE : GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, Dangnhap_Dangky_Activity.class);
            startActivity(loginIntent);
        } else {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            databaseReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((dataSnapshot.child("name").exists())) {
                        Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                        String userImage = dataSnapshot.child("imgAnhDD").getValue().toString();
                        String userName = dataSnapshot.child("name").getValue().toString();
                        txtTitle.setText(userName);

                        Picasso.with(MainActivity.this).load(userImage).into(profile_image);
                    } else {
                        Intent intent = new Intent(MainActivity.this, UpdateProfileUserActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}