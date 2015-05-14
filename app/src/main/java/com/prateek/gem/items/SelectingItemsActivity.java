package com.prateek.gem.items;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.OnModeConfirmListener;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.LoadingScreen;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.MainActivity;
import com.prateek.gem.widgets.AddFloatingActionButton;
import com.prateek.gem.widgets.ConfirmationDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 9/2/15.
 */
public class SelectingItemsActivity extends MainActivity implements RecyclerView.OnItemTouchListener, View.OnClickListener, OnModeConfirmListener{

    private RecyclerView vItemsList = null;
    private RecyclerView vCategoriesList = null;
    private TextView vEmptyView = null;
    private TextView vStatusText = null;
    private EditText searchItems = null;
    private View hline = null;

    private LinearLayoutManager mCategoryLayoutManager;
    private RecyclerView.LayoutManager mItemLayoutManager;
    private AddFloatingActionButton vAddItemButton;

    private CategoriesAdapter mCategoriesAdapter = null;
    private ItemsAdapter mItemsAdapter = null;
    private GestureDetectorCompat gestureDetector = null;
    int selectedCategoryIndex = 0;

    ItemsReceiver itemsReceiver;
    IntentFilter itemSuccessFilter;

    private ActionMode mSelectionMode = null;

    private List<Integer> itemIdsSelected;
    String itemNamesString = "";
    private boolean isActionButtonHidden = false;
    private String alreadySelectedItems = null;
    private Context context = null;
    private LinearLayout.LayoutParams categoryParams = null;
    private LinearLayout.LayoutParams itemsParams = null;
    private RelativeLayout itemsContainer = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();
        if(intent != null) {
            alreadySelectedItems = intent.getStringExtra(AppConstants.SELECTED_ITEMS);
        }

        vAddItemButton = (AddFloatingActionButton) findViewById(R.id.vAddItemsButton);
        vAddItemButton.setOnClickListener(this);
        vItemsList = (RecyclerView) findViewById(R.id.vItems);
        vCategoriesList = (RecyclerView) findViewById(R.id.vCategories);
        itemsContainer = (RelativeLayout) findViewById(R.id.itemsContainer);
        vEmptyView = (TextView) findViewById(android.R.id.text1);
        vStatusText = (TextView) findViewById(R.id.vStatusText);
        searchItems = (EditText) findViewById(R.id.searchItems);
        hline = findViewById(R.id.hLine);
        searchItems.setVisibility(View.VISIBLE);

        vStatusText.setOnClickListener(this);

        vItemsList.setHasFixedSize(true);
        vCategoriesList.setHasFixedSize(true);

        // use a linear layout manager
        mItemLayoutManager = new LinearLayoutManager(this);
        mCategoryLayoutManager = new LinearLayoutManager(this);
        vItemsList.setLayoutManager(mItemLayoutManager);
        vItemsList.setItemAnimator(new DefaultItemAnimator());
        vCategoriesList.setLayoutManager(mCategoryLayoutManager);
        vCategoriesList.setItemAnimator(new DefaultItemAnimator());

        mCategoriesAdapter = new CategoriesAdapter(this);
        vCategoriesList.setAdapter(mCategoriesAdapter);
        vCategoriesList.addOnItemTouchListener(this);
        gestureDetector =
                new GestureDetectorCompat(this, new RecyclerViewDemoOnGestureListener());

        itemsReceiver = new ItemsReceiver();
        itemSuccessFilter = new IntentFilter(ItemsReceiver.ITEMSUCCESSRECEIVER);
        itemSuccessFilter.addCategory(Intent.CATEGORY_DEFAULT);

        mCategoriesAdapter.toggleSelection(selectedCategoryIndex);
        loadItems(mCategoriesAdapter.getSelectedCategory());

        if(!alreadySelectedItems.isEmpty()) {
            runActionMode();
        }

        categoryParams = (LinearLayout.LayoutParams) vCategoriesList.getLayoutParams();
        itemsParams = (LinearLayout.LayoutParams) itemsContainer.getLayoutParams();

