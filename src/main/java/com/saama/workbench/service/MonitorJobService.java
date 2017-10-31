package com.saama.workbench.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.dao.MonitorJobDao;

@Service
@Transactional
public class MonitorJobService implements IMonitorJobService{
    
	
	@Autowired
	private MonitorJobDao MonitorJobdao;
	@Override
	public List<AuditJobsBean> getJobRunStatistics(Map<String, Object> hmInput) {
		// TODO Auto-generated method stub
		List<AuditJobsBean> listAuditProcessView = new ArrayList<AuditJobsBean>();
		try{
			listAuditProcessView = MonitorJobdao.getJobRunStatistics(hmInput);
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return listAuditProcessView;
	}

}
