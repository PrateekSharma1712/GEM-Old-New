package com.prateek.gem.groups;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.prateek.gem.AppConstants;

/**
 * Created by prateek on 4/5/15.
 */
public class ComponentsPagerAdapter extends FragmentPagerAdapter {

    public ComponentsPagerAdapter(FragmentManager fm){
        super(fm);

    }
    @Override
    public Fragment getItem(int position) {
        GroupComponentsCreator creator = new GroupComponentsCreator();
        return creator.getFragment(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return AppConstants.ITEMS;
            case 0:
            default:
                return AppConstants.EXPENSES;
        }
    }
}
