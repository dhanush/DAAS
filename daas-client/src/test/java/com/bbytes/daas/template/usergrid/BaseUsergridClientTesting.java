/**
 * 
 */
package com.bbytes.daas.template.usergrid;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bbytes.daas.template.AdminResourceTemplate;
import com.bbytes.daas.template.BaasException;
import com.bbytes.daas.template.DealResourceTemplate;
import com.bbytes.daas.template.StoreResourceTemplate;
import com.bbytes.daas.testing.DAASTesting;
import com.bbytes.endure.domain.Deal;
import com.bbytes.endure.domain.SocialMedia;
import com.bbytes.endure.domain.Store;
import com.bbytes.endure.domain.usergrid.ApplicationInfo;
import com.bbytes.endure.domain.usergrid.OrganizationOwnerInfo;

/**
 * @author Dhanush Gopinath
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/usergrid/test-usergrid-app-context.xml")
public class BaseUsergridClientTesting extends DAASTesting {

	@Autowired
	protected AdminResourceTemplate adminResourceTemplate;

	@Autowired
	protected DealResourceTemplate dealResourceTemplate;

	@Autowired
	protected StoreResourceTemplate storeResourceTemplate;

	/**
	 * Creates a dummy organization and applicatoin and returns the organization level access token
	 * 
	 * @param orgName
	 * @param appName
	 * @param username
	 * @param password
	 * @return
	 * @throws BaasException
	 */
	public String init(String orgName, String appName, String username, String password) throws BaasException {
		OrganizationOwnerInfo org = adminResourceTemplate.createNewOrganization(orgName, username, username, username
				+ "@user.com", password);
		assertNotNull(org);
		String token = adminResourceTemplate.getOrganizationLevelAccessToken(username, password, "password", null);
		assertNotNull(token);
		ApplicationInfo app = adminResourceTemplate.createNewApplication(org.getOrganization().getName(), appName,
				token);
		assertNotNull(app);
		return token;
	}

	protected SocialMedia createSocialMedia(String fbUrl, String twitterUrl, String gPlusUrl) {
		SocialMedia sm = new SocialMedia();
		sm.setFbUrl(fbUrl);
		sm.setTwitterUrl(twitterUrl);
		sm.setgPlusUrl(gPlusUrl);
		return sm;
	}

	/**
	 * Creates a Basic deal
	 * 
	 * @param orgName
	 * @param appName
	 * @param validFrom
	 * @param validTo
	 * @param token
	 * @return
	 * @throws BaasException
	 */
	protected Deal createBasicDeal(String orgName, String appName, Date validFrom, Date validTo, String token)
			throws BaasException {
		Deal deal = dealResourceTemplate.createDeal(orgName, appName, "Deal 1", "Buy 1 Get 1 Free", validFrom, validTo,
				token);
		return deal;
	}

	/**
	 * Method to create a store
	 * 
	 * @param orgName
	 * @param appName
	 * @param token
	 * @return
	 * @throws BaasException
	 */
	protected Store createBasicStore(String orgName, String appName, String token) throws BaasException {
		Map<String, Object> customData = new HashMap<String, Object>();
		customData.put("itemcount", 100);
		customData.put("parkinglots", 10);
		Store store = storeResourceTemplate.createStore(orgName, appName, token, "Kasturinagar", customData);
		assertNotNull(store);
		assertNotNull(store.getCustomData().get("parkinglots"));
		return store;
	}

	/**
	 * Creates a store and deal and adds the store to deal as a relationship valid
	 * 
	 * @param orgName
	 * @param appName
	 * @param dealUuid
	 *            TODO
	 * @param storeUuid
	 *            TODO
	 * @param token
	 * @return
	 * @throws BaasException
	 */
	protected boolean addValidStoreToDeal(String orgName, String appName, String dealUuid, String storeUuid,
			String token) throws BaasException {
		return dealResourceTemplate.addValidStore(orgName, appName, storeUuid, dealUuid, token);
	}
}
