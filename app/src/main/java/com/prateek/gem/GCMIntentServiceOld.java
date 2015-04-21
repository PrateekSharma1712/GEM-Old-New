package com.prateek.gem;

/*import static com.prateek.gem.AppConstants.DELIMITER;
import static com.prateek.gem.AppConstants.DELIMITER_BW;
import static com.prateek.gem.AppConstants.EXPENSEADDED;
import static com.prateek.gem.AppConstants.EXPENSEDELETED;
import static com.prateek.gem.AppConstants.ITEMADDED;
import static com.prateek.gem.AppConstants.ITEMDELETED;
import static com.prateek.gem.AppConstants.MEMBERADDED;
import static com.prateek.gem.AppConstants.MEMBERDELETED;
import static com.prateek.gem.AppConstants.SENDER_ID;
import static com.prateek.gem.AppConstants.SETTLEMENTADDED;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.prateek.gem.AppConstants.JSONConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Items;
import com.prateek.gem.model.Member;
import com.prateek.gem.persistence.DBAdapter;
import com.prateek.gem.persistence.DBAdapter.TExpenses;
import com.prateek.gem.persistence.DBAdapter.TGroups;
import com.prateek.gem.persistence.DBAdapter.TItems;
import com.prateek.gem.persistence.DBAdapter.TMembers;
import com.prateek.gem.persistence.DBAdapter.TSettlement;
import com.prateek.gem.services.ServerUtilities;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.MainActivity;*/

public class GCMIntentServiceOld //extends GCMBaseIntentService
{

