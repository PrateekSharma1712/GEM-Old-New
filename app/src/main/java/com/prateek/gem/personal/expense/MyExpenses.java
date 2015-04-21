package com.prateek.gem.personal.expense;

public class MyExpenses {

	private int expenseId;
	private String expDate;
	private int itemId;
	private float amount;
	private String mode;

	public MyExpenses() {
		super();
	}

	public int getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(int expenseId) {
		this.expenseId = expenseId;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "MyExpenses [expenseId=" + expenseId + ", expDate=" + expDate
				+ ", itemId=" + itemId + ", amount=" + amount + ", mode="
				+ mode + "]";
	}

}
