package com.saama.workbench.bean;

public class JobRunStatsBean {
	private String source;
	private String lastExecutedOn;
	private String pendingTransaction;

	public JobRunStatsBean(String s, String l, String p) {
		this.setSource(s);
		this.setLastExecutedOn(l);
		this.setPendingTransaction(p);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLastExecutedOn() {
		return lastExecutedOn;
	}

	public void setLastExecutedOn(String lastExecutedOn) {
		this.lastExecutedOn = lastExecutedOn;
	}

	public String getPendingTransaction() {
		return pendingTransaction;
	}

	public void setPendingTransaction(String pendingTransaction) {
		this.pendingTransaction = pendingTransaction;
	}

}
