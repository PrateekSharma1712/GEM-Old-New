package com.prateek.gem;

import android.content.Context;
import android.content.Intent;

public class AppConstants {
	
	public static final String CUSTOM_PREFERENCE = "gem_gcm";
	
	public static final String APP_NAME = "GEM";
	public static final String SPENT_FOR = "Spent For";
	public static final String SPENT_BY = "Spent By";
	public static final int DATE_WISE = 1;
	public static final int NAME_WISE = 2;
	
	public final static int REQUEST_CODE_FROM_GALLERY = 01;
	public final static int REQUEST_CODE_CLICK_IMAGE = 02;
	
	public final static String RUPEE_ICON = "Rs. ";
	
	
	public class JSONConstants{		
		public static final String MEMBERNAME = "membername";		
	}
	
	
	public static final int REQUEST_METHOD_GET = 1;
	public static final int REQUEST_METHOD_POST = 2;
	public static final String SERVICE_ID = "service_id";
	
	public static final String URL_API = "http://www.preetimodi.com/gem_api/gem.php";
	
	public static final String URL_IMAGES = "http://www.preetimodi.com/gem_api/images/";
	
	public static final String URL_UPLOADTOSERVER = "http://www.preetimodi.com/gem_api/uploadtoserver.php";
	
	public static final String FRAGMENT_ARGUMENTS_TITLE = "Title";
	public static final String FRAGMENT_ARGUMENTS_MESSAGE = "Message";
	
	public class ServiceIDs{
		public static final int GET_GROUPS_OF_MEMBER = 1;
		public static final int UPDATE_MEMBERS_COUNT = 2;
		public static final int GET_ADMIN = 3;
		public static final int ADD_GROUP = 4;
		public static final int GET_ITEMS = 5;
		public static final int ADD_ITEM = 6;
		public static final int ADD_MEMBER = 7;
		public static final int GET_MEMBERS = 8;
		public static final int ADD_USER = 9;
		public static final int GET_USERS = 10;
		public static final int DELETE_MEMBER = 11;
		public static final int DELETE_ITEM = 12;
		public static final int ADD_EXPENSE = 13;
		public static final int DELETE_EXPENSE = 14;
		public static final int GET_EXPENSES = 15;
		public static final int UPDATE_EXPENSE_TOTAL = 16;
		public static final int UPDATE_GROUP_NAME= 17;
		public static final int ADD_SETTLEMENT= 18;
		public static final int GET_SETTLEMENT_FOR_GROUP= 19;
		public static final int UPDATE_LASTMODIFIED= 20;
		public static final int GET_LASTMODIFIED= 21;
		public static final int CHECK_USER_REGISTERED= 23;
		public static final int GET_GCM_ID= 24;
		public static final int GET_SPECIFIC_ITEM = 25;
		public static final int GET_SPECIFIC_EXPENSE = 26;
		public static final int GET_SPECIFIC_SETTLEMENT = 27;
		public static final int GET_SPECIFIC_MEMBER = 28;
		public static final int GET_GROUP = 29;
		public static final int LOGIN = 30;
	}
	
	public class ConfirmConstants{
        public static final String CONFIRM_KEY = "confirmId";
        public static final int FROM_ADD_GROUP = 1;
        public static final int MEMBER_DELETE = 2;
        public static final int EXPENSE_DELETE = 3;
        public static final int ITEM_DELETE = 4;
        public static final int ITEM_ADD = 5;
        public static final int CONFIRM_SELECTED_ITEMS_LIST = 6;
        public static final int IMAGE_CAPTURE = 7;
	}
	
	
	public static final String ADMIN_ID = "adminId";
	public static final String ADMIN_NAME = "adminName";
	public static final String ADMIN_EMAIL = "adminEmail";
	public static final String ADMIN_PHONE = "adminPhone";
	public static final String ADMIN_PASSWORD = "adminPass";
	public static final String ONETIME_LOAD = "onetimeload";
	
	public static final String RESULT_BOOLEAN = "result";
	public static final String SUCCESS_RECEIVER = "com.prateek.gem.views.SUCCESS_RECEIVER";
	public static final String DB_RECEIVER = "com.prateek.gem.views.DB_RECEIVER";
	public static final String DB_RECEIVER2 = "com.prateek.gem.views.DB_RECEIVER2";
	
	
	
	
	
	// give your server registration url here
    public static final String SERVER_URL = "http://www.preetimodi.com/gem_api/gcm_server_php/register.php"; 

    // Google project id
    public static final String SENDER_ID = "713336931681"; 

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "Group Expense Manager";

    public static final String DISPLAY_MESSAGE_ACTION =
            "com.prateek.gem.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";
    public static final String RESULT = "result";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    
    
    public static final int NOT_ADDITEM = 1;
    public static final int NOT_DELETEITEM = 2;
    public static final int NOT_ADDEXPENSE = 3;
    public static final int NOT_DELETEEXPENSE = 4;
    public static final int NOT_ADDSETTLE = 5;
    public static final int NOT_ADDMEMBERS = 6;
    public static final int NOT_DELETEMEMBER = 7;
    public static final String DELIMITER = "@@";
    public static final String DELIMITER_BW = "##";
    public static final String ITEMADDED = "itemadded";
    public static final String ITEMDELETED = "itemdeleted";
    public static final String EXPENSEADDED = "expenseAdded";
    public static final String EXPENSEDELETED = "expenseDeleted";
    public static final String SETTLEMENTADDED = "settlementAdded";
    public static final String MEMBERADDED = "memberAdded";
    public static final String MEMBERDELETED = "memberDeleted";

    public static final String EMPTY_STRING = "";

    public static final String ALTER_GROUP_TABLE1 = "alter_group_table_1";

}


