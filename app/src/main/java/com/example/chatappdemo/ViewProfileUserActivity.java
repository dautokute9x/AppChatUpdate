package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileUserActivity extends AppCompatActivity {
    private ImageButton imgBtnBG;
    private CircleImageView imgBtnCamBG, imgBtnDD;
    private TextView tv_userName, tv_Phone, tv_Status, tv_Gioitinh, tv_Done;
    private ProgressDialog progressDialog;
    private String currentUserId;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_user);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        DisplayProfile();
        AnhXa();
    }

    private void AnhXa() {
        imgBtnBG = findViewById(R.id.imgBtnBG);
        imgBtnBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePhoto();
            }
        });
        imgBtnCamBG = findViewById(R.id.imgBtnCamBG);
        imgBtnCamBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePhoto();
            }
        });
        imgBtnDD = findViewById(R.id.imgBtnDD_ViewProfile);
        imgBtnDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_userName = findViewById(R.id.tv_userName);
        tv_Phone = findViewById(R.id.tv_Phone);
        tv_Status = findViewById(R.id.tv_Status);
        tv_Gioitinh = findViewById(R.id.tv_Gioitinh);
        progressDialog = new ProgressDialog(this);
        tv_Done = findViewById(R.id.tv_Done);
        tv_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackMain = new Intent(ViewProfileUserActivity.this, MainActivity.class);
                startActivity(intentBackMain);
            }
        });
    }

    private void ChoosePhoto() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void DisplayProfile() {
        databaseReference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && ((dataSnapshot.hasChild("imgAnhDD"))
                                && (dataSnapshot.hasChild("name"))
                                && (dataSnapshot.hasChild("status"))
                                && (dataSnapshot.hasChild("gioiTinh"))
                                && (dataSnapshot.hasChild("phone")))) {
                            String ImageDD = dataSnapshot.child("imgAnhDD").getValue().toString();
                            String UserName = dataSnapshot.child("name").getValue().toString();
                            String Status = dataSnapshot.child("status").getValue().toString();
                            String Phone = dataSnapshot.child("phone").getValue().toString();
                            String GioiTinh = dataSnapshot.child("gioiTinh").getValue().toString();


                            tv_userName.setText(UserName);
                            tv_Status.setText(Status);
                            tv_Phone.setText(Phone);
                            tv_Gioitinh.setText(GioiTinh);
                            Picasso.with(ViewProfileUserActivity.this).load(ImageDD).placeholder(R.drawable.user_profile).into(imgBtnDD);
                        } else if ((dataSnapshot.exists()) && ((dataSnapshot.hasChild("name"))
                                && (dataSnapshot.hasChild("status"))
                                && (dataSnapshot.hasChild("gioiTinh"))
                                && (dataSnapshot.hasChild("phone")))) {

                            String UserName = dataSnapshot.child("name").getValue().toString();
                            String Status = dataSnapshot.child("status").getValue().toString();
                            String Phone = dataSnapshot.child("phone").getValue().toString();
                            String GioiTinh = dataSnapshot.child("gioiTinh").getValue().toString();

                            tv_userName.setText(UserName);
                            tv_Status.setText(Status);
                            tv_Phone.setText(Phone);
                            tv_Gioitinh.setText(GioiTinh);

                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}