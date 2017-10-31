package com.saama.workbench.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Repository
public class DataTableDao {

	@Autowired
	SessionFactory sessionFactory;

	private static final Logger logger = Logger.getLogger(DataTableDao.class);

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void executeQuery(String query) throws Exception {
		try {
			
			getSessionFactory().getCurrentSession().createSQLQuery(query).executeUpdate();
			
		} catch(Exception e) {
			logger.error("Exception occured in executeQuery - " + e.getMessage());
			throw e;
		}
	}
	
	public boolean checkIfRowExist(String query) throws Exception {
		try {
			List list = getSessionFactory().getCurrentSession().createSQLQuery(query).list();
			if (list.size() > 0) {
				return true;
			}
		} catch(Exception e) {
			logger.error("Exception occured in executeQuery - " + e.getMessage());
			throw e;
		}
		return false;
	}

	public List getData(Map<String, Object> inputParams) throws Exception {
		Session session = getSessionFactory().getCurrentSession();
		Map<String, String> colType = new HashMap<String, String>();
		Map<String, String> colDBMap = new HashMap<String, String>();
		List<String> listColumns = new ArrayList<String>();
		String tableName = inputParams.get(AppConstants.TABLENAME).toString();
		
		String sqlQuery = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.QUERY_SQL);
		
