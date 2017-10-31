package com.saama.workbench.bean;

public class MapBean {
	private String key;
	private String value;

	public MapBean() {
		// TODO Auto-generated constructor stub
	}

	public MapBean(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
