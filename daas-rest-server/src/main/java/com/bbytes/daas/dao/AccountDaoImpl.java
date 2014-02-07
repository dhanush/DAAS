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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.db.orientDb.TenantRouter;
import com.bbytes.daas.domain.Account;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.object.OObjectIteratorClassInterface;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * Account DAO Impl
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Repository
@Transactional(TenantRouter.TENANT_MGT)
public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {

	/**
	 * The Db is global tenant management db - configured in db context xml file . The account is
	 * created per tenant in tenant management db.
	 */
	private ODatabaseObject getObjectDataBase() {
		return (OObjectDatabaseTx) orientDbTemplate.getTenantManagementDatabase();
	}

	@Override
	public Account save(Account account) throws BaasPersistentException {
		// check if the org name is unique if so then save
		OObjectDatabaseTx db = null;

		if (!findAny("name", account.getName())) {
			db = (OObjectDatabaseTx) getObjectDataBase();
			account.setUuid(UUID.randomUUID().toString());
			account.setCreationDate(new Date());
			account.setModificationDate(new Date());
			db.save(account);
			account = db.detach(account, true);
			if (account != null) {
				orientDbTemplate.createDatabase(account.getName(), "object");
			}
		} else {
			throw new BaasPersistentException("Account name has to be unique,  " + account.getName()
					+ " is already taken ");
		}

		return account;
	}

	@Override
	public Account update(Account account) throws BaasPersistentException {
		OObjectDatabaseTx db = (OObjectDatabaseTx) getObjectDataBase();
		account.setModificationDate(new Date());
		db.save(account);
		account = db.detach(account, true);
		return account;
	}

	@Override
	public void remove(Account account) throws BaasPersistentException {
		ODatabaseObject db = getObjectDataBase();
		db.delete(account);
		// Drop database after the account is delete . All the data in the tenant account db is
		// deleted and the db is dropped.
		orientDbTemplate.dropDatabase(account.getName());

	}

	@Override
	public Account find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException {
		OObjectDatabaseTx db = (OObjectDatabaseTx) getObjectDataBase();
		Account account = db.load(id);
		account = db.detach(account, true);
		return account;

	}

	@Override
	public List<Account> findAll() throws BaasPersistentException, BaasEntityNotFoundException {
		OObjectDatabaseTx db = (OObjectDatabaseTx) getObjectDataBase();
		OObjectIteratorClassInterface<Account> listItr = db.browseClass(Account.class);
		@SuppressWarnings("unchecked")
		List<Account> result = IteratorUtils.toList(listItr);
		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException();
		return detach(result, db);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#count()
	 */
	@Override
	public long count() throws BaasPersistentException {
		ODatabaseObject db = getObjectDataBase();
		long count = db.countClass(Account.class.getSimpleName());
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String)
	 */
	@Override
	public Account find(String uuid) throws BaasPersistentException, BaasEntityNotFoundException {
		OObjectDatabaseTx db = (OObjectDatabaseTx) getObjectDataBase();
		List<Account> result = db.query(new OSQLSynchQuery<Account>("select * from " + Account.class.getSimpleName()
				+ " where uuid = '" + uuid + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found " + Account.class.getSimpleName());
		result = detach(result, db);
		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Account> find(String property, String value) throws BaasEntityNotFoundException {
		OObjectDatabaseTx db = (OObjectDatabaseTx) getObjectDataBase();
		List<Account> result = db.query(new OSQLSynchQuery<ODocument>("select * from " + Account.class.getSimpleName()
				+ " where " + property + " = " + "'" + value + "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Account not found " + value);
		result = detach(result, db);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#isAvailable(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean findAny(String property, String value) {
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
	public boolean findAny(Map<String, String> propertyToValue) {
		ODatabaseObject db = getObjectDataBase();

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

	}

}
