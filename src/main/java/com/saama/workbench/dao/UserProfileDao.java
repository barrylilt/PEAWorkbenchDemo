package com.saama.workbench.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saama.workbench.bean.UserProfileBean;
import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.model.UserProfile;
import com.saama.workbench.util.AppConstants;

@Repository
public class UserProfileDao {
private static final Logger logger = Logger.getLogger(UserProfileDao.class);
	
	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public UserProfile getUserInfo(String username) throws Exception {
		
		List<UserProfile> arrObj =  new ArrayList<UserProfile>();
		UserProfile userprofile = null;
		try{
			Session session = getSessionFactory().getCurrentSession();
			String hql = "from UserProfile where UserName = :username";
			Query query = session.createQuery(hql);
			query.setParameter("username",username);
			arrObj = query.list();
			userprofile = new UserProfile();
			if(arrObj.size()>0)
			userprofile = arrObj.get(0);
			
			
		
		}
		 catch (Exception e) {
			 e.printStackTrace();
				logger.error("Exception in getSQLData - " + e.getMessage());
				throw e;
			}
		return userprofile;
		
		
		
	}
	public UserProfile updateUserInfo(UserProfileBean userProfileBean) throws Exception{
		List<UserProfile> arrObj =  new ArrayList<UserProfile>();
		UserProfile existingUser = new UserProfile();
		
		try {
			Session session = getSessionFactory().getCurrentSession();
			Criteria criteria = session.createCriteria(UserProfile.class);
			criteria.add(Restrictions.eq("userName",
					userProfileBean.getUserName()));
			arrObj = criteria.list();
			if (arrObj.size() > 1) {
				logger.error("Multiple records found for the selected user");
			} 
			else if (arrObj != null && arrObj.size() > 0) {
				existingUser = arrObj.get(0);
			} 
			else {
				existingUser.setTheme("skin-blue");
				existingUser.setIsActive(AppConstants.Y);
				existingUser.setPassword("demo");
			}

			existingUser.setUserName(userProfileBean.getUserName());
			existingUser.setFirstName(userProfileBean.getFirstName());
			existingUser.setLastName(userProfileBean.getLastName());
			existingUser.setCompany(userProfileBean.getCompany());
			existingUser.setEmailAddress(userProfileBean.getEmailAddress());
			existingUser.setAddress(userProfileBean.getAddress());
			existingUser.setCity(userProfileBean.getCity());
			existingUser.setCountry(userProfileBean.getCountry());
			existingUser.setPostalCode(userProfileBean.getPostalCode());
			existingUser.setAboutMe(userProfileBean.getAboutMe());
			existingUser.setLanguage(userProfileBean.getLanguage());
			existingUser.setDateFormat(userProfileBean.getDateFormat());

			if (userProfileBean.getPhoto() != null) {
				byte[] t = userProfileBean.getPhoto().getBytes();
				if (t.length > 0)
					existingUser.setPhoto(t);
			}

			session.saveOrUpdate(existingUser);

		} catch (Exception e) {
			logger.error("Exception in updateUserInfo - ", e);
			throw e;
		}

		return existingUser;
	}
	public void updateUserTheme(String user, String theme) {
		String sql = "update LND.WB_USER_PROFILE set theme = :theme where UserName = :user";
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("theme", theme);
		query.setString("user", user);
		query.executeUpdate();
		
	}
	public String getTheme(String username) {
		String sql = "select theme from LND.WB_USER_PROFILE where UserName = :user";
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("user", username);
		List list = query.list();
		String theme = (String) list.get(0);
		return theme;
	}
	
	public UserProfile saveUserSettings(Map<String, Object> hmInput) throws Exception{
		Session session = getSessionFactory().getCurrentSession();
		UserProfile userProfile = null;
		
		Criteria crit = session.createCriteria(UserProfile.class);
		crit.add(Restrictions.eq("userName", hmInput.getOrDefault(AppConstants.USERNAME, "")));
		List<UserProfile> list = crit.list();
		
		if (list != null && list.size() > 0)
			userProfile = (UserProfile) list.get(0);
		
		userProfile.setDateFormat(hmInput.get(AppConstants.DATE_FORMAT).toString());
		session.save(userProfile);
		
		return userProfile;
	}

}
