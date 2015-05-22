package com.prateek.gem.groups;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.OnModeConfirmListener;
import com.prateek.gem.R;
import com.prateek.gem.items.CategoriesAdapter;
import com.prateek.gem.items.ItemsAdapter;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.HidingScrollListener;
import com.prateek.gem.utils.LoadingScreen;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.ExpensesActivity;
import com.prateek.gem.widgets.AddFloatingActionButton;
import com.prateek.gem.widgets.ConfirmationDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 4/5/15.
 */
public class ItemsFragment extends Fragment implements RecyclerView.OnItemTouchListener, View.OnClickListener, OnModeConfirmListener {

    private RecyclerView vItemsList = null;
    private RecyclerView vCategoriesList = null;
    private TextView vEmptyView = null;

    private LinearLayoutManager mCategoryLayoutManager;
    private RecyclerView.LayoutManager mItemLayoutManager;
    private AddFloatingActionButton vAddItemButton;

    private CategoriesAdapter mCategoriesAdapter = null;
    private ItemsAdapter mItemsAdapter = null;
    private GestureDetectorCompat gestureDetector = null;
    int selectedCategoryIndex = 0;

    ItemsReceiver itemsReceiver;
    IntentFilter itemSuccessFilter;

    private ActionMode mDeleteMode = null;

