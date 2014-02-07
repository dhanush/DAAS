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
package com.bbytes.daas.client;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.daas.domain.Account;
import com.bbytes.daas.domain.Application;
import com.bbytes.daas.domain.DaasUser;

/**
 * Test case for issue #20 in DAAS
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClientRepeatAccountCreationTest extends DaasClientBaseTest {

	private String accnName = "new-accn-same" + UUID.randomUUID().toString();
	private String appName = "new-app-" + UUID.randomUUID().toString();

	@Before
	public void SetUp() throws DaasClientException {

	}

	@Test
	public void daasMgmtClientRepeatAccountCreationTest() throws DaasClientException {

		DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
		boolean success = daasManagementClient.login("admin", "password");

		Account account = daasManagementClient.createAccount(accnName);
		System.out.println("Account created : " + accnName);

		List<Account> accounts = daasManagementClient.getAccounts();
		int size = accounts.size();
		Assert.assertTrue(size > 0);

		Application application = new Application();
		application.setAccountName(accnName);
		application.setName(appName);
		application = daasManagementClient.createApplication(application);

		DaasUser user = new DaasUser();
		user.setAccountName(account.getName());
		user.setEmail("test@test.com");
		user.setName("testuser");
		user.setPassword("testpassword");
		user.setUserName("testuser");
		DaasUser returnedUser = daasManagementClient.createAccountUser(account.getName(), user);

		Assert.assertNotNull(returnedUser);

		System.out.println("Account deleted : " + accnName);
		daasManagementClient.deleteAccount(accnName);

		accounts = daasManagementClient.getAccounts();
		int newsize = accounts.size();
		Assert.assertTrue(newsize == size - 1);

		account = daasManagementClient.createAccount(accnName);

		accounts = daasManagementClient.getAccounts();
		size = accounts.size();
		Assert.assertTrue(size > 0);

		application = daasManagementClient.createApplication(application);

		returnedUser = daasManagementClient.createAccountUser(account.getName(), user);

		Assert.assertNotNull(returnedUser);

	}

	@Test
	public void daasMgmtClientAccountDelTest() throws DaasClientException {
		DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
		boolean success = daasManagementClient.login("admin", "password");

		List<Account> accounts = daasManagementClient.getAccounts();
		int size = accounts.size();

		System.out.println("Account deleted : " + accnName);
		success = daasManagementClient.deleteAccount(accounts.get(0).getName());
		Assert.assertTrue(success);

		accounts = daasManagementClient.getAccounts();
		int newSize = accounts.size();

		Assert.assertTrue(newSize == size - 1);
	}

	@After
	public void cleanUp() throws DaasClientException {
		asyncHttpClient.close();
	}
}
