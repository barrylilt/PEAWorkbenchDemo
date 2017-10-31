package com.saama.workbench.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.MapBean;
import com.saama.workbench.bean.NameCodeDistanceBean;
import com.saama.workbench.dao.ViewDataDao;
import com.saama.workbench.model.AuditJobs;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;

@Service
@Transactional
public class ManageDataService implements IManageDataService {
	
    @Autowired
    private ViewDataDao viewDataDao;
	
	@Override
	public boolean saveToAuditJobs(AuditJobsBean auditJobsBean) {
		// TODO Auto-generated method stub
		boolean isSavedToAuditJobs = false;
		try{
			AuditJobs auditJobs = new AuditJobs();
			auditJobs.setFileName(auditJobsBean.getFileName());
			auditJobs.setLayer(auditJobsBean.getLayer());
			auditJobs.setProcessName(auditJobsBean.getProcessName());
			auditJobs.setLayer(auditJobsBean.getLayer());
			auditJobs.setSubjectArea(auditJobsBean.getSubjectArea());
			auditJobs.setCreatedDate(auditJobsBean.getCreatedDate());
			auditJobs.setJobName(auditJobsBean.getJobName());
			auditJobs.setStartTime(new Date());
			auditJobs.setEndTime(new Date());
			auditJobs.setStatus(auditJobsBean.getStatus());
			auditJobs.setCreatedBy(auditJobsBean.getCreatedBy());
			auditJobs.setSource(auditJobsBean.getSource());
			isSavedToAuditJobs = viewDataDao.saveToAuditJobs(auditJobs);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List getLookupTableSectors(Map<String, Object> input) {
		return viewDataDao.getLookupTableSectors(input);
	}

	@Override
	public List getLookupTableAccounts(Map<String, Object> input) {
		return viewDataDao.getLookupTableAccounts(input);
	}

	@Override
	public Map<String, String> getIntProdList(Map<String, Object> input) {
		
		List<Object[]> list = viewDataDao.getIntProductList(input);
		Map<String, String> map = new LinkedHashMap<String, String>();
//		Map<String, BrandFormBean> map = new TreeMap<String, BrandFormBean>();
//		Map<String, List<BrandFormBean>> outMap = new HashMap<String, List<BrandFormBean>>();
		
		for (Object[] o : list) {
			if (o[0] != null && o[1] != null && o[2] != null )
				map.put(o[0].toString(), o[2].toString() + " (" + o[1].toString() + ")");
		}
		
//		List<Object[]> list = viewDataDao.getIntBrandFormList(input);
//		Map<String, BrandFormBean> map = new TreeMap<String, BrandFormBean>();
//		Map<String, List<BrandFormBean>> outMap = new HashMap<String, List<BrandFormBean>>();
//		
//		for (Object[] o : list) {
//			if (o[0] != null) {
//				BrandFormBean b = map.getOrDefault(o[0].toString(), new BrandFormBean(o[0].toString()));
//				List<String> formList = b.getValue();
//				if (formList == null) {
//					formList = new ArrayList<String>();
//				}
//				if (o[1] != null)
//					formList.add(o[1].toString());
//				b.setValue(formList);
//				map.put(b.getKey(), b);
//			}
//		}
//		
//		List<Object[]> list1 = viewDataDao.getIntFormSubformList(input);
//		Map<String, BrandFormBean> map1 = new TreeMap<String, BrandFormBean>();
//		for (Object[] o : list1) {
//			if (o[0] != null) {
//				BrandFormBean c = map1.getOrDefault(o[0].toString(), new BrandFormBean(o[0].toString()));
//				List<String> subformList = c.getValue();
//				if (subformList == null) {
//					subformList = new ArrayList<String>();
//				}
//				if (o[1] != null)
//					subformList.add(o[1].toString());
//				
//				c.setValue(subformList);
//				map1.put(c.getKey(), c);
//			}
//		}
//		outMap.put("Product", new ArrayList<BrandFormBean> (map.values()));
//		outMap.put("BrandForm", new ArrayList<BrandFormBean> (map1.values()));
		
		return map;
	}

	@Override
	public Map<String, Object> saveMapping(Map<String, Object> inputParams) throws Exception {
		String prodSeparator = "!\\|";
		String separator = "\\|";
		
		Map<String, Object> out = new HashMap<String, Object>();
		out.put(AppConstants.SUCCESS, true);
		
		String intProduct = inputParams.getOrDefault(AppConstants.INT_PRODUCT, "").toString();
		String extProdId = inputParams.getOrDefault(AppConstants.EXT_PRODID, "").toString();
		
		List<Map<String, String>> prodList = new ArrayList<Map<String,String>>();
		
		for (String prod : intProduct.split(prodSeparator)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(AppConstants.PRODUCTID, prod.split(separator)[0]);
//			map.put(AppConstants.BRAND, prod.split(separator)[0]);
//			map.put(AppConstants.FORM, prod.split(separator)[1]);
//			map.put(AppConstants.SUBFORM, prod.split(separator)[2]);
			map.put(AppConstants.WEIGHTAGE, prod.split(separator)[1]);
			prodList.add(map);
		}
		inputParams.put(AppConstants.MAPPING_DATA, prodList);
		
		if (PEAUtils.isEmpty(intProduct) || PEAUtils.isEmpty(extProdId) || PEAUtils.isEmpty(inputParams.getOrDefault(AppConstants.SOURCEID, "").toString())) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, "Failed due to insufficient parametes");
		}
		else {
			viewDataDao.saveProdMapping(inputParams);
		}
		
		return out;
	}

