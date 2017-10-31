package com.saama.workbench.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.saama.workbench.dao.DataTableDao;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Service
@Transactional
public class DataTableService implements IDataTableService {

	private static final Logger logger = Logger.getLogger(DataTableService.class);
	
	@Autowired
	DataTableDao dataTableDao;
	
	private static String sRowKeyDelim = "&%";
	
	private static Map<String, Map<String, String>> getColMappings(String tableName) {
		Map<String, Map<String, String>> hmColMappings = new HashMap<String, Map<String, String>>();
		Map<String, String> hmColType = new HashMap<String, String>();
		Map<String, String> hmColDBName = new HashMap<String, String>();
		String[] colMaping = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING).split("\\|");
		
		for(String cm : colMaping) {
			if (cm != null) {
				if (cm.length() > 1)
					hmColDBName.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[1]);
				if (cm.length() > 3)
					hmColType.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[3]);
			}
		}
		
		hmColMappings.put(AppConstants.COLTYPE, hmColType);
		hmColMappings.put(AppConstants.COLDBNAME, hmColDBName);
		
		return hmColMappings;
	}
	
	private static List getColSearchable(String tableName) {
		List<String> list = new ArrayList<String>();
		String[] colMaping = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING).split("\\|");
		
		for(String cm : colMaping) {
			if (cm != null) {
				if (cm.length() > 4 && PEAUtils.convertToBoolean(cm.split(AppConstants.COLON)[4]))
					list.add(cm.split(AppConstants.COLON)[1]);
			}
		}
		return list;
	}
	
	private static Map getColIdxMap(String tableName) {
		String[] colMaping = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING).split("\\|");
		Map<String, String> colMap = new HashMap<String, String>();
		
		for(String cm : colMaping) {
			if (cm != null) {
				if (cm.length() > 1)
					colMap.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[1]);
			}
		}
		
		return colMap;
	}
	
