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
package com.bbytes.daas.rest.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.DaasUser;
import com.bbytes.daas.rest.domain.Role;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Account DAO Impl
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Repository
public class UserDaoImpl extends AbstractDao<DaasUser> implements UserDao {

	@Override
	@Transactional
	public DaasUser saveAccountUser(DaasUser user) throws BaasPersistentException {
		// check if the user name and email combo is unique
		Map<String, String> emailPropValue = new HashMap<String, String>();
		emailPropValue.put("email", user.getEmail());
		emailPropValue.put(DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(), user.getAccountName());

		Map<String, String> usernamePropValue = new HashMap<String, String>();
		usernamePropValue.put("userName", user.getName());
		usernamePropValue.put(DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(), user.getAccountName());

		if (!findAny(emailPropValue) && !findAny(usernamePropValue)) {
			List<Role> roles = new ArrayList<Role>();
			roles.add(Role.ROLE_ACCOUNT_ADMIN);
			roles.add(Role.ROLE_APPLICATION_USER);
			DaasUser accountUser = new DaasUser(roles);
			accountUser = accountUser.copy(user);
			user = super.save(accountUser);
		} else {
			throw new BaasPersistentException("User name and email has to be unique,  " + user.getName() + " or "
					+ user.getEmail() + " is already taken ");
		}
		return user;
	}

	@Override
	@Transactional
	public DaasUser saveAppUser(DaasUser user) throws BaasPersistentException {

		// check if the user name and email combo is unique
		Map<String, String> emailPropValue = new HashMap<String, String>();
		emailPropValue.put("email", user.getEmail());
		emailPropValue.put(DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(), user.getAccountName());
		emailPropValue.put(DaasDefaultFields.FIELD_APPLICATION_NAME.toString(), user.getApplicationName());

		Map<String, String> usernamePropValue = new HashMap<String, String>();
		usernamePropValue.put("userName", user.getName());
		usernamePropValue.put(DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(), user.getAccountName());
		usernamePropValue.put(DaasDefaultFields.FIELD_APPLICATION_NAME.toString(), user.getApplicationName());

		if (!findAny(emailPropValue) && !findAny(usernamePropValue)) {
			List<Role> roles = new ArrayList<Role>();
			roles.add(Role.ROLE_APPLICATION_USER);
			DaasUser appUser = new DaasUser(roles);
			appUser = appUser.copy(user);
			user = super.save(appUser);
		} else {
			throw new BaasPersistentException("User name and email has to be unique,  " + user.getName() + " or "
					+ user.getEmail() + " is already taken ");
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.UserDao#findUser(java.lang.String, java.lang.String)
	 */
	@Override
	public DaasUser findUser(String accountName, String userName) throws BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
		List<ODocument> result = db.query(
				new OSQLSynchQuery<ODocument>("select * from DaasUser  where userName = '" + userName + "' and "
						+ DaasDefaultFields.FIELD_ACCOUNT_NAME.toString() + " = '" + accountName + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

		return convertToEntity(result.get(0));
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.UserDao#findUser(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public DaasUser findUser(String accountName, String appName, String userName) throws BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
		List<ODocument> result = db.query(
				new OSQLSynchQuery<ODocument>("select * from DaasUser  where userName = '" + userName + "' and "
						+ DaasDefaultFields.FIELD_ACCOUNT_NAME.toString() + " = '" + accountName + "' and "
						+ DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = '" + appName + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

		return convertToEntity(result.get(0));
		} finally {
			db.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.UserDao#findUser(java.lang.String)
	 */
	@Override
	public DaasUser findUser(String userName) throws BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
		List<ODocument> result = db.query(
				new OSQLSynchQuery<ODocument>("select * from DaasUser  where userName = '" + userName + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

		return convertToEntity(result.get(0));
		} finally {
			db.close();
		}
	}
	
}
