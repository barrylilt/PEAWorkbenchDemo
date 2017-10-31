package com.saama.workbench.uniauthentication;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import com.saama.workbench.bean.AuthorityInfo;
import com.saama.workbench.bean.LoginModel;

public class SamlUserDetails implements SAMLUserDetailsService {
	private static final Logger logger = Logger.getLogger(SamlUserDetails.class);
	private static final String GROUP_NAME_ATTR = "group_name";
	private static final String GROUP_NAME_KEY = "CN";

	public Object loadUserBySAML(SAMLCredential credential) {
		logger.debug("loadUserBySAML");
		logger.debug("NameID - " + credential.getNameID().getValue());
		if ((credential.getNameID() == null)
				|| (StringUtils.isBlank(credential.getNameID().getValue()))) {
			throw new UsernameNotFoundException(
					"No username found in SAML credential!");
		}
		Map<String, String> attrMap = new HashMap();
		String friendlyName;
		for (Attribute attribute : credential.getAttributes()) {
			String attrName = attribute.getName();
			friendlyName = attribute.getFriendlyName();
			String attrVal = credential.getAttributeAsString(attrName);

			attrMap.put(attribute.getName(), attrVal);

			logger.debug("SAM: Attribute name: " + attrName + "[" + friendlyName + "]");
			logger.debug("SAM: Attribute value: " + attrVal);
		}
		LoginModel login = new LoginModel();

		List<String> groups = getGroupNames(attrMap);

		login.setUsername(getUserName(attrMap));
		login.setFullname(credential.getNameID().getValue());
		login.setPassword("");
		login.setEnabled(true);

		Set<GrantedAuthority> authorities = new HashSet();
		if ((groups != null) && (!groups.isEmpty())) {
			login.setGroups(groups);
			for (String group : groups) {
				authorities.add(new AuthorityInfo(group));
			}
			groups.clear();
		}
		login.setAuthorities(authorities);
		groups = null;

		attrMap.clear();
		attrMap = null;

		return login;
	}
	
	private String getUserName(Map<String, String> attributeMap) {
		if (attributeMap != null) {
			for (Map.Entry<String, String> en : attributeMap.entrySet()) {
				if (en.getKey().contains("claims/name")) {
					return en.getValue();
				}
			}
			
		}
		return null;
	}

	private List<String> getGroupNames(Map<String, String> attributeMap) {
		String attrVal = (String) attributeMap.get("group_name");
		List<String> groupNames = null;
		if (attrVal != null) {
			String[] vals = attrVal.split(",");
			if (vals != null) {
				String[] arrayOfString1;
				int j = (arrayOfString1 = vals).length;
				for (int i = 0; i < j; i++) {
					String val = arrayOfString1[i];

					logger.debug("Parsing: " + val);
					String[] pair = val.split("=");
					if ((pair != null) && (pair.length == 2)
							&& ("CN".equals(pair[0]))) {
						logger.debug("Found group value: " + pair[1]);
						if (groupNames == null) {
							groupNames = new Vector();
						}
						groupNames.add(pair[1]);
					}
				}
			}
		}
		if (groupNames == null) {
			groupNames = Collections.emptyList();
		}
		return groupNames;
	}
}
