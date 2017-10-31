package com.saama.workbench.model;
// Generated 8 Jan, 2016 2:57:57 PM by Hibernate Tools 3.6.0


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * StRun generated by hbm2java
 */
@Entity
@Table(name="st_run",schema="harm_app")
public class StRun  implements java.io.Serializable {


     private String runId;
     private String stJob;
     private String stStep;
     private String status;
     private Date startDt;
     private Date endDt;
     private Byte wasScheduled;
     private Integer queuePosition;

    public StRun() {
    }

	
    public StRun(String runId, String status) {
        this.runId = runId;
        this.status = status;
    }
    public StRun(String runId, String stJob, String stStep, String status, Date startDt, Date endDt, Byte wasScheduled, Integer queuePosition) {
       this.runId = runId;
       this.stJob = stJob;
       this.stStep = stStep;
       this.status = status;
       this.startDt = startDt;
       this.endDt = endDt;
       this.wasScheduled = wasScheduled;
       this.queuePosition = queuePosition;
    }
   
     @Id 

    
    @Column(name="run_id", unique=true, nullable=false)
    public String getRunId() {
        return this.runId;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }

//@ManyToOne(fetch=FetchType.LAZY)
    @Column(name="job_id")
    public String getStJob() {
        return this.stJob;
    }
    
    public void setStJob(String stJob) {
        this.stJob = stJob;
    }

//@ManyToOne(fetch=FetchType.LAZY)
    @Column(name="step_id")
    public String getStStep() {
        return this.stStep;
    }
    
    public void setStStep(String stStep) {
        this.stStep = stStep;
    }

    
    @Column(name="status", nullable=false)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="start_dt", length=23)
    public Date getStartDt() {
        return this.startDt;
    }
    
    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="end_dt", length=23)
    public Date getEndDt() {
        return this.endDt;
    }
    
    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    
    @Column(name="was_scheduled")
    public Byte getWasScheduled() {
        return this.wasScheduled;
    }
    
    public void setWasScheduled(Byte wasScheduled) {
        this.wasScheduled = wasScheduled;
    }

    
    @Column(name="queue_position")
    public Integer getQueuePosition() {
        return this.queuePosition;
    }
    
    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }


}

