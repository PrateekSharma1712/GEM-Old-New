package com.prateek.gem.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Items;
import com.prateek.gem.model.Member;
import com.prateek.gem.model.SettlementObject;
import com.prateek.gem.personal.expense.MyExpenses;
import com.prateek.gem.personal.expense.MyItems;

public class DBAdapter {

	public static final String DATABASE_NAME = "gemdatabase";
	public static final int DATABASE_VERSION = 30;
	
	public class TGroups{
		public static final String TGROUPS = "t_groups";		
		public static final String GROUPID = "group_id";
		public static final String GROUPID_SERVER = "group_id_server";
		public static final String GROUPNAME = "group_name";
		public static final String GROUPICON = "group_icon";
		public static final String DATEOFCREATION = "date_of_creation";
		public static final String TOTALOFEXPENSE = "total_of_expense";		
		public static final String TOTALMEMBERS = "total_members";
		public static final String ADMIN = "admin";
		
		public static final String CREATE_QUERY_GROUPS = "CREATE TABLE IF NOT EXISTS "+ TGROUPS+" (" 
				+ GROUPID
				+ " integer primary key autoincrement, "
				+ GROUPID_SERVER 
				+ " integer, "
				+ GROUPNAME
				+ " text not null, "
				+ GROUPICON
				+ " text not null, "
				+ DATEOFCREATION
				+ " text not null, "
				+ TOTALOFEXPENSE
				+ " float, "
				+ TOTALMEMBERS
				+ " int, "
				+ ADMIN
				+ " text)";
				
	}
	
	public class TMembers{
		public static final String TMEMBERS = "table_members";
		public static final String MEMBER_ID = "member_id";
		public static final String MEMBER_ID_SERVER = "member_id_server";
		public static final String GROUP_ID_FK = "group_fk";
		public static final String NAME = "member_name";
		public static final String PHONE_NUMBER = "phone_number";		
		public static final String GCM_REG_NO = "gcm_reg_no";		
		public static final String CREATE_QUERY_MEMBERS = "CREATE TABLE IF NOT EXISTS "+ TMEMBERS+" (" 
				+ MEMBER_ID
				+ " integer primary key autoincrement, "
				+ MEMBER_ID_SERVER 
				+ " integer, "
				+ GROUP_ID_FK
				+ " integer, "
				+ NAME
				+ " text not null, "
				+ PHONE_NUMBER
				+ " text not null)";
	}
	
	public class TItems{
		public static final String TITEMS = "t_items";
		public static final String ITEM_ID = "item_id";
		public static final String ITEM_ID_SERVER = "item_id_server";
		public static final String ITEM_NAME = "item_name";
		public static final String GROUP_FK = "group_fk";
		public static final String CATEGORY = "category";
		public static final String CREATE_QUERY_ITEMS = "CREATE TABLE IF NOT EXISTS "+ TITEMS+" (" 
				+ ITEM_ID
				+ " integer primary key autoincrement, "
				+ ITEM_ID_SERVER 
				+ " integer, "
				+ ITEM_NAME
				+ " text not null, "
				+ GROUP_FK
				+ " int, "
				+ CATEGORY
				+ " text not null)";
		
	}
	
	public class TExpenses{
		public static final String TABLENAME = "t_expenses";
		public static final String EXPENSE_ID = "expense_id";
		public static final String EXPENSE_ID_SERVER = "expense_id_server";
		public static final String GROUP_ID_FK = "group_id_fk";
		public static final String DATE_OF_EXPENSE = "date_of_expense";
		public static final String AMOUNT = "amount";
		public static final String SHARE = "share";
		public static final String ITEM = "item";
		public static final String EXPENSE_BY = "expense_by";
		public static final String PARTICIPANTS = "participants";
		public static final String CREATE_QUERY_EXPENSES = "CREATE TABLE IF NOT EXISTS " +
				TABLENAME +
				"(" +
				EXPENSE_ID +
				" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				EXPENSE_ID_SERVER +
				" integer, " +
				GROUP_ID_FK +
				" INTEGER, " +
				DATE_OF_EXPENSE +
				" TEXT, " +
				AMOUNT +
				" FLOAT, " +
				SHARE +
				" FLOAT, " +
				ITEM +
				" TEXT, " +
				EXPENSE_BY +
				" TEXT, " +
				PARTICIPANTS +
				" TEXT)";		
	}
	
