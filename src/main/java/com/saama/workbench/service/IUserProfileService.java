package com.saama.workbench.service;

import java.util.Map;

import com.saama.workbench.bean.UserProfileBean;
import com.saama.workbench.model.UserProfile;

public interface IUserProfileService {
	
	UserProfile getUserInfo(String username) throws Exception;

	UserProfile updateUserInfo(UserProfileBean userdata) throws Exception;

	UserProfile addNewUserIntoUserProfile(UserProfileBean userProfileBean) throws Exception;

	void storeUserTheme(String user, String theme);

	String getTheme(String username);

	Map saveUserSettings(Map<String, Object> hmInput) throws Exception;
}
