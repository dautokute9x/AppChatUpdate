package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch darkSwitch;
    private TextView txtTaiKhoanSetting, txtTaiKhoanProfile, user_Name;

    private FirebaseAuth firebaseAuth;
    private String currentUserID;
    private DatabaseReference databaseReference;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cài đặt");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userProfileImage =(CircleImageView) findViewById(R.id.profile_image);
        user_Name = findViewById(R.id.user_Name);

        darkSwitch = findViewById(R.id.switch_dark);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkSwitch.setChecked(true);
        }
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    restartApp();
                }
            }
        });

        txtTaiKhoanSetting = findViewById(R.id.txt_taikhoan_setting);
        txtTaiKhoanSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TaiKhoanActivity.class));
            }
        });
        txtTaiKhoanProfile = findViewById(R.id.txt_tt_taikhoan);
        txtTaiKhoanProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ProfileUserActivity.class));
            }
        });

        UserInfor();
    }
    public void restartApp() {
        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        startActivity(intent);
        finish();
    }

    private void UserInfor() {
        databaseReference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name_signup") && (dataSnapshot.hasChild("image")))) {
                            String retrieveUserName = dataSnapshot.child("name_signup").getValue().toString();
                            String retrieveImage = dataSnapshot.child("image").getValue().toString();

                            user_Name.setText(retrieveUserName);
                            Picasso.with(SettingActivity.this).load(retrieveImage).into(userProfileImage);

                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name_signup"))) {
                            String retrieveUserName = dataSnapshot.child("name_signup").getValue().toString();
                            user_Name.setText(retrieveUserName);
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}