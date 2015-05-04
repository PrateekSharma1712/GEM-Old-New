package com.prateek.gem.groups;

import android.support.v4.app.Fragment;

import com.prateek.gem.AppConstants;

/**
 * Created by prateek on 4/5/15.
 */
public class GroupComponentsCreator {

    private ExpensesFragment expensesFragment;
    private ItemsFragment itemsFragment;

    public Fragment getFragment(int position) {
        switch (position) {
            case 1:
                if(itemsFragment == null) {
                    itemsFragment = new ItemsFragment();
                }
                return itemsFragment;
            case 0:
            default:
                if(expensesFragment == null) {
                    expensesFragment = new ExpensesFragment();
                }
                return expensesFragment;
        }

    }
}
