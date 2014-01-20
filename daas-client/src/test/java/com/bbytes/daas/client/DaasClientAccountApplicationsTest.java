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

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClientAccountApplicationsTest extends DaasClientBaseTest {

	private String accnName = "new-accn-" + UUID.randomUUID().toString();
	private String appName = "new-app-" + UUID.randomUUID().toString();

	@Before
	public void SetUp() throws DaasClientException {

	}

	@Test
	public void daasMgmtClientAccountAppTest() throws DaasClientException {
		DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
		boolean success = daasManagementClient.login("admin", "password");

		List<Account> accounts = daasManagementClient.getAccounts();
		int size = accounts.size();
		Assert.assertTrue(size > 0);

		daasManagementClient.createAccount(accnName);

		accounts = daasManagementClient.getAccounts();
		int newsize = accounts.size();
		Assert.assertTrue(size + 1 == newsize);

		Application application = new Application();
		application.setAccountName(accnName);
		application.setName(appName);
		application = daasManagementClient.createApplication(application);

		List<Application> applications = daasManagementClient.getApplications(accnName);
		size = applications.size();

		Assert.assertTrue(size > 0);

		Assert.assertTrue(daasManagementClient.deleteApplication(accnName, appName));

		Assert.assertTrue(daasManagementClient.deleteAccount(accnName));

	}

	@After
	public void cleanUp() throws DaasClientException {
		asyncHttpClient.close();
	}
}
