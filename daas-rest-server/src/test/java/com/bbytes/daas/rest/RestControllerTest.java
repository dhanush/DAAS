package com.bbytes.daas.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Date;

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
public class RestControllerTest extends DAASTesting {

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
	protected Store store;

	@BeforeClass
	public static void beforeClass() throws Exception {

	}

	@AfterClass
	public static void afterClass() throws Exception {

	}

	@Before
	public void setup() throws DaasException {
		// appending date to make them unique as retesting will create duplicate entry error
		long date = DateTime.now().getMillis();
		password = "admin";
		username = "admin";
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
				.andExpect(content().contentType(MediaType.TEXT_PLAIN)).andDo(print());

	}

	@Test
	public void testGetEntities() throws Exception {
		String contextPath = "/" + orgName + "/" + appName ;
		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].locality").value("Kasturinagar")).andDo(print());
	}

	@Test
	public void testGetEntity() throws Exception {
		String contextPath = "/" + orgName + "/" + appName + "StoreResourceTemplate.ENDPOINT" + "/" + store.getUuid();
		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.locality").value("Kasturinagar")).andDo(print());
	}

	@Test
	public void testGetRelatedEntities() throws Exception {
		Store s = createBasicStore(orgName, appName, token);
		Deal d = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusDays(1).toDate(), token);
		addValidStoreToDeal(orgName, appName, d.getUuid(), s.getUuid(), token);

		String contextPath = "/" + orgName + "/" + appName + "DealResourceTemplate.ENDPOINT" + "/" + d.getUuid()
				+ "DealResourceTemplate.DEAL_STORE_RELATION" + "StoreResourceTemplate.ENDPOINT";

		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].uuid").value(s.getUuid())).andDo(print());
	}

	@Test
	public void testGetConnectingEntities() throws Exception {
		Store s = createBasicStore(orgName, appName, token);
		Deal d = createBasicDeal(orgName, appName, DateTime.now().toDate(), DateTime.now().plusDays(1).toDate(), token);
		addValidStoreToDeal(orgName, appName, d.getUuid(), s.getUuid(), token);

		String contextPath = "/" + orgName + "/" + appName + "StoreResourceTemplate.ENDPOINT" + "/" + s.getUuid()
				+ "/connecting" + "DealResourceTemplate.DEAL_STORE_RELATION";
		this.mockMvc
				.perform(
						get(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].uuid").value(d.getUuid())).andDo(print());
	}

	/**
	 * @param orgName2
	 * @param appName2
	 * @param uuid
	 * @param uuid2
	 * @param token2
	 */
	private void addValidStoreToDeal(String orgName2, String appName2, Object uuid, Object uuid2, String token2) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param orgName2
	 * @param appName2
	 * @param date
	 * @param date2
	 * @param token2
	 * @return
	 */
	private Deal createBasicDeal(String orgName2, String appName2, Date date, Date date2, String token2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param orgName2
	 * @param appName2
	 * @param token2
	 * @return
	 */
	private Store createBasicStore(String orgName2, String appName2, String token2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param orgName2
	 * @param appName2
	 * @param username2
	 * @param password2
	 * @return
	 */
	private String init(String orgName2, String appName2, String username2, String password2) {
		// TODO Auto-generated method stub
		return null;
	}
}
