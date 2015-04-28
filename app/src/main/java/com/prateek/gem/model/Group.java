package com.prateek.gem.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.prateek.gem.AppConstants;
import com.prateek.gem.utils.AppSharedPreference;

import java.util.ArrayList;
import java.util.List;

public class Group implements Parcelable, Comparable<Group>{

	private Integer groupId;
    private Integer groupIdServer;
	private String groupName;
	private String groupIcon;
	private String date;
	private Float totalOfExpense;
	private Integer membersCount;
	private String admin;
    private long lastUpdatedOn;
	

	public Group(Integer groupId, Integer groupIdServer, String groupName,
                 String groupIcon, String date, Float totalOfExpense,
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

	public String getGroupIcon() {
		return groupIcon;
	}

	public void setGroupIcon(String groupIcon) {
		this.groupIcon = groupIcon;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Float getTotalOfExpense() {
        if(totalOfExpense != null)
		    return totalOfExpense;
        else
            return 0f;
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

    public Group(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeInt(groupId);
        destination.writeInt(groupIdServer);
        destination.writeString(groupName);
        destination.writeString(groupIcon);
        destination.writeString(date);
        destination.writeFloat(totalOfExpense);
        destination.writeInt(membersCount);
        destination.writeString(admin);
    }

    public void readFromParcel(Parcel in) {
        groupId = in.readInt();
        groupIdServer = in.readInt();
        groupName = in.readString();
        groupIcon = in.readString();
        date = in.readString();
        totalOfExpense = in.readFloat();
        membersCount = in.readInt();
        admin = in.readString();
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupIdServer=" + groupIdServer +
                ", groupName='" + groupName + '\'' +
                ", groupIcon='" + groupIcon + '\'' +
                ", date='" + date + '\'' +
                ", totalOfExpense=" + totalOfExpense +
                ", membersCount=" + membersCount +
                ", admin='" + admin + '\'' +
                '}';
    }

    public static Group getPersonalGroup() {
        Group group = new Group();
        group.setGroupId(0);
        group.setGroupIdServer(0);
        group.setGroupIcon("0");
        group.setAdmin(AppSharedPreference.getPreferenceString(AppConstants.ADMIN_NAME));
        group.setGroupName("Personal");
        group.setTotalOfExpense(0F);
        group.setMembersCount(0);
        return group;
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
