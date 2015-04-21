package com.prateek.gem;

public interface OnModeConfirmListener {
	public void modeConfirmed();
	public void deleteMemberConfirmed(int memberId);
	public void deleteExpenseConfirmed(int expenseId);
	public void deleteItemConfirmed(String deleteItemConfirmed);	
	public void openNewActivity(int requestCodeClickImage);
}
