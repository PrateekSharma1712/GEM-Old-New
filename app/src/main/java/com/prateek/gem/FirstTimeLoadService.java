package com.prateek.gem;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.model.Group;
import com.prateek.gem.persistence.DB.TExpenses;
import com.prateek.gem.persistence.DB.TGroups;
import com.prateek.gem.persistence.DB.TItems;
import com.prateek.gem.persistence.DB.TMembers;
import com.prateek.gem.persistence.DB.TSettlement;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.services.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirstTimeLoadService extends IntentService {
	
	Intent broadcastIntent;
	String adminPhoneNumber;
	
	public FirstTimeLoadService() {
		super("FirstTimeLoadService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		broadcastIntent = new Intent();
		broadcastIntent.setAction(AppConstants.SUCCESS_RECEIVER);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra("done", true);
		SharedPreferences preferences = getSharedPreferences(AppConstants.CUSTOM_PREFERENCE, 0);
		adminPhoneNumber = preferences.getString(AppConstants.ADMIN_PHONE, null);

		/** Insert Group in load groups method**/
		List<Group> groups = loadGroups();
		/** Insert Members **/
		for(Group group:groups){
			insertMembers(group.getGroupIdServer());
			insertExpenses(group.getGroupIdServer());
			insertItems(group.getGroupIdServer());
			insertSettlements(group.getGroupIdServer());
		}
		
		sendBroadcast(broadcastIntent);
		
	}
	
	private List<Group> loadGroups() {
		List<Group> groups;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(TMembers.PHONE_NUMBER, adminPhoneNumber));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_GROUPS_OF_MEMBER));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		//System.out.println("RESPONSE OF GETTING GROUPS"+jsonString);
		JSONArray groupsArray = null;	
		JSONObject jsonObject = null;
		groups = new ArrayList<Group>();
		try {
			groupsArray = new JSONArray(jsonString);
			for(int i = 0;i<groupsArray.length();i++){
				jsonObject = groupsArray.getJSONObject(i);
				Group group = new Group();
				ContentValues cv = new ContentValues();
				cv.put(TGroups.GROUPID_SERVER, jsonObject.getInt(TGroups.GROUPID));
				cv.put(TGroups.GROUPNAME, jsonObject.getString(TGroups.GROUPNAME));
				cv.put(TGroups.GROUPICON, jsonObject.getString(TGroups.GROUPICON));
				cv.put(TGroups.DATEOFCREATION, jsonObject.getString(TGroups.DATEOFCREATION));
				cv.put(TGroups.TOTALMEMBERS, jsonObject.getInt(TGroups.TOTALMEMBERS));
				cv.put(TGroups.TOTALOFEXPENSE, (float) jsonObject.getDouble(TGroups.TOTALOFEXPENSE));
				cv.put(TGroups.ADMIN, jsonObject.getString("admin"));
				group.setGroupIdServer(jsonObject.getInt(TGroups.GROUPID));
				group.setGroupName(jsonObject.getString(TGroups.GROUPNAME));
				group.setGroupIcon(jsonObject.getString(TGroups.GROUPICON));
				group.setDate(jsonObject.getString(TGroups.DATEOFCREATION));
				group.setMembersCount(jsonObject.getInt(TGroups.TOTALMEMBERS));
				group.setTotalOfExpense((float) jsonObject.getDouble(TGroups.TOTALOFEXPENSE));
				group.setAdmin(jsonObject.getString("admin"));
				
				//adding group for local references
				groups.add(group);
				
				// adding group to db
				long rowId = DBImpl.insert(TGroups.TGROUPS, cv);
				System.out.println("Inserted group "+rowId);
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups;
	}
	
	public void insertMembers(Integer groupId) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(TMembers.GROUP_ID_FK,""+groupId));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_MEMBERS));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		//System.out.println("jsonStrin:::::"+jsonString);
		JSONArray array = null;
		JSONObject jsonObject = null;
		try {
			array = new JSONArray(jsonString);
			//System.out.println("array======="+array);
			for(int i = 0;i<array.length();i++){				
				jsonObject = array.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put(TMembers.MEMBER_ID_SERVER, jsonObject.getInt(TMembers.MEMBER_ID));
				cv.put(TMembers.NAME, jsonObject.getString(TMembers.NAME));
				cv.put(TMembers.PHONE_NUMBER, jsonObject.getString(TMembers.PHONE_NUMBER));
				cv.put(TMembers.GROUP_ID_FK, jsonObject.getInt(TMembers.GROUP_ID_FK));
				//System.out.println("Content Values"+cv.toString());
				DBImpl.insert(TMembers.TMEMBERS, cv);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertExpenses(Integer groupId) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();			
		list.add(new BasicNameValuePair(TExpenses.GROUP_ID_FK, ""+groupId));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_EXPENSES));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(jsonString!=""){
			JSONArray array = null;
			JSONObject jsonObject = null;
			try {
				array = new JSONArray(jsonString);
				System.out.println("gettin members from groupId"+groupId);
				System.out.println(jsonString);
				for(int i = 0;i<array.length();i++){
					jsonObject = array.getJSONObject(i);
					ContentValues cv = new ContentValues();					
					cv.put(TExpenses.EXPENSE_ID_SERVER, jsonObject.getInt(TExpenses.EXPENSE_ID));
					cv.put(TExpenses.GROUP_ID_FK, jsonObject.getString(TExpenses.GROUP_ID_FK));
					cv.put(TExpenses.DATE_OF_EXPENSE, jsonObject.getString(TExpenses.DATE_OF_EXPENSE));
					cv.put(TExpenses.AMOUNT, (float)jsonObject.getDouble(TExpenses.AMOUNT));
					cv.put(TExpenses.SHARE, (float)jsonObject.getDouble(TExpenses.SHARE));
					cv.put(TExpenses.ITEM, jsonObject.getString(TExpenses.ITEM));
					cv.put(TExpenses.EXPENSE_BY, jsonObject.getString(TExpenses.EXPENSE_BY));
					cv.put(TExpenses.PARTICIPANTS, jsonObject.getString(TExpenses.PARTICIPANTS));
					long rowIf = DBImpl.insert(TExpenses.TABLENAME, cv);
					System.out.println("----expense "+rowIf);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertItems(Integer groupId) {
		
		List<NameValuePair> list = new ArrayList<NameValuePair>();			
		list.add(new BasicNameValuePair("group_fk", ""+groupId));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_ITEMS));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(jsonString!=""){
			JSONArray array = null;
			JSONObject jsonObject = null;
			try {
				array = new JSONArray(jsonString);
				//System.out.println("Items for group"+groupId);
				for(int i = 0;i<array.length();i++){
					jsonObject = array.getJSONObject(i);
					ContentValues cv = new ContentValues();
					cv.put(TItems.ITEM_ID_SERVER, jsonObject.getInt(TItems.ITEM_ID));
					cv.put(TItems.ITEM_NAME, jsonObject.getString(TItems.ITEM_NAME));
					cv.put(TItems.GROUP_FK, jsonObject.getInt(TItems.GROUP_FK));
					cv.put(TItems.CATEGORY, jsonObject.getString(TItems.CATEGORY));
					//System.out.println("Item is "+jsonObject.getInt(TItems.ITEM_ID));
                    DBImpl.insert(TItems.TITEMS, cv);
					
					System.out.println(cv.toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertSettlements(Integer groupId) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(TExpenses.GROUP_ID_FK, String.valueOf(groupId)));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_SETTLEMENT_FOR_GROUP));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
		System.out.println("Group id for settlement"+groupId);
		System.out.println("RESPONSE OF GETTING GROUPS"+jsonString);
		JSONArray array = null;		
		try {			
			array = new JSONArray(jsonString);
			
			for(int i = 0;i<array.length();i++){
				JSONObject object = array.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put(TSettlement.SET_ID_SERVER, object.getInt(TSettlement.SET_ID));
				cv.put(TSettlement.GROUP_ID_FK, object.getInt(TSettlement.GROUP_ID_FK));
				cv.put(TSettlement.GIVEN_BY, object.getString(TSettlement.GIVEN_BY));
				cv.put(TSettlement.TAKEN_BY, object.getString(TSettlement.TAKEN_BY));
				cv.put(TSettlement.AMOUNT, Float.parseFloat(String.valueOf(object.getDouble(TExpenses.AMOUNT))));
				cv.put(TSettlement.DATE, object.getDouble(TSettlement.DATE));
                DBImpl.insert(TSettlement.TABLENAME, cv);
				//System.out.println("Settlement id "+object.getInt(TSettlement.SET_ID));
			}				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	

}
