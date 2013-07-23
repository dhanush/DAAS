package com.bbytes.daas.template.usergrid;

import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bbytes.daas.template.AdminResourceTemplate;
import com.bbytes.daas.template.BaasException;
import com.bbytes.endure.domain.usergrid.ApplicationInfo;
import com.bbytes.endure.domain.usergrid.OrganizationOwnerInfo;
import com.bbytes.endure.domain.usergrid.User;


/**
 * JUnit test case for unit testing {@link AdminResourceTemplate} implementation
 * Note: This test will not run during an mvn test command
 * @author Dhanush Gopinath
 *
 */
public class UsergridAdminResourceTemplateImplTest extends BaseUsergridClientTesting{
	
	private static long date;

	@BeforeClass
	public static void beforeClass() throws Exception {
		startJettyTestServer(false, true, false);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		stopJettyTestServer();
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		date = DateTime.now().getMillis();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateOrgAppAndLogin() throws BaasException {
		
		String password = "usertest";
		String username = password + date;
		OrganizationOwnerInfo org = adminResourceTemplate.createNewOrganization("TestORG" + date, username,
				username, username + "@user.com", password);
		assertNotNull(org);
		String token = adminResourceTemplate.getOrganizationLevelAccessToken(username, password, "password", null);
		assertNotNull(token);
		String appName = "myapp"+date;
		ApplicationInfo app = adminResourceTemplate.createNewApplication(org.getOrganization().getName(), appName, token);
		assertNotNull(app);
//		User user = adminResourceTemplate.login(username, password, org.getOrganization().getName(), app.getName());
//		assertNotNull(user);
	}


	@Test(expected = BaasException.class) 
	public void testCreateOrgFailure() throws BaasException {
		String password = "usertest2";
		String username = password + date;
		adminResourceTemplate.createNewOrganization("TestORG" + date, username,
				username, username + "@user.com", password);
	}
}
