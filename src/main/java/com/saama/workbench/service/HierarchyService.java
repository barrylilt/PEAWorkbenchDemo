package com.saama.workbench.service;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saama.workbench.bean.HierarchyNodeBean;
import com.saama.workbench.dao.HierarchyDao;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;

@Service
@Transactional
public class HierarchyService implements IHierarchyService {

	
	private static final Logger logger = Logger.getLogger(HierarchyService.class);
	
	@Autowired
	private HierarchyDao hierarchyDao;
	
	@Override
	public Map getData(Map<String, Object> hmInput) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<>();
		Map map = hierarchyDao.getHierarchyDataNodes(hmInput);
		List<HierarchyNodeBean> dataList = new ArrayList<>();
		HierarchyNodeBean node = null;
		
		if (map.get(AppConstants.RESPONSE) != null) {
			List <Object[]> list = (List<Object[]>) map.get(AppConstants.RESPONSE);
			for (Object[] obj : list) {
				node = new HierarchyNodeBean();
				node.setParentId(Integer.parseInt(obj[0].toString()));
				node.setParentCode((String) obj[1]);
				node.setParentName((String) obj[2]);
				node.setChildId(Integer.parseInt(obj[3].toString()));
				node.setChildCode((String) obj[4]);
				node.setChildName((String) obj[5]);
				node.setDistance(Integer.parseInt(obj[6].toString()));
				node.setChildLevel(Integer.parseInt(obj[7].toString()));
				
				try {
					if (obj[8] != null) {
						Blob b = (Blob)obj[8];
						node.setChildImg(PEAUtils.convertToBase64photo(b.getBytes(1, (int)b.length())));
					}
				}
				catch (Exception e) {
					logger.warn("Exception in Product Image", e);
				}
				
				dataList.add(node);
			}
			
			hmOutput.put(AppConstants.DATA, dataList);
		}
		
		return hmOutput;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map getInitData(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		
		List list = new ArrayList<>();
		Object cacheObj = null;
		
		Thread t1 = null, t2 = null;
		
		if (!PEAUtils.convertToBoolean(hmInput.get(AppConstants.FORCED))) {
			cacheObj = PEAUtils.getFromCacheMap(AppConstants.CACHE_LEVEL_PRODUCT_DATA);
		}
		if (cacheObj == null) {
			t1 = new Thread(() -> {
				try {
					Map map1 = hierarchyDao.getLevelProductSelectData(hmInput);
					hmOutput.put(AppConstants.PRODUCT, map1.getOrDefault(AppConstants.RESPONSE, null));
					PEAUtils.putIntoCacheMap(AppConstants.CACHE_LEVEL_PRODUCT_DATA, map1.getOrDefault(AppConstants.RESPONSE, null));
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			});
			t1.start();
		} 
		else {
			list = (List) cacheObj;
			hmOutput.put(AppConstants.PRODUCT, list);
		}
		
		if (!PEAUtils.convertToBoolean(hmInput.get(AppConstants.FORCED))) {
			cacheObj = PEAUtils.getFromCacheMap(AppConstants.CACHE_LEVEL_CUSTOMER_DATA);
		}
		if (cacheObj == null) {
			t2 = new Thread(() -> {
				try {
					Map map1 = hierarchyDao.getLevelCustomerSelectData(hmInput);
					hmOutput.put(AppConstants.CUSTOMER, map1.getOrDefault(AppConstants.RESPONSE, null));
					PEAUtils.putIntoCacheMap(AppConstants.CACHE_LEVEL_CUSTOMER_DATA, map1.getOrDefault(AppConstants.RESPONSE, null));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Exception", e);
				}
			});
			t2.start();
		} 
		else {
			list = (List) cacheObj;
			hmOutput.put(AppConstants.CUSTOMER, list);
		}
		
		if (t1 != null)
			t1.join();
		
		if (t2 != null)
			t2.join();
		
		return hmOutput;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> addIntoHierarchy(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		String type = hmInput.get(AppConstants.TYPE).toString();
		
		if (AppConstants.PRODUCT.equalsIgnoreCase(type)) {
			hmOutput = hierarchyDao.addIntoProductHierarchy(hmInput);
		}
		else if (AppConstants.CUSTOMER.equalsIgnoreCase(type)) {
			hmOutput = hierarchyDao.addIntoCustomerHierarchy(hmInput);
		}

		return hmOutput;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deleteHierarchy(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		String type = hmInput.get(AppConstants.TYPE).toString();
		
		if (AppConstants.PRODUCT.equalsIgnoreCase(type)) {
			hmOutput = hierarchyDao.deleteProductHierarchy(hmInput);
		}
		else if (AppConstants.CUSTOMER.equalsIgnoreCase(type)) {
			hmOutput = hierarchyDao.deleteCustomerHierarchy(hmInput);
		}

		return hmOutput;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> editHierarchy(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		
		String type = hmInput.get(AppConstants.TYPE).toString();
		
		if (AppConstants.PRODUCT.equalsIgnoreCase(type)) {
			hmOutput = hierarchyDao.editProductHierarchy(hmInput);
		}
		else if (AppConstants.CUSTOMER.equalsIgnoreCase(type)) {
			hmOutput = hierarchyDao.editCustomerHierarchy(hmInput);
		}

		return hmOutput;
	}

	@Override
	public Map<String, Object> getModel(Map<String, Object> hmInput)
			throws Exception {
		
		return null;
	}

	
	
}