//	private static Map getEditColType(String tableName) {
//		Map<String, String> hmColEditType = new HashMap<String, String>();
//		if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_EDITABLE) != null) {
//			String[] colEditable = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_EDITABLE).split("\\|");
//			
//			for(String ce : colEditable) {
//				if (ce != null) {
//					if (ce.length() > 1)
//						hmColEditType.put(ce.split(AppConstants.COLON)[0], ce.split(AppConstants.COLON)[1]);
//				}
//			}
//		}
//		return hmColEditType;
//	}
	
	@Override
	public List getData(Map<String, Object> inputParams) throws Exception {
		List<String> columns = getColumnDisplayNames(inputParams);
		if (inputParams.get(AppConstants.SORT_COL_0) != null) {
			Map<String, String> hmDBDisplayColumns = getColumnDisplayIdxMap(inputParams);
			String colIdx = hmDBDisplayColumns.get(columns.get(Integer.parseInt(inputParams.get(AppConstants.SORT_COL_0).toString())));
			inputParams.put(AppConstants.SORT_COL_0, colIdx.replace(AppConstants.COL, ""));
		}
		else {
			inputParams.put(AppConstants.SORT_COL_0, "0");
		}
		
		List<Object[]> dataList = dataTableDao.getData(inputParams);
		if (dataList == null) throw new Exception("Exception in getData - getdata is null");
		return dataList;
	}

	@Override
	public JsonObject getProcessedData(Map<String, Object> inputParams) throws Exception {
		List<Object[]> dataList = (List<Object[]>) inputParams.get(AppConstants.DATA);
		JsonObject jObj = new JsonObject();
		JsonArray jaData = new JsonArray();
		
		if (dataList == null || dataList.size() < 1) {
			jObj.addProperty("totalCount", 0);
			jObj.add("data", jaData);
			return jObj;
			//throw new Exception("Exception in getProcessedData - Data list is null");
		}
		if (inputParams.get(AppConstants.TABLENAME) == null && inputParams.get(AppConstants.TABLENAME).toString().length() < 1) {
			logger.error("Provided table name is null or empty.");
			throw new Exception("Exception in getProcessedData - Provided table name is null or empty.");
		}
		
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		
		try {
			Map<String, Map<String, String>> colMappings = getColMappings(tableName);
			Map<String, String> hmColType = colMappings.get(AppConstants.COLTYPE);
			Map<String, String> hmColDBName = colMappings.get(AppConstants.COLDBNAME);
			Map<String, Map<String, Object>> colEditType = PEAUtils.getEditColType(tableName);
			
			boolean isSessionCacheON = AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SESSION_CACHE));
			
			if (isSessionCacheON) {
				// Cache Searching
				boolean isSearchON = inputParams.get(AppConstants.SEARCH_PARAM) != null && inputParams.get(AppConstants.SEARCH_PARAM).toString().length() > 0;
		
				List<Object[]> lSearchedDataList = new ArrayList<Object[]>();
				for (Object[] o : dataList) {
					if (isSearchON) {
						if (StringUtils.join(o, "!|!").contains(inputParams.get(AppConstants.SEARCH_PARAM).toString())) {
							lSearchedDataList.add(o);
						}
					} else {
						lSearchedDataList.add(o);
					}
				}
				
				String defaultDataType = new String();
				
				if (hmColType.get(AppConstants.COL + inputParams.get(AppConstants.SORT_COL_0)) != null) {
					switch(hmColType.get(AppConstants.COL + inputParams.get(AppConstants.SORT_COL_0).toString().toUpperCase())) {
						case AppConstants.N:
							defaultDataType = AppConstants.INTEGER;
							break;
						case AppConstants.D:
							defaultDataType = AppConstants.DATE;
							break;
					}
				}
				
				// Cache Sorting
				int iSortIdx = 0;
				int iSortDir = 1;
				if (inputParams.get(AppConstants.SORT_COL_0) != null) {
					iSortIdx = Integer.parseInt(inputParams.get(AppConstants.SORT_COL_0).toString());
				}
				
				if (inputParams.get(AppConstants.SORT_COL_0) != null) {
					iSortDir = AppConstants.ASC.equalsIgnoreCase(inputParams.get(AppConstants.SORT_DIR_0).toString()) ? 1 : -1;
				}
				
				if (colEditType.size() > 0 && iSortIdx > 0) {
					iSortIdx -= 1;
				}
				
				final int iSortIdx1 = iSortIdx;
				final int iSortDir1 = iSortDir;
				final String defaultDataType1 = defaultDataType; 
				final SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat(AppConstants.YMDHMS);
				Collections.sort(lSearchedDataList, new Comparator<Object[]>() {
					
					@Override
		            public int compare(Object[] o1, Object[] o2) {
						try {
							Object a = o1[iSortIdx1]; Object b = o2[iSortIdx1];
							if (AppConstants.INTEGER.equalsIgnoreCase(defaultDataType1)) {
								if (a != null)
									a = Integer.parseInt(a.toString());
								if (b != null)
									b = Integer.parseInt(b.toString());
							}
							else if (AppConstants.DATE.equalsIgnoreCase(defaultDataType1)) {
								if (a != null)
									a = SDF_YMDHMS.parse(a.toString());
								if (b != null)
									b = SDF_YMDHMS.parse(b.toString());
							}
							return PEAUtils.compareObjects(a, b, iSortDir1);
						} catch (Exception e) {
							logger.warn("Some error in datatable sorting");
						}
						return 0;
		            }
				});
				
				// Cache Pagination
				jObj.add("totalCount", new JsonPrimitive(lSearchedDataList.size()));
		
				int pgStIdx = Integer.parseInt(inputParams.get(AppConstants.DISPLAY_START).toString());
				int pgLen = Integer.parseInt(inputParams.get(AppConstants.DISPLAY_LENGTH).toString());
				int pgEndIdx = (pgStIdx + pgLen) > lSearchedDataList.size() ? lSearchedDataList.size() : (pgStIdx + pgLen);
		
				dataList = lSearchedDataList.subList(pgStIdx, pgEndIdx);
				
			} else {
				jObj.add("totalCount", new JsonPrimitive(Integer.parseInt(dataList.get(0)[dataList.get(0).length - 1].toString())));
			}
	
			for (Object[] o : dataList) {
				JsonArray row = new JsonArray();
				JsonObject rowObject = new JsonObject();
				
				String sKeyIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
				String sRowKey = new String();
				String sRowId = new String();
//				String sRowKeyDelim = ":|";
				
				if (sKeyIdx != null) {
					for (String sIdx : sKeyIdx.split("\\|")) {
						if (o[Integer.parseInt(sIdx)] != null) {
							sRowKey += o[Integer.parseInt(sIdx)].toString() + sRowKeyDelim;
						}
					}
					if (sRowKey.contains(sRowKeyDelim)) {
						sRowKey = sRowKey.substring(0, sRowKey.length() - sRowKeyDelim.length());
					}
				}
//				if (sRowKey != null && sRowKey.length() < 1) {
					sRowId = dataList.indexOf(o) + ""; 
//				}
				StringBuffer actions = new StringBuffer("<div style=\"width:150px\">");
				
				String btnIdSeperator = "|=";
				
				if (colEditType.size() > 0) {
					actions = actions.append("<button id=\""+ tableName + btnIdSeperator + "savebtn" + btnIdSeperator + sRowId + btnIdSeperator + sRowKey + "\" class=\"btn btn-primary btn-simple btn-xs\"  data-placement=\"left\" data-toggle=\"tooltip\" tooltip-title=\"Save\" data-title=\"Save\"><i class=\"fa  fa-save\"></i></button>");
				}
				
				if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.DELETABLE) != null && AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.DELETABLE))) {
					actions = actions.append("<button style=\"margin-left:5px\" id=\""+ tableName + btnIdSeperator + "deletebtn" + btnIdSeperator + sRowId + btnIdSeperator + sRowKey + "\" class=\"btn btn-danger btn-simple btn-xs\" data-toggle=\"tooltip\" tooltip-title=\"Delete\" data-placement=\"right\" data-title=\"Delete\"><i class=\"fa  fa-times\"></i></button>");
				}
				
				actions.append("</div>");
				
				if (actions != null && (actions.toString().contains("savebtn") || actions.toString().contains("deletebtn"))) {
					row.add(new JsonPrimitive(actions.toString()));
					rowObject.add("actions", new JsonPrimitive(actions.toString()));
				}
				for(int i=0; i<o.length; i++) {
					String sCol = AppConstants.COL + i;
					
					if (hmColDBName.get(sCol) == null) {
						continue;
					}
					
					String sColHidIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_HIDDEN); 
					if (sColHidIdx != null && sColHidIdx.split("\\|").length > 0) {
						if (Arrays.asList(sColHidIdx.split("\\|")).contains(i + "")) {
							continue;
						}
					}
					String val = new String();
					if (o[i] != null) {
						val = o[i].toString();
					}
					SimpleDateFormat SDF = new SimpleDateFormat(AppConstants.YMD); // AppConstants.SDF_YMD;
					SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat(AppConstants.YMDHMS);
					
					if (AppConstants.D.equalsIgnoreCase(hmColType.get(sCol))) {
						if (PEAUtils.getDateFormat(tableName) != null) {
							SDF = new SimpleDateFormat(PEAUtils.getDateFormat(tableName));
						}
						try {
							if (!PEAUtils.isEmpty(val)) {
								val = SDF.format(SDF_YMDHMS.parse(val));
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
					
					if (colEditType.get(sCol) != null) {
						switch (colEditType.get(sCol).get(AppConstants.EDIT_COL_TYPE).toString()) {
							case AppConstants.T: 
								val = "<input style=\"width:95%\" type=\"text\" class=\"" + tableName + "-ef-" + sRowId + "-text-" + sCol + "\" value=\""+ val +"\"></input>";
								break;
							case AppConstants.S: 
								val = "<select style=\"width:95%\" class=\"" + tableName + "-ef-" + sRowId + "-select-" + sCol + "\"><option>" + val + "</option></input>";
								break;
						}
					}
					
					if (val != null) {
						if (val.contains(AppConstants.SYM_POUND_SIGN)) {
							val = val.replaceAll(AppConstants.SYM_POUND_SIGN, AppConstants.SYM_POUND);
						}
					}
					row.add(new JsonPrimitive(val));
					rowObject.add(hmColDBName.get(sCol), new JsonPrimitive(val));
				}
				if (PEAUtils.convertToBoolean(inputParams.get(AppConstants.ISNGREQUEST))) {
					jaData.add(rowObject);
				}
				else {
					jaData.add(row);
				}
			}
	
			jObj.add(AppConstants.DATA, jaData);
			//return jObj;
		} catch (Exception e) {
			logger.error("Some error in datatable getProcessedData" + e.getMessage());
			throw e;
		}
		return jObj;
	}

	@Override
	public List<String> getColumnDisplayNames(Map<String, Object> inputParams) throws Exception {
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		
		try {
			String sColumnMappings = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING);
			List<String> lDisplayColumns = new ArrayList<String>();
			Map hmEditColType = PEAUtils.getEditColType(tableName);
			boolean deletable = PEAUtils.convertToBoolean(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.DELETABLE));
			
			if (AppConstants.FIRST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
				if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable )) {
					lDisplayColumns.add("Actions");
				}
			}
			for (int i=0; i < sColumnMappings.split("\\|").length; i++) {
				String s = sColumnMappings.split("\\|")[i];
				if (s.split(AppConstants.COLON).length > 2) {
					String sColHidIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_HIDDEN);
					
					if (sColHidIdx != null && sColHidIdx.split("\\|").length > 0 && Arrays.asList(sColHidIdx.split("\\|")).contains(i+"")) {
						
					} else {
						lDisplayColumns.add(s.split(AppConstants.COLON)[2]);
					}
				}
			}
			if (AppConstants.LAST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
				if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable )) {
					lDisplayColumns.add("Actions");
				}
			}
			return lDisplayColumns;
		} catch (Exception e) {
			logger.error("Some error in datatable getColumnDisplayNames" + e.getMessage());
			throw e;
		}
	}
	
	@Override
	public List<String> getColumnDBNames(Map<String, Object> inputParams) throws Exception {
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		
		try {
			String sColumnMappings = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING);
			List<String> lDBColumns = new ArrayList<String>();
			Map hmEditColType = PEAUtils.getEditColType(tableName);
			boolean deletable = PEAUtils.convertToBoolean(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.DELETABLE));
			
			if (AppConstants.FIRST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
				if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable)) {
					lDBColumns.add("Actions");
				}
			}
			for (int i=0; i < sColumnMappings.split("\\|").length; i++) {
				String s = sColumnMappings.split("\\|")[i];
	//		for (String s : sColumnMappings.split("\\|")) {
				if (s.split(AppConstants.COLON).length > 1) {
					String sColHidIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_HIDDEN);
					
					if (sColHidIdx != null && sColHidIdx.split("\\|").length > 0 && Arrays.asList(sColHidIdx.split("\\|")).contains(i+"")) {
						
					} else {
						lDBColumns.add(s.split(AppConstants.COLON)[1]);
					}
				}
			}
			if (AppConstants.LAST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
				if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable)) {
					lDBColumns.add("Actions");
				}
			}
			return lDBColumns;
		} catch (Exception e) {
			logger.error("Some error in datatable getColumnDBNames" + e.getMessage());
			throw e;
		}
		
	}
	
	public Map<String, Object> savePromoMechanic(Map<String, Object> inputParam) {
		String tabmenu = inputParam.get(AppConstants.TABLENAME).toString();
		
		Map<String, Object> out = new HashMap<String, Object>();
		out.put(AppConstants.SUCCESS, true);
		out.put(AppConstants.MESSAGE, "Rows are updated successfully");
		
		try {
			if (inputParam.get(AppConstants.EDITDATA) != null && !PEAUtils.isEmpty(inputParam.get(AppConstants.EDITDATA).toString().trim())) {
				
				Map<String, String> colIdxMap = getColIdxMap(tabmenu);
				
				String inputData = inputParam.get(AppConstants.EDITDATA).toString();
				String sKeyIdx = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
				
				JsonObject editData = (JsonObject) new JsonParser().parse(inputData);
				
				StringBuffer sbSqlBatch = new StringBuffer();
				String sql = "select " + colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[0]) + " from " + PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.TABLE) + " where ";
				String keyCol = "", keyColVal = "", alKeyCol = "", alkeyColVal = "", val = "";
				
				for (Map.Entry<String, JsonElement> obj : editData.entrySet()) {
					String key = obj.getKey(); val = "";
					if (obj.getValue() != null && obj.getValue().getAsJsonObject().get(AppConstants.COL + "8") != null)
						val = obj.getValue().getAsJsonObject().get(AppConstants.COL + "8").getAsString();
					
//					if (PEAUtils.isEmpty(val)) {
//						continue;
//					}
					
					keyCol = ""; keyColVal = ""; alKeyCol = ""; alkeyColVal = "";
					sql = "select " + colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[0]) + " from " + PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.TABLE) + " where ";
					
					for (int i = 0; i < sKeyIdx.split("\\|").length; i++) {
						keyCol = colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[i]);
						keyColVal = key.split(sRowKeyDelim)[i];
						
						alKeyCol += keyCol + ",";
						alkeyColVal += "'" + keyColVal + "'" + ",";
						
						sql += keyCol + "=" + "'" + keyColVal + "'";
						if (i < sKeyIdx.split("\\|").length - 1) {
							sql += " and ";
						}
					}
					
					if (!PEAUtils.isEmpty(val) && !dataTableDao.checkIfRowExist(sql)) {
						sql = "insert into " + PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.TABLE) + "(" + alKeyCol + "PromoMechanic,CreatedDate,CreatedBy,UpdatedDate,UpdatedBy) "
								+ "values (" + alkeyColVal + "'" + val + "', '" + PEAUtils.getUpdateStringDate() + "', '" + inputParam.get(AppConstants.USERNAME).toString() + "', '" + PEAUtils.getUpdateStringDate() + "', '" + inputParam.get(AppConstants.USERNAME).toString() + "')";
						logger.info("insert SQL for " + tabmenu + " - " + sql);
						
					}
					else {
						StringBuffer sbSql = new StringBuffer();
						if (PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.TABLE) != null) {
							sbSql = new StringBuffer("update " + PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.TABLE));
						}
						else {
							logger.error("Provided table is null or empty.");
							throw new Exception("Exception in updateTable - Provided table name is null or empty.");
	//						out.put(AppConstants.RESULT, AppConstants.FAILURE);
	//						out.put(AppConstants.MESSAGE, "Provided table is null or empty.");
	//						return out;
						}
						
						sbSql = sbSql.append(" SET ");
						if (sbSql.toString().endsWith(" SET ")) {
							sbSql.append(colIdxMap.get(AppConstants.COL + "8") + " = ").append("'" + val + "'");
						} else {
							sbSql.append(", " + colIdxMap.get(AppConstants.COL + "8") + " = ").append("'" + val + "'");
						}
						
						if (inputParam.get(AppConstants.AUDIT_COLUMN) != null) {
							Map<String, Map<String, String>> auditCols = (Map<String, Map<String, String>>) inputParam.get(AppConstants.AUDIT_COLUMN);
							for (Map.Entry<String, Map<String, String>> en  : auditCols.entrySet()) {
								String dbCol = colIdxMap.get(AppConstants.COL + en.getKey());
								String auditType = en.getValue().get(AppConstants.COLTYPE);
								String val2 = "";
								switch(auditType) {
									case "U":
										val2 = inputParam.get(AppConstants.USERNAME).toString();
										break;
									case "D":
										val2 = PEAUtils.getUpdateStringDate();
										break;
								}
								sbSql.append(", " + dbCol + " = ").append("'" + val2 + "'");
							}
						}
						
				//		sRowKeyDelim
