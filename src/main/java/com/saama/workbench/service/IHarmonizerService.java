package com.saama.workbench.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

/**
 * @author : Saama Technologies Inc.
 * @class : IExceptionService
 */
@Service
public interface IHarmonizerService {

	
	public String getJobIdByName(String jobName) throws Exception;
	
	public String chkLastJobStatus(String jobid) throws Exception;

	public void saveStJobParams(String stepName, Map<String, String> paramMap) throws Exception;

	public String getUserCountryCode(String userID);
	
	public String getStatusByRunId(String stRunId , String stJobId);

	
}
