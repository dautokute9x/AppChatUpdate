package com.example.chatappdemo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class Dangnhap_Dangky_ViewPager_Adapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> titleArrayList = new ArrayList<>();

    public Dangnhap_Dangky_ViewPager_Adapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragment (Fragment fragment, String title){
        fragmentArrayList.add(fragment);
        titleArrayList.add(title);
    }

    public CharSequence getPagerTitle(int position) {
        return titleArrayList.get(position);
    }
}
