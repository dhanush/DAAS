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
package com.bbytes.daas.security;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import com.bbytes.daas.dao.UserDao;
import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.rest.DaasEntityNotFoundException;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClientDetailsService implements ClientDetailsService {

	private static final Logger LOG = Logger.getLogger(DaasClientDetailsService.class);
	
	@Autowired
	private UserDao userDao;

	private Integer accessTokenValiditySeconds;
	
	@Autowired
	@Qualifier("adminClientDetails")
	private ClientDetailsService adminClientDetailsService;

	public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {

		try{
		ClientDetails clientDetails =adminClientDetailsService.loadClientByClientId(clientId);
		
		if(clientDetails!=null)
			return clientDetails;
		} catch (NoSuchClientException e) {
			LOG.warn("Logged in User is not configured user " + e.getMessage(),e);
		}
		
		try {
			DaasUser user = userDao.findUser(clientId);
			BaseClientDetails baseClientDetails = new BaseClientDetails();
			baseClientDetails.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
			baseClientDetails.setAuthorities(user.getAuthorities());
			baseClientDetails.setClientId(user.getUserName());
			baseClientDetails.setClientSecret(user.getPassword());
			return baseClientDetails;
		} catch (DaasEntityNotFoundException e) {
			throw new UsernameNotFoundException("Username Not Found", e);
		}
		
		
	}

	/**
	 * @return the accessTokenValiditySeconds
	 */
	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	/**
	 * @param accessTokenValiditySeconds
	 *            the accessTokenValiditySeconds to set
	 */
	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}


}
