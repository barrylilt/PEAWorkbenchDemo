package com.saama.workbench.bean;

import java.util.ArrayList;
import java.util.List;

public class BrandFormBean {
	
	private String key;
	private List<String> value;
	public BrandFormBean(String brand) {
		this.key = brand;
		this.value = new ArrayList<String>();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String brand) {
		this.key = brand;
	}
	public List<String> getValue() {
		return value;
	}
	public void setValue(List<String> value) {
		this.value = value;
	}
}
