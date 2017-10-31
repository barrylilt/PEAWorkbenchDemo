package com.saama.workbench.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.saama.workbench.service.IDataTableService;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Controller
@RequestMapping("/datatable")
public class DataTableController {

	private static final Logger logger = Logger.getLogger(DataTableController.class);
	
	@Autowired
	private IDataTableService dataTableService;
	
	@RequestMapping(value = "/{tableName}/display", method = RequestMethod.GET)
	public ModelAndView tableDisplay(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		String jspName = "datatable"; 
		
		if (request.getParameter("jsp") != null) {
			jspName = request.getParameter("jsp");
		}
		
		ModelAndView mav = new ModelAndView(jspName);
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.ACTIONS_COLUMNS_POSITION, PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.ACTIONS_COLUMNS_POSITION, "LAST"));
		logger.info("(" + tableName + "/display) Input Params - " + inputParams.toString());
		
		List<String> columns = dataTableService.getColumnDisplayNames(inputParams);
		model.addAttribute(AppConstants.TABLENAME, tableName);
		
		if (columns != null) {
			model.addAttribute("columns", columns);
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/{tableName}/columns", method = RequestMethod.GET)
	public void tableColumns(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.ACTIONS_COLUMNS_POSITION, PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.ACTIONS_COLUMNS_POSITION, "LAST"));
		logger.info("(" + tableName + "/display) Input Params - " + inputParams.toString());
		
		try {
			List<String> columns = dataTableService.getColumnDisplayNames(inputParams);
			model.addAttribute(AppConstants.TABLENAME, tableName);
			
			if (columns != null) {
				model.addAttribute("columns", columns);
			}
			
			outParams.put(AppConstants.TABLENAME, tableName);
			outParams.put(AppConstants.COLUMNS, columns);
			
			PrintWriter out = response.getWriter();
			out.print(new  Gson().toJson(outParams));
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - columns", e);
		}
	}
	
	@RequestMapping(value = "/{tableName}/data", method = RequestMethod.GET)
	public void tableData(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.ACTIONS_COLUMNS_POSITION, PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.ACTIONS_COLUMNS_POSITION, "LAST"));
		inputParams.put(AppConstants.DISPLAY_START, request.getParameter(AppConstants.DISPLAY_START));
		inputParams.put(AppConstants.DISPLAY_LENGTH, request.getParameter(AppConstants.DISPLAY_LENGTH));
		inputParams.put(AppConstants.SORT_COL_0, request.getParameter(AppConstants.SORT_COL_0));
		inputParams.put(AppConstants.SORT_DIR_0, request.getParameter(AppConstants.SORT_DIR_0));
		
		if (request.getParameter(AppConstants.SEARCH_PARAM) != null ) {
			inputParams.put(AppConstants.SEARCH_PARAM, request.getParameter(AppConstants.SEARCH_PARAM));
		}
		
		try {
			List<String> lColumns = dataTableService.getColumnDisplayNames(inputParams);
			String sSearchableColIdx = new String();
			for(int i = 0; i < lColumns.size(); i++) {
				if (request.getParameter(AppConstants.BSEARCHABLE_ + i) != null && AppConstants.TRUE.equalsIgnoreCase(request.getParameter(AppConstants.BSEARCHABLE_ + i))) {
					sSearchableColIdx += i + AppConstants.COLON;
				}
			}
			inputParams.put(AppConstants.BSEARCHABLE_, sSearchableColIdx);
			
			if (AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SESSION_CACHE))) {
				if (session.getAttribute(tableName + AppConstants.DOT + AppConstants.DATA) != null) {
					inputParams.put(AppConstants.DATA, session.getAttribute(tableName + AppConstants.DOT + AppConstants.DATA));
				}
			}
			if (inputParams.get(AppConstants.DATA) == null) {
				inputParams.put(AppConstants.DATA, dataTableService.getData(inputParams));
				session.setAttribute(tableName + AppConstants.DOT + AppConstants.DATA, inputParams.get(AppConstants.DATA));
			} 
			
			logger.info("(" + tableName + "/data) Input Params - " + inputParams.toString());
			JsonObject jObj = dataTableService.getProcessedData(inputParams);
			
			JsonObject respObj = new JsonObject();
			respObj.addProperty("iTotalRecords", jObj.get("totalCount").getAsString());
			respObj.addProperty("iTotalDisplayRecords", jObj.get("totalCount").getAsString());
			if (jObj.get("data") != null) {
				respObj.add("aaData", jObj.get("data"));
			}
			PrintWriter out = response.getWriter();
			out.print(respObj);
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - data", e);
		}
	}
	
	@RequestMapping(value = {"/{tableName}/update", "/{tableName}/ng/update"}, method = RequestMethod.POST)
	public void tableUpdate(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
		inputParams.put(AppConstants.KEY, request.getParameter(AppConstants.KEY));
		
		Map<String, Map<String, String>> auditColumns = new HashMap<String, Map<String,String>>();
		String sAuditColProp = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.AUDIT_COLUMN_UPDATE);
		if (sAuditColProp != null) {
			List<String> alAuditColProp = Arrays.asList(sAuditColProp.split("\\|"));
			for (String ac : alAuditColProp) {
				Map<String, String> map = new HashMap<String, String>();
				if (ac.split(AppConstants.COLON).length > 1) {
					map.put(AppConstants.COLTYPE, ac.split(AppConstants.COLON)[1]);
				}
				auditColumns.put(ac.split(AppConstants.COLON)[0], map);
			}
		}
		inputParams.put(AppConstants.AUDIT_COLUMN, auditColumns);
		
		Enumeration eAttrNames = request.getParameterNames();
		try {
			while (eAttrNames.hasMoreElements()) {
				Object oEl = eAttrNames.nextElement();
				
				if (oEl != null && PEAUtils.isNumeric(oEl.toString().replace(AppConstants.COL, ""))) {
					inputParams.put(oEl.toString(), request.getParameter(oEl.toString()));
				}
			}
			Map x = dataTableService.updateTable(inputParams);
			PrintWriter out = response.getWriter();
			out.print(new  Gson().toJson(x));
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - update", e);
		}
	}
	
	@RequestMapping(value = {"/{tableName}/delete", "/{tableName}/ng/delete"}, method = RequestMethod.POST)
	public void tableRowDelete(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.KEY, request.getParameter(AppConstants.KEY));
		
		try {
			Map x = dataTableService.deleteTableRow(inputParams);
			
			PrintWriter out = response.getWriter();
			out.print(new  Gson().toJson(x));
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - delete", e);
		}
	}
	
	@RequestMapping(value = "/{tableName}/export", method = RequestMethod.GET)
	public void tableExport(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.ACTIONS_COLUMNS_POSITION, PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.ACTIONS_COLUMNS_POSITION, "LAST"));
