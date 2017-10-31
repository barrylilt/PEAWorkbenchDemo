package com.saama.workbench.interceptor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Component
public class Interceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object object, Exception exception)
			throws Exception {
		
		HttpSession session = request.getSession();
		session.setAttribute(AppConstants.LAST_REQUEST_TIME, (new Date()).getTime());
		
		// System.out.println("afterCompletion");
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object object, ModelAndView mav) throws Exception {
		
		// System.out.println("postHandle");
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object object) throws Exception {
		
		if (AppConstants.LOGINURI.equalsIgnoreCase(request.getRequestURI().replaceAll(request.getContextPath(), ""))) {
			return true;
		}
		
//		if (PEAUtils.convertToBoolean(PropertiesUtil.getPropertyOrDefault(AppConstants.COMMON_SETTINGS_WITHOUT_LOGIN, "false"))) {
//			request.getSession().setAttribute(AppConstants.USERNAME, "DevUser");
//			return true;
//		}
		
		HttpSession session = request.getSession();
		if (session.getAttribute(AppConstants.USERNAME) != null && session.getAttribute(AppConstants.LAST_REQUEST_TIME) != null) {
			if (TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - (long)session.getAttribute(AppConstants.LAST_REQUEST_TIME)) < Integer.parseInt(PropertiesUtil.getProperty(AppConstants.COMMON_SETTINGS_SESSION_TIMEOUT))) {
				
				// To avoid Back button issue 
				response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		        response.setDateHeader("Expires", 0);
				
				return true;
			} 
		}
		
		session.removeAttribute(AppConstants.USERNAME);
		response.sendRedirect(request.getContextPath() + "/logout");
		// System.out.println("preHandle" + object);
		return false;
	}

}
