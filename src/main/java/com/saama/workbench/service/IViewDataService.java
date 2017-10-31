package com.saama.workbench.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.saama.workbench.bean.CategoryBrandBean;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.DataQualityReportBean;
import com.saama.workbench.bean.ProdDIMBean;
import com.saama.workbench.bean.TreeNode.Entry;
import com.saama.workbench.model.BusinessRuleMeta;
import com.saama.workbench.model.CustomerStats;
import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.model.SblCustomerHierarchy;
import com.saama.workbench.model.SblProductHierarchy;

@Service
public interface IViewDataService {
	
	Map<String, Object> getDisplayColumns(Map<String, Object> input) throws Exception;

	Map<String, Object> getDBColumns(Map<String, Object> input) throws Exception;
	
	Map<String, Object> getData(Map<String, Object> input) throws Exception;



	List<CustDIMBean> getCustomerList(Map<String, Object> input) throws Exception;

	List<ProdDIMBean> getProductList(Map<String, Object> input) throws Exception;

	Map getViewColumns(Map<String, Object> input) throws Exception;

	Map saveViewColumns(Map<String, Object> input) throws Exception;

	Map<String, String> getDatasetList(Map<String, Object> input) throws Exception;

	List<CustomerStats> getAccountList(Map<String, Object> input) throws Exception;

	Map<String, String> getBusinessRuleList(Map<String, Object> input) throws Exception;

	List<CustomerStats> viewDataAvailability(Map<String, Object> input) throws Exception;

	Map<String,Object> getDataQualityReport();

	Map<String, String> getPromotionList(Map<String, Object> input) throws Exception;

	Map<String, DatasetMeta> getDatasetObjectList(Map<String, Object> input) throws Exception;

	Map<String, String> getBusinessRuleDescMap(Map<String, Object> input) throws Exception;
	
	Map<String, String> getOverLappingPromotions(String PromoId) throws Exception;

	List<BusinessRuleMeta> getBusinessRuleBeanList(Map<String, Object> input) throws Exception;

	List<CategoryBrandBean> getProdCategoryBrandList(Map<String, Object> input) throws Exception;	

	List<Object[]> getHierarchiesSQL(String type) throws Exception;

	String getHierarchyJsonString(String type) throws Exception;

	List<String> getCustomerGroups() throws Exception;

	Map<String, String> getExtSourceList(Map<String, Object> input) throws Exception;

	List<SblProductHierarchy> getProductHierarchies() throws Exception;

	List<SblCustomerHierarchy> getCustomerHierarchies() throws Exception;

	Map<String, Object> promotionPeriodAlignmentData(Map<String,String> input) throws Exception;

	Map<String, Object> promotionPeriodAlignmentcusNprodData(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> saveEditData(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> updateProcessPromotionList(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> saveOverrideChanges(Map<String, Object> hmInput);

	Map<String, Set<String>> getDataQualityStatusMeta();

	Map<String, Object> saveDataQualityStatus(Map<String, Object> hmInput);


}
