package com.saama.workbench.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.saama.workbench.bean.CategoryBrandBean;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.ProdDIMBean;
import com.saama.workbench.model.BusinessRuleMeta;
import com.saama.workbench.model.CustomerStats;
import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.model.SblCustomerHierarchy;
import com.saama.workbench.model.SblProductHierarchy;
import com.saama.workbench.service.IViewDataService;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;



@Controller
public class ViewDataController {

	private static final Logger logger = Logger
			.getLogger(ViewDataController.class);

	@Autowired
	IViewDataService viewDataService;

	
	@RequestMapping(value = "/viewProductHierarchy", method = RequestMethod.GET)
	public void viewProductHierarchy(ModelMap model, HttpSession session,
			HttpServletResponse response) {
		new ModelAndView("viewHierarchies");
		new ArrayList<SblProductHierarchy>();
		try {
			String entryString = PEAUtils.getProdHierarchy();
			if (entryString == null) {
				System.out.println("out synchronized" + session.getId());
				synchronized (this) {
					System.out.println("in synchronized" + session.getId());
					entryString = PEAUtils.getProdHierarchy();
					if (entryString == null) {
						System.out.println("if synchronized" + session.getId());
						entryString = viewDataService.getHierarchyJsonString(AppConstants.PRODUCTHIERACHY.toUpperCase());
						PEAUtils.setProdHierarchy(entryString);
					}
				}
			}
			PrintWriter printWriter = response.getWriter();
			printWriter.print("[" + entryString + "]");
			printWriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in viewHierarchies", e);
		}

	}
	

	@RequestMapping(value = "/viewCustomerHierarchy", method = RequestMethod.GET)
	public void viewCustomerHierarchies(ModelMap model, HttpSession session,
			HttpServletResponse response) {
		new ModelAndView("viewHierarchies");
		new ArrayList<SblCustomerHierarchy>();
		try {

			String customerEntry = PEAUtils.getCustHierarchy();
			
			if (customerEntry == null) {
				System.out.println("out synchronized cust" + session.getId());
				synchronized (this) {
					System.out.println("in synchronized cust" + session.getId());
					customerEntry = PEAUtils.getCustHierarchy();
					if (customerEntry == null) {
						System.out.println("if synchronized cust" + session.getId());
						customerEntry = viewDataService.getHierarchyJsonString(AppConstants.CUSTOMERHIERACHY.toUpperCase());
						PEAUtils.setCustHierarchy(customerEntry);
					}
				}
			}

			PrintWriter printWriter = response.getWriter();
			printWriter.print("[" + customerEntry + "]");
			printWriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in viewHierarchies", e);
		}

	}

	@RequestMapping(value = "/{page}/ng/columns", method = RequestMethod.GET)
	public void viewExceptions(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("page") String page) throws Exception {

		new ModelAndView("viewExceptions");

		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		Map<String, Object> colMap = new HashMap<String, Object>();

		// if (AppConstants.VIEWEXCEPTIONS.equalsIgnoreCase(page)) {
		input.put(AppConstants.TABMENU, page);
		input.put(AppConstants.USERNAME,
				session.getAttribute(AppConstants.USERNAME));
		// }
		colMap = viewDataService.getDisplayColumns(input);

		outParams.put(AppConstants.TABLENAME, input.get(AppConstants.TABMENU));
		outParams.put(AppConstants.COLUMNS, colMap.get(AppConstants.COLUMNS));
		outParams.put(AppConstants.COL_WIDTH_MAP, colMap.get(AppConstants.COL_WIDTH_MAP));
		outParams.put(AppConstants.COL_TYPE_MAP, colMap.get(AppConstants.COL_TYPE_MAP));

		PrintWriter out = response.getWriter();
		out.print(new Gson().toJson(outParams));
		out.flush();
	}

