package com.prateek.gem.groups;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.FirstTimeLoadService;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Group;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.BottomItemDecorator;
import com.prateek.gem.utils.DividerItemDecoration;
import com.prateek.gem.utils.LoadingScreen;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.AddExpenseActivity;
import com.prateek.gem.views.ExpensesActivity;
import com.prateek.gem.views.MainActivity;
import com.prateek.gem.widgets.AddFloatingActionButton;

import java.util.ArrayList;

public class MainLandingScreen extends MainActivity {

    private RecyclerView vGroupsList = null;
    private GroupsAdapter mGroupsAdapter = null;

    private RecyclerView.LayoutManager mLayoutManager;
    private AddFloatingActionButton vAddGroupButton;

    private Intent mAddGroupScreenIntent = null;
    private Intent mExpensesScreenIntent = null;
    private Intent mAddExpensesScreenIntent = null;
    private Intent dataLoadingIntent = null;

    private IntentFilter successIntentFilter;
    private MySuccessReceiver successReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar("Groups", R.drawable.ic_group);

        base = this;

        DBImpl.updateGroupTable1();

        mAddGroupScreenIntent = new Intent(MainLandingScreen.this,AddGroupScreen.class);

        vAddGroupButton = (AddFloatingActionButton) findViewById(R.id.vAddGroupButton);
        vGroupsList = (RecyclerView) findViewById(R.id.vGroups);

        vGroupsList.setHasFixedSize(true);
        vGroupsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //vGroupsList.addItemDecoration(new BottomItemDecorator(Utils.dpToPixels(8), Utils.dpToPixels(4)));

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        vGroupsList.setLayoutManager(mLayoutManager);

        // specify an adapter
        mGroupsAdapter = new GroupsAdapter(base);
        mGroupsAdapter.setGroups(new ArrayList<Group>());
        vGroupsList.setAdapter(mGroupsAdapter);

        vAddGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(AppConstants.GROUP_SOURCE, true);
                mAddGroupScreenIntent.putExtras(bundle);
                startActivity(mAddGroupScreenIntent);
            }
        });

        AppDataManager.setUser();
        DebugLogger.message(AppDataManager.getUser());

        successIntentFilter = new IntentFilter(AppConstants.SUCCESS_RECEIVER);
        successIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        successReceiver = new MySuccessReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(successReceiver, successIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(successReceiver);
    }

    private void doFirstTimeLoad() {
        if(Utils.isConnected(base)){
            LoadingScreen.showLoading(base, "Loading data, if you are existing user");
            dataLoadingIntent = new Intent(MainLandingScreen.this, FirstTimeLoadService.class);
            startService(dataLoadingIntent);
        }else{
            Utils.showToast(this, getString(R.string.nonetwork));
        }

    }

    private boolean isFirstTimeLoadDone() {
        //Return true if first time load key has true value
        return AppSharedPreference.getPreferenceBoolean(AppConstants.ONETIME_LOAD);

    }

    public void onItemSelected(Group group, int position, View transitionView) {
        //super.onItemSelected(group);
        if(position == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.GROUP_SOURCE, false);
            bundle.putParcelable(AppConstants.GROUP, group);
            AppDataManager.setCurrentGroup(group);
            App.getInstance().setCurr_group(group);
            if(group.getTotalOfExpense() > 0) {
                mExpensesScreenIntent = new Intent(MainLandingScreen.this, ExpensesActivity.class);
                mExpensesScreenIntent.putExtras(bundle);
                startActivity(mExpensesScreenIntent);
            } else {
                mAddExpensesScreenIntent = new Intent(MainLandingScreen.this, AddExpenseActivity.class);
                mAddExpensesScreenIntent.putExtras(bundle);
                startActivity(mAddExpensesScreenIntent);
            }

        } else {
            DebugLogger.message("onItemSelected" + group);
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.GROUP_SOURCE, false);
            bundle.putParcelable(AppConstants.GROUP, group);
            //AddGroupScreen.launch(MainLandingScreen.this, transitionView, bundle);
            /*mAddGroupScreenIntent.putExtras(bundle);
            startActivity(mAddGroupScreenIntent);*/
            mExpensesScreenIntent = new Intent(MainLandingScreen.this, ExpensesActivity.class);
            mExpensesScreenIntent.putExtras(bundle);
            startActivity(mExpensesScreenIntent);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_landing_screen;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* One time load code */
        if(!isFirstTimeLoadDone()){
            //Load first time setup if preferences doesnot have
            doFirstTimeLoad();
        }else{
            updateGroups();
        }
    }

    public class MySuccessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LoadingScreen.dismissProgressDialog();
            updateUI();
            if(intent.getBooleanExtra("done", false)){
                Utils.showToast(context, "Loaded successfully");
            }
        }

    }

    private void updateUI() {
        AppSharedPreference.storePreferences(AppConstants.ONETIME_LOAD, true);
        updateGroups();
    }

    private void updateGroups() {
        if(mGroupsAdapter == null) {
            mGroupsAdapter = new GroupsAdapter(this);
        }

        mGroupsAdapter.setGroups(DBImpl.getGroups());
        mGroupsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBImpl.closeDatabase();
    }
}
