package com.saama.workbench.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public interface IDataTableService {
	List getData(Map<String, Object> inputParams) throws Exception;
	
	Map updateTable(Map<String, Object> inputParams) throws Exception;
	
	JsonObject getProcessedData(Map<String, Object> inputParams) throws Exception;
	
	List<String> getColumnDisplayNames(Map<String, Object> inputParams) throws Exception;
	
	List<String> getColumnDBNames(Map<String, Object> inputParams) throws Exception;
	
	Map deleteTableRow(Map<String, Object> inputParams) throws Exception;
	
	Map importIntoTable(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> getColumnDBDisplayNames(Map<String, Object> inputParams) throws Exception;

	Map<String, String> getColumnDisplayIdxMap(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> insertIntoTable(Map<String, Object> inputParams) throws Exception;

	Map<String, Object> savePromoMechanic(Map<String, Object> inputParams) throws Exception;
}
