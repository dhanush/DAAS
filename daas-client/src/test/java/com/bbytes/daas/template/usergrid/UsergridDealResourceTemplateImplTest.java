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
package com.bbytes.daas.template.usergrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bbytes.daas.template.BaasException;
import com.bbytes.endure.domain.Deal;
import com.bbytes.endure.domain.Photo;
import com.bbytes.endure.domain.Store;

/**
 * 
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class UsergridDealResourceTemplateImplTest extends BaseUsergridClientTesting {

	private String password;
	private String username;
	private String appName;
	private String orgName;
	private String token;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		startJettyTestServer(false, true, false);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		stopJettyTestServer();
	}

	@Before
	public void setUp() throws Exception {
		// appending date to make them unique as retesting will create duplicate entry error
		long date = DateTime.now().getMillis();
		password = "usertest";
		username = password + date;
		appName = "myapp" + date;
		orgName = "TestORG" + date;
		token = init(orgName, appName, username, password);
	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#createDeal(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testCreateDeal() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		assertNotNull(deal);
	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#deleteDeal(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testDeleteDeal() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		assertNotNull(deal);
		deal = dealResourceTemplate.getDeal(orgName, appName, deal.getUuid(), token);
		assertNotNull(deal);
		Deal deletedDeal = dealResourceTemplate.deleteDeal(orgName, appName, deal.getUuid(), token);
		assertNotNull(deletedDeal);
		try {
			deletedDeal = dealResourceTemplate.getDeal(orgName, appName, deal.getUuid(), token);
		} catch (BaasException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#editDeal(java.lang.String, java.lang.String, java.lang.String, java.util.Map)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testEditDeal() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);

		assertNull(deal.getDealUrl());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dealUrl", "http://edit.me");
		deal = dealResourceTemplate.editDeal(orgName, appName, deal.getUuid(), map, token);
		assertNotNull(deal.getDealUrl());

	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#editDealValidity(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testEditDealValidity() throws BaasException {
		DateTime now = DateTime.now();
		Date validFrom = now.toDate();
		Date validTo = now.plusMonths(1).toDate();
		Deal deal = createBasicDeal(orgName, appName, validFrom, validTo, token);
		assertNotNull(deal);
		Date newValidFrom = now.plusDays(15).toDate();
		Date newValidTo = now.plusMonths(3).toDate();
		deal = dealResourceTemplate.editDealValidity(orgName, appName, deal.getUuid(), newValidFrom, newValidTo, token);
		assertNotNull(deal.getValidTo());
		assertEquals(newValidFrom, deal.getValidFrom());
		assertEquals(newValidTo, deal.getValidTo());

	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#addDealInformation(java.lang.String, java.lang.String, java.lang.String, java.util.Map)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testAddDealInformation() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		assertNull(deal.getDealUrl());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dealBonusUrl", "http://edit.me");
		map.put("dealBonus", "Fiat Punto");
		deal = dealResourceTemplate.addDealInformation(orgName, appName, deal.getUuid(), map, token);
		assertNotNull(deal.getCustomData().get("dealBonus"));
	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#addDealPhotos(java.lang.String, java.lang.String, java.lang.String, java.util.List)}
	 * .
	 * 
	 * @throws MalformedURLException
	 * @throws BaasException
	 */
	@Test
	public void testAddDealPhotos() throws MalformedURLException, BaasException {
		Photo p1 = new Photo();
		p1.setHeight(10);
		p1.setWidth(10);
		p1.setTitle("photo 1");
		p1.setImageURL(new URL("http://p.p"));

		Photo p2 = new Photo();
		p2.setHeight(10);
		p2.setWidth(10);
		p2.setTitle("photo 2");
		p2.setImageURL(new URL("http://p.p"));

		List<Photo> photos = new ArrayList<>();
		photos.add(p1);
		photos.add(p2);
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		deal = dealResourceTemplate.addDealPhotos(orgName, appName, deal.getUuid(), photos, token);
		assertNotNull(deal);
		assertEquals(2, deal.getDealPhotos().size());

	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#addDealContacts(java.lang.String, java.lang.String, java.lang.String, java.util.List)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testAddDealContacts() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		List<String> contactNumbers = new ArrayList<>();
		contactNumbers.add("123");
		contactNumbers.add("124");
		deal = dealResourceTemplate.addDealContacts(orgName, appName, deal.getUuid(), contactNumbers, token);
		assertNotNull(deal);
		assertEquals(2, deal.getDealContactNumbers().size());
	}

	/**
	 * Test method for
	 * {@link com.bbytes.daas.template.usergrid.UsergridDealResourceTemplateImpl#addDealWebsite(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws BaasException
	 */
	@Test
	public void testAddDealWebsite() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		assertNull(deal.getDealUrl());
		deal = dealResourceTemplate.addDealWebsite(orgName, appName, deal.getUuid(), "http://newurl", token);
		assertNotNull(deal.getDealUrl());
	}

	@Test
	public void testAddValidStore() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		Store store = createBasicStore(orgName, appName, token);
		assertTrue(dealResourceTemplate.addValidStore(orgName, appName, store.getUuid(), deal.getUuid(), token));
		assertNotNull(storeResourceTemplate.getStoresWhereDealIsValid(orgName, appName, deal.getUuid(), token));
		assertNotNull(dealResourceTemplate.getDealsValidAtStore(orgName, appName, store.getUuid(), token));
	}

	
	@Test
	public void testRemoveValidStore() throws BaasException {
		Deal deal = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusMonths(1).toDate(),
				token);
		Store store = createBasicStore(orgName, appName, token);
		assertTrue(dealResourceTemplate.addValidStore(orgName, appName, store.getUuid(), deal.getUuid(), token));
		assertNotNull(storeResourceTemplate.getStoresWhereDealIsValid(orgName, appName, deal.getUuid(), token));
		assertNotNull(dealResourceTemplate.getDealsValidAtStore(orgName, appName, store.getUuid(), token));
		
		assertTrue(dealResourceTemplate.removeValidStore(orgName, appName, store.getUuid(), deal.getUuid(), token));
		assertNull(storeResourceTemplate.getStoresWhereDealIsValid(orgName, appName, deal.getUuid(), token));
		assertNull(dealResourceTemplate.getDealsValidAtStore(orgName, appName, store.getUuid(), token));
	}
}