	public class TSettlement{
		public static final String TABLENAME = "t_settlement";
		public static final String SET_ID = "set_id";
		public static final String SET_ID_SERVER = "set_id_server";
		public static final String GROUP_ID_FK = "group_id_fk";
		public static final String GIVEN_BY = "givenby";
		public static final String TAKEN_BY = "takenby";
		public static final String AMOUNT = "amount";
		public static final String DATE = "date";
		public static final String CREATE_QUERY_SETTLEMENT = "CREATE TABLE IF NOT EXISTS " +
				TABLENAME +
				"(" +
				SET_ID +
				" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SET_ID_SERVER +
				" integer, " +
				GROUP_ID_FK +
				" INTEGER, " +
				GIVEN_BY +
				" TEXT NOT NULL, " +
				TAKEN_BY +
				" TEXT NOT NULL, " +
				AMOUNT +
				" FLOAT, " +
				DATE +				
				" TEXT NOT NULL)";		
	}	
		
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private Context context;
	
	public DBAdapter(Context context) {		
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}
	
	public static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(TGroups.CREATE_QUERY_GROUPS);
				db.execSQL(TMembers.CREATE_QUERY_MEMBERS);
				db.execSQL(TItems.CREATE_QUERY_ITEMS);
				db.execSQL(TExpenses.CREATE_QUERY_EXPENSES);
				db.execSQL(TSettlement.CREATE_QUERY_SETTLEMENT);
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldDatabase, int newDatabase) {
			Log.w("DATABSE_TAG", "Database updated from "+ oldDatabase + " to "+newDatabase);			
			db.execSQL("DROP TABLE IF EXISTS " +TExpenses.TABLENAME);
			db.execSQL("DROP TABLE IF EXISTS " +TGroups.TGROUPS);
			db.execSQL("DROP TABLE IF EXISTS " +TMembers.TMEMBERS);			
			db.execSQL("DROP TABLE IF EXISTS " +TItems.TITEMS);		
			db.execSQL("DROP TABLE IF EXISTS " +TSettlement.TABLENAME);
			onCreate(db);
		}
		
	}
	
	public DBAdapter open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		db.execSQL(TGroups.CREATE_QUERY_GROUPS);
		db.execSQL(TMembers.CREATE_QUERY_MEMBERS);
		db.execSQL(TItems.CREATE_QUERY_ITEMS);
		db.execSQL(TExpenses.CREATE_QUERY_EXPENSES);
		db.execSQL(TSettlement.CREATE_QUERY_SETTLEMENT);
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	/********* GROUP RELATED METHODS STARTS ************/
	
	public long addGroup(String groupName, String path, String date){
		ContentValues initialValues = new ContentValues();
		initialValues.put(TGroups.GROUPNAME, groupName);
		initialValues.put(TGroups.GROUPICON, path);
		initialValues.put(TGroups.DATEOFCREATION, date);
		initialValues.put(TGroups.TOTALMEMBERS, 0);
		initialValues.put(TGroups.TOTALOFEXPENSE, 0);		
		return db.insert(TGroups.TGROUPS, null, initialValues);
	}
	
	public List<Group> getGroups(){
		List<Group> groups = new ArrayList<Group>(); 
		Cursor c = db.query(TGroups.TGROUPS, new String[]{
				TGroups.GROUPID,
				TGroups.GROUPID_SERVER,
				TGroups.GROUPNAME,
				TGroups.GROUPICON,
				TGroups.TOTALMEMBERS,
				TGroups.DATEOFCREATION,
				TGroups.ADMIN,				
				TGroups.TOTALOFEXPENSE
				}, null, null, null, null, null);
		if(c.moveToFirst()){
			do{
				Group group = new Group();
				group.setGroupId(c.getInt(c.getColumnIndex(TGroups.GROUPID)));
				group.setGroupIdServer(c.getInt(c.getColumnIndex(TGroups.GROUPID_SERVER)));
				group.setGroupName(c.getString(c.getColumnIndex(TGroups.GROUPNAME)));
				group.setGroupIcon(Uri.parse(c.getString(c.getColumnIndex(TGroups.GROUPICON))));
				group.setDate(c.getString(c.getColumnIndex(TGroups.DATEOFCREATION)));
				group.setMembersCount(c.getInt(c.getColumnIndex(TGroups.TOTALMEMBERS)));				
				group.setTotalOfExpense(c.getFloat(c.getColumnIndex(TGroups.TOTALOFEXPENSE)));
				group.setAdmin(c.getString(c.getColumnIndex(TGroups.ADMIN)));
				groups.add(group);
			}while(c.moveToNext());
		}
			
		return groups;
	}
	
	public Group getGroup(int groupId){
		Group group = null;
		Cursor c = db.query(TGroups.TGROUPS, null, TGroups.GROUPID_SERVER +" = "+groupId, null, null, null, null);
		if(c.moveToFirst()){
			do{
				group = new Group();
				group.setGroupId(c.getInt(c.getColumnIndex(TGroups.GROUPID)));
				group.setGroupIdServer(c.getInt(c.getColumnIndex(TGroups.GROUPID_SERVER)));
				group.setGroupName(c.getString(c.getColumnIndex(TGroups.GROUPNAME)));
				group.setGroupIcon(Uri.parse(c.getString(c.getColumnIndex(TGroups.GROUPICON))));
				group.setDate(c.getString(c.getColumnIndex(TGroups.DATEOFCREATION)));
				group.setAdmin(c.getString(c.getColumnIndex(TGroups.ADMIN)));
				group.setMembersCount(c.getInt(c.getColumnIndex(TGroups.TOTALMEMBERS)));
				group.setTotalOfExpense(c.getFloat(c.getColumnIndex(TGroups.TOTALOFEXPENSE)));
				
			}while(c.moveToNext());
		}
		return group;		
	}
	
	public int updateGroup(Integer groupId,String groupTitle){
		int rowAffected = 0;
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(TGroups.GROUPNAME, groupTitle);
		rowAffected = db.update(TGroups.TGROUPS, updatedValues, TGroups.GROUPID +" = "+groupId, null);
		return rowAffected;
	}
	
	public int updateGroup(Integer groupId,float amount,String field){
		int rowAffected = 0;
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(field, amount);
		rowAffected = db.update(TGroups.TGROUPS, updatedValues, TGroups.GROUPID +" = "+groupId, null);
		return rowAffected;
	}
	
	public int updateGroupMembersCount(Integer groupId,int membersCount){
		int rowAffected = 0;
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(TGroups.TOTALMEMBERS, membersCount);
		rowAffected = db.update(TGroups.TGROUPS, updatedValues, TGroups.GROUPID +" = "+groupId, null);
		return rowAffected;
	}
	
	public int deleteGroup(Integer groupId){
		int rowAffected = 0;		
		rowAffected = db.delete(TGroups.TGROUPS,TGroups.GROUPID +" = "+groupId, null);
		return rowAffected;
	}
	/*********** GROUP RELATED METHODS ENDS ************/
	
	/*********** MEMBER REALTED METHODS STARTS *********/
	public long addMembers(Member member,int groupId){
		ContentValues iniContentValues = new ContentValues();
		iniContentValues.put(TMembers.GROUP_ID_FK, groupId);
		iniContentValues.put(TMembers.NAME, member.getMemberName());
		iniContentValues.put(TMembers.PHONE_NUMBER, member.getPhoneNumber());		
		return db.insert(TMembers.TMEMBERS, null, iniContentValues);
	}
	
	public List<Member> getMembersOfGroup(int groupId){
		List<Member> members = new ArrayList<Member>();		
		Cursor c = db.query(TMembers.TMEMBERS, new String[]{
				TMembers.GROUP_ID_FK,
				TMembers.MEMBER_ID,
				TMembers.MEMBER_ID_SERVER,
				TMembers.NAME,
				TMembers.PHONE_NUMBER,				
		}, TMembers.GROUP_ID_FK + " = "+groupId , null, null, null, null);
		if(c.moveToFirst()){
			do{
				Member member = new Member();
				member.setGroupIdFk(c.getInt(0));
				member.setMemberId(c.getInt(1));
				member.setMemberIdServer(c.getInt(2));
				member.setMemberName(c.getString(3));
				member.setPhoneNumber(c.getString(4));				
				members.add(member);
			}while(c.moveToNext());
		}
		
		return members;
	}
	
	public int deleteMember(int memberId){
		int rowAffected = db.delete(TMembers.TMEMBERS, TMembers.MEMBER_ID +" = "+memberId, null);
		return rowAffected;
	}
	
	public int deleteMemberByGroup(int groupId){
		int rowAffected = db.delete(TMembers.TMEMBERS, TMembers.GROUP_ID_FK +" = "+groupId, null);
		return rowAffected;
	}
	
	public Member updateMemberServerId(long memberId, int memberIdServerAdded) {
		ContentValues cv= new ContentValues();
		Member member = new Member();
		cv.put(TMembers.MEMBER_ID_SERVER, memberIdServerAdded);		
		db.update(TMembers.TMEMBERS, cv, TMembers.MEMBER_ID+ " = " + memberId, null);
		Cursor c = db.query(TMembers.TMEMBERS, new String[]{
				TMembers.GROUP_ID_FK,
				TMembers.MEMBER_ID,
				TMembers.MEMBER_ID_SERVER,
				TMembers.NAME,
				TMembers.PHONE_NUMBER}, TMembers.MEMBER_ID +" = "+memberId, null, null, null, null);
		if(c.moveToFirst()){
			do{
				member.setGroupIdFk(c.getInt(0));
				member.setMemberId(c.getInt(1));
				member.setMemberIdServer(c.getInt(2));
				member.setMemberName(c.getString(3));
				member.setPhoneNumber(c.getString(4));
			}while(c.moveToNext());
		}
		return member;
		
	}
	
	public int removeMember(int memberId,int groupId){
		int rowAffected = db.delete(TMembers.TMEMBERS, TMembers.MEMBER_ID +" = "+ memberId +" AND "+TMembers.GROUP_ID_FK + " = " + groupId, null);
		return rowAffected;
	}
	
	public int removeMember(int memberServerId){
		int rowAffected = db.delete(TMembers.TMEMBERS, TMembers.MEMBER_ID_SERVER +" = "+ memberServerId, null);
		return rowAffected;
	}
	
	public Member getMemberByServerId(int memberServerId) {
		Member member = new Member();
		Cursor c = db.query(TMembers.TMEMBERS, null, TMembers.MEMBER_ID_SERVER +" = "+ memberServerId, null, null, null, null);
		if (c.moveToFirst()) {
			do{
				member.setMemberId(c.getInt(c.getColumnIndex(TMembers.MEMBER_ID)));
				member.setMemberIdServer(c.getInt(c.getColumnIndex(TMembers.MEMBER_ID_SERVER)));
				member.setMemberName(c.getString(c.getColumnIndex(TMembers.NAME)));
				member.setPhoneNumber(c.getString(c.getColumnIndex(TMembers.PHONE_NUMBER)));
				member.setGroupIdFk(c.getInt(c.getColumnIndex(TMembers.GROUP_ID_FK)));
			}while(c.moveToNext());
		}
		return member;
		// TODO Auto-generated method stub
		
	}
	
	public int removeMemberByGroupServerId(int groupServerId) {
		int rowAffected = db.delete(TMembers.TMEMBERS, TMembers.GROUP_ID_FK +" = "+groupServerId, null);
		return rowAffected;
		
	}
	/************ MEMBER REALTED METHODS ENDS **********/
	
	/************ ITEMS RELATED METHODS STARTS *********/
	public List<Items> getItems(int groupId){
		List<Items> items = new ArrayList<Items>();
		Cursor c = db.query(TItems.TITEMS, null, TItems.GROUP_FK + " = "+groupId, null, null, null,TItems.ITEM_NAME + " ASC ");
		if(c.moveToFirst()){
			do{
				Items item = new Items();
				item.setGroupFK(c.getInt(c.getColumnIndex(TItems.GROUP_FK)));
				item.setId(c.getInt(c.getColumnIndex(TItems.ITEM_ID)));
				item.setIdServer(c.getInt(c.getColumnIndex(TItems.ITEM_ID_SERVER)));
				item.setItemName(c.getString(c.getColumnIndex(TItems.ITEM_NAME)));
				item.setCategory(c.getString(c.getColumnIndex(TItems.CATEGORY)));
				items.add(item);				
			}while(c.moveToNext());
		}
		return items;
	}
	
	public int getItemServerId(String itemName,int groupId){
		Cursor c = db.query(TItems.TITEMS, new String[]{TItems.ITEM_ID_SERVER}, TItems.ITEM_NAME +" = '"+ itemName +"' AND "+TItems.GROUP_FK + " = " + groupId, null, null, null, null);
		if(c.moveToFirst()){
			do{
				return c.getInt(c.getColumnIndex(TItems.ITEM_ID_SERVER));
			}while(c.moveToNext());
		}
		return 0;
		
	}
	
	public int removeItem(String itemName,int groupId){
		int rowAffected = db.delete(TItems.TITEMS, TItems.ITEM_NAME +" = '"+ itemName +"' AND "+TItems.GROUP_FK + " = " + groupId, null);
		return rowAffected;
	}
	
	public int removeItem(int itemServerId){
		int rowAffected = db.delete(TItems.TITEMS, TItems.ITEM_ID_SERVER +" = "+itemServerId, null);
		return rowAffected;
	}
	
	public int updateItemServerId(long itemId,int itemserverId){
		ContentValues cv= new ContentValues();
		cv.put(TItems.ITEM_ID_SERVER, itemserverId);		
		return db.update(TItems.TITEMS, cv, TItems.ITEM_ID + " = " + itemId, null);
		
	}
	
	public Items getItemByServerId(int itemServerId){
		Items items = new Items();
		Cursor c = db.query(TItems.TITEMS, null, TItems.ITEM_ID_SERVER+" = "+itemServerId, 
				null, null, null, null);
		if(c.moveToFirst()){
			do{
				items.setItemName(c.getString(c.getColumnIndex(TItems.ITEM_NAME)));
				items.setGroupFK(c.getInt(c.getColumnIndex(TItems.GROUP_FK)));
			}while(c.moveToNext());
		}
		return items;
		
	}
	/************ ITEMS RELATED METHODS ENDS ***********/
	/******** SETTLEMENTS RELATED METHODS STARTS *******/
	public List<SettlementObject> getSettlements(int groupId) {
		List<SettlementObject> settlements = new ArrayList<SettlementObject>();
		Cursor c = db.query(TSettlement.TABLENAME, null, TSettlement.GROUP_ID_FK + " = " +groupId, null, null, null,null);
		System.out.println("++++++++++++++++Total settlemnts"+c.getCount());
		if(c.moveToFirst()){
			do{
				SettlementObject settlement = new SettlementObject();
				settlement.setSetId(c.getInt(c.getColumnIndex(TSettlement.SET_ID)));
				settlement.setSetIdServer(c.getInt(c.getColumnIndex(TSettlement.SET_ID_SERVER)));
				settlement.setGroupId(c.getInt(c.getColumnIndex(TSettlement.GROUP_ID_FK)));
				settlement.setGivenBy(c.getString(c.getColumnIndex(TSettlement.GIVEN_BY)));
				settlement.setTakenBy(c.getString(c.getColumnIndex(TSettlement.TAKEN_BY)));
				settlement.setAmount(c.getFloat(c.getColumnIndex(TSettlement.AMOUNT)));
				
				settlements.add(settlement);				
			}while(c.moveToNext());
		}
		return settlements;
	
	}
	
	public int removeItemyGroupServerId(int groupServerId) {
		int rowAffected = db.delete(TItems.TITEMS, TItems.GROUP_FK +" = "+groupServerId, null);
		return rowAffected;
	}
	
	/********** SETTLEMENTS RELATED METHODS ENDS *******/
	/************ EXPENSE RELATED METHODS ENDS *********/
	public List<ExpenseOject> getExpenses(int groupId){
		List<ExpenseOject> expenses = new ArrayList<ExpenseOject>();
		Cursor expensesCursor = db.query(TExpenses.TABLENAME, null, 
				TExpenses.GROUP_ID_FK +" = "+groupId, null, null, null, null);
		System.out.println(expensesCursor.getCount());
		if(expensesCursor.moveToFirst()){
			do{
				ExpenseOject expenseObject = new ExpenseOject();
				expenseObject.setExpenseId(expensesCursor.getInt(expensesCursor.getColumnIndex(TExpenses.EXPENSE_ID)));
				expenseObject.setExpenseIdServer(expensesCursor.getInt(expensesCursor.getColumnIndex(TExpenses.EXPENSE_ID_SERVER)));
				expenseObject.setDate(expensesCursor.getLong(expensesCursor.getColumnIndex(TExpenses.DATE_OF_EXPENSE)));
				expenseObject.setAmount(expensesCursor.getFloat(expensesCursor.getColumnIndex(TExpenses.AMOUNT)));
				expenseObject.setShare(expensesCursor.getFloat(expensesCursor.getColumnIndex(TExpenses.SHARE)));
				expenseObject.setItem(expensesCursor.getString(expensesCursor.getColumnIndex(TExpenses.ITEM)));
				expenseObject.setExpenseBy(expensesCursor.getString(expensesCursor.getColumnIndex(TExpenses.EXPENSE_BY)));
				expenseObject.setParticipants(expensesCursor.getString(expensesCursor.getColumnIndex(TExpenses.PARTICIPANTS)));
				expenseObject.setGroupId(expensesCursor.getInt(expensesCursor.getColumnIndex(TExpenses.GROUP_ID_FK)));
				expenses.add(expenseObject);
			}while(expensesCursor.moveToNext());
		}
		return expenses;
		
	}
	
	public int updateEpenseServerId(long expenseId,int expenseserverId){
		ContentValues cv= new ContentValues();
		cv.put(TExpenses.EXPENSE_ID_SERVER, expenseserverId);		
		return db.update(TExpenses.TABLENAME, cv, TExpenses.EXPENSE_ID+ " = " + expenseId, null);
	}
	
	public int getExpenseServerId(int expenseId,int groupId){
		Cursor c = db.query(TExpenses.TABLENAME, new String[]{TExpenses.EXPENSE_ID_SERVER}, TExpenses.EXPENSE_ID +" = "+ expenseId +" AND "+TExpenses.GROUP_ID_FK + " = " + groupId, null, null, null, null);
		if(c.moveToFirst()){
			do{
				return c.getInt(c.getColumnIndex(TExpenses.EXPENSE_ID_SERVER));
			}while(c.moveToNext());
		}
		return 0;		
	}
	
	public ExpenseOject getExpenseByServerId(int expenseIdServer){
		ExpenseOject expense = new ExpenseOject();
		Cursor c = db.query(TExpenses.TABLENAME, null, TExpenses.EXPENSE_ID_SERVER +" = "+ expenseIdServer, null, null, null, null);
		if(c.moveToFirst()){
			do{
				expense.setAmount(c.getFloat(c.getColumnIndex(TExpenses.AMOUNT)));
				expense.setDate(c.getLong(c.getColumnIndex(TExpenses.DATE_OF_EXPENSE)));
				expense.setExpenseBy(c.getString(c.getColumnIndex(TExpenses.EXPENSE_BY)));
				expense.setExpenseId(c.getInt(c.getColumnIndex(TExpenses.EXPENSE_ID)));
				expense.setExpenseIdServer(c.getInt(c.getColumnIndex(TExpenses.EXPENSE_ID_SERVER)));
				expense.setGroupId(c.getInt(c.getColumnIndex(TExpenses.GROUP_ID_FK)));
				expense.setItem(c.getString(c.getColumnIndex(TExpenses.ITEM)));
				expense.setParticipants(c.getString(c.getColumnIndex(TExpenses.PARTICIPANTS)));
				expense.setShare(c.getFloat(c.getColumnIndex(TExpenses.SHARE)));
			}while(c.moveToNext());
		}
		return expense;		
	}
	
	public int removeExpense(int expenseId,int groupId){
		int rowAffected = db.delete(TExpenses.TABLENAME, TExpenses.EXPENSE_ID +" = "+ expenseId +" AND "+TExpenses.GROUP_ID_FK + " = " + groupId, null);
		return rowAffected;
	}
	
	public int removeExpense(int expenseServerId){
		int rowAffected = db.delete(TExpenses.TABLENAME, TExpenses.EXPENSE_ID_SERVER +" = "+ expenseServerId, null);
		return rowAffected;
	}
	
	public float getExpenseTotal(int groupId){
		float totalOfExpense = 0f;
		String query = "SELECT SUM("+TExpenses.AMOUNT+") FROM "+TExpenses.TABLENAME +" WHERE "+TExpenses.GROUP_ID_FK + " = " + groupId;
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			do{
				totalOfExpense = cursor.getFloat(0);
			}while(cursor.moveToNext());
		}
		return totalOfExpense;
	}
	
	public float getExpenseTotalByMember(int groupId,String member){
		float totalOfExpense = 0f;
		
		
		String query = "SELECT SUM("+TExpenses.AMOUNT+") FROM "+TExpenses.TABLENAME +" WHERE "+TExpenses.GROUP_ID_FK + " = " + groupId +" AND "+TExpenses.EXPENSE_BY+" = '"+member+"' COLLATE NOCASE";
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			do{
				totalOfExpense = cursor.getFloat(0);
			}while(cursor.moveToNext());
		}
		return totalOfExpense;
	}
	/************ EXPENSE RELATED METHODS ENDS *********/
	/********* SETTLEMENT RELATED METHODS STARTS *******/
	public int updateSettlementServerId(long settlementId,int settlementserverId){
		ContentValues cv= new ContentValues();
		cv.put(TSettlement.SET_ID_SERVER, settlementserverId);		
		return db.update(TSettlement.TABLENAME, cv, TSettlement.SET_ID+ " = " + settlementId, null);
	}
	/********** SETTLEMENT RELATED METHODS ENDS ********/
	
	
	
	/*************** COMMON FUNCTIONS STARTS ***********/
	public long insert(String tableName,ContentValues values){
		return db.insert(tableName, null, values);
	}
	/**************** COMMON FUNCTIONS ENDS ************/

	/*************** COMMON FUNCTIONS STARTS ***********/
	public boolean deleteAllStuff(int groupId,boolean dodeletegroup){
		boolean result = false;
		int memUpdated = db.delete(TMembers.TMEMBERS, TMembers.GROUP_ID_FK +" = "+groupId, null);
		int settUpdated = db.delete(TSettlement.TABLENAME, TSettlement.GROUP_ID_FK +" = "+groupId, null);
		int itemUpdated = db.delete(TItems.TITEMS, TItems.GROUP_FK +" = "+groupId, null);
		int expenseUpdated = db.delete(TExpenses.TABLENAME, TExpenses.GROUP_ID_FK +" = "+groupId, null);
		if(dodeletegroup){
			int groupUpdated = db.delete(TGroups.TGROUPS, TGroups.GROUPID_SERVER +" = "+groupId, null);
		}
		
		return result;
		
	}
	/**************** COMMON FUNCTIONS ENDS ************/
	
	/**************** PERSONAL EXPENSES ***************/
	public static final String TABLE_ITEMS = "table_items";
    public static final String TABLE_EXPENSES = "table_expenses";

    public static final String ITEMS_ID = "item_id";
    public static final String ITEMS_NAME = "item_name";
    public static final String ITEMS_CATEGORY = "item_category";

    public static final String EXP_ID = "exp_id";
    public static final String EXP_DATE = "exp_date";
    public static final String EXP_AMOUNT = "exp_amount";
    public static final String EXP_ITEM = "item_id";
    public static final String EXP_MODE = "exp_mode";
    
    public static final String CREATE_ITEMS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEMS +
            "(" +
            ITEMS_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ITEMS_NAME +
            " TEXT NOT NULL, " +
            ITEMS_CATEGORY +
            " TEXT NOT NULL)";

    public static final String CREATE_EXPENSE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_EXPENSES +
            "(" +
            EXP_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EXP_DATE +
            " TEXT NOT NULL, " +
            EXP_ITEM +
            " INTEGER, " +
            EXP_AMOUNT +
            " FLOAT, " +
            EXP_MODE +
            " TEXT NOT NULL)";
    
    public void cretateTables() {
    	db.execSQL(CREATE_ITEMS);
    	db.execSQL(CREATE_EXPENSE);
    }
    
    public List<MyItems> getItems(String category) {
    	List<MyItems> items = new ArrayList<MyItems>();
    	Cursor c = null; 
    	if(category != null) {
    		c = db.query(TABLE_ITEMS, new String[]{
	    			ITEMS_ID,
	    			ITEMS_NAME,
	    			ITEMS_CATEGORY
	    	}, ITEMS_CATEGORY + " = '"+category+"'", null, null, null, null);
    	}
    	else {
    		c = db.query(TABLE_ITEMS, new String[]{
	    			ITEMS_ID,
	    			ITEMS_NAME,
	    			ITEMS_CATEGORY
	    	}, category, null, null, null, null);
    	}
    	
    	if(c.moveToFirst()) {
    		do {
    			MyItems item = new MyItems();
    			item.setItemId(c.getInt(c.getColumnIndex(ITEMS_ID)));
    			item.setItemName(c.getString(c.getColumnIndex(ITEMS_NAME)));
    			item.setCategory(c.getString(c.getColumnIndex(ITEMS_CATEGORY)));
    			items.add(item);
    		}while(c.moveToNext());
    	}
    	
    	return items;
    }
    
    public List<MyExpenses> getExpenses() {
    	List<MyExpenses> expenses = new ArrayList<MyExpenses>();
    	Cursor c = db.query(TABLE_EXPENSES, new String[]{
    			EXP_ID,
    			EXP_ITEM,
    			EXP_AMOUNT,
    			EXP_MODE,
    			EXP_DATE
    	}, null, null, null, null, null);
    	
    	if(c.moveToFirst()) {
    		do {
    			MyExpenses expense = new MyExpenses();
    			expense.setExpenseId(c.getInt(c.getColumnIndex(EXP_ID)));
    			expense.setExpDate(c.getString(c.getColumnIndex(EXP_DATE)));
    			expense.setItemId(c.getInt(c.getColumnIndex(EXP_ITEM)));
    			expense.setMode(c.getString(c.getColumnIndex(EXP_MODE)));
    			expense.setAmount(c.getFloat(c.getColumnIndex(EXP_AMOUNT)));
    			expenses.add(expense);
    		}while(c.moveToNext());
    	}
    	
    	return expenses;
    }
}
