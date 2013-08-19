/*
 * Copyright (C) 2013 The Daas Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.bbytes.daas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.dao.UserDao;
import com.bbytes.daas.rest.domain.DaasUser;

/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
@Service("UserService")
public class UserServiceImpl implements UserService{

	
	@Autowired
	private UserDao userDao;
	
	/* (non-Javadoc)
	 * @see com.bbytes.daas.service.UserService#createAccountUser(com.bbytes.daas.rest.domain.DaasUser)
	 */
	@Override
	public DaasUser createAccountUser(String accountName, DaasUser user) throws BaasPersistentException {
		if (user == null)
			throw new IllegalArgumentException("User object is null");
		user.setAccountName(accountName);
		return userDao.saveAccountUser(user);
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.service.UserService#createApplicationUser(com.bbytes.daas.rest.domain.DaasUser)
	 */
	@Override
	public DaasUser createApplicationUser(String accountName, String applicationName, DaasUser user) throws BaasPersistentException {
		if (user == null)
			throw new IllegalArgumentException("User object is null");

		user.setAccountName(accountName);
		user.setApplicationName(applicationName);
		return userDao.saveAppUser(user);
	}

}
