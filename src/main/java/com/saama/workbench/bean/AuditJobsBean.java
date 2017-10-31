package com.saama.workbench.bean;

import java.util.Date;

public class AuditJobsBean {

	
	
	private String layer;
    private String source;
    private String fileName;
    private String processName;
    private String jobName;
    private String transformationName;
    private String startTime;
    private String endTime;
    private Integer recordsRead;
    private Integer recordsWritten;
    private Integer rejects;
    private String status;
    private String subjectArea;
    private String createdBy;
    private Date createdDate;
    
    
	public String getLayer() {
		return layer;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getTransformationName() {
		return transformationName;
	}
	public void setTransformationName(String transformationName) {
		this.transformationName = transformationName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String string) {
		this.startTime = string;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String string) {
		this.endTime = string;
	}
	public Integer getRecordsRead() {
		return recordsRead;
	}
	public void setRecordsRead(Integer recordsRead) {
		this.recordsRead = recordsRead;
	}
	public Integer getRecordsWritten() {
		return recordsWritten;
	}
	public void setRecordsWritten(Integer recordsWritten) {
		this.recordsWritten = recordsWritten;
	}
	public Integer getRejects() {
		return rejects;
	}
	public void setRejects(Integer rejects) {
		this.rejects = rejects;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubjectArea() {
		return subjectArea;
	}
	public void setSubjectArea(String subjectArea) {
		this.subjectArea = subjectArea;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
    
    
    
}
