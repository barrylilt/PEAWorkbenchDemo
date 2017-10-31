package com.saama.workbench.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saama.workbench.model.StStep;
import com.saama.workbench.model.StStepParameter;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.DateUtils;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

/**
 * @author : Saama Technologies Inc.
 * @class : ManageExceptionDao
 */
@Repository
public class HarmonizerDao {

	@Autowired
	SessionFactory sessionFactory;

	private static final Logger logger = Logger
			.getLogger(HarmonizerDao.class);

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	static Map ruleNameDescMap = new HashMap();
	static List<String> ruleFieldsMetaDataList = new ArrayList<String>();


		

	
	public void saveJobParamsToDB(String stepName, Map<String, String> paramMap)
			throws Exception {
		try {	
			
			String StepId;
			
			String updateQry = "UPDATE " + AppConstants.HARMONIZER_DATABASE + ".harm_app.st_step_parameter SET PARAM_VAL = :PARAM_VAL, LAST_AUDIT_TIME = :LAST_AUDIT_TIME WHERE STEP_ID IN (SELECT STEP_ID FROM " + AppConstants.HARMONIZER_DATABASE + ".HARM_APP.ST_STEP WHERE STEP_NAME = :STEP_NAME) AND PARAM_NAME = :PARAM_NAME";

			
			Session session = sessionFactory.getCurrentSession();
			
			for (Map.Entry<String, String> en : paramMap.entrySet()) {
				
				SQLQuery sqlQry = session.createSQLQuery(updateQry);
				
				sqlQry.setString("STEP_NAME", stepName);
				sqlQry.setString("PARAM_VAL", en.getValue());
				sqlQry.setDate("LAST_AUDIT_TIME", new Date());
				sqlQry.setString("PARAM_NAME", en.getKey());
				
				sqlQry.executeUpdate();
			}
			
//			Criteria criteria = session.createCriteria(StStep.class).add(Restrictions.eq("stepName", stepName));			
//			stepList = criteria.list();
						
//			for (StStep ststep : stepList) {
//				Iterator sItr = ststep.getStStepParameters().iterator();
//				while (sItr.hasNext()) {
//					StStepParameter stStepParameter = new StStepParameter();
//					stStepParameter=(StStepParameter) sItr.next();
//					stStepParameter.setParamVal(paramMap.get(stStepParameter.getParamName()));
//					stStepParameter.setLastAuditTime(DateUtils.getUpdateDate());
//					session.update(stStepParameter);					
//				}				
//			}			
			
		} catch (Exception e) {
			logger.error("Exception in saveUserSettings method :", e);
		}
	}
	
	
	public String getJobIdByName(String jobName) throws Exception {
		String jobId = null;
		try {
			
			Session session = sessionFactory.getCurrentSession();
			SQLQuery qry = session
					.createSQLQuery("select job_id from " + AppConstants.HARMONIZER_DATABASE + ".harm_app.st_job where job_name='"+ jobName +"'");
			jobId = (String) qry.list().get(0);
			
			session.clear();
		} catch (Exception e) {
			logger.error("Exception in getRuleNameDescriptionMap method :" + e);
		}

		return jobId;
	}
	
	public String chkLastJobStatus(String jobId) throws Exception {
		String Status = null;
		try {
			
			Session session = sessionFactory.getCurrentSession();
			SQLQuery qry = session.createSQLQuery("select status from " + AppConstants.HARMONIZER_DATABASE + ".harm_app.st_run where job_id='"+ jobId +"' order by start_dt desc");
			Status = (String) qry.list().get(0);
			
			session.clear();
		} catch (Exception e) {
			logger.error("Exception in getRuleNameDescriptionMap method :" + e);
		}

		return Status;
	}


	
	public String getUserCountryCode(String userID) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "", countryCode = "US";
		
		sql = "select countryCode "
				+ " from peaworkbench.UserProfile  where userID = '"+userID+"' ";
		Query query = session.createSQLQuery(sql);
		List list = query.list();
		if ((Object) list.get(0) != null) {
			if (list.get(0) != null)
				countryCode =  ((Object) list.get(0)).toString();
		}
		return countryCode.toUpperCase();
	}

	
	public String getStatusByRunId(String stRunId , String stJobId) {
		 
		String sql = "select status from " + AppConstants.HARMONIZER_DATABASE + ".harm_app.st_run where run_id = :runID and job_id = :jobID";

		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery(sql);
		query.setParameter("runID", stRunId);
		query.setParameter("jobID", stJobId);
		logger.info(query.getQueryString());
		List<Object[]> list = query.list();
		logger.info("Current Status : "+list.get(0));
		return ""+list.get(0);
	}
}
