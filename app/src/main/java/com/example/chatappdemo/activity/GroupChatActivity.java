package com.example.chatappdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chatappdemo.R;

public class GroupChatActivity extends AppCompatActivity {
    int themeIdcurrent;
    String SHARED_PREFS = "codeTheme";
    private Toolbar toolbar;
    private ImageButton imgSend;
    private EditText edt_message;
    private ScrollView scrollView;
    private TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences locationpref = getApplicationContext()
                .getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        themeIdcurrent = locationpref.getInt("themeid",R.style.AppTheme);
        setTheme(themeIdcurrent);
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