	@Override
	public Map<String, Object> getMappedProducts(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> outMap = new HashMap<String, Object>();
		List<Object[]> list = viewDataDao.getMappedProducts(inputParams);
		outMap = getClosestMatchProducts(inputParams);
		outMap.put(AppConstants.MAPDATA, list);
		return outMap;
	}
	
	@Override
	public Map<String, Object> getMappedCustomers(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> outMap = new HashMap<String, Object>();
		List<Object[]> list = viewDataDao.getMappedCustomers(inputParams);
		outMap = getClosestMatchCustomers(inputParams);
		outMap.put(AppConstants.MAPDATA, list);
		return outMap;
	}
	
	@Override
	public Map<String, Object> getClosestMatchCustomers(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> outMap = new HashMap<String, Object>();
		List<Object[]> alExtCust = viewDataDao.getExtCustomer(inputParams);
		List<NameCodeDistanceBean> intCustomers = new ArrayList<NameCodeDistanceBean>();
		
		List<Object[]> extProd = viewDataDao.getExtCustomer(inputParams);
		List<Object[]> alIntCustomers = viewDataDao.getIntCustomerList(inputParams);
		
		inputParams.put(AppConstants.EXT_ITEM_OBJECT, alExtCust);
		inputParams.put(AppConstants.INT_ITEMS, alIntCustomers);
		inputParams.put(AppConstants.EXT_ITEM_INDEX, 1);
		inputParams.put(AppConstants.MAX_ITEMS, 1);

		
//		int maxProd = Integer.parseInt(inputParams.getOrDefault(AppConstants.MAX_PRODUCTS, "-1").toString());
//		if (maxProd < 0)
//			maxProd = 3;
//		
//		if (alExtCust != null && alExtCust.size() > 0) {
//			for (Object[] obj : alExtCust) {
//				if (obj[1] != null) {
//					String sExtCustName = obj[1].toString();
//					List<Object[]> list = viewDataDao.getIntCustomerList(inputParams);
//					for (Object[] obj1 : list) {
//						if (obj1!= null && obj1.length > 2 && obj1[2] != null) {
//							String intCustName = obj1[2].toString();
////							int distance = StringUtils.getLevenshteinDistance(sExtCustName, intCustName);
//							int distance = PEAUtils.calDistanceBtwnString(sExtCustName, intCustName);
//							NameCodeDistanceBean nameCodeDistanceBean = new NameCodeDistanceBean(distance, intCustName, obj1[1].toString(), obj1[0].toString());
//							
//							intCustomers.add(nameCodeDistanceBean);
//						}
//					}
//				}
//				
//				intCustomers.sort(new Comparator<NameCodeDistanceBean>() {
//					@Override
//					public int compare(NameCodeDistanceBean o1, NameCodeDistanceBean o2) {
//						if (o1 == null || o2 == null) 
//							return 0;
//						else if (o1.getDistance() > o2.getDistance()) {
//							return -1;
//						}
//						else if (o1.getDistance() < o2.getDistance()) {
//							return 1;
//						}
//						return 0;
//					}
//				});
//				break;
//			}
//		}
//		
//		if (intCustomers.size() > 0) {
//			List<NameCodeDistanceBean> suggestedCustomers = new ArrayList<NameCodeDistanceBean>(intCustomers.subList(0, (intCustomers.size() >= maxProd ? maxProd : intCustomers.size())));
//			
////			suggestedCustomers.sort(new Comparator<NameCodeDistanceBean>() {
////				@Override
////				public int compare(NameCodeDistanceBean o1, NameCodeDistanceBean o2) {
////					return (-1) * o1.getName().compareToIgnoreCase(o2.getName());
////				}
////			});
//			
//			outMap.put(AppConstants.SUGGESTED_CUSTOMERS, suggestedCustomers);
//		}
		
		List<?> x = PEAUtils.getSuggestedItems(inputParams);
		if (x.size() > 0) {
			outMap.put(AppConstants.SUGGESTED_CUSTOMERS, x);
		}
		
		return outMap;
	}
	
