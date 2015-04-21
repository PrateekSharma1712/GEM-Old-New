package com.prateek.gem.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.prateek.gem.AppConstants;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Items;
import com.prateek.gem.model.Member;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.Utils;

import java.util.ArrayList;

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
                group.setGroupIcon(Uri.parse(c.getString(c.getColumnIndex(TGroups.GROUPICON))));
                group.setDate(c.getString(c.getColumnIndex(TGroups.DATEOFCREATION)));
                group.setMembersCount(c.getInt(c.getColumnIndex(TGroups.TOTALMEMBERS)));
                group.setTotalOfExpense(c.getFloat(c.getColumnIndex(TGroups.TOTALOFEXPENSE)));
                group.setAdmin(c.getString(c.getColumnIndex(TGroups.ADMIN)));
                group.setLastUpdatedOn(Long.parseLong(c.getString(c.getColumnIndex(TGroups.LASTUPDATEDON))));
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
        return getDatabase().insert(tableName,null,cv);
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
                member.setGcmregNo(cursor.getString(cursor.getColumnIndex(TMembers.GCM_REG_NO)));
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
}