	@RequestMapping(value = "/{page}/ng/data", method = RequestMethod.POST)
	public void viewPageData(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response,
			@PathVariable("page") String page) throws Exception {

		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();

		try {

			String tabmenu = page;
			input.put(AppConstants.TABMENU, tabmenu);
			input.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			input.put(AppConstants.ISNGREQUEST, AppConstants.TRUE);
			input.put(AppConstants.DISPLAY_START, request.getParameter(AppConstants.START));
			input.put(AppConstants.DISPLAY_LENGTH, request.getParameter(AppConstants.LENGTH));
			input.put(AppConstants.DRAW, Integer.parseInt(request.getParameter(AppConstants.DRAW)));
			input.put(AppConstants.SORT_COL_0, request.getParameter("order[0][column]"));
			input.put(AppConstants.SORT_DIR_0, request.getParameter("order[0][dir]"));
			input.put(AppConstants.SORT_MANUAL, PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.SORT_MANUAL));
//			input.put(AppConstants.promoOverId,request.getParameter(AppConstants.promoOverId));
//			input.put(AppConstants.promoDrivId,request.getParameter(AppConstants.promoDrivId));
			
			input.put(AppConstants.FLTR_SOURCE, request.getParameter("fltrSource"));

			if (request.getParameter(AppConstants.NG_SEARCH_PARAM) != null) {
				input.put(AppConstants.SEARCH_PARAM, request.getParameter(AppConstants.NG_SEARCH_PARAM));
			}

			Map<String, Map<String, String>> hmWhereClause = new HashMap<String, Map<String, String>>();
			hmWhereClause = getSQLWhereClause(request, tabmenu);
			input.put(AppConstants.WHERE_CLAUSE, hmWhereClause);
			
			Map<String, String> hmSqlReplaceReqParam = new HashMap<String, String>();
			hmSqlReplaceReqParam = getSqlReplaceReqParam(request, tabmenu);
			input.put(AppConstants.SQL_REPLACE_REQ_PARAM, hmSqlReplaceReqParam);
			

			/*
			 * Map<String, Object> colMap =
			 * viewDataService.getDisplayColumns(input); List<String> lColumns =
			 * (List<String>) colMap.get(AppConstants.COLUMNS);
			 * 
			 * String sSearchableColIdx = new String(); for(int i = 0; i <
			 * lColumns.size(); i++) { if
			 * (request.getParameter(AppConstants.BSEARCHABLE_ + i) != null &&
			 * AppConstants
			 * .TRUE.equalsIgnoreCase(request.getParameter(AppConstants
			 * .BSEARCHABLE_ + i))) { sSearchableColIdx += i +
			 * AppConstants.COLON; } } input.put(AppConstants.BSEARCHABLE_,
			 * sSearchableColIdx);
			 */

			dataMap = viewDataService.getData(input);

			logger.info("(" + tabmenu + "/data) Input Params - "
					+ input.toString());

			JsonObject respObj = new JsonObject();
			out.put("iTotalRecords", dataMap.getOrDefault("totalCount", "0"));
			out.put("iTotalDisplayRecords", dataMap.getOrDefault("totalCount", "0"));
			out.put(AppConstants.DATA, dataMap.get(AppConstants.DATA));

//			respObj.addProperty(AppConstants.DRAW, input.get(AppConstants.DRAW).toString());
//			respObj.addProperty("recordsTotal", dataMap.getOrDefault("totalCount", "0").toString());
//			respObj.addProperty("recordsFiltered", dataMap.getOrDefault("totalCount", "0").toString());
//			if (dataMap.get(AppConstants.DATA) != null) {
//				respObj.add(AppConstants.DATA, (JsonArray) dataMap.get(AppConstants.DATA));
//			}

			PrintWriter printWriter = response.getWriter();
			printWriter.print(new Gson().toJson(out));
			printWriter.flush();
			


		} catch (Exception e) {
			logger.error("Exception in viewPageData", e);
		}

