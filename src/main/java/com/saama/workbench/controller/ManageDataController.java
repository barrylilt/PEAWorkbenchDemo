package com.saama.workbench.controller;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.MapBean;
import com.saama.workbench.bean.UploadVersion;
import com.saama.workbench.service.IManageDataService;
import com.saama.workbench.util.AppConstants;

@Controller
public class ManageDataController {

	private static final Logger logger = Logger
			.getLogger(ManageDataController.class);

	@Autowired
	IManageDataService manageDataService;

	@RequestMapping(value = "/configureDataFiles", method = RequestMethod.GET)
	public ModelAndView configureDataFiles(ModelMap model, HttpSession session) {
		ModelAndView mav = new ModelAndView("configureDataFiles");
		try {

		} catch (Exception e) {
			logger.error("Exception in configureDataFiles", e);
		}
		return mav;
	}

	@RequestMapping(value = "/uploadDataFiles", method = RequestMethod.GET)
	public ModelAndView uploadDataFiles(ModelMap model, HttpSession session) {
		ModelAndView mav = new ModelAndView("uploadDataFiles");
		try {

		} catch (Exception e) {
			logger.error("Exception in uploadDataFiles", e);
		}
		return mav;
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public void uploadFileHandler(HttpServletRequest request,
			HttpServletResponse response, UploadVersion uploadVersion,
			HttpSession session) {

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		String uploadResponse = "";
		CommonsMultipartFile file = uploadVersion.getUploadVersion();
		String fileNameFormat = uploadVersion.getFileNameFormat();
		String sourceType = uploadVersion.getSourceType();
		String fileName = uploadVersion.getFileName();
		boolean isOverwrite = uploadVersion.isCheckOverwrite();
		if (fileName == null) {
			fileName = file.getOriginalFilename();
		}
		// System.out.println(fileName+" "+isOverwrite);

		try {
			PrintWriter printWriter = response.getWriter();
			File fileLocation = null;
			String filePath = "";

			if (sourceType.equalsIgnoreCase("brand hierarchy")) {
				filePath = AppConstants.DIRECTORY_PATH + AppConstants.ESRA;
			}

			else {
				filePath = AppConstants.DIRECTORY_PATH + sourceType;
			}
			fileLocation = new File(filePath + "\\" + fileName);

			if (fileLocation.exists() && isOverwrite == false) {
				printWriter.print("File already exist.");
				printWriter.flush();
				return;

			} else {
				File newFile = new File(filePath);
				if (!newFile.exists()) {
					Path path = Paths.get(filePath);
					Files.createDirectories(path);
				}

				if (file.getSize() > 0) {

					String mimeType = mimeTypesMap.getContentType(fileLocation);
					if (!mimeType.equals("text/plain")
							&& !mimeType.equals("application/octet-stream")) {
						uploadResponse = "file type is not correct="
								+ fileNameFormat;
						printWriter.print(uploadResponse);
						printWriter.flush();
					} else {
						file.transferTo(fileLocation);
						boolean isSavedToAudit = false;
						AuditJobsBean auditJobsBean = new AuditJobsBean();
						auditJobsBean.setFileName(file.getOriginalFilename());
						auditJobsBean.setSource("Input Files");
						auditJobsBean.setLayer("LND");
						auditJobsBean.setJobName("Internal Processing");
						auditJobsBean.setProcessName("Data Acquisition");
						auditJobsBean.setSubjectArea(sourceType.toUpperCase());
						auditJobsBean.setStartTime(new Date().toString());
						auditJobsBean.setEndTime(new Date().toString());
						auditJobsBean.setStatus("Success");
						String username = (String) session
								.getAttribute(AppConstants.USERNAME);
						auditJobsBean.setCreatedBy(username);
						auditJobsBean.setCreatedDate(new Date());
						isSavedToAudit = manageDataService
								.saveToAuditJobs(auditJobsBean);
						uploadResponse = "You successfully uploaded file="
								+ file.getOriginalFilename();

						printWriter.print(uploadResponse);
						printWriter.flush();
					}
				} else {

					printWriter.print("File unable to upload");
					printWriter.flush();
				}
			}
		} catch (Exception e) {

			logger.error("into uploadFileHandler method ", e);
		}

	}

	@RequestMapping(value = "/monitorDataFileStatus", method = RequestMethod.GET)
	public ModelAndView monitorDataFileStatus(ModelMap model,
			HttpSession session) {
		ModelAndView mav = new ModelAndView("monitorDataFileStatus");
		try {

		} catch (Exception e) {
			logger.error("Exception in monitorDataFileStatus", e);
		}
		return mav;
	}

	@RequestMapping(value = "/manageLookupTables", method = RequestMethod.GET)
	public ModelAndView manageLookupTables(ModelMap model, HttpSession session) {
		ModelAndView mav = new ModelAndView("manageLookupTables");
		try {

		} catch (Exception e) {
			logger.error("Exception in manageLookupTables", e);
		}
		return mav;
	}

	@RequestMapping(value = "/getLookTableAccounts", method = RequestMethod.GET)
	public void getLookTableAccounts(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();

		input.put(AppConstants.TYPE, request.getParameter(AppConstants.TYPE));

		try {

			List<String> list = manageDataService.getLookupTableAccounts(input);

			resp.put("list", list);

			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(resp));
			out.flush();

		} catch (Exception e) {
			logger.error("Exception in getLookTableAccounts", e);
		}
	}

