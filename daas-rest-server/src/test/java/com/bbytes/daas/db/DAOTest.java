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
package com.bbytes.daas.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.dao.ApplicationDao;
import com.bbytes.daas.rest.dao.AccountDao;
import com.bbytes.daas.rest.domain.Application;
import com.bbytes.daas.rest.domain.Account;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
@Transactional("objectDB")
public class DAOTest extends BaseDBTest {

	private static final Logger LOG = Logger.getLogger(DAOTest.class);

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private AccountDao accountDao;

	private String uuid;
	
	String orgName = "same name";

	@Before
	public void setUp() throws BaasPersistentException {

		Account org = new Account();
		org.setName(UUID.randomUUID().toString());

		accountDao.save(org);
		
		Account org2 = new Account();
		org2.setName(UUID.randomUUID().toString());

		accountDao.save(org2);

		Application app1 = new Application();
		app1.setName(orgName);

		applicationDao.save(app1);

		for (int i = 0; i < 2; i++) {
			Application app = new Application();
			app.setAccountName(org.getName());
			app.setName(UUID.randomUUID().toString());
			app = applicationDao.save(app);
			uuid = app.getUuid();
		}

		LOG.debug("setup ended...");
	}

	@Test
	public void testDaoQueryAll() throws BaasPersistentException, BaasEntityNotFoundException {
		long start = Calendar.getInstance().getTimeInMillis();
		int size = applicationDao.findAll().size();
		System.out.println("application object size in DB " + size);
		assertTrue(size > 0);
		long stop = Calendar.getInstance().getTimeInMillis();

		LOG.debug("Time taken to fetch in sec " + (start - stop) / 1000);

	}

	@Test
	public void testDaoCount() throws BaasPersistentException {
		long start = Calendar.getInstance().getTimeInMillis();
		long size = applicationDao.count();
		LOG.debug("app object size " + size);
		assertTrue(size > 0);
		long stop = Calendar.getInstance().getTimeInMillis();

		LOG.debug("Time taken to fetch size in sec " + (start - stop) / 1000);

	}

	@Test
	public void testDaoFindByID() throws BaasPersistentException, BaasEntityNotFoundException {
		LOG.debug("testDaoFindByID started...");
		long start = Calendar.getInstance().getTimeInMillis();
		Application app = applicationDao.find(uuid);
		assertNotNull(app);
		long stop = Calendar.getInstance().getTimeInMillis();

		LOG.debug("testDaoFindByID ended...");

	}

	@Test
	public void testDaoIsAvailable() throws BaasPersistentException, BaasEntityNotFoundException {
		LOG.debug("isAvailable test...");

		Application app = applicationDao.find(uuid);
		assertNotNull(app);

		boolean available = applicationDao.findAny("uuid", uuid);
		assertTrue(available);

		available = applicationDao.findAny("uuid", "dummy uuid");
		assertTrue(!available);

		available = accountDao.findAny("name", "dummy org");
		assertTrue(!available);

	}

	
	@Test
	@Rollback(false)
	public void createOrg() throws BaasPersistentException {

		Account org2 = new Account();
		org2.setName(uuid);

		accountDao.save(org2);
	}
	
	@Test(expected=BaasPersistentException.class)
	public void testDuplicateOrg() throws BaasPersistentException {

		Account org2 = new Account();
		org2.setName(orgName);

		accountDao.save(org2);
	}
	
	@Test(expected=BaasPersistentException.class)
	public void testDuplicateApp() throws BaasPersistentException {

		String orgName = UUID.randomUUID().toString();
		String appName = UUID.randomUUID().toString();
		
		Application app = new Application();
		app.setAccountName(orgName);
		app.setName(appName);
		app = applicationDao.save(app);
		
		
		Application app2 = new Application();
		app2.setAccountName(orgName);
		app2.setName(appName);
		app2 = applicationDao.save(app2);
		
		
	}
}
