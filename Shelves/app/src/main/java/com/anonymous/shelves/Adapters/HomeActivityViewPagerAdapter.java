package com.anonymous.shelves.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ANONYMOUS on 13-Mar-18.
 */

public class HomeActivityViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){

        this.fragments.add(fragment);
        this.tabTitles.add(title);

    }

    public HomeActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.tabTitles.get(position);
    }
}
