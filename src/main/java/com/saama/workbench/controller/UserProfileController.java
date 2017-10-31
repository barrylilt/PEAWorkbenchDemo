package com.saama.workbench.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.saama.workbench.bean.UserProfileBean;
import com.saama.workbench.model.UserProfile;
import com.saama.workbench.service.IUserProfileService;
import com.saama.workbench.util.AppConstants;

@Controller
public class UserProfileController {

	private static final Logger logger = Logger.getLogger(UserProfileController.class);
	
	@Autowired
	IUserProfileService UserProfileService;
	
	@RequestMapping(value = "/userProfile/getinfo", method = RequestMethod.GET)
	public void userProfileGetinfo(ModelMap model, HttpSession session,HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserProfile userdata = null;
		try {
			String username= (String) session.getAttribute(AppConstants.USERNAME);
			userdata = new UserProfile();
			userdata = UserProfileService.getUserInfo(username);
			Gson gson = new Gson(); 
			String json = gson.toJson(userdata); 
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
		} catch (Exception e) {
			logger.error("Exception in userProfileGetInfo", e);
		}

	}
	
	@RequestMapping(value = "/userProfile/update", method = RequestMethod.POST)
	public void userProfileUpdate(ModelMap model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			UserProfileBean userdata) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<>();

		try {
			UserProfile userProfile = UserProfileService.updateUserInfo(userdata);
			session.setAttribute(AppConstants.USER_OBJECT, userProfile);
			session.setAttribute(AppConstants.DATE_FORMAT_UI, userProfile.getDateFormatUI());
			session.setAttribute(AppConstants.LANGUAGE, userProfile.getLanguage());
			
			hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
			hmOutput.put(AppConstants.MESSAGE, "Profile is updated successfully");
			hmOutput.put(AppConstants.USER_OBJECT, userProfile);
			
		} catch (Exception e) {
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FALSE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			
			logger.error("Exception in userProfileUpdate", e);
		}
		
		PrintWriter out = response.getWriter();
		out.print(new Gson().toJson(hmOutput));
		out.flush();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveUserSettings", method = RequestMethod.POST)
	public void saveUserSettings(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> hmInput = new HashMap<>();
		Map<String, Object> hmOutput = new HashMap<>();
		
		hmInput.put(AppConstants.DATE_FORMAT, request.getParameter("dateFormat"));
		hmInput.put(AppConstants.CURRENCY_FORMAT, request.getParameter("currencyFormat"));
		hmInput.put(AppConstants.LANG_FORMAT, request.getParameter("langFormat"));
		hmInput.put(AppConstants.USERNAME, session.getAttribute(AppConstants.USERNAME));
		
		try {
			
			hmOutput = UserProfileService.saveUserSettings(hmInput);
			UserProfile profile = (UserProfile) hmOutput.get(AppConstants.USER_OBJECT);
			
			if (hmOutput.get(AppConstants.USER_OBJECT) != null) {
				session.setAttribute(AppConstants.USER_OBJECT, profile);
				session.setAttribute(AppConstants.DATE_FORMAT_UI, profile.getDateFormatUI());
			}
			
			hmOutput.put(AppConstants.SUCCESS, AppConstants.TRUE);
			hmOutput.put(AppConstants.MESSAGE, "User settings are saved successfully.");
		
		} catch (Exception e){
			hmOutput.put(AppConstants.SUCCESS, AppConstants.FAILURE);
			hmOutput.put(AppConstants.MESSAGE, e.getMessage());
			logger.error("Exception in storing theme", e);
		}
		
		PrintWriter out = response.getWriter();
		out.print(new Gson().toJson(hmOutput));
		out.flush();
	}
	
	@RequestMapping(value = "/storeUserTheme", method = RequestMethod.POST)
	public void storeUserTheme(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String theme = request.getParameter("val");
		String username = (String) session.getAttribute(AppConstants.USERNAME);
		
		try{
			UserProfileService.storeUserTheme(username,theme);
		} catch (Exception e){
			logger.error("Exception in storing theme", e);
		}
		
	}

}
