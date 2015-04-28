package com.prateek.gem.services;

import java.util.ArrayList;
import java.util.List;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.model.Group;
import com.prateek.gem.persistence.DB.TGroups;
import com.prateek.gem.persistence.DBImpl;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MyDBService extends IntentService {
	
	Intent sendingIntent;
	
	public MyDBService() {
		super("MyDBService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("in service");
		sendingIntent = new Intent();		
		sendingIntent.addCategory(Intent.CATEGORY_DEFAULT);
		System.out.println("intent service id"+intent.getIntExtra(AppConstants.SERVICE_ID, 0));
		switch (intent.getIntExtra(AppConstants.SERVICE_ID, 0)) {
			case AppConstants.ServiceIDs.GET_GROUPS_OF_MEMBER:
				getGroups(this);
				sendingIntent.setAction(AppConstants.DB_RECEIVER);
				sendingIntent.putExtra(AppConstants.RESULT_BOOLEAN, true);
				
				break;
			case AppConstants.ServiceIDs.GET_EXPENSES:
				int currentGroupId = intent.getIntExtra(TGroups.GROUPID_SERVER, 0);
				System.out.println("get expenses"+currentGroupId);
				getExpenses(this,currentGroupId);
				getGroupDetail(this,currentGroupId);
				getMembers(this,currentGroupId);
				getItems(this,currentGroupId);
				getSettlements(this,currentGroupId);
				//System.out.println(GEMApp.getInstance().getCurr_group());
				System.out.println(App.getInstance().getCurr_Members());
				//System.out.println(GEMApp.getInstance().getItems());
				//System.out.println(GEMApp.getInstance().getExpensesList());
				//System.out.println(GEMApp.getInstance().getSettlements());
				//getSettlements(this,currentGroupId);
				sendingIntent.setAction(AppConstants.DB_RECEIVER2);
				sendingIntent.putExtra(AppConstants.RESULT_BOOLEAN, true);
				
				break;

		default:
			break;
		}
		sendingIntent.putExtra(AppConstants.SERVICE_ID,intent.getIntExtra(AppConstants.SERVICE_ID, 0));
		sendBroadcast(sendingIntent);
		System.out.println("))))))))))))))");
	}

	

	private void getGroups(Context context) {
		List<Group> newGroupList = new ArrayList<Group>();
		App.getInstance().setAllGroups(DBImpl.getGroups());
		for(Group group: App.getInstance().getAllGroups()){
			float totalexpense = DBImpl.getExpenseTotal(group.getGroupIdServer());
			Group newGroup = group;
			newGroup.setTotalOfExpense(totalexpense);
			newGroupList.add(newGroup);
		}
		App.getInstance().setAllGroups(newGroupList);
	}
	
	public void getExpenses(Context context,int groupId){
		App.getInstance().setExpensesList(DBImpl.getExpenses(groupId));
		System.out.println("....."+DBImpl.getExpenses(groupId));
	}
	
	private void getGroupDetail(Context context,int groupId){
		App.getInstance().setCurr_group(DBImpl.getGroup(groupId));
	}
	
	public void getMembers(Context context,int groupId){
		App.getInstance().setCurr_Members(DBImpl.getMembersOfGroup(groupId));
	}
	
	public void getItems(Context context,int groupId){
		App.getInstance().setItems(DBImpl.getItems(String.valueOf(groupId)));
	}
	
	public void getSettlements(Context context, int groupId) {
		App.getInstance().setSettlements(DBImpl.getSettlements(groupId));
	}
}
