package com.bbytes.daas.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bbytes.daas.db.orientDb.TenantRouter;

/**
 * Unit test for Endure Rest Services using Usergrid as the BAAS
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:/spring/test-rest-servlet.xml")
public class ManagementControllerTest extends DAASTesting{

	@Autowired
	protected WebApplicationContext wac;
	@Autowired
	protected MockHttpSession session;
	@Autowired
	protected MockHttpServletRequest request;

	protected MockMvc mockMvc;

	protected String password;
	protected String username;
	protected String appName;
	protected String accountName;
	protected String token;


	@BeforeClass
	public static void beforeClass() throws Exception {

	}

	@AfterClass
	public static void afterClass() throws Exception {

	}

	@Before
	public void setup() throws BaasException {
		// appending date to make them unique as retesting will create duplicate entry error
		long date = DateTime.now().getMillis();
		password = "usertest";
		username = password + date;
		appName = "myapp" + date;
		accountName = "TestORG" + date;
		token = "token";
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		
	}

	@Test
	public void testCreateAccount() throws Exception {
		String contextPath = "/management/accounts/" + accountName;

		this.mockMvc
				.perform(
						post(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
	}

	@Test
	public void testCreateApplication() throws Exception {
		String contextPath = "/management/accounts/" + accountName + "/applications/" + appName;
		TenantRouter.setTenantIdentifier(accountName);
		this.mockMvc
				.perform(
						post(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
	}

	@Test
	public void testGetApplications() throws Exception {
		String contextPath = "/management/accounts/" + accountName + "/applications";
		TenantRouter.setTenantIdentifier(accountName);
		this.mockMvc
				.perform(
						get(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
	}
	
	@Test
	public void testGetAccounts() throws Exception {
		String contextPath = "/management/accounts/";
		TenantRouter.setTenantIdentifier(accountName);
		this.mockMvc
				.perform(
						get(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
	}

}
