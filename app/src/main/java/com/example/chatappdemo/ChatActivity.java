package com.example.chatappdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String messReceiverId, messReceiverImage, messReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(messReceiverName);

        messReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        //messReceiverImage = getIntent().getExtras().get("visit_user_image").toString();
        messReceiverName = getIntent().getExtras().get("visit_user_name").toString();
    }
}