//		inputParams.put(AppConstants.DISPLAY_START, 0);
//		inputParams.put(AppConstants.DISPLAY_LENGTH, 10);
//		inputParams.put(AppConstants.SORT_COL_0, 0);
//		inputParams.put(AppConstants.SORT_DIR_0, AppConstants.ASC);
		inputParams.put(AppConstants.EXCLUDEACTIONS, AppConstants.TRUE);
		
		try {
			inputParams.put("fileName", tableName);
			if (request.getParameter("fileName") != null && request.getParameter("fileName").length() > 0) {
				inputParams.put("fileName", request.getParameter("fileName"));
			}
			
			List<String> lColumns = dataTableService.getColumnDisplayNames(inputParams);
			List<Object[]> reList = dataTableService.getData(inputParams);
			
			/*for (int i=0; i< 15; i++) {
				reList.addAll(reList);
				System.out.println(reList.size());
			}*/
			
			
			inputParams.put(AppConstants.COLUMNS, lColumns);
			inputParams.put(AppConstants.COLUMN_IDX_MAP, dataTableService.getColumnDisplayIdxMap(inputParams));
			inputParams.put(AppConstants.DATA, reList);
			
			File file = PEAUtils.exportToFile(inputParams);
			
			response.setContentType("application/xls");
			response.setContentLength(new Long(file.length()).intValue());
			response.setHeader("Content-Disposition", "attachment; filename="+ inputParams.get("fileName").toString() +".xlsx");
			FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
			
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - export", e);
		}
	}
	
	@RequestMapping(value = "/{tableName}/import", method = RequestMethod.GET)
	public void tableImport(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		try {
			inputParams.put(AppConstants.TABLENAME, tableName);
			inputParams.put(AppConstants.FILENAME, "D:\\test.xlx");
			inputParams.put(AppConstants.EXCELLIST, PEAUtils.convertExcelToList(inputParams));
			
			dataTableService.importIntoTable(inputParams);
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - import", e);
		}
	}
	@RequestMapping(value = "/{tableName}/ng/columns", method = RequestMethod.GET)
	public void ngColumns(ModelMap model, HttpSession session, 
			HttpServletRequest request, 
			HttpServletResponse response, 
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.ACTIONS_COLUMNS_POSITION, PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.ACTIONS_COLUMNS_POSITION, "LAST"));
		logger.info("(" + tableName + "/display) Input Params - " + inputParams.toString());
		
		try {
			
//			if (AppConstants.PromoMechanic.equalsIgnoreCase(tableName)) {
//				inputParams.put(AppConstants.EXCLUDEACTIONS, AppConstants.TRUE);
//			}
			
			Map<String, Object> colAttrs = dataTableService.getColumnDBDisplayNames(inputParams);
			model.addAttribute(AppConstants.TABLENAME, tableName);
			
			if (colAttrs != null) {
				model.addAttribute("columns", colAttrs.get(AppConstants.COLUMNS));
			}
			
			outParams.put(AppConstants.TABLENAME, tableName);
			outParams.put(AppConstants.COLUMNS, colAttrs.get(AppConstants.COLUMNS));
			outParams.put(AppConstants.COL_WIDTH_MAP, colAttrs.get(AppConstants.COL_WIDTH_MAP));
			
			PrintWriter out = response.getWriter();
			out.print(new  Gson().toJson(outParams));
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - columns", e);
		}
	}
	
	@RequestMapping(value = "/ng/blank", method = RequestMethod.GET)
	public void ngBlank(ModelMap model, 
			HttpSession session, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		JsonObject respObj = new JsonObject();
		respObj.addProperty(AppConstants.DRAW, 1);
		respObj.addProperty("recordsTotal", 0);
		respObj.addProperty("recordsFiltered", 0);
		respObj.add("data", new JsonArray());
		
		
		PrintWriter out = response.getWriter();
		out.print(respObj);
		out.flush();
	}
	
	@RequestMapping(value = "/{tableName}/ng/data", method = RequestMethod.POST)
	public void ngData(ModelMap model, 
			HttpSession session, 
			HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(AppConstants.ISNGREQUEST, AppConstants.TRUE);
		inputParams.put(AppConstants.TABLENAME, tableName);
		inputParams.put(AppConstants.ACTIONS_COLUMNS_POSITION, PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.ACTIONS_COLUMNS_POSITION, "LAST"));
		inputParams.put(AppConstants.DISPLAY_START, request.getParameter(AppConstants.START));
		inputParams.put(AppConstants.DISPLAY_LENGTH, request.getParameter(AppConstants.LENGTH));
		inputParams.put(AppConstants.DRAW, Integer.parseInt(request.getParameter(AppConstants.DRAW)));
		inputParams.put(AppConstants.SORT_COL_0, request.getParameter("order[0][column]"));
		inputParams.put(AppConstants.SORT_DIR_0, request.getParameter("order[0][dir]"));
		
		
		Map<String, Map<String, String>> hmWhereClause = new HashMap<String, Map<String, String>>();
		if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.WHERE_CLAUSE) != null) {
			for (String s : PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.WHERE_CLAUSE).split("\\|")) {
				Map<String, String> p = new HashMap<String, String>();
				if (s != null && s.split(AppConstants.COLON).length > 1) {
					if (request.getParameter(s.split(AppConstants.COLON)[1]) != null && request.getParameter(s.split(AppConstants.COLON)[1]).length() > 0) {
						p.put(AppConstants.COL, s.split(AppConstants.COLON)[0]);
						p.put(AppConstants.VALUE, request.getParameter(s.split(AppConstants.COLON)[1]));
						p.put(AppConstants.VALUE_TYPE, AppConstants.SS);
						p.put(AppConstants.OPERATOR, "=");
					}
					else {
						continue;
					}
				}
				if (s != null && s.split(AppConstants.COLON).length > 2) {
					p.put(AppConstants.VALUE_TYPE, s.split(AppConstants.COLON)[2]);
				}
				if (s != null && s.split(AppConstants.COLON).length > 3) {
					p.put(AppConstants.OPERATOR, s.split(AppConstants.COLON)[3]);
				}
				hmWhereClause.put(s.split(AppConstants.COLON)[0], p);
			}
		}
		inputParams.put(AppConstants.WHERE_CLAUSE, hmWhereClause);
		
		Map<String, String> hmSqlReplaceReqParam = new HashMap<String, String>();
		if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SQL_REPLACE_REQ_PARAM) != null) {
			for (String s : PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SQL_REPLACE_REQ_PARAM).split("\\|")) {
				if (s != null && s.split(AppConstants.COLON).length > 1) {
					String key = s.split(AppConstants.COLON)[0], value = "";
					if (request.getParameter(s.split(AppConstants.COLON)[1]) != null) {
						value = request.getParameter(s.split(AppConstants.COLON)[1]);
					}
					if (s.split(AppConstants.COLON).length > 2) {
						if (AppConstants.MS.equalsIgnoreCase(s.split(AppConstants.COLON)[2])) {
							if (!PEAUtils.isEmpty(value) && value.split("\\|").length > 0) {
								value = StringEscapeUtils.escapeSql(value);
								value = "'" + StringUtils.join(value.split("\\|"), "', '") + "'";
							}
							else if (s.split(AppConstants.COLON).length > 3 && !PEAUtils.isEmpty(s.split(AppConstants.COLON)[3])) {
								value = s.split(AppConstants.COLON)[3].trim();
							}
							else {
								value = "''";
							}
						}
						else if (AppConstants.CSVLIKEOR.equalsIgnoreCase(s.split(AppConstants.COLON)[2])) {
							if (!PEAUtils.isEmpty(value)) {
								value = StringEscapeUtils.escapeSql(value);
								String condition = new String();
								for (String s1 : value.split("\\|")) {
									if (!PEAUtils.isEmpty(s1)) {
										s1 = s1.trim();
										if (PEAUtils.isEmpty(condition)) {
											condition = "( ',' + replace(BusinessRuleID, ' ', '') + ',' like '%," + s1 + ",%'";
										}
										else {
											condition += " or ',' + replace(BusinessRuleID, ' ', '') + ',' like '%," + s1 + ",%'";
										}
									}
								}
								if (PEAUtils.isEmpty(condition)) {
									condition = "(1=1)";
								}
								else {
									condition += " ) "; 
								}
								value = condition;
							}
							else {
								value = "(1=1)";
							}
						}
						else if (AppConstants.BETWEEN.equalsIgnoreCase(s.split(AppConstants.COLON)[2])) {
							if (!PEAUtils.isEmpty(value)) {
								value = StringEscapeUtils.escapeSql(value);
								value = AppConstants.BETWEEN + " '" + value.replaceAll(" and ",  "' and '") + "'"; 
							}
							else {
								value = " = '' ";
							}
						}
						else {
							if (!PEAUtils.isEmpty(value)) {
								value = StringEscapeUtils.escapeSql(value);
							}
						}
					}
					hmSqlReplaceReqParam.put(key, value);
				}
			}
		}
		inputParams.put(AppConstants.SQL_REPLACE_REQ_PARAM, hmSqlReplaceReqParam);
		
		if (request.getParameter(AppConstants.SEARCH_PARAM) != null ) {
			inputParams.put(AppConstants.SEARCH_PARAM, request.getParameter(AppConstants.SEARCH_PARAM));
		}
		
		if (request.getParameter("search[value]") != null ) {
			inputParams.put(AppConstants.SEARCH_PARAM, request.getParameter("search[value]")); 
		}
		
		try {
			List<String> lColumns = dataTableService.getColumnDisplayNames(inputParams);
			String sSearchableColIdx = new String();
			for(int i = 0; i < lColumns.size(); i++) {
				if (request.getParameter(AppConstants.BSEARCHABLE_ + i) != null && AppConstants.TRUE.equalsIgnoreCase(request.getParameter(AppConstants.BSEARCHABLE_ + i))) {
					sSearchableColIdx += i + AppConstants.COLON;
				}
			}
			inputParams.put(AppConstants.BSEARCHABLE_, sSearchableColIdx);
			
			if (AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SESSION_CACHE))) {
				if (session.getAttribute(tableName + AppConstants.DOT + AppConstants.DATA) != null) {
					inputParams.put(AppConstants.DATA, session.getAttribute(tableName + AppConstants.DOT + AppConstants.DATA));
				}
			}
			if (inputParams.get(AppConstants.DATA) == null) {
				inputParams.put(AppConstants.DATA, dataTableService.getData(inputParams));
				if (AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SESSION_CACHE))) {
					session.setAttribute(tableName + AppConstants.DOT + AppConstants.DATA, inputParams.get(AppConstants.DATA));
				}
			} 
			
			logger.info("(" + tableName + "/data) Input Params - " + inputParams.toString());
			JsonObject jObj = dataTableService.getProcessedData(inputParams);
			
			JsonObject respObj = new JsonObject();
