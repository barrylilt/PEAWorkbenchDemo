package com.saama.workbench.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "WB_CUSTOMERS_TAXONOMY", schema = "LND"

)
@SequenceGenerator(name = "WB_CUSTOMERS_TAXONOMY_SEQ", sequenceName = "WB_CUSTOMERS_TAXONOMY_SEQ")
public class WbCustomerTaxonomy {
	private Long custTaxonomyIdPK;
	private Long parentId;
	private Long childId;
	private Long level;
	private Date dateCreated;
	private Date lastUpdated;

	@Id
	@GeneratedValue(generator = "WB_CUSTOMERS_TAXONOMY_SEQ", strategy = GenerationType.AUTO)
	@Column(name = "Cust_taxonomy_ID_PK", unique = true, nullable = false)
	public Long getCustTaxonomyIdPK() {
		return custTaxonomyIdPK;
	}

	public void setCustTaxonomyIdPK(Long custTaxonomyIdPK) {
		this.custTaxonomyIdPK = custTaxonomyIdPK;
	}

	@Column(name = "Parent_ID")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Column(name = "Child_ID")
	public Long getChildId() {
		return childId;
	}

	public void setChildId(Long childId) {
		this.childId = childId;
	}

	@Column(name = "Level")
	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	@Generated(GenerationTime.INSERT)
	@Column(name = "DateCreated")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Generated(GenerationTime.ALWAYS)
	@Column(name = "LastUpdated")
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
