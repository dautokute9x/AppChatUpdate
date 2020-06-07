package com.example.chatappdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Fragment_Dangnhap extends Fragment {
    private FirebaseAuth firebaseAuth;

    private Button login_Button;
    private TextInputLayout login_Email, login_Password;
    TextView login_forgetPassword;
    private ProgressDialog loadingBar;
    public String email_signin, password_signin;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z][\\w-]+@([\\w]+\\.[\\w]+|[\\w]+\\.[\\w]{2,}\\.[\\w]{2,})$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dangnhap, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(getActivity());

        login_Button = view.findViewById(R.id.login_button);
        login_Email = view.findViewById(R.id.login_email);
        login_Password = view.findViewById(R.id.login_password);
        login_forgetPassword = view.findViewById(R.id.forget_password_link);

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
                                        Toast.makeText(getActivity(), "Đăng nhập thành công...",Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                        Intent intent_main = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent_main);
                                    }
                                    else {
                                        String message = task.getException().toString();
                                        Toast.makeText(getActivity(), "Lỗi: " + message, Toast.LENGTH_LONG).show();
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
                Intent intent_forget = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent_forget);
            }
        });
        return view;
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
