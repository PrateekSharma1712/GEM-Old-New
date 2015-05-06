package com.prateek.gem.model;

import android.support.v4.app.Fragment;

/**
 * Created by prateek on 5/5/15.
 */
public class SamplePagerItem {

    private final CharSequence mTitle;
    private final int mIndicatorColor;
    private final int mDividerColor;

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    private Fragment fragment;

    public SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor, Fragment fragment) {
        mTitle = title;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
        this.fragment = fragment;
    }

    /**
     * @return the title which represents this tab. In this sample this is used directly by
     * {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
     */
    public CharSequence getTitle() {
        return mTitle;
    }

    /**
     * @return the color to be used for indicator on the {@link com.prateek.gem.widgets.SlidingTabLayout}
     */
    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    /**
     * @return the color to be used for right divider on the {@link com.prateek.gem.widgets.SlidingTabLayout}
     */
    public int getDividerColor() {
        return mDividerColor;
    }
}
