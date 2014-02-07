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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.daas.domain.Application;
import com.bbytes.daas.domain.DaasUser;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClientAsyncCRUDTest extends DaasClientBaseTest {

	private TestPojo test;

	private TestPojoRelated testPojoRelated;

	@Before
	public void SetUp() throws DaasClientException {
		try {
			DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
			boolean state = daasManagementClient.login("root", "root");

			try {
				daasManagementClient.createAccount("testAccn");
				Application app = new Application();
				app.setAccountName("testAccn");
				app.setName("testApp");
				daasManagementClient.createApplication(app);
			} catch (Exception e) {
				// TODO: handle exception
			}

			// create accn user
			DaasUser user = new DaasUser();
			user.setAccountName("testAccn");
			user.setEmail("test@test.com");
			user.setName("accnUser");
			user.setPassword("accnPassword");
			user.setUserName("accnUser");
			DaasUser returnedUser = daasManagementClient.createAccountUser("testAccn", user);

		} catch (Exception e) {
			// do nothing
		}

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);

		test = new TestPojo();
		test = daasClient.createEntity(test);

		testPojoRelated = new TestPojoRelated();
		testPojoRelated = daasClient.createEntity(testPojoRelated);

		System.out.println(test.getUuid());
		Assert.assertNotNull(test.getUuid());

	}

	@Test
	public void daasClientUpdateTest() throws DaasClientException, InterruptedException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		test.setField2(9);
		test = daasClient.updateEntity(test);
		System.out.println(test.getField2());
		Assert.assertEquals(new Integer(9), test.getField2());
		
		test.setField2(5);
		daasClient.updateEntity(test,new AsyncResultHandler<TestPojo>() {

			@Override
			public void onComplete(TestPojo t) {
				try {
					Thread.currentThread().sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("came in to on complete");
				System.out.println(test.getField2());
				Assert.assertEquals(new Integer(5), test.getField2());
			}

			@Override
			public void onError(Throwable throwable) {
				// TODO Auto-generated method stub
				
			}
		});
		System.out.println("came out 1");
		System.out.println(test.getField2());
		
		Thread.currentThread().sleep(8000);

	}

	

	@After
	public void cleanUp() throws DaasClientException {
		// DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
		// boolean success = daasManagementClient.login("admin", "password");
		// daasManagementClient.deleteApplication("testAccn", "testApp");
		// daasManagementClient.deleteAccount("testAccn");

		asyncHttpClient.close();
	}
}
