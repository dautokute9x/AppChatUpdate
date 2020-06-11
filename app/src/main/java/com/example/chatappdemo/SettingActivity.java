package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private TextView txtTaiKhoanSetting, txtTaiKhoanProfile, user_Name, tv_Logout, tv_DelAccout;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
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
        getSupportActionBar().setTitle("");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(SettingActivity.this, Dangnhap_Dangky_Activity.class));
                    finish();
                }
            }
        };

        userProfileImage =(CircleImageView) findViewById(R.id.profile_image);
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNextSelect = new Intent(SettingActivity.this, SeclecImageActivity.class);
                startActivity(intentNextSelect);
            }
        });
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

        tv_Logout = findViewById(R.id.tv_Logout);
        tv_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });

        tv_DelAccout = findViewById(R.id.tv_DelAccout);
        tv_DelAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    progressDialog.setTitle("Delete Accout");
                    progressDialog.setMessage("Please Wait!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        startActivity(new Intent(SettingActivity.this, Dangnhap_Dangky_Activity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SettingActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
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
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("imgAnhDD")))) {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveImage = dataSnapshot.child("imgAnhDD").getValue().toString();

                            user_Name.setText(retrieveUserName);
                            Picasso.with(SettingActivity.this).load(retrieveImage).into(userProfileImage);

                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            user_Name.setText(retrieveUserName);
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}