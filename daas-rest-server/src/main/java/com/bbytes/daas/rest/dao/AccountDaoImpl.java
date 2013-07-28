/*
 * Copyright (C) 2013 The Zorba Open Source Project
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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Repository;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.Account;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.object.OObjectIteratorClassInterface;
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
public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {

	/**
	 * The Db is global tenant management db - configured in db context xml file . The account is
	 * created per tenant in tenant management db.
	 */
	protected ODatabaseObject getObjectDataBase() {
		return orientDbTemplate.getTenantManagementDatabase();
	}

	@Override
	public Account save(Account org) throws BaasPersistentException {
		// check if the org name is unique if so then save
		if (!findAny("name", org.getName())) {
			org = super.save(org);
		} else {
			throw new BaasPersistentException("Account name has to be unique,  " + org.getName() + " is already taken ");
		}
		return org;
	}

	@Override
	public Account update(Account entity) throws BaasPersistentException {
		ODatabaseObject db = getObjectDataBase();
		try {
			entity.setModificationDate(new Date());
			return db.save(entity);
		} finally {
			db.close();
		}
	}

	@Override
	public void remove(Account entity) throws BaasPersistentException {
		ODatabaseObject db = getObjectDataBase();
		try {
			db.delete(entity);
		} finally {
			db.close();
		}
	}

	@Override
	public Account find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException {
		ODatabaseObject db = getObjectDataBase();
		try {
			return (Account) db.load(id);
		} finally {
			db.close();
		}
	}

	@Override
	public List<Account> findAll() throws BaasPersistentException, BaasEntityNotFoundException {
		ODatabaseObject db = getObjectDataBase();
		try {
			OObjectIteratorClassInterface<Account> listItr = db.browseClass(Account.class);
			@SuppressWarnings("unchecked")
			List<Account> result = IteratorUtils.toList(listItr);
			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException();
			return result;
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#count()
	 */
	@Override
	public long count() throws BaasPersistentException {
		ODatabaseObject db = getObjectDataBase();
		try {
			long count = db.countClass(Account.class.getSimpleName());
			return count;
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String)
	 */
	@Override
	public Account find(String uuid) throws BaasPersistentException, BaasEntityNotFoundException {
		ODatabaseObject db = getObjectDataBase();
		try {
			List<Account> result = db.query(new OSQLSynchQuery<Account>("select * from "
					+ Account.class.getSimpleName() + " where uuid = '" + uuid + "'"));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException("Entity not found " + Account.class.getSimpleName());

			return result.get(0);
		} finally {
			db.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#isAvailable(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean findAny(String property, String value) throws BaasPersistentException {
		Map<String, String> propertyToValue = new HashMap<String, String>();
		propertyToValue.put(property, value);
		return findAny(propertyToValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#findAny(java.util.Map)
	 */
	@Override
	public boolean findAny(Map<String, String> propertyToValue) throws BaasPersistentException {
		ODatabaseObject db = getObjectDataBase();
		try {
			if (propertyToValue == null)
				throw new IllegalArgumentException("Null value passed as arg");

			String whereCondition = "";
			int index = 0;
			for (Iterator<String> iterator = propertyToValue.keySet().iterator(); iterator.hasNext();) {
				String property = iterator.next();
				String value = propertyToValue.get(property);
				if (index == 0) {
					whereCondition = whereCondition + property + " = " + "'" + value + "'";
				} else {
					whereCondition = whereCondition + " and " + property + " = " + "'" + value + "'";
				}
				index++;

			}

			String sql = "SELECT COUNT(*) as count FROM " + Account.class.getSimpleName() + "  WHERE " + whereCondition;
			long count = ((ODocument) db.query(new OSQLSynchQuery<Account>(sql)).get(0)).field("count");

			if (count == 0)
				return false;

			return true;
		} finally {
			db.close();
		}
	}

}
