package com.prateek.gem.model;


public class GroupGrid {

	private String gridName;
	private Integer gridColor;
	private String gridValue;
	public String getGridName() {
		return gridName;
	}
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}
	public Integer getGridColor() {
		return gridColor;
	}
	public void setGridColor(Integer gridColor) {
		this.gridColor = gridColor;
	}
	public String getGridValue() {
		return gridValue;
	}
	public void setGridValue(String gridValue) {
		this.gridValue = gridValue;
	}
	public GroupGrid(String gridName, Integer gridColor, String gridValue) {
		super();
		this.gridName = gridName;
		this.gridColor = gridColor;
		this.gridValue = gridValue;
	}
	public GroupGrid() {
		super();
	}
	
}
