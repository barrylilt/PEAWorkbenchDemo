package com.saama.workbench.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saama.workbench.bean.CategoryBrandBean;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.DataQualityReportBean;
import com.saama.workbench.bean.NameCodeDistanceBean;
import com.saama.workbench.bean.ProdDIMBean;
import com.saama.workbench.bean.PromoDIMBean;
import com.saama.workbench.dao.ViewDataDao;
import com.saama.workbench.model.BusinessRuleMeta;
import com.saama.workbench.model.CustomerStats;
import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.model.SblCustomerHierarchy;
import com.saama.workbench.model.SblProductHierarchy;
import com.saama.workbench.model.WbUserColumnConfig;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;
import com.saama.workbench.util.TestTree;

@Service
@Transactional
public class ViewDataService implements IViewDataService {

	private static final Logger logger = Logger.getLogger(ViewDataService.class);
	private final String sEditRowKeyDelim = "&%";
	
	@Autowired
	private ViewDataDao viewDataDao;
		
	@Override
	public Map<String, Object> getDisplayColumns(Map<String, Object> input) throws Exception {
		
		if (input.get(AppConstants.TABMENU) == null) {
			logger.error("Tab menu is not defined properly");
			throw new Exception("Exception in getDisplayColumns - Tab menu is not defined properly");
		}
		
		try {
			String sTabMenu = input.get(AppConstants.TABMENU).toString();
			Map<String, String> hmColumns = new LinkedHashMap<String, String>();
			Map<String, String> hmColWidth = new LinkedHashMap<String, String>();
			Map<String, String> hmColType = new LinkedHashMap<String, String>();
			List<String> hmDispColList = new ArrayList<String>();
			
			List<String> alColCurr = new ArrayList<String>();
			List<String> alColPerc = new ArrayList<String>();
			
			
			Map<String, Map<String, String>> hmColumnMapping = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Object> out = new HashMap<String, Object>();
			
			String [] chkHiddenColViews = { AppConstants.VIEWEXCEPTIONS, AppConstants.VIEWPROMOTEDPRODUCTS, AppConstants.VIEWWEEKLYPROMOTION, AppConstants.VIEWDATASETS };
			
			if (!PEAUtils.convertToBoolean(input.getOrDefault(AppConstants.EXCLUDEACTIONS, "N")) && 
					Arrays.asList(new String[]{AppConstants.VIEWEXCEPTIONS}).contains(sTabMenu)) {
				hmColumns.put("actions", "Select");
				//hmColumns.put("reprocess", "Reprocess");
			}
			
			String sColMapping = PropertiesUtil.getProperty(sTabMenu + AppConstants.DOT + AppConstants.COLUMN_MAPPING);
			String sColCurr = PropertiesUtil.getProperty(sTabMenu + AppConstants.DOT + AppConstants.COLUMN_CURRENCY);
			String sColPerc = PropertiesUtil.getProperty(sTabMenu + AppConstants.DOT + AppConstants.COLUMN_PERCENTAGE);
			String sColPropHidden = PropertiesUtil.getProperty(sTabMenu + AppConstants.DOT + AppConstants.COLUMN_HIDDEN);
			String[] saColMapping = sColMapping.split(AppConstants.SYM_PIPE_REGX);
			List<String> lColPropHidden = new ArrayList<String>();
			if (sColPropHidden != null) {
				lColPropHidden = Arrays.asList(sColPropHidden.split(AppConstants.SYM_PIPE_REGX));
			}
			
			List<String> viewsDisplayCols = new ArrayList<String>();
			if (Arrays.asList(chkHiddenColViews).contains(sTabMenu)) {
				WbUserColumnConfig userColumnConfig = viewDataDao.getViewConfig(input.get(AppConstants.USERNAME).toString(), sTabMenu);
				if (userColumnConfig != null && userColumnConfig.getDisplayColumns() != null) {
					viewsDisplayCols = Arrays.asList(userColumnConfig.getDisplayColumns().split(AppConstants.SYM_PIPE_REGX));
				}
				
			}
			
			Map<String, String> colIdxMap = new HashMap<String, String>();
			int colIdx = 0;
			for(String cm : saColMapping) {
				if (cm != null && cm.split(AppConstants.COLON).length > 2) {
					//if (!lColPropHidden.contains(cm.split(AppConstants.COLON)[0].trim().replace(AppConstants.COL, ""))) {
						Map<String, String> p = new HashMap<String, String>();
						p.put(AppConstants.DBCOLUMN, cm.split(AppConstants.COLON)[1]);
						p.put(AppConstants.DISPLAYCOLUMN, cm.split(AppConstants.COLON)[2]);
						if (cm.split(AppConstants.COLON).length > 3 && !PEAUtils.isEmpty(cm.split(AppConstants.COLON)[3]))
							p.put(AppConstants.COLTYPE, cm.split(AppConstants.COLON)[3]);
						if (cm.split(AppConstants.COLON).length > 4 && !PEAUtils.isEmpty(cm.split(AppConstants.COLON)[4]))
							p.put(AppConstants.COL_WIDTH, cm.split(AppConstants.COLON)[4]);
						hmColumnMapping.put(cm.split(AppConstants.COLON)[0], p);
//					}
					if (colIdxMap.get(cm.split(AppConstants.COLON)[2]) == null)	
						colIdxMap.put(cm.split(AppConstants.COLON)[2], ""+colIdx);
					else 
						colIdxMap.put(cm.split(AppConstants.COLON)[2], colIdxMap.get(cm.split(AppConstants.COLON)[2]) + "," + colIdx);
				}
				colIdx++;
			}
			
			if (viewsDisplayCols.size() > 0) {
				for (String c : viewsDisplayCols) {
					if (hmColumnMapping.get(c) != null && !lColPropHidden.contains(c.trim().replace(AppConstants.COL, ""))) {
						hmDispColList.add(c);
						hmColumns.put(hmColumnMapping.get(c).get(AppConstants.DBCOLUMN), hmColumnMapping.get(c).get(AppConstants.DISPLAYCOLUMN));
						hmColWidth.put(hmColumnMapping.get(c).get(AppConstants.DBCOLUMN), hmColumnMapping.get(c).get(AppConstants.COL_WIDTH));
						hmColType.put(hmColumnMapping.get(c).get(AppConstants.DBCOLUMN), hmColumnMapping.get(c).get(AppConstants.COLTYPE));
					}
				}
			}
			else {
				for (Map.Entry<String, Map<String, String>> ent : hmColumnMapping.entrySet()) {
					if (!lColPropHidden.contains(ent.getKey().trim().replace(AppConstants.COL, ""))) {
						hmDispColList.add(ent.getKey());
						hmColumns.put(ent.getValue().get(AppConstants.DBCOLUMN), ent.getValue().get(AppConstants.DISPLAYCOLUMN));
						hmColWidth.put(ent.getValue().get(AppConstants.DBCOLUMN), ent.getValue().get(AppConstants.COL_WIDTH));
						hmColType.put(ent.getValue().get(AppConstants.DBCOLUMN), ent.getValue().get(AppConstants.COLTYPE));
					}
				}
			}
			
			if (sColCurr != null && sColCurr.trim().length() > 0) {
				alColCurr = Arrays.asList(sColCurr.split(AppConstants.SYM_PIPE_REGX));
			}
			
			if (sColPerc != null && sColPerc.trim().length() > 0) {
				alColPerc = Arrays.asList(sColPerc.split(AppConstants.SYM_PIPE_REGX));
			}
			
			if(!PEAUtils.convertToBoolean(input.getOrDefault(AppConstants.EXCLUDEACTIONS, "N")) && Arrays.asList(new String[]{AppConstants.MAPPING_DATA, AppConstants.CUST_MAPPING_DATA, AppConstants.MANAGE_BUSINESS_RULES}).contains(sTabMenu)) {
				hmColumns.put("actions", "Actions");
			}
//			if(!PEAUtils.convertToBoolean(input.getOrDefault(AppConstants.EXCLUDEACTIONS, "N")) && Arrays.asList(new String[]{AppConstants.CUST_MAPPING_DATA}).contains(sTabMenu)){
//				hmColumns.put("actions", "Actions");
//			}
			out.put(AppConstants.TABMENU, sTabMenu);
			out.put(AppConstants.COLUMNS, hmColumns);
			out.put(AppConstants.COL_WIDTH_MAP, hmColWidth);
			out.put(AppConstants.COL_TYPE_MAP, hmColType);
			out.put(AppConstants.DISPLAY_COL_LIST, hmDispColList);
			out.put(AppConstants.COLUMN_IDX_MAP, colIdxMap);
			out.put(AppConstants.COLUMN_CURRENCY, alColCurr);
			out.put(AppConstants.COLUMN_PERCENTAGE, alColPerc);
			out.put(AppConstants.COLUMN_MAPPING, hmColumnMapping);			
			
			return out;
		} catch (Exception e) {
			logger.error("Exception in getDisplayColumns - " + e.getMessage());
			throw e;
		}
	}
	
