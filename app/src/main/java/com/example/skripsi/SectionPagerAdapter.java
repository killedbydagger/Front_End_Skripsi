package com.example.skripsi;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTabList = new ArrayList<>();

    public SectionPagerAdapter(FragmentManager fm){super(fm);}

    public void addFragment(Fragment fragment, String tabMenu){
        mFragmentList.add(fragment);
        mFragmentTabList.add(tabMenu);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTabList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
