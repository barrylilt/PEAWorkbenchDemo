package com.saama.workbench.bean;

import org.springframework.security.core.GrantedAuthority;

public class AuthorityInfo implements GrantedAuthority {
	private String authority;

	public AuthorityInfo(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}
}