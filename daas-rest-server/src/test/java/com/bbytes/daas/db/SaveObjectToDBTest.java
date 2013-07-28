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

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.bbytes.daas.rest.domain.Application;
import com.bbytes.daas.rest.domain.Account;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
@Transactional("objectDB")
public class SaveObjectToDBTest extends BaseDBTest {

	@Autowired
	private OrientDbTemplate orientDbTemplate;

	@Before
	@Rollback(false)
	public void setUp() {

		long start = Calendar.getInstance().getTimeInMillis();
		Account org = new Account();
		org.setName("testorg2");

		orientDbTemplate.getObjectDatabase().save(org);

		for (int i = 0; i < 2000; i++) {
			Application app = new Application();
			app.setAccountName(org.getName());
			app.setName("test app 1");
			orientDbTemplate.getObjectDatabase().save(app);
		}

		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to save 2000 records in sec " + (start - stop) / 1000);

	}

	@Test
	@Rollback(false)
	public void getSavedObjects() {
		
		long start = Calendar.getInstance().getTimeInMillis();
		List<Account> result = orientDbTemplate.getObjectDatabase().query(
				new OSQLSynchQuery<Account>("select * from Application limit 10000"));

		assertTrue(result.size() > 0);

		List<Application> appResult = orientDbTemplate.getObjectDatabase().query(
				new OSQLSynchQuery<Application>("select * from Application where accountName ='testorg2' limit 10000 "));

		System.out.println(appResult.size());
		assertTrue(appResult.size() > 0);

		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to query in sec " + (start - stop) / 1000);

	}
}
