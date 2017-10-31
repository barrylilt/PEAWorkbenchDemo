package com.saama.workbench.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saama.workbench.bean.DatasetBean;

@Service
public interface IManageDatasetService {

	DatasetBean createDataset(DatasetBean datasetBean) throws Exception;

	List<DatasetBean> listDataSetBean();

	boolean deleteDataset(Long datasetId) throws Exception;

	boolean makeDatasetInActive(Long datasetId) throws Exception;

	String getTicket() throws Exception;
	
	
	

}
