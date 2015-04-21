package com.prateek.gem.model;

public class GiverTakerObject {

	private String whoSpent;
	private String whoUsed;
	public String getWhoSpent() {
		return whoSpent;
	}
	public void setWhoSpent(String whoSpent) {
		this.whoSpent = whoSpent;
	}
	public String getWhoUsed() {
		return whoUsed;
	}
	public void setWhoUsed(String whoUsed) {
		this.whoUsed = whoUsed;
	}
	public GiverTakerObject() {
		super();
	}
	public GiverTakerObject(String whoSpent, String whoUsed) {
		super();
		this.whoSpent = whoSpent;
		this.whoUsed = whoUsed;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((whoSpent == null) ? 0 : whoSpent.hashCode());
		result = prime * result + ((whoUsed == null) ? 0 : whoUsed.hashCode());
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
		GiverTakerObject other = (GiverTakerObject) obj;
		if (whoSpent == null) {
			if (other.whoSpent != null)
				return false;
		} else if (!whoSpent.equals(other.whoSpent))
			return false;
		if (whoUsed == null) {
			if (other.whoUsed != null)
				return false;
		} else if (!whoUsed.equals(other.whoUsed))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "\nGiverTakerObject [whoSpent=" + whoSpent + ", whoUsed="
				+ whoUsed + "]";
	}

}
