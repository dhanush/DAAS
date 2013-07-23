package com.bbytes.daas.template.usergrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bbytes.daas.template.BaasException;
import com.bbytes.daas.template.StoreResourceTemplate;
import com.bbytes.endure.domain.Store;

/**
 * JUnit test case for unit testing {@link StoreResourceTemplate} implementation Note: This test
 * will not run during an mvn test command
 * 
 * 
 * @author Dhanush Gopinath
 * 
 */
public class UsergridStoreResourceTemplateImplTest extends BaseUsergridClientTesting {

	private String password;
	private String username;
	private String appName;
	private String orgName;
	private String token;


	@BeforeClass
	public static void beforeClass() throws Exception {
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

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateAndGetStore() throws BaasException {
		createBasicStore(orgName, appName, token);
		List<Store> stores = storeResourceTemplate.getStores(orgName, appName, token);
		assertNotNull(stores);
		assertNotNull(stores.get(0).getCustomData().get("parkinglots"));
	}

	
//	 @Test
//	 public void testGetStoresByLocation() throws BaasException {
//		 createBasicStore();
//		 List<Store> stores = storeResourceTemplate.getStoresByLocality(orgName, appName, "Kasturinagar", token);
////		 assertNotNull(stores);
////		 assertNotNull(stores.get(0).getCustomData().get("parkinglots"));
//		 stores = storeResourceTemplate.getStoresByLocality(orgName, appName, "Indiranagar", token);
//		 assertNotNull(stores);
//	 }
	 
	 @Test
	 public void testGetStore() throws BaasException {
		 Store store = 	createBasicStore(orgName, appName, token);
		 assertNotNull(storeResourceTemplate.getStore(orgName, appName, store.getUuid(), token));
	 }
	 
	//
	// @Test
	// public void testGetStoresFromGeoLocation() {
	// fail("Not yet implemented");
	// }
	//
	 @Test
	 public void testAddSocialMedia() throws BaasException {
		 Store store = 	createBasicStore(orgName, appName, token);
		 Store storeUpdated = storeResourceTemplate.addSocialMedia(orgName, appName, store.getUuid(), "FB", "TWEET", "G PLUS", token);
		 assertNotNull(storeUpdated);
		 assertEquals("FB", storeUpdated.getSocialMedia().getFbUrl());
	 }
	 
	 @Test
	 public void testAddMapUrl() throws BaasException {
		 Store store = 	createBasicStore(orgName, appName, token);
		 Store storeUpdated = storeResourceTemplate.addMapLocationUrl(orgName, appName, store.getUuid(), "http://map.com", token);
		 assertNotNull(storeUpdated);
		 assertEquals("http://map.com", storeUpdated.getMapLocatorUrl());
	 }



}
