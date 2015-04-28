package com.prateek.gem;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Application;

import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.GiverTakerObject;
import com.prateek.gem.model.Group;
import com.prateek.gem.model.Items;
import com.prateek.gem.model.Member;
import com.prateek.gem.model.SettlementObject;
import com.prateek.gem.model.Users;
import com.prateek.gem.utils.AppDataManager;

public class App extends Application {

	private static App _instance;
	private List<Group> allGroups;
	private List<ExpenseOject> expensesList;
	private List<Member> curr_Members;
	private List<Items> items;
	private List<SettlementObject> settlements;
	private Group curr_group;
	private List<Member> participantsList;
	private Users admin;
	private Map<GiverTakerObject,Float> giverTakermap;

    public static synchronized App getInstance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }

    public List<Group> getAllGroups() {
		return allGroups;
	}

	public void setAllGroups(List<Group> allGroups) {
		this.allGroups = allGroups;
	}

	public List<ExpenseOject> getExpensesList() {
		return expensesList;
	}

	public void setExpensesList(List<ExpenseOject> expensesList) {
		this.expensesList = expensesList;
	}

	public List<Member> getCurr_Members() {
		if(curr_Members != null)
		Collections.sort(curr_Members,Member.memberComparator);
		return curr_Members;
	}

	public void setCurr_Members(List<Member> curr_Members) {
		if(curr_Members != null)
		Collections.sort(curr_Members,Member.memberComparator);
		this.curr_Members = curr_Members;
	}
	
	public List<Items> getItems() {
		if(items != null && items.size() != 0) 
			Collections.sort(items);
		return items;
	}

	public void setItems(List<Items> items) {		
		this.items = items;
	}

	public Group getCurr_group() {
		return curr_group;
	}

	public void setCurr_group(Group curr_group) {
		this.curr_group = curr_group;
	}

	public List<Member> getParticipantsList() {
		return participantsList;
	}

	public void setParticipantsList(List<Member> participantsList) {
		this.participantsList = participantsList;
	}

	public Users getAdmin() {
		return admin;
	}

	public void setAdmin(Users admin) {
		this.admin = admin;
	}
	
	public Map<GiverTakerObject,Float> getGiverTakermap() {
		return giverTakermap;
	}

	public void setGiverTakermap(Map<GiverTakerObject,Float> giverTakermap) {
		this.giverTakermap = giverTakermap;
	}

	public List<SettlementObject> getSettlements() {
		return settlements;
	}

	public void setSettlements(List<SettlementObject> settlements) {
		this.settlements = settlements;
	}
}
