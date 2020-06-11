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

public class SeclecImageActivity extends AppCompatActivity {
    private ImageButton imgBtnBG;
    private CircleImageView imgBtnCamBG, imgBtnDD, imgBtnCamDD;
    private TextView tv_userName, tv_Phone, tv_Status, tv_Gioitinh, tv_Done;
    private static final int GalleryPick = 1;
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
        setContentView(R.layout.activity_seclec_image);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        AnhXa();
    }

    private void AnhXa() {
        imgBtnBG = findViewById(R.id.imgBtnBG);
        imgBtnBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent imgAnhBGIntent = new Intent();
//                imgAnhBGIntent.setAction(Intent.ACTION_GET_CONTENT);
//                imgAnhBGIntent.setType("imageBG/*");
//                startActivityForResult(imgAnhBGIntent, GalleryPick2);
            }
        });
        imgBtnCamBG = findViewById(R.id.imgBtnCamBG);
        imgBtnCamBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent imgAnhBGIntent = new Intent();
//                imgAnhBGIntent.setAction(Intent.ACTION_GET_CONTENT);
//                imgAnhBGIntent.setType("imageBG/*");
//                startActivityForResult(imgAnhBGIntent, GalleryPick2);
            }
        });
        imgBtnDD = findViewById(R.id.imgBtnDD);
        imgBtnDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgAnhDDIntent = new Intent();
                imgAnhDDIntent.setAction(Intent.ACTION_GET_CONTENT);
                imgAnhDDIntent.setType("imageDD/*");
                startActivityForResult(imgAnhDDIntent, GalleryPick);
            }
        });
        imgBtnCamDD = findViewById(R.id.imgBtnCamDD);
//        imgBtnCamDD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent imgAnhDDIntent = new Intent();
//                imgAnhDDIntent.setAction(Intent.ACTION_GET_CONTENT);
//                imgAnhDDIntent.setType("imageDD/*");
//                startActivityForResult(imgAnhDDIntent, GalleryPick);
//            }
//        });
        tv_userName = findViewById(R.id.tv_userName);
        tv_Phone = findViewById(R.id.tv_Phone);
        tv_Status = findViewById(R.id.tv_Status);
        tv_Gioitinh = findViewById(R.id.tv_Gioitinh);
        progressDialog = new ProgressDialog(this);
        tv_Done = findViewById(R.id.tv_Done);
        tv_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackMain = new Intent(SeclecImageActivity.this, MainActivity.class);
                startActivity(intentBackMain);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        DisplayProfile();
        super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
                    Uri imgUri = data.getData();
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this);
                }
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        progressDialog.setTitle("Set profile image");
                        progressDialog.setMessage("Please Wait!");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        Uri resultUri = result.getUri();
                        StorageReference filePath = storageReference.child(currentUserId + ".jpg");
                        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SeclecImageActivity.this, "Update image succelfully", Toast.LENGTH_SHORT).show();
                                    final String downloadUrl = task.getResult().getDownloadUrl().toString();
                                    databaseReference.child("Users").child(currentUserId).child("imgAnhDD")
                                            .setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SeclecImageActivity.this, "Save image in database", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        String message = task.getException().toString();
                                                        Toast.makeText(SeclecImageActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    progressDialog.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(SeclecImageActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

    }

    private void DisplayProfile() {
        databaseReference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") &&
                                (dataSnapshot.hasChild("status")) &&
                                (dataSnapshot.hasChild("gioiTinh")) &&
                                (dataSnapshot.hasChild("phone")) &&
                                (dataSnapshot.hasChild("imgAnhDD")))) {
                            String UserName = dataSnapshot.child("name").getValue().toString();
                            String Status = dataSnapshot.child("status").getValue().toString();
                            String Phone = dataSnapshot.child("phone").getValue().toString();
                            String GioiTinh = dataSnapshot.child("gioiTinh").getValue().toString();
                            String ImageDD = dataSnapshot.child("imgAnhDD").getValue().toString();



                            tv_userName.setText(UserName);
                            tv_Status.setText(Status);
                            tv_Phone.setText(Phone);
                            tv_Gioitinh.setText(GioiTinh);
                            Picasso.with(SeclecImageActivity.this).load(ImageDD).placeholder(R.drawable.user_profile).into(imgBtnDD);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}