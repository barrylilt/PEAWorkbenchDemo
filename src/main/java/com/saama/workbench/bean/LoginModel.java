package com.saama.workbench.bean;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginModel implements UserDetails {
	private String username;
	private String password;
	private String fullname;
	private boolean enabled;
	private Set<GrantedAuthority> authorities;
	private List<String> groups;

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	public void setAuthorities(Set<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAccountNonExpired() {
		return this.enabled;
	}

	public boolean isAccountNonLocked() {
		return this.enabled;
	}

	public boolean isCredentialsNonExpired() {
		return this.enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean hasAuthority(String... roleNames) {
		boolean hasIt = false;
		Set<String> roleSet;
		if ((roleNames != null) && (roleNames.length > 0)) {
			roleSet = new HashSet(Arrays.asList(roleNames));
			for (GrantedAuthority auth : this.authorities) {
				if (roleSet.contains(auth.getAuthority())) {
					hasIt = true;
					break;
				}
			}
		}
		return hasIt;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
}
