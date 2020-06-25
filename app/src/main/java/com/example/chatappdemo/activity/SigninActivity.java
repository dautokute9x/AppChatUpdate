package com.example.chatappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappdemo.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SigninActivity extends AppCompatActivity {
    int themeIdcurrent;
    String SHARED_PREFS = "codeTheme";
    private FirebaseAuth firebaseAuth;
    private Button login_Button;
    private TextInputLayout login_Email, login_Password;
    private TextView login_forgetPassword;
    private ProgressDialog loadingBar;
    public String email_signin, password_signin;
    private CircleImageView btn_Back, imgGoogle;
    private DatabaseReference databaseReference;


    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z][\\w-]+@([\\w]+\\.[\\w]+|[\\w]+\\.[\\w]{2,}\\.[\\w]{2,})$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$");
    private Boolean emailChecker;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences locationpref = getApplicationContext()
                .getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        themeIdcurrent = locationpref.getInt("themeid",R.style.AppTheme);
        setTheme(themeIdcurrent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        loadingBar = new ProgressDialog(this);

        login_Button = findViewById(R.id.login_button);
        login_Email = findViewById(R.id.login_email);
        login_Password = findViewById(R.id.login_password);
        login_forgetPassword = findViewById(R.id.forget_password_link);
        btn_Back = findViewById(R.id.back_signin);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgGoogle = findViewById(R.id.img_google);
        imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validatePassword()) {
                    return;
                }
                else {
                    email_signin = login_Email.getEditText().getText().toString();
                    password_signin = login_Password.getEditText().getText().toString();
                    loadingBar.setTitle("Log In");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    firebaseAuth.signInWithEmailAndPassword(email_signin, password_signin)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        VerifyEmailAddress();
                                        loadingBar.dismiss();
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(SigninActivity.this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SigninActivity.this, "Connection to Google Sign In false",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            loadingBar.setTitle("Google Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(SigninActivity.this,"Please wait, while we are getting your auth result",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SigninActivity.this,"Can't get auth result",Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String currentUserId = firebaseAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            databaseReference.child(currentUserId).child("device_token")
                                    .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent1 = new Intent(SigninActivity.this, MainActivity.class);
                                        startActivity(intent1);
                                    }
                                }
                            });

                            databaseReference.child("Users").child(currentUserId).setValue("");
                            databaseReference.child("Users").child(currentUserId).child("device_token").setValue(deviceToken);

                            loadingBar.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            Toast.makeText(SigninActivity.this, "Not authenticated: " + message,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void VerifyEmailAddress() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        emailChecker = user.isEmailVerified();
        if (emailChecker) {
            String currentUserId = firebaseAuth.getCurrentUser().getUid();
            String deviceToken = FirebaseInstanceId.getInstance().getToken();

            databaseReference.child(currentUserId).child("device_token")
                    .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent1 = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent1);
                    }
                }
            });

        } else {
            Toast.makeText(SigninActivity.this,"Please verify your accout first!",Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
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