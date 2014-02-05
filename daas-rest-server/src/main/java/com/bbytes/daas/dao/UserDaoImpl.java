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
package com.bbytes.daas.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.domain.Role;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

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
			Role adminRole = new Role(Role.ROLE_ACCOUNT_ADMIN);
			roles.add(adminRole);
			Role appRole = new Role(Role.ROLE_APPLICATION_USER);
			roles.add(appRole);
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
			Role role = new Role(Role.ROLE_APPLICATION_USER);
			roles.add(role);
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
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
		List<DaasUser> result = dbTx.query(
				new OSQLSynchQuery<DaasUser>("select * from DaasUser  where userName = '" + userName + "' and "
						+ DaasDefaultFields.FIELD_ACCOUNT_NAME.toString() + " = '" + accountName + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

		return detach(result.get(0), dbTx);
		} finally {
			dbTx.close();
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
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
		List<DaasUser> result = dbTx.query(
				new OSQLSynchQuery<DaasUser>("select * from DaasUser  where userName = '" + userName + "' and "
						+ DaasDefaultFields.FIELD_ACCOUNT_NAME.toString() + " = '" + accountName + "' and "
						+ DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = '" + appName + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

		return detach(result.get(0), dbTx);
		} finally {
			dbTx.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.UserDao#findUser(java.lang.String)
	 */
	@Override
	public DaasUser findUser(String userName) throws BaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
		List<DaasUser> result = dbTx.query(
				new OSQLSynchQuery<DaasUser>("select * from DaasUser  where userName = '" + userName + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

		return detach(result.get(0), dbTx);
		} finally {
			dbTx.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.dao.UserDao#findUserByRole(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DaasUser> findUserByRole(String accountName, String role) throws BaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
		List<DaasUser> result = dbTx.query(
				new OSQLSynchQuery<DaasUser>("select * from DaasUser  where "+DaasDefaultFields.FIELD_ACCOUNT_NAME.toString() + " = '" + accountName + "' and "
						+ "roles.value in ['"+role+"']"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Daas User with given role -  " + role + " and account name - "+ accountName);

		return detach(result, dbTx);
		} finally {
			dbTx.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.bbytes.daas.dao.UserDao#findUserByRole(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DaasUser> findUserByRole(String accountName,String applicationName, String role) throws BaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
		List<DaasUser> result = dbTx.query(
				new OSQLSynchQuery<DaasUser>("select * from DaasUser  where "+DaasDefaultFields.FIELD_ACCOUNT_NAME.toString() + " = '" + accountName + "' and "
						+DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = '" + applicationName + "' and "+ "roles.value in ['"+role+"']"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Daas User with given role -  " + role + " and account name - "+ accountName);

		return detach(result, dbTx);
		} finally {
			dbTx.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.dao.UserDao#findUserAsRecord(java.lang.String)
	 */
	@Override
	public ODocument findUserAsRecord(String userName) throws BaasEntityNotFoundException {
		OrientGraph db = getDataBase();
		try {
			String sql = "select * from DaasUser  where userName = '" + userName + "'";

			List<ODocument> result = db.getRawGraph().query(new OSQLSynchQuery<ODocument>(sql));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException("Entity not found of type DaasUser with Username " + userName);

			return result.get(0);
		} finally {
			db.shutdown();
		}
	}
	
	
	
}
