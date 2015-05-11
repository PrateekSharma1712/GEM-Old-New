package com.prateek.gem.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.prateek.gem.AppConstants;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.Items;
import com.prateek.gem.model.Member;
import com.prateek.gem.model.SettlementObject;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 8/12/14.
 */
public class DBImpl extends DB {
    private static DBImpl instanceHelper = null;
    private static Context context = null;

    public DBImpl(Context ctx) {
        super(ctx);
        context = ctx;
    }

    public static DBImpl sharedHelper() {
        if (instanceHelper == null) {
            instanceHelper = new DBImpl(context);
        }
        return instanceHelper;
    }

    /**
     * Method to clear all the records from all the tables
     *
     * @return none
     */
    public void clearAllTables() {
        DebugLogger.method("DBImpl :: clearAllTables");
        clearTables();
    }

    public void cretateTables() {
        getDatabase().execSQL(CREATE_ITEMS);
        getDatabase().execSQL(CREATE_EXPENSE);
    }

    /**
     * Method to get all groups of the user
     *
     * @return {ArrayList of Groups}
     */
    public static ArrayList<Group> getGroups() {
        ArrayList<Group> mGroups = new ArrayList<Group>();
        Cursor c = getDatabase().query(TGroups.TGROUPS, new String[]{
                TGroups.GROUPID,
                TGroups.GROUPID_SERVER,
                TGroups.GROUPNAME,
                TGroups.GROUPICON,
                TGroups.TOTALMEMBERS,
                TGroups.DATEOFCREATION,
                TGroups.ADMIN,
                TGroups.TOTALOFEXPENSE,
                TGroups.LASTUPDATEDON
        }, null, null, null, null, null);

        if(c.moveToFirst()){
            do{
                Group group = new Group();
                group.setGroupId(c.getInt(c.getColumnIndex(TGroups.GROUPID)));
                group.setGroupIdServer(c.getInt(c.getColumnIndex(TGroups.GROUPID_SERVER)));
                group.setGroupName(c.getString(c.getColumnIndex(TGroups.GROUPNAME)));
                group.setGroupIcon(c.getString(c.getColumnIndex(TGroups.GROUPICON)));
                group.setDate(c.getString(c.getColumnIndex(TGroups.DATEOFCREATION)));
                group.setMembersCount(c.getInt(c.getColumnIndex(TGroups.TOTALMEMBERS)));
                group.setTotalOfExpense(c.getFloat(c.getColumnIndex(TGroups.TOTALOFEXPENSE)));
                group.setAdmin(c.getString(c.getColumnIndex(TGroups.ADMIN)));
                String lastUpdatedString = c.getString(c.getColumnIndex(TGroups.LASTUPDATEDON));

                group.setLastUpdatedOn((lastUpdatedString != null) ? Long.parseLong(lastUpdatedString) : 0);
                mGroups.add(group);
            }while(c.moveToNext());
        }

        //Collections.sort(mGroups);

        return mGroups;

    }

    public static long addGroup(Group group) {
        ContentValues cv = new ContentValues();
        cv.put(TGroups.GROUPID_SERVER, group.getGroupIdServer());
        cv.put(TGroups.GROUPNAME, group.getGroupName());
        cv.put(TGroups.GROUPICON, group.getGroupIcon().toString());
        cv.put(TGroups.DATEOFCREATION, group.getDate());
        cv.put(TGroups.TOTALMEMBERS, group.getMembersCount());
        cv.put(TGroups.TOTALOFEXPENSE, group.getTotalOfExpense());
        cv.put(TGroups.ADMIN, group.getAdmin());
        return insert(TGroups.TGROUPS,cv);
    }

