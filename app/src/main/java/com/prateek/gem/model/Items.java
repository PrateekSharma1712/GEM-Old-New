package com.prateek.gem.model;

import java.util.ArrayList;
import java.util.List;

import com.prateek.gem.App;

public class Items implements Comparable<Items>{
	private int id;
	private int idServer;
	private String itemName;
	private int groupFK;
	private String category;

	

	public Items(int id, int idServer, String itemName, int groupFK,
			String category) {
		super();
		this.id = id;
		this.idServer = idServer;
		this.itemName = itemName;
		this.groupFK = groupFK;
		this.category = category;
	}

	public Items() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getGroupFK() {
		return groupFK;
	}

	public void setGroupFK(int groupFK) {
		this.groupFK = groupFK;
	}

	@Override
	public int compareTo(Items another) {
		// TODO Auto-generated method stub
		return this.itemName.compareToIgnoreCase(another.itemName);
	}
	
	public static List<String> getItemNameOfItems(List<Items> items,int groupId){
		List<String> itemnames = new ArrayList<String>();
		if(items != null){
			for(Items i:items){
				//if(i.getGroupFK() == groupId){
					
				//}
				itemnames.add(i.getItemName());
			}
		}		
		return itemnames;
	}
	
	public static List<String> getItemNameOfItems(List<Items> items,int groupId,String category){
		List<String> itemnames = new ArrayList<String>();
		if(items != null){
			for(Items i:items){
				if(i.getGroupFK() == groupId && i.getCategory().equalsIgnoreCase(category)){
					itemnames.add(i.getItemName());
				}
			}
		}		
		return itemnames;
	}

	

	@Override
	public String toString() {
		return "Items [id=" + id + ", idServer=" + idServer + ", itemName="
				+ itemName + ", groupFK=" + groupFK + ", category=" + category
				+ "]";
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getIdServer() {
		return idServer;
	}

	public void setIdServer(int idServer) {
		this.idServer = idServer;
	}
	
	public static String getCategoryOfItem(String itemName) {
		for(Items item : App.getInstance().getItems()) {
			if(item.getItemName().equals(itemName)) {
				return item.getCategory();
			}
		}
		
		return "";
	}
	
}
