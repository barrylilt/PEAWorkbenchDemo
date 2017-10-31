package com.saama.workbench.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.service.IMonitorJobService;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;

@Controller
public class MonitorJobController {
      
	private static final Logger logger = Logger.getLogger(MonitorJobController.class);
	@Autowired
	IMonitorJobService monitorJobService;
	
	@RequestMapping(value = "/monitorJobLogs", method = RequestMethod.GET)
	public ModelAndView monitorJobLogs(ModelMap model, HttpSession session) {
		ModelAndView mav = new ModelAndView("monitorJobLogs");
		try {
			
		} catch (Exception e) {
			logger.error("Exception in monitorJobLogs", e);
		}
		return mav;
	}
	
	@RequestMapping(value = "/jobRunStatistics", method = RequestMethod.GET)
	public void jobRunStatistics(ModelMap model, HttpSession session,HttpServletResponse response ) {
		ModelAndView mav = new ModelAndView("monitorJobLogs");
		List<AuditJobsBean> lstAuditProcessView  = new ArrayList<AuditJobsBean>();
		Map<String, Object> hmInput = new HashMap<String, Object>();
		
		try {
			
			hmInput.put(AppConstants.DATE_FORMAT, PEAUtils.getDateFormat(AppConstants.DASHBOARD_JOBRUNSTATISTICS));
			
			lstAuditProcessView = monitorJobService.getJobRunStatistics(hmInput);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(new Gson().toJson(lstAuditProcessView));
			printWriter.flush();
		} catch (Exception e) {
			logger.error("Exception in monitorJobLogs", e);
		}
		
	}
	
	
	
	
}