	/*private static final String TAG = "GCMIntentService";
	static DBAdapter db;
	private static Context context;
	static SharedPreferences preferences;
	private static int groupToDelete;
	static StringBuilder messageToShow,bigMessageToShow;
	static StringBuilder titleToShow;
	static FirstTimeLoadService loadingService;
	static int notId;

    public GCMIntentService() {
        super(SENDER_ID);
    }

    *//**
     * Method called on device registered
     **//*
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        Log.d("NAME", MainActivity.name);
        Log.d("EMAIL", MainActivity.email);
        Log.d("NUMBER", MainActivity.number);
        Log.d("PASSWORD VALUE", MainActivity.passwordValue);
        ServerUtilities.register(context, MainActivity.name, MainActivity.email,MainActivity.number, MainActivity.passwordValue, registrationId);
    }

    *//**
     * Method called on device un registred
     * *//*
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    *//**
     * Method called on Receiving a new message
     * *//*
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        this.context = this;
        String message = intent.getExtras().getString("price");
        preferences = getSharedPreferences(AppConstants.CUSTOM_PREFERENCE, 0);
        //displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    *//**
     * Method called on receiving a deleted message
     * *//*
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        //displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    *//**
     * Method called on Error
     * *//*
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,errorId));
        return super.onRecoverableError(context, errorId);
    }

    *//**
     * Issues a notification to inform the user that server has sent a message.
     *//*
    private static void generateNotification(Context context, String messageFull) {
    	System.out.println("Received Notification: "+messageFull);    	
    	
    	String message = messageFull.split(DELIMITER)[0];
    	String secretCode = messageFull.split(DELIMITER)[1];
    	System.out.println(secretCode);
    	System.out.println(secretCode.length());
    	System.out.println("Secret code "+(secretCode.length() != 0));
    	if(secretCode.length() != 0){    		
    		String[] eventType = new String(secretCode).split(DELIMITER_BW);
    		System.out.println("event "+eventType.length);
    		System.out.println("Eventtype "+eventType[0].equalsIgnoreCase("itemadded"));
    		
    		if(eventType[0].equalsIgnoreCase(ITEMADDED)){
    			String response = getAddedItemFromServer(secretCode);    			
    			addItem(response);
    			notId = 1;
    			System.out.println("response after notification"+response);
    		}else if(eventType[0].equalsIgnoreCase(ITEMDELETED)){
    			deleteItem(secretCode);
    			notId = 2;
    		}else if(eventType[0].equalsIgnoreCase(EXPENSEADDED)){
    			String response = getAddedExpenseFromServer(secretCode);    			
    			addExpense(response);
    			notId = 3;
    			System.out.println("response after notification"+response);
    		}else if(eventType[0].equalsIgnoreCase(MEMBERADDED)){
    			String response = getAddedMemberFromServer(secretCode);    			
    			addMember(response);
    			notId = 4;
    			System.out.println("response after notification"+response);
    		}else if(eventType[0].equalsIgnoreCase(EXPENSEDELETED)){
    			deleteExpense(secretCode);
    			notId = 5;
    		}else if(eventType[0].equalsIgnoreCase(MEMBERDELETED)){
    			deleteMember(secretCode);
    			notId = 6;
    		}else if(eventType[0].equalsIgnoreCase(SETTLEMENTADDED)){
    			String response = getAddedSettlementFromServer(secretCode);
    			addSettlement(response);
    			notId = 7;
    			System.out.println("response after notification"+response);
    		}
    		
    	}
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("notId", notId);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);		
		builder.setContentTitle(titleToShow);	
		builder.setContentText(messageToShow);
		builder.setContentIntent(intent);
		builder.setSmallIcon(icon);
		builder.setAutoCancel(true);
		builder.setOngoing(false);
		builder.setWhen(when);
		builder.setTicker(messageToShow);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigMessageToShow));
		long[] pattern = {200,100};
		builder.setVibrate(pattern);		
		Uri alarmSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		builder.setSound(alarmSound);
        
        
        *//*notification.setLatestEventInfo(context, titleToShow, messageToShow, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;*//*
        notificationManager.notify(notId, builder.build());      

    }

    *//*
     * this method gets the json response of added item in server by its item_id in table items
     *//*
	private static String getAddedItemFromServer(String secretCode) {
		String itemIdToFetch = secretCode.split(DELIMITER_BW)[1];
		ServiceHandler handler = new ServiceHandler();
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id", itemIdToFetch));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_SPECIFIC_ITEM)));
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
		return response;
	}
	
	*//*
	 * this method gets the json response of added item in server by its item_id in table items
	 *//*
	private static String getAddedExpenseFromServer(String secretCode) {
		String expenseIdToFetch = secretCode.split(DELIMITER_BW)[1];
		ServiceHandler handler = new ServiceHandler();
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id", expenseIdToFetch));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_SPECIFIC_EXPENSE)));
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
		return response;
	}
	*//*
	 * this method gets the json response of added item in server by its item_id in table items
	 *//*
	private static String getAddedMemberFromServer(String secretCode) {
		String memberIdToFetch = secretCode.split(DELIMITER_BW)[1];
		ServiceHandler handler = new ServiceHandler();
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id", memberIdToFetch));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_SPECIFIC_MEMBER)));
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
		return response;
	}
	
	*//*
	 * this method gets the json response of added settlement in server by its server_id in table items
	 *//*
	private static String getAddedSettlementFromServer(String secretCode) {
		String settlementIdToFetch = secretCode.split(DELIMITER_BW)[1];
		ServiceHandler handler = new ServiceHandler();
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id", settlementIdToFetch));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_SPECIFIC_SETTLEMENT)));
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
		return response;
	}


	
	*//*
	 * this method adds item received from server in local db
	 *//*
	private static void addItem(String response) {
		
		db = new DBAdapter(context);
		db.open();
		ContentValues cv = new ContentValues();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if(!jsonObject.getBoolean("error")){
				cv.put(TItems.ITEM_ID_SERVER, jsonObject.getInt(TItems.ITEM_ID));
				cv.put(TItems.ITEM_NAME, jsonObject.getString(TItems.ITEM_NAME));
				cv.put(TItems.GROUP_FK, jsonObject.getInt(TItems.GROUP_FK));
				cv.put(TItems.CATEGORY, jsonObject.getString(TItems.CATEGORY));
				long rowId = db.insert(TItems.TITEMS, cv);
				System.out.println("New Item inserted at id: "+rowId);
				messageToShow = new StringBuilder();
				titleToShow = new StringBuilder();
				bigMessageToShow = new StringBuilder();
				messageToShow.append(jsonObject.getString(TItems.ITEM_NAME));
				titleToShow.append("Item Added - "+db.getGroup(jsonObject.getInt(TItems.GROUP_FK)).getGroupName());
				bigMessageToShow.append(jsonObject.getString(TItems.ITEM_NAME) + " added in \""+db.getGroup(jsonObject.getInt(TItems.GROUP_FK)).getGroupName()+"\" group");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.close();
	}
	
	*//*
	 * gets the item's server id from secret code and deletes particular item in local db according to its item_server_id field
	 *//*
	private static void deleteItem(String secretCode) {
		int itemServerIdToDelete = Integer.parseInt(secretCode.split(DELIMITER_BW)[1]);		
		db = new DBAdapter(context);
		db.open();
		Items itemToDelete = db.getItemByServerId(itemServerIdToDelete);
		int rowsAffacted = db.removeItem(itemServerIdToDelete);
		db.close();
		
		System.out.println("Rows affected afer deletion "+rowsAffacted);
		db.open();
		System.out.println(db.getItems(GEMApp.getInstance().getCurr_group().getGroupIdServer()));
		db.close();
		db.open();
		messageToShow = new StringBuilder();
		titleToShow = new StringBuilder();
		bigMessageToShow = new StringBuilder();
		messageToShow.append(itemToDelete.getItemName());
		titleToShow.append("Item Deleted - "+db.getGroup(itemToDelete.getGroupFK()));
		bigMessageToShow.append(itemToDelete.getItemName() + " deleted from \""+db.getGroup(itemToDelete.getGroupFK())+"\" group");
		db.close();
	}
	
	*//*
	 * this method adds expense received from server in local db
	 *//*
	private static void addExpense(String response) {
		
		db = new DBAdapter(context);
		db.open();
		ContentValues cv = new ContentValues();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if(!jsonObject.getBoolean("error")){
				cv.put(TExpenses.EXPENSE_ID_SERVER, jsonObject.getInt(TExpenses.EXPENSE_ID));
				cv.put(TExpenses.GROUP_ID_FK, jsonObject.getInt(TExpenses.GROUP_ID_FK));
				cv.put(TExpenses.DATE_OF_EXPENSE, jsonObject.getString(TExpenses.DATE_OF_EXPENSE));
				cv.put(TExpenses.AMOUNT, jsonObject.getDouble(TExpenses.AMOUNT));
				cv.put(TExpenses.SHARE, jsonObject.getDouble(TExpenses.SHARE));
				cv.put(TExpenses.ITEM, jsonObject.getString(TExpenses.ITEM));
				cv.put(TExpenses.EXPENSE_BY, jsonObject.getString(TExpenses.EXPENSE_BY));
				cv.put(TExpenses.PARTICIPANTS, jsonObject.getString(TExpenses.PARTICIPANTS));
				JSONArray parts = new JSONArray(jsonObject.getString(TExpenses.PARTICIPANTS));
				String participantsString = "";
				for(int i = 0;i<parts.length();i++){
					participantsString += parts.getJSONObject(i).getString(JSONConstants.MEMBERNAME);
					participantsString += ", ";
				}
				participantsString = (String) participantsString.subSequence(0, participantsString.length()-2);
				System.out.println(cv);
				long rowId = db.insert(TExpenses.TABLENAME, cv);
				System.out.println("New expense inserted at id: "+rowId);
				messageToShow = new StringBuilder();
				titleToShow = new StringBuilder();
				bigMessageToShow = new StringBuilder();
				messageToShow.append("On "+Utils.formatShortDate(jsonObject.getString(TExpenses.DATE_OF_EXPENSE)));
				messageToShow.append(" by "+jsonObject.getString(TExpenses.EXPENSE_BY));
				messageToShow.append(" for "+jsonObject.getString(TExpenses.ITEM));
				titleToShow.append("Expense Added - "+db.getGroup(jsonObject.getInt(TExpenses.GROUP_ID_FK)).getGroupName());
				bigMessageToShow.append(jsonObject.getString(TExpenses.EXPENSE_BY) + " added an expense on "+Utils.formatShortDate(jsonObject.getString(TExpenses.DATE_OF_EXPENSE))+" for "+jsonObject.getString(TExpenses.ITEM));
				bigMessageToShow.append(" worth Rs. "+jsonObject.getDouble(TExpenses.AMOUNT) +" having "+participantsString
						+ " as participants in \""+db.getGroup(jsonObject.getInt(TExpenses.GROUP_ID_FK)).getGroupName()+"\" group");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.close();
	}
	
	*//*
	 * this method adds member received from server in local db
	 *//*
	private static void addMember(String response) {		
		db = new DBAdapter(context);
		
		ContentValues cv = new ContentValues();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if(!jsonObject.getBoolean("error")){
				//Check if member is already part of group?
				//if not, add all stuff related of group from server db to local db
				int groupId = jsonObject.getInt(TMembers.GROUP_ID_FK);
				if(isNewMember(jsonObject.getInt(TMembers.GROUP_ID_FK),db)){
					System.out.println("is new");
					// after adding all stuff related to groupid, remove duplicate members
					ServiceHandler handler = new ServiceHandler();
					List<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair(TGroups.GROUPID_SERVER, String.valueOf(groupId)));
					list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_GROUP)));
					String response2 = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
					if(response2 != null){
						JSONObject jsonObject1 = new JSONObject(response2);
						if(!jsonObject1.getBoolean("error")){
							ContentValues cv1 = new ContentValues();
							cv1.put(TGroups.GROUPID_SERVER, jsonObject1.getInt(DBAdapter.TGroups.GROUPID));				
							cv1.put(TGroups.GROUPNAME, jsonObject1.getString(DBAdapter.TGroups.GROUPNAME));
							cv1.put(TGroups.GROUPICON, jsonObject1.getString(DBAdapter.TGroups.GROUPICON));
							cv1.put(TGroups.DATEOFCREATION, jsonObject1.getString(DBAdapter.TGroups.DATEOFCREATION));
							cv1.put(TGroups.TOTALMEMBERS, jsonObject1.getInt(DBAdapter.TGroups.TOTALMEMBERS));
							cv1.put(TGroups.TOTALOFEXPENSE, (float) jsonObject1.getDouble(DBAdapter.TGroups.TOTALOFEXPENSE));
							cv1.put(TGroups.ADMIN, jsonObject1.getString("admin"));
							db.open();
							long rowId = db.insert(TGroups.TGROUPS, cv1);
							System.out.println("Group added"+rowId);
							if(rowId > 0){
								loadingService = new FirstTimeLoadService();
								db.deleteMemberByGroup(groupId);
								loadingService.insertMembers(groupId,db);
								loadingService.insertExpenses(groupId,db);
								loadingService.insertItems(groupId,db);
								loadingService.insertSettlements(groupId,db);
								messageToShow = new StringBuilder();
								titleToShow = new StringBuilder();
								bigMessageToShow = new StringBuilder();
								messageToShow.append("Admin has added you");
								titleToShow.append("Added Member - "+db.getGroup(jsonObject.getInt(TMembers.GROUP_ID_FK)).getGroupName());
								bigMessageToShow.append("You are added by admin in \""+db.getGroup(jsonObject.getInt(TMembers.GROUP_ID_FK)).getGroupName()+"\" group");
								
							}
							db.close();
						}						
					}					
				}else{
					System.out.println("is not new");
					cv.put(TMembers.MEMBER_ID_SERVER, jsonObject.getInt(TMembers.MEMBER_ID));
					cv.put(TMembers.GROUP_ID_FK, jsonObject.getInt(TMembers.GROUP_ID_FK));				
					cv.put(TMembers.NAME, jsonObject.getString(TMembers.NAME));
					cv.put(TMembers.PHONE_NUMBER, jsonObject.getString(TMembers.PHONE_NUMBER));
					db.open();
					long rowId = db.insert(TMembers.TMEMBERS, cv);
					System.out.println("New member inserted at id: "+rowId);
					messageToShow = new StringBuilder();
					titleToShow = new StringBuilder();
					bigMessageToShow = new StringBuilder();
					messageToShow.append("Admin has added "+jsonObject.getString(TMembers.NAME));
					titleToShow.append("Added Member - "+db.getGroup(jsonObject.getInt(TMembers.GROUP_ID_FK)).getGroupName());
					bigMessageToShow.append("New Member '"+jsonObject.getString(TMembers.NAME)+"' added by admin in \""+db.getGroup(jsonObject.getInt(TMembers.GROUP_ID_FK)).getGroupName()+"\" group");
					db.close();
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	*//*
	 * this method searches whether its a new member for that group
	 *//*
	private static boolean isNewMember(int groupId, DBAdapter db) {
		boolean result = true;
		db.open();
		List<Group> groups = db.getGroups();
		db.close();
		for (Group group : groups) {
			if(group.getGroupIdServer() == groupId){
				System.out.println("server"+group.getGroupIdServer() +" vs "+groupId);
				result = false;
				break;
			}
		}
		
		return result;
		
	}

	*//*
	 * this method adds settlement received from server in local db
	 *//*
	private static void addSettlement(String response) {
		
		db = new DBAdapter(context);
		db.open();
		ContentValues cv = new ContentValues();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if(!jsonObject.getBoolean("error")){
				cv.put(TSettlement.SET_ID_SERVER, jsonObject.getInt(TSettlement.SET_ID));
				cv.put(TSettlement.GROUP_ID_FK, jsonObject.getInt(TSettlement.GROUP_ID_FK));
				cv.put(TSettlement.GIVEN_BY, jsonObject.getString(TSettlement.GIVEN_BY));
				cv.put(TSettlement.TAKEN_BY, jsonObject.getString(TSettlement.TAKEN_BY));
				cv.put(TSettlement.AMOUNT, Float.parseFloat(String.valueOf(jsonObject.getDouble(TSettlement.AMOUNT))));
				cv.put(TSettlement.DATE, jsonObject.getDouble(TSettlement.DATE));
				
				long rowId = db.insert(TSettlement.TABLENAME, cv);
				System.out.println("New settlement inserted at id: "+rowId);
				
				messageToShow = new StringBuilder();
				titleToShow = new StringBuilder();
				bigMessageToShow = new StringBuilder();
				messageToShow.append(jsonObject.getString(TSettlement.GIVEN_BY)+" to "+jsonObject.getString(TSettlement.TAKEN_BY) +" of Rs. "+jsonObject.getDouble(TSettlement.AMOUNT));
				titleToShow.append("Settlement Done - "+db.getGroup(jsonObject.getInt(TSettlement.GROUP_ID_FK)).getGroupName());
				bigMessageToShow.append("A settlement is made in \""+db.getGroup(jsonObject.getInt(TSettlement.GROUP_ID_FK)).getGroupName()+"\" group");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.close();
	}

	*//*
	 * gets the expense's server id from secret code and deletes particular expense in local db according to its expense_server_id field
	 *//*
	private static void deleteExpense(String secretCode) {
		int expenseServerIdToDelete = Integer.parseInt(secretCode.split(DELIMITER_BW)[1]);
		db = new DBAdapter(context);
		db.open();
		ExpenseOject expense = db.getExpenseByServerId(expenseServerIdToDelete);
		int rowsAffacted = db.removeExpense(expenseServerIdToDelete);
		db.close();
		
		System.out.println("Rows affected afer deletion "+rowsAffacted);
		db.open();
		System.out.println(db.getExpenses(GEMApp.getInstance().getCurr_group().getGroupIdServer()));
		JSONArray parts = null;
		db.close();
		
		String participantsString = "";
		try{
			parts = new JSONArray(expense.getParticipants());
			for(int i = 0;i<parts.length();i++){
				participantsString += parts.getJSONObject(i).getString(JSONConstants.MEMBERNAME);
				participantsString += ", ";
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		db.open();
		participantsString = (String) participantsString.subSequence(0, participantsString.length()-2);
		messageToShow = new StringBuilder();
		titleToShow = new StringBuilder();
		bigMessageToShow = new StringBuilder();
		messageToShow.append("On "+Utils.formatShortDate(String.valueOf(expense.getDate())));
		messageToShow.append(" by "+expense.getExpenseBy());
		messageToShow.append(" for "+expense.getItem());
		titleToShow.append("Expense Deleted - "+db.getGroup(expense.getGroupId()));
		bigMessageToShow.append("Expense by "+expense.getExpenseBy()+" for "+expense.getItem()+" has been deleted dated "+Utils.formatShortDate(String.valueOf(expense.getDate())));
		bigMessageToShow.append(" worth Rs. "+expense.getAmount() +" having "+participantsString
				+ " as participants in \""+db.getGroup(expense.getGroupId()).getGroupName()+"\" group");
		
		db.close();
	}
	
	*//*
	 * gets the member's server id from secret code and deletes particular member in local db according to its member_server_id field
	 *//*
	private static void deleteMember(String secretCode) {
		int memberServerIdToDelete = Integer.parseInt(secretCode.split(DELIMITER_BW)[1]);
		System.out.println("member serverid to delete"+memberServerIdToDelete);
		//check whether admin is the one who is deleted,
		//if yes delete all the related stuff of that group  from which he is deleted
		db = new DBAdapter(context);
		if(isDeletedMemberAdmin(memberServerIdToDelete,db)){
			System.out.println("Group Id"+groupToDelete);
			System.out.println("is admin of device");
			db.open();
			String groupName = db.getGroup(groupToDelete).getGroupName();
			db.deleteAllStuff(groupToDelete,true);
			db.close();
			db.open();
			messageToShow = new StringBuilder();
			titleToShow = new StringBuilder();
			bigMessageToShow = new StringBuilder();
			messageToShow.append("Admin has removed you");
			titleToShow.append("Removed You - "+groupName);
			bigMessageToShow.append("Admin has removed you from \""+groupName+"\" group. ");
			db.close();
		}else{
			System.out.println("is not admin of device");
			
			db.open();
			Member memberByServerId = db.getMemberByServerId(memberServerIdToDelete);
			int rowsAffacted = db.removeMember(memberServerIdToDelete);
			db.close();
			
			System.out.println("Rows affected afer deletion "+rowsAffacted);
			db.open();
			System.out.println(db.getMembersOfGroup(GEMApp.getInstance().getCurr_group().getGroupIdServer()));
			db.close();
			db.open();
			messageToShow = new StringBuilder();
			titleToShow = new StringBuilder();
			bigMessageToShow = new StringBuilder();
			messageToShow.append("Admin has removed "+memberByServerId.getMemberName());
			titleToShow.append("Removed Member - "+db.getGroup(memberByServerId.getGroupIdFk()).getGroupName());
			bigMessageToShow.append("Admin has removed "+memberByServerId.getMemberName()+" from \""+db.getGroup(memberByServerId.getGroupIdFk()).getGroupName()+"\" group");
			db.close();
		}
	}

	private static boolean isDeletedMemberAdmin(int memberServerIdToDelete, DBAdapter db) {
		boolean result = false;
		String adminPhone = preferences.getString(AppConstants.ADMIN_PHONE, null);
		db.open();
		Member member = db.getMemberByServerId(memberServerIdToDelete);
		db.close();
		System.out.println("is admin"+member);
		System.out.println(" is admin"+adminPhone);
		if(member.getPhoneNumber().equals(adminPhone)){
			result = true;
			groupToDelete = member.getGroupIdFk();
		}
		
		return result;
	}*/
}
