package com.saama.workbench.controller;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.saama.workbench.bean.AuditJobsBean;
import com.saama.workbench.bean.DatasetBean;
import com.saama.workbench.bean.LoginModel;
import com.saama.workbench.bean.UserProfileBean;
import com.saama.workbench.model.AuthRole;
import com.saama.workbench.model.CustomerStats;
import com.saama.workbench.model.UserProfile;
import com.saama.workbench.service.IDashboardService;
import com.saama.workbench.service.IManageDatasetService;
import com.saama.workbench.service.IMonitorJobService;
import com.saama.workbench.service.IUserProfileService;
import com.saama.workbench.service.IViewDataService;
import com.saama.workbench.util.ActiveDirectoryAuthentication;
import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;


/**
 * @author : Saama Technologies Inc.
 * @class : MainController
 */
@Controller
//@Scope("session")
public class MainController implements Serializable {

	private static final Logger logger = Logger.getLogger(MainController.class);
	
	@Autowired
	IDashboardService dashboardService;
	
	@Autowired
	IManageDatasetService manageDatasetService;
	@Autowired
	IViewDataService viewDataService;
	@Autowired
	IMonitorJobService monitorJobService;
	
	@Autowired
	IUserProfileService userProfileService;
	/*
	 * @method : mainPage
	 */
	
	
	
	
	
	@RequestMapping(value = { "/dashboard", "/home" }, method = RequestMethod.GET)
	public ModelAndView mainPage(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mav = new ModelAndView("page");
		String theme = "skin-blue";
		try {
			if (session.getAttribute(AppConstants.USER_OBJECT) != null) {
				UserProfile userProfile = (UserProfile) session.getAttribute(AppConstants.USER_OBJECT);
				theme = userProfile.getTheme();
			}

			mav.addObject("theme", theme);
			
		} catch (Exception e) {
			logger.error("Exception in mainPage", e);
		}

		return mav;

	}
	
	
	
	
		
	
	
	
	
	@RequestMapping(value = {"/dashboard/data", "/home/data"}, method = RequestMethod.GET)
	public void mainPagedata(ModelMap model, HttpSession session, HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> outParams = new HashMap<String, Object>();
		Map<String, Object> hmInput = new HashMap<String, Object>();
		
		Thread t1 = new Thread() {
			public void run() {
				List<DatasetBean> listDataSetBean = new ArrayList<DatasetBean>();
				listDataSetBean = manageDatasetService.listDataSetBean();
				if (!listDataSetBean.isEmpty() && listDataSetBean.size() > 0) {
					outParams.put("datasets", listDataSetBean);
				}
			};
		};

		Thread t2 = new Thread() {
			public void run() {
				List<CustomerStats> listCustomerStats = new ArrayList<CustomerStats>();
				try {
					listCustomerStats = viewDataService.viewDataAvailability(null);
				} catch (Exception e) {
					logger.error("Exception in viewDataService.viewDataAvailability", e);
				}
				if (!listCustomerStats.isEmpty() && listCustomerStats.size() > 0) {
					outParams.put("dataAvailabilityStatistics", listCustomerStats);
				}
			}
		};

		Thread t3 = new Thread() {
			public void run() {
				List<AuditJobsBean> lstAuditProcessView = new ArrayList<AuditJobsBean>();
				hmInput.put(AppConstants.DATE_FORMAT, PEAUtils.getDateFormat(AppConstants.DASHBOARD_JOBRUNSTATISTICS));
				lstAuditProcessView = monitorJobService.getJobRunStatistics(hmInput);

				if (!lstAuditProcessView.isEmpty() && lstAuditProcessView.size() > 0) {
					outParams.put("jobRunStatistics", lstAuditProcessView);
				}

			}
		};

		Thread t4 = new Thread() {
			public void run() {
				Map<String, Object> dataQualityReport = new HashMap<String, Object>();
				dataQualityReport = viewDataService.getDataQualityReport();
				if (!dataQualityReport.isEmpty() && dataQualityReport.size() > 0) {
					// dataQuality.put(AppConstants.DataQualityReport, dataQualityReport);
					outParams.put(AppConstants.DataQualityReport, dataQualityReport);
				}
			}
		};
		
		t1.start();
		t2.start();
		t4.start();
		t3.run();
		
		try {
			t1.join();
			t2.join();
//			t3.join();
			t4.join();
		} catch (InterruptedException e) {
			logger.error("Exception in dashboard/data Thread join", e);
		}
		

		try {
			PrintWriter out = response.getWriter();
			out.print(new Gson().toJson(outParams));
			out.flush();
		} catch (IOException e) {
			logger.error("Exception in Dashboard/data", e);
		}

	}

