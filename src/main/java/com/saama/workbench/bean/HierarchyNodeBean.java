package com.saama.workbench.bean;

public class HierarchyNodeBean {
	private int parentId;
	private String parentCode;
	private String parentName;
	private int childId;
	private String childCode;
	private String childName;
	private int distance;
	private int childLevel;
	private String childImg;

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int obj) {
		this.parentId = obj;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentProductCode) {
		this.parentCode = parentProductCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentProductName) {
		this.parentName = parentProductName;
	}

	public int getChildId() {
		return childId;
	}

	public void setChildId(int childId) {
		this.childId = childId;
	}

	public String getChildCode() {
		return childCode;
	}

	public void setChildCode(String childProductCode) {
		this.childCode = childProductCode;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childProductName) {
		this.childName = childProductName;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getChildLevel() {
		return childLevel;
	}

	public void setChildLevel(int childLevel) {
		this.childLevel = childLevel;
	}

	public String getChildImg() {
		return childImg;
	}

	public void setChildImg(String childImg) {
		this.childImg = childImg;
	}

}
