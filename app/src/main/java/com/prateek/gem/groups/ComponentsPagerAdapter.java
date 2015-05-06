package com.prateek.gem.groups;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.prateek.gem.AppConstants;
import com.prateek.gem.views.ExpensesActivity;

/**
 * Created by prateek on 4/5/15.
 */
public class ComponentsPagerAdapter extends FragmentPagerAdapter {

    public ComponentsPagerAdapter(FragmentManager fm){
        super(fm);

    }
    @Override
    public Fragment getItem(int position) {
        return ExpensesActivity.mTabs.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return ExpensesActivity.mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
       return ExpensesActivity.mTabs.get(position).getTitle();
    }
}
