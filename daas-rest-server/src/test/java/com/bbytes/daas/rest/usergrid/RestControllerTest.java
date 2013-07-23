package com.bbytes.daas.rest.usergrid;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.bbytes.daas.template.BaasException;
import com.bbytes.daas.template.DealResourceTemplate;
import com.bbytes.daas.template.StoreResourceTemplate;
import com.bbytes.daas.template.usergrid.BaseUsergridClientTesting;
import com.bbytes.endure.domain.Deal;
import com.bbytes.endure.domain.Store;

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
public class RestControllerTest extends BaseUsergridClientTesting {

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
	protected String orgName;
	protected String token;

	private Store store;

	@BeforeClass
	public static void beforeClass() throws Exception {
		startJettyTestServer(false, true, false);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		stopJettyTestServer();
	}

	@Before
	public void setup() throws BaasException {
		// appending date to make them unique as retesting will create duplicate entry error
		long date = DateTime.now().getMillis();
		password = "usertest";
		username = password + date;
		appName = "myapp" + date;
		orgName = "TestORG" + date;
		token = init(orgName, appName, username, password);
		store = createBasicStore(orgName, appName, token);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

	}

	@Test
	public void testLogin() throws Exception {
		String contextPath = "/login";
		this.mockMvc
				.perform(
						post(contextPath).session(session).contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("userName", username).param("password", password)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.TEXT_PLAIN));

	}

	@Test
	public void testGetEntities() throws Exception {
		String contextPath = "/" + orgName + "/" + appName + StoreResourceTemplate.ENDPOINT;
		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].locality").value("Kasturinagar"));
	}

	@Test
	public void testGetEntity() throws Exception {
		String contextPath = "/" + orgName + "/" + appName + StoreResourceTemplate.ENDPOINT + "/" + store.getUuid();
		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.locality").value("Kasturinagar"));
	}

	@Test
	public void testGetRelatedEntities() throws Exception {
		Store s = createBasicStore(orgName, appName, token);
		Deal d = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusDays(1).toDate(), token);
		addValidStoreToDeal(orgName, appName, d.getUuid(), s.getUuid(), token);

		String contextPath = "/" + orgName + "/" + appName + DealResourceTemplate.ENDPOINT + "/" + d.getUuid()
				+ DealResourceTemplate.DEAL_STORE_RELATION + StoreResourceTemplate.ENDPOINT;

		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].uuid").value(s.getUuid()));
	}

	@Test
	public void testGetConnectingEntities() throws Exception {
		Store s = createBasicStore(orgName, appName, token);
		Deal d = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusDays(1).toDate(), token);
		addValidStoreToDeal(orgName, appName, d.getUuid(), s.getUuid(), token);

		String contextPath = "/" + orgName + "/" + appName + StoreResourceTemplate.ENDPOINT + "/" + s.getUuid()
				+ "/connecting" + DealResourceTemplate.DEAL_STORE_RELATION;
		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].uuid").value(d.getUuid()));
	}
}
