package com.prateek.gem;

public interface OnModeConfirmListener {
    public void modeConfirmed();

    public void deleteMemberConfirmed(int memberId);

    public void deleteExpenseConfirmed(int expenseId);

    public void deleteItemConfirmed(boolean deleteItemConfirmed);

    public void openNewActivity(int requestCodeClickImage);

    public void onItemsAdded(String category, String item);
}
