package com.saama.workbench.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;





import com.saama.workbench.bean.PEAUserBean;
import com.saama.workbench.dao.HarmonizerDao;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;

/**
 * @author : Saama Technologies Inc.
 * @class : ExceptionService
 */
@Service
@Transactional
public class HarmonizerService implements IHarmonizerService {

	private static final Logger logger = Logger.getLogger(HarmonizerService.class);
	
	@Autowired
	private HarmonizerDao harmonizerDao;

	
	@Override
	public void saveStJobParams(String stepName,Map<String, String> paramMap)
			throws Exception {
		// TODO Auto-generated method stub
		harmonizerDao.saveJobParamsToDB(stepName,paramMap);
	}
	
	@Override
	public String getJobIdByName(String jobName) throws Exception {
		// TODO Auto-generated method stub
		return harmonizerDao.getJobIdByName(jobName);
	}
	
	@Override
	public String chkLastJobStatus(String jobid) throws Exception {
		// TODO Auto-generated method stub
		return harmonizerDao.chkLastJobStatus(jobid);
	}



	@Override
	public String getUserCountryCode(String userID) {
		return harmonizerDao.getUserCountryCode(userID);
	}


	public void setUserBean(HttpServletRequest request) {
		
		try {
			HttpSession session = request.getSession();
			String userId = (String) request.getParameter("UserID");
			String datasetId = (String) request.getParameter("DatasetID");
			PEAUserBean peaUserBean = new PEAUserBean();
//			if (userId != null) {
//				peaUserBean.setUserId(userId);
//			} else {
//				peaUserBean.setUserId(PEAUtils.getUserId());
//			}
//			if (datasetId != null) {
//				peaUserBean.setDatasetId(datasetId);
//			} else {
//				peaUserBean.setDatasetId(PEAUtils.getDatasetId());
//			}
			String countryCode = getUserCountryCode(userId);
			String locale = "en_" + countryCode.toUpperCase(); //(String) request.getParameter("Locale");
			if(locale != null){
				peaUserBean.setUserLocale(new Locale("en_"+countryCode.toUpperCase()));
				peaUserBean.setCountryCode(countryCode);
			} else {
				peaUserBean.setUserLocale(new Locale("en_US"));
				peaUserBean.setCountryCode("US");
			}
			session.setAttribute("peaUserBean", peaUserBean);
		}
		catch(Exception e) {
			logger.error("Exception in setUserBean - ", e);
		}
	}

		
	@Override
	public String getStatusByRunId(String stRunId , String stJobId) {
		return harmonizerDao.getStatusByRunId(stRunId, stJobId);
	}
	
	
}
