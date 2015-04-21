package com.prateek.gem.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.FullFlowService;
import com.prateek.gem.R;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.persistence.DBAdapter.TItems;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.Utils;

public class ItemsActivity extends ActionBarActivity implements OnClickListener{

	Button addItemButton;
	EditText itemNameField;
	ListView itemsListView;
	List<String> items;
	TextView noItemsLabel;
	Spinner categories;
	ServiceHandler serviceHandler;
	DBAdapter db;
	int currentgroupid;
	String[] categoryArray;
	Context context;
	ItemsAdapter itemAdapter;
	int itemLocation;
	MyProgressDialog pd;
	ItemsReceiver itemsReceiver;
	IntentFilter itemSuccessFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items);
		
		initUI();
		
		items = Items.getItemNameOfItems(App.getInstance().getItems(), currentgroupid);
		System.out.println(items.size());
		itemAdapter = new ItemsAdapter(items, context);
		if(items.size() == 0){
			noItemsLabel.setVisibility(View.VISIBLE);
		}
		else{
			noItemsLabel.setVisibility(View.INVISIBLE);
			itemAdapter = new ItemsAdapter(items, context);
		}
		
		itemsListView.setAdapter(itemAdapter);
		
		itemsReceiver = new ItemsReceiver();
		itemSuccessFilter = new IntentFilter(ItemsReceiver.ITEMSUCCESSRECEIVER);
		itemSuccessFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(itemsReceiver, itemSuccessFilter);
		System.out.println("Done");
	}	
	
	public void initUI() {    
    	context = this;
    	currentgroupid = App.getInstance().getCurr_group().getGroupIdServer();
    	addItemButton = (Button) findViewById(R.id.addItemButton);
    	addItemButton.setOnClickListener(this);
    	categories = (Spinner) findViewById(R.id.categories);
    	itemNameField = (EditText) findViewById(R.id.newItemName);
    	noItemsLabel = (TextView) findViewById(R.id.noItemsLabel);
    	itemsListView = (ListView) findViewById(R.id.itemsList);
    	itemsListView.setCacheColorHint(0);
    	categoryArray = getResources().getStringArray(R.array.categoryarray);
    	ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(context, R.layout.my_simple_spinner_item, categoryArray);
    	categoriesAdapter.setDropDownViewResource(R.layout.listitem_dropdown);
    	categories.setAdapter(categoriesAdapter);
    	serviceHandler = new ServiceHandler();
	}

	
	@Override
	public void onResume() {
		super.onResume();
		if(itemAdapter != null){
			itemAdapter.notifyDataSetChanged();
		}
	}

	public class ItemsAdapter extends BaseAdapter {

		private List<String> items;
		private Context _c;
		
		public ItemsAdapter(List<String> items, Context _c) {
			super();
			this.items = items;
			this._c = _c;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			if(v == null){
				LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.list_element_item, null);
			}
			
			final TextView itemName,categoryName;
			ImageView deleteButton,editButton;
			itemName = (TextView) v.findViewById(R.id.itemName);
			categoryName = (TextView) v.findViewById(R.id.categoryName);
			deleteButton = (ImageView) v.findViewById(R.id.button_remove_item);
			editButton = (ImageView) v.findViewById(R.id.button_edit_item);
			
			deleteButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.out.println("1");
					itemLocation = position;
					System.out.println(itemLocation);
					deleteItem(Utils.stringify(itemName.getText()));
				}
			});
			
			editButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					editItem(Utils.stringify(itemName.getText()));
				}
			});
			
			String item = items.get(position);		
			itemName.setText(item);
			categoryName.setText(Items.getCategoryOfItem(item));
			
			return v;
		}
	}
	
	public void deleteItem(final String itemName){		
		StringBuilder message = new StringBuilder("Are you sure to delete "+ itemName + " from "+ App.getInstance().getCurr_group().getGroupName());
		StringBuilder title = new StringBuilder(getString(R.string.deleteItem));
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final AlertDialog ad = builder.create();
		builder.setMessage(message);
		builder.setTitle(title);		
		builder.setIcon(getResources().getDrawable(R.drawable.ic_action_content_discard));
		builder.setPositiveButton(getString(R.string.button_delete), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				db = new DBAdapter(context);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair(TItems.ITEM_NAME, itemName));
				list.add(new BasicNameValuePair(TItems.GROUP_FK, String.valueOf(App.getInstance().getCurr_group().getGroupIdServer())));
				list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.DELETE_ITEM));
				pd = new MyProgressDialog(context,true,"Deleting "+itemName);
				pd.show();
				FullFlowService.ServiceDeleteItem(context,AppConstants.NOT_DELETEITEM, list);				
				ad.dismiss();
				
			}
		});
		
		builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ad.dismiss();
			}
		});
		
		
		
		builder.show();
		
		/*ConfirmationDialog mcd = new ConfirmationDialog();
		Bundle bundle = new Bundle();
		bundle.putString(ConfirmationDialog.TITLE, getString(R.string.deleteItem));
		bundle.putInt(ConfirmConstants.CONFIRM_KEY, ConfirmConstants.ITEM_DELETE);
		bundle.putString("itemname", itemName);							
		bundle.putString(ConfirmationDialog.BUTTON1, getResources().getString(R.string.button_delete));
		bundle.putString(ConfirmationDialog.BUTTON2, getResources().getString(R.string.button_cancel));
		bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to delete "+ itemName + " from "+ GEMApp.getInstance().getCurr_group().getGroupName());
		mcd.setArguments(bundle);
		mcd.show(getSupportFragmentManager(), "ComfirmationDialog");*/
	}
	
	public void editItem(String itemName){
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addItemButton:
			String itemValue = Utils.stringify(itemNameField.getText());
			if(itemValue.length() == 0){	
				Utils.showError(itemNameField, (ItemsActivity)context, getString(R.string.warning_enter_item_message));				
			}
			else if(items.contains(itemValue)){
				Utils.showToast(context, itemValue+" already exists");				
				for(String s:items){
					itemsListView.getChildAt(items.indexOf(s)).setBackgroundResource(R.drawable.group_list_item_selector);
				}
				
				itemsListView.getChildAt(items.indexOf(itemValue)).setBackgroundColor(getResources().getColor(R.color.android_basic_orange));
				itemsListView.setSmoothScrollbarEnabled(true);
				itemsListView.scrollTo(0, items.indexOf(itemValue)*75);
			}
			else{				
				ContentValues cv = new ContentValues();
				cv.put(TItems.ITEM_NAME, itemValue);
				cv.put(TItems.GROUP_FK, String.valueOf(currentgroupid));
				cv.put(TItems.CATEGORY, categoryArray[categories.getSelectedItemPosition()]);
				cv.put(TItems.ITEM_ID_SERVER, 0);				
				
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair(TItems.ITEM_NAME, itemValue));
				list.add(new BasicNameValuePair(TItems.GROUP_FK, String.valueOf(currentgroupid)));
				list.add(new BasicNameValuePair(TItems.CATEGORY, categoryArray[categories.getSelectedItemPosition()]));
				list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.ADD_ITEM));
				pd = new MyProgressDialog(context,true,"Adding "+itemValue);
				pd.show();
				FullFlowService.ServiceAddItem(context,AppConstants.NOT_ADDITEM, list,cv);
			}
			
			break;

		default:
			break;
		}
	}
	
	public class ItemsReceiver extends BroadcastReceiver{

		public static final String ITEMSUCCESSRECEIVER = "com.prateek.gem.views.ItemsActivity.ITEMSUCCESSRECEIVER";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			pd.dismiss();
			int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
			boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);			
			switch (notId) {
			case AppConstants.NOT_ADDITEM:
				if(result){
					String itemName = intent.getStringExtra(TItems.ITEM_NAME);
					Utils.showToast(context, "Added Succesfully");
					
					items.add(itemName);
					itemAdapter.notifyDataSetChanged();
				}else{
					Utils.showToast(context, "Cannot add, Please try after some time");	
				}
				break;
			case AppConstants.NOT_DELETEITEM:
				if(result){
					Utils.showToast(context, "Deleted Succesfully");
					String itemName = intent.getStringExtra(TItems.ITEM_NAME);
					items.remove(itemName);
					App.getInstance().getItems().remove(itemLocation);
					itemAdapter.notifyDataSetChanged();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("hi destroy");
		super.onDestroy();
		unregisterReceiver(itemsReceiver);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("hi pause");
		super.onPause();
	}
	
	

}
