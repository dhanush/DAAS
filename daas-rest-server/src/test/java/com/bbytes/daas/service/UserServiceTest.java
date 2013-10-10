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

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.dao.AccountDao;
import com.bbytes.daas.dao.ApplicationDao;
import com.bbytes.daas.dao.UserDao;
import com.bbytes.daas.db.BaseDBTest;
import com.bbytes.daas.db.orientDb.TenantRouter;
import com.bbytes.daas.domain.Account;
import com.bbytes.daas.domain.Application;
import com.bbytes.daas.domain.DaasUser;
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
public class UserServiceTest extends BaseDBTest {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserService userService;

	private String uuid;

	protected static String DB_NAME = "TEST";

	@BeforeClass
	public static void setUp() {
		TenantRouter.setTenantIdentifier(DB_NAME);
	}

	@Before
	public void SetUp() throws BaasPersistentException {

		Account org = new Account();
		org.setName(UUID.randomUUID().toString());

		accountDao.save(org);

		uuid = UUID.randomUUID().toString();
		Application app1 = new Application();
		app1.setAccountName(org.getName());
		app1.setName(uuid);

		app1 = applicationDao.save(app1);

		DaasUser user = new DaasUser();
		user.setEmail("test1@test1.com");
		user.setAccountName(app1.getAccountName());
		user = userDao.saveAccountUser(user);
		uuid = user.getUuid();

		setAuthObjectForTest("ROLE_TENENT_ADMIN");
	}

	@Test
	public void updatePasswordTest() throws BaasPersistentException, BaasEntityNotFoundException, BaasException {
		DaasUser user = userService.updateUserPassword("test123", "newTest123", uuid);
		Assert.assertTrue(user.getPassword().equals("newTest123"));

		// query from db and test
		DaasUser dbUser = userDao.find(uuid);
		Assert.assertTrue(dbUser.getPassword().equals("newTest123"));
	}

	@After
	@Transactional
	public void cleanUp() throws BaasPersistentException, BaasEntityNotFoundException {
		DaasUser dbUser = userDao.find(uuid);
		userDao.remove(dbUser);
	}
}
