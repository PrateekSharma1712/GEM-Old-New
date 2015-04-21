package com.prateek.gem.model;

public class SettlementObject {

	private int setId;
	private int setIdServer;
	private int groupId;
	private String givenBy;
	private String takenBy;
	private float amount;
	private String date;

	public SettlementObject(int setId, int setIdServer, int groupId,
			String givenBy, String takenBy, float amount) {
		super();
		this.setId = setId;
		this.setIdServer = setIdServer;
		this.groupId = groupId;
		this.givenBy = givenBy;
		this.takenBy = takenBy;
		this.amount = amount;
	}

	

	@Override
	public String toString() {
		return "SettlementObject [setId=" + setId + ", setIdServer="
				+ setIdServer + ", groupId=" + groupId + ", givenBy=" + givenBy
				+ ", takenBy=" + takenBy + ", amount=" + amount + ", date="
				+ date + "]";
	}



	public SettlementObject() {
		super();
	}

	public String getGivenBy() {
		return givenBy;
	}

	public void setGivenBy(String givenBy) {
		this.givenBy = givenBy;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(String takenBy) {
		this.takenBy = takenBy;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getSetId() {
		return setId;
	}

	public void setSetId(int setId) {
		this.setId = setId;
	}

	public int getSetIdServer() {
		return setIdServer;
	}

	public void setSetIdServer(int setIdServer) {
		this.setIdServer = setIdServer;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
