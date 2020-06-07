package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserID, curent_state, sender_userId;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileSex;
    private Button btnSendMess, btnDeclineRequest;
    private DatabaseReference userRef, requestRef, contactRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserID = getIntent().getExtras().get("visit_userId").toString();
        sender_userId = firebaseAuth.getCurrentUser().getUid();
        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName = findViewById(R.id.visit_username);
        userProfileSex = findViewById(R.id.visit_userGioitinh);
        btnSendMess = findViewById(R.id.btnSend_Mess);
        btnDeclineRequest = findViewById(R.id.btnDecline);
        curent_state = "new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        userRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name_signup").getValue().toString();
                    String userSex = dataSnapshot.child("gioitinh_signup").getValue().toString();

                    Picasso.with(ProfileActivity.this).load(userImage).placeholder(R.drawable.user_profile).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileSex.setText(userSex);

                    ManageRequests();
                } else {
                    String userName = dataSnapshot.child("name_signup").getValue().toString();
                    String userSex = dataSnapshot.child("gioitinh_signup").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileSex.setText(userSex);

                    ManageRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageRequests() {
        requestRef.child(sender_userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserID)) {
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();
                            if (request_type.equals("sent")) {
                                curent_state = "request_sent";
                                btnSendMess.setText("Cancel request");
                            } else if (request_type.equals("received")) {
                                curent_state = "request_received";
                                btnSendMess.setText("Accept Request");
                                btnDeclineRequest.setVisibility(View.VISIBLE);
                                btnDeclineRequest.setEnabled(true);
                                btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelRequest();
                                    }
                                });
                            }
                        } else {
                            contactRef.child(sender_userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiverUserID)) {
                                                curent_state = "friends";
                                                btnSendMess.setText("Remove this contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (!sender_userId.equals(receiverUserID)) {
            btnSendMess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSendMess.setEnabled(false);
                    if (curent_state.equals("new")) {
                        SendRequest();
                    }
                    if (curent_state.equals("request_sent")) {
                        CancelRequest();
                    }
                    if (curent_state.equals("request_received")) {
                        AcceptRequest();
                    }
                    if (curent_state.equals("friends")) {
                        RemoveSpecificContact();
                    }
                }
            });
        } else {
            btnSendMess.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {
        contactRef.child(sender_userId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRef.child(receiverUserID).child(sender_userId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnSendMess.setEnabled(true);
                                                curent_state = "new";
                                                btnSendMess.setText("Send Request");

                                                btnDeclineRequest.setVisibility(View.INVISIBLE);
                                                btnDeclineRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptRequest() {
        contactRef.child(sender_userId).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRef.child(receiverUserID).child(sender_userId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                requestRef.child(sender_userId).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    requestRef.child(receiverUserID).child(sender_userId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    btnSendMess.setEnabled(true);
                                                                                    curent_state = "friends";
                                                                                    btnSendMess.setText("Remove this contact");

                                                                                    btnDeclineRequest.setVisibility(View.INVISIBLE);
                                                                                    btnDeclineRequest.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelRequest() {
        requestRef.child(sender_userId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            requestRef.child(receiverUserID).child(sender_userId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnSendMess.setEnabled(true);
                                                curent_state = "new";
                                                btnSendMess.setText("Send Request");

                                                btnDeclineRequest.setVisibility(View.INVISIBLE);
                                                btnDeclineRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendRequest() {
        requestRef.child(sender_userId).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            requestRef.child(receiverUserID).child(sender_userId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnSendMess.setEnabled(true);
                                                curent_state = "request_sent";
                                                btnSendMess.setText("Cancel request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}