        vItemsList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy < 0) {
                    if(isActionButtonHidden) {
                        Utils.toggleVisibility(vAddItemButton, true);
                    }
                    isActionButtonHidden = false;
                } else if(dy > 5) {
                    if(!isActionButtonHidden) {
                        Utils.toggleVisibility(vAddItemButton, true);
                    }
                    isActionButtonHidden = true;
                }
            }
        });

        searchItems.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchItems.getRight() - searchItems.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        searchItems.setText("");

                        return true;
                    }
                }
                return false;
            }
        });

        searchItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DebugLogger.message("search string .."+s.toString());
                DebugLogger.message(DBImpl.getItemsLike(s.toString()));
                ArrayList<Items> items = DBImpl.getItemsLike(s.toString());
                if(items.size() > 0) {
                    if (mItemsAdapter == null) {
                        mItemsAdapter = new ItemsAdapter(this);
                        vItemsList.setAdapter(mItemsAdapter);
                        mItemsAdapter.setSelectedPositions(alreadySelectedItems);
                    }
                    mItemsAdapter.setDataset(items);
                    vItemsList.setVisibility(View.VISIBLE);
                    vEmptyView.setVisibility(View.GONE);
                } else {
                    vItemsList.setVisibility(View.GONE);
                    vEmptyView.setVisibility(View.VISIBLE);
                    vEmptyView.setText("No items matching '"+s.toString()+"'");

                }

                if(s.toString().isEmpty()) {
                    categoryParams.weight = 0.4f;
                    vCategoriesList.setLayoutParams(categoryParams);
                    itemsParams.weight = 0.6f;
                    itemsContainer.setLayoutParams(itemsParams);
                } else {
                    categoryParams.weight = 0;
                    vCategoriesList.setLayoutParams(categoryParams);
                    itemsParams.weight = 1;
                    itemsContainer.setLayoutParams(itemsParams);
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_member_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.saveMembers) {
            generateSelectedItemsString();
            if(itemNamesString != null && !itemNamesString.isEmpty())
                Utils.openConfirmationDialog(AppDataManager.currentScreen,itemNamesString, true, this);
            else
                modeConfirmed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Select Items", R.drawable.ic_group);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(itemsReceiver, itemSuccessFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(itemsReceiver);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(vAddItemButton)) {
            openDialog();
        } else if(v.equals(vStatusText)) {
            // open list showing item names
            generateSelectedItemsString();
            Utils.openConfirmationDialog(AppDataManager.currentScreen, itemNamesString, false, this);
        }
    }

    private void openDialog() {
        ConfirmationDialog mcd = new ConfirmationDialog();
        mcd.setOnModeConfirmListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, getString(R.string.addGroup));
        bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.ITEM_ADD);
        bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_cancel));
        bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_add));
        bundle.putInt(ConfirmationDialog.SELECTED_CATEGORY, selectedCategoryIndex);
        mcd.setArguments(bundle);
        mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
    }


    private ActionMode.Callback mSelectModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate( R.menu.add_member_menu, menu );
            MenuItem item = menu.findItem(R.id.menu_search);
            item.setVisible(false);
            //Utils.toggleVisibility(vAddItemButton, true);
            vStatusText.setVisibility(View.VISIBLE);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mItemsAdapter.resetSelectedPositions();
            actionMode.finish();
            mSelectionMode = null;
            if(vAddItemButton.getVisibility() == View.GONE)
                Utils.toggleVisibility(vAddItemButton, true);

            vStatusText.setVisibility(View.GONE);
            mItemsAdapter.notifyDataSetChanged();
            //Utils.showToast(base, "You have cleared selection");
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            int totalSelected = mItemsAdapter.getSelectedCount();
            if(totalSelected > 0) {
                actionMode.setTitle(totalSelected + " selected");
            } else {
                actionMode.finish();
                mSelectionMode = null;
                //Utils.toggleVisibility(vAddItemButton, true);
                vStatusText.setVisibility(View.GONE);
            }

            DebugLogger.message("getSelectedPositions" + mItemsAdapter.getSelectedPositions());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.saveMembers) {
                onOptionsItemSelected(menuItem);
            }
            return true;

        }

    };

    private void generateSelectedItemsString() {
        itemIdsSelected = mItemsAdapter.getItemIds();
        DebugLogger.message("to be selected" + itemIdsSelected);
        itemNamesString = "";
        for(int i = 0;i< itemIdsSelected.size();i++) {
            itemNamesString += DBImpl.getItem(itemIdsSelected.get(i)).getItemName() + ", ";
        }

        if(itemNamesString.length() > 2) {
            itemNamesString = itemNamesString.substring(0, itemNamesString.length() -2);
        }
    }



    public void runActionMode() {
        if(mSelectionMode == null) {
            mSelectionMode = this.startSupportActionMode(mSelectModeCallback);
        } else {
            mSelectModeCallback.onCreateActionMode(mSelectionMode, mSelectionMode.getMenu());
        }
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = vCategoriesList.findChildViewUnder(e.getX(), e.getY());

            int idx = vCategoriesList.getChildPosition(view);

            mCategoriesAdapter.toggleSelection(idx);
            selectedCategoryIndex = idx;
            loadItems(mCategoriesAdapter.getSelectedCategory());
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {

        }
    }

    private void loadItems(String category) {
        DebugLogger.message("category" + category);
        ArrayList<Items> items = DBImpl.getItems(AppDataManager.getCurrentGroup().getGroupIdServer(), category);
        if(items.size() > 0) {
            if(mItemsAdapter == null) {
                mItemsAdapter = new ItemsAdapter(this);
                vItemsList.setAdapter(mItemsAdapter);
                mItemsAdapter.setSelectedPositions(alreadySelectedItems);
            }
            mItemsAdapter.setDataset(items);
            vItemsList.setVisibility(View.VISIBLE);
            vEmptyView.setVisibility(View.GONE);
        } else {
            vItemsList.setVisibility(View.GONE);
            vEmptyView.setVisibility(View.VISIBLE);
            vEmptyView.setText(getString(R.string.no, "items in " + category));
        }
    }

    @Override
    public void onItemsAdded(String category, String item) {
        DebugLogger.message("category" + category);
        DebugLogger.message("item" + item);
        ContentValues cv = new ContentValues();
        cv.put(DB.TItems.ITEM_NAME, item);
        cv.put(DB.TItems.GROUP_FK, AppDataManager.getCurrentGroup().getGroupIdServer());
        cv.put(DB.TItems.CATEGORY, category);
        cv.put(DB.TItems.ITEM_ID_SERVER, 0);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(DB.TItems.ITEM_NAME, item));
        list.add(new BasicNameValuePair(DB.TItems.GROUP_FK, String.valueOf(AppDataManager.getCurrentGroup().getGroupIdServer())));
        list.add(new BasicNameValuePair(DB.TItems.CATEGORY, category));
        list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.ADD_ITEM));
        LoadingScreen.showLoading(this, "Adding "+item);
        if(Utils.isConnected(this)) {
            LoadingScreen.showLoading(this, "Adding " + item);
            FullFlowService.ServiceAddItem(this, AppConstants.NOT_ADDITEM, list, cv);
        } else {
            Utils.showToast(this, "Adding item requires internet connection");
        }
    }

    @Override
    public void modeConfirmed() {
        DebugLogger.message("Confirmed");
        DebugLogger.message(itemNamesString);
        DebugLogger.message("itemIdsSelected.." + itemIdsSelected.size());
        Intent intent = new Intent();
        intent.putExtra("items", itemNamesString);
        intent.putExtra("itemCount", ((itemNamesString.indexOf(",") > 0) ? itemNamesString.split(",").length : 1));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public class ItemsReceiver extends BroadcastReceiver {

        public static final String ITEMSUCCESSRECEIVER = "com.prateek.gem.views.ItemsActivity.ITEMSUCCESSRECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            LoadingScreen.dismissProgressDialog();
            int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
            boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
            switch (notId) {
                case AppConstants.NOT_ADDITEM:
                    if(result){
                        Utils.showToast(context, "Added Succesfully");
                        loadItems(mCategoriesAdapter.getSelectedCategory());
                    }else{
                        Utils.showToast(context, "Cannot add, Please try after some time");
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        /*if(mItemsAdapter != null) {
            if (mItemsAdapter.getSelectedCount() <= 0) {
                Intent intent = new Intent();
                intent.putExtra("items", "");
                setResult(Activity.RESULT_OK, intent);
                //finish();
            } else {
                DebugLogger.message("selected" + mItemsAdapter.getSelectedCount());
            }
        }*/
    }
}