	@Override
	public Map<String, Object> getClosestMatchProducts(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> outMap = new HashMap<String, Object>();
		List<Object[]> extProd = viewDataDao.getExtProduct(inputParams);
		List<Object[]> alIntProducts = viewDataDao.getIntProductList(inputParams);
		
		inputParams.put(AppConstants.EXT_ITEM_OBJECT, extProd);
		inputParams.put(AppConstants.INT_ITEMS, alIntProducts);
		inputParams.put(AppConstants.EXT_ITEM_INDEX, 3);
		inputParams.put(AppConstants.MAX_ITEMS, 1);
		
//		List<NameCodeDistanceBean> intProducts = new ArrayList<NameCodeDistanceBean>();
//		
//		int maxProd = Integer.parseInt(inputParams.getOrDefault(AppConstants.MAX_PRODUCTS, "1").toString());
//		if (maxProd < 0) {
//			maxProd = 3;
//		}
//		
//		if (extProd != null && extProd.size() > 0) {
//			for (Object[] obj : extProd) {
//				if (obj[1] != null) {
//					// Used ExtProductNameNonUnilever
//					String extProdName = obj[3].toString();
//					List<Object[]> list = viewDataDao.getIntProductList(inputParams);
//					for (Object[] obj1 : list) {
//						if (obj1!= null && obj1.length > 2 && obj1[2] != null) {
////							int distance = StringUtils.getLevenshteinDistance(extProdName, obj1[2].toString());
//							int distance = PEAUtils.calDistanceBtwnString(extProdName, obj1[2].toString());
//							intProducts.add(new NameCodeDistanceBean(distance, obj1[2].toString(), obj1[1].toString(), obj1[0].toString()));
//						}
//					}
//				}
//				
//				intProducts.sort(new Comparator<NameCodeDistanceBean>() {
//					@Override
//					public int compare(NameCodeDistanceBean o1, NameCodeDistanceBean o2) {
//						if (o1 == null || o2 == null)
//							return 0;
//						else if (o1.getDistance() > o2.getDistance()) {
//							return -1;
//						}
//						else if (o1.getDistance() < o2.getDistance()) {
//							return 1;
//						}
//						return 0;
//					}
//				});
//				break;
//			}
//		}
//		
//		if (intProducts.size() > 0) {
//			outMap.put(AppConstants.SUGGESTED_PRODUCTS, intProducts.subList(0, (intProducts.size() >= maxProd ? maxProd : intProducts.size())));
//		}
		
		List<?> x = PEAUtils.getSuggestedItems(inputParams);
		if (x.size() > 0) {
			outMap.put(AppConstants.SUGGESTED_PRODUCTS, x);
		}
		
		return outMap;
	}

	@Override
	public Map<String, Object> saveCustMapping(Map<String, Object> inputParams) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> out = new HashMap<String, Object>();
		try{
			out = viewDataDao.saveCustMapping(inputParams);
			
		}
		catch(Exception e){
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
			throw e;
		}
		
