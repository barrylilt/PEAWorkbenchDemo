package com.saama.workbench.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface IHierarchyService {

	Map getData(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> getInitData(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> getModel(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> addIntoHierarchy(Map<String, Object> hmInput) throws Exception;
	
	Map<String, Object> deleteHierarchy(Map<String, Object> hmInput) throws Exception;

	Map<String, Object> editHierarchy(Map<String, Object> hmInput) throws Exception;

}
