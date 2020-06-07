package com.example.chatappdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imgSend;
    private EditText edt_message;
    private ScrollView scrollView;
    private TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        AnhXa();
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolBar);
        imgSend = findViewById(R.id.imgBtnSend);
        edt_message = findViewById(R.id.edt_group_message);
        tv_message = findViewById(R.id.tv_groupChat);
        scrollView = findViewById(R.id.scroll_view);
    }
}