    private List<Integer> itemIdsToDelete;
    private ItemsFragment self = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items,
                container, false);

        self = this;

        vAddItemButton = (AddFloatingActionButton) rootView.findViewById(R.id.vAddItemsButton);
        vAddItemButton.setOnClickListener(this);
        vItemsList = (RecyclerView) rootView.findViewById(R.id.vItems);
        vCategoriesList = (RecyclerView) rootView.findViewById(R.id.vCategories);
        vEmptyView = (TextView) rootView.findViewById(android.R.id.text1);

        vItemsList.setHasFixedSize(true);
        vCategoriesList.setHasFixedSize(true);

        // use a linear layout manager
        mItemLayoutManager = new LinearLayoutManager(this.getActivity());
        mCategoryLayoutManager = new LinearLayoutManager(this.getActivity());
        vItemsList.setLayoutManager(mItemLayoutManager);
        vItemsList.setItemAnimator(new DefaultItemAnimator());
        vCategoriesList.setLayoutManager(mCategoryLayoutManager);
        vCategoriesList.setItemAnimator(new DefaultItemAnimator());

        mCategoriesAdapter = new CategoriesAdapter(this);
        vCategoriesList.setAdapter(mCategoriesAdapter);
        vCategoriesList.addOnItemTouchListener(this);
        gestureDetector =new GestureDetectorCompat(this.getActivity(), new RecyclerViewDemoOnGestureListener());

        itemsReceiver = new ItemsReceiver();
        itemSuccessFilter = new IntentFilter(ItemsReceiver.ITEMSUCCESSRECEIVER);
        itemSuccessFilter.addCategory(Intent.CATEGORY_DEFAULT);

        mCategoriesAdapter.toggleSelection(selectedCategoryIndex);
        //loadItems(mCategoriesAdapter.getSelectedCategory());


        /*vItemsList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                DebugLogger.message("dx" + dx + "dy" + dy);
                if(mDeleteMode == null) {
                    if (dy < 0) {
                        if (vAddItemButton.getVisibility() == View.GONE) {
                            Utils.toggleVisibility(vAddItemButton, true);
                        }
                    } else if (dy > 5) {
                        if (vAddItemButton.getVisibility() == View.VISIBLE) {
                            Utils.toggleVisibility(vAddItemButton, true);
                        }
                    }
                }
            }
        });*/

        vItemsList.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
        return rootView;
    }

    private void hideViews() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) vAddItemButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        vAddItemButton.animate().translationY(vAddItemButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        vAddItemButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(itemsReceiver, itemSuccessFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(itemsReceiver);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(vAddItemButton)) {
            openDialog();
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
        mcd.show(getActivity().getSupportFragmentManager(), "ComfirmationDialog");
    }


    private ActionMode.Callback mDeleteModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate( R.menu.add_group_screen, menu );
            MenuItem item = menu.findItem(R.id.editGroup);
            item.setVisible(false);
            if(vAddItemButton.getVisibility() == View.VISIBLE)
                Utils.toggleVisibility(vAddItemButton, true);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            ((ExpensesActivity) getActivity()).updateSlidingTabLayout(R.color.theme_default_primary);
            mItemsAdapter.resetSelectedPositions();
            actionMode.finish();
            mDeleteMode = null;
            if(vAddItemButton.getVisibility() == View.GONE)
                Utils.toggleVisibility(vAddItemButton, true);

            mItemsAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            ((ExpensesActivity) getActivity()).updateSlidingTabLayout(R.color.dividerColor);
            int totalSelected = mItemsAdapter.getSelectedCount();
            if(totalSelected > 0) {
                actionMode.setTitle(totalSelected + " selected");
            } else {
                actionMode.finish();
                mDeleteMode = null;
                if(vAddItemButton.getVisibility() == View.GONE)
                    Utils.toggleVisibility(vAddItemButton, true);
            }

            DebugLogger.message("getSelectedPositions" + mItemsAdapter.getSelectedPositions());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.deleteGroup) {
                itemIdsToDelete = mItemsAdapter.getItemIds();
                DebugLogger.message("to be deleted" + itemIdsToDelete);
                String itemNamesString = "";
                for(int i = 0;i< itemIdsToDelete.size();i++) {
                    itemNamesString += DBImpl.getItem(itemIdsToDelete.get(i)).getItemName() + ", ";
                }

                if(itemNamesString.length() > 2) {
                    itemNamesString = itemNamesString.substring(0, itemNamesString.length() -2);
                }

                ConfirmationDialog mcd = new ConfirmationDialog();
                mcd.setOnModeConfirmListener(self);
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.button_delete));
                bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.ITEM_DELETE);
                bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_cancel));
                bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_delete));
                bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to delete "+itemNamesString+"?");
                mcd.setArguments(bundle);
                mcd.show(getActivity().getSupportFragmentManager(), "ComfirmationDialog");
            }
            return true;

        }

    };

    public void runActionMode() {
        if(mDeleteMode == null) {
            mDeleteMode = ((ExpensesActivity)this.getActivity()).startSupportActionMode(mDeleteModeCallback);
        } else {
            mDeleteModeCallback.onCreateActionMode(mDeleteMode, mDeleteMode.getMenu());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    public void stopActionMode() {
        if(mDeleteModeCallback != null && mDeleteMode != null) {
            mDeleteModeCallback.onDestroyActionMode(mDeleteMode);
        }
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            DebugLogger.message("onSing");
            View view = vCategoriesList.findChildViewUnder(e.getX(), e.getY());

            int idx = vCategoriesList.getChildPosition(view);
            if(mDeleteMode != null) {
                if(mItemsAdapter != null) {
                    mItemsAdapter.resetSelectedPositions();
                }
                mDeleteMode.finish();
                mDeleteMode = null;
            }
            mCategoriesAdapter.toggleSelection(idx);
            selectedCategoryIndex = idx;
            loadItems(mCategoriesAdapter.getSelectedCategory());
            return super.onSingleTapConfirmed(e);
        }
    }

    public void loadItems(String category) {
        DebugLogger.message("category" + category);
        ArrayList<Items> items = DBImpl.getItems(AppDataManager.getCurrentGroup().getGroupIdServer(), category);
        if(items.size() > 0) {
            if(mItemsAdapter == null) {
                mItemsAdapter = new ItemsAdapter(this);


            }
            vItemsList.setAdapter(mItemsAdapter);
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
        LoadingScreen.showLoading(this.getActivity(), "Adding " + item);
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
        if(Utils.isConnected(this.getActivity())) {
            FullFlowService.ServiceAddItem(this.getActivity(), AppConstants.NOT_ADDITEM, list, cv);
        } else {
            Utils.showToast(this.getActivity(), "Adding item requires internet connection");
        }
    }

    @Override
    public void modeConfirmed() {

    }

    @Override
    public void deleteMemberConfirmed(int memberId) {

    }

    @Override
    public void deleteExpenseConfirmed(int expenseId) {

    }

    @Override
    public void deleteItemConfirmed(boolean delete) {
        if(delete) {
            DebugLogger.message("itemIdsToDelete" + itemIdsToDelete);
            if(itemIdsToDelete.size() > 0) {
                LoadingScreen.showLoading(this.getActivity(), "Deleting item " + DBImpl.getItem(itemIdsToDelete.get(0)).getItemName());
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair(DB.TItems.ITEM_ID, String.valueOf(itemIdsToDelete.get(0))));
                list.add(new BasicNameValuePair(DB.TItems.GROUP_FK, String.valueOf(AppDataManager.getCurrentGroup().getGroupIdServer())));
                list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.DELETE_ITEM_BY_ID));
                if(Utils.isConnected(this.getActivity())) {
                    FullFlowService.ServiceDeleteItem(this.getActivity(), AppConstants.NOT_DELETEITEM, list);
                } else {
                    Utils.showToast(this.getActivity(), "Removing item requires internet connection");
                    LoadingScreen.dismissProgressDialog();
                }
            } else {
                LoadingScreen.dismissProgressDialog();
                Utils.showToast(this.getActivity(), "Deleted Succesfully");
                if(mDeleteMode != null) {
                    mDeleteMode.finish();
                }
            }
        }
    }

    @Override
    public void openNewActivity(int requestCodeClickImage) {

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
                case AppConstants.NOT_DELETEITEM:
                    if(result){
                        itemIdsToDelete.remove(new Integer(intent.getIntExtra(DB.TItems.ITEM_ID_SERVER, 0)));
                        deleteItemConfirmed(true);
                        loadItems(mCategoriesAdapter.getSelectedCategory());
                    }else{
                        Utils.showToast(context, "Cannot Delete, Please try after some time");
                    }

                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems(mCategoriesAdapter.getSelectedCategory());
    }

    public void setSelectedCategoryIndex(int position) {
        selectedCategoryIndex = position;
    }
}