	@RequestMapping(value = "/{page}", method = RequestMethod.GET)
	public String navigatePage(ModelMap model, HttpSession session,
			HttpServletRequest request, @PathVariable("page") String page) {
		
		try {
			session.invalidate();
		} catch (Exception e) {
			logger.error("Exception in navigatePage", e);
		}
		
		return page;
	}
	
	@RequestMapping(value = "/sessionInvalidate", method = RequestMethod.GET)
	public void sessionInvalidate(ModelMap model,
			HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		try {
			if(session!=null && request.isRequestedSessionIdValid()) {
				System.out.println("Session Invalidated successfully");
				session.invalidate();
			}
		} catch (Exception e) {
			logger.error("Exception in navigatePage", e);
		}
	}
	
	/*@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView home(ModelMap model,
			HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("login");
		ModelAndView dashboardMAV = new ModelAndView("/dashboard");
		String userName = request.getParameter("user");
		String password = request.getParameter("password");
		
		try {
			if (userName != null && password != null && userName.equalsIgnoreCase(password))
				return dashboardMAV;
			return mav;
		} catch (Exception e) {
			logger.error("Exception in navigatePage", e);
			throw e;
		}
	}*/
	
	@RequestMapping(value = "/getVersion", method = RequestMethod.GET)
	public void getVersion(ModelMap model, HttpServletRequest request, HttpSession session, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.print("Latest SVN Revision - " + AppConstants.DEPLOYED_ENV.toUpperCase() + "." + PropertiesUtil.getProperty(AppConstants.COMMON_SETTINGS_LASTEST_SVN_VERSION));
		out.flush();
	}
	
