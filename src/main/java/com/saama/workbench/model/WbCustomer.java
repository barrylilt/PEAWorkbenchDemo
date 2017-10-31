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
@Table(name = "WB_CUSTOMERS", schema = "LND"

)
@SequenceGenerator(name = "WB_CUSTOMERS_SEQ", sequenceName = "WB_CUSTOMERS_SEQ")
public class WbCustomer {
	private Long customerIdPK;
	private Long id;
	private String code;
	private String name;
	private byte[] img;
	private Date dateCreated;
	private Date lastUpdated;

	@Id
	@GeneratedValue(generator = "WB_CUSTOMERS_SEQ", strategy = GenerationType.AUTO)
	@Column(name = "CustomerID_PK", unique = true, nullable = false)
	public Long getCustomerIdPK() {
		return customerIdPK;
	}

	public void setCustomerIdPK(Long customerIdPK) {
		this.customerIdPK = customerIdPK;
	}

	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "CId")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "CCode")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "CName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Column(name = "CImg")
	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

}
