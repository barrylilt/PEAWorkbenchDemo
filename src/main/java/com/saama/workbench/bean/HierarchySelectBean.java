package com.saama.workbench.bean;

public class HierarchySelectBean {
	private int levelNumber;
	private String levelName;
	private int childId;
	private int cId;
	private String childCode;
	private String childName;

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public int getChildId() {
		return childId;
	}

	public void setChildId(int productId) {
		this.childId = productId;
	}

	public int getCId() {
		return cId;
	}

	public void setCId(int pId) {
		this.cId = pId;
	}

	public String getChildCode() {
		return childCode;
	}

	public void setChildCode(String productCode) {
		this.childCode = productCode;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String productName) {
		this.childName = productName;
	}

}
