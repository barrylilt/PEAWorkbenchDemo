package com.saama.workbench.model;


import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="AUDIT_PROCESS_VIEW"
    ,schema="AUX"    
)
public class AuditProcessView  implements java.io.Serializable {


     private Long auditProcessViewId;
     private Long auditLogId;
     private String layer;
     private String fileName;
     private String processName;
     private Date startTime;
     private Date endTime;
     private Integer recordsRead;
     private Integer recordsWritten;
     private Integer rejects;
     private String status;
     private String failureReason;
     private Integer batchId;
     private Date createdDate;
     private String createdBy;
     private Date updatedDate;
     private String updatedBy;
     private String source;

    public AuditProcessView() {
    }

    public AuditProcessView(Long auditProcessViewId,Long auditLogId, String layer, String fileName, String processName, Date startTime, Date endTime, Integer recordsRead, Integer recordsWritten, Integer rejects, String status, String failureReason, Integer batchId, Date createdDate, String createdBy, Date updatedDate, String updatedBy, String source) {
       this.auditProcessViewId = auditProcessViewId;
       this.auditLogId = auditLogId;
       this.layer = layer;
       this.fileName = fileName;
       this.processName = processName;
       this.startTime = startTime;
       this.endTime = endTime;
       this.recordsRead = recordsRead;
       this.recordsWritten = recordsWritten;
       this.rejects = rejects;
       this.status = status;
       this.failureReason = failureReason;
       this.batchId = batchId;
       this.createdDate = createdDate;
       this.createdBy = createdBy;
       this.updatedDate = updatedDate;
       this.updatedBy = updatedBy;
       this.source = source;
    }
   
      

    @Id 
    @Column(name="AuditProcessViewId", unique=true, nullable=false)
    public Long getAuditProcessViewId() {
		return auditProcessViewId;
	}

	public void setAuditProcessViewId(Long auditProcessViewId) {
		this.auditProcessViewId = auditProcessViewId;
	}
    
    @Column(name="AuditLogId")
    public Long getAuditLogId() {
        return this.auditLogId;
    }
    
    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
    }


   

	@Column(name="Layer", length=64)
    public String getLayer() {
        return this.layer;
    }
    
    public void setLayer(String layer) {
        this.layer = layer;
    }


    @Column(name="FileName", length=64)
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Column(name="ProcessName", length=64)
    public String getProcessName() {
        return this.processName;
    }
    
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="StartTime", length=23)
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EndTime", length=23)
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    @Column(name="RecordsRead")
    public Integer getRecordsRead() {
        return this.recordsRead;
    }
    
    public void setRecordsRead(Integer recordsRead) {
        this.recordsRead = recordsRead;
    }


    @Column(name="RecordsWritten")
    public Integer getRecordsWritten() {
        return this.recordsWritten;
    }
    
    public void setRecordsWritten(Integer recordsWritten) {
        this.recordsWritten = recordsWritten;
    }


    @Column(name="Rejects")
    public Integer getRejects() {
        return this.rejects;
    }
    
    public void setRejects(Integer rejects) {
        this.rejects = rejects;
    }


    @Column(name="Status", length=64)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }


    @Column(name="FailureReason", length=64)
    public String getFailureReason() {
        return this.failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }


    @Column(name="BatchID")
    public Integer getBatchId() {
        return this.batchId;
    }
    
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CreatedDate", length=23)
    public Date getCreatedDate() {
        return this.createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }


    @Column(name="CreatedBy", length=64)
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UpdatedDate", length=23)
    public Date getUpdatedDate() {
        return this.updatedDate;
    }
    
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }


    @Column(name="UpdatedBy", length=64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }


    @Column(name="Source", length=64)
    public String getSource() {
        return this.source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }



}



