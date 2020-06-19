package com.example.chatappdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileUserActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private TextView tv_Cancel;
    private RadioButton radioButtonOption;
    private TextInputLayout set_user_name, set_profile_status, set_profile_phone;
    private CircleImageView update_button, imgBtnDD, imgBtnCamDD;
    private String edtUserName, edtStatus, edtPhone, gioitinh, currentUserId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final int GalleryPick = 1;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private static final Pattern PHONE_PATTERN = Pattern.compile("(09|01[2|6|8|9])+([0-9]{8})\\b");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_user);
        tv_Cancel = findViewById(R.id.tv_Cancel);
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        AnhXa();
    }

    private void AnhXa() {
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        progressDialog = new ProgressDialog(this);
        imgBtnDD = findViewById(R.id.imgBtnDD);
        imgBtnDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePhoto();
            }
        });
        imgBtnCamDD = findViewById(R.id.imgBtnCamDD);
        imgBtnCamDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePhoto();
            }
        });
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonOption = radioGroup.findViewById(checkedId);
                switch (checkedId) {
                    case R.id.male_checkbox:
                        gioitinh = radioButtonOption.getText().toString();
                        break;
                    case R.id.female_checkbox:
                        gioitinh = radioButtonOption.getText().toString();
                        break;
                    default:
                }
            }
        });
        set_user_name = findViewById(R.id.set_user_name);
        set_profile_status = findViewById(R.id.set_profile_status);
        set_profile_phone = findViewById(R.id.set_profile_phone);
        update_button = findViewById(R.id.update_button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileUser();
                set_user_name.getEditText().setText("");
                set_profile_status.getEditText().setText("");
                set_profile_phone.getEditText().setText("");
            }
        });
        DisplayProfile();
    }

    private void DisplayProfile() {
        databaseReference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("imgAnhDD"))) {
                            String ImageDD = dataSnapshot.child("imgAnhDD").getValue().toString();
                            Picasso.with(UpdateProfileUserActivity.this).load(ImageDD).placeholder(R.drawable.user_profile).into(imgBtnDD);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void ChoosePhoto() {
        Intent imgAnhDDIntent = new Intent();
        imgAnhDDIntent.setAction(Intent.ACTION_GET_CONTENT);
        imgAnhDDIntent.setType("imageDD/*");
        startActivityForResult(imgAnhDDIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                            Toast.makeText(UpdateProfileUserActivity.this, "Update image succelfully", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            databaseReference.child("Users").child(currentUserId).child("imgAnhDD")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(UpdateProfileUserActivity.this, "Save image in database", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                String message = task.getException().toString();
                                                Toast.makeText(UpdateProfileUserActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(UpdateProfileUserActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void UpdateProfileUser() {
        edtUserName = set_user_name.getEditText().getText().toString();
        edtStatus = set_profile_status.getEditText().getText().toString();
        edtPhone = set_profile_phone.getEditText().getText().toString().trim();
        if (!validateName() | !validateStatus() | !validatePhone() | !validateSex()) {
            return;
        } else {

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", edtUserName);
            profileMap.put("status", edtStatus);
            profileMap.put("phone", edtPhone);
            profileMap.put("gioiTinh", gioitinh);

            databaseReference.child("Users").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdateProfileUserActivity.this, "Update succelfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateProfileUserActivity.this, ViewProfileUserActivity.class);
                                startActivity(intent);
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(UpdateProfileUserActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean validateName() {
        edtUserName = set_user_name.getEditText().getText().toString().trim();
        if (edtUserName.isEmpty()) {
            set_user_name.setError("Hay dien ten ban muon hien thi!");
            return false;
        } else {
            set_user_name.setError(null);
            return true;
        }
    }

    private boolean validateStatus() {
        edtStatus = set_profile_status.getEditText().getText().toString().trim();
        if (edtStatus.isEmpty()) {
            set_profile_status.setError("Hay them loi gioi thieu ve ban!");
            return false;
        } else {
            set_profile_status.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        edtPhone = set_profile_phone.getEditText().getText().toString().trim();
        if (edtPhone.isEmpty()) {
            set_profile_phone.setError("Bạn không được để trống!");
            return false;
        } else if (!PHONE_PATTERN.matcher(edtPhone).matches()) {
            set_profile_phone.setError("Vui lòng nhập đúng định dạng số điện thoại!");
            return false;
        } else {
            set_profile_phone.setError(null);
            return true;
        }
    }

    private boolean validateSex() {
        int isSelecter = radioGroup.getCheckedRadioButtonId();
        if (isSelecter == -1) {
            Toast.makeText(this, "Bạn chưa chọn giới tính!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
