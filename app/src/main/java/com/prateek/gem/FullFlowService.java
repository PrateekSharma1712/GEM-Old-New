
package com.prateek.gem;

import static com.prateek.gem.AppConstants.DELIMITER;
import static com.prateek.gem.AppConstants.DELIMITER_BW;
import static com.prateek.gem.AppConstants.EXPENSEADDED;
import static com.prateek.gem.AppConstants.EXPENSEDELETED;
import static com.prateek.gem.AppConstants.ITEMADDED;
import static com.prateek.gem.AppConstants.ITEMDELETED;
import static com.prateek.gem.AppConstants.MEMBERADDED;
import static com.prateek.gem.AppConstants.MEMBERDELETED;
import static com.prateek.gem.AppConstants.SETTLEMENTADDED;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.items.ItemsActivity;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Member;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DB.TExpenses;
import com.prateek.gem.persistence.DB.TSettlement;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.views.AddExpenseActivity.AddExpenseRecevier;
import com.prateek.gem.views.AddMembersActivity;
import com.prateek.gem.groups.ExpensesFragment.DeleteExpenseReceiver;
import com.prateek.gem.views.HisabActivity.AddSettlementRecevier;
import com.prateek.gem.views.MembersActivity.DeleteMemberRecevier;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class FullFlowService extends IntentService {

    public static List<NameValuePair> addItemList,deleteItemList,addExpenseList,deleteExpenseList,addSettlementList,deleteMemberList;
    public static ContentValues addExpenseCv,addSettlementCv,addItemCv;
    public static List<Member> addSelectedMembers;
    public static int membersCurrentGroupIdServer,membersCurrentGroupId;
    private Context context;
    private static Context activityContext;
    static Intent itemIntent;
    static Intent expenseIntent;
    static Intent memberIntent;
    static Intent settleIntent;

    public static final String EXTRA_NOTID = "com.prateek.gem.extra.NOTID";

    public static void ServiceAddItem(Context context, int notAdditem,List<NameValuePair> listArrived,ContentValues cv) {
        itemIntent = new Intent();
        itemIntent.setAction(ItemsActivity.ItemsReceiver.ITEMSUCCESSRECEIVER);
        itemIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent intent = new Intent(context, FullFlowService.class);
        intent.putExtra(EXTRA_NOTID, notAdditem);
        addItemCv = cv;
        addItemList = listArrived;
        context.startService(intent);
    }

    public static void ServiceDeleteItem(Context context, int notDeleteitem,List<NameValuePair> listArrived) {
        DebugLogger.message("started");
        itemIntent = new Intent();
        itemIntent.setAction(ItemsActivity.ItemsReceiver.ITEMSUCCESSRECEIVER);
        itemIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent intent = new Intent(context, FullFlowService.class);
        intent.putExtra(EXTRA_NOTID, notDeleteitem);
        deleteItemList = listArrived;
        context.startService(intent);
    }

	public static void ServiceAddExpense(Context context, int notAddExpense,List<NameValuePair> listArrived, ContentValues cv) {
		expenseIntent = new Intent();
		expenseIntent.setAction(AddExpenseRecevier.ADDEXPENSESUCCESSRECEIVER);
		expenseIntent.addCategory(Intent.CATEGORY_DEFAULT);

		Intent intent = new Intent(context, FullFlowService.class);
		intent.putExtra(EXTRA_NOTID, notAddExpense);
		addExpenseList = listArrived;
		addExpenseCv = cv;
		context.startService(intent);
	}

	public static void ServiceDeleteExpense(Context context, int notDeleteExpense,List<NameValuePair> listArrived) {
		expenseIntent = new Intent();
		expenseIntent.setAction(DeleteExpenseReceiver.DELETEEXPENSESUCCESSRECEIVER);
		expenseIntent.addCategory(Intent.CATEGORY_DEFAULT);

		Intent intent = new Intent(context, FullFlowService.class);
		intent.putExtra(EXTRA_NOTID, notDeleteExpense);
		deleteExpenseList = listArrived;
		context.startService(intent);
	}

	public static void ServiceDeleteMember(Context context, int notDeleteMember,List<NameValuePair> listArrived) {
		memberIntent = new Intent();
		memberIntent.setAction(DeleteMemberRecevier.DELETEMEMBERSUCCESSRECEIVER);
		memberIntent.addCategory(Intent.CATEGORY_DEFAULT);

		Intent intent = new Intent(context, FullFlowService.class);
		intent.putExtra(EXTRA_NOTID, notDeleteMember);
		deleteMemberList = listArrived;
		context.startService(intent);
	}

	public static void ServiceAddSettlement(Context context, int notAddsettle,List<NameValuePair> listArrived, ContentValues cv) {
		settleIntent = new Intent();
		settleIntent.setAction(AddSettlementRecevier.ADDSETTLEMENTSUCCESSRECEIVER);
		settleIntent.addCategory(Intent.CATEGORY_DEFAULT);

		Intent intent = new Intent(context, FullFlowService.class);
		intent.putExtra(EXTRA_NOTID, notAddsettle);
		addSettlementList = listArrived;
		addSettlementCv = cv;
		context.startService(intent);
	}

    public static void ServiceAddMembers(Context context, int notAddMembers,List<Member> selectedMembers, Integer groupIdServer, Integer groupId) {
        memberIntent = new Intent();
        memberIntent.setAction(AddMembersActivity.AddMemberRecevier.ADDMEMBERSUCCESSRECEIVER);
        memberIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent intent = new Intent(context, FullFlowService.class);
        intent.putExtra(EXTRA_NOTID, notAddMembers);
        addSelectedMembers = selectedMembers;
        membersCurrentGroupIdServer = groupIdServer;
        membersCurrentGroupId = groupId;
        activityContext = context;
        context.startService(intent);
    }

    public FullFlowService() {
        super("FullFlowService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DebugLogger.method("onHandleIntent");
        context = this;
        if (intent != null) {

            final int notId = intent.getIntExtra(EXTRA_NOTID, 0);
            DebugLogger.message(notId);
            switch (notId) {
                case AppConstants.NOT_ADDITEM:
                    System.out.println("NOT_ADDITEM");
                    handleAddItem(addItemList);
                    itemIntent.putExtra(EXTRA_NOTID, notId);
                    System.out.println("RUN");
                    sendBroadcast(itemIntent);
                    break;

                case AppConstants.NOT_DELETEITEM:
                    int groupId = intent.getIntExtra(DB.TItems.GROUP_FK, 0);
                    System.out.println("NOT_DELETEITEM");
                    handleDeleteItem(deleteItemList);
                    itemIntent.putExtra(EXTRA_NOTID, notId);
                    System.out.println("RUN");
                    sendBroadcast(itemIntent);
                    break;
			case AppConstants.NOT_ADDEXPENSE:
				handleAddExpense(addExpenseList);
				expenseIntent.putExtra(EXTRA_NOTID, notId);
				System.out.println("RUN");
				sendBroadcast(expenseIntent);
				break;
			case AppConstants.NOT_DELETEEXPENSE:
				expenseIntent.putExtra(EXTRA_NOTID, notId);
				System.out.println("RUN");
				handleDeleteExpense(deleteExpenseList);
				sendBroadcast(expenseIntent);
				break;
			case AppConstants.NOT_ADDSETTLE:
				settleIntent.putExtra(EXTRA_NOTID, notId);
				System.out.println("RUN");
				handleAddSettlement(addSettlementList);
				sendBroadcast(settleIntent);
				break;
                case AppConstants.NOT_ADDMEMBERS:
                    memberIntent.putExtra(EXTRA_NOTID, notId);
                    handleAddMembers(addSelectedMembers,membersCurrentGroupIdServer,membersCurrentGroupId);
                    sendBroadcast(memberIntent);
                    break;
			case AppConstants.NOT_DELETEMEMBER:
				memberIntent.putExtra(EXTRA_NOTID, notId);
				handleDeleteMember(deleteMemberList);
				sendBroadcast(memberIntent);
				break;

                default:
                    break;
            }
        }
    }

    private void handleAddMembers(List<Member> addSelectedMembers, int membersCurrentGroupIdServer, int membersCurrentGroupId) {
        ServiceHandler handler = new ServiceHandler();
        List<Long> addedIdsSuccess = new ArrayList<Long>();
        for(Member m:addSelectedMembers){
            System.out.println("Adding.. going on"+m);
            int memberIdServerAdded =0;
            ContentValues cv = new ContentValues();
            cv.put(DB.TMembers.MEMBER_ID_SERVER, 0);
            cv.put(DB.TMembers.NAME, m.getMemberName());
            cv.put(DB.TMembers.PHONE_NUMBER, m.getPhoneNumber());
            cv.put(DB.TMembers.GROUP_ID_FK, membersCurrentGroupIdServer);
            System.out.println(cv);

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair(DB.TMembers.NAME, m.getMemberName()));
            list.add(new BasicNameValuePair(DB.TMembers.PHONE_NUMBER, m.getPhoneNumber()));
            list.add(new BasicNameValuePair(DB.TMembers.GROUP_ID_FK,""+membersCurrentGroupIdServer));
            list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.ADD_MEMBER));
            System.out.println("Parameter List "+list);
            String jsonString = handler.makeServiceCall(AppConstants.URL_API,AppConstants.REQUEST_METHOD_POST, list);
            System.out.println("JsonString "+jsonString);
            JSONObject obj;
            if(jsonString != null){
                try {
                    obj = new JSONObject(jsonString);
                    if(!obj.getBoolean("error")){
                        memberIdServerAdded = obj.getInt("id");
                        long memberIdAdded = DBImpl.insert(DB.TMembers.TMEMBERS, cv);
                        addedIdsSuccess.add(memberIdAdded);
                        Member insertedMember = DBImpl.updateMemberServerId(memberIdAdded, memberIdServerAdded);
                        System.out.println("Member rows updated"+insertedMember);

                        String secretCode = MEMBERADDED+DELIMITER_BW+memberIdServerAdded;

                        String message = AppDataManager.getUser().getUserName() + " added member(s) " + AppDataManager.getCurrentGroup().getGroupName()+DELIMITER+secretCode;

                        List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
                        String ids = getMembersGCMIds(AppDataManager.getCurrentGroup().getGroupIdServer());

                        System.out.println("gmc dsssss"+ids);
                        if(ids != null && ids.length() != 0){
                            //String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
                            notificationList.add(new BasicNameValuePair("regId", ids));
                            notificationList.add(new BasicNameValuePair("message", message));
                            notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_ADDITEM)));// sending same as te because there is no diff in the server code
                            sendNotification(notificationList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{

            }
        }

        if(addedIdsSuccess.size() == addSelectedMembers.size()){
            memberIntent.putExtra(AppConstants.RESULT, true);
        }else{
            memberIntent.putExtra(AppConstants.RESULT, false);
        }
    }


    private void handleAddItem(List<NameValuePair> list) {
        System.out.println("items adding list "+list);
        int itemIdServerReceived = 0;
        ServiceHandler handler = new ServiceHandler();
        String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
        System.out.println("Received Response" + response);
        if(response != null){
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(!jsonObject.getBoolean("error")){
                    String itemName = list.get(0).getValue();
                    System.out.println("Item name" + itemName);
                    itemIntent.putExtra(DB.TItems.ITEM_NAME, itemName);
                    itemIntent.putExtra(AppConstants.RESULT, true);
                    long currentRow = DBImpl.insert(DB.TItems.TITEMS, addItemCv);
                    itemIdServerReceived = jsonObject.getInt("id");

                    long rowsUpdated = DBImpl.updateItemServerId(currentRow, itemIdServerReceived);
                    System.out.println("Item rows updated" + rowsUpdated);

                    String secretCode = ITEMADDED+DELIMITER_BW+itemIdServerReceived;
                    String message = AppDataManager.getUser().getUserName() + " added " + itemName + " in " + AppDataManager.getCurrentGroup().getGroupName()+DELIMITER+secretCode;

                    List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
                    String ids = getMembersGCMIds(AppDataManager.getCurrentGroup().getGroupIdServer());
                    System.out.println("gmc dsssss"+ids);
                    if(ids != null && ids.length() != 0){
                        //String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
                        notificationList.add(new BasicNameValuePair("regId", ids));
                        notificationList.add(new BasicNameValuePair("message", message));
                        notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_ADDITEM)));
                        sendNotification(notificationList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            itemIntent.putExtra(AppConstants.RESULT, false);
        }
    }

    private void handleDeleteItem(List<NameValuePair> list) {
        System.out.println("items deleting list " + list);
        ServiceHandler handler = new ServiceHandler();
        String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
        System.out.println("Received response"+response);
        if(response != null){
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(!jsonObject.getBoolean("error")){
                    int itemServerId = Integer.parseInt(list.get(0).getValue());
                    String itemName = DBImpl.getItem(itemServerId).getItemName();
                    int groupId = Integer.parseInt(list.get(1).getValue());
                    System.out.println("Item Server Id"+itemServerId);
                    System.out.println("Group Id"+groupId);
                    itemIntent.putExtra(DB.TItems.ITEM_ID_SERVER, itemServerId);
                    itemIntent.putExtra(AppConstants.RESULT, true);

                    System.out.println("Item server id "+itemServerId);

                    String secretCode = ITEMDELETED+DELIMITER_BW+itemServerId;

                    int rowsUpdated = DBImpl.removeItem(itemServerId, groupId);
                    System.out.println("Rows updated "+rowsUpdated);
                    String message = AppDataManager.getUser().getUserName() + " deleted " +itemName + " in " + AppDataManager.getCurrentGroup().getGroupName()+DELIMITER+secretCode;
                    System.out.println(AppDataManager.getCurrentGroup());
                    List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
                    String ids = getMembersGCMIds(AppDataManager.getCurrentGroup().getGroupIdServer());
                    System.out.println("gmc dsssss"+ids);
                    if(ids != null && ids.length() != 0){
                        //String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
                        notificationList.add(new BasicNameValuePair("regId", ids));
                        notificationList.add(new BasicNameValuePair("message", message));
                        notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_DELETEITEM)));
                        sendNotification(notificationList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            itemIntent.putExtra(AppConstants.RESULT, false);
        }
    }

	private void handleDeleteExpense(List<NameValuePair> list) {
		System.out.println("expense deleting list "+list);
		ServiceHandler handler = new ServiceHandler();
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(response != null){
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(!jsonObject.getBoolean("error")){
					long expenseServerId = Long.parseLong(list.get(0).getValue());
					int expenseId = Integer.parseInt(list.get(1).getValue());
					int expenseGroupId = Integer.parseInt(list.get(2).getValue());

					expenseIntent.putExtra(AppConstants.RESULT, true);

					int rowsUpdated = DBImpl.removeExpense(expenseId, expenseGroupId);
					System.out.println("Rows updated after deleting expense "+rowsUpdated);

					String secretCode = EXPENSEDELETED+DELIMITER_BW+expenseServerId;
                    
					String message = AppDataManager.getUser().getUserName() + " deleted an expense in " + App.getInstance().getCurr_group().getGroupName()+DELIMITER+secretCode;
					System.out.println(App.getInstance().getCurr_group());

					List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
					String ids = getMembersGCMIds(App.getInstance().getCurr_group().getGroupIdServer());
					System.out.println("gmc dsssss"+ids);
					if(ids != null && ids.length() != 0){
						//String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
						notificationList.add(new BasicNameValuePair("regId", ids));
						notificationList.add(new BasicNameValuePair("message", message));
						notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_DELETEITEM)));
						sendNotification(notificationList);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			expenseIntent.putExtra(AppConstants.RESULT, false);
		}
	}

	private void handleDeleteMember(List<NameValuePair> list) {
		System.out.println("member deleting list "+list);
		ServiceHandler handler = new ServiceHandler();
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(response != null){
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(!jsonObject.getBoolean("error")){
					int memberServerId = Integer.parseInt(list.get(0).getValue());
					int memberId = Integer.parseInt(list.get(1).getValue());

					int rowsUpdated = DBImpl.removeMember(memberId, App.getInstance().getCurr_group().getGroupIdServer());
					System.out.println("Rows updated "+rowsUpdated);

					String secretCode = MEMBERDELETED+DELIMITER_BW+memberServerId;

					String message = AppDataManager.getUser().getUserName() + " deleted member in " + App.getInstance().getCurr_group().getGroupName()+DELIMITER+secretCode;
					System.out.println(App.getInstance().getCurr_group());

					memberIntent.putExtra(AppConstants.RESULT, true);

					List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
					String ids = getMembersGCMIds(App.getInstance().getCurr_group().getGroupIdServer());
					System.out.println("gmc dsssss"+ids);
					if(ids != null && ids.length() != 0){
						//String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
						notificationList.add(new BasicNameValuePair("regId", ids));
						notificationList.add(new BasicNameValuePair("message", message));
						notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_DELETEITEM)));
						sendNotification(notificationList);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			memberIntent.putExtra(AppConstants.RESULT, false);
		}
	}

	private void handleAddExpense(List<NameValuePair> list) {
		System.out.println("expense adding list "+list);
		int expenseIdServerReceived = 0;
		ServiceHandler handler = new ServiceHandler();
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(response != null){
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(!jsonObject.getBoolean("error")){
					expenseIdServerReceived = jsonObject.getInt("id");
					expenseIntent.putExtra(AppConstants.RESULT, true);

					long expenseIdAdded = DBImpl.insert(TExpenses.TABLENAME, addExpenseCv);

					System.out.println("expense added id before sending notification" + expenseIdAdded);

					long rowsUpdated = DBImpl.updateEpenseServerId(expenseIdAdded, expenseIdServerReceived);
					System.out.println("Expense rows updated"+rowsUpdated);

					System.out.println("expenses "+DBImpl.getExpenses(App.getInstance().getCurr_group().getGroupId()));

					String secretCode = EXPENSEADDED+DELIMITER_BW+expenseIdServerReceived;

					
					String message = AppDataManager.getUser().getUserName() + " saved an expense in " + App.getInstance().getCurr_group().getGroupName()+DELIMITER+secretCode;
					System.out.println(App.getInstance().getCurr_group());
					
					List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
					String ids = getMembersGCMIds(App.getInstance().getCurr_group().getGroupIdServer());
					System.out.println("gmc dsssss"+ids);
					if(ids != null && ids.length() != 0){
						//String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
						notificationList.add(new BasicNameValuePair("regId", ids));
						notificationList.add(new BasicNameValuePair("message", message));
						notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_ADDITEM)));// sending same as te because there is no diff in the server code
						sendNotification(notificationList);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			expenseIntent.putExtra(AppConstants.RESULT, false);
		}
	}

	private void handleAddSettlement(List<NameValuePair> list) {
		System.out.println("settlement adding list "+list);
		int settlementIdServerReceived = 0;
		ServiceHandler handler = new ServiceHandler();
		String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(response != null){
			try {
				JSONObject jsonObject = new JSONObject(response);
				if(!jsonObject.getBoolean("error")){
					settlementIdServerReceived = jsonObject.getInt("id");

					settleIntent.putExtra(AppConstants.RESULT, true);

					
					long settlementIdAdded = DBImpl.insert(TSettlement.TABLENAME, addSettlementCv);
					

					System.out.println("settlement added id before sending notification" + settlementIdAdded);

					
					long rowsUpdated = DBImpl.updateSettlementServerId(settlementIdAdded, settlementIdServerReceived);
					System.out.println("Settlement rows updated"+rowsUpdated);
					
					
					System.out.println("settlements "+DBImpl.getSettlements(App.getInstance().getCurr_group().getGroupId()));
					

					String secretCode = SETTLEMENTADDED+DELIMITER_BW+settlementIdServerReceived;

					
					String message = AppDataManager.getUser().getUserName() + " made a settlement in " + App.getInstance().getCurr_group().getGroupName()+DELIMITER+secretCode;
					System.out.println(App.getInstance().getCurr_group());
					

					List<NameValuePair> notificationList = new ArrayList<NameValuePair>();
					String ids = getMembersGCMIds(App.getInstance().getCurr_group().getGroupIdServer());
					System.out.println("gmc dsssss"+ids);
					if(ids != null && ids.length() != 0){
						//String s = "APA91bGZu4HHe9qSHKCAdEBvOmtJd1B3hd5PamlRbkecaO_JusQjvnoCs1csl0LF4RnYQ65iqv6QLJjVk-kRoo-tWu4vY9gUiuT9ZLeACcPuQedWxOdw-WDUDjCFX5fnuFbqvz710YEl,APA91bHzw3Dmw125yjZj8LtYPgFyNMohkBHi2kOsVt0DDA03mFR2vPc1AJpDfGjYcuwWhBB9lZTgY-qQIcKzWVWj26cGB0IEwHQQR1bfZh6m1bqKtNo_mqHgoSx7a7AQezwalLbrV_ZuWv-njGBR4NlxvZbSkVSAHA";
						notificationList.add(new BasicNameValuePair("regId", ids));
						notificationList.add(new BasicNameValuePair("message", message));
						notificationList.add(new BasicNameValuePair("not_id", String.valueOf(AppConstants.NOT_ADDITEM)));// sending same as te because there is no diff in the server code
						sendNotification(notificationList);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			settleIntent.putExtra(AppConstants.RESULT, false);
		}
	}


    private String getMembersGCMIds(Integer groupIdServer) {
        StringBuilder s = new StringBuilder();
        for (Member member : DBImpl.getMembers(AppDataManager.getCurrentGroup().getGroupIdServer())) {
            String phoneNumber = member.getPhoneNumber();
            System.out.println(phoneNumber);
            if(!phoneNumber.equals(AppDataManager.getUser().getPhoneNumber())){
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair(DB.TMembers.PHONE_NUMBER, phoneNumber));
                list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_GCM_ID)));
                ServiceHandler handler = new ServiceHandler();
                String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
                System.out.println(phoneNumber+"..."+response);
                JSONObject object = null;
                if(response != null) {
                    try {
                        object = new JSONObject(response);
                        if (!object.getBoolean("error")) {
                            s.append(object.getString("gcm_regid")).append(",");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(s!=null && s.length() != 0)
            return s.substring(0, s.length()-1);

        return s.toString();
    }

    private String getNewMembersGCMId(Member m) {
        StringBuilder s = new StringBuilder();
        String phoneNumber = m.getPhoneNumber();
        System.out.println(phoneNumber);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(DB.TMembers.PHONE_NUMBER, phoneNumber));
        list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, String.valueOf(ServiceIDs.GET_GCM_ID)));
        ServiceHandler handler = new ServiceHandler();
        String response = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
        System.out.println(phoneNumber+"..."+response);
        JSONObject object = null;
        if(response != null){
            try {
                object = new JSONObject(response);
                if(!object.getBoolean("error")){
                    s.append(object.getString("gcm_regid")).append(",");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            s.append("");
        }
        if(s!=null && s.length() != 0)
            return s.substring(0, s.length()-1);

        return s.toString();
    }


    private void sendNotification(List<NameValuePair> list) {
        Log.d("RESO__", "Sending notification");
        ServiceHandler handler = new ServiceHandler();
        String response = handler.makeServiceCall("http://www.preetimodi.com/gem_api/gcm_server_php/notification.php", AppConstants.REQUEST_METHOD_POST, list);
        Log.d("RESO__", response);
        System.out.println("__________"+response);
    }
}
