package com.prateek.gem.model;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Parcelable;

public class Group implements Comparable<Group>{

	private Integer groupId;
	private Integer groupIdServer;
	private String groupName;
	private Uri groupIcon;
	private String date;
	private Float totalOfExpense;
	private Integer membersCount;
	private String admin;
    private long lastUpdatedOn;
	

	public Group(Integer groupId, Integer groupIdServer, String groupName,
			Uri groupIcon, String date, Float totalOfExpense,
			Integer membersCount, String admin) {
		super();
		this.groupId = groupId;
		this.groupIdServer = groupIdServer;
		this.groupName = groupName;
		this.groupIcon = groupIcon;
		this.date = date;
		this.totalOfExpense = totalOfExpense;
		this.membersCount = membersCount;
		this.admin = admin;
	}

	public Group() {
		super();
	}


	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", groupIdServer=" + groupIdServer
				+ ", groupName=" + groupName + ", groupIcon=" + groupIcon
				+ ", date=" + date + ", totalOfExpense=" + totalOfExpense				
				+ ", membersCount=" + membersCount + ", admin=" + admin + "]";
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Uri getGroupIcon() {
		return groupIcon;
	}

	public void setGroupIcon(Uri groupIcon) {
		this.groupIcon = groupIcon;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Float getTotalOfExpense() {
		return totalOfExpense;
	}

	public void setTotalOfExpense(Float totalOfExpense) {
		this.totalOfExpense = totalOfExpense;
	}
	
	public Integer getMembersCount() {
		return membersCount;
	}

	public void setMembersCount(Integer membersCount) {
		this.membersCount = membersCount;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}
	
	public static Group getGroupById(int groupId,List<Group> groups){
		for(Group g:groups){
			if(g.getGroupId() == groupId){
				return g;
			}
		}	
		return null;
	}
	
	public static boolean contains(List<Group> groups,String groupName){
		for(Group g:groups){
			if(g.getGroupName().equals(groupName))
				return true;
		}
		return false;
	}
	/*
	 * @params groupId,lstOfgroups,totalExpense,amount,type
	 * @description type decides whether to add or subtract amount from total expense for a particular group in all groups.
	 * @return Return all the groups, and put them in GEMApp.getInstance().getAllGroups.  
	 */
	public static List<Group> updateTotalExpense(int groupId,List<Group> list,Float totalExpense,float amount,int type){
		List<Group> resultList = new ArrayList<Group>();
		for (Group group : list) {
			if(group.getGroupId() == groupId){
				if(type == 0)
					group.setTotalOfExpense(group.getTotalOfExpense()-amount);
				else
					group.setTotalOfExpense(group.getTotalOfExpense()+amount);
			}
			resultList.add(group);
			System.out.println(group);
		}
		return resultList;
		
	}

	public Integer getGroupIdServer() {
		return groupIdServer;
	}

	public void setGroupIdServer(Integer groupIdServer) {
		this.groupIdServer = groupIdServer;
	}

    public long getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(long lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    @Override
    public int compareTo(Group another) {
        return Long.compare(another.getLastUpdatedOn(), this.getLastUpdatedOn());
    }
}
