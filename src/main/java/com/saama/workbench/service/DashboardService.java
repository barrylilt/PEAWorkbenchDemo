package com.saama.workbench.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.saama.workbench.bean.DataAvailabilityStatsBean;
import com.saama.workbench.bean.JobRunStatsBean;

@Service
//@Transactional
public class DashboardService implements IDashboardService {

	private static final Logger logger = Logger.getLogger(DashboardService.class);
	
	@Override
	public List<DataAvailabilityStatsBean> getDataAvailabilityStatsData(Map<String, Object> inputParams) throws Exception {
		List<DataAvailabilityStatsBean> listDataAvailabilityStats = new ArrayList<DataAvailabilityStatsBean>();

		try {
			
			listDataAvailabilityStats.add(new DataAvailabilityStatsBean("March", 1257463, 1211111, 9d));
			listDataAvailabilityStats.add(new DataAvailabilityStatsBean("April", 2232431, 2121342, 8d));
			listDataAvailabilityStats.add(new DataAvailabilityStatsBean("May", 2423326, 2343125, 15d));

			return listDataAvailabilityStats;
			
		} catch(Exception e) {
			logger.error("Exception in getDataAvailabilityStatsData - " + e.getMessage());
			throw e;
		}
	}

	@Override
	public List<JobRunStatsBean> getJobRunStatsData(Map<String, Object> inputParams) {
		List<JobRunStatsBean> listJobRunStatsBean = new ArrayList<JobRunStatsBean>();

		try {
			listJobRunStatsBean.add(new JobRunStatsBean("Siebel", "2016-05-03 11:12", "N"));
			listJobRunStatsBean.add(new JobRunStatsBean("Sell-Out", "2016-05-03 11:12", "N"));
			listJobRunStatsBean.add(new JobRunStatsBean("Sell-In", "2016-05-03 11:12", "Y"));
	
			return listJobRunStatsBean;
		
		} catch(Exception e) {
			logger.error("Exception in getJobRunStatsData - " + e.getMessage());
			throw e;
		}

	}
}