    public static float getExpenseTotal(int groupId){
        float totalOfExpense = 0f;
        String query = "SELECT SUM("+TExpenses.AMOUNT+") FROM "+TExpenses.TABLENAME +" WHERE "+TExpenses.GROUP_ID_FK + " = " + groupId;
        Cursor cursor = getDatabase().rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                totalOfExpense = cursor.getFloat(0);
            }while(cursor.moveToNext());
        }
        return totalOfExpense;
    }

    public static float getExpenseTotalByMember(int groupId,String member){
        float totalOfExpense = 0f;


        String query = "SELECT SUM("+TExpenses.AMOUNT+") FROM "+TExpenses.TABLENAME +" WHERE "+TExpenses.GROUP_ID_FK + " = " + groupId +" AND "+TExpenses.EXPENSE_BY+" = '"+member+"' COLLATE NOCASE";
        Cursor cursor = getDatabase().rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                totalOfExpense = cursor.getFloat(0);
            }while(cursor.moveToNext());
        }
        return totalOfExpense;
    }

    public static Group getGroup(int groupId){
        Group group = null;
        Cursor c = getDatabase().query(TGroups.TGROUPS, null, TGroups.GROUPID_SERVER +" = "+groupId, null, null, null, null);
        if(c.moveToFirst()){
            do{
                group = new Group();
                group.setGroupId(c.getInt(c.getColumnIndex(TGroups.GROUPID)));
                group.setGroupIdServer(c.getInt(c.getColumnIndex(TGroups.GROUPID_SERVER)));
                group.setGroupName(c.getString(c.getColumnIndex(TGroups.GROUPNAME)));
                group.setGroupIcon(c.getString(c.getColumnIndex(TGroups.GROUPICON)));
                group.setDate(c.getString(c.getColumnIndex(TGroups.DATEOFCREATION)));
                group.setAdmin(c.getString(c.getColumnIndex(TGroups.ADMIN)));
                group.setMembersCount(c.getInt(c.getColumnIndex(TGroups.TOTALMEMBERS)));
                group.setTotalOfExpense(c.getFloat(c.getColumnIndex(TGroups.TOTALOFEXPENSE)));

            }while(c.moveToNext());
        }
        return group;
    }

    public static long addAdminToGroup(int addedMemberIntoGroup, int groupIdFk) {
        ContentValues cv = new ContentValues();
        cv = new ContentValues();
        cv.put(TMembers.MEMBER_ID_SERVER, addedMemberIntoGroup);
        cv.put(TMembers.NAME, AppSharedPreference.getPreferenceString(AppConstants.ADMIN_NAME));
        cv.put(TMembers.PHONE_NUMBER, AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PHONE));
        cv.put(TMembers.GROUP_ID_FK, groupIdFk);
        long rowId = insert(TMembers.TMEMBERS,cv);
        if(rowId > 0)
            updateLastUpdated(groupIdFk, Utils.getCurrentTimeInMilliSecs());
        return rowId;
    }

    public static long insert(String tableName, ContentValues cv) {
        return getDatabase().insert(tableName, null, cv);
    }

    public static long updateLastUpdated(int groupId, long time) {
        ContentValues cv = new ContentValues();
        cv.put(TGroups.LASTUPDATEDON, String.valueOf(time));
        String condition = TGroups.GROUPID_SERVER + " = " +groupId;
        return update(condition, TGroups.TGROUPS, cv);
    }

    private static long update(String condition, String table, ContentValues cv) {
        return getDatabase().update(table, cv, condition, null);
    }

    public static ArrayList<Member> getMembers(int groupId) {
        Cursor cursor = getDatabase().query(TMembers.TMEMBERS, memberFields, TMembers.GROUP_ID_FK + " = " + groupId, null, null, null, null);
        ArrayList<Member> members = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                Member member = new Member();
                member.setMemberId(cursor.getInt(cursor.getColumnIndex(TMembers.MEMBER_ID)));
                member.setMemberIdServer(cursor.getInt(cursor.getColumnIndex(TMembers.MEMBER_ID_SERVER)));
                member.setPhoneNumber(cursor.getString(cursor.getColumnIndex(TMembers.PHONE_NUMBER)));
                //member.setGcmregNo(cursor.getString(cursor.getColumnIndex(TMembers.GCM_REG_NO)));
                member.setGroupIdFk(cursor.getInt(cursor.getColumnIndex(TMembers.GROUP_ID_FK)));
                member.setMemberName(cursor.getString(cursor.getColumnIndex(TMembers.NAME)));
                members.add(member);
            } while(cursor.moveToNext());
        }
        return members;
    }

    public static ArrayList<String> getMemberName(int groupId) {
        Cursor cursor = getDatabase().query(TMembers.TMEMBERS, new String[]{TMembers.NAME}, TMembers.GROUP_ID_FK + " = " + groupId, null, null, null, null);
        ArrayList<String> members = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                members.add(cursor.getString(cursor.getColumnIndex(TMembers.NAME)));
            } while(cursor.moveToNext());
        }
        return members;
    }

    public static Member updateMemberServerId(long memberIdAdded, int memberIdServerAdded) {
        ContentValues cv= new ContentValues();
        Member member = new Member();
        cv.put(TMembers.MEMBER_ID_SERVER, memberIdServerAdded);
        getDatabase().update(TMembers.TMEMBERS, cv, TMembers.MEMBER_ID+ " = " + memberIdAdded, null);
        Cursor c = getDatabase().query(TMembers.TMEMBERS, memberFields, TMembers.MEMBER_ID +" = "+memberIdAdded, null, null, null, null);
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

    public static long addMembers(Member member,int groupId){
        ContentValues iniContentValues = new ContentValues();
        iniContentValues.put(TMembers.GROUP_ID_FK, groupId);
        iniContentValues.put(TMembers.NAME, member.getMemberName());
        iniContentValues.put(TMembers.PHONE_NUMBER, member.getPhoneNumber());
        return getDatabase().insert(TMembers.TMEMBERS, null, iniContentValues);
    }

    public static List<Member> getMembersOfGroup(int groupId){
        List<Member> members = new ArrayList<Member>();
        Cursor c = getDatabase().query(TMembers.TMEMBERS, new String[]{
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

    public static int deleteMember(int memberId){
        int rowAffected = getDatabase().delete(TMembers.TMEMBERS, TMembers.MEMBER_ID +" = "+memberId, null);
        return rowAffected;
    }

    public static int deleteMemberByGroup(int groupId){
        int rowAffected = getDatabase().delete(TMembers.TMEMBERS, TMembers.GROUP_ID_FK +" = "+groupId, null);
        return rowAffected;
    }

    public static int removeMember(int memberServerId){
        int rowAffected = getDatabase().delete(TMembers.TMEMBERS, TMembers.MEMBER_ID_SERVER +" = "+ memberServerId, null);
        return rowAffected;
    }

    public static Member getMemberByServerId(int memberServerId) {
        Member member = new Member();
        Cursor c = getDatabase().query(TMembers.TMEMBERS, null, TMembers.MEMBER_ID_SERVER +" = "+ memberServerId, null, null, null, null);
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

    public static int removeMemberByGroupServerId(int groupServerId) {
        int rowAffected = getDatabase().delete(TMembers.TMEMBERS, TMembers.GROUP_ID_FK +" = "+groupServerId, null);
        return rowAffected;

    }

    public static int getItemId(String itemName, int groupId) {
        Cursor cursor = getDatabase().query(TItems.TITEMS, itemFields, TItems.GROUP_FK + " = " + groupId + " AND " + TItems.ITEM_NAME + " = '" + itemName + "'", null, null, null, TItems.ITEM_NAME);
        Items item= new Items();
        if(cursor.moveToFirst()) {
            do {
                item = fillInItem(cursor);
            } while(cursor.moveToNext());
        }
        if(item != null)
            return item.getIdServer();

        return 0;
    }

    public static ArrayList<Items> getItems(String groupId) {
        Cursor cursor = getDatabase().query(TItems.TITEMS, itemFields, TItems.GROUP_FK + " = " + groupId, null, null, null, TItems.ITEM_NAME);
        return resolveCursorForItems(cursor);
    }

    public static ArrayList<Items> getItems(Integer groupId, String category) {
        Cursor cursor = getDatabase().query(TItems.TITEMS, itemFields, TItems.GROUP_FK + " = " + groupId + " AND " + TItems.CATEGORY + " = '" + category + "'", null, null, null, null);
        return resolveCursorForItems(cursor);
    }

    public static Items getItem(int itemIdServer) {
        Cursor cursor = getDatabase().query(TItems.TITEMS, itemFields, TItems.ITEM_ID_SERVER + " = " + itemIdServer, null, null, null, null);
        Items item = null;
        if(cursor.moveToFirst()) {
            do {
                item = fillInItem(cursor);
            } while(cursor.moveToNext());
        }
        return item;
    }

    public static int removeItem(int itemIdServer,int groupId){
        int rowAffected = getDatabase().delete(TItems.TITEMS, TItems.ITEM_ID_SERVER+" = "+ itemIdServer +" AND "+TItems.GROUP_FK + " = " + groupId, null);
        return rowAffected;
    }

    private static ArrayList<Items> resolveCursorForItems(Cursor cursor) {
        ArrayList<Items> items = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                items.add(fillInItem(cursor));
            } while(cursor.moveToNext());
        }
        return items;
    }

    private static Items fillInItem(Cursor cursor) {
        Items item = new Items();
        item.setCategory(cursor.getString(cursor.getColumnIndex(TItems.CATEGORY)));
        item.setGroupFK(cursor.getInt(cursor.getColumnIndex(TItems.GROUP_FK)));
        item.setId(cursor.getInt(cursor.getColumnIndex(TItems.ITEM_ID)));
        item.setIdServer(cursor.getInt(cursor.getColumnIndex(TItems.ITEM_ID_SERVER)));
        item.setItemName(cursor.getString(cursor.getColumnIndex(TItems.ITEM_NAME)));
        return item;
    }

    public static int updateItemServerId(long itemId,int itemserverId){
        ContentValues cv= new ContentValues();
        cv.put(TItems.ITEM_ID_SERVER, itemserverId);
        return getDatabase().update(TItems.TITEMS, cv, TItems.ITEM_ID + " = " + itemId, null);

    }

    public static int removeExpense(int expenseId,int groupId){
        int rowAffected = getDatabase().delete(TExpenses.TABLENAME, TExpenses.EXPENSE_ID + " = " + expenseId + " AND " + TExpenses.GROUP_ID_FK + " = " + groupId, null);
        return rowAffected;
    }

    public static int removeMember(int memberId,int groupId){
        int rowAffected = getDatabase().delete(TMembers.TMEMBERS, TMembers.MEMBER_ID + " = " + memberId + " AND " + TMembers.GROUP_ID_FK + " = " + groupId, null);
        return rowAffected;
    }

    public static int updateEpenseServerId(long expenseId,int expenseserverId){
        ContentValues cv= new ContentValues();
        cv.put(TExpenses.EXPENSE_ID_SERVER, expenseserverId);
        return getDatabase().update(TExpenses.TABLENAME, cv, TExpenses.EXPENSE_ID + " = " + expenseId, null);
    }

    public static List<ExpenseOject> getExpenses(int groupId){
        List<ExpenseOject> expenses = new ArrayList<ExpenseOject>();
        Cursor expensesCursor = getDatabase().query(TExpenses.TABLENAME, null,
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

    public int getExpenseServerId(int expenseId,int groupId){
        Cursor c = getDatabase().query(TExpenses.TABLENAME, new String[]{TExpenses.EXPENSE_ID_SERVER}, TExpenses.EXPENSE_ID +" = "+ expenseId +" AND "+TExpenses.GROUP_ID_FK + " = " + groupId, null, null, null, null);
        if(c.moveToFirst()){
            do{
                return c.getInt(c.getColumnIndex(TExpenses.EXPENSE_ID_SERVER));
            }while(c.moveToNext());
        }
        return 0;
    }

    public static ExpenseOject getExpenseByServerId(int expenseIdServer){
        ExpenseOject expense = new ExpenseOject();
        Cursor c = getDatabase().query(TExpenses.TABLENAME, null, TExpenses.EXPENSE_ID_SERVER +" = "+ expenseIdServer, null, null, null, null);
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

    public static int removeExpense(int expenseServerId){
        int rowAffected = getDatabase().delete(TExpenses.TABLENAME, TExpenses.EXPENSE_ID_SERVER +" = "+ expenseServerId, null);
        return rowAffected;
    }

    /********* SETTLEMENT RELATED METHODS STARTS *******/
    public static int updateSettlementServerId(long settlementId,int settlementserverId){
        ContentValues cv= new ContentValues();
        cv.put(TSettlement.SET_ID_SERVER, settlementserverId);
        return getDatabase().update(TSettlement.TABLENAME, cv, TSettlement.SET_ID + " = " + settlementId, null);
    }

    public static List<SettlementObject> getSettlements(int groupId) {
        List<SettlementObject> settlements = new ArrayList<SettlementObject>();
        Cursor c = getDatabase().query(TSettlement.TABLENAME, null, TSettlement.GROUP_ID_FK + " = " +groupId, null, null, null,null);
        System.out.println("++++++++++++++++Total settlements"+c.getCount());
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

    public static void updateGroupTable1() {
        if(!AppSharedPreference.getPreferenceBoolean(AppConstants.ALTER_GROUP_TABLE1)) {
            DebugLogger.message("AppSharedPreference.getPreferenceBoolean(AppConstants.ALTER_GROUP_TABLE1)" + AppSharedPreference.getPreferenceBoolean(AppConstants.ALTER_GROUP_TABLE1));
            getDatabase().execSQL(TGroups.ALTER_GROUP_TABLE1);
            AppSharedPreference.storeAccPreference(AppConstants.ALTER_GROUP_TABLE1, true);
            DebugLogger.message("AppSharedPreference.getPreferenceBoolean(AppConstants.ALTER_GROUP_TABLE1)" + AppSharedPreference.getPreferenceBoolean(AppConstants.ALTER_GROUP_TABLE1));
        }
    }

    public static boolean deleteAllStuff(int groupId,boolean dodeletegroup){
        boolean result = false;
        int memUpdated = getDatabase().delete(TMembers.TMEMBERS, TMembers.GROUP_ID_FK +" = "+groupId, null);
        int settUpdated = getDatabase().delete(TSettlement.TABLENAME, TSettlement.GROUP_ID_FK +" = "+groupId, null);
        int itemUpdated = getDatabase().delete(TItems.TITEMS, TItems.GROUP_FK +" = "+groupId, null);
        int expenseUpdated = getDatabase().delete(TExpenses.TABLENAME, TExpenses.GROUP_ID_FK +" = "+groupId, null);
        if(dodeletegroup){
            int groupUpdated = getDatabase().delete(TGroups.TGROUPS, TGroups.GROUPID_SERVER +" = "+groupId, null);
        }

        return result;

    }

    public static ArrayList<Items> getItemsLike(String like) {
        ArrayList<Items> items = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("SELECT * FROM "+TItems.TITEMS + " WHERE "+TItems.ITEM_NAME + " LIKE '%" + like + "%'", null);
        if(cursor.moveToFirst()) {
            do {
                Items item = new Items();
                item.setItemName(cursor.getString(cursor.getColumnIndex(TItems.ITEM_NAME)));
                item.setCategory(cursor.getString(cursor.getColumnIndex(TItems.CATEGORY)));
                item.setIdServer(cursor.getInt(cursor.getColumnIndex(TItems.ITEM_ID_SERVER)));
                item.setGroupFK(cursor.getInt(cursor.getColumnIndex(TItems.GROUP_FK)));
                item.setId(cursor.getInt(cursor.getColumnIndex(TItems.ITEM_ID)));
                items.add(item);
            } while(cursor.moveToNext());
        }

        return items;
    }
    
}
