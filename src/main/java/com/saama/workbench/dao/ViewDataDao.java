package com.saama.workbench.dao;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.DataQualityReportBean;
import com.saama.workbench.bean.ProdDIMBean;
import com.saama.workbench.bean.PromoDIMBean;
import com.saama.workbench.model.AuditJobs;
import com.saama.workbench.model.BusinessRuleMeta;
import com.saama.workbench.model.CustomerStats;
import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.model.SblCustomerHierarchy;
import com.saama.workbench.model.SblProductHierarchy;
import com.saama.workbench.model.WbUserColumnConfig;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;
@Repository
public class ViewDataDao {

	private static final Logger logger = Logger.getLogger(ViewDataDao.class);
	
	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void executeQuery(String query) throws Exception {
		try {
			getSessionFactory().getCurrentSession().createSQLQuery(query).executeUpdate();
		} catch(Exception e) {
			logger.error("Exception occured in executeQuery - ", e);
			throw e;
		}
	}
	
	public Map<String, Object> getSQLData(Map<String, Object> mInput) throws Exception {
		
		Map<String, Object> mOut = new HashMap<String, Object>();
		
		try {
			Session session = getSessionFactory().getCurrentSession();
			Map<String, String> colType = new HashMap<String, String>();
			Map<String, String> colDBMap = new HashMap<String, String>();
			Map<String, String> colDBHdnMap = new HashMap<String, String>();
			
			List<String> listColumns = new ArrayList<String>();
			
			String tabmenu = mInput.get(AppConstants.TABMENU).toString();
			
			String sqlQuery = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.QUERY_SQL);
			
			boolean isSessionCacheON = AppConstants.ON.equalsIgnoreCase(PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.SESSION_CACHE));
			
