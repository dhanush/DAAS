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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.dao.AccountDao;
import com.bbytes.daas.rest.dao.ApplicationDao;
import com.bbytes.daas.rest.domain.Account;
import com.bbytes.daas.rest.domain.Application;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
@Service("ManagementService")
public class ManagementServiceImpl implements ManagementService {

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private ApplicationDao applicationDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#getAllAccounts()
	 */
	@Override
	public List<Account> getAllAccounts() throws BaasPersistentException, BaasEntityNotFoundException {
		try {
			return accountDao.findAll();
		} catch (BaasEntityNotFoundException e) {
			return new ArrayList<Account>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#createAccount(java.lang.String)
	 */
	@Override
	public Account createAccount(String accountName, String accountType, String accountSubType, String accountFullName) throws BaasPersistentException {
		Account account = new Account();
		account.setName(accountName);
		account.setAccountType(accountType);
		account.setAccountSubType(accountSubType);
		account.setFullName(accountFullName);
		return accountDao.save(account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#createApplication(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Application createApplication(String accountName, String applicationName) throws BaasPersistentException {
		Application app = new Application();
		app.setAccountName(accountName);
		app.setName(applicationName);
		return applicationDao.save(app);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#getAllApplications(java.lang.String)
	 */
	@Override
	public List<Application> getAllApplications(String accountName) throws BaasPersistentException,
			BaasEntityNotFoundException {
		try {
			// we dont have to filter based on account name as we have separate database per account
			return applicationDao.findAll();
		} catch (BaasEntityNotFoundException e) {
			return new ArrayList<Application>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#deleteAccount(java.lang.String)
	 */
	@Override
	public boolean deleteAccount(String accountName) throws BaasPersistentException, BaasEntityNotFoundException {
		List<Account> accns = accountDao.find("name", accountName);
		if (accns != null && accns.size() > 0) {
			// account name is unique so get the only one in the list
			Account accn = accns.get(0);
			accountDao.remove(accn);
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#deleteApplication(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean deleteApplication(String accountName, String applicationName) throws BaasPersistentException,
			BaasEntityNotFoundException {
		List<Application> apps = applicationDao.find("name", applicationName);
		if (apps != null && apps.size() > 0) {
			// account name is unique so get the only one in the list
			Application app = apps.get(0);
			applicationDao.remove(app);
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#getAccount(java.lang.String)
	 */
	@Override
	public Account getAccount(String accountName) throws BaasPersistentException, BaasEntityNotFoundException {
		List<Account> accns = accountDao.find("name", accountName);
		if (accns != null && accns.size() > 0) {
			// account name is unique so get the only one in the list
			return accns.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.ManagementService#getApplication(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Application getApplication(String accountName, String applicationName) throws BaasPersistentException,
			BaasEntityNotFoundException {
		List<Application> apps = applicationDao.find("name", accountName);
		if (apps != null && apps.size() > 0) {
			// app name is unique so get the only one in the list
			return apps.get(0);
		}
		return null;
	}

}
