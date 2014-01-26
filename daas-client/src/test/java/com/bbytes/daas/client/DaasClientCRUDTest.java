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
public class DaasClientCRUDTest extends DaasClientBaseTest {

	private TestPojo test;

	private TestPojoRelated testPojoRelated;

	@Before
	public void SetUp() throws DaasClientException {
		try {
			DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
			boolean state = daasManagementClient.login("admin", "password");

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
	public void daasClientUpdateTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		test.setField2(9);
		test = daasClient.updateEntity(test);
		System.out.println(test.getField2());
		Assert.assertEquals(new Integer(9), test.getField2());

	}

	@Test
	public void daasClientDeleteTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		String status = daasClient.deleteEntity(test);
		System.out.println(status);
		Assert.assertEquals("success", status);

	}

	@Test
	public void daasClientRelateEntityTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		TestPojoRelated testPojoRelated = new TestPojoRelated();
		testPojoRelated = daasClient.updateEntity(testPojoRelated);
		boolean status = daasClient.addRelation(test, testPojoRelated, "testRelation");
		System.out.println(status);
		Assert.assertTrue(status);

	}

	@Test
	public void daasClientDeleteRelateEntityTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		testPojoRelated = daasClient.updateEntity(testPojoRelated);
		boolean status = daasClient.addRelation(test, testPojoRelated, "testRelation");
		System.out.println(status);
		Assert.assertTrue(status);

	}

	@Test
	public void daasClientQueryRightRelatedEntityTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		boolean status = daasClient.addRelation(test, testPojoRelated, "testRelation");
		Assert.assertTrue(status);
		List<TestPojoRelated> testPojoRelatedList = daasClient.getRightSideRelatedEntities(test, "testRelation",
				TestPojoRelated.class);
		Assert.assertNotNull(testPojoRelatedList);
		Assert.assertTrue(testPojoRelatedList.size() > 0);
		for (TestPojoRelated testPojoRelated : testPojoRelatedList) {
			System.out.println(testPojoRelated.getUuid());
		}

	}

	@Test
	public void daasClientQueryLeftRelatedEntityTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		boolean status = daasClient.addRelation(test, testPojoRelated, "testRelation");
		Assert.assertTrue(status);
		List<TestPojo> testPojoList = daasClient.getLeftSideRelatedEntities(testPojoRelated, "testRelation",
				TestPojo.class);
		Assert.assertNotNull(testPojoList);
		Assert.assertTrue(testPojoList.size() > 0);
		for (TestPojo testPojo : testPojoList) {
			System.out.println(testPojo.getUuid());
		}

	}

	@Test
	public void daasClientQueryEntitySizeTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		Long size = daasClient.getEntitySize(TestPojo.class);
		Assert.assertTrue(size > 0);
		System.out.println(size);

	}

	@Test
	public void daasClientQueryEntityByPropertyTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		test.setField2(7);
		daasClient.updateEntity(test);
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("field2", "7");
		List<TestPojo> testPojos = daasClient.getEntitiesByProperty(TestPojo.class, propertyMap);
		Assert.assertTrue(testPojos.size() > 0);
		System.out.println(testPojos.size());

	}

	@Test
	public void daasClientQueryEntityByRangeTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		List<TestPojo> testPojos = daasClient.getEntitiesByRange(TestPojo.class, "field2", "integer", "7", "8");
		Assert.assertTrue(testPojos.size() > 0);
		System.out.println(testPojos.size());

	}
	
	@Test
	public void daasClientQueryEntityByIdTest() throws DaasClientException {

		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "accnUser", "accnPassword");
		Assert.assertTrue(success);
		TestPojo testPojo = daasClient.getEntityById(TestPojo.class, test.getUuid());
		Assert.assertNotNull(testPojo);
		System.out.println(testPojo.getUuid());

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