			String[] colMaping = PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_MAPPING).split("\\|");
			
			List<String> alColIdxHidden = new ArrayList<String>();
					
			if (PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_HIDDEN) != null) {
				alColIdxHidden = Arrays.asList(PropertiesUtil.getProperty(tabmenu + AppConstants.DOT + AppConstants.COLUMN_HIDDEN).split("\\|"));
			}
			
			for(String cm : colMaping) {
				if (cm != null) {
					listColumns.add(cm.split(AppConstants.COLON)[1]);
					if (cm.length() > 3)
						colType.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[3]);
					if (cm.length() > 1)
						colDBMap.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[1]);
					
					if (alColIdxHidden.contains(cm.split(AppConstants.COLON)[0].replace(AppConstants.COL, ""))) {
						colDBHdnMap.put(cm.split(AppConstants.COLON)[0], cm.split(AppConstants.COLON)[1]);
					}
					
				}
			}
			
			if (!isSessionCacheON) {
				if (sqlQuery != null && sqlQuery.contains(AppConstants.SORT_COL_SQL_KEY)) {
					if (AppConstants.N.equalsIgnoreCase(colType.get(AppConstants.COL + mInput.get(AppConstants.SORT_COL_0).toString()))) {
						sqlQuery = sqlQuery.replaceAll(AppConstants.SORT_COL_SQL_KEY, "CAST(" + colDBMap.get(AppConstants.COL + mInput.get(AppConstants.SORT_COL_0).toString()) + " as numeric(20,4))");
					}
					else
						sqlQuery = sqlQuery.replaceAll(AppConstants.SORT_COL_SQL_KEY, colDBMap.get(AppConstants.COL + mInput.get(AppConstants.SORT_COL_0).toString()) + "");
				}
				if (sqlQuery != null && sqlQuery.contains(AppConstants.SORT_DIR_SQL_KEY)) {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SORT_DIR_SQL_KEY, mInput.get(AppConstants.SORT_DIR_0).toString());
				}
				
		//		if (sqlQuery.contains(" where ")) {
		//			sqlQuery += " rn between " + inputParams.get(AppConstants.DISPLAY_START) + " and " + inputParams.get(AppConstants.DISPLAY_START) + inputParams.get(AppConstants.DISPLAY_LENGTH) + "";
		//		}
		//		else {
				if (mInput.get(AppConstants.DISPLAY_START) != null && mInput.get(AppConstants.DISPLAY_LENGTH) != null) {
					sqlQuery += " where rn between " + (Integer.parseInt(mInput.get(AppConstants.DISPLAY_START).toString()) + 1) + " and " + (Integer.parseInt(mInput.get(AppConstants.DISPLAY_START).toString()) + Integer.parseInt(mInput.get(AppConstants.DISPLAY_LENGTH).toString())) + "";
				}
		//		}
					
					
				String sSearchStr = "";
				if (mInput.get(AppConstants.SEARCH_PARAM) != null && mInput.get(AppConstants.SEARCH_PARAM).toString().trim().length() > 0) {
					sSearchStr = mInput.get(AppConstants.SEARCH_PARAM).toString();
				}
				
				if (!PEAUtils.isEmpty(sSearchStr)) {
					sSearchStr = PEAUtils.escapeLikeSpecialChar(sSearchStr);
				}
				
				StringBuffer searchCols = new StringBuffer();
				
				if (colType != null) {
					int i = 0;
					String whrConcate = " + ' ' + "; 
					for (Map.Entry<String, String> entry : colType.entrySet()) {
						i++;
						
						if (colDBHdnMap.get(entry.getKey()) == null) {
							if (!PEAUtils.isEmpty(searchCols.toString())) {
								searchCols.append(whrConcate);
							}
							
							switch(entry.getValue()) {
								case AppConstants.D:
	//								searchCols.append("convert(varchar(25), cast( isNULL(" + colDBMap.get(entry.getKey()) + ", '')  as date), 103)");
									searchCols.append("FORMAT(cast(isNull(" + colDBMap.get(entry.getKey()) + ", '') as datetime), '" + PEAUtils.getDateFormat(tabmenu) + "')");
									break;
								case AppConstants.N:
									searchCols.append("convert(varchar(500), isNULL(" + colDBMap.get(entry.getKey()) + ", 0) )");
									break;
								default:
									searchCols.append("convert(varchar(500), isNULL(" + colDBMap.get(entry.getKey()) + ", '') )");
									break;	
							}
						}
					}
					
					if (searchCols.length() > 0) {
						searchCols.append(" like '%" + StringEscapeUtils.escapeSql(sSearchStr) + "%' ESCAPE '>'");
					}
					// System.out.println(searchCols);
				}
				
				if (sqlQuery.contains(AppConstants.SEARCH_SQL_CLAUSE)) {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SEARCH_SQL_CLAUSE, searchCols.toString());
				}
				
				if (sqlQuery.contains(AppConstants.SEARCH_STR_SQL_KEY)) {
					sqlQuery = sqlQuery.replaceAll(AppConstants.SEARCH_STR_SQL_KEY, "'%" + StringEscapeUtils.escapeSql(sSearchStr.toLowerCase()) + "%'");
				}
				
				if (sqlQuery.contains(AppConstants.WHERE_CLAUSE_SQL_KEY)) {
					StringBuffer sf = new StringBuffer(); 
					if (mInput.get(AppConstants.WHERE_CLAUSE) != null) {
						for (Map.Entry<String, Map<String, String>> en : ((Map<String, Map<String, String>>) mInput.get(AppConstants.WHERE_CLAUSE)).entrySet()) {
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
			}
			if (mInput.get(AppConstants.SQL_REPLACE_REQ_PARAM) != null) {
				Map<String, String> hmSqlReplaceReqParam = (Map<String, String>) mInput.get(AppConstants.SQL_REPLACE_REQ_PARAM);
				for (String key : hmSqlReplaceReqParam.keySet()) {
					sqlQuery = sqlQuery.replaceAll(key, hmSqlReplaceReqParam.get(key));
				}
			}
			if (sqlQuery.contains(AppConstants.COLUMNS_SQL_KEY)) {
				sqlQuery = sqlQuery.replaceAll(AppConstants.COLUMNS_SQL_KEY, StringUtils.join(listColumns.toArray(new String[]{}), ", "));
			}
			// System.out.println(sqlQuery);
			SQLQuery query = session.createSQLQuery(sqlQuery);
			
			mOut.put(AppConstants.TABMENU, tabmenu);
			mOut.put(AppConstants.DATA, query.list());
			return mOut;
		
		} catch (Exception e) {
			logger.error("Exception in getSQLData - " + e.getMessage());
			throw e;
		}
	}

	public List<CustomerStats> getDataAvailabilityStat(Map<String, Object> input) {
		// TODO Auto-generated method stub
		List<Object> arrObj = new ArrayList<Object>();
		List<Object> arrSelectedRecord = new ArrayList<Object>();
		List<CustomerStats> listCustomerStats = new ArrayList<CustomerStats>();
		String whereCondition = " 1=1 ", selAccounts = "";
		final String mnthOrder = AppConstants.ASC;
		
		Session session = null;
		
		try {
			if (input != null && input.get(AppConstants.SEL_ACCOUNTS) != null) {
				selAccounts = " ('"
						+ StringUtils
								.join((String[]) input
										.get(AppConstants.SEL_ACCOUNTS), "','")
						+ "') ";
				whereCondition = " c.account in " + selAccounts;
			}

//			Session session = getSessionFactory().getCurrentSession();
			session = getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(CustomerStats.class);
			String hql =  "SELECT c.promotionEndMonth, c.promotionEndYear, "
							+	 "sum(c.shipmentVolume) as shipmentVolume, sum(c.eposvolume) as eposvolume, "
							+ 	 "SUM(c.eposvolume)/SUM(c.shipmentVolume)*100.0  as ratio "
						+ "FROM CustomerStats c "
						+ "where ((PromotionEndMonth <= DATEPART(MM, getdate()) and PromotionEndYear = DATEPART(YYYY, getdate())) or (PromotionEndYear < DATEPART(YYYY, getdate()))) "
						+ 		 "and " + whereCondition  
						+ "group by c.promotionEndYear, c.promotionEndMonth "
						+ "ORDER BY c.promotionEndYear " + mnthOrder + ", c.promotionEndMonth " + mnthOrder + "";
			
			Query query = session.createQuery(hql);
			arrObj = query.list();

			if (arrObj.size() > 6) {
				if (AppConstants.ASC.equalsIgnoreCase(mnthOrder))
					arrSelectedRecord.addAll(arrObj.subList(arrObj.size() - 6, arrObj.size()));
				else 
					arrSelectedRecord.addAll(arrObj.subList(0, 6));
			}
			else
				arrSelectedRecord.addAll(arrObj);

			if (arrSelectedRecord != null && !arrSelectedRecord.isEmpty()) {

				CustomerStats customerStats = null;
				for (Object obj : arrSelectedRecord) {
					Object[] objArray = (Object[]) obj;
					customerStats = new CustomerStats();
					int num = Integer.parseInt((String) objArray[0]);
					String year = "00";
					if (objArray[1] != null
							&& objArray[1].toString().length() == 4) {
						year = objArray[1].toString().substring(2);
					} else {
						year = "" + objArray[1];
					}
					String month = new DateFormatSymbols().getShortMonths()[num - 1]
							+ " " + year;
					customerStats.setPromotionEndMonth(month);
					customerStats.setShipmentVolume((Long) objArray[2]);
					customerStats.setEposvolume((Long) objArray[3]);
					
					Double sellOutToshipmentRatio = null;
					if (objArray[4] != null) {
						sellOutToshipmentRatio = PEAUtils.round((Double)objArray[4], 2);
					}
					customerStats.setSellOutToshipmentRatio(sellOutToshipmentRatio);
					listCustomerStats.add(customerStats);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (session != null)
				session.close();
		}
		return listCustomerStats;
	}
	
	public List<DataQualityReportBean> getDataQualityReport() throws Exception {
		// TODO Auto-generated method stub
		List<DataQualityReportBean> arrDataQualityReportBean = new ArrayList<DataQualityReportBean>();
		List<Object> arrObj = new ArrayList<Object>();
		List<Object> arrSelectedRecord =  new ArrayList<Object>();

		Session session = null;
		
		try {
//			Session session = getSessionFactory().getCurrentSession();
			session = getSessionFactory().openSession();
			String sql = AppConstants.Dashboard_dataqualityreport_buisnessexception;	
			Query query = session.createSQLQuery(sql);
			arrObj = query.list();
			DataQualityReportBean dataQualityReportBean = null;					
			Set<String> months = new HashSet<String>();			
			if (arrObj != null && !arrObj.isEmpty()) {
				for (Object objArray : arrObj) {
					Object[] objarr = (Object[]) objArray;
					if (objarr != null && objarr.length > 3) {
						dataQualityReportBean = new DataQualityReportBean();
						int num = 1;
						if (objarr[0] != null) {
							num = (Integer) objarr[0];
							String year = "00";
							if (objarr[1] != null && objarr[1].toString().length() == 4) {
								year = objarr[1].toString().substring(2);
							} else {
								year = "" + objarr[1];
							}
							String month = new DateFormatSymbols().getShortMonths()[num - 1] + " " + year;
							if (months.size() < 6 || months.contains(month)) {
								dataQualityReportBean.setMonth(month);
								dataQualityReportBean.setExceptionFlag((String) objarr[2]);
								dataQualityReportBean.setCount((Integer) objarr[3]);

								arrDataQualityReportBean.add(dataQualityReportBean);

								months.add(month);
							} else {
								break;
							}
						}
					}
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return arrDataQualityReportBean;
	}


	public List<SblProductHierarchy> getProductHierarchy() {
		// TODO Auto-generated method stub
		List<SblProductHierarchy> listSblProductHierarchy =  new ArrayList<SblProductHierarchy>();
		try{

			Session session = getSessionFactory().getCurrentSession();	
			
			Criteria criteria = session
					.createCriteria(SblProductHierarchy.class);
			//criteria.add(Restrictions.eq("activeFlag", "Y"));
			
			listSblProductHierarchy = criteria.list();
			session.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
		return listSblProductHierarchy;
	}
	
	public List<Object[]> getProductHierarchySQL() {
		// TODO Auto-generated method stub
		List<Object[]> ob = new ArrayList<Object[]>();
		try{

//			String sql = "select  SUBSTRING(DivisionCode, 0, charindex(' ',DivisionCode)) + ' - ' + DivisionName a, " +
//					"SUBSTRING(Subdivision1Code, 0, charindex(' ',Subdivision1Code)) + ' - ' + Subdivision1Name b, " +
//					"SUBSTRING(Subdivision2Code, 0, charindex(' ',Subdivision2Code)) + ' - ' + Subdivision2Name c, " +
//					"SUBSTRING(CategoryCode, 0, charindex(' ',CategoryCode)) + ' - ' + CategoryName d, " +
//					"SUBSTRING(MarketCode, 0, charindex(' ',MarketCode)) + ' - ' + MarketName e, " +
//					"SUBSTRING(SectorCode, 0, charindex(' ',SectorCode)) + ' - ' + SectorName f, " +
//					"SUBSTRING(SubsectorCode, 0, charindex(' ',SubsectorCode)) + ' - ' + SubsectorName g, " +
//					"SUBSTRING(SegmentCode, 0, charindex(' ',SegmentCode)) + ' - ' + SegmentName h, " +
//					"SUBSTRING(FormCode, 0, charindex(' ',FormCode)) + ' - ' + FormName i, " +
//					"SUBSTRING(SubformCode, 0, charindex(' ',SubformCode)) + ' - ' + SubformName j, " +
//					"SUBSTRING(BrandFormCode, 0, charindex(' ',BrandFormCode)) + ' - ' + BrandFormName k, " +
//					"SUBSTRING(SizePackFormCode, 0, charindex(' ',SizePackFormCode)) + ' - ' + SizePackFormName l, " +
//					"SUBSTRING(SizePackFormVariantCode, 0, charindex(' ',SizePackFormVariantCode)) + ' - ' + SizePackFormVariantName m " +
//					"from LND.SBL_PRODUCT_HIERARCHY where activeFlag = 'Y'";
			String queryProduct = "select  SUBSTRING(Level1Code, 0, charindex(' ',Level1Code)) + ' - ' + Level1Name a, " +
					"SUBSTRING(Level2Code, 0, charindex(' ',Level2Code)) + ' - ' + Level2Name b, " +					
					"SUBSTRING(Level3Code, 0, charindex(' ',Level3Code)) + ' - ' + Level3Name d, " +
					"SUBSTRING(Level4Code, 0, charindex(' ',Level4Code)) + ' - ' + Level4Name e, " +
					"SUBSTRING(Level5Code, 0, charindex(' ',Level5Code)) + ' - ' + Level5Name f " +					
					"from LND.PRODUCT_HIERARCHY";
			
			
			Session session = getSessionFactory().getCurrentSession();
			SQLQuery qry = session.createSQLQuery(queryProduct);
			ob = qry.list();
			session.clear();
		} catch(Exception e){
			e.printStackTrace();
		}
		return ob;
	}
	
	public List<Object[]> getCustomerHierarchySQL() {
		// TODO Auto-generated method stub
		List<Object[]> ob = new ArrayList<Object[]>();
		try{

			 String queryCustomer = "select  SUBSTRING(Level1Code, 0, charindex(' ',Level1Code)) + ' - ' + Level1Name a, " +
					"SUBSTRING(Level2Code, 0, charindex(' ',Level2Code)) + ' - ' + Level2Name b, " +					
					"SUBSTRING(Level3Code, 0, charindex(' ',Level3Code)) + ' - ' + Level3Name d, " +
					"SUBSTRING(Level4Code, 0, charindex(' ',Level4Code)) + ' - ' + Level4Name e, " +
					"SUBSTRING(Level5Code, 0, charindex(' ',Level5Code)) + ' - ' + Level5Name f " +					
					"from LND.CUSTOMER_HIERARCHY";
			
			Session session = getSessionFactory().getCurrentSession();
			SQLQuery qry = session.createSQLQuery(queryCustomer);
			ob = qry.list();
			session.clear();
		} catch(Exception e){
			e.printStackTrace();
		}
		return ob;
	}

	public List<SblCustomerHierarchy> getCustomerHierarchy() {
		// TODO Auto-generated method stub
		List<SblCustomerHierarchy> listSblCustomerHierarchy =  new ArrayList<SblCustomerHierarchy>();
		try{

			Session session = getSessionFactory().getCurrentSession();
			Criteria criteria = session.createCriteria(SblCustomerHierarchy.class);
			
			//criteria.add(Restrictions.eq("name", "SODEXO LTD"));
			//criteria.add(Restrictions.eq("customerStatus", "Active"));
			listSblCustomerHierarchy = criteria.list();
			session.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
		return listSblCustomerHierarchy;
	}
	
	public List<CustDIMBean> getCustomerList(Map<String, Object> input) throws Exception {
		
//		Session session = getSessionFactory().getCurrentSession();
//		Criteria criteria = session.createCriteria(CustDim.class);
//		List<CustDim> list = criteria.list();
		
		List<CustDIMBean> out = new ArrayList<CustDIMBean>();
		String sql = "select distinct b.CustId, a.Name from [CNF].[CUSTOMER_HIERARCHY_LEVEL] a join PRS.CUSTOMER_DIM b on a.Description = b.lowestLevelValue order by a.Name";
		
		if (input.get("OpsoCustomerGroup") != null && input.get("Year") != null && input.get("Month") != null) {
			sql = "select distinct b.CustId, a.Name "
					+ "from [CNF].[CUSTOMER_HIERARCHY_LEVEL] a "
					+ "join PRS.CUSTOMER_DIM b on a.Description = b.lowestLevelValue "
						+ "and a.name in ( select Customer from lnd.WB_OPSO_CUSTOMER_GROUPINGS where Groupings='" + input.get("OpsoCustomerGroup") + "') "
						+ "and b.custId in( select d.customerId from PRS.PROMO_DIM c join PRS.PROMOTION_FACT d on c.promotionID = d.PromotionID where  YEAR(InStoreEndDate)='" + input.get("Year") + "' AND MONTH(InStoreEndDate) = '" + input.get("Month") + "')  "
					+ "order by a.Name";
		}
		Session session = getSessionFactory().getCurrentSession();
		List<Object[]> list = session.createSQLQuery(sql).list();
		for (Object[] obj : list) {
			CustDIMBean p = new CustDIMBean();
			p.setCustomerId(Long.parseLong(obj[0].toString()));
			p.setCustomerName(obj[1].toString());
			out.add(p);
		}
		return out;
	}
	
	public List<DatasetMeta> getDatasetList(Map<String, Object> input) {
		
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(DatasetMeta.class);
		if (input.get("datasetType") != null)
			criteria.add(Restrictions.eq("datasetType", input.get("datasetType").toString()));
		
		criteria.add(Restrictions.or(Restrictions.ne("isActive", "N"), Restrictions.isNull("isActive")));
		
		List<DatasetMeta> list = criteria.list();
		
		session.clear();
		
		return list;
	}


	public List<ProdDIMBean> getProductList(Map<String, Object> input) throws Exception {
		
//		Session session = getSessionFactory().getCurrentSession();
//		Criteria criteria = session.createCriteria(ProdDim.class);
//		List<ProdDim> list = criteria.list();
		
		List<ProdDIMBean> out = new ArrayList<ProdDIMBean>();
		String sql = "select distinct b.ProdId, a.ProductName from CNF.PRODUCT_HIERARCHY_BASE a join PRS.PRODUCT_DIM b on a.TUEAN = b.TUEAN and a.CUEAN = b.CUEAN order by a.ProductName";
		Session session = getSessionFactory().getCurrentSession();
		List<Object[]> list = session.createSQLQuery(sql).list();
		for (Object[] obj : list) {
			ProdDIMBean p = new ProdDIMBean();
			p.setProductId(Long.parseLong(obj[0].toString()));
			p.setProductName(obj[1].toString());
			out.add(p);
		}
		return out;
	}
	
	public List<CustomerStats> getAccountList(Map<String, Object> input) {
		
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(CustomerStats.class).setProjection(Projections.projectionList().add(Projections.groupProperty("account")));
		List<CustomerStats> list = criteria.list();
		//System.out.println(list);
		
		return list;
	}
	
	public List<PromoDIMBean> getPromotionList(Map<String, Object> input) {
		List<PromoDIMBean> out = new ArrayList<PromoDIMBean>();
		String sql = "select distinct PromotionId, Promotion from [LNK].[PROMO_DIM] where Promotion is not null";
		Session session = getSessionFactory().getCurrentSession();
		List<Object[]> list = session.createSQLQuery(sql).list();
		for (Object[] obj : list) {
			PromoDIMBean p = new PromoDIMBean();
			p.setPromotionId(Long.parseLong(obj[0].toString()));
			p.setPromotion(obj[1].toString());
			out.add(p);
		}
		return out;
	}
	
	public List<BusinessRuleMeta> getBusinessRuleList(Map<String, Object> input) {
		
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(BusinessRuleMeta.class);
		criteria = criteria.addOrder(Order.asc("ruleShortDesc"));
		List<BusinessRuleMeta> list = criteria.list();
		
		return list;
	}

	
	public WbUserColumnConfig getViewConfig (String userName, String viewName) throws Exception {
		
		String defaultUser = "default";
		Session session = getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(WbUserColumnConfig.class);
		
		c = session.createCriteria(WbUserColumnConfig.class);
		c.add(Restrictions.eq("userName", userName));
		c.add(Restrictions.eq("viewName", viewName));
		List<?> list = c.list();
		if (list != null && list.size() > 0)
			return (WbUserColumnConfig) c.list().get(0);
		else {
			c = session.createCriteria(WbUserColumnConfig.class);
			c.add(Restrictions.eq("userName", defaultUser));
			c.add(Restrictions.eq("viewName", viewName));
			if (c.list().size() > 0)
				return (WbUserColumnConfig) c.list().get(0);
		}
		return null;
	}

	public void updateViewColumns(Map<String, Object> input) throws Exception {
		
		String currentUser = input.get(AppConstants.USERNAME).toString();
		String viewName = input.get(AppConstants.VIEW).toString();
		
		WbUserColumnConfig userColConfig = new WbUserColumnConfig();
		
		Session session = getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(WbUserColumnConfig.class);
		c.add(Restrictions.eq("userName", currentUser));
		c.add(Restrictions.eq("viewName", viewName));
		List<WbUserColumnConfig> list = c.list();
		
		if (list.size() < 1) {
			userColConfig.setUserName(currentUser);
			userColConfig.setViewName(viewName);
		}
		else {
			userColConfig = list.get(0);
		}
		userColConfig.setDisplayColumns(input.get(AppConstants.DISPLAY_COLUMNS).toString());
		userColConfig.setHiddenColumns(input.get(AppConstants.HIDDEN_COLUMNS).toString());
		
		session.saveOrUpdate(userColConfig);
	}

	public boolean saveToAuditJobs(AuditJobs auditJobs) {
		// TODO Auto-generated method stub
		
		boolean isDataSavedToAuditJobs = false;
		try{
			Session session = getSessionFactory().getCurrentSession();
			session.save(auditJobs);
			isDataSavedToAuditJobs = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
//	
//	public Map<String, Object> getDrivingProductPromotion(String promoID){
//		Map<String, Object> mOut = new HashMap<String, Object>();
//		List<Object> arrObj = new ArrayList<Object>();
//		try{
//			Session session = getSessionFactory().getCurrentSession();
//			
//			String sqlQuery = "select t.* from (select <columns>, row_number() over ( order by <sort_col> <sort_dir> ) rn, count(*) over () tot_cnt from (select a.promoId,  c.promotion, a.TUEAN,a.SpendType,a.TTBlock,a.Invoiceterms from [LND].[SBL_PROMOTED_PRODUCT_SPEND] a  join PRS.PROMO_DIM c on a.promoId = c.promoId where a.promoid in(select overlappingWith from [PEA_UK_DEV].[LND].[OVERLAPPING_PROMOTIONS] where promoId = '<promoId>')) x where <search_sql_clause> and <where_clause>) t ";
//			
//			SQLQuery query = session.createSQLQuery(sqlQuery);
//			query.setParameter("<promoId>",promoID);
//			arrObj = query.list();
//			mOut.put("DrivingProductPrmotion", arrObj);
//		}catch(Exception e){
//			e.printStackTrace();
//			
//		}
//		return mOut;
//	}
	
	public List<String> getDrivingPrmotions(){
		List<String> prmotionList = new ArrayList<String>();
		try{
			Session session = getSessionFactory().getCurrentSession();
			String sqlQuery = "select b.promotion from LND.OVERLAPPING_PROMOTIONS a join PRS.PROMO_DIM b on a.promoID = b.promoID";
			SQLQuery query = session.createSQLQuery(sqlQuery);
			
			prmotionList = query.list();
		}catch(Exception e){
			
		}
		return prmotionList;
	}
	
	public Map<String,String> getOverlappingPromotion(String promoID){
		
		List<Object> drivingPromoProductList = new ArrayList<Object>();
		
		Map<String,String> overLappingPromotion = new HashMap<String,String>();
		try{
			Session session = getSessionFactory().getCurrentSession();
			//String sqlQuery = "select c.promoID, c.promotion + ' | ' + FORMAT(DrivingPromoShipmentStartDate, 'dd/MM/yyyy') + ' - ' + FORMAT(DrivingPromoShipmentEndDate, 'dd/MM/yyyy') + ' | ' + FORMAT(OverlappedPromoShipmentEndDate, 'dd/MM/yyyy') + ' - ' + FORMAT(OverlappedPromoInStoreStartDate, 'dd/MM/yyyy') from [LND].[SBL_PROMOTED_PRODUCT_SPEND] a  join PRS.PROMO_DIM c on a.promoId = c.promoId where a.promoid in(select overlappingWith from [LND].[OVERLAPPING_PROMOTIONS] where promoId = :promoId)";
			
//			String sqlQuery = "select distinct c.promoID, c.promotion + ' | ' + FORMAT(DrivingPromoShipmentStartDate, 'dd/MM/yyyy') + ' - ' + FORMAT(DrivingPromoShipmentEndDate, 'dd/MM/yyyy') + ' | ' + FORMAT(OverlappedPromoShipmentEndDate, 'dd/MM/yyyy') + ' - ' + FORMAT(OverlappedPromoInStoreStartDate, 'dd/MM/yyyy')" 
//					+ 	"from [LND].[SBL_PROMOTED_PRODUCT_SPEND] a  "
//					+	"	 join PRS.PROMO_DIM c on a.promoId = c.promoId " 
//					+	"	 join [LND].[OVERLAPPING_PROMOTIONS] d on d.overlappingWith = a.promoid and d.promoId = :promoId";
			
			
			String sqlQuery = "select distinct c.promoID, c.promotion + ' | ' + FORMAT(DrivingPromoShipmentStartDate, 'dd/MM/yyyy') + ' - ' + FORMAT(DrivingPromoShipmentEndDate, 'dd/MM/yyyy') + ' | ' + FORMAT(OverlappedPromoShipmentStartDate, 'dd/MM/yyyy') + ' - ' + FORMAT(OverlappedPromoShipmentEndDate, 'dd/MM/yyyy') "
					+ 	"from [LND].[SBL_PROMOTED_PRODUCT_SPEND] a   "
					+ 	"	join PRS.PROMO_DIM c on a.promoId = c.promoId " 	 
					+ 	"	join ( "
					+ 	"		select * from ( "
					+ 	"			select distinct PromoId PromoId, OverlappingWith OverlappingWith, DrivingPromoShipmentStartDate DrivingPromoShipmentStartDate, DrivingPromoShipmentEndDate DrivingPromoShipmentEndDate, OverlappedPromoShipmentStartDate OverlappedPromoShipmentStartDate, OverlappedPromoShipmentEndDate OverlappedPromoShipmentEndDate from [LND].[OVERLAPPING_PROMOTIONS] "
					+ 	"			union "
					+ 	"			select distinct OverlappingWith PromoId, PromoId OverlappingWith, OverlappedPromoShipmentStartDate DrivingPromoShipmentStartDate, OverlappedPromoShipmentEndDate DrivingPromoShipmentEndDate, DrivingPromoShipmentStartDate OverlappedPromoShipmentStartDate, DrivingPromoShipmentEndDate OverlappedPromoShipmentEndDate from [LND].[OVERLAPPING_PROMOTIONS] "
					+ 	"		) t  "
					+ 	"	) d on d.overlappingWith = a.promoid and d.promoId = :promoId";
			
			SQLQuery query = session.createSQLQuery(sqlQuery);
			query.setParameter("promoId",promoID);
			drivingPromoProductList = query.list();			
			
			
			for(Object obj : drivingPromoProductList){
				 Object[] objArr = (Object[]) obj;
				 String promoName = "";
				 if (objArr[1] != null) {
					 if (objArr[1].toString().contains(AppConstants.SYM_POUND_SIGN)) {
						 promoName = objArr[1].toString().replaceAll(AppConstants.SYM_POUND_SIGN, AppConstants.SYM_POUND);
					 }
					 else {
						 promoName = objArr[1].toString();
					 }
				 }
				 overLappingPromotion.put((String)objArr[0], promoName);			
			}
			
		} catch(Exception e){
			logger.error("Exception in getOverlappingPromotion", e);
			throw e;
		}
		return overLappingPromotion;
	}

	public List<String> getProdCategoryBrandList(Map<String, Object> input) throws Exception {
		//String sql = "select distinct ProductCategory, ProductBrand from LND.BRAND_HIERARCHY order by ProductCategory, ProductBrand";
		String sql = "select distinct Level2Name from PRS.PRODUCT_DIM order by Level2Name";
		List<String> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public List<?> getLookupTableAccounts(Map<String, Object> input) {
		String sql = "select name from [CNF].[CUSTOMER_HIERARCHY_LEVEL] where level not in ('L1', 'L2', 'L3') and name not in ( select Account from [LND].[WB_EYNTK_CUSTOMER_GROUPINGS] ) order by name";
		
		if (input.get(AppConstants.TYPE) != null && AppConstants.OPSO.equalsIgnoreCase(input.get(AppConstants.TYPE).toString())) {
			sql = "select name from [CNF].[CUSTOMER_HIERARCHY_LEVEL] where level not in ('L1', 'L2', 'L3') and name not in ( select customer from [LND].[WB_OPSO_CUSTOMER_GROUPINGS] ) order by name";
		}
		
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public List<?> getLookupTableSectors(Map<String, Object> input) {
		String sql = "select name from [CNF].[CUSTOMER_HIERARCHY_LEVEL] where level in ('L2', 'L3') order by name";
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public List<String> getCustomerGroups() {
		// TODO Auto-generated method stub
		
		try{
			String sql = "select distinct name from CNF.CUSTOMER_HIERARCHY_LEVEL";
			List<String> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
			return list;
		}catch(Exception e){
			
		}
		return null;
	}

	public List<Object[]> getChannelList(Map<String, Object> input) {
		String sql = "select distinct Brand, Form from PRS.BRANDFORM_DIM order by Brand, Form";
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
		
	}
	
	public List<Object[]> getIntProductList(Map<String, Object> input) {
		String sql = "select ProdID, ProductCode code, ProductName Name from LNK.PROD_DIM where LowestLevel = 'L5' order by 3 asc";
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
		
	}
	
	public List<Object[]> getIntCustomerList(Map<String, Object> input) {
		String sql = "select CustId, customerCode code, CustomerName Name from LNK.CUST_DIM where lowestlevelvalue = customercode + ' - ' + customername order by 3 asc";
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
		
	}
	
	public List<Object[]> getIntFormSubformList(Map<String, Object> input) {
		String sql = "select distinct Brand +'|'+ Form, Subform from PRS.BRANDFORM_DIM order by 1,2";
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public List<Object[]> getMappedProducts(Map<String, Object> input) {
		int extProdId = 0;
		if (input != null && input.get(AppConstants.EXT_PRODID) != null) 
			extProdId = Integer.parseInt(input.get(AppConstants.EXT_PRODID).toString());
		
		String sql = "select a.ProdID, a.ProductCode code, SUBSTRING(a.lowestLevelValue, CHARINDEX('-', a.lowestLevelValue) + 1, len(a.lowestLevelValue)) Name from LNK.PROD_DIM a JOIN LNK.PROD_MAP b on a.ProdID = b.ProdId and ExtProdId = " + extProdId;
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public void saveProdMapping(Map<String, Object> inputParams) throws Exception {
		String delSql = "delete from LNK.PROD_MAP where ExtProdId = :extProdId";
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery sqlQuery = session.createSQLQuery(delSql);
		sqlQuery.setInteger("extProdId", Integer.parseInt(inputParams.get(AppConstants.EXT_PRODID).toString()));
		sqlQuery.executeUpdate();
		
//		String sql = "select brandFormId from [PRS].[BRANDFORM_DIM] where brand = :brand and form = :form and subForm = :subForm";
//		String sql = "select max(brandMasterId) brandFormId from [CNF].[BRAND_MASTER] where brand = :brand";
		String insertSql = new String("insert into LNK.PROD_MAP(SourceId, ProdId, ExtProdId, Weightage, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy ) values (:SourceId, :ProdId, :ExtProdId, :Weightage, :createdDate, :createdBy, :updatedDate, :updatedBy)");
//		String insertSql = new String("insert into LNK.PROD_MAP(SourceId, BrandFormId, ExtProdId) values (:SourceId, :BrandFormId, :ExtProdId)");
		List<Map<String, String>> list = (List<Map<String, String>>)inputParams.get(AppConstants.MAPPING_DATA);
		for (Map<String, String> map : list) {
//			sqlQuery = session.createSQLQuery(sql);
//			sqlQuery.setString("brand", map.get(AppConstants.BRAND));
//			sqlQuery.setString("form", map.get(AppConstants.FORM));
//			sqlQuery.setString("subForm", map.get(AppConstants.SUBFORM));
//			Object brandFormId = sqlQuery.uniqueResult();
			int prodId = Integer.parseInt(map.get(AppConstants.PRODUCTID).toString());
			
//			if (brandFormId != null) {
				SQLQuery sqlQuery1 = session.createSQLQuery(insertSql);
				sqlQuery1.setInteger("SourceId", Integer.parseInt(inputParams.getOrDefault(AppConstants.SOURCEID, "1").toString()));
				sqlQuery1.setInteger("ProdId", (int)prodId);
				sqlQuery1.setInteger("ExtProdId", Integer.parseInt(inputParams.getOrDefault(AppConstants.EXT_PRODID, "-1").toString()));
				sqlQuery1.setDouble("Weightage", Double.parseDouble(map.get(AppConstants.WEIGHTAGE))/100);
				sqlQuery1.setDate("createdDate",(Date) inputParams.get(AppConstants.DATE));
				sqlQuery1.setString("createdBy", inputParams.get(AppConstants.USERNAME).toString());
				sqlQuery1.setDate("updatedDate",(Date) inputParams.get(AppConstants.DATE));
				sqlQuery1.setString("updatedBy", inputParams.get(AppConstants.USERNAME).toString());
				sqlQuery1.executeUpdate();
//			}
//			else {
//				logger.warn("Brand is not present - " + map.get(AppConstants.BRAND) + ", " + map.get(AppConstants.FORM) + ", " + map.get(AppConstants.SUBFORM));
//			}
		}
	}
	/*public void saveProdMapping(Map<String, Object> inputParams) {
	try {
			Session session = getSessionFactory().getCurrentSession();
			String sql = "select distinct ExtProdId from [LNK].[PROD_MAP] where ExtProdId = :extProdId";
			SQLQuery query = session.createSQLQuery(sql);
			query.setInteger("extProdId", Integer.parseInt(inputParams.get(AppConstants.EXT_PRODID).toString()));
			List prodList = query.list();
			String selectIdSql = "select brandFormId from PRS.BRANDFORM_DIM where brand = :brand and form = :form and subForm = :subForm";
			List<Map<String, String>> list = (List<Map<String, String>>)inputParams.get(AppConstants.MAPPING_DATA);
		for (Map<String, String> map : list) {
			query = session.createSQLQuery(selectIdSql);
			query.setString("brand", map.get(AppConstants.BRAND));
			query.setString("form", map.get(AppConstants.FORM));
			query.setString("subForm", map.get(AppConstants.SUBFORM));
			Object brandFormId = query.uniqueResult();
			
			
			if (prodList.size() > 0) {
				String sqlUpdate = "update [LNK].[PROD_MAP] set SourceId = :sourceId, BrandFormId = :brandFormId, UpdatedDate = :updatedDate, UpdatedBy = :updatedBy where ExtProdId = :extProdId ";
				query = session.createSQLQuery(sqlUpdate);
				query.setInteger("brandFormId", (int)brandFormId);
				query.setInteger("sourceId", Integer.parseInt(inputParams.get(AppConstants.SOURCEID).toString()));
				query.setDate("updatedDate", (Date) (inputParams.get(AppConstants.DATE)));
				query.setString("updatedBy",inputParams.get(AppConstants.USERNAME).toString());
				query.setInteger("extProdId", Integer.parseInt(inputParams.get(AppConstants.EXT_PRODID).toString()));
				query.executeUpdate();
			}
			else {
				String sqlInsert = "insert into [LNK].[PROD_MAP] (SourceId, BrandFormId, ExtProdId, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy) values (:sourceId, :brandFormId, :extProdId, :createdDate, :createdBy, :updatedDate, :updatedBy)";
				query = session.createSQLQuery(sqlInsert);
				query.setInteger("brandFormId", (int)brandFormId);
				query.setInteger("extProdId", Integer.parseInt(inputParams.get(AppConstants.EXT_PRODID).toString()));
				query.setDate("createdDate",(Date) inputParams.get(AppConstants.DATE));
				query.setString("createdBy", inputParams.get(AppConstants.USERNAME).toString());
				query.setDate("updatedDate",(Date) inputParams.get(AppConstants.DATE));
				query.setString("updatedBy", inputParams.get(AppConstants.USERNAME).toString());
				query.setInteger("sourceId", Integer.parseInt(inputParams.get(AppConstants.SOURCEID).toString()));
				query.executeUpdate();
			}
		}
	}
		catch (Exception e) {
			logger.error("Exception in saveProdMapping", e);
			
		}
}*/
	
	public Map<String, Object> saveCustMapping(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> out = new HashMap<String, Object>();
		out.put(AppConstants.SUCCESS, true);
		out.put(AppConstants.MESSAGE, "Mapping saved Successfully.");
		
		int extCustId = Integer.parseInt(inputParams.get(AppConstants.EXT_CUSTID).toString());
		
		String delSql = "delete from LNK.CUST_MAP where ExtCustId = :" + AppConstants.EXT_CUSTID;
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(delSql);
		query.setInteger(AppConstants.EXT_CUSTID, extCustId);
		query.executeUpdate();
		
		
//		String sql = "select distinct ExtCustId from [LNK].[CUST_Map] where ExtCustId = :ExtCustId";
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setInteger("ExtCustId", Integer.parseInt(inputParams.get(AppConstants.EXT_CUSTID).toString()));
//		List custList = query.list();
//		if (custList.size() > 0) {
//			String sqlUpdate = "update [LNK].[CUST_MAP] set CustId = :custId, SourceId = :sourceId, UpdatedDate = :updatedDate, UpdatedBy = :updatedBy where ExtCustId = :extCustId ";
//			query = session.createSQLQuery(sqlUpdate);
//			query.setInteger("custId", Integer.parseInt(inputParams.get(AppConstants.INT_CUSTID).toString()));
//			query.setInteger("sourceId", Integer.parseInt(inputParams.get(AppConstants.SOURCEID).toString()));
//			query.setDate("updatedDate", (Date) (inputParams.get(AppConstants.DATE)));
//			query.setString("updatedBy",inputParams.get(AppConstants.USERNAME).toString());
//			query.setInteger("extCustId", Integer.parseInt(inputParams.get(AppConstants.EXT_CUSTID).toString()));
//			query.executeUpdate();
//		}
//		else {
		
		JsonObject joIntCustomers = (JsonObject) inputParams.get(AppConstants.INT_CUSTOMER);
		for (Map.Entry<String, JsonElement> en : joIntCustomers.entrySet()) {
			int intCustId = Integer.parseInt(en.getKey());
			double weightage = Double.parseDouble(en.getValue().getAsJsonObject().get(AppConstants.PERCENTAGE).getAsString());
			
			String sqlInsert = "insert into [LNK].[CUST_MAP] (SourceId, CustId, ExtCustId, Weightage, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy) "
					+ "values (:" + AppConstants.SOURCEID + ", :" + AppConstants.INT_CUSTID + ", :" + AppConstants.EXT_CUSTID + ", :" + AppConstants.WEIGHTAGE + ","
							+ " :" + AppConstants.CREATED_DATE + ", :" + AppConstants.CREATED_BY + ", :" + AppConstants.UPDATED_DATE + ", :" + AppConstants.UPDATED_BY + ")";
			
			query = session.createSQLQuery(sqlInsert);
			query.setInteger(AppConstants.INT_CUSTID, intCustId);
			query.setInteger(AppConstants.EXT_CUSTID, extCustId);
			query.setDouble(AppConstants.WEIGHTAGE, weightage);
			query.setDate(AppConstants.CREATED_DATE,(Date) inputParams.get(AppConstants.DATE));
			query.setString(AppConstants.CREATED_BY, inputParams.get(AppConstants.USERNAME).toString());
			query.setDate(AppConstants.UPDATED_DATE,(Date) inputParams.get(AppConstants.DATE));
			query.setString(AppConstants.UPDATED_BY, inputParams.get(AppConstants.USERNAME).toString());
			query.setInteger(AppConstants.SOURCEID, Integer.parseInt(inputParams.get(AppConstants.SOURCEID).toString()));
			query.executeUpdate();
		}
		
		return out;
	}

	public List<CustDIMBean> getIntCustList(Map<String, Object> input) {
		List<CustDIMBean> out = new ArrayList<CustDIMBean>();
//		String sql = "select distinct Level3ID, Level3Name from PRS.CUSTOMER_DIM where Level3ID is not null";
		String sql = "Select CustId, CustomerName, CustomerCode  from LNK.CUST_DIM where lowestlevelvalue = customercode + ' - ' + customername order by 2 asc";
		Session session = getSessionFactory().getCurrentSession();
		List<Object[]> list = session.createSQLQuery(sql).list();
		for (Object[] obj : list) {
			CustDIMBean p = new CustDIMBean();
			p.setCustomerId(Long.parseLong(obj[0].toString()));
			p.setCustomerName(obj[1].toString());
			p.setCustomerCode(obj[2].toString());
			out.add(p);
		}
		return out;
	}

	public  Map<String, String>  getExtSourceList(Map<String, Object> input) {
		Map<String, String> hmSource = new HashMap<String, String>();
		String sql = "select SourceId, SourceName from [CNF].[SOURCE_MASTER]";
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		for (Object[] obj : list) {
			hmSource.put(obj[0].toString(), obj[1].toString());
		}
		return hmSource;
		
	}

	public List<String> getUnMappedBrandList(Map<String, Object> input) {
		
		Map<String, String> hmSource = new HashMap<String, String>();
		String sql = "select distinct Brand from [PRS].[BRANDFORM_DIM] where BrandFormID not in (select BrandFormID from LNK.PROD_MAP)";
		List<String> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
		
	}

	public  Map<String, String> markExtBrandIgnored(Map<String, Object> input) throws Exception {
		
		String sql = "update CNF.EXT_PRODUCT_MASTER set ignored = 'Y', UpdatedDate = :updatedDate, UpdatedBy = :updatedBy where ExtProdID = :ExtProdID";
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("ExtProdID", Integer.parseInt(input.get(AppConstants.EXT_PRODID).toString()));
		query.setDate("updatedDate",(Date) input.get(AppConstants.DATE));
		query.setString("updatedBy", input.get(AppConstants.USERNAME).toString());
		query.executeUpdate();
		
		return null;
	}

	public void unmapProduct(Map<String, Object> input) throws Exception {
		
		String sql = "delete from LNK.PROD_MAP where ExtProdID = :ExtProdID";
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("ExtProdID", Integer.parseInt(input.get(AppConstants.EXT_PRODID).toString()));
		query.executeUpdate();
		
	}
	
	public void mapProduct(Map<String, Object> input) throws Exception {
		
		String sql = "insert into LNK.PROD_MAP(SourceId, ProdId, ExtProdId, Weightage, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy) values (1, :" + AppConstants.INT_PRODID + ", :" + AppConstants.EXT_PRODID + ", 1, GetDate(), :" + AppConstants.USERNAME + ", GetDate(), :" + AppConstants.USERNAME + ")";
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(AppConstants.INT_PRODID, Integer.parseInt(input.get(AppConstants.INT_PRODID).toString()));
		query.setInteger(AppConstants.EXT_PRODID, Integer.parseInt(input.get(AppConstants.EXT_PRODID).toString()));
		query.setString(AppConstants.USERNAME, input.get(AppConstants.USERNAME).toString());
		query.executeUpdate();
		
	}
	
	public void mapCustomer(Map<String, Object> input) throws Exception {
		
		String sql = "insert into LNK.CUST_MAP(SourceId, CustId, ExtCustId, Weightage, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy) values (1, :" + AppConstants.INT_CUSTID + ", :" + AppConstants.EXT_CUSTID + ", 1, GetDate(), :" + AppConstants.USERNAME + ", GetDate(), :" + AppConstants.USERNAME + ")";
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(AppConstants.INT_CUSTID, Integer.parseInt(input.get(AppConstants.INT_CUSTID).toString()));
		query.setInteger(AppConstants.EXT_CUSTID, Integer.parseInt(input.get(AppConstants.EXT_CUSTID).toString()));
		query.setString(AppConstants.USERNAME, input.get(AppConstants.USERNAME).toString());
		query.executeUpdate();
		
	}

	public void unmapCustomer(Map<String, Object> input) throws Exception {
		
		String sql = "delete from LNK.CUST_MAP where ExtCustID = :ExtCustID";
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("ExtCustID", Integer.parseInt(input.get(AppConstants.EXT_CUSTID).toString()));
		query.executeUpdate();
		
	}
	public List<Object[]> getCategoryList(Map<String, Object> input) {
		// TODO Auto-generated method stub	
			String sql = "select distinct SourceId, Category from [CNF].[EXT_PRODUCT_MASTER] where Category is not null";
			SQLQuery qry = getSessionFactory().getCurrentSession().createSQLQuery(sql);
//			qry.setInteger("sourceId", Integer.parseInt(input.get(AppConstants.SOURCEID).toString()));
			List<Object[]> list = qry.list();
			return list;
		
	}

	public Map<String,Integer> getUnmappedData() throws Exception {
		Map<String,Integer> arrUnmappedData = new HashMap<String,Integer>();
		String sql = AppConstants.Dashboard_dataqualityreport_mappingexception;
		
		Session  session = null;
		try {
			session = getSessionFactory().openSession();
			SQLQuery qry = session.createSQLQuery(sql);
			List<Object> list = qry.list();
			
			if (list != null && !list.isEmpty()) {
				for (Object objArray : list) {
					Object[] objarr = (Object[]) objArray;
					arrUnmappedData.put("UnmappedProductCount", (Integer)objarr[0]);
					arrUnmappedData.put("UnmappedCustomerCount",(Integer) objarr[1]);
					arrUnmappedData.put("mappedProdCount",(Integer) objarr[2]);
					arrUnmappedData.put("mappedCustomerCount", (Integer) objarr[3]);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return arrUnmappedData;
	}

	public List<DataQualityReportBean> getTechnicalDQreport() throws Exception {
		
		List<DataQualityReportBean> arrDataQualityReportBean = new ArrayList<DataQualityReportBean>();
		String sql = AppConstants.Dashboard_dataqualityreport_techicalreject;
		
		Session session = null;
		
		try {
			session = getSessionFactory().openSession();
			SQLQuery qry = session.createSQLQuery(sql);
			//qry.setInteger("sourceId", Integer.parseInt(input.get(AppConstants.SOURCEID).toString()));
			List<DataQualityReportBean> list = qry.list();
			
			DataQualityReportBean dataQualityReportBean = null;					
			Set<String> months = new HashSet<String>();			
			if (list != null && !list.isEmpty()) {
				for (Object objArray : list) {
					Object[] objarr = (Object[]) objArray;
					if (objarr != null && objarr.length > 3) {
						dataQualityReportBean = new DataQualityReportBean();
						int num = 1;
						if (objarr[1] != null) {
							num = (Integer) objarr[1];
							String year = "00";
							if (objarr[2] != null && objarr[2].toString().length() == 4) {
								year = objarr[2].toString().substring(2);
							} else {
								year = "" + objarr[2];
							}
							String month = new DateFormatSymbols().getShortMonths()[num - 1] + " " + year;
							if (months.size() < 6 || months.contains(month)) {
								dataQualityReportBean.setMonth(month);
								dataQualityReportBean.setProcessName((String) objarr[0]);
								dataQualityReportBean.setCount((Integer) objarr[3]);
								arrDataQualityReportBean.add(dataQualityReportBean);
								months.add(month);
							} else {
								break;
							}
						}
					}
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return arrDataQualityReportBean;
	}

	public Map<String, Object> promotionPeriodAlignmentData(
			Map<String, String> input) throws Exception {
		// TODO Auto-generated method stub
		List<Object> lstAlinmentData= new ArrayList<Object>();
		List<Object> lstPromotionAlinment= new ArrayList<Object>();
		Map<String, Object> lstPromotionAlignmentData = new HashMap<String, Object>();
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String sql =AppConstants.promotion_alignment;	
		sql = sql.replace("<customerName>", input.get("customerName")).replace("<productName>", input.get("productName"));
		SQLQuery qry = getSessionFactory().getCurrentSession().createSQLQuery(sql);				
		String s1 = input.get("startDate");
		String s2 = input.get("endDate");			
		qry.setString("startdate", s1);
		qry.setString("enddate", s2);			
		lstAlinmentData = qry.list();
		lstPromotionAlignmentData.put("topGraph",lstAlinmentData);
		
		String promotionData = AppConstants.promotion_alignment_data;
		String promotionDataCust = promotionData.replace("<customerName>", input.get("customerName"));
		String promotionDataProd =promotionDataCust.replace("<productName>", input.get("productName"));
		SQLQuery promotionDataQry = getSessionFactory().getCurrentSession().createSQLQuery(promotionDataProd);
		promotionDataQry.setString("startdate",s1);
		promotionDataQry.setString("enddate", s2);			
		lstPromotionAlinment = promotionDataQry.list();
		lstPromotionAlignmentData.put("bottomGraph",lstPromotionAlinment);
			
		return lstPromotionAlignmentData;
	}
	
	
public Map<String,Object> promotionPeriodAlignmentcusNprodData(Map<String, Object> hmInput) throws Exception {	
	    Map<String, Object> lstPromotionAlignmentData = new HashMap<String, Object>();
		//List<String> out = new ArrayList<String>();
	    Map<String,String> customerData = new HashMap<String,String>();
	    Map<String,String> productData = new HashMap<String,String>();
		String sql = new String();
		
		List<Object[]> list = getIntProductList(hmInput);
		for (Object[] obj : list) {	
			Object[] objArray = obj;
			productData.put(objArray[0].toString(), objArray[1].toString() + " - " + objArray[2].toString());
			
		}
		lstPromotionAlignmentData.put("product", productData);
			
		sql = "select distinct custID, lowestLevelValue from lnk.cust_dim where lowestlevelvalue = customerCode + ' - ' + customerName order by 2";
		list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		for (Object obj : list) {	
			Object[] objArray = (Object[]) obj;
			customerData.put(objArray[0].toString(),objArray[1].toString());
		}
		
		lstPromotionAlignmentData.put("customer", customerData);
		
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.set(Calendar.DATE, 1);
		aCalendar.add(Calendar.DAY_OF_MONTH, AppConstants.previousmonth);		
		String lastDateOfPreviousMonth = new SimpleDateFormat("dd-MM-yyyy").format(aCalendar.getTime());
		aCalendar.set(Calendar.DATE, 1);		
		String firstDateOfPreviousMonth = new SimpleDateFormat("dd-MM-yyyy").format(aCalendar.getTime());
		lstPromotionAlignmentData.put("StartDate", firstDateOfPreviousMonth);
		lstPromotionAlignmentData.put("endDate", lastDateOfPreviousMonth);			
		return lstPromotionAlignmentData;
	}

	public List<Object[]> getExtProduct(Map<String, Object> inputParams) {
		String sql = "select ExtProdId, ProductName, SourceId, ExtProductNameNonUnilever from CNF.EXT_PRODUCT_MASTER where ExtProdId = :" + AppConstants.EXT_PRODID;
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(AppConstants.EXT_PRODID, Integer.parseInt(inputParams.get(AppConstants.EXT_PRODID).toString()));
		
		return query.list();
		
	}

	public List<Object[]> getMappedCustomers(Map<String, Object> input) {
		int extProdId = 0;
		if (input != null && input.get(AppConstants.EXT_CUSTID) != null) 
			extProdId = Integer.parseInt(input.get(AppConstants.EXT_CUSTID).toString());
		
		String sql = "select a.CustId, a.CustomerCode code, SUBSTRING(lowestLevelValue, CHARINDEX('-', lowestLevelValue) + 1, len(lowestLevelValue)) Name  from LNK.CUST_DIM a JOIN LNK.CUST_MAP b on a.CUstId = b.CustId and b.ExtCustId = " + extProdId;
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public List<Object[]> getExtCustomer(Map<String, Object> inputParams) {
		String sql = "select ExtCustId, CustomerName, CustCode from CNF.EXT_Customer_MASTER where ExtCustId = :" + AppConstants.EXT_CUSTID;
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(AppConstants.EXT_CUSTID, Integer.parseInt(inputParams.get(AppConstants.EXT_CUSTID).toString()));
		
		return query.list();
	}
	
	public Map<String, Object> updateProcessPromotionList(Map<String, Object> inputParams) {
		String sql = null;
		String promoId = null; 
		Map<String, Object> hmOut = new HashMap<String, Object>();
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = null;
		if (inputParams.get(AppConstants.PROMO_ID) != null) {
			promoId = inputParams.get(AppConstants.PROMO_ID).toString();
		}
		else if (inputParams.get(AppConstants.PROMOTION_ID) != null) {
			sql = "SELECT PromoId FROM PRS.PROMO_DIM where PromotionId = :" + AppConstants.PROMOTION_ID;
			query = session.createSQLQuery(sql);
			query.setBigDecimal(AppConstants.PROMOTION_ID, new BigDecimal(inputParams.get(AppConstants.PROMOTION_ID).toString()));
			List<String> list = query.list();
			if (list != null && list.size() > 0) {
				promoId = list.get(0);
			}
		}
		
		if (!PEAUtils.isEmpty(promoId)) {
			sql = "SELECT PromoId FROM LND.PROCESS_PROMOTION_LIST where PromoId = :" + AppConstants.PROMO_ID;
			query = session.createSQLQuery(sql);
			query.setString(AppConstants.PROMO_ID, promoId);
			List<String> list = query.list();
			
			List<String> colToUpdate = (List<String>) inputParams.get(AppConstants.COLUMNS_TO_UPDATE);
			
			if (list != null && list.size() > 0 && colToUpdate!= null && colToUpdate.size() > 0) {
				sql = "UPDATE LND.PROCESS_PROMOTION_LIST SET ";
				for (String str : colToUpdate) {
					if (!sql.endsWith(" SET ")) {
						str += " , ";
					}
					sql += str + " = 'Y'";
					
//					switch (str) {
//						case AppConstants.ALIGN_CHANGE:
//							sql += " AlignChange = 'Y'";
//							break;
//						case AppConstants.METRIC_CHANGE:
//							sql += " MetricChange = 'Y'";
//							break;
//						case AppConstants.ADHOC_CHANGE:
//							sql += " AdhocChange = 'Y'";
//							break;
//						case AppConstants.SELL_IN_CHANGE:
//							sql += " SellInChange = 'Y'";
//							break;
//						case AppConstants.SELL_OUT_CHANGE:
//							sql += " AlignChange = 'Y'";
//							break;
//						
//						
//					}
				}
				
				sql += " WHERE PROMOID = :" + AppConstants.PROMO_ID;
				query = session.createSQLQuery(sql);
				query.setString(AppConstants.PROMO_ID, promoId);
				query.executeUpdate();
			}
			else {
				sql = "insert into LND.PROCESS_PROMOTION_LIST (PromoId, <ADDITIONAL_COLUMNS>) values (:" + AppConstants.PROMO_ID + ", <ADDITIONAL_COL_VALUES>)";
				List<String> addColumns = new ArrayList<String>();
				List<String> addColValues = new ArrayList<String>();
				
				for (String str : colToUpdate) {
					addColumns.add(str);
					addColValues.add("'Y'");
				}
				
				sql = sql.replace("<ADDITIONAL_COLUMNS>", StringUtils.join(addColumns.toArray(new String[] {}), " , "));
				sql = sql.replace("<ADDITIONAL_COL_VALUES>", StringUtils.join(addColValues.toArray(new String[] {}), " , "));
				
				query = session.createSQLQuery(sql);
				query.setString(AppConstants.PROMO_ID, promoId);
				query.executeUpdate();
				
			}
			hmOut.put(AppConstants.SUCCESS, AppConstants.TRUE);
			hmOut.put(AppConstants.MESSAGE, "Record is Updated Successfully!");
		}
		else {
			hmOut.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOut.put(AppConstants.MESSAGE, "Not a valid Promotion Id - " + inputParams.get(AppConstants.PROMOTION_ID) + " " + inputParams.get(AppConstants.PROMO_ID));
		}
		
		return hmOut;
		
	}
	
	
	public Map<String, Object> saveOverrideChanges(Map<String,Object> hmInput) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object>  hmOutput = new HashMap<String,Object>();
			
		String insertSQL = "INSERT INTO LND.PROMOTION_OVERRIDE (PromoID, CustomerID, ProductID, AlignDate, FixedCost, TotalCost, NaturalKey, NaturalKeyHash, RowHash, BatchID, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy) VALUES (:" + AppConstants.PROMO_ID + ", :" + AppConstants.CUSTOMERID + ", :" + AppConstants.PRODUCTID + ", :" + AppConstants.STARTDATE + ", NULL, NULL, NULL, NULL, NULL, NULL, getDate(), 'ETL', getDate(), 'ETL')";
		String selectSQL = "SELECT PromoId FROM LND.PROMOTION_OVERRIDE WHERE PromoId = :" + AppConstants.PROMO_ID + " AND CustomerId = :" + AppConstants.CUSTOMERID + " AND ProductId = :" + AppConstants.PRODUCTID;
		String updateSQL = "UPDATE LND.PROMOTION_OVERRIDE SET AlignDate = :" + AppConstants.STARTDATE + " where PromoId = :" + AppConstants.PROMO_ID + " and CustomerId = :" + AppConstants.CUSTOMERID + " and ProductId = :" + AppConstants.PRODUCTID + "";
		
		String productId = hmInput.get(AppConstants.PRODUCTID).toString();
		String customerId = hmInput.get(AppConstants.CUSTOMERID).toString();
		
		Session session = sessionFactory.getCurrentSession();
		SQLQuery selectQry = session.createSQLQuery(selectSQL);
		SQLQuery updateQry = session.createSQLQuery(updateSQL);
		SQLQuery insertQry = session.createSQLQuery(insertSQL);
		
		List<?> list = null;
		
		JsonObject joAlignData = (JsonObject) hmInput.get(AppConstants.PROMO_ALIGN_DATA);
		if (joAlignData != null) {
			
			for (Map.Entry<String, JsonElement> en : joAlignData.entrySet()) {
				String promoId = en.getKey();
				selectQry.setString(AppConstants.PROMO_ID, promoId);
				selectQry.setString(AppConstants.CUSTOMERID, customerId);
				selectQry.setString(AppConstants.PRODUCTID, productId);
				
				list = selectQry.list();
				
				if (list != null && list.size() > 0) {
					updateQry.setString(AppConstants.PROMO_ID, promoId);
					updateQry.setString(AppConstants.CUSTOMERID, customerId);
					updateQry.setString(AppConstants.PRODUCTID, productId);
					updateQry.setString(AppConstants.STARTDATE, en.getValue().getAsJsonObject().get(AppConstants.STARTDATE).toString().replaceAll("\"", ""));
					
					updateQry.executeUpdate();
				}
				
				else {
					insertQry.setString(AppConstants.PROMO_ID, promoId);
					insertQry.setString(AppConstants.CUSTOMERID, customerId);
					insertQry.setString(AppConstants.PRODUCTID, productId);
					insertQry.setString(AppConstants.STARTDATE, en.getValue().getAsJsonObject().get(AppConstants.STARTDATE).toString().replaceAll("\"", ""));
					
					insertQry.executeUpdate();
				}
				
				// Update Process Promotion list
				hmInput.put(AppConstants.PROMO_ID, promoId);
				updateProcessPromotionList(hmInput);
			}
		}
		hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
		hmOutput.put(AppConstants.MESSAGE, "Data saved successfully.");
		return hmOutput;
	}
	
	public Map<String, Set<String>> getDataQualityStatusMeta() throws Exception{
		
		Map<String, Set<String>> dataQualityStatusData = new HashMap<String, Set<String>>();
		
		try{
			String sql = "select distinct processName,mainfilename,rejectreason  from AUX.DQ_REJECTS ";
			SQLQuery qry = getSessionFactory().getCurrentSession().createSQLQuery(sql);
            Set<String> process = new HashSet<String>(); 
            Set<String> filename = new HashSet<String>(); 
            Set<String> issuename = new HashSet<String>(); 
			List<Object[]> list = qry.list();
			if (list != null && !list.isEmpty()) {
				for (Object objArray : list) {
					Object[] objarr = (Object[]) objArray;					
					process.add(objarr[0].toString());
					filename.add(objarr[1].toString());
					issuename.add(objarr[2].toString());
				}
			}
			dataQualityStatusData.put("processName",process);
			dataQualityStatusData.put("filename",filename);
			dataQualityStatusData.put("issuename", issuename);
			return dataQualityStatusData;
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return dataQualityStatusData;
	}

	public Map<String, Object> saveDataQualityStatus(Map<String, Object> hmInput) {
		// TODO Auto-generated method stub
		Map<String, Object> hmOutput = new HashMap<String, Object>();
		
		hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
		hmOutput.put(AppConstants.MESSAGE, "Data saved successfully.");
		
		String sUpdate = "UPDATE AUX.DQ_REJECTS SET STATUS = :status WHERE REJECTID IN (:rejectIds)";
		try {
			JsonObject jo = (JsonObject) hmInput.get(AppConstants.EDITED_STATUS);
			SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(sUpdate);
			
			for (Map.Entry<String, JsonElement> en : jo.entrySet()) {
				String status = en.getKey();
				List<String> alRejectId = new ArrayList<String>();
				StringBuilder sbRejectIds = new StringBuilder();
				for (JsonElement je : en.getValue().getAsJsonArray()) {
					
					alRejectId.add(je.getAsString());
				}
				
				query.setString("status", status);
				query.setParameterList("rejectIds", alRejectId);
				
				query.executeUpdate();
			}
		} catch(Exception e){
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			
			logger.error("Exception in saveDataQualityStatus", e);
		}
		return hmOutput;
	}

	public Map<String, Object> changeBusinessRuleStatus(
			Map<String, Object> inputParams) throws Exception {
		
		String sql = "UPDATE PRS.BUSINESS_RULES_META SET IsDeleted = 'Y' WHERE RuleShortDesc = :" + AppConstants.RULE_NAME;
		
		if (AppConstants.ENABLE.equalsIgnoreCase(inputParams.getOrDefault(AppConstants.STATUS, AppConstants.BLANK).toString())) {
			sql = "UPDATE PRS.BUSINESS_RULES_META SET IsEnabled = 'Y' WHERE RuleShortDesc = :" + AppConstants.RULE_NAME;
		}
		
		if (AppConstants.DISABLE.equalsIgnoreCase(inputParams.getOrDefault(AppConstants.STATUS, AppConstants.BLANK).toString())) {
			sql = "UPDATE PRS.BUSINESS_RULES_META SET IsEnabled = 'N' WHERE RuleShortDesc = :" + AppConstants.RULE_NAME;
		}
		
		try {
			Query qry = getSessionFactory().getCurrentSession().createSQLQuery(sql);
			qry.setParameter(AppConstants.RULE_NAME, inputParams.get(AppConstants.RULE_NAME));
			qry.executeUpdate();
		}
		catch (Exception e) {
			logger.error("Exception in deleteBusinessRule", e);
			throw e;
		}
		
		return null;
	}

	public List<?> getBRColumnList(Map<String, Object> hmInput) throws Exception {
		String sql = "select distinct COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME in ('PROMOTION_FACT', 'PROMO_DIM') ORDER BY 1";
		
		if (hmInput.get(AppConstants.COLTYPE) != null && AppConstants.ACTION.equalsIgnoreCase(hmInput.get(AppConstants.COLTYPE).toString())) {
			sql = "select distinct COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME in ('PROMOTION_FACT', 'PROMO_DIM') ORDER BY 1";
		}
		
		List<Object[]> list = getSessionFactory().getCurrentSession().createSQLQuery(sql).list();
		return list;
	}

	public void addBusinessRule(Map<String, Object> inputParams) {
		
		JsonObject jo = (JsonObject) inputParams.get(AppConstants.EDITDATA);
		String ruleName = jo.get(AppConstants.RULE_NAME).getAsString();
		String ruleDescription = jo.get(AppConstants.DESCRIPTION).getAsString();
		String username = inputParams.get(AppConstants.USERNAME).toString();
		
		insertIntoBRMeta(ruleName, ruleDescription, username);
		insertIntoBRCriteria(jo, username);
	}
	
	private void insertIntoBRCriteria (JsonObject jo, String username) {
		String ruleName = jo.get(AppConstants.RULE_NAME).getAsString();
		
		String sql = "DELETE FROM PRS.BUSINESS_RULES WHERE RuleName = :" + AppConstants.RULE_NAME;
		Session session = getSessionFactory().getCurrentSession();
		Query qry = session.createSQLQuery(sql);
		qry.setParameter(AppConstants.RULE_NAME, ruleName);
		qry.executeUpdate();
		
		JsonArray jaConstraints = jo.get(AppConstants.CONSTRAINT).getAsJsonArray();
		JsonArray jaActions = jo.get(AppConstants.ACTION).getAsJsonArray();
		int len = jaConstraints.size();
		if (len < jaActions.size()) {
			len = jaActions.size();
		}
		
		while (--len >= 0) {
			JsonObject joConstraint = null;
			JsonObject joAction = null;
			
			if (jaConstraints.size() > len && jaConstraints.get(len) != null)
				joConstraint = jaConstraints.get(len).getAsJsonObject();
			if (jaActions.size() > len && jaActions.get(len) != null)
				joAction = jaActions.get(len).getAsJsonObject();
			
			String insertSql = "INSERT INTO PRS.BUSINESS_RULES (RuleName, RuleColumn, RuleOperator, RuleValue, ActionColumn, ActionValue, CreatedDate, CreatedBy, UpdatedDate, UpdatedBy) "
					+ "VALUES (:" + AppConstants.RULE_NAME + ", "
							+ ":" + AppConstants.COLUMN + ", "
							+ ":" + AppConstants.CONDITION + ", "
							+ ":" + AppConstants.VALUE + ", "
							+ ":" + AppConstants.ACTION_COLUMN + ", "
							+ ":" + AppConstants.ACTION_VALUE + ", "
							+ "GetDate(), "
							+ ":" + AppConstants.CREATED_BY + ", "
							+ "GetDate(), "
							+ ":" + AppConstants.UPDATED_BY + ")";
			
			session = getSessionFactory().getCurrentSession();
			qry = session.createSQLQuery(insertSql);
			qry.setParameter(AppConstants.RULE_NAME, ruleName);
			
			if (joConstraint!= null) {
				qry.setParameter(AppConstants.COLUMN, joConstraint.get(AppConstants.COLUMN).getAsString());
				qry.setParameter(AppConstants.CONDITION, joConstraint.get(AppConstants.CONDITION).getAsString());
				qry.setParameter(AppConstants.VALUE, joConstraint.get("Value").getAsString());
			}
			else {
				qry.setParameter(AppConstants.COLUMN, AppConstants.BLANK);
				qry.setParameter(AppConstants.CONDITION, AppConstants.BLANK);
				qry.setParameter(AppConstants.VALUE, AppConstants.BLANK);
			}
			
			if (joAction != null) {
				qry.setParameter(AppConstants.ACTION_COLUMN, joAction.get(AppConstants.COLUMN).getAsString());
				qry.setParameter(AppConstants.ACTION_VALUE, joAction.get("Value").getAsString());
			}
			else {
				qry.setParameter(AppConstants.ACTION_COLUMN, AppConstants.BLANK);
				qry.setParameter(AppConstants.ACTION_VALUE, AppConstants.BLANK);
			}
			qry.setParameter(AppConstants.CREATED_BY, username);
			qry.setParameter(AppConstants.UPDATED_BY, username);
			
			qry.executeUpdate();
		}
	}
	
	private void insertIntoBRMeta (String ruleName, String ruleDescription, String username) {
		int iBRCnt = 0;
//		String sql = "DELETE FROM PRS.BUSINESS_RULES_META WHERE RuleShortDesc = :" + AppConstants.RULE_NAME;
		String sql = "SELECT COUNT(*) FROM PRS.BUSINESS_RULES_META WHERE RuleShortDesc = :" + AppConstants.RULE_NAME;
		Session session = getSessionFactory().getCurrentSession();
		Query qry = session.createSQLQuery(sql);
		qry.setParameter(AppConstants.RULE_NAME, ruleName);
		
		List list = qry.list();
		if (list != null && list.size() > 0) {
			iBRCnt = (int) list.get(0);
		}
//		qry.setParameter(AppConstants.RULE_NAME, ruleName);
//		qry.executeUpdate();
		
		sql = "insert into PRS.BUSINESS_RULES_META (RuleShortDesc, RuleDescription, CreatedBy, CreatedDate, UpdatedBy, UpdatedDate, IsDeleted, IsEnabled) "
				+ "VALUES ("
						+ ":" + AppConstants.RULE_NAME + ", "
						+ ":" + AppConstants.DESCRIPTION + ", "
						+ ":" + AppConstants.CREATED_BY + ", "
						+ "GetDate(), "
						+ ":" + AppConstants.UPDATED_BY + ", "
						+ "GetDate(), "
						+ ":" + AppConstants.ISDELETED + ", "
						+ ":" + AppConstants.ISENABLED + ")";
		
		if (iBRCnt > 0) {
			sql = "UPDATE PRS.BUSINESS_RULES_META "
					+ "SET RuleDescription = :" + AppConstants.DESCRIPTION + ", "
					+ "UpdatedBy = :" + AppConstants.UPDATED_BY + ", "
					+ "UpdatedDate = GetDate() "
					+ "WHERE RuleShortDesc = :" + AppConstants.RULE_NAME;
		}
		qry = session.createSQLQuery(sql);
		qry.setParameter(AppConstants.RULE_NAME, PEAUtils.escapeSql(ruleName));
		qry.setParameter(AppConstants.DESCRIPTION, PEAUtils.escapeSql(ruleDescription));
		qry.setParameter(AppConstants.UPDATED_BY, PEAUtils.escapeSql(username));
		
		if (iBRCnt < 1) {
			qry.setParameter(AppConstants.CREATED_BY, PEAUtils.escapeSql(username));
			qry.setParameter(AppConstants.ISDELETED, AppConstants.N);
			qry.setParameter(AppConstants.ISENABLED, AppConstants.Y);
		}
		qry.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getBusinessRuleColumns() {
		Map<String, Map<String,Object>> businessRulesMap = new HashMap<>();
		String sql = "select a.RuleID, a.RuleShortDesc, a.RuleDescription, b.RuleColumn from [PRS].[BUSINESS_RULES_META] a join [PRS].[BUSINESS_RULES] b on a.RuleShortDesc = b.RuleName";
		Session session = getSessionFactory().getCurrentSession();
		Query qry = session.createSQLQuery(sql);
		List<Object[]> list = qry.list();
		if (list != null && !list.isEmpty()) {
			for (Object[] objArr : list) {
				
				Map<String,Object> businessRuleData = businessRulesMap.getOrDefault(objArr[0].toString(), new HashMap<String, Object>());
				Set<String> ruleColumns = (Set<String>) businessRuleData.getOrDefault(AppConstants.RULE_COLUMN, new HashSet<String>());
				ruleColumns.add(objArr[3].toString());
				
				businessRuleData.put(AppConstants.RULE_ID, objArr[0]);
				businessRuleData.put(AppConstants.RULE_NAME, objArr[1].toString());
				businessRuleData.put(AppConstants.RULE_DESC, objArr[2].toString());
				businessRuleData.put(AppConstants.RULE_COLUMN, ruleColumns);
				businessRulesMap.put(businessRuleData.get(AppConstants.RULE_ID).toString(), businessRuleData);
			}
		}
		return businessRulesMap;
	}
	
	

}
