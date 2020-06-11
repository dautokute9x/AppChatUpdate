package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private Toolbar toolBarSignup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button register_button;
    private TextInputLayout register_Email, register_Password, register_ConfirmPassword;
    private ProgressDialog loadingBar;
    private String email_signup, password_signup, rePassword_signup;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z][\\w-]+@([\\w]+\\.[\\w]+|[\\w]+\\.[\\w]{2,}\\.[\\w]{2,})$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        toolBarSignup = findViewById(R.id.toolBarSignup);
        setSupportActionBar(toolBarSignup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(SignupActivity.this);
        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        register_Email = findViewById(R.id.register_email);
        register_Password = findViewById(R.id.register_password);
        register_ConfirmPassword = findViewById(R.id.register_confirm_password);
    }

    public void registerUser(){
        email_signup = register_Email.getEditText().getText().toString();
        password_signup = register_Password.getEditText().getText().toString();
        rePassword_signup = register_ConfirmPassword.getEditText().getText().toString();

        if (!validateEmail() | !validatePassword() | !validateRePassword()) {
            return;
        } else {
            loadingBar.setTitle("Đang tạo tài khoản");
            loadingBar.setMessage("Đợi chút...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            firebaseAuth.createUserWithEmailAndPassword(email_signup, password_signup)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                databaseReference.child("Users").child(currentUserId).setValue("");
                                Toast.makeText(SignupActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent1 = new Intent(SignupActivity.this,MainActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);
                                finish();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SignupActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private boolean validateEmail() {
        email_signup = register_Email.getEditText().getText().toString().trim();
        if (email_signup.isEmpty()) {
            register_Email.setError("Bạn không được để trống!");
            return false;
        } else if (!EMAIL_PATTERN.matcher(email_signup).matches()) {
            register_Email.setError("Vui lòng nhập đúng định dạng email!");
            return false;
        } else {
            register_Email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        password_signup = register_Password.getEditText().getText().toString().trim();
        if (password_signup.isEmpty()) {
            register_Password.setError("Bạn không được để trống!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password_signup).matches()) {
            register_Password.setError("Mật khẩu cần có 1 chữ viết hoa và dài hơn 6 kí tự gồm chữ và số!");
            return false;
        } else {
            register_Password.setError(null);
            return true;
        }
    }

    private boolean validateRePassword() {
        rePassword_signup = register_ConfirmPassword.getEditText().getText().toString().trim();
        password_signup = register_Password.getEditText().getText().toString().trim();
        if (rePassword_signup.isEmpty()) {
            register_ConfirmPassword.setError("Bạn không được để trống!");
            return false;
        } else if (!rePassword_signup.equals(password_signup)) {
            register_ConfirmPassword.setError("Mật khẩu nhập lại khác mật khẩu ở trên!");
            return false;
        } else {
            register_ConfirmPassword.setError(null);
            return true;
        }
    }
}