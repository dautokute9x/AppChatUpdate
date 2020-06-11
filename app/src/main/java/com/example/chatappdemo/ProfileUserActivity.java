package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButtonOption;
    private TextInputLayout set_user_name, set_profile_status, set_profile_phone;
    private CircleImageView update_button;
    private String edtUserName, edtStatus, edtPhone, gioitinh, currentUserId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final Pattern PHONE_PATTERN = Pattern.compile("(09|01[2|6|8|9])+([0-9]{8})\\b");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        AnhXa();
    }

    private void AnhXa() {
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
    }

    private void UpdateProfileUser() {
        edtUserName = set_user_name.getEditText().getText().toString();
        edtStatus = set_profile_status.getEditText().getText().toString();
        edtPhone = set_profile_phone.getEditText().getText().toString().trim();
        if (!validateName() | !validateStatus() | !validatePhone() | !validateSex()) {
            return;
        } else {

            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", edtUserName);
            profileMap.put("status", edtStatus);
            profileMap.put("phone", edtPhone);
            profileMap.put("gioiTinh", gioitinh);

            databaseReference.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileUserActivity.this, "Update succelfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileUserActivity.this, SeclecImageActivity.class);
                                startActivity(intent);
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(ProfileUserActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
