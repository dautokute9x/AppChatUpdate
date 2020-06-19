package com.example.chatappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private Button login_Button;
   private TextInputLayout login_Email, login_Password;
    private TextView login_forgetPassword;
    private Toolbar toolBarSignin;
    private ProgressDialog loadingBar;
    public String email_signin, password_signin;

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
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        login_Button = findViewById(R.id.login_button);
        login_Email = findViewById(R.id.login_email);
        login_Password = findViewById(R.id.login_password);
        login_forgetPassword = findViewById(R.id.forget_password_link);


        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validatePassword()) {
                    return;
                }
                else {
                    email_signin = login_Email.getEditText().getText().toString();
                    password_signin = login_Password.getEditText().getText().toString();
                    loadingBar.setTitle("Đăng nhập");
                    loadingBar.setMessage("Đợi chút...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    firebaseAuth.signInWithEmailAndPassword(email_signin, password_signin)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SigninActivity.this, "Đăng nhập thành công...",Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                        Intent intent1 = new Intent(SigninActivity.this, MainActivity.class);
                                        startActivity(intent1);
                                    }
                                    else {
                                        String message = task.getException().toString();
                                        Toast.makeText(SigninActivity.this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }
        });

        login_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_forget = new Intent(SigninActivity.this, ResetPasswordActivity.class);
                startActivity(intent_forget);
            }
        });
    }

    private boolean validateEmail() {
        email_signin = login_Email.getEditText().getText().toString().trim();
        if (email_signin.isEmpty()) {
            login_Email.setError("Bạn không được để trống!");
            return false;
        } else if (!EMAIL_PATTERN.matcher(email_signin).matches()) {
            login_Email.setError("Vui lòng nhập đúng định dạng email!");
            return false;
        } else {
            login_Email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        password_signin = login_Password.getEditText().getText().toString().trim();
        if (password_signin.isEmpty()) {
            login_Password.setError("Bạn không được để trống!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password_signin).matches()) {
            login_Password.setError("Mật khẩu cần có 1 chữ viết hoa và dài hơn 6 kí tự gồm chữ và số!");
            return false;
        } else {
            login_Password.setError(null);
            return true;
        }
    }
}