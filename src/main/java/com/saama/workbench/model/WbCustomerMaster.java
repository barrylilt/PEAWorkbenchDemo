package com.saama.workbench.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "WB_CUSTOMERS_MASTER", schema = "LND"

)
@SequenceGenerator(name = "WB_CUSTOMERS_MASTER_SEQ", sequenceName = "WB_CUSTOMERS_MASTER_SEQ")
public class WbCustomerMaster {

	private long customerMasterId;
	private String levelName;
	private long levelNumber;
	private Date dateCreated;
	private Date lastUpdated;

	@Id
	@GeneratedValue(generator = "WB_CUSTOMERS_MASTER_SEQ", strategy = GenerationType.AUTO)
	@Column(name = "Customer_Master_ID", unique = true, nullable = false)
	public long getCustomerMasterId() {
		return customerMasterId;
	}

	public void setCustomerMasterId(long customerMasterId) {
		this.customerMasterId = customerMasterId;
	}

	@Column(name = "PLevelName")
	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	@Column(name = "PLevelNumber")
	public long getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(long levelNumber) {
		this.levelNumber = levelNumber;
	}

	@Column(name = "DateCreated")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "LastUpdated")
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
