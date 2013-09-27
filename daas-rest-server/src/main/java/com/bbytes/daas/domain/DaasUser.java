/*
 * Copyright (C) 2013 The Daas Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bbytes.daas.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * User pojo object
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public class DaasUser extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2911620978523067262L;

	private String userName;

	private String email;

	private String password;

	private String mobileNo;

	private boolean active;

	private String accountName;

	private String applicationName;

	private List<Role> roles;

	public DaasUser() {
		super();
		type = DaasUser.class.getSimpleName();
	}

	public DaasUser(List<Role> roles) {
		super();
		type = DaasUser.class.getSimpleName();
		this.roles = roles;
		this.active=true;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}

	/**
	 * @param mobileNo
	 *            the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName
	 *            the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName
	 *            the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the authorities
	 */
	@JsonIgnore
	public Set<GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (Role role : getRoles())
			authorities.add(new SimpleGrantedAuthority(role.getValue()));
		return authorities;
	}

	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}
	
	public DaasUser copy(DaasUser tobeCopied)
	{
		if(tobeCopied==null)
			return tobeCopied;
		
		this.userName=tobeCopied.getUserName();
		this.email=tobeCopied.getEmail();
		this.password=tobeCopied.getPassword();
		this.mobileNo=tobeCopied.getMobileNo();
		this.accountName=tobeCopied.getAccountName();
		this.applicationName=tobeCopied.getApplicationName();
		
		return this;
	}
	

}