	@RequestMapping(value = "/getLookTableSectors", method = RequestMethod.GET)
	public void getLookTableSectors(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			List<String> list = manageDataService.getLookupTableSectors(input);

			resp.put("list", list);

			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(resp));
			out.flush();

		} catch (Exception e) {
			logger.error("Exception in getLookTableSectors", e);
		}
	}

	@RequestMapping(value = "/getExtSourceList", method = RequestMethod.GET)
	public void getExtSourceList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, String> resp = new HashMap<String, String>();
		try {
			resp = manageDataService.getExtSourceList(input);

		} catch (Exception e) {
			logger.error("Exception in getCustomerList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(resp));
			out.flush();
		}
	}
	
//	@RequestMapping(value = "/getCategoryList", method = RequestMethod.GET)
//	public void getCategoryList(ModelMap model, HttpServletRequest request,
//			HttpSession session, HttpServletResponse response) throws Exception {
//		List<MapBean> categoryList = new ArrayList<MapBean> ();
//		Map<String, Object> input = new HashMap<String, Object>();
//		input.put(AppConstants.SOURCEID, request.getParameter("sourceId"));
//		try{
//			categoryList = manageDataService.getCategoryList(input);
//		}catch(Exception e){
//			logger.error("Exception in OpsoCustomerGroups", e);
//		}
//		finally {
//			PrintWriter out = response.getWriter();
//			out.print(new Gson().toJson(categoryList));
//			out.flush();
//		}
//	}


	@RequestMapping(value = "/getMappedDataMetaData", method = RequestMethod.GET)
	public void getMappedDataMetaData(ModelMap model,
			HttpServletRequest request, HttpSession session,
			HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> outMap = new HashMap<String, Object>();
		Map<String, String> intProdList = new HashMap<String, String>();
		List<CustDIMBean> custList = new ArrayList<CustDIMBean>();

		try {

			intProdList = manageDataService.getIntProdList(input);
			custList = manageDataService.getIntCustList(input);
			Map<Integer, List<String>> categories = manageDataService.getCategoryList(input);
			
			outMap.put("Product", intProdList);
			outMap.put("Customer", custList);
			outMap.put("Categories", categories);

		} catch (Exception e) {
			logger.error("Exception in getMappedDataMetaData", e);
		}

		PrintWriter out = response.getWriter();
		out.print(new Gson().toJson(outMap));
		out.flush();
	}

	@RequestMapping(value = "/getUnMappedUniBrand", method = RequestMethod.GET)
	public void getUnMappedUniBrand(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {

		Map<String, Object> input = new HashMap<String, Object>();
		List<MapBean> brandMap = new ArrayList<MapBean>();
		try {
			brandMap = manageDataService.getUnMappedUniBrand(input);
		} catch (Exception e) {
			logger.error("Exception in getUnMappedUniBrand", e);
		}

		PrintWriter out = response.getWriter();
		out.print(new Gson().toJson(brandMap));
		out.flush();

	}
	
	@RequestMapping(value = "/{tableName}/markIgnored", method = RequestMethod.POST)
	public void markIgnored(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {
		
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> outMap = new HashMap<String, Object>();
		
		outMap.put(AppConstants.SUCCESS, true);
		outMap.put(AppConstants.MESSAGE, "Marked ignored successfully.");
		
		try {
			input.put(AppConstants.USERNAME,
					session.getAttribute(AppConstants.USERNAME));
			input.put(AppConstants.DATE,
					new Date());
			if (request.getParameter(AppConstants.EXT_PRODID) != null) {
				input.put(AppConstants.EXT_PRODID, request.getParameter(AppConstants.EXT_PRODID));
			}
			manageDataService.markExtBrandIgnored(input);
		}
		catch (Exception e) {
			outMap.put(AppConstants.SUCCESS, false);
			outMap.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in markIgnored", e);
		}
		
		PrintWriter outpw = response.getWriter();
		outpw.print(new Gson().toJson(outMap));
		outpw.flush();
		
	}
	
	@RequestMapping(value = "/{tableName}/unmapExtMapping", method = RequestMethod.POST)
	public void unmapExtMapping(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {
		
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> outMap = new HashMap<String, Object>();
		
		outMap.put(AppConstants.SUCCESS, true);
		outMap.put(AppConstants.MESSAGE, "Unmapped successfully.");
		
		try {
			if (request.getParameter(AppConstants.EXT_PRODID) != null) {
				input.put(AppConstants.EXT_PRODID, request.getParameter(AppConstants.EXT_PRODID));
			}
			if (request.getParameter(AppConstants.EXT_CUSTID) != null) {
				input.put(AppConstants.EXT_CUSTID, request.getParameter(AppConstants.EXT_CUSTID));
			}
			manageDataService.unmapExistingMapping(input);
		}
		catch (Exception e) {
			outMap.put(AppConstants.SUCCESS, false);
			outMap.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in unmapExtMapping", e);
		}
		
		PrintWriter outpw = response.getWriter();
		outpw.print(new Gson().toJson(outMap));
		outpw.flush();
		
	}
	
	@RequestMapping(value = "/{tableName}/mapExtMapping", method = RequestMethod.POST)
	public void mapExtMapping(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {
		
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> outMap = new HashMap<String, Object>();
		input.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
		input.put(AppConstants.TABLENAME, tableName);
		
		outMap.put(AppConstants.SUCCESS, true);
		outMap.put(AppConstants.MESSAGE, "Mapped successfully.");
		
		try {
			if (request.getParameter(AppConstants.EXT_PRODID) != null) {
				input.put(AppConstants.EXT_PRODID, request.getParameter(AppConstants.EXT_PRODID));
				input.put(AppConstants.INT_PRODID, request.getParameter(AppConstants.INT_PRODID));
			}
			if (request.getParameter(AppConstants.EXT_CUSTID) != null) {
				input.put(AppConstants.EXT_CUSTID, request.getParameter(AppConstants.EXT_CUSTID));
				input.put(AppConstants.INT_CUSTID, request.getParameter(AppConstants.INT_CUSTID));
			}
			manageDataService.mapExistingMapping(input);
		}
		catch (Exception e) {
			outMap.put(AppConstants.SUCCESS, false);
			outMap.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in mapExtMapping", e);
		}
		
		PrintWriter outpw = response.getWriter();
		outpw.print(new Gson().toJson(outMap));
		outpw.flush();
		
	}

	@RequestMapping(value = "/{tableName}/saveMapping", method = RequestMethod.POST)
	public void saveMapping(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {

		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		out.put(AppConstants.SUCCESS, true);
		out.put(AppConstants.MESSAGE, "Mapping is saved successfully.");

		try {
			inputParams.put(AppConstants.TABLENAME, tableName);
			inputParams.put(AppConstants.USERNAME,
					session.getAttribute(AppConstants.USERNAME));
			inputParams.put(AppConstants.DATE,
					new Date());

			if (request.getParameter(AppConstants.EXT_PRODID) != null) {
				inputParams.put(AppConstants.EXT_PRODID,
						request.getParameter(AppConstants.EXT_PRODID));
			}
			if (request.getParameter(AppConstants.SOURCEID) != null) {
				inputParams.put(AppConstants.SOURCEID,
						request.getParameter(AppConstants.SOURCEID));
			}
			if (request.getParameter(AppConstants.INT_PRODUCT) != null) {
				inputParams.put(AppConstants.INT_PRODUCT,
						request.getParameter(AppConstants.INT_PRODUCT));
			}
			out = manageDataService.saveMapping(inputParams);

			if ((boolean) out.get(AppConstants.SUCCESS)) {
				out.put(AppConstants.MESSAGE, "Mapping is saved successfully.");
			}
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in saveMapping", e);
		}

		PrintWriter outpw = response.getWriter();
		outpw.print(new Gson().toJson(out));
		outpw.flush();
	}

	@RequestMapping(value = "/{tableName}/getMappedProducts", method = RequestMethod.GET)
	public void getMappedProducts(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, tableName);
			JsonObject mapData = new JsonObject();
			if (request.getParameter(AppConstants.EXT_PRODID) != null) {
				inputParams.put(AppConstants.EXT_PRODID,
						request.getParameter(AppConstants.EXT_PRODID));
			}
			out = manageDataService.getMappedProducts(inputParams);
			
		} catch (Exception e) {
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in getMappedProducts", e);
		}

		PrintWriter outpw = response.getWriter();
		outpw.print(new Gson().toJson(out));
		outpw.flush();
	}
	
	@RequestMapping(value = "/{tableName}/getMappedCustomers", method = RequestMethod.GET)
	public void getMappedCustomers(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, tableName);
			JsonObject mapData = new JsonObject();
			if (request.getParameter(AppConstants.EXT_CUSTID) != null) {
				inputParams.put(AppConstants.EXT_CUSTID, request.getParameter(AppConstants.EXT_CUSTID));
			}
			out = manageDataService.getMappedCustomers(inputParams);
			
		} catch (Exception e) {
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in getMappedCustomers", e);
		}

		PrintWriter outpw = response.getWriter();
		outpw.print(new Gson().toJson(out));
		outpw.flush();
	}

	@RequestMapping(value = "/{tableName}/saveCustMapping", method = RequestMethod.POST)
	public void saveCustMapping(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "tableName") String tableName)
			throws Exception {
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, tableName);
			inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			inputParams.put(AppConstants.DATE, new Date());
			
			if (request.getParameter(AppConstants.EXT_CUSTID) != null) {
				inputParams.put(AppConstants.EXT_CUSTID, request.getParameter(AppConstants.EXT_CUSTID));
			}
			
			if(request.getParameter(AppConstants.SOURCEID) != null){
				inputParams.put(AppConstants.SOURCEID, request.getParameter(AppConstants.SOURCEID));
			}
			
			if(request.getParameter(AppConstants.INT_CUSTOMER) != null) {
				JsonObject joIntCustomers = (JsonObject) new JsonParser().parse(request.getParameter(AppConstants.INT_CUSTOMER));
				inputParams.put(AppConstants.INT_CUSTOMER, joIntCustomers);
			}
			
			out = manageDataService.saveCustMapping(inputParams);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in manageMapping", e);
		}
		PrintWriter print = response.getWriter();
		print.print(new Gson().toJson(out));
		print.flush();
	}
	
	@RequestMapping(value = "/manageBR/{status}", method = RequestMethod.POST)
	public void deleteBR(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "status") String status)
			throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, AppConstants.MANAGE_BUSINESS_RULES);
			inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			inputParams.put(AppConstants.STATUS, status);
			inputParams.put(AppConstants.DATE, new Date());
			
			if (request.getParameter(AppConstants.RULE_NAME) != null) {
				inputParams.put(AppConstants.RULE_NAME, request.getParameter(AppConstants.RULE_NAME));
			}
			
			out = manageDataService.changeBusinessRuleStatus(inputParams);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in deleteBR", e);
		}
		PrintWriter print = response.getWriter();
		print.print(new Gson().toJson(out));
		print.flush();
		
	}
	
	@RequestMapping(value = "/manageBR/{colType}/getColumnList", method = RequestMethod.GET)
	public void getColumnList(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "colType") String colType)
			throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, AppConstants.MANAGE_BUSINESS_RULES);
			inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			inputParams.put(AppConstants.COLTYPE, colType);
			inputParams.put(AppConstants.DATE, new Date());
			
			out = manageDataService.getBRColumnList(inputParams);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in getColumnList", e);
		}
		PrintWriter print = response.getWriter();
		print.print(new Gson().toJson(out));
		print.flush();
		
	}
	
	@RequestMapping(value = "/manageBR/add", method = RequestMethod.POST)
	public void addBusinessRule(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, AppConstants.MANAGE_BUSINESS_RULES);
