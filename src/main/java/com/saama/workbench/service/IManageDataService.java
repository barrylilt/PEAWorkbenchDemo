package com.saama.workbench.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.bean.BrandFormBean;
import com.saama.workbench.bean.CustDIMBean;
import com.saama.workbench.bean.MapBean;

@Service
public interface IManageDataService {

	boolean saveToAuditJobs(AuditJobsBean auditJobsBean) throws Exception;

	List getLookupTableSectors(Map<String, Object> input) throws Exception;

	List getLookupTableAccounts(Map<String, Object> input) throws Exception;

	Map<String, String> getIntProdList(Map<String, Object> input) throws Exception;

	Map<String, Object> saveMapping(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> getMappedProducts(Map<String, Object> inputParams) throws Exception;
	
	List<CustDIMBean> getIntCustList(Map<String, Object> input) throws Exception;

	Map<String, Object> saveCustMapping(Map<String, Object> inputParams) throws Exception;

	Map<String, String> getExtSourceList(Map<String, Object> input) throws Exception;

	List<MapBean> getUnMappedUniBrand(Map<String, Object> input) throws Exception;

	Map<String, Object> markExtBrandIgnored(Map<String, Object> input) throws Exception;
	
	Map<String, Object> unmapExistingMapping(Map<String, Object> input) throws Exception;

	Map<Integer, List<String>> getCategoryList(Map<String, Object> input) throws Exception;

	Map<String, Object> getClosestMatchProducts(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> getMappedCustomers(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> getClosestMatchCustomers(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> changeBusinessRuleStatus(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> getBRColumnList(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> addUpdateBusinessRule(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> mapExistingMapping(Map<String, Object> input) throws Exception;

}