		return out;
	}

	@Override
	public List<CustDIMBean> getIntCustList(Map<String, Object> input) {
		Map<String, String> hmCust = new HashMap<String, String>();
		List<CustDIMBean> list = viewDataDao.getIntCustList(input);
		return list;
	}

	@Override
	public Map<String, String> getExtSourceList(Map<String, Object> input) {
		return viewDataDao.getExtSourceList(input);
	}

	@Override
	public List<MapBean> getUnMappedUniBrand(Map<String, Object> input) {
		 List<String> list = viewDataDao.getUnMappedBrandList(input);
		 List<MapBean> outMap = new ArrayList<MapBean>();
		 
		 for (String s : list) {
			 outMap.add(new MapBean(s, s));
		 }
		 
		 outMap.sort(new Comparator<MapBean>() {
			@Override
			public int compare(MapBean o1, MapBean o2) {
				return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
			}
		 });
		 
		 return outMap;
	}

	@Override
	public Map<String, Object> markExtBrandIgnored(Map<String, Object> input) throws Exception {
		
		viewDataDao.markExtBrandIgnored(input);
		
		return null;
	}

	@Override
	public Map<String, Object> unmapExistingMapping(Map<String, Object> input) throws Exception {
		
		if (input.get(AppConstants.EXT_PRODID) != null) {
			viewDataDao.unmapProduct(input);
		}
		
		if (input.get(AppConstants.EXT_CUSTID) != null) {
			viewDataDao.unmapCustomer(input);
		}
		
		return null;
	}
	
	@Override
	public Map<String, Object> mapExistingMapping(Map<String, Object> input) throws Exception {
		
		if (input.get(AppConstants.EXT_PRODID) != null) {
			viewDataDao.mapProduct(input);
		}
		
		if (input.get(AppConstants.EXT_CUSTID) != null) {
			viewDataDao.mapCustomer(input);
		}
		
		return null;
	}

	@Override
	public Map<Integer, List<String>> getCategoryList(Map<String, Object> input) throws Exception {
		List<Object[]> categoryList =  new ArrayList<Object[]>();
		
			categoryList = viewDataDao.getCategoryList(input);
			Map<Integer, List<String>> outMap = new HashMap<Integer, List<String>>();
			 
			for (Object[] s : categoryList) {
				if (s != null && s.length > 1) {
					List<String> a = outMap.get(s[0]);
					
					if (a == null) {
						a = new ArrayList<String>();
						a.add(s[1].toString());
					}
					Collections.sort(a);
					outMap.put((Integer) s[0], a);
				}
			 }
			 
//			 outMap.sort(new Comparator<MapBean>() {
//				@Override
//				public int compare(MapBean o1, MapBean o2) {
//					return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
//				}
//			 });
			 
			 
		
		return outMap;
	}
	
	public Map<String, Object> changeBusinessRuleStatus(
			Map<String, Object> inputParams) throws Exception {
		
		Map<String, Object> out = new HashMap<>();
		out.put(AppConstants.SUCCESS, true);
		out.put(AppConstants.MESSAGE, "Business Rule is " + inputParams.get(AppConstants.STATUS) + "d successfully!");
		
		try {
			viewDataDao.changeBusinessRuleStatus(inputParams);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
		}

		return out;
	}
	
	public Map<String, Object> getBRColumnList(Map<String, Object> inputParams) throws Exception {
		Map<String, Object> out = new HashMap<>();
		
		out.put(AppConstants.SUCCESS, true);
		out.put(AppConstants.MESSAGE, "");
		
		try {
			@SuppressWarnings("unchecked")
			List<String> colList = (List<String>) viewDataDao.getBRColumnList(inputParams);
			out.put(AppConstants.COLUMNS, colList);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
		}

		return out;
	}
	
	public Map<String, Object> addUpdateBusinessRule(Map<String, Object> inputParams) throws Exception{
		Map<String, Object> out = new HashMap<>();
		
		out.put(AppConstants.SUCCESS, true);
		out.put(AppConstants.MESSAGE, "Business Rule Updated Successfully");
		
		try {
			viewDataDao.addBusinessRule(inputParams);
		} catch (Exception e) {
			out.put(AppConstants.SUCCESS, false);
			out.put(AppConstants.MESSAGE, e.getMessage());
		}

		return out;
	}

}
