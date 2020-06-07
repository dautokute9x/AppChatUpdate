package com.example.chatappdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class Dangnhap_Dangky_Activity extends AppCompatActivity {
    public TabLayout tabLayout;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap__dangky_);
        tabLayout = findViewById(R.id.myTabLayout);
        viewPager = findViewById(R.id.myViewPager);
        init();
    }

    private void init() {
        Dangnhap_Dangky_ViewPager_Adapter dangnhap_dangky_viewPager_adapter = new Dangnhap_Dangky_ViewPager_Adapter(getSupportFragmentManager());
        dangnhap_dangky_viewPager_adapter.addFragment(new Fragment_Dangnhap(), "Sign In");
        dangnhap_dangky_viewPager_adapter.addFragment(new Fragment_Dangky(), "Sign Up");
        viewPager.setAdapter(dangnhap_dangky_viewPager_adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Sign In");
        tabLayout.getTabAt(1).setText("Sign Up");
    }
}
