package com.saama.workbench.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saama.workbench.bean.DatasetBean;
import com.saama.workbench.dao.ManageDatasetDao;
import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.DateUtils;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Service
@Transactional
public class ManageDatasetService implements IManageDatasetService{
    
	private static Logger logger = Logger.getLogger(ManageDatasetService.class);
	
	@Autowired
	private ManageDatasetDao manageDatasetDao;
	
	@Override
	public boolean deleteDataset(Long datasetId) throws Exception {
		boolean isDeleted = false;
		
		isDeleted = manageDatasetDao.deleteDataset(datasetId);
		
		return isDeleted;
	}
	
	@Override
	public boolean makeDatasetInActive(Long datasetId) throws Exception {
		boolean isDeleted = false;
		
		isDeleted = manageDatasetDao.makeDatasetInActive(datasetId);
		
		return isDeleted;
	}
	
	@Override
	public DatasetBean createDataset(DatasetBean datasetBean) throws Exception {
		// TODO Auto-generated method stub
		
		try{
			DatasetMeta datasetMetaData = new DatasetMeta();
			datasetMetaData.setDatasetName(datasetBean.getDatasetName());
			datasetMetaData.setDatasetType(datasetBean.getDatasetType());
			
			datasetMetaData.setEndingDate(datasetBean.getEndingDate());
			datasetMetaData.setNotes(datasetBean.getNotes());
			datasetMetaData.setProcessStatus(datasetBean.getProcessStatus());
			datasetMetaData.setRunType(datasetBean.getRunType());
			datasetMetaData.setStartingDate(datasetBean.getStartingDate());
			datasetMetaData.setUpdatedBy(datasetBean.getUpdatedBy());
			datasetMetaData.setUpdatedDate(PEAUtils.getUpdateDate());
			datasetMetaData.setCountryCode(datasetBean.getCountryCode());
			datasetMetaData.setCreatedBy(datasetBean.getCreatedBy());
			datasetMetaData.setCreatedDate(PEAUtils.getUpdateDate());
			datasetMetaData.setCustomerMappingId(datasetBean.getCustomerMappingId());
			datasetMetaData.setProductMappingId(datasetBean.getProductMappingId());
			
			datasetMetaData = manageDatasetDao.createDataset(datasetMetaData);
			//datasetBean.setIsdatasetExist(datasetMetaData.getIsDatasetExist());
			datasetBean.setDatasetId(datasetMetaData.getDatasetId());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return datasetBean;
	}

	@Override
	public List<DatasetBean> listDataSetBean() {
		// TODO Auto-generated method stub
		List<DatasetBean> listDatasetBean =  new ArrayList<DatasetBean>();
		List<DatasetMeta> listDatasetMeta =  new ArrayList<DatasetMeta>();
		try{
			listDatasetMeta = manageDatasetDao.retriveListDatasets();
			for(DatasetMeta datasetMeta : listDatasetMeta){
				DatasetBean datasetBean = new DatasetBean();
				datasetBean.setDatasetType(datasetMeta.getDatasetType());
				datasetBean.setDatasetName(datasetMeta.getDatasetName());
				datasetBean.setDatasetId(datasetMeta.getDatasetId());
				datasetBean.setCreatedBy(datasetMeta.getCreatedBy());
				datasetBean.setCreatedDate(datasetMeta.getCreatedDate());
				listDatasetBean.add(datasetBean);
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return listDatasetBean;
	}

	@Override
	public String getTicket() throws Exception {
		String ticket = "-1", url = "";
		final String variable = PropertiesUtil.getProperty(AppConstants.DASHBOARD_NAME);
		final String ReportName = PropertiesUtil.getProperty(AppConstants.REPORT_NAME);
		final String user = "Demo_User";
		final String wgserver = PropertiesUtil.getProperty(AppConstants.REPORT_SERVER);
		final String publicIP = PropertiesUtil.getProperty(AppConstants.REPORT_PUBLIC_IP);
		final String dst = "views/Book1/Sheet1";
		final String params = ":embed=yes&:toolbar=yes";
		final String target_site = PropertiesUtil.getProperty(AppConstants.REPORT_TARGET_SITE);
		final String clientIP = PropertiesUtil.getProperty(AppConstants.REPORT_CLIENT_IP);

		ticket = getTrustedTicket(wgserver, user, clientIP, target_site);
		logger.info("###------------------ticket:" + ticket);
 
		url = "http://" + publicIP + "/trusted/"
				+ ticket
				+ "/t/"
				+ target_site
				+ "/views/"
				+ variable
				+ "/"+ReportName+"?"
				// + "User%20ID=" + PEAUtils.getUserId() // Removed as it is
				// not required for tableu
				// + "&"
				
				+ ":embed=y&:showShareOptions=true&:display_count=no&:showVizHome=no";
		
        System.out.println(url);
		logger.info(url);

		if (!ticket.equals("-1")) {
			// response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			// response.setHeader("Location", "http://" + wgserver +
			// "/trusted/" + ticket + "/" + dst + "?" + params);
			logger.info("###----------------- URL::" + "http://" + publicIP
					+ "/trusted/" + ticket + "/" + dst + "?" + params);
		} else
			// handle error
			throw new ServletException("Invalid ticket " + ticket);
		
		return url;
	}

	// the client_ip parameter isn't necessary to send in the POST unless you
	// have
	// wgserver.extended_trusted_ip_checking enabled (it's disabled by default)
	private String getTrustedTicket(String wgserver, String user, String clientIP, String target_site) throws Exception {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			// Encode the parameters
			StringBuffer data = new StringBuffer();
			data.append(URLEncoder.encode("username", "UTF-8"));
			data.append("=");
			data.append(URLEncoder.encode(user, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode("client_ip", "UTF-8"));
			data.append("=");
			data.append(URLEncoder.encode(clientIP, "UTF-8"));
			data.append("&");
			data.append(URLEncoder.encode("target_site", "UTF-8"));
			data.append("=");
			data.append(URLEncoder.encode(target_site, "UTF-8"));
			// Send the request
			logger.info("+++++++++++++++user" + user);
			logger.info(" remoteAddr new: " + clientIP);

			URL url = new URL("http://" + wgserver + "/trusted");
			URLConnection conn = url.openConnection();

			// HttpsURLConnection httpsCon = (HttpsURLConnection)
			// url.openConnection();
			// httpsCon.setHostnameVerifier(new HostnameVerifier()
			// {
			// public boolean verify(String hostname, SSLSession session)
			// {
			// return true;
			// }
			// });
			conn.setDoOutput(true);
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data.toString());
			out.flush();

			// Read the response
			StringBuffer rsp = new StringBuffer();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				rsp.append(line);
			}

			return rsp.toString();

		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				logger.error("Exception while connecting tableau server - getTrustedTicket :: Finally: ", e);
			}
		}
	}

}
