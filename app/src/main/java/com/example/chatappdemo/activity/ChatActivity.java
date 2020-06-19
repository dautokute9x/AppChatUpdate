package com.example.chatappdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappdemo.fragment.BottomSheetDialogFragment;
import com.example.chatappdemo.adapter.MessageAdapter;
import com.example.chatappdemo.R;
import com.example.chatappdemo.model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messReceiverId, messReceiverImage, messReceiverName, messSenderId;
    private CircleImageView imgMore, imgProfileFriend, back_user_chat;
    private TextView name_user_chat, userLastSeen;
    private ImageButton btnSendMessage;
    private EditText messageInput;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView userMessageList;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imgProfileFriend = findViewById(R.id.image_user_chat);
        name_user_chat = findViewById(R.id.name_user_chat);
        messReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messReceiverImage = getIntent().getExtras().get("visit_image").toString();
        Picasso.with(ChatActivity.this).load(messReceiverImage)
                .placeholder(R.drawable.user_profile).into(imgProfileFriend);
        name_user_chat.setText(messReceiverName);
        back_user_chat = findViewById(R.id.back_user_chat);
        back_user_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        messageInput = findViewById(R.id.input_message);

        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList = (RecyclerView) findViewById(R.id.messager_list_of_users);


        mAuth = FirebaseAuth.getInstance();
        messSenderId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageAdapter = new MessageAdapter(messagesList,this);


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imgMore = findViewById(R.id.imgMore);
        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        btnSendMessage = findViewById(R.id.img_send_mess);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(messSenderId).child(messReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Nhap tin nhan truoc", Toast.LENGTH_SHORT).show();

        } else {
            String messageSendRef = "Messages/" + messSenderId + "/" + messReceiverId;
            String messageRecvRef = "Messages/" + messReceiverId + "/" + messSenderId;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messSenderId)
                    .child(messReceiverId).push();
            String messagePushID = userMessageKeyRef.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSendRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageRecvRef + "/" + messagePushID, messageTextBody);
            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {

                        userMessageList.setAdapter(messageAdapter);
                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                        messageAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                    messageInput.setText("");
                }
            });


        }
    }
}
