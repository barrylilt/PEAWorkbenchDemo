package com.saama.workbench.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.model.AuditJobs;
import com.saama.workbench.model.AuditProcessView;


@Service
public interface IMonitorJobService {

	List<AuditJobsBean> getJobRunStatistics(Map<String, Object> hmInput);

}
