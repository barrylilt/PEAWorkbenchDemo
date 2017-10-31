package com.saama.workbench.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.saama.workbench.bean.DataAvailabilityStatsBean;
import com.saama.workbench.bean.JobRunStatsBean;

@Service
public interface IDashboardService {
	List<DataAvailabilityStatsBean> getDataAvailabilityStatsData(Map<String, Object> inputParams) throws Exception;
	List<JobRunStatsBean> getJobRunStatsData(Map<String, Object> inputParams) throws Exception;
}
