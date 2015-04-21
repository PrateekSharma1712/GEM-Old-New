package com.prateek.gem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.prateek.gem.App;
import com.prateek.gem.utils.Utils;

public class ExpenseOject implements Item,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5079726862438457122L;
	private int expenseId;
	private int expenseIdServer;
	private Long date;
	private float amount;
	private float share;
	private String item;
	private String expenseBy;
	private String participants;
	private int groupId;
	
	public int getExpenseIdServer() {
		return expenseIdServer;
	}



	public void setExpenseIdServer(int expenseIdServer) {
		this.expenseIdServer = expenseIdServer;
	}

	
	
	public ExpenseOject() {
		super();
	}

	

	public ExpenseOject(Long date,
			float amount, float share, String item, String expenseBy,
			String participants, int groupId) {
		super();
		this.date = date;
		this.amount = amount;
		this.share = share;
		this.item = item;
		this.expenseBy = expenseBy;
		this.participants = participants;
		this.groupId = groupId;
	}



	public int getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(int expenseId) {
		this.expenseId = expenseId;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getShare() {
		return share;
	}

	public void setShare(float share) {
		this.share = share;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getExpenseBy() {
		return expenseBy;
	}

	public void setExpenseBy(String expenseBy) {
		this.expenseBy = expenseBy;
	}

	public String getParticipants() {
		return participants;
	}

	public void setParticipants(String participants) {
		this.participants = participants;
	}
	
	@Override
	public String toString() {
		return "ExpenseOject [expenseId=" + expenseId + ", expenseIdServer="
				+ expenseIdServer + ", date=" + date + ", amount=" + amount
				+ ", share=" + share + ", item=" + item + ", expenseBy="
				+ expenseBy + ", participants=" + participants + ", groupId="
				+ groupId + "]";
	}

	@Override
	public boolean isSection() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(amount);
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((expenseBy == null) ? 0 : expenseBy.hashCode());
		result = prime * result + groupId;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result
				+ ((participants == null) ? 0 : participants.hashCode());
		result = prime * result + Float.floatToIntBits(share);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpenseOject other = (ExpenseOject) obj;
		if (Float.floatToIntBits(amount) != Float.floatToIntBits(other.amount))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (expenseBy == null) {
			if (other.expenseBy != null)
				return false;
		} else if (!expenseBy.equals(other.expenseBy))
			return false;
		if (groupId != other.groupId)
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (participants == null) {
			if (other.participants != null)
				return false;
		} else if (!participants.equals(other.participants))
			return false;
		if (Float.floatToIntBits(share) != Float.floatToIntBits(other.share))
			return false;
		return true;
	}



	public static List<ExpenseOject> sortByDate(String mMonth) {
		List<ExpenseOject> expenses = new ArrayList<ExpenseOject>();
		for(ExpenseOject e : App.getInstance().getExpensesList()) {
			if(Utils.getMonthName(String.valueOf(e.getDate())).equals(mMonth)) {
				expenses.add(e);
			}
		}
		Collections.sort(expenses, new DateComparator());
		return expenses;
	}
}
