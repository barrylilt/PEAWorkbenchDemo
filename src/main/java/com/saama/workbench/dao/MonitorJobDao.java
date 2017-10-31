package com.saama.workbench.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.util.AppConstants;

@Repository
public class MonitorJobDao {
	private static final Logger logger = Logger.getLogger(MonitorJobDao.class);
	@Autowired
	SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public List<AuditJobsBean> getJobRunStatistics(Map<String, Object> hmInput) {
		// TODO Auto-generated method stub
		
		List<AuditJobsBean> lstAuditProcessView = new ArrayList<AuditJobsBean>();
		List<Object> arrObj =  new ArrayList<Object>();
		List<String> subjectArea =  new ArrayList<String>();
		
		Session session = null;
		try{
//			session = getSessionFactory().getCurrentSession();
			session = getSessionFactory().openSession();
			String hql = "select subjectArea, max(endtime), status from AUX.AUDIT_JOBS where subjectArea in ('Promotion', 'SellIn', 'SellOut') and status='success' and endtime IS NOT NULL and subjectArea IS NOT NULL and status IS NOT NULL  group by subjectarea, status  order by max(endtime), subjectArea, status  ";
			Query query = session.createSQLQuery(hql);
			arrObj = query.list();
			
			SimpleDateFormat format1 = new SimpleDateFormat(hmInput.get(AppConstants.DATE_FORMAT).toString());
			AuditJobsBean auditJobs = null;
			for(Object arrObj1:arrObj){
				 Object[] objArr = (Object[]) arrObj1;
				 auditJobs = new AuditJobsBean();				
				 auditJobs.setSubjectArea((String)objArr[0]);
//				 if(auditJobs.getSubjectArea().equalsIgnoreCase("NA")){
//					 auditJobs.setSubjectArea("Internal");
//				 }
				 String Date = format1.format((Date)objArr[1]);
				 auditJobs.setEndTime(Date);
				 auditJobs.setStatus((String)objArr[2]);
				 if(!subjectArea.contains((String)objArr[0])){
				  lstAuditProcessView.add(auditJobs); 
				  subjectArea.add((String)objArr[0]);
				 }
			}
				
	
			
		}
		catch(Exception e){
			logger.error("Exception in getJobRunStatistics - " + e.getMessage());
		}
		finally {
			if (session != null)
				session.close();
		}
		return lstAuditProcessView;
	}
	
	

}