//			inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			inputParams.put(AppConstants.USERNAME, "DevUser");
			inputParams.put(AppConstants.DATE, new Date());
			
			if (request.getParameter(AppConstants.EDITDATA) == null) 
				throw new Exception("Provided input was wrong!");
			
			String editData = request.getParameter(AppConstants.EDITDATA).toString();
			
//			String editData = "{\"Constraint\":[{\"Column\":\"FixedTrdSpnd\",\"Condition\":\">\",\"Value\":\"200\"},{\"Column\":\"FixedTrdSpnd\",\"Condition\":\"<\",\"Value\":\"300\"},{\"Column\":\"TotTrdSpnd\",\"Condition\":\"<=\",\"Value\":\"1.5\"}],\"Action\":[{\"Column\":\"ExceptionFlag\",\"Value\":\"Exception\"}],\"RuleName\":\"TotalEventCost\",\"Description\":\"TotalEventCost\"}";
			JsonObject joEditData = (JsonObject) new JsonParser().parse(editData);
			inputParams.put(AppConstants.EDITDATA, joEditData);
			
			out = manageDataService.addUpdateBusinessRule(inputParams);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in deleteBR", e);
		}
		PrintWriter print = response.getWriter();
		print.print(new Gson().toJson(out));
		print.flush();
	}
}
