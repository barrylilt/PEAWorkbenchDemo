package com.saama.workbench.bean;

public class DataAvailabilityStatsBean {

	private String month;
	private long shipmentData;
	private long opsoData;
	private double ratio;
	
	public DataAvailabilityStatsBean() {
		
	}
	
	public DataAvailabilityStatsBean(String m, long s, long o, double r) {
		this.setMonth(m);
		this.setShipmentData(s);
		this.setOpsoData(o);
		this.setRatio(r);
	}
	

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public long getShipmentData() {
		return shipmentData;
	}

	public void setShipmentData(long shipmentData) {
		this.shipmentData = shipmentData;
	}

	public long getOpsoData() {
		return opsoData;
	}

	public void setOpsoData(long opsoData) {
		this.opsoData = opsoData;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

}
