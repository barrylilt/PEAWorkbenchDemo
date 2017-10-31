package com.saama.workbench.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.saama.workbench.bean.HierarchyNodeAjaxBean;
import com.saama.workbench.service.IHierarchyService;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;

@Controller
@RequestMapping(value="/hierarchy")
public class HierarchyController {

	private static final Logger logger = Logger.getLogger(HierarchyController.class);

	@Autowired
	private IHierarchyService hierarchyService;
	
	@RequestMapping(value = "/{type}/data", method = RequestMethod.GET)
	public void getHierarchyData(ModelMap model, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="type") String type) throws Exception {
		
		Map<String, Object> hmInput = new HashMap<>();
		Map<String, Object> hmOutput = new HashMap<>();
		
		hmInput.put(AppConstants.ROOT_ID, request.getParameter(AppConstants.ROOT_ID));
		hmInput.put(AppConstants.TYPE, type);
		
		hmOutput = hierarchyService.getData(hmInput);
		
		PrintWriter pw = response.getWriter();
		pw.print(new Gson().toJson(hmOutput));
		pw.flush();

	}
	
	@RequestMapping(value = "/initData", method = RequestMethod.GET)
	public void getHierarchyInitData(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String, Object> hmInput = new HashMap<>();
		Map<String, Object> hmOutput = new HashMap<>();
		
		if (request.getParameter(AppConstants.FORCED) != null) {
			hmInput.put(AppConstants.FORCED, request.getParameter(AppConstants.FORCED));
		}
		
		Map<String, Object> hmInitData = hierarchyService.getInitData(hmInput);
		
		hmOutput.put(AppConstants.DATA, hmInitData);
		
		PrintWriter pw = response.getWriter();
		pw.print(new Gson().toJson(hmOutput));
		pw.flush();

	}
	
	@RequestMapping(value = "/{type}/addIntoHierarchy", method = RequestMethod.POST)
	public void addIntoProductHierarchy(ModelMap model, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="type") String type, HierarchyNodeAjaxBean hierarchyNodeAjaxBean) throws Exception {
		
		Map<String, Object> hmInput = new HashMap<>();
		Map<String, Object> hmOutput = new HashMap<>();
		
		try {
			
			if (hierarchyNodeAjaxBean.getCode() == null || hierarchyNodeAjaxBean.getName() == null || hierarchyNodeAjaxBean.getNodeId() == null)
				throw new Exception ("Please provide proper Code / Name / Parent");
			
			hmInput.put(AppConstants.CODE, hierarchyNodeAjaxBean.getCode());
			hmInput.put(AppConstants.NAME, hierarchyNodeAjaxBean.getName());
			hmInput.put(AppConstants.PARENT_ID, hierarchyNodeAjaxBean.getNodeId());
			hmInput.put(AppConstants.TYPE, type);
			
			if (hierarchyNodeAjaxBean.getPhoto() != null && hierarchyNodeAjaxBean.getPhoto().getSize() > 0) {
				hmInput.put(AppConstants.NODE_IMAGE, hierarchyNodeAjaxBean.getPhoto().getBytes());
			}
			
			hmOutput = hierarchyService.addIntoHierarchy(hmInput);
		}
		catch(Exception e)  { 
			hmOutput.put(AppConstants.SUCCESS, false);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			
			logger.error("Exception in addIntoProductHierarchy", e);
		}
		
		PrintWriter pw = response.getWriter();
		pw.print(new Gson().toJson(hmOutput));
		pw.flush();
		
	}
	
	@RequestMapping(value = "/{type}/deleteHierarchy", method = RequestMethod.POST)
	public void deleteProductHierarchy(ModelMap model, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="type") String type) throws Exception {
		Map<String, Object> hmInput = new HashMap<>();
		Map<String, Object> hmOutput = new HashMap<>();
		
		try {
			
			Object parentId = request.getParameter(AppConstants.PARENT_ID);
			
			if (parentId == null)
				throw new Exception ("Please provide proper Product Id");
			
			hmInput.put(AppConstants.PARENT_ID, parentId);
			hmInput.put(AppConstants.TYPE, type);
			
			hmOutput = hierarchyService.deleteHierarchy(hmInput);
		}
		catch(Exception e)  { 
			hmOutput.put(AppConstants.SUCCESS, false);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			
			logger.error("Exception in deleteProductHierarchy", e);
		}
		
		PrintWriter pw = response.getWriter();
		pw.print(new Gson().toJson(hmOutput));
		pw.flush();
	}
	
	@RequestMapping(value = "/{type}/editHierarchy", method = RequestMethod.POST)
	public void editProductHierarchy(ModelMap model, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="type") String type, HierarchyNodeAjaxBean hierarchyNodeAjaxBean) throws Exception {
		Map<String, Object> hmInput = new HashMap<>();
		Map<String, Object> hmOutput = new HashMap<>();
		
		try {
			Object oNodeId = hierarchyNodeAjaxBean.getNodeId();
			Object oNodeName = hierarchyNodeAjaxBean.getName();
			
			if (oNodeId == null || PEAUtils.isEmpty(oNodeId.toString()))
				throw new Exception ("Please provide Product Id");
			
			if (oNodeName == null || PEAUtils.isEmpty(oNodeName.toString()))
				throw new Exception ("Please provide Product Name");
			
			hmInput.put(AppConstants.NODEID, oNodeId);
			hmInput.put(AppConstants.NODENAME, oNodeName);
			hmInput.put(AppConstants.TYPE, type);
			
			if (hierarchyNodeAjaxBean.getPhoto().getSize() > 0)
				hmInput.put(AppConstants.NODE_IMAGE, hierarchyNodeAjaxBean.getPhoto().getBytes());
			
			hmOutput = hierarchyService.editHierarchy(hmInput);
		}
		catch(Exception e)  { 
			hmOutput.put(AppConstants.SUCCESS, false);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			
			logger.error("Exception in editProductHierarchy", e);
		}
		
		PrintWriter pw = response.getWriter();
		pw.print(new Gson().toJson(hmOutput));
		pw.flush();
	}

}
