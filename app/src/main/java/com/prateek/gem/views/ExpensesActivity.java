package com.prateek.gem.views;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.R;
import com.prateek.gem.SyncService;
import com.prateek.gem.groups.ComponentsPagerAdapter;
import com.prateek.gem.groups.ExpensesFragment;
import com.prateek.gem.groups.ItemsFragment;
import com.prateek.gem.groups.MainLandingScreen;
import com.prateek.gem.groups.MembersFragment;
import com.prateek.gem.items.ItemsActivity;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.SamplePagerItem;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.CreateExcel;
import com.prateek.gem.utils.LoadingScreen;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.widgets.SlidingTabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.write.WriteException;

public class ExpensesActivity extends MainActivity {

    private ViewPager pager;
    private ComponentsPagerAdapter pagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    public static List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();
    
    Intent itemsIntent,calculateIntent, pieChartIntent;
    SyncSuccessReceiver syncSuccessReceiver;
    IntentFilter syncDataIntentFilter;

    private ExpensesFragment expensesFragment;
    private ItemsFragment itemsFragment;
    private MembersFragment membersFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        itemsIntent = new Intent(this, ItemsActivity.class);
        calculateIntent = new Intent(this, HisabActivity.class);
        pieChartIntent = new Intent(this, PieChartActivity.class);

        if(itemsFragment == null) {
            itemsFragment = new ItemsFragment();
        }

        if(expensesFragment == null) {
            expensesFragment = new ExpensesFragment();
        }

        if(membersFragment == null) {
            membersFragment = new MembersFragment();
        }


        mTabs.add(new SamplePagerItem(AppConstants.EXPENSES, R.color.theme_default_icons, android.R.color.transparent, expensesFragment));
        mTabs.add(new SamplePagerItem(AppConstants.ITEMS, R.color.theme_default_icons, android.R.color.transparent, itemsFragment));
        mTabs.add(new SamplePagerItem(AppConstants.MEMBERS, R.color.theme_default_icons, android.R.color.transparent, membersFragment));

        syncSuccessReceiver = new SyncSuccessReceiver();
        syncDataIntentFilter = new IntentFilter(SyncSuccessReceiver.SUCCESS_RECEIVER);
        syncDataIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);


        pager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ComponentsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(pager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                DebugLogger.message("onpageScrolled"+position);
                if(itemsFragment != null) {
                    itemsFragment.stopActionMode();
                }
            }

            @Override
            public void onPageSelected(int position) {
                DebugLogger.message("onpage Selected"+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
	}

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar(App.getInstance().getCurr_group().getGroupName());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_expenses;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expenses, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setQueryHint("item,amount,expense by");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            /*case R.id.members:

                startActivity(membersIntent);
                return true;*/
            case R.id.items:

                startActivity(itemsIntent);
                return true;
            case R.id.calculate:

                startActivity(calculateIntent);
                return true;

            /*case R.id.mystats:
                startActivity(mystatsIntent);
                System.out.println("in mystats click"+App.getInstance().getCurr_group());

                return true;*/

            case R.id.action_sync:
                performSync();
                return true;

            case R.id.export:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose month");
                final String[] months = Utils.getMonths();
                builder.setItems(months, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int position) {
                        try {
                            handleExport(months[position]);
                        } catch (WriteException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                builder.show();
                break;
            case R.id.action_piechart:
                startActivity(pieChartIntent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void handleExport(String month) throws WriteException, IOException {
        CreateExcel test = new CreateExcel(this);
        test.setParam(month);
        test.setOutputFile(App.getInstance().getCurr_group().getGroupName()+"_"+month+".xls");
        test.write();
    }

    private void performSync() {
        System.out.println(App.getInstance().getCurr_group().getGroupIdServer());
        DBImpl.deleteAllStuff(App.getInstance().getCurr_group().getGroupIdServer(), false);

        //load full data related to group
        LoadingScreen.showLoading(this, "Syncing group");

        Intent syncServiceIntent = new Intent(this,SyncService.class);
        startService(syncServiceIntent);

    }

    public void updateSlidingTabLayout(int color) {
        if(mSlidingTabLayout != null) {
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(color));
        }
    }

    public void hideToolbar() {
        mToolBar.animate().translationY(-mToolBar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    public void showToolbar() {
        mToolBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public void hideInfo() {
        mSlidingTabLayout.animate().translationY(-mSlidingTabLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    public void showInfo() {
        mSlidingTabLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public class SyncSuccessReceiver extends BroadcastReceiver {

        public static final String SUCCESS_RECEIVER = "com.prateek.gem.views.ExpenseActivity.SyncSuccessReceiver.SuccessReceiver";

        @Override
        public void onReceive(Context cxt, Intent receivingIntent) {
            LoadingScreen.dismissProgressDialog();
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(syncSuccessReceiver, syncDataIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(syncSuccessReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabs.clear();
    }
}
