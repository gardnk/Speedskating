package com.example.gard.speedskating;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class SkaterTabAdapter extends FragmentStatePagerAdapter {

    private List<String> pageTitle;
    private ArrayList<Integer> times;
    private Skater skater;

    public SkaterTabAdapter(FragmentManager fm, Skater skater, ArrayList<Integer> times, ArrayList<String> titles) {
        super(fm);
        pageTitle = titles;
        this.times = times;
        this.skater = skater;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new SkaterFragment();
        Bundle args = new Bundle();
        args.putString("name", skater.getName());
        args.putInt("time", times.get(position));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return pageTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitle.get(position);
    }
}
