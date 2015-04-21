package com.prateek.gem.model;

import java.util.List;

public class Users {

	private int id;
	private String userName;
	private String phoneNumber;
	private String password;
	private String gcmRegId;
	private String email;
	
	
	
	public static final String ID = "user_id";
	public static final String USERNAME = "user_name";
	public static final String PHONENUMBER = "phone_number";
	public static final String PASSWORD = "password";
	public static final String GCM_REG_ID = "gcmregId";
	public static final String EMAIL = "email";
	
	public Users() {
		// TODO Auto-generated constructor stub
	}

	
	


	@Override
	public String toString() {
		return "Users [id=" + id + ", userName=" + userName + ", phoneNumber="
				+ phoneNumber + ", password=" + password + ", gcmRegId="
				+ gcmRegId + ", email=" + email + "]";
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((gcmRegId == null) ? 0 : gcmRegId.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		Users other = (Users) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (gcmRegId == null) {
			if (other.gcmRegId != null)
				return false;
		} else if (!gcmRegId.equals(other.gcmRegId))
			return false;
		if (id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}





	public Users(int id, String userName, String phoneNumber, String password,
			String gcmRegId, String email) {
		super();
		this.id = id;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.gcmRegId = gcmRegId;
		this.email = email;
	}





	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getGcmRegId() {
		return gcmRegId;
	}


	public void setGcmRegId(String gcmRegId) {
		this.gcmRegId = gcmRegId;
	}


	public static boolean contains(List<Users> users,String other,int whatToCompare){
		switch(whatToCompare){
		case 1:				
				if (other != null) {
		        	for(Users user:users){
		        		if(user.phoneNumber.equals(other)){
		        			return true;
		        		}
		        	}
		        } 
		        return false;	
		case 2:
			if (other != null) {
	        	for(Users user:users){
	        		if(user.password.equals(other)){
	        			return true;
	        		}
	        	}
	        } 
	        return false;
	        
		default: return false;
		}
	}
	
	public static Users getUserByNumber(List<Users> users,String phoneNumber){
		Users user = null;
		for(Users u: users){
			if(u.getPhoneNumber().equals(phoneNumber)){
				user = u;
			}
		}
		return user;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
}
