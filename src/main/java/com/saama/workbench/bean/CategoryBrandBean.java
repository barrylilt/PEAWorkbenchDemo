package com.saama.workbench.bean;

import java.util.ArrayList;
import java.util.List;

public class CategoryBrandBean {
	private String category;
	private List<String> brand;
	
	public CategoryBrandBean() {
		// TODO Auto-generated constructor stub
	}
	
	public CategoryBrandBean(String category) {
		this.category = category;
		this.brand = new ArrayList<String>();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getBrand() {
		return brand;
	}

	public void setBrand(List<String> brand) {
		this.brand = brand;
	}

}
