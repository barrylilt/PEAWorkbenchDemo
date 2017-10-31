package com.saama.workbench.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saama.workbench.bean.UserProfileBean;
import com.saama.workbench.dao.UserProfileDao;
import com.saama.workbench.model.UserProfile;
import com.saama.workbench.util.AppConstants;



@Service
@Transactional
public class UserProfileService implements IUserProfileService{
private static final Logger logger = Logger.getLogger(UserProfileService.class);
	
	@Autowired
	private UserProfileDao userProfileDao;
	
	@Override
	public UserProfile getUserInfo(String username) throws Exception {
		
		UserProfile userdata = null;
		try {
			 userdata = new UserProfile();
			 userdata = userProfileDao.getUserInfo(username);
		}
		catch(Exception e) {
			logger.error("Exception in getUserInfo - " + e.getMessage());
			throw e;
		}
		return userdata;
		
	
	}
	
	@Override
	public UserProfile updateUserInfo( UserProfileBean userProfileBean) throws Exception{
		
		UserProfile userData = userProfileDao.updateUserInfo(userProfileBean);
		
		userData.setDateFormatBK(userData.getDateFormatBK());
		userData.setDateFormatUI(userData.getDateFormatUI());
		
		return userData;
	}

	@Override
	public UserProfile addNewUserIntoUserProfile(UserProfileBean userProfile)
			throws Exception {
		UserProfile profile = new UserProfile();
		try {
			profile = userProfileDao.updateUserInfo(userProfile);
		} catch (Exception e) {
			logger.error("Exception in addNewUserIntoUserProfile - ", e);
			throw e;
		}
		return profile;
	}

	@Override
	public void storeUserTheme(String user,String theme) {
			
		userProfileDao.updateUserTheme(user,theme);
		
	}

	@Override
	public String getTheme(String username) {
		String theme = userProfileDao.getTheme(username);
		return theme;
	}

	@Override
	public Map saveUserSettings(Map<String, Object> hmInput) throws Exception{
		Map<String, Object> hmOutput = new HashMap<> ();
		UserProfile userProfile = userProfileDao.saveUserSettings(hmInput);
		hmOutput.put(AppConstants.USER_OBJECT, userProfile);
		return hmOutput;
	}
	
	

}
