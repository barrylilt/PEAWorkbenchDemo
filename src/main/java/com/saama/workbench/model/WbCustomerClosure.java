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
@Table(name="WB_CUSTOMERS_CLOSURE"
    ,schema="LND"
   
)
@SequenceGenerator(name = "WB_CUSTOMERS_CLOSURE_SEQ", sequenceName = "WB_CUSTOMERS_CLOSURE_SEQ")
public class WbCustomerClosure {

	private Long custClosureId;
	private String ancestor;
	private String descendent;
	private Long length;
	private Date createDate;
	
	public WbCustomerClosure() {
		
	}
	
	public WbCustomerClosure (WbCustomerClosure old) {
		
		this.setAncestor(old.getAncestor());
		this.setDescendent(old.getDescendent());
		this.setLength(old.getLength());

	}

	@Id 
	@GeneratedValue(generator = "WB_CUSTOMERS_CLOSURE_SEQ", strategy = GenerationType.AUTO)
	@Column(name="Cust_Closure_id", unique=true, nullable=false)
	public Long getCustClosureId() {
		return custClosureId;
	}

	public void setCustClosureId(Long custClosureId) {
		this.custClosureId = custClosureId;
	}

	@Column(name = "Ancestor")
	public String getAncestor() {
		return ancestor;
	}

	public void setAncestor(String ancestor) {
		this.ancestor = ancestor;
	}

	@Column(name="Descendent")
	public String getDescendent() {
		return descendent;
	}

	public void setDescendent(String descendent) {
		this.descendent = descendent;
	}

	@Column(name="Length")
	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	@Generated(GenerationTime.INSERT)
	@Column(name="create_dt")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
