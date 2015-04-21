package com.prateek.gem.model;

import java.util.List;

public class GroupDetail {

	private Group group;
	private List<Member> members;
	private List<ExpenseOject> expenses;

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public List<ExpenseOject> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<ExpenseOject> expenses) {
		this.expenses = expenses;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "GroupDetail [group=" + group + ", members=" + members
				+ ", expenses=" + expenses + "]";
	}
}
