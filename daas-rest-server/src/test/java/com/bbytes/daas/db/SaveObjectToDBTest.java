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
package com.bbytes.daas.db;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.dao.AccountDao;
import com.bbytes.daas.dao.ApplicationDao;
import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.bbytes.daas.domain.Account;
import com.bbytes.daas.domain.Application;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class SaveObjectToDBTest extends BaseDBTest {

	@Autowired
	private OrientDbTemplate orientDbTemplate;
	
	@Autowired
	private ConversionService conversionService;
	
	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	private String uuid = UUID.randomUUID().toString();

	@Before
	public void setUp() {

		long start = Calendar.getInstance().getTimeInMillis();
		Account org = new Account();
		org.setName(uuid);

		try {
			org = accountDao.save(org);
		} catch (BaasPersistentException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 2000; i++) {
			Application app = new Application();
			app.setAccountName(org.getName());
			app.setName("test app " + new Random().nextLong());
			applicationDao.save(app);
		}

		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to save 2000 records in sec " + (start - stop) / 1000);

	}

	@Test
	public void getSavedObjects() throws BaasPersistentException, BaasEntityNotFoundException {
		
		long start = Calendar.getInstance().getTimeInMillis();
		List<Account> result = accountDao.findAll();

		assertTrue(result.size() > 0);

		List<Application> appResult = applicationDao.find("accountName",uuid);

		System.out.println(appResult.size());
		assertTrue(appResult.size() > 0);

		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to query in sec " + (start - stop) / 1000);

	}
}