		boolean isSessionCacheON = AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.SESSION_CACHE));
		
		try {
			
			String[] colMaping = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING).split("\\|");
			for(String cm : colMaping) {
				if (cm != null) {
					listColumns.add(cm.split(AppConstants.COLON)[1]);
					if (cm.length() > 3)
						colType.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[3]);
					if (cm.length() > 1)
						colDBMap.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[1]);
				}
			}
			
			if (!isSessionCacheON) {
				if (inputParams.get(AppConstants.SORT_COL_0) == null || inputParams.get(AppConstants.SORT_COL_0).toString().trim().length() < 1) {
					inputParams.put(AppConstants.SORT_COL_0, "0");
				}
				if (inputParams.get(AppConstants.SORT_DIR_0) == null || inputParams.get(AppConstants.SORT_DIR_0).toString().trim().length() < 1) {
					inputParams.put(AppConstants.SORT_DIR_0, AppConstants.ASC);
				}
				if (sqlQuery != null && sqlQuery.contains(AppConstants.SORT_COL_SQL_KEY)) {
					if (AppConstants.N.equalsIgnoreCase(colType.get(AppConstants.COL + inputParams.get(AppConstants.SORT_COL_0).toString()))) {
						sqlQuery = sqlQuery.replaceAll(AppConstants.SORT_COL_SQL_KEY, "CAST(" + colDBMap.get(AppConstants.COL + inputParams.get(AppConstants.SORT_COL_0).toString()) + " as numeric(20,4))");
					}
					else
						sqlQuery = sqlQuery.replaceAll(AppConstants.SORT_COL_SQL_KEY, colDBMap.get(AppConstants.COL + inputParams.get(AppConstants.SORT_COL_0).toString()) + "");
				}
				if (sqlQuery != null && sqlQuery.contains(AppConstants.SORT_DIR_SQL_KEY)) {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SORT_DIR_SQL_KEY, inputParams.get(AppConstants.SORT_DIR_0).toString());
				}
				
				if (inputParams.get(AppConstants.DISPLAY_START) != null && inputParams.get(AppConstants.DISPLAY_LENGTH) != null) {
					sqlQuery += " where rn between " + (Integer.parseInt(inputParams.get(AppConstants.DISPLAY_START).toString()) + 1) + " and " + (Integer.parseInt(inputParams.get(AppConstants.DISPLAY_START).toString()) + Integer.parseInt(inputParams.get(AppConstants.DISPLAY_LENGTH).toString())) + "";
				}
				
				StringBuffer searchCols = new StringBuffer();
				String sSearchStr = "";
				if (inputParams.get(AppConstants.SEARCH_PARAM) != null && inputParams.get(AppConstants.SEARCH_PARAM).toString().trim().length() > 0) {
					sSearchStr = inputParams.get(AppConstants.SEARCH_PARAM).toString();
				}
				
				if (!PEAUtils.isEmpty(sSearchStr)) {
					sSearchStr = PEAUtils.escapeLikeSpecialChar(sSearchStr);
				}
				
				if (colType != null) {
					int i = 0;
					for (Map.Entry<String, String> entry : colType.entrySet()) {
						i++;
						switch(entry.getValue()) {
							case AppConstants.D:
//								dd/MM/yyyy hh:mm:ss
								searchCols.append("FORMAT(cast(isNull(" + colDBMap.get(entry.getKey()) + ", '') as datetime), '" + PEAUtils.getDateFormat(tableName) + "')");
								//searchCols.append("convert(varchar(25), cast( isNULL(" + colDBMap.get(entry.getKey()) + ", '')  as date), 103)");
								break;
							case AppConstants.N:
								searchCols.append("convert(varchar(500), isNULL(" + colDBMap.get(entry.getKey()) + ", 0) )");
								break;
							default:
								searchCols.append("convert(varchar(500), isNULL(" + colDBMap.get(entry.getKey()) + ", '') )");
								break;	
						}
						if (colType.size() > i)
							searchCols.append(" + ' ' + ");
					}
					
					if (searchCols.length() > 0) {
						searchCols.append(" like '%" + StringEscapeUtils.escapeSql(sSearchStr) + "%' ESCAPE '>'");
					}
					System.out.println(searchCols);
				}
				
				if (sqlQuery.contains(AppConstants.SEARCH_SQL_CLAUSE)) {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SEARCH_SQL_CLAUSE, searchCols.toString());
				}
				
				if (sqlQuery.contains(AppConstants.SEARCH_STR_SQL_KEY)) {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SEARCH_STR_SQL_KEY, "'%" + StringEscapeUtils.escapeSql(sSearchStr.toLowerCase()) + "%'");
				}
				
				if (sqlQuery.contains(AppConstants.WHERE_CLAUSE_SQL_KEY)) {
					StringBuffer sf = new StringBuffer(); 
					if (inputParams.get(AppConstants.WHERE_CLAUSE) != null) {
						for (Map.Entry<String, Map<String, String>> en : ((Map<String, Map<String, String>>) inputParams.get(AppConstants.WHERE_CLAUSE)).entrySet()) {
							Map<String, String> vMap = en.getValue();
							if (sf.length() > 0) {
								sf.append(" AND ");
							}
							if (vMap.get(AppConstants.COL) == null) {
								continue;
							}
							switch(colType.get(vMap.get(AppConstants.COL))) {
								case AppConstants.D:
									sf.append(colDBMap.get(vMap.get(AppConstants.COL)) + " ");
									sf.append(vMap.get(AppConstants.OPERATOR) + " ");
									if (AppConstants.BETWEEN.equalsIgnoreCase(vMap.get(AppConstants.OPERATOR))) {
										sf.append(" '" + vMap.get(AppConstants.VALUE).replace(" and ", "' and '") + "'");
									}
									else {
										sf.append(" '" + vMap.get(AppConstants.VALUE) + "'");
									}
									break;
//								case AppConstants.N:
//									sf.append(colDBMap.get(vMap.get(AppConstants.COL)));
//									sf.append(vMap.get(AppConstants.OPERATOR));
//									sf.append("'" + vMap.get(AppConstants.VALUE) + "'");
//									break;
//								case AppConstants.S:
//									sf.append("lower(" + colDBMap.get(vMap.get(AppConstants.COL)) + ")");
//									sf.append(vMap.get(AppConstants.OPERATOR));
//									sf.append("'" + vMap.get(AppConstants.VALUE).toLowerCase() + "'");
//									break;
								default:
									if (vMap.get(AppConstants.VALUE_TYPE) == null || !vMap.get(AppConstants.VALUE_TYPE).equalsIgnoreCase(AppConstants.MS)) {
										if (AppConstants.LIKE.equalsIgnoreCase(vMap.get(AppConstants.OPERATOR))) {
											sf.append("lower(" + colDBMap.get(vMap.get(AppConstants.COL)) + ") ");
											sf.append(vMap.get(AppConstants.OPERATOR));
											sf.append(" '%" + vMap.get(AppConstants.VALUE).toLowerCase() + "%'");
										}
										else {
											sf.append("lower(" + colDBMap.get(vMap.get(AppConstants.COL)) + ")");
											sf.append(vMap.get(AppConstants.OPERATOR));
											sf.append("'" + vMap.get(AppConstants.VALUE).toLowerCase() + "'");
										}
									}
									else {
										String delim = "|";
										sf.append("'" + delim + "" + vMap.get(AppConstants.VALUE).toLowerCase() + "" + delim + "'");
										sf.append(" LIKE ");
										sf.append("'%" + delim + "' + lower(" + colDBMap.get(vMap.get(AppConstants.COL)) + ") + '" + delim + "%'");
									}
									break;
							}
						}
					}
					if (sf.length() > 0) {
						sqlQuery = sqlQuery.replaceAll(AppConstants.WHERE_CLAUSE_SQL_KEY, sf.toString());
					}
					else {
						sqlQuery = sqlQuery.replaceAll(AppConstants.WHERE_CLAUSE_SQL_KEY, "1=1");
					}
				}
				
				/*if (sqlQuery.contains(AppConstants.SEARCH_CONDITION_SQL_KEY)) {
					String sSearchableColumns = getColSearchable(tableName);
					String sDefaultSearchCondition = "1=1";
					if (sSearchableColumns != null && sSearchableColumns.trim().length() > 0) {
						String sSearchStr = "";
						if (inputParams.get(AppConstants.SEARCH_PARAM) != null && inputParams.get(AppConstants.SEARCH_PARAM).toString().trim().length() > 0) {
							sSearchStr = inputParams.get(AppConstants.SEARCH_PARAM).toString();
						}
						sDefaultSearchCondition = "lower(" + sSearchableColumns + ")" + " like '%" + sSearchStr.toLowerCase() + "%'";
					}
	//				else {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SEARCH_CONDITION_SQL_KEY, sDefaultSearchCondition);
	//				}
				}*/
			}
			
			if (inputParams.get(AppConstants.SQL_REPLACE_REQ_PARAM) != null) {
				Map<String, String> hmSqlReplaceReqParam = (Map<String, String>) inputParams.get(AppConstants.SQL_REPLACE_REQ_PARAM);
				for (String key : hmSqlReplaceReqParam.keySet()) {
					sqlQuery = sqlQuery.replaceAll(key, hmSqlReplaceReqParam.get(key));
				}
			}
			
			if (sqlQuery.contains(AppConstants.COLUMNS_SQL_KEY)) {
				sqlQuery = sqlQuery.replaceAll(AppConstants.COLUMNS_SQL_KEY, StringUtils.join(listColumns.toArray(new String[]{}), ", "));
			}
			
			SQLQuery query = session.createSQLQuery(sqlQuery);
			return query.list();
		} catch(Exception e) {
			logger.error("Exception occured in getData - " + e.getMessage());
			throw e;
		}
	}
	
	private static String getColSearchable(String tableName) throws Exception {
		List<String> lStringCols = new ArrayList<String>();
		List<String> lNumCols = new ArrayList<String>();
		List<String> lDateCols = new ArrayList<String>();
		
		String[] colMaping = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_MAPING).split("\\|");
		
		for(String cm : colMaping) {
			if (cm != null) {
				if (cm.length() > 4 && PEAUtils.convertToBoolean(cm.split(AppConstants.COLON)[4]))
					if (AppConstants.S.equalsIgnoreCase(cm.split(AppConstants.COLON)[3])) {
						lStringCols.add(cm.split(AppConstants.COLON)[1]);
					}
					else {
						lStringCols.add("cast( " + cm.split(AppConstants.COLON)[1] + " as varchar)");
					}
			}
		}
		return StringUtils.join(lStringCols, " + '!|!' + ");
	}
}
