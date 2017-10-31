package com.saama.workbench.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface ITableDisplayService {
	List<String> getTableColumns(String tableName,String schName);

	List getTableData(String tableName, String columns, String datasetId, String countryCode);

	void updatePrePostProduction(String data) throws Exception;
	
	Map<Integer, String> getTableNumericColumns(String tableName, String schName, List<String> columns);
}
