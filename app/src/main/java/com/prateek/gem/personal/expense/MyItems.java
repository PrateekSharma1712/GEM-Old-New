package com.prateek.gem.personal.expense;

import java.util.ArrayList;

public class MyItems {

	private int itemId;
	private String itemName;
	private String category;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public MyItems() {
		super();
	}
	
	@Override
	public String toString() {
		return "MyItems [itemId=" + itemId + ", itemName=" + itemName
				+ ", category=" + category + "]";
	}

	public static MyItems contains(String itemName, ArrayList<MyItems> items){
        
        if(items != null && items.size()!=0) {
            for (MyItems item : items) {
                if (item.getItemName().equalsIgnoreCase(itemName)) {
                    return item;
                }
            }
        }
        return null;
    }
	
	public static MyItems getItem(int itemId, ArrayList<MyItems> items) {
		for(MyItems item : items) {
			if(item.getItemId() == itemId) {
				return item;
			}
		}
		
		return null;
	}

}