//			respObj.addProperty("iTotalRecords", jObj.get("totalCount").getAsString());
//			respObj.addProperty("iTotalDisplayRecords", jObj.get("totalCount").getAsString());
//			if (jObj.get("data") != null) {
//				respObj.add("aaData", jObj.get("data"));
//			}
			
			respObj.addProperty(AppConstants.DRAW, inputParams.get(AppConstants.DRAW).toString());
			respObj.addProperty("recordsTotal", jObj.get("totalCount").getAsString());
			respObj.addProperty("recordsFiltered", jObj.get("totalCount").getAsString());
			if (jObj.get("data") != null) {
				respObj.add("data", jObj.get("data"));
			}
			
			PrintWriter out = response.getWriter();
			out.print(respObj);
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in table " + tableName + " - data", e);
		}
	}
	
	@RequestMapping(value = "/savePromoMechanic", method = RequestMethod.POST)
	public void savePromoMechanic(ModelMap model, 
			HttpSession session, 
			HttpServletRequest request, 
			HttpServletResponse response/*,
			@PathVariable(value="tableName") String tableName*/
			) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		
		try {
			String tableName = request.getParameter(AppConstants.TABLENAME);
			Map<String, Map<String, String>> auditColumns = new HashMap<String, Map<String,String>>();
			
			inputParams.put(AppConstants.TABLENAME, tableName);
			inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			inputParams.put(AppConstants.EDITDATA, request.getParameter(AppConstants.EDITDATA));
			
			String sAuditColProp = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.AUDIT_COLUMN_UPDATE);
			if (sAuditColProp != null) {
				List<String> alAuditColProp = Arrays.asList(sAuditColProp.split("\\|"));
				for (String ac : alAuditColProp) {
					Map<String, String> map = new HashMap<String, String>();
					if (ac.split(AppConstants.COLON).length > 1) {
						map.put(AppConstants.COLTYPE, ac.split(AppConstants.COLON)[1]);
					}
					auditColumns.put(ac.split(AppConstants.COLON)[0], map);
				}
			}
			inputParams.put(AppConstants.AUDIT_COLUMN, auditColumns);
			
			out = dataTableService.savePromoMechanic(inputParams);
			
			
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in savePromoMechanic", e);
		}
		
		PrintWriter print = response.getWriter();
		print.print(new Gson().toJson(out));
		print.flush();
		
	}
	
	@RequestMapping(value = "/{tableName}/add", method = RequestMethod.POST)
	public void tableRowAdd(ModelMap model, 
			HttpSession session, 
			HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable(value="tableName") String tableName) throws Exception {
		
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		
		try {
			
			inputParams.put(AppConstants.TABLENAME, tableName);
			inputParams.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			
			Map<String, Map<String, String>> auditColumns = new HashMap<String, Map<String,String>>();
			String sAuditColProp = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.AUDIT_COLUMN_ADD);
			if (sAuditColProp != null) {
				List<String> alAuditColProp = Arrays.asList(sAuditColProp.split("\\|"));
				for (String ac : alAuditColProp) {
					Map<String, String> map = new HashMap<String, String>();
					if (ac.split(AppConstants.COLON).length > 1) {
						map.put(AppConstants.COLTYPE, ac.split(AppConstants.COLON)[1]);
					}
					auditColumns.put(ac.split(AppConstants.COLON)[0], map);
				}
			}
			inputParams.put(AppConstants.AUDIT_COLUMN, auditColumns);
			
			JsonObject editData = new JsonObject();
			if (request.getParameter(AppConstants.EDITDATA) != null) {
				editData = (JsonObject) new JsonParser().parse(request.getParameter(AppConstants.EDITDATA));
			}
			inputParams.put(AppConstants.EDITDATA, editData);
			out = dataTableService.insertIntoTable(inputParams);
			
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in manageLookupTables", e);
		}
		PrintWriter print = response.getWriter();
		print.print(new Gson().toJson(out));
		print.flush();
	}
}
