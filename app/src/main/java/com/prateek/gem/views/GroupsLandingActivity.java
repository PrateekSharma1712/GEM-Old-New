package com.prateek.gem.views;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.FirstTimeLoadService;
import com.prateek.gem.R;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Users;
import com.prateek.gem.services.MyDBService;
import com.prateek.gem.utils.ImageLoader;
import com.prateek.gem.utils.Utils;

public class GroupsLandingActivity extends ActionBarActivity {

	SharedPreferences preferences;
	MyProgressDialog pd;
	Intent dataLoadingIntent,newGroupIntent;
	static Intent dbServiceIntent,expensesIntent;
	MySuccessReceiver successReceiver;	
	IntentFilter successIntentFilter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_landing);
		preferences = getSharedPreferences(AppConstants.CUSTOM_PREFERENCE, 0);
		
		Users admin = new Users();
		admin.setGcmRegId(preferences.getString(AppConstants.ADMIN_ID, null));
		admin.setUserName(preferences.getString(AppConstants.ADMIN_NAME, null));
		admin.setEmail(preferences.getString(AppConstants.ADMIN_EMAIL, null));
		admin.setPassword(preferences.getString(AppConstants.ADMIN_PASSWORD, null));
		admin.setPhoneNumber(preferences.getString(AppConstants.ADMIN_PHONE, null));
		App.getInstance().setAdmin(admin);
		System.out.println(App.getInstance().getAdmin());
		successIntentFilter = new IntentFilter(AppConstants.SUCCESS_RECEIVER);
		successIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		successReceiver = new MySuccessReceiver();		
		registerReceiver(successReceiver, successIntentFilter);		
		newGroupIntent = new Intent(GroupsLandingActivity.this,NewGroupActivity.class);
		dbServiceIntent = new Intent(GroupsLandingActivity.this, MyDBService.class);
		expensesIntent = new Intent(GroupsLandingActivity.this, ExpensesActivity.class);
		
		/* One time load code */
		if(!isFirstTimeLoadDone()){
			//Load first time setup if preferences doesnot have
			doFirstTimeLoad();
		}else{
			displayGroups(savedInstanceState);
		}
		
		
	}

	private void displayGroups(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void doFirstTimeLoad() {
		if(Utils.hasConnection(this)){
			pd = new MyProgressDialog(this, true, "Loading initial data");
			pd.show();
			dataLoadingIntent = new Intent(GroupsLandingActivity.this, FirstTimeLoadService.class);			
			startService(dataLoadingIntent);
		}else{
			Utils.showToast(this, getString(R.string.nonetwork));
		}
		
	}

	private boolean isFirstTimeLoadDone() {
		//Return true if first time load key has true value
		return preferences.getBoolean(AppConstants.ONETIME_LOAD, false);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.groups_landing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if(id == R.id.action_add_group_action){
			startActivity(newGroupIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
		unregisterReceiver(successReceiver);		
	}
	
	private void updateUI() {
		pd.dismiss();
		Editor editor = preferences.edit();
		editor.putBoolean(AppConstants.ONETIME_LOAD, true);
		editor.commit();
	} 
	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private ListView groupsListView;
		private GroupsAdapter groupsAdapter;
		MyResultReceiver resultReceiver;
		IntentFilter dbIntentFilter;
		
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_groups_landing,
					container, false);
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			initUI();
			resultReceiver = new MyResultReceiver();
			
			dbIntentFilter = new IntentFilter(AppConstants.DB_RECEIVER);
			dbIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
			
			getActivity().registerReceiver(resultReceiver, dbIntentFilter);
			
			dbServiceIntent.putExtra(AppConstants.SERVICE_ID, AppConstants.ServiceIDs.GET_GROUPS_OF_MEMBER);
			
			getActivity().startService(dbServiceIntent);
		}
		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			if(groupsAdapter != null)
			groupsAdapter.notifyDataSetChanged();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			getActivity().unregisterReceiver(resultReceiver);
		}

		private void initUI() {
			groupsListView = (ListView) getActivity().findViewById(R.id.groupsListView);
			groupsListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					App.getInstance().setCurr_group(App.getInstance().getAllGroups().get(position));
					getActivity().startActivity(expensesIntent);
				}
			});
			
		}
		
		private void populateGroups() {			
			System.out.println("Populating Groups");
			View emptyView = getActivity().findViewById(R.id.noGroupsView);
			groupsListView.setEmptyView(emptyView);
			groupsAdapter = new GroupsAdapter(getActivity(), App.getInstance().getAllGroups());
			groupsListView.setAdapter(groupsAdapter);			
			/*if(groupsAdapter.isEmpty()){
				emptyView.setVisibility(View.VISIBLE);
				groupsListView.setVisibility(View.GONE);
			}else{
				emptyView.setVisibility(View.GONE);
				groupsListView.setVisibility(View.VISIBLE);
			}*/
				
		}
		
		public class GroupsAdapter extends BaseAdapter {
			private Context _c;
			private List<Group> groups;
			private ImageLoader imgLoader;
			
			public class ViewHolder{
				public TextView groupname,groupSubText;
				public ImageView groupImage;
			}
			
			public GroupsAdapter(Context _c, List<Group> groups) {
				super();
				this.groups = groups;
				this._c = _c;
				imgLoader = new ImageLoader(_c);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return groups.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return groups.get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return groups.get(position).getGroupId();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View v = convertView;
				ViewHolder holder;
				if(v == null){
					LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = li.inflate(R.layout.group_row, null);
					holder = new ViewHolder();
					holder.groupImage = (ImageView) v.findViewById(R.id.groupImage);
					holder.groupname = (TextView) v.findViewById(R.id.groupName);
					holder.groupSubText = (TextView) v.findViewById(R.id.groupSubText);
					v.setTag(holder);
				}
				else
					holder=(ViewHolder)v.getTag();
				
				
				Group group = groups.get(position);	
				holder.groupImage.setTag(AppConstants.URL_IMAGES+group.getGroupIcon().getPath());
				if(group.getGroupIcon().getPath().equals("0")){
					holder.groupImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
				}
				else{
					imgLoader.DisplayImage(AppConstants.URL_IMAGES+group.getGroupIcon().getPath(), holder.groupImage);
					//imageManager.displayImage(AppConstants.URL_IMAGES+group.getGroupIcon().getPath(), holder.groupImage, R.drawable.ic_launcher);
					//holder.groupImage.setImageURI(group.getGroupIcon());
				}
				holder.groupname.setText(group.getGroupName());
				holder.groupSubText.setText("Expenses : "+group.getTotalOfExpense());
				
				return v;
			}
		}
		
		public class MyResultReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("in receiver");
				switch (intent.getIntExtra(AppConstants.SERVICE_ID, 0)) {
				case AppConstants.ServiceIDs.GET_GROUPS_OF_MEMBER:
					System.out.println("to populate");
					populateGroups();
					break;

				default:
					break;
				}
			}
		}
	}
	
	public class MySuccessReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI();
			if(intent.getBooleanExtra("done", false)){			
				Utils.showToast(context, "Loaded successfully");
				getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
			}
		}
		
	}
	
	

}
