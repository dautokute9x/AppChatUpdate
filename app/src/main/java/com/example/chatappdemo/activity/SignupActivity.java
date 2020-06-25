package com.example.chatappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatappdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    int themeIdcurrent;
    String SHARED_PREFS = "codeTheme";
    private CircleImageView btn_Back;
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
        SharedPreferences locationpref = getApplicationContext()
                .getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        themeIdcurrent = locationpref.getInt("themeid",R.style.AppTheme);
        setTheme(themeIdcurrent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_Back = findViewById(R.id.back_signup);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



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
            loadingBar.setTitle("Creating account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            firebaseAuth.createUserWithEmailAndPassword(email_signup, password_signup)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                databaseReference.child("Users").child(currentUserId).setValue("");
                                databaseReference.child("Users").child(currentUserId).child("device_token").setValue(deviceToken);
                                SendEmailVerificationMessage();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SignupActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
    private void SendEmailVerificationMessage(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                        builder.setTitle("Notification").setMessage("We have sent an email to you. Please check your email!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });
                        builder.show();
                        firebaseAuth.signOut();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(SignupActivity.this, "Error: " + error,Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
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