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
package com.bbytes.daas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.daas.dao.UserDao;
import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.domain.Role;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
@Service("UserService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.service.UserService#createAccountUser(com.bbytes.daas.rest.domain.DaasUser)
	 */
	@Override
	public DaasUser createAccountUser(String accountName, DaasUser user) throws BaasPersistentException {
		if (user == null)
			throw new IllegalArgumentException("User object is null");
		user.setAccountName(accountName);
		return userDao.saveAccountUser(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.service.UserService#createApplicationUser(com.bbytes.daas.rest.domain.DaasUser
	 * )
	 */
	@Override
	public DaasUser createApplicationUser(String accountName, String applicationName, DaasUser user)
			throws BaasPersistentException {
		if (user == null)
			throw new IllegalArgumentException("User object is null");

		user.setAccountName(accountName);
		user.setApplicationName(applicationName);
		return userDao.saveAppUser(user);
	}

	@Override
	public List<DaasUser> getAccountUsers(String accountName) throws BaasEntityNotFoundException {
		return userDao.findUserByRole(accountName, Role.ROLE_ACCOUNT_ADMIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.UserService#getApplicationUsers(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<DaasUser> getApplicationUsers(String accountName, String applicationName)
			throws BaasEntityNotFoundException {
		return userDao.findUserByRole(accountName, applicationName,Role.ROLE_APPLICATION_USER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.UserService#updateUserPassword(java.lang.String,
	 * java.lang.String, com.bbytes.daas.domain.DaasUser)
	 */
	@Override
	public DaasUser updateUserPassword(String oldPassword, String newPassword, String userId)
			throws BaasPersistentException, BaasEntityNotFoundException, BaasException {
		DaasUser dbUser = userDao.find(userId);
		if (dbUser != null) {
			if (dbUser.getPassword()==null || dbUser.getPassword().equals(oldPassword)) {
				dbUser.setPassword(newPassword);
				return userDao.update(dbUser);
			} else {
				throw new BaasException("Pasword update failed : Old Password incorrect or null");
			}
		}else{
			throw new BaasEntityNotFoundException("User with user id : " + userId + "not found");
		}
	}

}