	@RequestMapping(value = {"/login"}, method = RequestMethod.POST)
	public String login(ModelMap model, HttpServletRequest request, HttpSession session, HttpServletResponse response) throws Exception {
		
		String username = null, language = "en";
		session.setAttribute(AppConstants.LOGOUT_URL, "logout");
		
		if (PEAUtils.convertToBoolean(PropertiesUtil.getProperty(AppConstants.SAML_ENABLED))) {
			try {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				LoginModel loginModel = ((LoginModel)authentication.getDetails());
				
				if (loginModel != null && loginModel.getUsername() != null) {
					logger.debug("logged in User Full Name - " + loginModel.getFullname());
					logger.debug("logged in Username - " + loginModel.getUsername());
					logger.debug("logged in User Principal - " + authentication.getPrincipal());
					logger.debug("logged in User Groups - " + loginModel.getGroups());
					
					username = loginModel.getUsername();
					
					UserProfile userprofile = userProfileService.getUserInfo(username);
					
					if (userprofile == null || PEAUtils.isEmpty(userprofile.getUserName())) {
						UserProfileBean userProfileBean = new UserProfileBean();
						userProfileBean.setUserName(username);
						userProfileBean.setFirstName(username);
						userProfileBean.setLastName(username);
						userProfileBean.setLanguage(language);
						userprofile = userProfileService.addNewUserIntoUserProfile(userProfileBean);
					}
					
					session.setAttribute(AppConstants.USER_OBJECT, userprofile);
					session.setAttribute(AppConstants.USERNAME, userprofile.getUserName());
					session.setAttribute(AppConstants.USERPHOTO, userprofile.getBase64photo());
					session.setAttribute(AppConstants.LANGUAGE, userprofile.getLanguage());
					session.setAttribute(AppConstants.DATE_FORMAT_UI, userprofile.getDateFormatUI());
					if (loginModel.getGroups() != null) {
						session.setAttribute(AppConstants.LDAPGROUP, loginModel.getGroups());
					}
					
					// SAML Specific logout
					session.setAttribute(AppConstants.LOGOUT_URL, "saml/logout");
					return "redirect:/dashboard";
				}
			}
			catch(Exception e) {
				logger.error("Exception in Login", e);
			}
			
			return "redirect:/error";
		}
		else {
			
			String password = request.getParameter("password");
			username = request.getParameter("user");
			
			List<String> userGroups = new ArrayList<String>();	
			try {
				
				if (PEAUtils.convertToBoolean(PropertiesUtil.getPropertyOrDefault(AppConstants.COMMON_SETTINGS_WITHOUT_LOGIN, "false"))) {
					
					username = "DevUser";
					password = "demo";
//					session.setAttribute(AppConstants.USERNAME, "DevUser");
//					session.setAttribute(AppConstants.USERPHOTO, "");
//					session.setAttribute(AppConstants.LDAPGROUP, "default");
//					return "redirect:/dashboard";
				}
				
				if (PEAUtils.convertToBoolean(PropertiesUtil.getProperty(AppConstants.DB_AUTH_ENABLED))) {
					
					UserProfile userprofile = userProfileService.getUserInfo(username);					
					if (!PEAUtils.convertToBoolean(userprofile.getIsActive()) || !userprofile.getPassword().equals(password)) {
						session.setAttribute("invalidUser", "Username or Password is incorrect");
						return "login";
					}
					
					StringBuffer userGrps = new StringBuffer();
					for (AuthRole ar : userprofile.getRole()) {
						if (userGrps.length() > 0) {
							userGrps.append(",");
						}
						userGrps.append(ar.getRoleName());
					}
					
					session.setAttribute(AppConstants.USER_OBJECT, userprofile);
					session.setAttribute(AppConstants.USERNAME, userprofile.getUserName());
					session.setAttribute(AppConstants.USERPHOTO, userprofile.getBase64photo());
					session.setAttribute(AppConstants.LDAPGROUP, userGrps.toString());
					session.setAttribute(AppConstants.LANGUAGE, userprofile.getLanguage());
					session.setAttribute(AppConstants.DATE_FORMAT_UI, userprofile.getDateFormatUI());
					return "redirect:/dashboard";
				}
				else if (username != null 
						&& password != null 
						&& username.length() > 0 
						&& password.length() > 0 
						&& ActiveDirectoryAuthentication.chkLDAPAuthentication(username, password)) {
					
					UserProfile userprofile = userProfileService.getUserInfo(username);
					userGroups = new ArrayList<String>();				
					userGroups = ActiveDirectoryAuthentication.getGroups(username, password);
					if (userprofile == null || PEAUtils.isEmpty(userprofile.getUserName())) {
						UserProfileBean userProfileBean = new UserProfileBean();
						userProfileBean.setUserName(username);
						userProfileBean.setFirstName(username);
						userProfileBean.setLastName(username);
						userProfileBean.setLanguage(language);
						userprofile = userProfileService.addNewUserIntoUserProfile(userProfileBean);
					}
					
					session.setAttribute(AppConstants.USER_OBJECT, userprofile);
					session.setAttribute(AppConstants.USERNAME, userprofile.getUserName());
					session.setAttribute(AppConstants.LDAPGROUP, userGroups);
					session.setAttribute(AppConstants.USERPHOTO, userprofile.getBase64photo());
					session.setAttribute(AppConstants.LANGUAGE, userprofile.getLanguage());
					session.setAttribute(AppConstants.DATE_FORMAT_UI, userprofile.getDateFormatUI());
					return "redirect:/dashboard";
					
				}
				
				session.setAttribute("invalidUser", "Username or Password is incorrect");
				return "login";
				
				
			} catch (Exception e) {
				logger.error("Exception in navigatePage", e);
				throw e;
			}
		
		}
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		
		try {
			if(session!=null && request.isRequestedSessionIdValid()) {
				System.out.println("Session Invalidated successfully");
				session.invalidate();
			}
		} catch (Exception e) {
			logger.error("Exception in navigatePage", e);
		}
		
//		if (PEAUtils.convertToBoolean(PropertiesUtil.getProperty(AppConstants.SAML_ENABLED))) 
//			return "redirect:/saml/logout";
		return "redirect:/login";
		
	}
	
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Error has been occurred!!");
		return "error";
	}
	
	@RequestMapping(value = "/getReportURL", method = RequestMethod.GET)
	public void getReportURL(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Map<String, Object> hmOutput = new HashMap<>();
		try {
			String reportURL = manageDatasetService.getTicket();
			
//			String reportURL = "http://52.205.98.239/trusted/3YVPjCjtPAdPp0ierDN6sbOb/t/CPGDemo/views/CPGEventAnalytics/CPGDemoHomePage?:embed=y&:showShareOptions=true&:display_count=no&:showVizHome=no"; 
			
			hmOutput.put(AppConstants.REPORT_URL, reportURL);
			hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
			hmOutput.put(AppConstants.MESSAGE, "URL is generated");
		} 
		catch (Exception e) {
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			
			logger.error("Exception in getReportURL - ", e);
		}
		
		PrintWriter printWriter = response.getWriter();
		printWriter.print(new Gson().toJson(hmOutput));
	    printWriter.flush();
	}
}