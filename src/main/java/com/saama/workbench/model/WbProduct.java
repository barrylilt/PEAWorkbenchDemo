package com.saama.workbench.model;

import java.sql.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;

@Entity
@Table(name = "WB_PRODUCT", schema = "LND"

)
@SequenceGenerator(name = "WB_PRODUCT_SEQ", sequenceName = "WB_PRODUCT_SEQ")
public class WbProduct {
	private Long productIdPK;
	private Long id;
	private String code;
	private String name;
	private byte[] img;
	private Date dateCreated;
	private Date lastUpdated;

	@Id
	@GeneratedValue(generator = "WB_PRODUCT_SEQ", strategy = GenerationType.AUTO)
	@Column(name = "ProductID_PK", unique = true, nullable = false)
	public Long getProductIdPK() {
		return productIdPK;
	}

	public void setProductIdPK(Long productIdPK) {
		this.productIdPK = productIdPK;
	}

	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "PId")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "PCode")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "PName")
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

	@Column(name = "PImg")
	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	// @OneToOne(fetch=FetchType.LAZY)
	// @JoinTable(schema = "LND", name = "WB_PRODUCT_TAXONOMY_TEST", joinColumns
	// = { @JoinColumn(name = "Child_ID") }, inverseJoinColumns = {
	// @JoinColumn(name = "Parent_ID")})
	// public WbProduct getParent() {
	// return parent;
	// }
	//
	// public void setParent(WbProduct parent) {
	// this.parent = parent;
	// }
	//
	// @OneToMany(fetch=FetchType.LAZY)
	// @JoinTable(schema = "LND", name = "WB_PRODUCT_TAXONOMY_TEST", joinColumns
	// = { @JoinColumn(name = "Parent_ID") }, inverseJoinColumns = {
	// @JoinColumn(name = "child_id")})
	// public Set<WbProduct> getChildren() {
	// return children;
	// }
	//
	// public void setChildren(Set<WbProduct> children) {
	// this.children = children;
	// }

}
