package com.saama.workbench.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import com.saama.workbench.bean.DatasetBean;
import com.saama.workbench.model.UserProfile;
import com.saama.workbench.service.IHarmonizerService;
import com.saama.workbench.service.IManageDatasetService;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.DateUtils;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Controller
public class ManageDatasetController {
	
	private static final Logger logger = Logger.getLogger(ManageDatasetController.class);
	
	@Autowired
	IManageDatasetService manageDatasetService;
	
	@Autowired
	IHarmonizerService harmonizerService;
	
	@RequestMapping(value = "/createDataset", method = RequestMethod.POST)
	public void createDataset( HttpSession session,HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		PrintWriter printWriter = null;
		DatasetBean datasetBean = new DatasetBean();
		String userName = "Default";
		
		if (session.getAttribute(AppConstants.USERNAME) != null) {
			userName = session.getAttribute(AppConstants.USERNAME).toString();
		}
		
		try {
			
			UserProfile loggedInUser = (UserProfile) session.getAttribute(AppConstants.USER_OBJECT);
			
			datasetBean.setCountryCode(request.getParameter("countryCode"));
			datasetBean.setCreatedBy(userName);
			datasetBean.setCustomerMappingId(request.getParameter("customer"));		
			datasetBean.setDatasetName(request.getParameter("datasetName"));
			datasetBean.setDatasetType("EYNTK");			
			datasetBean.setNotes(request.getParameter("notes"));
			datasetBean.setProductMappingId(request.getParameter("product"));			
			String dateRange = request.getParameter("daterange");
			String startNEndDate[] =  dateRange.split("-");
			
			datasetBean.setStartingDate(DateUtils.convertToDate(startNEndDate[0], loggedInUser.getDateFormatBK()));
			datasetBean.setEndingDate(DateUtils.convertToDate(startNEndDate[1], loggedInUser.getDateFormatBK()));
			datasetBean = manageDatasetService.createDataset(datasetBean);
			printWriter = response.getWriter();
			
			// ============ Trigger Harmonizer JOB. ============
			Map<String, String> params = new HashMap<String, String>();
			params.put("DatasetID", Long.toString(datasetBean.getDatasetId()));
			
			PEAUtils.runKJB(
				PropertiesUtil.getProperty(AppConstants.JOB_CREATE_DATASET_NAME), 
				PropertiesUtil.getProperty(AppConstants.STEP_CREATE_DATASET_NAME),
				params,
				harmonizerService
			);
			// ============
			
			if (datasetBean.getDatasetId() > 0) {
				printWriter.print("Dataset created successfully");
				printWriter.flush();
			}
			
		} catch (Exception e) {
			logger.error("Exception in createDataset", e);
			if (datasetBean.getDatasetId() > 0) {
				try {
					manageDatasetService.deleteDataset(datasetBean.getDatasetId());
				} catch (Exception ex) {
					logger.error("Exception while deleting dataset", e);
				}
			}
			printWriter.print(e.getMessage());
			printWriter.flush();
		}
	}
	
	
	@RequestMapping(value = "/getListDatasets", method = RequestMethod.GET)
	public void getListDataset(HttpSession session,HttpServletResponse response) throws Exception {
		//ModelAndView mav = new ModelAndView("createDataset");
		PrintWriter printWriter = null;
		List<DatasetBean> listDataSetBean = new ArrayList<DatasetBean>();
		try {
			 listDataSetBean =  manageDatasetService.listDataSetBean();
			 printWriter = response.getWriter();
			 printWriter.print(new Gson().toJson(listDataSetBean));
		     printWriter.flush();
		} catch (Exception e) {
			printWriter.print(e.getMessage());
			logger.error("Exception in createDataset", e);
		}
		
	}
	
	@RequestMapping(value = "/makeDatasetInactive", method = RequestMethod.POST)
	public void makeDatasetInactive(ModelMap model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		String username = "DevUser", outMsg = "Dataset deleted successfully";
		boolean datasetDeleted = false;
		
		Map<String, String> output = new HashMap<String, String>(); 
		
		if (session.getAttribute(AppConstants.USERNAME) != null) {
			username = session.getAttribute(AppConstants.USERNAME).toString();
		}
		try {
			if (request.getParameter("datasetId") != null) {
				long datasetId = Long.parseLong(request.getParameter("datasetId"));
				datasetDeleted = manageDatasetService.makeDatasetInActive(datasetId);
			}
		} catch (Exception e) {
			datasetDeleted = false;
			outMsg = e.getMessage();
			logger.error("Exception in deleteDataset", e);
		}
		
		output.put(AppConstants.SUCCESS, Boolean.toString(datasetDeleted));
		output.put(AppConstants.MESSAGE, outMsg);
		
		PrintWriter out = response.getWriter();
		out.print(new Gson().toJson(output));
		out.flush();
	}
	
	@RequestMapping(value = "/exportDataset", method = RequestMethod.GET)
	public ModelAndView exportDataset(ModelMap model, HttpSession session) throws Exception {
		ModelAndView mav = new ModelAndView("exportDataset");
		try {
			
		} catch (Exception e) {
			logger.error("Exception in exportDataset", e);
		}
		return mav;
	}
}