	@Override
	public Map<String, Object> getDBColumns(Map<String, Object> input) throws Exception {
		
		if (input.get(AppConstants.TABMENU) == null) {
			logger.error("Tab menu is not defined properly");
			throw new Exception("Exception in getDBColumns - Tab menu is not defined properly");
		}
		
		String tabmenu = input.get(AppConstants.TABMENU).toString().toUpperCase();
		List<String> lColumns = new ArrayList<String>();
		Map<String, Object> out = new HashMap<String, Object>();
		
		try {

			String sColMapping = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_MAPPING);
			String[] saColMapping = sColMapping.split(AppConstants.SYM_PIPE_REGX);
			for(String cm : saColMapping) {
				if (cm != null && cm.split(AppConstants.COLON).length > 1) {
					lColumns.add(cm.split(AppConstants.COLON)[1]);
				}
			}
		
			out.put(AppConstants.TABMENU, tabmenu);
			out.put(AppConstants.COLUMNS, lColumns);
			
			return out;
			
		} catch (Exception e) {
			logger.error("Exception in getDBColumns - " + e.getMessage());
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getData(Map<String, Object> input) throws Exception{
		
		Map<String, Object> out = new HashMap<String, Object>();
		if (input.get(AppConstants.TABMENU) == null) {
			logger.error("Tab menu is not defined properly");
			throw new Exception("Exception in getData - Tab menu is not defined properly");
		}
		
		try {
			String tabmenu = input.get(AppConstants.TABMENU).toString();
			Map<String, Double> total = new HashMap<String, Double>();
			Map<String, Object> dispCol = getDisplayColumns(input);
			
			if (dispCol.get(AppConstants.DISPLAY_COL_LIST) != null && input.get(AppConstants.SORT_COL_0) != null) {
				if (((List<String>)dispCol.get(AppConstants.DISPLAY_COL_LIST)).size() > 0) {
					int sortColIdx = Integer.parseInt(input.get(AppConstants.SORT_COL_0).toString());
					if (AppConstants.VIEWEXCEPTIONS.equalsIgnoreCase(tabmenu) || AppConstants.VIEWDATASETS.equalsIgnoreCase(tabmenu)) {
						sortColIdx--;
						if (sortColIdx < 0) {
							sortColIdx = 0;
						}
					}
					if (AppConstants.MAPPING_DATA.equalsIgnoreCase(tabmenu)) {
						if (input.getOrDefault(AppConstants.FLTR_SOURCE, "").toString().equalsIgnoreCase(AppConstants.KANTAR)) {
							sortColIdx +=3;
						}
						else {
							if (sortColIdx >= 3)
								sortColIdx +=1;
						}
					}
					if (sortColIdx > ((List<String>)dispCol.get(AppConstants.DISPLAY_COL_LIST)).size() - 1) {
						sortColIdx = ((List<String>)dispCol.get(AppConstants.DISPLAY_COL_LIST)).size() - 1;
					}
					String sortCol = ((List<String>)dispCol.get(AppConstants.DISPLAY_COL_LIST)).get(sortColIdx);
					input.put(AppConstants.SORT_COL_0, sortCol.replace(AppConstants.COL, ""));
				}
			}
			if (input.get(AppConstants.SORT_COL_0) == null || input.get(AppConstants.SORT_COL_0).toString().trim().length() < 1) {
				input.put(AppConstants.SORT_COL_0, "0");
			}
			if (input.get(AppConstants.SORT_DIR_0) == null || input.get(AppConstants.SORT_DIR_0).toString().trim().length() < 1) {
				input.put(AppConstants.SORT_DIR_0, AppConstants.ASC);
			}
			
			Map<String, Object> dataOut = viewDataDao.getSQLData(input);
			String sTotalCount = new String("0");
			
			if (dataOut != null && dataOut.get(AppConstants.DATA) != null) {
				List<Object[]> listData = (List<Object[]>) dataOut.get(AppConstants.DATA);
				if (listData.size() > 0) {
					sTotalCount = listData.get(0)[listData.get(0).length - 1].toString();
				}
			}
			String[] sakeyColIdx = null;
			if (!PEAUtils.isEmpty(PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX))) {
				sakeyColIdx = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX).split(AppConstants.SYM_PIPE_REGX);
			}
			
			Map<String, Map<String, Object>> hmColEditType = PEAUtils.getEditColType(tabmenu);
			String sColMapping = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_MAPPING);
			String sColCurrencies = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_CURRENCY);
			String sColPerc = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_PERCENTAGE);
			String sColTrim = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_TRIM);
			String sColHighlightRuleColumn = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_HIGLIGHT_RULE);
			String[] saColMapping = sColMapping.split(AppConstants.SYM_PIPE_REGX);
			List<String> lColCurrencies = new ArrayList<String>();
			List<String> lColPerc = new ArrayList<String>();
			Map<String, Map<String, String>> hmColTrim = new HashMap<String, Map<String, String>>();
			Map<String,String> hrm =  new HashMap<String, String>();
			if (sColCurrencies != null) {
				lColCurrencies = Arrays.asList(sColCurrencies.split(AppConstants.SYM_PIPE_REGX)); 
			}
			if (sColPerc != null) {
				lColPerc = Arrays.asList(sColPerc.split(AppConstants.SYM_PIPE_REGX)); 
			}
			
			if (sColTrim != null) {
				for (String s : sColTrim.split(AppConstants.SYM_PIPE_REGX)) {
					Map<String, String> hm = new HashMap<String, String>();
					String[] as = s.split(AppConstants.COLON);
					
					hm.put(AppConstants.COLUMN_TRIM, AppConstants.TRUE);
					
					if (as.length > 1 && as[1] != null) {
						hm.put(AppConstants.TRIM_LENGTH, s.split(AppConstants.COLON)[1]);
					}
					else {
						hm.put(AppConstants.TRIM_LENGTH, "10");
					}
					hmColTrim.put(as[0], hm);
				}
			}
			if(sColHighlightRuleColumn != null){
				for(String s : sColHighlightRuleColumn.split(AppConstants.SYM_PIPE_REGX)){
					String[] as = s.split(AppConstants.COLON);
					hrm.put(as[0], as[1]);
				}
			}
			
			Map<String, Map<String, Object>> lhmColMappings = new LinkedHashMap<String, Map<String, Object>>();
			int idx = 0;
			 
			Map<String, Integer> sqlDBColumnIdx = new HashMap<String, Integer>();
			
			List<String> alSortManual = new ArrayList<String>();
			
			if (input.get(AppConstants.SORT_MANUAL) != null) {
				alSortManual = Arrays.asList(input.get(AppConstants.SORT_MANUAL).toString().split(AppConstants.SYM_PIPE_REGX));
			}
			
			for(String cm : saColMapping) {
				Map<String, Object> hmObj = new HashMap<String, Object>();
				String[] acm = new String[]{};
				if (cm != null ) {
					acm = cm.split(AppConstants.COLON);
				}
				if (acm.length > 2) {
					hmObj.put(AppConstants.DBCOLUMN, acm[1]);
					hmObj.put(AppConstants.DISPLAYCOLUMN, acm[2]);
					hmObj.put(AppConstants.COLTYPE, acm[3]);
					if (acm.length > 4)
						hmObj.put(AppConstants.COL_WIDTH, acm[4]);
					
					if (acm.length > 5)
						hmObj.put(AppConstants.COL_DECIMALS, acm[5]);
					else
						hmObj.put(AppConstants.COL_DECIMALS, "0");
					
					if ( lColCurrencies.contains(acm[0].replace(AppConstants.COL,"")))
						hmObj.put(AppConstants.COL_DATATYPE, AppConstants.CURRENCY);
					
					if ( lColPerc.contains(acm[0].replace(AppConstants.COL,"")))
						hmObj.put(AppConstants.COL_DATATYPE, AppConstants.PERCENTAGE);
					
					if ( hmColTrim.get(acm[0].replace(AppConstants.COL,"")) != null)
						hmObj.putAll(hmColTrim.get(acm[0].replace(AppConstants.COL,"")));
					
					if (hmColEditType.get(acm[0]) != null) {
						hmObj.put(AppConstants.EDITABLE, AppConstants.Y);
						hmObj.putAll(hmColEditType.get(acm[0]));
					}
					if (alSortManual.contains(acm[0].replace(AppConstants.COL,""))) {
						hmObj.put(AppConstants.SORT_MANUAL, true);
					}
					hmObj.put(AppConstants.ACTUAL_DB_COL, hrm.getOrDefault(acm[0], AppConstants.BLANK));
				}
				
				String[] DBColumnNames = new String[] {"promotionId", "promoId", "overlappingPromotions", "promotion", "ExtProdID", "BrandFormID"
						, "IntProdID", "ExtCustID", "ExtCustName", "IntCustId", "ExtBrand", "ExtForm", "ExtSubForm", "ExtProdName", "ExtProdCode", "IntProdName" 
						, "IntProdCode", "Brand", "Form", "SubForm", "Weightage", "Mapping", "Ignored", "Status", "RuleName", "Description", "Constraints"
						, "ActionColumn", "IsEnabled", "suggProdName", "suggCustName"};
				
				for(String dbColName : DBColumnNames) {
					if (dbColName.equalsIgnoreCase(acm[1])) {
						sqlDBColumnIdx.put(dbColName, idx);
					}
				}
				
//				if ("promotionId".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("promotionId", idx);
//				}
//				if ("promoId".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("promoId", idx);
//				}
//				if ("overlappingPromotions".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("overlappingPromotions", idx);
//				}
//				if ("promotion".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("promotion", idx);
//				}
//				if ("ExtProdID".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtProdID", idx);
//				}
//				if ("BrandFormID".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("BrandFormID", idx);
//				}
//				if ("IntProdID".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("IntProdID", idx);
//				}
//				if ("ExtCustID".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtCustID", idx);
//				}
//				if ("ExtCustName".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtCustName", idx);
//				}
//				if ("IntCustId".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("IntCustId", idx);
//				}
//				if ("ExtBrand".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtBrand", idx);
//				}
//				if ("ExtForm".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtForm", idx);
//				}
//				if ("ExtSubForm".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtSubForm", idx);
//				}
//				if ("ExtProdName".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtProdName", idx);
//				}
//				if ("ExtProdCode".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ExtProdCode", idx);
//				}
//				if ("IntProdName".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("IntProdName", idx);
//				}
//				if ("IntProdCode".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("IntProdCode", idx);
//				}
//				if ("Brand".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Brand", idx);
//				}
//				if ("Form".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Form", idx);
//				}
//				if ("SubForm".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("SubForm", idx);
//				}
//				if ("Weightage".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Weightage", idx);
//				}
//				if ("Mapping".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Mapping", idx);
//				}
//				if ("Ignored".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Ignored", idx);
//				}
//				if ("Status".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Status", idx);
//				}
//				if ("RuleName".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("RuleName", idx);
//				}
//				if ("Description".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Description", idx);
//				}
//				if ("Constraints".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("Constraints", idx);
//				}
//				if ("ActionColumn".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("ActionColumn", idx);
//				}
//				if ("IsEnabled".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("IsEnabled", idx);
//				}
//				if ("suggProdName".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("suggProdName", idx);
//				}
//				if ("suggCustName".equalsIgnoreCase(acm[1])) {
//					sqlDBColumnIdx.put("suggCustName", idx);
//				}
				if ("BizRules".equalsIgnoreCase(acm[1])) {
					sqlDBColumnIdx.put("BizRules", idx);
				}
				if ("BizRules".equalsIgnoreCase(acm[1])) {
					sqlDBColumnIdx.put("BizRules", idx);
				}
				
				
				lhmColMappings.put(acm[0], hmObj);
				idx++;
			}
		
			List listData = new ArrayList<JsonObject>();
			SimpleDateFormat SDF = new SimpleDateFormat(AppConstants.YMD); //AppConstants.SDF_YMD;
			SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat(AppConstants.YMDHMS);
			SimpleDateFormat SDF_YMD = new SimpleDateFormat(AppConstants.YMD);
			
			JsonObject jo = new JsonObject();
			Map<String, String> bizRuleMap = getBusinessRuleList(new HashMap<String, Object>());
			Map<String, String> bizRuleDescMap = getBusinessRuleDescMap(new HashMap<String, Object>());
			
			List<Object[]> listObjArr = new ArrayList<Object[]>();
			
			Map<String, Object> hmSggInput = new HashMap<>();
			boolean mappingDataUnmapped = false;
			
			Map<String, Map<String, Object>> hmBRDetails = viewDataDao.getBusinessRuleColumns();
			for(Object[] obj : (List<Object[]>) dataOut.get(AppConstants.DATA)) {
				jo = new JsonObject();
				Object[] derivedObj = obj;
				String actions = new String();
				String mappingDataSuggCustProdName = new String();
				NameCodeDistanceBean oProdCustMapNameCodeDist = null;
				Map<String, Set<String>> businessRuleColumns = new HashMap<String, Set<String>>();
				if (sqlDBColumnIdx.get("suggProdName") != null) {
					mappingDataSuggCustProdName = (String) obj[sqlDBColumnIdx.get("suggProdName")];
					if (mappingDataSuggCustProdName.split(AppConstants.SYM_DASH).length > 1) {
						String extProdId = mappingDataSuggCustProdName.split(AppConstants.SYM_DASH, 2)[1];
						
						hmSggInput.put(AppConstants.EXT_PRODID, extProdId);
						List<Object[]> extProd = viewDataDao.getExtProduct(hmSggInput);
						
						if (hmSggInput.get(AppConstants.INT_ITEMS) == null) {
							List<Object[]> alIntProducts = viewDataDao.getIntProductList(hmSggInput);
							hmSggInput.put(AppConstants.INT_ITEMS, alIntProducts);
						}
						
						hmSggInput.put(AppConstants.EXT_ITEM_OBJECT, extProd);
						hmSggInput.put(AppConstants.EXT_ITEM_INDEX, 3);
						hmSggInput.put(AppConstants.MAX_ITEMS, 1);
						
						List<NameCodeDistanceBean> alSggItms =  PEAUtils.getSuggestedItems(hmSggInput);
						oProdCustMapNameCodeDist = alSggItms.get(0);
					}
				}
				
				if (sqlDBColumnIdx.get("suggCustName") != null) {
					mappingDataSuggCustProdName = (String) obj[sqlDBColumnIdx.get("suggCustName")];
					if (mappingDataSuggCustProdName.split(AppConstants.SYM_DASH).length > 1) {
						String extCustId = mappingDataSuggCustProdName.split(AppConstants.SYM_DASH, 2)[1];
						
						hmSggInput.put(AppConstants.EXT_CUSTID, extCustId);
						List<Object[]> extCust = viewDataDao.getExtCustomer(hmSggInput);
						
						if (hmSggInput.get(AppConstants.INT_ITEMS) == null) {
							List<Object[]> alIntCustomers = viewDataDao.getIntCustomerList(hmSggInput);
							hmSggInput.put(AppConstants.INT_ITEMS, alIntCustomers);
						}
						
						hmSggInput.put(AppConstants.EXT_ITEM_OBJECT, extCust);
						hmSggInput.put(AppConstants.EXT_ITEM_INDEX, 1);
						hmSggInput.put(AppConstants.MAX_ITEMS, 1);
						
						List<NameCodeDistanceBean> alSggItms =  PEAUtils.getSuggestedItems(hmSggInput);
						oProdCustMapNameCodeDist = alSggItms.get(0);
					}
				}
				
				if (oProdCustMapNameCodeDist != null) {
					mappingDataUnmapped = true;
				}
				
				if (AppConstants.VIEWEXCEPTIONS.equalsIgnoreCase(tabmenu)) {
					int vpplinkPromotionId = 0; 
					if (sqlDBColumnIdx.get("promotionId") != null && obj[sqlDBColumnIdx.get("promotionId")] != null) {
						vpplinkPromotionId = (int)Double.parseDouble(obj[sqlDBColumnIdx.get("promotionId")].toString());
					}
					
					String promoPId = "";
					if (sqlDBColumnIdx.get("promoId") != null && obj[sqlDBColumnIdx.get("promoId")] != null)
						promoPId = obj[sqlDBColumnIdx.get("promoId")].toString();
					
//					String actions = "<div style=\"width: 70px;\"><input type=\"checkbox\" name=\"selectPromotion\" value=\"promoid\"> &nbsp;<a href=\"viewPromotions?promoId=1-CWOJUZ\"><i title=\"View Promotions\" class=\"fa fa-random\"></i></a><a style=\"margin-left: 10px;\" href=\"viewWeeklyPromotions\"><i title=\"View Weekly Promotions\" class=\"fa fa-list\"></i></a></div>";
					
					actions = "<div style=\"width: 40px;float:left;\"><input type=\"checkbox\" class=\"cb-reprocess\" data-PromoId=\"" + promoPId + "\"></div>";
					actions += "<div style=\"width: 20px;float:left;\"><a><span id=\"viewPromotedProductLink_" + vpplinkPromotionId + "\" class=\"viewPromotedProductLink\"><i title=\"View Promoted Products\" class=\"fa fa-list-ul\"></i></span></a></div>";
					 
//					if(obj[promoIdIdx] != null) {
//						String promoName = obj[sqlDBColumnIdx.get("promotion")].toString();
//						if (promoName != null) {
//							if (promoName.contains(AppConstants.SYM_POUND_SIGN)) {
//								promoName = promoName.replaceAll(AppConstants.SYM_POUND_SIGN, AppConstants.SYM_POUND);
//							}
//						}
//						actions = actions+ "<div style=\"width: 20px;float:left;\"><a><span id=\"overlappingLink_" + obj[promoIdIdx] + "\" class=\"overlappingLink\"><i title=\"View Overlapping\" class=\"fa fa-clone\"></i></span></a></div><input class=\"promotionName\" type=\"hidden\"  value=\""+ promoName +"\"  >";
//					
//					}
				}
				if (Arrays.asList(new String[]{AppConstants.VIEWEXCEPTIONS, AppConstants.VIEWDATASETS}).contains(tabmenu)) {
					if(sqlDBColumnIdx.get("promoId") != null 
							&& obj[sqlDBColumnIdx.get("promoId")] != null 
							&& sqlDBColumnIdx.get("overlappingPromotions") != null 
							&& obj[sqlDBColumnIdx.get("overlappingPromotions")] != null 
							&& obj[sqlDBColumnIdx.get("overlappingPromotions")].toString().trim().length() > 0) {
						
						String promoName = sqlDBColumnIdx.get("promotion") != null ? obj[sqlDBColumnIdx.get("promotion")].toString() : AppConstants.BLANK;
						if (promoName != null) {
							if (promoName.contains(AppConstants.SYM_POUND_SIGN)) {
								promoName = promoName.replaceAll(AppConstants.SYM_POUND_SIGN, AppConstants.SYM_POUND);
							}
						}
						actions = actions+ "<div style=\"width: 20px;float:left;\"><a><span id=\"overlappingLink_" + obj[sqlDBColumnIdx.get("promoId")] + "\" class=\"overlappingLink\"><i title=\"View Overlapping\" class=\"fa fa-clone\"></i></span></a></div><input class=\"promotionName\" type=\"hidden\"  value=\""+ promoName +"\"  >";
					    
					}
				}
				
//				if (Arrays.asList(new String[]{AppConstants.DQ_REJECTS}).contains(tabmenu)) {
//					
//					StringBuilder sbRowKey = new StringBuilder();
//					
//					for (String k : sakeyColIdx) {
//						sbRowKey.append(obj[Integer.parseInt(k)]);
//					}
//					
//					String status = sqlDBColumnIdx.get("Status") != null && obj[sqlDBColumnIdx.get("Status")] != null ? obj[sqlDBColumnIdx.get("Status")].toString() : "";
//					
//					actions = actions+ 
//							"<select data-rowKey=\""+ sbRowKey.toString() +"\">"
//								+ "<option " + (status.trim().equalsIgnoreCase("Fixed") ? "selected" : "") + " value=\"Fixed\">Fixed</option>"
//								+ "<option " + (status.trim().equalsIgnoreCase("Ignored") ? "selected" : "") + " value=\"Ignored\">Ignored</option>"
//								+ "<option " + (status.trim().equalsIgnoreCase("Pending") ? "selected" : "") + " value=\"Pending\">Pending</option>"
//							+ "</select>";
//					
//				}
				
				
				if (Arrays.asList(new String[]{AppConstants.MAPPING_DATA}).contains(tabmenu)) {
					int extProdId = sqlDBColumnIdx.get("ExtProdID") != null && obj[sqlDBColumnIdx.get("ExtProdID")] != null ? (int)Double.parseDouble(obj[sqlDBColumnIdx.get("ExtProdID")].toString()) : 0;
//					int intProdId = sqlDBColumnIdx.get("BrandFormID") != null && obj[sqlDBColumnIdx.get("BrandFormID")] != null ? (int)Double.parseDouble(obj[sqlDBColumnIdx.get("BrandFormID")].toString()) : 0;
					
					int intProdId = sqlDBColumnIdx.get("IntProdID") != null && obj[sqlDBColumnIdx.get("IntProdID")] != null ? (int)Double.parseDouble(obj[sqlDBColumnIdx.get("IntProdID")].toString()) : 0;
					String separator = AppConstants.separator;
					String mapping = sqlDBColumnIdx.get("Mapping") != null && obj[sqlDBColumnIdx.get("Mapping")] != null ? obj[sqlDBColumnIdx.get("Mapping")].toString() : "";
					String ignored = sqlDBColumnIdx.get("Ignored") != null && obj[sqlDBColumnIdx.get("Ignored")] != null ? obj[sqlDBColumnIdx.get("Ignored")].toString() : "";
//					String extBrandName = sqlDBColumnIdx.get("ExtBrand") != null && obj[sqlDBColumnIdx.get("ExtBrand")] != null ? obj[sqlDBColumnIdx.get("ExtBrand")].toString() : "";
//					String extFormName = sqlDBColumnIdx.get("ExtForm") != null && obj[sqlDBColumnIdx.get("ExtForm")] != null ? obj[sqlDBColumnIdx.get("ExtForm")].toString() : "";
//					String extSubformName = sqlDBColumnIdx.get("ExtSubForm") != null && obj[sqlDBColumnIdx.get("ExtSubForm")] != null ? obj[sqlDBColumnIdx.get("ExtSubForm")].toString() : ""; 
//					String brandName = sqlDBColumnIdx.get("Brand") != null && obj[sqlDBColumnIdx.get("Brand")] != null ? obj[sqlDBColumnIdx.get("Brand")].toString() : "";
//					String formName = sqlDBColumnIdx.get("Form") != null && obj[sqlDBColumnIdx.get("Form")] != null ? obj[sqlDBColumnIdx.get("Form")].toString() : "";
//					String subformName = sqlDBColumnIdx.get("SubForm") != null && obj[sqlDBColumnIdx.get("SubForm")] != null ? obj[sqlDBColumnIdx.get("SubForm")].toString() : "";
					String weightage = sqlDBColumnIdx.get("Weightage") != null && obj[sqlDBColumnIdx.get("Weightage")] != null ? obj[sqlDBColumnIdx.get("Weightage")].toString() : "";
					String extProdName = sqlDBColumnIdx.get("ExtProdName") != null && obj[sqlDBColumnIdx.get("ExtProdName")] != null ? obj[sqlDBColumnIdx.get("ExtProdName")].toString() : "";
					String extProdCode = sqlDBColumnIdx.get("ExtProdCode") != null && obj[sqlDBColumnIdx.get("ExtProdCode")] != null ? obj[sqlDBColumnIdx.get("ExtProdCode")].toString() : "";
					
					String intProdName = sqlDBColumnIdx.get("intProdName") != null && obj[sqlDBColumnIdx.get("intProdName")] != null ? obj[sqlDBColumnIdx.get("intProdName")].toString() : "";
					String intProdCode = sqlDBColumnIdx.get("intProdCode") != null && obj[sqlDBColumnIdx.get("intProdCode")] != null ? obj[sqlDBColumnIdx.get("intProdCode")].toString() : "";
					
//					if (obj[sqlDBColumnIdx.get("ExtProdID")] != null) {
//						extProdId = (int)Double.parseDouble(obj[sqlDBColumnIdx.get("ExtProdID")].toString());
//					}
					
					String intProdList = "";
//					if (!PEAUtils.isEmpty(brandName) && !PEAUtils.isEmpty(formName) && !PEAUtils.isEmpty(subformName)) {
//					if (!PEAUtils.isEmpty(brandName)) {
//						intProdList = brandName + separator + formName + separator + subformName + separator + weightage;
						if (!PEAUtils.isEmpty(intProdCode) && !PEAUtils.isEmpty(intProdName))
							intProdList = intProdCode + " - " + intProdName + separator + weightage;
//					}
					
					String extProdList = "";
//					if (!PEAUtils.isEmpty(extBrandName) && !PEAUtils.isEmpty(extFormName) && !PEAUtils.isEmpty(extSubformName)) {
//					if (!PEAUtils.isEmpty(extBrandName)) {	
//						extProdList = extBrandName + separator + extFormName + separator + extSubformName + separator + ExtProdName;
						extProdList = extProdCode + " - " + extProdName;
//					}
					
					actions = actions + "<button class=\"btn btn-primary btn-simple btn-xs edit\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Edit\" data-title=\"Edit\"  data-id=\"" + extProdId + "\" intProd-id=\"" + intProdId + "\" data-ExtProdList=\"" + extProdList + "\" data-IntProdList=\"" + intProdList + "\" style=\"margin:2px 2px;\" ><i class=\"fa  fa-pencil\"></i></button>";
					
					if (AppConstants.MAPPED.equalsIgnoreCase(mapping)) {
						actions = actions + "<button class=\"btn  btn-simple btn-xs bg-yellow prod-unmapped\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Unmap\" data-title=\"Unmap?\"  data-id=\"" + extProdId + "\" intProd-id=\"" + intProdId + "\" data-ExtProdList=\"" + extProdList + "\" data-IntProdList=\"" + intProdList + "\" style=\"margin:2px 2px;\" ><i class=\"fa  fa-chain-broken\"></i></button>";
					}
					if (AppConstants.UNMAPPED.equalsIgnoreCase(mapping) && !PEAUtils.convertToBoolean(ignored)) {
						actions = actions + "<button class=\"btn btn-simple btn-xs prod-ignored\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Ignore for future\" data-title=\"Ignore for future\"  data-id=\"" + extProdId + "\" intProd-id=\"" + intProdId + "\" data-ExtProdList=\"" + extProdList + "\" data-IntProdList=\"" + intProdList + "\" style=\"margin:2px 2px;\" ><i class=\"fa  fa-eye-slash\"></i></button>";
						
						if (mappingDataUnmapped) {
							actions = actions + "<button class=\"btn  btn-simple btn-xs bg-yellow prod-map\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Map\" data-title=\"Map?\" title=\"Map?\" data-id=\"" + extProdId + "\" intProd-id=\"" + oProdCustMapNameCodeDist.getId() + "\" data-ExtProdList=\"" + extProdList + "\" data-IntProdList=\"" + intProdList + "\" style=\"margin:2px 2px;\" ><i class=\"fa  fa-chain\"></i></button>";
						}
					}
				}
				if (Arrays.asList(new String[]{AppConstants.CUST_MAPPING_DATA}).contains(tabmenu)) {
					int extCustId = 0;
					int intCustId = sqlDBColumnIdx.get("IntCustId") != null && obj[sqlDBColumnIdx.get("IntCustId")] != null ? (int)Double.parseDouble(obj[sqlDBColumnIdx.get("IntCustId")].toString()) : 0;
					
					if (obj[sqlDBColumnIdx.get("ExtCustID")] != null) {
						extCustId = (int)Double.parseDouble(obj[sqlDBColumnIdx.get("ExtCustID")].toString());
					}
					
					String extCustName = sqlDBColumnIdx.get("ExtCustName") != null && obj[sqlDBColumnIdx.get("ExtCustName")] != null ? obj[sqlDBColumnIdx.get("ExtCustName")].toString() : "";
					String mapping = sqlDBColumnIdx.get("Mapping") != null && obj[sqlDBColumnIdx.get("Mapping")] != null ? obj[sqlDBColumnIdx.get("Mapping")].toString() : "";
					
					if (sqlDBColumnIdx.get("IntCustId") != null && obj[sqlDBColumnIdx.get("IntCustId")] != null) {
						intCustId =  (int)Double.parseDouble(obj[sqlDBColumnIdx.get("IntCustId")].toString());
					}
					
					actions = actions+"<button class=\"btn btn-primary btn-simple btn-xs custEdit\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Edit\" data-title=\"Edit\"  data-extId=\""+extCustId+"\" data-ExtCustName=\""+extCustName+"\" data-UniCustId=\""+intCustId+"\"style=\"margin:2px 2px;\" ><i class=\"fa  fa-pencil\"></i></button>";
					
//					if (Arrays.asList(new String[]{AppConstants.DQ_REJECTS}).contains(tabmenu)) {
//						actions = actions+"<button class=\"btn  btn-simple btn-xs cust-unmapped\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Unmap\" data-title=\"Unmap?\"  style=\"margin:2px 2px;\" ><i class=\"fa  fa-chain-broken\"></i></button>";
//					}
					
					if (AppConstants.MAPPED.equalsIgnoreCase(mapping)) {
						actions = actions+"<button class=\"btn  btn-simple btn-xs bg-yellow cust-unmapped\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Unmap\" data-title=\"Unmap?\"  data-extId=\""+extCustId+"\" data-ExtCustName=\""+extCustName+"\" data-UniCustId=\""+intCustId+"\"style=\"margin:2px 2px;\" ><i class=\"fa  fa-chain-broken\"></i></button>";
					}
					else {
						if (mappingDataUnmapped) {
							actions = actions+"<button class=\"btn  btn-simple btn-xs bg-yellow cust-map\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Map\" data-title=\"Map?\" title=\"Map?\" data-extId=\""+extCustId+"\" data-ExtCustName=\""+extCustName+"\" data-intCustId=\"" + oProdCustMapNameCodeDist.getId() + "\"style=\"margin:2px 2px;\" ><i class=\"fa  fa-chain\"></i></button>";
						}
						
					}
				}
				
				if (Arrays.asList(new String[]{AppConstants.MANAGE_BUSINESS_RULES}).contains(tabmenu)) {
					String ruleName = new String();
					String constraints = new String();
					String actionColumns = new String();
					String description = new String();
					String isEnabled = new String();
					
					if (sqlDBColumnIdx.get("RuleName") != null && obj[sqlDBColumnIdx.get("RuleName")] != null) {
						ruleName = obj[sqlDBColumnIdx.get("RuleName")].toString();
					}
					if (sqlDBColumnIdx.get("Description") != null && obj[sqlDBColumnIdx.get("Description")] != null) {
						description = obj[sqlDBColumnIdx.get("Description")].toString();
					}
					if (sqlDBColumnIdx.get("Constraints") != null && obj[sqlDBColumnIdx.get("Constraints")] != null) {
						constraints = obj[sqlDBColumnIdx.get("Constraints")].toString();
					}
					if (sqlDBColumnIdx.get("ActionColumn") != null && obj[sqlDBColumnIdx.get("ActionColumn")] != null) {
						actionColumns = obj[sqlDBColumnIdx.get("ActionColumn")].toString();
					}
					if (sqlDBColumnIdx.get("IsEnabled") != null && obj[sqlDBColumnIdx.get("IsEnabled")] != null) {
						isEnabled = obj[sqlDBColumnIdx.get("IsEnabled")].toString();
					}
					
					actions = "<button class=\"btn btn-danger btn-simple btn-xs BRDelete\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Delete\" data-title=\"Delete\"  data-RuleName=\"" + ruleName + "\" data-Constraints=\"" + constraints + "\" data-ActionColumn=\"" + actionColumns + "\"style=\"margin:2px 2px;\" ><i class=\"fa fa-times\"></i></button>";
					actions += "<button class=\"btn btn-primary btn-simple btn-xs BREdit\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Edit\" data-title=\"Edit\"  data-RuleName=\"" + ruleName + "\" data-description=\"" + description + "\" data-Constraints=\"" + constraints + "\" data-ActionColumns=\"" + actionColumns + "\"style=\"margin:2px 2px;\" ><i class=\"fa fa-pencil\"></i></button>";
//					actions += "<button class=\"btn btn-danger btn-simple btn-xs BRStatus\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Delete\" data-title=\"Delete\"  data-RuleName=\"" + ruleName + "\" data-Constraints=\"" + constraints + "\" data-ActionColumn=\"" + actionColumn + "\"style=\"margin:2px 2px;\" ><i class=\"fa fa-times\"></i></button>";
					
					if (PEAUtils.convertToBoolean(isEnabled)) {
						actions += "<button class=\"btn btn-warning btn-simple btn-xs BRDisable\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Disable\" data-title=\"Disable\"  data-RuleName=\"" + ruleName + "\" data-Constraints=\"" + constraints + "\" data-ActionColumn=\"" + actionColumns + "\"style=\"margin:2px 2px;\" ><i class=\"fa fa-toggle-off\"></i></button>";
					}
					else {
						actions += "<button class=\"btn btn-success btn-simple btn-xs BREnable\" data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Enable\" data-title=\"Enable\"  data-RuleName=\"" + ruleName + "\" data-Constraints=\"" + constraints + "\" data-ActionColumn=\"" + actionColumns + "\"style=\"margin:2px 2px;\" ><i class=\"fa fa-toggle-on\"></i></button>";
					}
				}
				
				if (!PEAUtils.isEmpty(actions)) {
					jo.addProperty("actions", actions);
				}
				
				String sKeyIdx = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
				String sRowKey = new String();
				
				if (sKeyIdx != null) {
					for (String sIdx : sKeyIdx.split(AppConstants.SYM_PIPE_REGX)) {
						if (obj[Integer.parseInt(sIdx)] != null) {
							sRowKey += obj[Integer.parseInt(sIdx)].toString() + sEditRowKeyDelim;
						}
					}
					if (sRowKey.contains(sEditRowKeyDelim)) {
						sRowKey = sRowKey.substring(0, sRowKey.length() - sEditRowKeyDelim.length());
					}
				}
				
				if (sqlDBColumnIdx.get("BizRules") != null && obj[sqlDBColumnIdx.get("BizRules")] != null) {
					String ruleIds = obj[sqlDBColumnIdx.get("BizRules")].toString();
					String[] ruleIdArray = ruleIds.split(",");
					for( String ruleId : ruleIdArray){
						for(String ruleColumn : (Set<String>) hmBRDetails.get(ruleId).get(AppConstants.RULE_COLUMN)){
						Set<String> ruleDesc = businessRuleColumns.getOrDefault(ruleColumn, new HashSet<String>());
						ruleDesc.add(hmBRDetails.get(ruleId).get(AppConstants.RULE_DESC).toString());
						businessRuleColumns.put(ruleColumn,ruleDesc);
						}
					}
				}
				
				for (Map.Entry<String, Map<String, Object>> entry : lhmColMappings.entrySet()) {
					String val = new String();
					String sColIdx = entry.getKey();
					
					
					Map<String, Object> hmColSetting = entry.getValue();
					
					if (obj[Integer.parseInt(sColIdx.replace(AppConstants.COL, ""))] != null) {
						Object obVal = obj[Integer.parseInt(sColIdx.replace(AppConstants.COL, ""))];
						boolean isNegative = false;
						if (AppConstants.N.equalsIgnoreCase(hmColSetting.get(AppConstants.COLTYPE).toString())) {
							
							if (hmColSetting.get(AppConstants.COL_DATATYPE) != null && AppConstants.PERCENTAGE.equalsIgnoreCase(hmColSetting.get(AppConstants.COL_DATATYPE).toString())) {
								obVal = ((BigDecimal) obVal).multiply(new BigDecimal(100));
							}
							
							if (hmColSetting.get(AppConstants.COL_DECIMALS) != null) {
								if (obVal instanceof BigDecimal) {
									obVal = ((BigDecimal) obVal).setScale(Integer.parseInt(hmColSetting.get(AppConstants.COL_DECIMALS).toString()), RoundingMode.HALF_UP);
								}
							}
							if (obVal != null && obVal.toString().trim().length() > 0 && Double.parseDouble(obVal.toString()) < 0) {
								isNegative = true;
								obVal = ((BigDecimal) obVal).abs();
							}
							
							if (hmColSetting.get(AppConstants.COL_DATATYPE) != null && AppConstants.PERCENTAGE.equalsIgnoreCase(hmColSetting.get(AppConstants.COL_DATATYPE).toString())) {
								if (obVal instanceof BigDecimal)
									obVal = String.format("%,." + hmColSetting.get(AppConstants.COL_DECIMALS).toString() + "f", (BigDecimal)obVal);
								
								obVal = obVal.toString() + " " + AppConstants.SYM_PERC ;
							}
							
							if (hmColSetting.get(AppConstants.COL_DATATYPE) != null && AppConstants.CURRENCY.equalsIgnoreCase(hmColSetting.get(AppConstants.COL_DATATYPE).toString())) {
								BigDecimal bdVal = new BigDecimal(obVal.toString());
								//Change Exchange rate here
//								obVal = bdVal.multiply(new BigDecimal(AppConstants.EXCHG_RATE_USD_INR));
								
								if (obVal instanceof BigDecimal)
									obVal = String.format("%,." + hmColSetting.get(AppConstants.COL_DECIMALS).toString() + "f", (BigDecimal)obVal);
								//Change currency symbol
								obVal = AppConstants.SYM_POUND + " " + obVal.toString();
							}
							
							if (obVal instanceof BigDecimal)
								obVal = String.format("%,." + hmColSetting.get(AppConstants.COL_DECIMALS).toString() + "f", (BigDecimal)obVal);
							
							if (isNegative) {
								obVal = "<span style=\"color: red;\">(" + obVal.toString() + ")</span>";
							}
						}
						
						if(!PEAUtils.isEmpty(hmColSetting.get(AppConstants.ACTUAL_DB_COL).toString()) && businessRuleColumns.containsKey(hmColSetting.get(AppConstants.ACTUAL_DB_COL).toString())){
							Set<String> ruleDescription = businessRuleColumns.get(hmColSetting.get(AppConstants.ACTUAL_DB_COL).toString());
							String mergedString = ruleDescription.stream().filter(string ->!string.isEmpty()).collect(Collectors.joining(","));
							obVal = "<span title=\""+mergedString+"\" style=\"background-color: burlywood;\">" + obVal.toString() + "</span>";
						}
						val = obVal.toString();
					}
					
//					else {
//						jo.addProperty(entry.getValue().get(AppConstants.DBCOLUMN).getAsString(), "");
//					}
					
					if (AppConstants.D.equalsIgnoreCase(entry.getValue().get(AppConstants.COLTYPE).toString())) {
						if (PEAUtils.getDateFormat(tabmenu) != null) {
							SDF = new SimpleDateFormat(PEAUtils.getDateFormat(tabmenu));
						}
						try {
							if (!PEAUtils.isEmpty(val)) {
								if (val.length() > 10) {
									val = SDF.format(SDF_YMDHMS.parse(val));
								}
								else {
									val = SDF.format(SDF_YMD.parse(val));
								}
							}
						} catch (Exception e) {
							logger.warn(e.getMessage(), e);
//							if (e instanceof java.text.ParseException) {
//								logger.warn(e.getMessage());
//							} else {
//								throw e;
//							}
						}
					}
					
					if (AppConstants.MAPPING_DATA.equalsIgnoreCase(tabmenu)) {
						// Suggested Product Name for unmapped products
						if (mappingDataUnmapped && entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("intprodname")) {
							val = oProdCustMapNameCodeDist.getName();
							if (oProdCustMapNameCodeDist.getDistance() > 70)
								val = "<span style=\"color:green; font-weight: bold;\">" + val + "</span>";
							else
								val = "<span style=\"color:red; font-weight: bold;\">" + val + "</span>";
						}
						
						// Suggested Product Code for unmapped products
						if (mappingDataUnmapped && entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("intprodcode")) {
							val = oProdCustMapNameCodeDist.getCode();
							if (oProdCustMapNameCodeDist.getDistance() > 70)
								val = "<span style=\"color:green; font-weight: bold;\">" + val + "</span>";
							else
								val = "<span style=\"color:red; font-weight: bold;\">" + val + "</span>";
						}
						
						// Suggested Product's Confidence for unmapped products
						if (mappingDataUnmapped && entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("weightage")) {
							val = oProdCustMapNameCodeDist.getDistance() + "%";
							if (oProdCustMapNameCodeDist.getDistance() > 70)
								val = "<span style=\"color:green; font-weight: bold;\">" + val + "</span>";
							else
								val = "<span style=\"color:red; font-weight: bold;\">" + val + "</span>";
						}
					}
					
					if (AppConstants.CUST_MAPPING_DATA.equalsIgnoreCase(tabmenu)) {
						// Suggested Customer Name for unmapped products
						if (mappingDataUnmapped && entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("intcustname")) {
							val = oProdCustMapNameCodeDist.getName();
							if (oProdCustMapNameCodeDist.getDistance() > 70)
								val = "<span style=\"color:green; font-weight: bold;\">" + val + "</span>";
							else
								val = "<span style=\"color:red; font-weight: bold;\">" + val + "</span>";
						}
						
						// Suggested Customer Code for unmapped products
						if (mappingDataUnmapped && entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("intcustcode")) {
							val = oProdCustMapNameCodeDist.getCode();
							if (oProdCustMapNameCodeDist.getDistance() > 70)
								val = "<span style=\"color:green; font-weight: bold;\">" + val + "</span>";
							else
								val = "<span style=\"color:red; font-weight: bold;\">" + val + "</span>";
						}
						
						// Suggested Customer's Confidence for unmapped products
						if (mappingDataUnmapped && entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("weightage")) {
							val = oProdCustMapNameCodeDist.getDistance() + "%";
							if (oProdCustMapNameCodeDist.getDistance() > 70)
								val = "<span style=\"color:green; font-weight: bold;\">" + val + "</span>";
							else
								val = "<span style=\"color:red; font-weight: bold;\">" + val + "</span>";
						}
					}
					
					if (entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("businessrule") && val != null) {
						
						List<String> lRules = new ArrayList<String>();
						StringBuffer sbRules = new StringBuffer("<select class=\""+ entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase() + "_" + tabmenu +"\" size=\"2\" multiple=\"multiple\" style=\"width: 100%;\">");
						for (String s : val.split(",")) {
							if (!PEAUtils.isEmpty(s)) 
								s = s.trim();
							
							if (!lRules.contains(bizRuleMap.get(s))) {
								sbRules.append("<option title=\"" + bizRuleDescMap.getOrDefault(s, s) + "\" value=\"" + s + "\">" + bizRuleMap.getOrDefault(s, s) + "</option>");
								lRules.add(bizRuleMap.get(s));
							}
						}
						
						derivedObj[Integer.parseInt(sColIdx.replace(AppConstants.COL, ""))] = StringUtils.join(lRules.toArray(), ", "); 
						
						
						sbRules.append("</select>");
						val = sbRules.toString();
					}
					
					if (entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("overlappingpromotions") && val != null) {
						
						List<String> lOverlappingPromotions = new ArrayList<String>();
						StringBuffer sbOverlappingPromotions = new StringBuffer("<select class=\""+ entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase() + "_" + tabmenu +"\" size=\"2\" multiple=\"multiple\" style=\"width: 100%;\">");
						for (String s : val.split(",")) {
							if (!PEAUtils.isEmpty(s)) 
								s = s.trim();
							
							if (!lOverlappingPromotions.contains(s)) {
								sbOverlappingPromotions.append("<option title=\"" + s + "\" value=\"" + s + "\">" + s + "</option>");
								lOverlappingPromotions.add(s);
							}
						}
						
						//derivedObj[Integer.parseInt(sColIdx.replace(AppConstants.COL, ""))] = StringUtils.join(lRules.toArray(), ", "); 
						
						
						sbOverlappingPromotions.append("</select>");
						val = sbOverlappingPromotions.toString();
					}
					
					if (entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase().contains("action") && val != null) {
						System.out.println("You are missing action for - " + tabmenu);
//						StringBuffer sbRules = new StringBuffer("<select class=\""+ entry.getValue().get(AppConstants.DBCOLUMN).toString().toLowerCase() + "_" + tabmenu +"\" size=\"2\" multiple=\"multiple\" style=\"width: 100%;\">");
//						for (String s : val.split(",")) {
//							if (s != null && s.trim().length() > 0)
//								sbRules.append("<option title=\"" + StringEscapeUtils.escapeHtml(s) + "\" value=\"" + StringEscapeUtils.escapeHtml(s) + "\">" + StringEscapeUtils.escapeHtml(s) + "</option>");
//						}
//						sbRules.append("</select>");
//						val = sbRules.toString();
					}
					
					//code added to find total on data availabilty page
					BigDecimal totalSum = null;
					
					if(!val.isEmpty()&& tabmenu.equalsIgnoreCase(AppConstants.DATAAVAILABILITY)){
						if (AppConstants.N.equalsIgnoreCase(entry.getValue().get(AppConstants.COLTYPE).toString())) {
							
							Object object1 = obj[Integer.parseInt(sColIdx.replace(AppConstants.COL, ""))];
							
							if(total.containsKey(entry.getValue().get(AppConstants.DBCOLUMN))){
								Double sum = null;
							    totalSum = null;
							   
								sum = total.get(entry.getValue().get(AppConstants.DBCOLUMN));							
								if(entry.getValue().get(AppConstants.DBCOLUMN).toString().equalsIgnoreCase("preRatio")||entry.getValue().get(AppConstants.DBCOLUMN).toString().equalsIgnoreCase("curRatio")){								
									if(entry.getValue().get(AppConstants.DBCOLUMN).toString().equalsIgnoreCase("preRatio")) {	
										 
										Double ratio =(total.get("preEPOSVolume")/total.get("preShipmentVolume"))*100;								
										total.put("preRatio", new BigDecimal(ratio).setScale(2,RoundingMode.HALF_UP).doubleValue());
									}
									else if(entry.getValue().get(AppConstants.DBCOLUMN).toString().equalsIgnoreCase("curRatio")) {
										
										Double ratio	=(total.get("curEPOSVolume")/total.get("curShipmentVolume"))*100;								
										total.put("curRatio", new BigDecimal(ratio).setScale(2,RoundingMode.HALF_UP).doubleValue());
									 }
								} 
								else {	
										totalSum = new BigDecimal(sum + Double.parseDouble(object1.toString()));	
										total.put(entry.getValue().get(AppConstants.DBCOLUMN).toString(), totalSum.doubleValue());
								}
							} 
							else {
								total.put(entry.getValue().get(AppConstants.DBCOLUMN).toString(), Double.parseDouble(object1.toString()));
							}
						}
					}
					
					if (val.contains(AppConstants.SYM_POUND_SIGN)) {
						val = val.replaceAll(AppConstants.SYM_POUND_SIGN, AppConstants.SYM_POUND);
					}
					
					if (PEAUtils.convertToBoolean(entry.getValue().get(AppConstants.COLUMN_TRIM))) {
						int trimLen = Integer.parseInt(entry.getValue().get(AppConstants.TRIM_LENGTH).toString());
						if (val.length() > trimLen) {
							String oriVal = val;
							val = new String(val.substring(0, trimLen)) + "...";
							val = "<span title=\"" + oriVal + "\">" + val + "</span>";
						}
					}
					
					if (PEAUtils.convertToBoolean(hmColSetting.get(AppConstants.EDITABLE))) {
					//if (hmColEditType.get(sColIdx) != null) {
						String promoId = "";
						String editColDisabled = new String();
						
						if (!PEAUtils.isEmpty(hmColSetting.getOrDefault(AppConstants.EDIT_COL_DISABLED, "").toString())) {
							editColDisabled = (PEAUtils.convertToBoolean(hmColSetting.get(AppConstants.EDIT_COL_DISABLED).toString()) ? "disabled" : "");
						}
						
						if (sqlDBColumnIdx.get("promoId") != null && obj[sqlDBColumnIdx.get("promoId")] != null)
							promoId = obj[sqlDBColumnIdx.get("promoId")].toString();
						
						switch (hmColSetting.get(AppConstants.EDIT_COL_TYPE).toString()) {
							case AppConstants.T:
								String typeCls = "clsString";
								switch (hmColSetting.get(AppConstants.COLTYPE).toString().toUpperCase()) {
									case AppConstants.N:
										typeCls = "clsNumeric";
								}
								val = "<input style=\"width:95%\" type=\"text\" class=\"" + tabmenu + "-ef-text-" + sColIdx + " " + typeCls + "\" data-PromoId=\"" + promoId + "\" data-rowkey=\"" + sRowKey + "\" value=\""+ val +"\" " + editColDisabled + "></input>";
								break;
							case AppConstants.S: 
								if (Arrays.asList(new String[]{AppConstants.DQ_REJECTS}).contains(tabmenu)) {
									
									StringBuilder sbRowKey = new StringBuilder();
									for (String k : sakeyColIdx) {
										sbRowKey.append(obj[Integer.parseInt(k)]);
									}
									String status = val;
									val =	"<select " + editColDisabled + " class=\"" + tabmenu + "-ef-select-" + sColIdx + "\" data-rowKey=\""+ sbRowKey.toString() +"\">"
												+ "<option " + (status.trim().equalsIgnoreCase("Fixed") ? "selected" : "") + " value=\"Fixed\">Fixed</option>"
												+ "<option " + (status.trim().equalsIgnoreCase("Ignored") ? "selected" : "") + " value=\"Ignored\">Ignored</option>"
												+ "<option " + (status.trim().equalsIgnoreCase("Pending") ? "selected" : "") + " value=\"Pending\">Pending</option>"
											+ "</select>";
								}
								else {
									val = "<select " + editColDisabled + " style=\"width:95%\" class=\"" + tabmenu + "-ef-select-" + sColIdx + "\" data-PromoId=\"" + promoId + "\" data-rowkey=\"" + sRowKey + "\"><option>" + val + "</option></input>";
								}

								break;
							case AppConstants.CB:
								String checked = "";
								if (PEAUtils.convertToBoolean(val)) {
									checked = "checked";
								}
								val = "<input " + editColDisabled + " type=\"checkbox\" class=\"" + tabmenu + "-ef-cb-" + sColIdx + "\" data-rowkey=\"" + sRowKey + "\" " + checked + ">";
								break;
						}
					}
					
					jo.addProperty(entry.getValue().get(AppConstants.DBCOLUMN).toString(), val);
					
				}
				
				listData.add(jo);
				listObjArr.add(derivedObj);
			}
			
			int iSortDir = input.get(AppConstants.SORT_DIR_0).toString().equalsIgnoreCase(AppConstants.ASC) ? 1 : -1;
			Map hmSortColMapping = lhmColMappings.get(AppConstants.COL + input.get(AppConstants.SORT_COL_0).toString() + "");
			String sortColName = (String) hmSortColMapping.get(AppConstants.DBCOLUMN);
			System.out.println("Sort column - " + sortColName);
			
			if (PEAUtils.convertToBoolean(hmSortColMapping.get(AppConstants.SORT_MANUAL))) {
				
				if (!((AppConstants.MAPPING_DATA.equalsIgnoreCase(tabmenu) || AppConstants.CUST_MAPPING_DATA.equalsIgnoreCase(tabmenu)) && !mappingDataUnmapped)) {
					Collections.sort(listData, new Comparator<JsonObject>() {
		
						@Override
						public int compare(JsonObject o1, JsonObject o2) {
							switch(sortColName.toLowerCase()) {
								case "weightage":
									Double x1 = 0d, x2 = 0d;
									if (o1.get(sortColName) != null && o2.get(sortColName) != null) {
										x1 = Double.parseDouble((o1.get(sortColName)+"").split("\">", 2).clone()[1].split("%</")[0]);
										x2 = Double.parseDouble((o2.get(sortColName)+"").split("\">", 2).clone()[1].split("%</")[0]);
									}
									if (x1 < x2) {
										return iSortDir * -1;
									}
									if (x1 > x2) {
										return iSortDir * 1;
									}
									break;
								case "intprodcode":
								case "intcustcode":
									String cc1 = AppConstants.BLANK, cc2 = AppConstants.BLANK;
									if (o1.get(sortColName) != null && o2.get(sortColName) != null) {
										cc1 = (o1.get(sortColName)+"").split("\">", 2).clone()[1].split("</")[0];
										cc2 = (o2.get(sortColName)+"").split("\">", 2).clone()[1].split("</")[0];
									}
									if (cc1.compareToIgnoreCase(cc2) < 0) {
										return iSortDir * -1;
									}
									if (cc1.compareToIgnoreCase(cc2) > 0) {
										return iSortDir * 1;
									}
									break;
								case "intprodname":
								case "intcustname":
									String cn1 = AppConstants.BLANK, cn2 = AppConstants.BLANK;
									if (o1.get(sortColName) != null && o2.get(sortColName) != null) {
										cn1 = (o1.get(sortColName)+"").split("\">", 2).clone()[1].split("</")[0];
										cn1 = (o2.get(sortColName)+"").split("\">", 2).clone()[1].split("</")[0];
									}
									if (cn1.compareToIgnoreCase(cn2) < 0) {
										return iSortDir * -1;
									}
									if (cn1.compareToIgnoreCase(cn2) > 0) {
										return iSortDir * 1;
									}
									break;	
							}
							return 0;
						}
					});
				}
			}
			if (!total.isEmpty()) {
				jo = new JsonObject();
				for (String key : total.keySet()) {
					String val = "";
					if (key.equalsIgnoreCase("curDiff") ) {
						Double diff = total.get("curRatio") - total.get("preRatio");
						diff = new BigDecimal(diff).setScale(2, RoundingMode.HALF_UP).doubleValue();
						
						if(diff != null) {
							if (diff < 0) {
								//val = Double.toString(diff * -1) + "%";
								val = String.format("%,.2f", (diff * -1)) + "%";
								val = "<span style=\"color: red;\">(" + val + ")</span>";
							}
							else {
								val = String.format("%,.2f", (diff));
								val = diff + "%";
							}
						}
						total.put(key, diff);
						jo.addProperty(key, val);
					}
					else {
						if (!key.equalsIgnoreCase("preDiff")) {
							boolean isRatioColumn = false;
							if (key.equalsIgnoreCase("curRatio")) {
								Double ratio = (total.get("curEPOSVolume") / total.get("curShipmentVolume")) * 100;
								ratio = new BigDecimal(ratio).setScale(2, RoundingMode.HALF_UP).doubleValue();
								total.put(key, ratio);
								isRatioColumn = true;
							}
							if (key.equalsIgnoreCase("preRatio")) {
								Double ratio = (total.get("preEPOSVolume") / total.get("preShipmentVolume")) * 100;
								ratio = new BigDecimal(ratio).setScale(2, RoundingMode.HALF_UP).doubleValue();
								total.put(key, ratio);
								isRatioColumn = true;
							}
							
							if (total.get(key) != null) {
								if (total.get(key) < 0) {
									
//									val = String.format("%,.2f", total.get(key));
									val = Double.toString(total.get(key) * -1);
									if (isRatioColumn) {
										val = String.format("%,.2f", (total.get(key) * -1));
										val += "%";
									}
									else {
										val = String.format("%,.0f", (total.get(key) * -1));
									}
									val = "<span style=\"color: red;\">(" + val + ")</span>";
								}
								else {
									val = "" + total.get(key); //String.format("%,.2f", total.get(key));
									if (isRatioColumn) {
										val = String.format("%,.2f", total.get(key));
										val += "%";
									}
									else {
										val = String.format("%,.0f", total.get(key));
									}
								}
							}
							jo.addProperty(key, val);
						}
					}
				}
				if (total.get("curRatio") != null && total.get("preRatio") != null) {
					String key = "curDiff", val = "";
					
					Double diff = total.get("curRatio") - total.get("preRatio");
					diff = new BigDecimal(diff).setScale(2, RoundingMode.HALF_UP).doubleValue();
					
					if(diff != null) {
						if (diff < 0) {
							//val = Double.toString(diff * -1) + "%";
							val = String.format("%,.2f", (diff * -1)) + "%";
							val = "<span style=\"color: red;\">(" + val + ")</span>";
						}
						else {
							val = String.format("%,.2f", (diff));
							val = diff + "%";
						}
					}
					total.put(key, diff);
					jo.addProperty(key, val);
				}
				if (listData.size() > 0) {
					listData.add(jo);
				}
			}
			
			out.put("totalCount", sTotalCount);
			out.put(AppConstants.DATA.toLowerCase(), listData);
			out.put(AppConstants.DATA_LIST_ARRAY, dataOut.get(AppConstants.DATA));
			out.put(AppConstants.DERIVED_DATA_LIST_ARRAY, listObjArr);
			
		} catch (Exception e) {
			logger.error("Exception in getData - ", e);
			throw e;
		}
		
		return out;
	}

	

	@Override
	public List<CustomerStats> viewDataAvailability(Map<String, Object> input) {
		// TODO Auto-generated method stub

		List<CustomerStats> listCustomerStats = new ArrayList<CustomerStats>();
		try {
			listCustomerStats = viewDataDao.getDataAvailabilityStat(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listCustomerStats;
	}


	
	@Override
	public List<Object[]> getHierarchiesSQL(String type) {
		// TODO Auto-generated method stub
		List<Object[]> ob = new ArrayList<Object[]>();
		try {
			if (AppConstants.PRODUCTHIERACHY.equalsIgnoreCase(type)) {
				ob = viewDataDao.getProductHierarchySQL();
			}
			else if (AppConstants.CUSTOMERHIERACHY.equalsIgnoreCase(type)) {
				ob = viewDataDao.getCustomerHierarchySQL();
			}
		} catch (Exception e) {
			logger.error("Exception in getHierarchiesSQL", e);
		}
		return ob;
	}


	
	@Override
	public List<CustDIMBean> getCustomerList(Map<String, Object> input) throws Exception {
		new HashMap<String, String>();
		List<CustDIMBean> list = viewDataDao.getCustomerList(input);
//		for (CustDIMBean cust : list) {
//			hmCustomers.put(Long.toString(cust.getCustomerId()), cust.getCustomerName());
//		}
//		return hmCustomers;
		return list;
	}
	
	@Override
	public List<CategoryBrandBean> getProdCategoryBrandList(Map<String, Object> input) throws Exception {
		List<String> list = viewDataDao.getProdCategoryBrandList(input);
		Map<String, CategoryBrandBean> map = new TreeMap<String, CategoryBrandBean>();
		for (String o : list) {
			if (o != null) {
				CategoryBrandBean c = map.getOrDefault(o.toString(), new CategoryBrandBean(o.toString()));
				c.getBrand();
//				if (brList == null) {
//					brList = new ArrayList<String>();
//				}
			//	brList.add(o[1].toString());
				//c.setBrand(brList);
				map.put(c.getCategory(), c);
			}
		}
		return new ArrayList<CategoryBrandBean>(map.values());
	};
	
	@Override
	public List<ProdDIMBean> getProductList(Map<String, Object> input) throws Exception {
		new HashMap<String, String>();
		List<ProdDIMBean> list = viewDataDao.getProductList(input);
//		for (ProdDIMBean prod : list) {
//			hmProducts.put(Long.toString(prod.getProductId()), prod.getProductName());
//		}
//		return hmProducts;
		return list;
	}
	
	@Override
	public Map<String, String> getOverLappingPromotions(String promoId) throws Exception {
		Map<String, String> OverLappingPromotions = new HashMap<String, String>();
		OverLappingPromotions = viewDataDao.getOverlappingPromotion(promoId);
		
		return OverLappingPromotions;
	}
	
	@Override
	public Map<String, String> getDatasetList(Map<String, Object> input)
			throws Exception {
		Map<String, String> hmDatasets = new HashMap<String, String>();
		List<DatasetMeta> list = viewDataDao.getDatasetList(input);
		for (DatasetMeta dataset : list) {
			hmDatasets.put(Long.toString(dataset.getDatasetId()), dataset.getDatasetName());
		}
		return hmDatasets;
	}
	
	@Override
	public Map<String, DatasetMeta> getDatasetObjectList(Map<String, Object> input)
			throws Exception {
		Map<String, DatasetMeta> hmDatasets = new HashMap<String, DatasetMeta>();
		List<DatasetMeta> list = viewDataDao.getDatasetList(input);
		for (DatasetMeta dataset : list) {
			hmDatasets.put(Long.toString(dataset.getDatasetId()), dataset);
		}
		return hmDatasets;
	}
	
	@Override
	public List<CustomerStats> getAccountList(Map<String, Object> input) throws Exception {
		
		//Map<String, String> hmAccounts = new HashMap<String, String>();
		List<CustomerStats> list = viewDataDao.getAccountList(input);
		
		return list;
	}
	
	@Override
	public Map<String, String> getBusinessRuleList(Map<String, Object> input) throws Exception {
		
		Map<String, String> hmBizRules = new HashMap<String, String>();
		List<BusinessRuleMeta> list = viewDataDao.getBusinessRuleList(input);
		for (BusinessRuleMeta bizRuleMeta : list) {
			hmBizRules.put(Long.toString(bizRuleMeta.getRuleId()), bizRuleMeta.getRuleShortDesc());
		}
		return hmBizRules;
	}
	
	@Override
	public List<BusinessRuleMeta> getBusinessRuleBeanList(Map<String, Object> input) throws Exception {
		
		List<BusinessRuleMeta> list = viewDataDao.getBusinessRuleList(input);
//		for (BusinessRuleMeta bizRuleMeta : list) {
//			BusinessRuleBean bean = new BusinessRuleBean();
//			bean.setRuleId(bizRuleMeta.getRuleId());
//			bean.setRuleShortDesc(bizRuleMeta.getRuleShortDesc());
//			bean.setRuleDescription(bizRuleMeta.getRuleDescription());
//			bean.setRuleType(bizRuleMeta.getRuleType());
//			
//			hmBizRules.put(bean.getRuleId(), bean);
//		}
//		return hmBizRules;
		return list;
	}
	
	@Override
	public Map<String, String> getBusinessRuleDescMap(Map<String, Object> input) throws Exception {
		
		Map<String, String> hmBizRules = new HashMap<String, String>();
		List<BusinessRuleMeta> list = viewDataDao.getBusinessRuleList(input);
		for (BusinessRuleMeta bizRuleMeta : list) {
			hmBizRules.put(Long.toString(bizRuleMeta.getRuleId()), bizRuleMeta.getRuleDescription());
		}
		return hmBizRules;
	}
	
	@Override
	public Map<String, String> getPromotionList(Map<String, Object> input)
			throws Exception {
		Map<String, String> hmPromotions = new HashMap<String, String>();
		List<PromoDIMBean> list = viewDataDao.getPromotionList(input);
		for (PromoDIMBean promo : list) {
			hmPromotions.put(Long.toString(promo.getPromotionId()), promo.getPromotion());
		}
		return hmPromotions;
	}


	@Override
	public Map<String, Map<String, List<String>>> getViewColumns(Map<String, Object> input) throws Exception {
		
		Map<String, WbUserColumnConfig> hmConfig = new HashMap<String, WbUserColumnConfig>();
		Map<String, Map<String, List<String>>> viewColumns = new HashMap<String, Map<String, List<String>>>();
		String [] views = { AppConstants.VIEWEXCEPTIONS, AppConstants.VIEWPROMOTEDPRODUCTS, AppConstants.VIEWDATASETS };
		String currentUser = input.get(AppConstants.USERNAME).toString();
		
		for (String currentView : views) {
			hmConfig.put(currentView, viewDataDao.getViewConfig(currentUser, currentView));
		}
		
		for (Map.Entry<String, WbUserColumnConfig> entry : hmConfig.entrySet()) {
		
			String viewName = entry.getKey();
			WbUserColumnConfig userView = entry.getValue();
			String columnsToShow = userView.getDisplayColumns();
			String columnsToHide = userView.getHiddenColumns();
			Map<String, List<String>> colMap = new HashMap<String, List<String>>();
			
			String sHiddenCols = PropertiesUtil.getProperty(viewName + AppConstants.DOT + AppConstants.COLUMN_HIDDEN);
			List<String> lHiddenCols = new ArrayList<String>();
			if (sHiddenCols != null) {
				lHiddenCols = Arrays.asList(sHiddenCols.split(AppConstants.SYM_PIPE_REGX));
			}
			
			String[] colMaping = PropertiesUtil.getProperty(viewName + AppConstants.DOT + AppConstants.COLUMN_MAPPING).split(AppConstants.SYM_PIPE_REGX);
			Map<String, String> viewColDispMap = new HashMap<String, String>();
			for(String cm : colMaping) {
				if (cm != null) {
					if (cm.length() > 2)
						viewColDispMap.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[2]);
				}
			}
			
			if (columnsToShow != null) {
				List<String> lColumnsToShow = Arrays.asList(columnsToShow.split(AppConstants.SYM_PIPE_REGX));
				List<String> lColDisplay = new ArrayList<String>();
				String stFirstElement = null;
				for (String s : lColumnsToShow) {
					if (s != null && s.length() > 0 && lHiddenCols.indexOf(s.replace(AppConstants.COL, "")) < 0 && viewColDispMap.get(s) != null){
						lColDisplay.add(viewColDispMap.get(s));
						if(stFirstElement == null)
							stFirstElement = viewColDispMap.get(s);
					}
				}
				if(currentUser != null && currentUser.equalsIgnoreCase("default")){
					Collections.sort(lColDisplay);
					lColDisplay.remove(stFirstElement);
					lColDisplay.add(0, stFirstElement);
				}
				
				colMap.put("display", lColDisplay);
			}
			
			if (columnsToHide != null) {
				List<String> lColumnsToHide = Arrays.asList(columnsToHide.split(AppConstants.SYM_PIPE_REGX));
				List<String> lColHidden = new ArrayList<String>();
				for (String s : lColumnsToHide) {
					if (s != null && s.length() > 0 && lHiddenCols.indexOf(s.replace(AppConstants.COL, "")) < 0 && viewColDispMap.get(s) != null)
						lColHidden.add(viewColDispMap.get(s));
				}
				Collections.sort(lColHidden);
				colMap.put("hidden", lColHidden);
			}
			viewColumns.put(viewName, colMap);
		}
		
		return viewColumns;
	}
	@Override
	public List<SblProductHierarchy> getProductHierarchies() {
		// TODO Auto-generated method stub
		List<SblProductHierarchy> sblProductHierarchies = new ArrayList<SblProductHierarchy>();
		try{
			
			sblProductHierarchies = viewDataDao.getProductHierarchy();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sblProductHierarchies;
	}
	
		@Override
	public List<SblCustomerHierarchy> getCustomerHierarchies() {
		// TODO Auto-generated method stub
		List<SblCustomerHierarchy> sblCustomerHierarchies = new ArrayList<SblCustomerHierarchy>();
		try{
			
			sblCustomerHierarchies = viewDataDao.getCustomerHierarchy();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sblCustomerHierarchies;
	}
	@Override
	public Map<?, ?> saveViewColumns(Map<String, Object> input) throws Exception {
		
		String viewName = input.get(AppConstants.VIEW).toString();
		StringBuffer sbDisplayCols = new StringBuffer();
		StringBuffer sbHiddenCols = new StringBuffer();
		
		String[] colMaping = PropertiesUtil.getProperty(viewName + AppConstants.DOT + AppConstants.COLUMN_MAPPING).split(AppConstants.SYM_PIPE_REGX);
		
		Map<String, String> viewDispColMap = new HashMap<String, String>();
		for(String cm : colMaping) {
			if (cm != null) {
				if (cm.length() > 2)
					viewDispColMap.put(cm.split(AppConstants.COLON)[2], cm.split(AppConstants.COLON)[0]);
			}
		}
		if (input.get(AppConstants.HIDDEN_COLUMNS) != null) {
			String[] hiddenColumns = input.get(AppConstants.HIDDEN_COLUMNS).toString().split(AppConstants.SYM_PIPE_REGX);
			for (String s : hiddenColumns) {
				if (s != null && s.length() > 0 && viewDispColMap.get(s) != null) {
					sbHiddenCols.append(viewDispColMap.get(s));
					sbHiddenCols.append("|");
				}
			}
		}
		if (input.get(AppConstants.DISPLAY_COLUMNS) != null) {
			String[] displayColumns = input.get(AppConstants.DISPLAY_COLUMNS).toString().split(AppConstants.SYM_PIPE_REGX);
			for (String s : displayColumns) {
				if (s != null && s.length() > 0 && viewDispColMap.get(s) != null) {
					sbDisplayCols.append(viewDispColMap.get(s));
					sbDisplayCols.append("|");
				}
			}
		}
		input.put(AppConstants.HIDDEN_COLUMNS, sbHiddenCols.toString());
		input.put(AppConstants.DISPLAY_COLUMNS, sbDisplayCols.toString());
		viewDataDao.updateViewColumns(input);
		
		return null;
	}

	@Override
	public Map<String,Object> getDataQualityReport() {
		// TODO Auto-generated method stub
		Map<String,Object> arrObject = new HashMap<String,Object>();
		
		Thread t1 = new Thread() {
			public void run() {
				List<DataQualityReportBean> arrDataQualityBean  = new ArrayList<DataQualityReportBean>();
				try {
					arrDataQualityBean = viewDataDao.getDataQualityReport();
				} catch (Exception e) {
					logger.error("Exception in  viewDataDao.getDataQualityReport() -", e);
				}
				if(arrDataQualityBean!=null && !arrDataQualityBean.isEmpty())
					arrObject.put(AppConstants.BUSINESSEXCEPTION, arrDataQualityBean);
			};
		};
		
		
		Thread t2 = new Thread() {
			public void run() {
				Map<String,Integer> arrUnmappedData =  new HashMap<String,Integer>();
				try {
					arrUnmappedData = viewDataDao.getUnmappedData();
				} catch (Exception e) {
					logger.error("Exception in viewDataDao.getUnmappedData() -", e);
				}
				if(arrUnmappedData!=null && !arrUnmappedData.isEmpty())
					arrObject.put(AppConstants.MAPPINGEXCEPTION, arrUnmappedData);
			};
		};

	
		Thread t3 = new Thread() {
			public void run() {
				List<DataQualityReportBean> arrTechnicalDq =  new ArrayList<DataQualityReportBean>();
				try {
					arrTechnicalDq = viewDataDao.getTechnicalDQreport();
				} catch (Exception e) {
					logger.error("Exception in viewDataDao.getTechnicalDQreport() -", e);
				}
				if(arrTechnicalDq!=null && !arrTechnicalDq.isEmpty())
					arrObject.put(AppConstants.TECHNICALDQ, arrTechnicalDq);
			}
		};
		
		t1.start();
		t2.start();
		t3.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			logger.error("Exception in thread join getDataQualityReport -", e);
		}
			
		return arrObject;
	}
	
	
	@Override
	public String getHierarchyJsonString(String type) {
		String jsonStr = "";
		TestTree test = new TestTree();
		List<Object[]> listObjs = getHierarchiesSQL(type);
		jsonStr = test.getData(listObjs, type);
		return jsonStr;
	}
	
		

	@Override
	public List<String> getCustomerGroups() throws Exception {
		// TODO Auto-generated method stub
		List<String> OpsoCustomerGroup =  new ArrayList<String>();
		try{
			OpsoCustomerGroup = viewDataDao.getCustomerGroups();
		}catch(Exception e){
			
		}
		return OpsoCustomerGroup;
	}
	
	@Override
	public Map<String, String> getExtSourceList(Map<String, Object> input) throws Exception {
		return viewDataDao.getExtSourceList(input);
	}

	@Override
	public Map<String, Object> promotionPeriodAlignmentData(Map<String, String> input) {
		// TODO Auto-generated method stub
		
		Map<String, Object> outData = new HashMap<String, Object>();
		try{
			outData = viewDataDao.promotionPeriodAlignmentData(input);
			//outData = viewDataDao.promotionPeriodAlignmentcusNprodData();
			
		}catch(Exception e){
			logger.error("promotionPeriodAlignmentData- " + e.getMessage());
		}	
		
		return outData;
	}

	@Override
	public Map<String, Object> promotionPeriodAlignmentcusNprodData(Map<String, Object> hmInput) {
		// TODO Auto-generated method stub
		Map<String,Object> outData = new HashMap<String,Object>();
		try{
			outData = viewDataDao.promotionPeriodAlignmentcusNprodData(hmInput);
		}catch(Exception e){
			logger.error("promotionPeriodAlignmentcusNprodData- " + e.getMessage());
		}
		return outData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> saveEditData(Map<String, Object> hmInput) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<String, Object>();
		hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
		hmOutput.put(AppConstants.MESSAGE, "Data saved successfully");
		
		try {
			JsonObject jo = (JsonObject) new JsonParser().parse(hmInput.get(AppConstants.EDITDATA).toString());
			String tabmenu = hmInput.get(AppConstants.TABMENU).toString();
			
			Map<String, Object> hmColMap = getDisplayColumns(hmInput);
			Map<String, Map<String, String>> hmColDetails = new HashMap<String, Map<String,String>>();
			
			if (hmColMap != null) {
				hmColDetails = (Map<String, Map<String, String>>) hmColMap.get(AppConstants.COLUMN_MAPING);
			}
			
			String sKeyIdx = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
			String editTable = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.TABLE);
			String sUpdateSQL = "Update " + editTable + " set ";
			String sWhereSQL = " where ";
			StringBuffer sfUpdateSql = new StringBuffer();
			for (Map.Entry<String, JsonElement> row : jo.entrySet()) {
				if (PEAUtils.isEmpty(row.getKey())) {
					continue;
				}
				
				sfUpdateSql.append(sUpdateSQL);
				
				for (Map.Entry<String, JsonElement> col : row.getValue().getAsJsonObject().entrySet()) {
					
					if (col.getValue() == null) {
						continue;
					}
					
					if (!sfUpdateSql.toString().endsWith(sUpdateSQL)) {
						sfUpdateSql.append(" , ");
					}
					
					String val = PEAUtils.convertToDatatype(col.getValue().getAsString(), hmColDetails.get(col.getKey()).get(AppConstants.COLTYPE)).toString();
					
					sfUpdateSql.append(hmColDetails.get(col.getKey()).get(AppConstants.DBCOLUMN));
					sfUpdateSql.append(" = '" + PEAUtils.escapeSql(val) + "'");
				}
	
				sfUpdateSql.append(sWhereSQL);
				
				if (sKeyIdx != null) {
					String[] saKeyIdx = sKeyIdx.split(AppConstants.SYM_PIPE_REGX);
					for (int x = 0; x < saKeyIdx.length; x++) {
						String dbColName = hmColDetails.get(AppConstants.COL + saKeyIdx[x]).get(AppConstants.DBCOLUMN);
						String dbColVal = row.getKey().split(sEditRowKeyDelim)[x];
						
						if (!sfUpdateSql.toString().endsWith(sWhereSQL)) {
							sfUpdateSql.append(" and ");
						}
						
						sfUpdateSql.append(dbColName + " = '" + PEAUtils.escapeSql(dbColVal) + "'");
					}
				}
				sfUpdateSql.append(";");
				
				//System.out.println(sfUpdateSql);
			}
			viewDataDao.executeQuery(sfUpdateSql.toString());
		}
		catch (Exception e) {
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			logger.debug("Exception in saveEditData - ", e);
		}
		
		return hmOutput;
	}
	
	@Override
	public Map<String, Object> updateProcessPromotionList(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = null;
		if (hmInput.get(AppConstants.PROMO_ID) != null) {
			String promoIds = hmInput.get(AppConstants.PROMO_ID).toString();
			Map<String, Object> hmPPLInput = new HashMap<String, Object>(hmInput);
			hmPPLInput.remove(AppConstants.PROMO_ID);
			for (String pid : promoIds.split(",")) {
				hmPPLInput.put(AppConstants.PROMO_ID, pid);
				hmOutput = viewDataDao.updateProcessPromotionList(hmPPLInput);
			}
		}
		if (hmOutput == null) {
			hmOutput = new HashMap<String, Object>();
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, "Something went wrong, please try later!");
		}
		return hmOutput;
	}

	@Override
	public Map<String, Object> saveOverrideChanges(Map<String, Object> hmInput) {
		// TODO Auto-generated method stub
		Map<String, Object> hmOutput = new HashMap<>();
		try {
			hmOutput = viewDataDao.saveOverrideChanges(hmInput);
		} catch (Exception e) {
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in saveOverrideChanges", e);
		}
		return hmOutput;
	}

	@Override
	public Map<String, Set<String>> getDataQualityStatusMeta() {
		// TODO Auto-generated method stub
		Map<String, Set<String>> hmOutput = null;
		try{
			hmOutput = viewDataDao.getDataQualityStatusMeta();
		}catch(Exception e){
			e.printStackTrace();
		}
		return hmOutput;
	}

	@Override
	public Map<String, Object> saveDataQualityStatus(Map<String, Object> hmInput) {
		Map<String, Object> hmOutput = null;
		hmOutput = viewDataDao.saveDataQualityStatus(hmInput);
//		String sUpdate = "UPDTAE AUX.DQ_REJECTS SET STATUS = :status WHERE REJECTID IN (:RejectIds)";
//		try {
//			JsonObject jo = (JsonObject) hmInput.get(AppConstants.EDITED_STATUS);
//			for (Map.Entry<String, JsonElement> en : jo.entrySet()) {
//				String status = en.getKey();
//				List<String> alRejectId = new ArrayList<String>(); 
//				for (JsonElement je : en.getValue().getAsJsonArray()) {
//					alRejectId.add(je.getAsString());
//				}
//				
//			}
//			
//		} catch(Exception e){
//			e.printStackTrace();
//		}
		return hmOutput;
	}
	
}
