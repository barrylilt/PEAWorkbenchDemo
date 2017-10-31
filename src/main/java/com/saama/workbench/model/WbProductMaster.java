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
@Table(name = "WB_PRODUCT_MASTER", schema = "LND"

)
@SequenceGenerator(name = "WB_PRODUCT_MASTER_SEQ", sequenceName = "WB_PRODUCT_MASTER_SEQ")
public class WbProductMaster {

	private long productMasterId;
	private String levelName;
	private long levelNumber;
	private Date dateCreated;
	private Date lastUpdated;

	@Id
	@GeneratedValue(generator = "WB_PRODUCT_MASTER_SEQ", strategy = GenerationType.AUTO)
	@Column(name = "Product_Master_ID", unique = true, nullable = false)
	public long getProductMasterId() {
		return productMasterId;
	}

	public void setProductMasterId(long productMasterId) {
		this.productMasterId = productMasterId;
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