		// return mav;
	}
	
	private Map<String, String> getSqlReplaceReqParam (HttpServletRequest request, String tabmenu) throws Exception {
		Map<String, String> hmSqlReplaceReqParam = new HashMap<String, String>();
		if (PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.SQL_REPLACE_REQ_PARAM) != null) {
			for (String s : PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.SQL_REPLACE_REQ_PARAM).split("\\|")) {
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
		return hmSqlReplaceReqParam;
	}
	
	private Map<String, Map<String, String>> getSQLWhereClause (HttpServletRequest request, String tabmenu) throws Exception {
		Map<String, Map<String, String>> hmWhereClause = new HashMap<String, Map<String, String>>();
		if (PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.WHERE_CLAUSE) != null) {
			for (String s : PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.WHERE_CLAUSE).split("\\|")) {
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
		return hmWhereClause;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{page}/export", method = RequestMethod.GET)
	public void viewExport(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response, @PathVariable("page") String page) throws Exception {
		
		Map<String, Object> input = new HashMap<String, Object>();
		new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> columnMap = new HashMap<String, Object>();
		String format = "";
		if(request.getParameter("format")!=null && request.getParameter("format")!=("")){
         format = request.getParameter("format"); 
        }
		try {
			String tabmenu = page;
			input.put(AppConstants.TABMENU, tabmenu);
			input.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
			input.put(AppConstants.EXCLUDEACTIONS, AppConstants.TRUE);
//			input.put(AppConstants.ISNGREQUEST, AppConstants.TRUE);
//			input.put(AppConstants.DISPLAY_START, request.getParameter(AppConstants.START));
//			input.put(AppConstants.DISPLAY_LENGTH, request.getParameter(AppConstants.LENGTH));
//			input.put(AppConstants.DRAW, Integer.parseInt(request.getParameter(AppConstants.DRAW)));
//			input.put(AppConstants.SORT_COL_0, request.getParameter("order[0][column]"));
//			input.put(AppConstants.SORT_DIR_0, request.getParameter("order[0][dir]"));
			
			Map<String, Map<String, String>> hmWhereClause = new HashMap<String, Map<String, String>>();
//			if (PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.WHERE_CLAUSE) != null) {
//				for (String s : PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.WHERE_CLAUSE).split("\\|")) {
//					Map<String, String> p = new HashMap<String, String>();
//					if (s != null && s.split(AppConstants.COLON).length > 1) {
//						if (request.getParameter(s.split(AppConstants.COLON)[1]) != null && request.getParameter(s.split(AppConstants.COLON)[1]).length() > 0) {
//							p.put(AppConstants.COL, s.split(AppConstants.COLON)[0]);
//							p.put(AppConstants.VALUE, request.getParameter(s.split(AppConstants.COLON)[1]));
//							p.put(AppConstants.VALUE_TYPE, AppConstants.SS);
//							p.put(AppConstants.OPERATOR, "=");
//						}
//						else {
//							continue;
//						}
//					}
//					if (s != null && s.split(AppConstants.COLON).length > 2) {
//						p.put(AppConstants.VALUE_TYPE, s.split(AppConstants.COLON)[2]);
//					}
//					if (s != null && s.split(AppConstants.COLON).length > 3) {
//						p.put(AppConstants.OPERATOR, s.split(AppConstants.COLON)[3]);
//					}
//					hmWhereClause.put(s.split(AppConstants.COLON)[0], p);
//				}
//			}
			
			hmWhereClause = getSQLWhereClause(request, tabmenu);
			input.put(AppConstants.WHERE_CLAUSE, hmWhereClause);
			
			
			
			Map<String, String> hmSqlReplaceReqParam = new HashMap<String, String>();
//			if (PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.SQL_REPLACE_REQ_PARAM) != null) {
//				for (String s : PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.SQL_REPLACE_REQ_PARAM).split("\\|")) {
//					if (s != null && s.split(AppConstants.COLON).length > 1) {
//						String key = s.split(AppConstants.COLON)[0], value = "";
//						if (request.getParameter(s.split(AppConstants.COLON)[1]) != null) {
//							value = request.getParameter(s.split(AppConstants.COLON)[1]);
//						}
//						if (s.split(AppConstants.COLON).length > 2) {
//							if (AppConstants.MS.equalsIgnoreCase(s.split(AppConstants.COLON)[2])) {
//								if (!PEAUtils.isEmpty(value) && value.split("\\|").length > 0) {
//									value = StringEscapeUtils.escapeSql(value);
//									value = "'" + StringUtils.join(value.split("\\|"), "', '") + "'";
//								}
//								else if (s.split(AppConstants.COLON).length > 3 && !PEAUtils.isEmpty(s.split(AppConstants.COLON)[3])) {
//									value = s.split(AppConstants.COLON)[3].trim();
//								}
//								else {
//									value = "''";
//								}
//							}
//							else if (AppConstants.CSVLIKEOR.equalsIgnoreCase(s.split(AppConstants.COLON)[2])) {
//								if (!PEAUtils.isEmpty(value)) {
//									value = StringEscapeUtils.escapeSql(value);
//									String condition = new String();
//									for (String s1 : value.split("\\|")) {
//										if (!PEAUtils.isEmpty(s1)) {
//											s1 = s1.trim();
//											if (PEAUtils.isEmpty(condition)) {
//												condition = "( ',' + replace(BusinessRuleID, ' ', '') + ',' like '%," + s1 + ",%'";
//											}
//											else {
//												condition += " or ',' + replace(BusinessRuleID, ' ', '') + ',' like '%," + s1 + ",%'";
//											}
//										}
//									}
//									if (PEAUtils.isEmpty(condition)) {
//										condition = "(1=1)";
//									}
//									else {
//										condition += " ) "; 
//									}
//									value = condition;
//								}
//								else {
//									value = "(1=1)";
//								}
//							}
//							else if (AppConstants.BETWEEN.equalsIgnoreCase(s.split(AppConstants.COLON)[2])) {
//								if (!PEAUtils.isEmpty(value)) {
//									value = StringEscapeUtils.escapeSql(value);
//									value = AppConstants.BETWEEN + " '" + value.replaceAll(" and ",  "' and '") + "'"; 
//								}
//								else {
//									value = " = '' ";
//								}
//							}
//							else {
//								if (!PEAUtils.isEmpty(value)) {
//									value = StringEscapeUtils.escapeSql(value);
//								}
//							}
//						}
//						hmSqlReplaceReqParam.put(key, value);
//					}
//				}
//			}
			hmSqlReplaceReqParam = getSqlReplaceReqParam(request, tabmenu);
			input.put(AppConstants.SQL_REPLACE_REQ_PARAM, hmSqlReplaceReqParam);
			
			if (tabmenu.equalsIgnoreCase(AppConstants.DATAAVAILABILITY)) {
				if (request.getParameter("prevMonth") != null) {
					
					input.put("prevMonth", AppConstants.SHORT_MONTHS[Integer.parseInt(request.getParameter("prevMonth"))-1] + " " + request.getParameter("prevYear"));
					
				}
				if (request.getParameter("curMonth") != null) {
					
					input.put("curMonth", AppConstants.SHORT_MONTHS[Integer.parseInt(request.getParameter("curMonth"))-1] + " " + request.getParameter("curYear"));
					
				}
			}
			
			
			File file = null;
//			if (!tabmenu.equalsIgnoreCase(AppConstants.CUSTOMER_HIERARCHY) && !tabmenu.equalsIgnoreCase(AppConstants.PRODUCT_HIERARCHY)) {
//				dataMap = viewDataService.getData(input);
//				columnMap = viewDataService.getDisplayColumns(input);
//
//				Map<String, String> dbDispColMap = (Map<String, String>) columnMap.get(AppConstants.COLUMNS);
//
//				input.put(AppConstants.COLUMNS, new ArrayList<String>(dbDispColMap.values()));
//				input.put(AppConstants.COLUMN_IDX_MAP, columnMap.get(AppConstants.COLUMN_IDX_MAP));
//				input.put(AppConstants.DATA, dataMap.get(AppConstants.DATA_LIST_ARRAY));
//
//				file = PEAUtils.exportToFile(input);
//
//				response.setContentType("application/xls");
//				response.setContentLength(new Long(file.length()).intValue());
//				response.setHeader("Content-Disposition", "attachment; filename=" + page + ".xls");
//				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
//			}

			if (tabmenu.equalsIgnoreCase(AppConstants.CUSTOMER_HIERARCHY)) {
				List<SblCustomerHierarchy> arrCustomer = viewDataService.getCustomerHierarchies();
				file = PEAUtils.ExportCustomerHierarchy(arrCustomer, format);
			} 
			else if (tabmenu.equalsIgnoreCase(AppConstants.PRODUCT_HIERARCHY)) {
				List<SblProductHierarchy> arrProduct = viewDataService.getProductHierarchies();
				file = PEAUtils.ExportProductHierarchy(arrProduct, format);
			}
			else if (tabmenu.equalsIgnoreCase(AppConstants.OPSO_REPORT_PROMO)) {
				Map<String, Object> promotionsDataMap = new HashMap<String, Object>();
				Map<String, Object> promotionsColumnMap = new HashMap<String, Object>();
				Map<String, String> promotionsDbDispColMap  = new HashMap<String, String>();
				
				Map<String, Object> productDataMap = new HashMap<String, Object>();
				Map<String, Object> productColumnMap = new HashMap<String, Object>();
				Map<String, String> productDbDispColMap  = new HashMap<String, String>();
				
				System.out.println("Getting data for " + input.get(AppConstants.TABMENU));
				
				promotionsDataMap = viewDataService.getData(input);
				promotionsColumnMap = viewDataService.getDisplayColumns(input);
				promotionsDbDispColMap = (Map<String, String>) promotionsColumnMap.get(AppConstants.COLUMNS);
				
				input.put(AppConstants.TABMENU, AppConstants.OPSO_REPORT_PRODUCT);
				
				System.out.println("Getting data for " + input.get(AppConstants.TABMENU));
				
				productDataMap = viewDataService.getData(input);
				productColumnMap = viewDataService.getDisplayColumns(input);
				productDbDispColMap = (Map<String, String>) productColumnMap.get(AppConstants.COLUMNS);

				input.put(AppConstants.COLUMNS + AppConstants.OPSO_REPORT_PROMO, new ArrayList<String>(promotionsDbDispColMap.values()));
				input.put(AppConstants.COLUMN_IDX_MAP + AppConstants.OPSO_REPORT_PROMO, promotionsColumnMap.get(AppConstants.COLUMN_IDX_MAP));
				input.put(AppConstants.DATA + AppConstants.OPSO_REPORT_PROMO, promotionsDataMap.get(AppConstants.DATA_LIST_ARRAY));
				
				input.put(AppConstants.COLUMNS + AppConstants.OPSO_REPORT_PRODUCT, new ArrayList<String>(productDbDispColMap.values()));
				input.put(AppConstants.COLUMN_IDX_MAP + AppConstants.OPSO_REPORT_PRODUCT, productColumnMap.get(AppConstants.COLUMN_IDX_MAP));
				input.put(AppConstants.DATA + AppConstants.OPSO_REPORT_PRODUCT, productDataMap.get(AppConstants.DATA_LIST_ARRAY));
				
				System.out.println("Export To File OPSO");
				
				file = PEAUtils.exportToFileOPSO(input);
				
				format = AppConstants.FORMAT_XLSX;
				
			}
			else {
				
				dataMap = viewDataService.getData(input);
				columnMap = viewDataService.getDisplayColumns(input);

				Map<String, String> dbDispColMap = (Map<String, String>) columnMap.get(AppConstants.COLUMNS);
				
				input.put(AppConstants.COLUMNS, new ArrayList<String>(dbDispColMap.values()));
				input.put(AppConstants.COLUMN_IDX_MAP, columnMap.get(AppConstants.COLUMN_IDX_MAP));
				input.put(AppConstants.DISPLAY_COL_LIST, columnMap.get(AppConstants.DISPLAY_COL_LIST));
				input.put(AppConstants.COLUMN_CURRENCY, columnMap.get(AppConstants.COLUMN_CURRENCY));
				input.put(AppConstants.COLUMN_PERCENTAGE, columnMap.get(AppConstants.COLUMN_PERCENTAGE));
				input.put(AppConstants.DATA, dataMap.get(AppConstants.DERIVED_DATA_LIST_ARRAY));
				
				if (input.get(AppConstants.TABMENU) != null && AppConstants.MAPPING_DATA.equalsIgnoreCase(input.get(AppConstants.TABMENU).toString())) {
					Map<String, String> source = viewDataService.getExtSourceList(input);
					
					List<String> columns = (List<String>) input.get(AppConstants.COLUMNS);
					Map<String, String> idxMap = (Map<String, String>) input.get(AppConstants.COLUMN_IDX_MAP);
					
//					String colName1 = source.get(request.getParameter("fltrSourceId")) + " Brand";
//					columns.set(0, colName1);
					
					input.put("ExternalProduct", source.get(request.getParameter("fltrSourceId")) + " Product");
					
					if (Integer.parseInt(request.getParameter("fltrSourceId").toString()) == 1) {
						columns.remove("Product Name");
						input.put("mergedCol1", "0,0,0,2");
						input.put("mergedCol2", "0,0,3,6");
					}
					else {
						columns.remove("Brand");
						columns.remove("Type");
						columns.remove("SubType");
						input.put("mergedCol1", "");
						input.put("mergedCol2", "0,0,1,4");
						idxMap.put("Brand", "7");
					}
					input.put(AppConstants.COLUMNS, columns);
				}
				
				if (input.get(AppConstants.TABMENU) != null && AppConstants.CUST_MAPPING_DATA.equalsIgnoreCase(input.get(AppConstants.TABMENU).toString())) {
					Map<String, String> source = viewDataService.getExtSourceList(input);
					
					List<String> columns = (List<String>) input.get(AppConstants.COLUMNS);
					String colName1 = source.get(request.getParameter("fltrSourceId")) + " Customer";
					columns.set(0, colName1);
					input.put(AppConstants.COLUMNS, columns);
					
					Map<String, String> idxMap = (Map<String, String>) input.get(AppConstants.COLUMN_IDX_MAP);
					idxMap.put(colName1, idxMap.remove("<label id=\"custSourceLbl\"></label> Customer"));
				}

				file = PEAUtils.exportToFile(input);
				
				format = AppConstants.FORMAT_XLSX;
			}
			
			
			if (format.equalsIgnoreCase(AppConstants.FORMAT_CSV)) {
				response.setContentType("text/csv;charset=utf-8");
				response.setContentLength(new Long(file.length()).intValue());
				response.setHeader("Content-Disposition", "attachment; filename=" + page + ".csv");
				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
			} 
			else if (format.equalsIgnoreCase(AppConstants.FORMAT_XLSX)) {
				response.setContentType("application/xls");
				response.setContentLength(new Long(file.length()).intValue());
				response.setHeader("Content-Disposition", "attachment; filename=" + page + ".xlsx");
				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
			}
			
			if (file != null) {
				logger.info("Deleting file - " + file.getAbsolutePath());
				file.delete();
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in viewExport", e);
		}
		
	}

	@RequestMapping(value = "/viewPromotions", method = RequestMethod.GET)
	public ModelAndView viewPromotions(ModelMap model, HttpSession session)
			throws Exception {
		ModelAndView mav = new ModelAndView("viewPromotions");
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		Map<String, Object> colMap = new HashMap<String, Object>();

		try {

			input.put(AppConstants.TABMENU, AppConstants.PROMOTION);
			colMap = viewDataService.getDisplayColumns(input);

			out.put(AppConstants.COLUMNS, colMap.get(AppConstants.COLUMNS));

			model.addAttribute(AppConstants.RESPONSE, new Gson().toJson(out));
		} catch (Exception e) {
			logger.error("Exception in viewPromotions", e);
		}

		return mav;
	}

	@RequestMapping(value = "/viewWeeklyPromotions", method = RequestMethod.GET)
	public ModelAndView viewWeeklyPromotions(ModelMap model, HttpSession session)
			throws Exception {
		ModelAndView mav = new ModelAndView("viewWeeklyPromotions");

		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		Map<String, Object> colMap = new HashMap<String, Object>();

		try {

			input.put(AppConstants.TABMENU, AppConstants.WEEKLYPROMOTION);
			colMap = viewDataService.getDisplayColumns(input);

			out.put(AppConstants.COLUMNS, colMap.get(AppConstants.COLUMNS));

			model.addAttribute(AppConstants.RESPONSE, new Gson().toJson(out));

		} catch (Exception e) {
			logger.error("Exception in viewWeeklyPromotions", e);
		}

		return mav;
	}

	@RequestMapping(value = "/viewDataAvailability", method = RequestMethod.GET)
	public void viewDataAvailability(ModelMap model, HttpSession session,
			HttpServletResponse response) throws Exception {
		new ModelAndView("viewDataAvailability");
		List<CustomerStats> listCustomerStats = null;
		Map<String, Object> input = new HashMap<String, Object>();
		try {
			listCustomerStats = new ArrayList<CustomerStats>();
			listCustomerStats = viewDataService.viewDataAvailability(input);

			PrintWriter printWriter = response.getWriter();
			printWriter.print(new Gson().toJson(listCustomerStats));
			printWriter.flush();

		} catch (Exception e) {
			logger.error("Exception in viewDataAvailability", e);
		}

	}
	
	@RequestMapping(value = "/viewGrouppedDataAvailability", method = RequestMethod.GET)
	public void viewGrouppedDataAvailability(ModelMap model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		new ModelAndView("viewDataAvailability");
		List<CustomerStats> listCustomerStats = null;
		Map<String, Object> input = new HashMap<String, Object>();
		
		if (request.getParameter(AppConstants.SEL_ACCOUNTS) != null) {
			String acc = request.getParameter(AppConstants.SEL_ACCOUNTS);
			input.put(AppConstants.SEL_ACCOUNTS, acc.split("\\|"));
		}

		try {
			listCustomerStats = new ArrayList<CustomerStats>();
			listCustomerStats = viewDataService.viewDataAvailability(input);

			PrintWriter printWriter = response.getWriter();
			printWriter.print(new Gson().toJson(listCustomerStats));
			printWriter.flush();

		} catch (Exception e) {
			logger.error("Exception in viewDataAvailability", e);
		}

	}

	@RequestMapping(value = "/getBizRuleList", method = RequestMethod.GET)
	public void getBizRuleList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, String> hmBizRules = new HashMap<String, String>();
		try {
			hmBizRules = viewDataService.getBusinessRuleList(input);
		} catch (Exception e) {
			logger.error("Exception in getCustomerList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(hmBizRules));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getBizRuleBeanMap", method = RequestMethod.GET)
	public void getBizRuleBeanMap(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		
		Map<String, Object> input = new HashMap<String, Object>();
		List<BusinessRuleMeta> alBizRules = new ArrayList<BusinessRuleMeta>();
		try {
			alBizRules = viewDataService.getBusinessRuleBeanList(input);
		} 
		catch (Exception e) {
			logger.error("Exception in getCustomerList", e);
		} 
		finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(alBizRules));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/OpsoCustomerGroups", method = RequestMethod.GET)
	public void OpsoCustomerGroups(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		List<String> opsoCustomerGroups = new ArrayList<String> ();
		try{
			opsoCustomerGroups = viewDataService.getCustomerGroups();
		}catch(Exception e){
			logger.error("Exception in OpsoCustomerGroups", e);
		}
		finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(opsoCustomerGroups));
			out.flush();
		}
		
	}
	@RequestMapping(value = "/getCustomerList", method = RequestMethod.GET)
	public void getCustomerList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		List<CustDIMBean> alCustomers = new ArrayList<CustDIMBean> ();
		try {
			input.put("Month", request.getParameter("Month"));
			input.put("Year", request.getParameter("Year"));
			input.put("OpsoCustomerGroup", request.getParameter("OpsoCustomerGroup"));
			
			alCustomers = viewDataService.getCustomerList(input);
		} catch (Exception e) {
			logger.error("Exception in getCustomerList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(alCustomers));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getProdCategoryBrandList", method = RequestMethod.GET)
	public void getProdCategoryBrandListList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		List<CategoryBrandBean> alCategoryBrandBean = new ArrayList<CategoryBrandBean> ();
		try {
			alCategoryBrandBean = viewDataService.getProdCategoryBrandList(input);
		} catch (Exception e) {
			logger.error("Exception in getProdCategoryBrandList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(alCategoryBrandBean));
			out.flush();
		}
	}

	@RequestMapping(value = "/getProductList", method = RequestMethod.GET)
	public void getProductList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		List<ProdDIMBean> alProducts = new ArrayList<ProdDIMBean>();
		try {
			alProducts = viewDataService.getProductList(input);
		} catch (Exception e) {
			logger.error("Exception in getProductList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(alProducts));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getDatasetList", method = RequestMethod.GET)
	public void getDatasetList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, String> hmDatasets = new HashMap<String, String>();
		try {
			input.put("datasetType", request.getParameter("datasetType"));
			hmDatasets = viewDataService.getDatasetList(input);
		} catch (Exception e) {
			logger.error("Exception in getDatasetList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(hmDatasets));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getDatasetObjectList", method = RequestMethod.GET)
	public void getDatasetObjectList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, DatasetMeta> hmDatasets = new HashMap<String, DatasetMeta>();
		try {
			input.put("datasetType", request.getParameter("datasetType"));
			hmDatasets = viewDataService.getDatasetObjectList(input);
		} catch (Exception e) {
			logger.error("Exception in getDatasetList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(hmDatasets));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getAccountList", method = RequestMethod.GET)
	public void getAccountList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		List<CustomerStats> listAccounts = null;
		try {
			listAccounts = viewDataService.getAccountList(input);
		} catch (Exception e) {
			logger.error("Exception in getAccountList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(listAccounts));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getPromotionList", method = RequestMethod.GET)
	public void getPromotionList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, String> hmPromotions = new HashMap<String, String>();
		try {
			hmPromotions = viewDataService.getPromotionList(input);
		} catch (Exception e) {
			logger.error("Exception in getPromotionList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(hmPromotions));
			out.flush();
		}
	}
	
	@RequestMapping(value = "/getOverlappingPromotionList", method = RequestMethod.GET)
	public void getOverlappingPromotionList(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, String> input = new HashMap<String, String>();
		
		try {
			String promoId = request.getParameter("promoId");
			input = viewDataService.getOverLappingPromotions(promoId);
		} catch (Exception e) {
			logger.error("Exception in getAccountList", e);
		} finally {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(input));
			out.flush();
		}
	}
	
	
	
	@RequestMapping(value = "/config/getViewColumns", method = RequestMethod.GET)
	public void getViewColumns(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {

		Map<String, Object> input = new HashMap<String, Object>();

		try {
			input.put(AppConstants.USERNAME,
					session.getAttribute(AppConstants.USERNAME));

			Object viewColumns = viewDataService.getViewColumns(input);

			PrintWriter writer = response.getWriter();
			writer.print(new Gson().toJson(viewColumns));
			writer.flush();
		} catch (Exception e) {
			logger.error("Exception in /config/getViewColumns", e);
		}
	}
   
	
	@RequestMapping(value = "/config/saveViewColumns", method = RequestMethod.POST)
	public void saveViewColumns(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {

		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();

		try {
			input.put(AppConstants.USERNAME,
					session.getAttribute(AppConstants.USERNAME));
			input.put(AppConstants.HIDDEN_COLUMNS,
					request.getParameter(AppConstants.HIDDEN_COLUMNS));
			input.put(AppConstants.DISPLAY_COLUMNS,
					request.getParameter(AppConstants.DISPLAY_COLUMNS));
			input.put(AppConstants.VIEW,
					request.getParameter(AppConstants.VIEW));

			viewDataService.saveViewColumns(input);
			out.put(AppConstants.MESSAGE,
					"Configurations are saved successfully");
			out.put(AppConstants.RESULT, AppConstants.SUCCESS);
		} catch (Exception e) {
			out.put(AppConstants.MESSAGE, e.getMessage());
			out.put(AppConstants.RESULT, AppConstants.FAILURE);
			logger.error("Exception in /config/saveViewColumns", e);
		} finally {
			PrintWriter writer = response.getWriter();
			writer.print(new Gson().toJson(out));
			writer.flush();
		}
	}
	
	
	@RequestMapping(value = "/promotionPeriodAlignmentData", method = RequestMethod.GET)
	public void promotionPeriodAlignmentData(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		
		Map<String, String> input = new HashMap<String, String>();
		//List<Object> output = new ArrayList<Object>();
		Map<String, Object> output = new HashMap<String, Object>();
		
		try{
			input.put(AppConstants.STARTDATE, request.getParameter(AppConstants.STARTDATE));
			input.put(AppConstants.ENDDATE, request.getParameter(AppConstants.ENDDATE));
			input.put(AppConstants.CUSTOMERNAME, request.getParameter(AppConstants.CUSTOMERNAME));
			input.put(AppConstants.PRODUCTNAME, request.getParameter(AppConstants.PRODUCTNAME));

			output = viewDataService.promotionPeriodAlignmentData(input);
		
		    
			PrintWriter printWriter = response.getWriter();
			 printWriter.print(new Gson().toJson(output));
		     printWriter.flush();
		}catch(Exception e){
			logger.error("Exception in promotionPeriodAlignmentData", e);
		}
	}

	@RequestMapping(value = "/promotionPeriodAlignmentcusNprodData", method = RequestMethod.GET)
	public void promotionPeriodAlignmentcusNprodData(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		
		Map<String, Object> hmInput = new HashMap<String, Object>();
		Map<String, Object> output = new HashMap<String, Object>();
		
		try{

			 output = viewDataService.promotionPeriodAlignmentcusNprodData(hmInput);
		
			 PrintWriter printWriter = response.getWriter();
			 printWriter.print(new Gson().toJson(output));
		     printWriter.flush();
		}catch(Exception e){
			logger.error("Exception in promotionPeriodAlignmentData", e);
		}
	}
	
	@RequestMapping(value = "/{page}/saveEditChanges", method = RequestMethod.POST)
	public void saveEditChanges(ModelMap model, 
			HttpServletRequest request, 
			HttpSession session, 
			HttpServletResponse response, 
			@PathVariable("page") String page) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<String, Object>();
		Map<String, Object> hmInput = new HashMap<String, Object>();
		hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
		
		if (request.getParameter(AppConstants.EDITDATA) == null) {
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FAILURE);
			hmOutput.put(AppConstants.MESSAGE, "Edit data is wrong");
		}
		
		if (PEAUtils.convertToBoolean(hmOutput.get(AppConstants.SUCCESS))) {
			try {
				hmInput.put(AppConstants.TABMENU, page);
				hmInput.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
				hmInput.put(AppConstants.EDITDATA, request.getParameter(AppConstants.EDITDATA));
				hmInput.put(AppConstants.PROMO_ID, request.getParameter(AppConstants.PROMO_ID));
				
				hmOutput = viewDataService.saveEditData(hmInput);
				
				// Updating Process Promotion List
				if (!Arrays.asList(new String [] {AppConstants.REPROCESS_PROMOTIONS}).contains(page)) {
					hmInput.put(AppConstants.COLUMNS_TO_UPDATE, Arrays.asList(new String [] {AppConstants.METRIC_CHANGE}));
					hmOutput = viewDataService.updateProcessPromotionList(hmInput);
				}
				
				PrintWriter printWriter = response.getWriter();
				printWriter.print(new Gson().toJson(hmOutput));
			    printWriter.flush();
			} catch(Exception e){
				logger.error("Exception in saveEditChanges", e);
			}
		}
	}
	
	@RequestMapping(value = "/{page}/reprocessPromotions", method = RequestMethod.POST)
	public void reprocessPromotions(ModelMap model, 
			HttpServletRequest request, 
			HttpSession session, 
			HttpServletResponse response, 
			@PathVariable("page") String page) throws Exception {
		
		
		Map<String, Object> hmOutput = new HashMap<String, Object>();
		Map<String, Object> hmInput = new HashMap<String, Object>();
		hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
		
		if (request.getParameter(AppConstants.PROMO_ID) == null) {
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FAILURE);
			hmOutput.put(AppConstants.MESSAGE, "Promo Ids are wrong");
		}
		
		if (PEAUtils.convertToBoolean(hmOutput.get(AppConstants.SUCCESS))) {
			try {
				hmInput.put(AppConstants.TABMENU, page);
				hmInput.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
				hmInput.put(AppConstants.PROMO_ID, request.getParameter(AppConstants.PROMO_ID));
				
				// Updating Process Promotion List
				hmInput.put(AppConstants.COLUMNS_TO_UPDATE, Arrays.asList(new String [] {AppConstants.METRIC_CHANGE}));
				hmOutput = viewDataService.updateProcessPromotionList(hmInput);
				
				PrintWriter printWriter = response.getWriter();
				printWriter.print(new Gson().toJson(hmOutput));
			    printWriter.flush();
			} catch(Exception e){
				logger.error("Exception in reprocessPromotions", e);
			}
		}
		
	}
	
	
	@RequestMapping(value = "/savePromotionData", method = RequestMethod.POST)
	public void savePromotionData(ModelMap model, 
			HttpServletRequest request, 
			HttpSession session, 
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<String, Object>();
		Map<String, Object> hmInput = new HashMap<String, Object>();
		
		hmInput.put(AppConstants.COLUMNS_TO_UPDATE, Arrays.asList(new String [] {AppConstants.ALIGN_CHANGE}));
		
		hmInput.put(AppConstants.STARTDATE, request.getParameter(AppConstants.STARTDATE));	
		hmInput.put(AppConstants.CUSTOMERID, request.getParameter(AppConstants.CUSTOMERID));	
		hmInput.put(AppConstants.CUSTOMERNAME, request.getParameter(AppConstants.CUSTOMERNAME));
		hmInput.put(AppConstants.PRODUCTID, request.getParameter(AppConstants.PRODUCTID));
		hmInput.put(AppConstants.PRODUCTNAME, request.getParameter(AppConstants.PRODUCTNAME));
		
		if (request.getParameter(AppConstants.PROMO_ALIGN_DATA) != null) {
			JsonObject jo = (JsonObject) new JsonParser().parse(request.getParameter(AppConstants.PROMO_ALIGN_DATA).toString());
			hmInput.put(AppConstants.PROMO_ALIGN_DATA, jo);
		}
		
		try {
						
			hmOutput = viewDataService.saveOverrideChanges(hmInput);
			
//			hmOutput = viewDataService.updateProcessPromotionList(hmInput);
			
			PrintWriter printWriter = response.getWriter();
			printWriter.print(new Gson().toJson(hmOutput));
		    printWriter.flush();
		} catch(Exception e){
			logger.error("Exception in savePromotionData", e);
		}
	}
	
	@RequestMapping(value = "/dataQualityStatusMeta", method = RequestMethod.GET)
	public void getDataQualityStatus(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		
		Map<String, Set<String>> output = new HashMap<String, Set<String>>();
		
		try{

			 output = viewDataService.getDataQualityStatusMeta();
		
			 PrintWriter printWriter = response.getWriter();
			 printWriter.print(new Gson().toJson(output));
		     printWriter.flush();
		}catch(Exception e){
			logger.error("Exception in promotionPeriodAlignmentData", e);
		}
	}
	
	@RequestMapping(value = "/saveDataQualityStatus", method = RequestMethod.POST)
	public void saveDataQualityStatus(ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();
		Map<String, Object> hmInput = new HashMap<String, Object>();
		
		hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
		hmOutput.put(AppConstants.MESSAGE, "Invalid input data");
		
		try {
			
			if (request.getParameter(AppConstants.EDITED_STATUS) != null) {
				JsonObject jo = (JsonObject) new JsonParser().parse(request.getParameter(AppConstants.EDITED_STATUS).toString());
				hmInput.put(AppConstants.EDITED_STATUS, jo);
				hmOutput = viewDataService.saveDataQualityStatus(hmInput);
			}
			
			
		} catch(Exception e){
			logger.error("Exception in saveDataQualityStatus", e);
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
		}
		
		PrintWriter printWriter = response.getWriter();
		printWriter.print(new Gson().toJson(hmOutput));
	    printWriter.flush();
	}
}