//						sKeyIdx = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
						if (!PEAUtils.isEmpty(key)) {
							if (sKeyIdx != null) {
								keyCol = new String();
								keyColVal = new String();
								
								sbSql.append(" where ");
								
								for (int i = 0; i < sKeyIdx.split("\\|").length; i++) {
									keyCol = colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[i]);
									keyColVal = key.split(sRowKeyDelim)[i];
									
									if (sbSql.toString().endsWith(" where ")) {
										sbSql.append(keyCol + " = '" + keyColVal + "'");
									} else {
										sbSql.append(" and " + keyCol + " = '" + keyColVal + "'"); //.append("and "+keyCol+ "='"+keyColVal + "'");
									}
								}
								sbSql.append(" and " + colIdxMap.get(AppConstants.COL + "8") + " != '" + val + "'");
							}
						}
						sql = sbSql.toString();
						logger.info("Update SQL for " + tabmenu + " - " + sql);
					}
					sbSqlBatch.append(";" + sql);
				}
				if (sbSqlBatch != null && !PEAUtils.isEmpty(sbSqlBatch.toString())) {
					dataTableDao.executeQuery(sbSqlBatch.substring(0));
				}
			} 
		}
		catch(Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in savePromoMechanic", e);
		}
		
		return out;
	}
	
	@Override
	public Map updateTable(Map<String, Object> inputParams) throws Exception {
		
		Map<String, String> out = new HashMap<String, String>();
		if (inputParams.get(AppConstants.TABLENAME) == null || inputParams.get(AppConstants.TABLENAME).toString().trim().length() < 1) {
			logger.error("Provided table name is null or empty.");
			throw new Exception("Exception in updateTable - Provided table name is null or empty.");
//			out.put(AppConstants.RESULT, "Failure");
//			out.put(AppConstants.MESSAGE, "Provided table name is null or empty.");
//			return out;
		}
		
		
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		StringBuffer updateSql = new StringBuffer();
		try {
			Map<String, String> colIdxMap = getColIdxMap(tableName);
			
			
			if (AppConstants.PromoMechanic.equalsIgnoreCase(tableName)) {
				String sKeyIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
				if (sKeyIdx != null && inputParams.get(AppConstants.KEY) != null) {
					String keyCol = new String();
					String keyColVal = new String();
				
					String sql = "select " + colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[0]) + " from " + PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE) + " where ";
					String alKeyCol = "";
					String alkeyColVal = "";
					for (int i = 0; i < sKeyIdx.split("\\|").length; i++) {
						keyCol = colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[i]);
						keyColVal = inputParams.get(AppConstants.KEY).toString().split(sRowKeyDelim)[i];
						
						alKeyCol += keyCol + ",";
						alkeyColVal += "'" + PEAUtils.escapeSql(keyColVal) + "'" + ",";
						
						sql += keyCol + "=" + "'" + keyColVal + "'";
						if (i < sKeyIdx.split("\\|").length - 1) {
							sql += " and ";
						}
					}
					if (!dataTableDao.checkIfRowExist(sql)) {
						sql = "insert into " + PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE) + "(" + alKeyCol + "PromoMechanic,CreatedDate,CreatedBy,UpdatedDate,UpdatedBy) "
								+ "values (" + alkeyColVal + "'" + PEAUtils.escapeSql(PEAUtils.getDefaultIfNull(inputParams.get(AppConstants.COL + "8"), AppConstants.BLANK).toString()) + "', '" + PEAUtils.getUpdateStringDate() + "', '" + inputParams.get(AppConstants.USERNAME).toString() + "', '" + PEAUtils.getUpdateStringDate() + "', '" + inputParams.get(AppConstants.USERNAME).toString() + "')";
						
						logger.info("Update SQL for " + tableName + " - " + sql);
						dataTableDao.executeQuery(sql);
						
						out.put(AppConstants.RESULT, AppConstants.SUCCESS);
						out.put(AppConstants.MESSAGE, "Row updated successfully");
						
						return out;
					}
				}
			}
			
			if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE) != null) {
				updateSql = new StringBuffer("update " + PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE));
			}
			else {
				logger.error("Provided table is null or empty.");
				throw new Exception("Exception in updateTable - Provided table name is null or empty.");
//				out.put(AppConstants.RESULT, AppConstants.FAILURE);
//				out.put(AppConstants.MESSAGE, "Provided table is null or empty.");
//				return out;
			}
			
			updateSql = updateSql.append(" SET ");
			for (Map.Entry<String, Object> entry : inputParams.entrySet()) {
				if (entry.getKey() != null && PEAUtils.isNumeric(entry.getKey().replace(AppConstants.COL, ""))) {
					if (updateSql.toString().endsWith(" SET ")) {
						updateSql.append(colIdxMap.get(entry.getKey()) + " = ").append("'" + PEAUtils.escapeSql(entry.getValue().toString()) + "'");
					} else {
						updateSql.append(", " + colIdxMap.get(entry.getKey()) + " = ").append("'" + PEAUtils.escapeSql(entry.getValue().toString()) + "'");
					}
				}
			}
			
			if (inputParams.get(AppConstants.AUDIT_COLUMN) != null) {
				Map<String, Map<String, String>> auditCols = (Map<String, Map<String, String>>) inputParams.get(AppConstants.AUDIT_COLUMN);
				for (Map.Entry<String, Map<String, String>> en  : auditCols.entrySet()) {
					String dbCol = colIdxMap.get(AppConstants.COL + en.getKey());
					String auditType = en.getValue().get(AppConstants.COLTYPE);
					String val = "";
					switch(auditType) {
						case "U":
							val = inputParams.get(AppConstants.USERNAME).toString();
							break;
						case "D":
							val = PEAUtils.getUpdateStringDate();
							break;
					}
					updateSql.append(", " + dbCol + " = ").append("'" + PEAUtils.escapeSql(val) + "'");
				}
			}
			
	//		sRowKeyDelim
			if (inputParams.get(AppConstants.KEY) != null && inputParams.get(AppConstants.KEY).toString().split(sRowKeyDelim).length > 0) {
				
				String sKeyIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
				if (sKeyIdx != null) {
					String keyCol = new String();
					String keyColVal = new String();
					
					updateSql.append(" where ");
					
					for (int i = 0; i < sKeyIdx.split("\\|").length; i++) {
						keyCol = colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[i]);
						keyColVal = inputParams.get(AppConstants.KEY).toString().split(sRowKeyDelim)[i];
						
						if (updateSql.toString().endsWith(" where ")) {
							updateSql.append(keyCol + " = '" + PEAUtils.escapeSql(keyColVal) + "'");
						} else {
							updateSql.append(" and " + keyCol + " = '" + PEAUtils.escapeSql(keyColVal) + "'").append("and "+keyCol+ "='"+PEAUtils.escapeSql(keyColVal) + "'");
						}
					}
				}
			}
			
			logger.info("Update SQL for " + tableName + " - " + updateSql.toString());
			dataTableDao.executeQuery(updateSql.toString());
			
			out.put(AppConstants.RESULT, AppConstants.SUCCESS);
			out.put(AppConstants.MESSAGE, "Row updated successfully");
			
		} catch(Exception e) {
			logger.error("Exception in UPDATE TABLE " + tableName + " - " + e.getMessage());
			throw e;
//			out.put(AppConstants.RESULT, AppConstants.FAILURE);
//			out.put(AppConstants.MESSAGE, e.getMessage());
		}
		return out;
	}
	
	@Override
	public Map deleteTableRow(Map<String, Object> inputParams) throws Exception {
		
		Map<String, String> out = new HashMap<String, String>();
		if (inputParams.get(AppConstants.TABLENAME) == null || inputParams.get(AppConstants.TABLENAME).toString().trim().length() < 1) {
			logger.error("Provided table name is null or empty.");
			throw new Exception("Exception in deleteTableRow - Provided table name is null or empty.");
//			out.put(AppConstants.RESULT, AppConstants.FAILURE);
//			out.put(AppConstants.MESSAGE, "Provided table name is null or empty.");
//			return out;
		}
		
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		StringBuffer deleteSQL = new StringBuffer();
		
		try {
		
			Map<String, String> colIdxMap = getColIdxMap(tableName);
			
			if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE) != null) {
				deleteSQL = new StringBuffer("delete from " + PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE) + " where ");
			}
			else {
				logger.error("Provided table is null or empty.");
				throw new Exception("Exception in deleteTableRow - Provided table name is null or empty.");
//				out.put(AppConstants.RESULT, AppConstants.FAILURE);
//				out.put(AppConstants.MESSAGE, "Provided table is null or empty.");
//				return out;
			}
			
			if (inputParams.get(AppConstants.KEY) != null && inputParams.get(AppConstants.KEY).toString().split(sRowKeyDelim).length > 0) {
				
				String sKeyIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
				if (sKeyIdx != null) {
					String keyCol = new String();
					String keyColVal = new String();
					
	//				deleteSQL.append(" where ");
					
					for (int i = 0; i < sKeyIdx.split("\\|").length; i++) {
						keyCol = colIdxMap.get(AppConstants.COL + sKeyIdx.split("\\|")[i]);
						keyColVal = inputParams.get(AppConstants.KEY).toString().split(sRowKeyDelim)[i];
						
						if (deleteSQL.toString().endsWith(" where ")) {
							deleteSQL.append(keyCol + " = '" + keyColVal + "'");
						} else {
							deleteSQL.append(" and " + keyCol + " = '" + keyColVal + "'");
						}
					}
				}
			}
			
			logger.info("Update SQL for " + tableName + " - " + deleteSQL.toString());
			dataTableDao.executeQuery(deleteSQL.toString());
			
			out.put(AppConstants.RESULT, AppConstants.SUCCESS);
			out.put(AppConstants.MESSAGE, "Row deleted successfully");
			
		} catch (Exception e) {
			logger.error("Exception in Delete TABLE " + tableName + " - " + e.getMessage());
			throw e;
//			out.put(AppConstants.RESULT, AppConstants.FAILURE);
//			out.put(AppConstants.MESSAGE, e.getMessage());
		}
		
		return out;
	}
	
	@Override
	public Map importIntoTable(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> out = new HashMap<String, Object>();
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		
		try {
			inputParams.put(AppConstants.EXCLUDEACTIONS, AppConstants.TRUE);
			List<String> dbColumns = getColumnDBNames(inputParams);
			String table = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TABLE);
			List<Object[]> excelList = (List<Object[]>) inputParams.get(AppConstants.EXCELLIST);
			
			
			
			for (Object[] obj : excelList) {
				StringBuffer insertSql = new StringBuffer("insert into " + table + " ( " + StringUtils.join(dbColumns.toArray(), " , ") + " ) values ( ");
				insertSql.append("'" + StringUtils.join(obj, "' , '") + "'");
				insertSql.append(" )");
				System.out.println(insertSql);
				
				dataTableDao.executeQuery(insertSql.toString());
			}
			return out;
		} catch (Exception e) {
			logger.error("Exception in Delete TABLE " + tableName + " - " + e.getMessage());
			throw e;
	//		out.put(AppConstants.RESULT, AppConstants.FAILURE);
	//		out.put(AppConstants.MESSAGE, e.getMessage());
		}
	}
	
	@Override
	public Map<String, Object> getColumnDBDisplayNames(Map<String, Object> inputParams) throws Exception {
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		Map<String, Object> outMap = new HashMap<String, Object>();
		
		try {
			String sColumnMappings = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING);
			Map<String, String> hmDBDisplayColumns = new LinkedHashMap<String, String>();
			Map<String, String> hmDBColumnWidths = new LinkedHashMap<String, String>();
			Map hmEditColType = PEAUtils.getEditColType(tableName);
			boolean deletable = PEAUtils.convertToBoolean(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.DELETABLE));
			
			if (AppConstants.FIRST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
	  			if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable )) {
					hmDBDisplayColumns.put("actions", "Actions");
				}
			}
			for (int i=0; i < sColumnMappings.split("\\|").length; i++) {
				String s = sColumnMappings.split("\\|")[i];
	//		for (String s : sColumnMappings.split("\\|")) {
				if (s.split(AppConstants.COLON).length > 1) {
					String sColHidIdx = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_HIDDEN);
					
					if (sColHidIdx != null && sColHidIdx.split("\\|").length > 0 && Arrays.asList(sColHidIdx.split("\\|")).contains(i+"")) {
						
					} else {
						hmDBDisplayColumns.put(s.split(AppConstants.COLON)[1], s.split(AppConstants.COLON)[2]);
						
						if (s.split(AppConstants.COLON).length > 4 && !PEAUtils.isEmpty(s.split(AppConstants.COLON)[4])) {
							hmDBColumnWidths.put(s.split(AppConstants.COLON)[1], s.split(AppConstants.COLON)[4]);
						}
					}
				}
			}
			if (AppConstants.LAST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
	  			if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable )) {
					hmDBDisplayColumns.put("actions", "Actions");
				}
			}
			
			outMap.put(AppConstants.COLUMNS, hmDBDisplayColumns);
			outMap.put(AppConstants.COL_WIDTH_MAP, hmDBColumnWidths);
			return outMap;
		} catch (Exception e) {
			logger.error("Some error in datatable getColumnDBNames" + e.getMessage());
			throw e;
		}
		
	}
	
	@Override
	public Map<String, String> getColumnDisplayIdxMap(Map<String, Object> inputParams) throws Exception {
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		
		try {
			String sColumnMappings = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING);
			Map<String, String> hmDBDisplayColumns = new LinkedHashMap<String, String>();
			Map hmEditColType = PEAUtils.getEditColType(tableName);
			boolean deletable = PEAUtils.convertToBoolean(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.DELETABLE));
			
			if (AppConstants.FIRST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
	  			if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable )) {
					hmDBDisplayColumns.put("actions", "Actions");
				}
			}
			for (int i=0; i < sColumnMappings.split("\\|").length; i++) {
				String s = sColumnMappings.split("\\|")[i];
				if (s.split(AppConstants.COLON).length > 1) {
					hmDBDisplayColumns.put(s.split(AppConstants.COLON)[2], s.split(AppConstants.COLON)[0].replace(AppConstants.COL, ""));
				}
			}
			if (AppConstants.LAST.equalsIgnoreCase(inputParams.get(AppConstants.ACTIONS_COLUMNS_POSITION).toString())) {
	  			if (!PEAUtils.convertToBoolean(inputParams.get(AppConstants.EXCLUDEACTIONS)) && (hmEditColType.size() > 0 || deletable )) {
					hmDBDisplayColumns.put("actions", "Actions");
				}
			}
			return hmDBDisplayColumns;
		} catch (Exception e) {
			logger.error("Some error in datatable getColumnDBNames" + e.getMessage());
			throw e;
		}
		
	}

	@Override
	public Map<String, Object> insertIntoTable(Map<String, Object> inputParams) throws Exception {
		StringBuffer insertSQL = new StringBuffer("insert into ");
		Map<String, Object> outMap = new HashMap<String, Object>();
		outMap.put(AppConstants.MESSAGE, "Record Added Successfully.");
		
		try {
			Map<String, String> colIdxMap = getColIdxMap(inputParams.get(AppConstants.TABLENAME).toString());
			String dbTableName = PropertiesUtil.getProperty(inputParams.get(AppConstants.TABLENAME) + AppConstants.DOT + AppConstants.TABLE);
			
			if (checkIfRowDuplicate(inputParams)) {
				outMap.put(AppConstants.SUCCESS, false);
				outMap.put(AppConstants.MESSAGE, "Record is already preset!");
				return outMap;
			}
			
			insertSQL.append(dbTableName + " ( ");
			
			JsonObject joEditData = (JsonObject) inputParams.get(AppConstants.EDITDATA);
			Map<String, Object> map = new HashMap<String, Object>();
			map = (Map<String, Object>) new Gson().fromJson(joEditData, map.getClass());
			
			logger.info("Add input map - " + map);
			
			if (inputParams.get(AppConstants.AUDIT_COLUMN) != null) {
				Map<String, Map<String, String>> auditCols = (Map<String, Map<String, String>>) inputParams.get(AppConstants.AUDIT_COLUMN);
				for (Entry<String, Map<String, String>> en : auditCols.entrySet()) {
					String dbCol = colIdxMap.get(AppConstants.COL + en.getKey());
					String colType = en.getValue().get(AppConstants.COLTYPE);
					String val = "";
					switch (colType) {
						case "U":
							val = inputParams.get(AppConstants.USERNAME).toString();
							break;
						case "D":
							val = PEAUtils.getUpdateStringDate(); //AppConstants.SDF_YMDHMS.format(new Date());
							break;
					}
					map.put(dbCol, val);
				}
			}
			
			String columns = map.keySet().stream().map(p -> p.toString()).collect(Collectors.joining(", "));
			String values = map.values().stream().map(p -> "'" + PEAUtils.escapeSql(p.toString()) + "'").collect(Collectors.joining(", "));
			
			insertSQL.append(columns);
			insertSQL.append(") values (");
			insertSQL.append(values + " ) ");
			
			logger.info(insertSQL);
			dataTableDao.executeQuery(insertSQL.toString());
			
				
		} catch(Exception e) {
			logger.error("Some error in datatable insertIntoTable" + e.getMessage());
			outMap.put(AppConstants.SUCCESS, false);
			outMap.put(AppConstants.MESSAGE, e.getMessage());
			throw e;
			
		}
		return outMap;
	}
	
	public boolean checkIfRowDuplicate(Map<String, Object> inputParams) throws Exception {
		
		Map<String, String> colIdxMap = getColIdxMap(inputParams.get(AppConstants.TABLENAME).toString());
		String dbTableName = PropertiesUtil.getProperty(inputParams.get(AppConstants.TABLENAME) + AppConstants.DOT + AppConstants.TABLE);
		
		String keyColIdx = PropertiesUtil.getProperty(inputParams.get(AppConstants.TABLENAME).toString() + AppConstants.DOT + AppConstants.KEY_COLUMN_IDX);
		
		JsonObject joEditData = (JsonObject) inputParams.get(AppConstants.EDITDATA);
		Map<String, Object> map = new HashMap<String, Object>();
		
		map = (Map<String, Object>) new Gson().fromJson(joEditData, map.getClass());
		
		List<String> alKeyCol = new ArrayList<String>();
		
		if (keyColIdx != null && keyColIdx.split("\\|").length > 0) {
			for (String s : keyColIdx.split("\\|")) {
				if (s != null) 
					alKeyCol.add(colIdxMap.get(AppConstants.COL + s));
			}
		}
		String whrCondition = "";
		if (alKeyCol != null && alKeyCol.size() > 0) {
			for (String s : alKeyCol) {
				if (map.get(s) != null) {
					if (PEAUtils.isEmpty(whrCondition)) 
						whrCondition += s + " = '" + map.get(s) + "' ";
					else 
						whrCondition += " and " + s + " = '" + map.get(s) + "' "; 
				}
			}
			
			if (PEAUtils.isEmpty(whrCondition)) {
				whrCondition = "1=2";
			}
			
			String chkDuplicateQuery = "select " + alKeyCol.get(0) + " from " + dbTableName + " where " + whrCondition;
			
			if (dataTableDao.checkIfRowExist(chkDuplicateQuery)) {
				return true;
			}
		}
		return false;
	}

}
