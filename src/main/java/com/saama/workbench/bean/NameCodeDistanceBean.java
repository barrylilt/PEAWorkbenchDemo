package com.saama.workbench.bean;

public class NameCodeDistanceBean {
	private double distance;
	private String name;
	private String code;
	private String id;

	public NameCodeDistanceBean(double distance, String name,
			String code, String id) {
		this.distance = distance;
		this.name = name;
		this.code = code;
		this.id = id;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}