package com.prateek.gem.model;

import java.io.Serializable;
import java.util.Comparator;

import android.support.v4.os.ParcelableCompat;

public class Member extends ParcelableCompat implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4119273519399807458L;
	private int memberId;
	private int memberIdServer;
	private String phoneNumber;
	private String memberName;
	private int groupIdFk;
	private String gcmregNo;
	
	public Member() {
		super();
	}
	
	public Member(int memberId, int memberIdServer, String phoneNumber,
			String memberName, int groupIdFk, String gcmregNo) {
		super();
		this.memberId = memberId;
		this.memberIdServer = memberIdServer;
		this.phoneNumber = phoneNumber;
		this.memberName = memberName;
		this.groupIdFk = groupIdFk;
		this.gcmregNo = gcmregNo;
	}

	@Override
	public String toString() {
		return "Member [memberId=" + memberId + ", memberIdServer="
				+ memberIdServer + ", phoneNumber=" + phoneNumber
				+ ", memberName=" + memberName + ", groupIdFk=" + groupIdFk
				+ ", gcmregNo=" + gcmregNo + "]";
	}

	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public int getGroupIdFk() {
		return groupIdFk;
	}
	public void setGroupIdFk(int groupIdFk) {
		this.groupIdFk = groupIdFk;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupIdFk;
		result = prime * result + memberId;
		result = prime * result
				+ ((memberName == null) ? 0 : memberName.hashCode());
		result = prime * result
				+ ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
		Member other = (Member) obj;
		if (groupIdFk != other.groupIdFk)
			return false;
		if (memberId != other.memberId)
			return false;
		if (memberName == null) {
			if (other.memberName != null)
				return false;
		} else if (!memberName.equals(other.memberName))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}
	
	public int getMemberIdServer() {
		return memberIdServer;
	}
	public void setMemberIdServer(int memberIdServer) {
		this.memberIdServer = memberIdServer;
	}

	public String getGcmregNo() {
		return gcmregNo;
	}
	public void setGcmregNo(String gcmregNo) {
		this.gcmregNo = gcmregNo;
	}

	public static Comparator<Member> memberComparator = new Comparator<Member>() {
		
		@Override
		public int compare(Member lhs, Member rhs) {
			// TODO Auto-generated method stub
			return lhs.getMemberName().compareTo(rhs.getMemberName());
		}
	};
}
