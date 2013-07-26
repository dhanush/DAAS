/*
 * Copyright (C) 2013 The Zorba Open Source Project 
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
package com.bbytes.daas.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.bbytes.daas.rest.dao.ApplicationDao;
import com.bbytes.daas.rest.domain.Application;
import com.bbytes.daas.rest.domain.Organization;

/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
@Transactional("objectDB")

public class DAOTest extends BaseDBTest{

	private static final Logger LOG = Logger.getLogger(DAOTest.class);
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private OrientDbTemplate orientDbTemplate;
	
	private String uuid = UUID.randomUUID().toString();
	
	@Before
	public void setUp() {

		Organization org = new Organization();
		org.setName("testorg 2");
		org.setUuid(UUID.randomUUID().toString());

		orientDbTemplate.getObjectDatabase().save(org);

		for (int i = 0; i < 2; i++) {
			Application app = new Application();
			app.setOrganizationName(org.getName());
			app.setName("test app new 2");
			app.setUuid(uuid);
			orientDbTemplate.getObjectDatabase().save(app);
		}

	
	
		LOG.debug("setup ended...");
	}

	@Test
	public void testDaoQueryAll()
	{
		long start = Calendar.getInstance().getTimeInMillis();
		int size = applicationDao.list().size();
		System.out.println("application object size in DB " + size);
		assertTrue( size > 0);
		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to fetch in sec " + (start - stop) / 1000);

	}
	
	@Test
	public void testDaoCount()
	{
		long start = Calendar.getInstance().getTimeInMillis();
		long size = applicationDao.count();
		System.out.println("app object size " + size);
		assertTrue( size > 0);
		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to fetch size in sec " + (start - stop) / 1000);

	}
	
	@Test
	@Rollback(false)
	public void testDaoFindByID()
	{
		LOG.debug("testDaoFindByID started...");
		long start = Calendar.getInstance().getTimeInMillis();
		Application app = applicationDao.find(uuid);
		assertNotNull(app);
		long stop = Calendar.getInstance().getTimeInMillis();

		System.out.println("Time taken to fetch one app in sec " + (start - stop) / 1000);
		LOG.debug("testDaoFindByID ended...");

	}
}
