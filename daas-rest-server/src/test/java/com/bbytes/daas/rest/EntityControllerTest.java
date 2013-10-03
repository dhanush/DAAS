package com.bbytes.daas.rest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
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
public class EntityControllerTest extends DAASTesting {

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
	protected Store store;
	private String entityUuid;

	@BeforeClass
	public static void beforeClass() throws Exception {

	}

	@AfterClass
	public static void afterClass() throws Exception {

	}

	@Before
	public void setup() throws JsonProcessingException, IOException, Exception {
		// appending date to make them unique as retesting will create duplicate entry error
		long date = DateTime.now().getMillis();
		password = "usertest";
		username = password + date;
		appName = "myapp";
		accountName = "TestORG" +date;
		token = "token";
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		
		String contextPath = "/management/accounts/" + accountName;

		this.mockMvc
				.perform(
						post(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
		
		contextPath = "/management/accounts/" + accountName + "/applications/" + appName;
		TenantRouter.setTenantIdentifier(accountName);
		this.mockMvc
				.perform(
						post(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
		
		createEntity();
	}
	
	public void createEntity() throws Exception, IOException, JsonProcessingException {
		String contextPath = "/" + accountName + "/" + appName + "/stores";
		CustomResultHandlerImpl customResultHandler = new CustomResultHandlerImpl();
		this.mockMvc
				.perform(
						post(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
								.content("{'name': 'mystorename'}")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(customResultHandler);
		
		String resultJson = customResultHandler.getJsonResult();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode resultJsonNode = mapper.readTree(resultJson);
		entityUuid = resultJsonNode.get("uuid").getTextValue();
	}
	
	@Test
	public void testGetEntity() throws Exception, IOException, JsonProcessingException {
		String contextPath = "/" + accountName + "/" + appName + "/stores";
		CustomResultHandlerImpl customResultHandler = new CustomResultHandlerImpl();
		this.mockMvc
				.perform(
						get(contextPath).session(session).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(customResultHandler);
		
		String resultJson = customResultHandler.getJsonResult();
		System.out.println(resultJson);
	}
	
	
	@Test
	public void testGetEntitySize() throws Exception, IOException, JsonProcessingException {
		String contextPath = "/" + accountName + "/" + appName + "/stores/size";
		CustomResultHandlerImpl customResultHandler = new CustomResultHandlerImpl();
		this.mockMvc
				.perform(
						get(contextPath).session(session).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(customResultHandler);
		
		String resultJson = customResultHandler.getJsonResult();
		System.out.println(resultJson);
	}
	
	
	@Test
	public void testGetEntityWithProperty() throws Exception, IOException, JsonProcessingException {
		String contextPath = "/" + accountName + "/" + appName + "/stores?pName=name&pValue=mystorename";
		CustomResultHandlerImpl customResultHandler = new CustomResultHandlerImpl();
		this.mockMvc
				.perform(
						get(contextPath).session(session).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(customResultHandler);
		
		String resultJson = customResultHandler.getJsonResult();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JsonNode resultJsonNode = mapper.readTree(resultJson);
		for(JsonNode node : resultJsonNode){
			String name = node.get("name").getTextValue();
			assertEquals(name, "mystorename");
		}
	}

	@Test
	public void testUpdateEntity() throws Exception {
		String contextPath = "/" + accountName + "/" + appName + "/stores/" +entityUuid;
		this.mockMvc
				.perform(
						put(contextPath).session(session).contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
								.content("{'name': 'mystorename123'}")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("mystorename123")).andDo(print());
	}

	@Test
	public void testDeleteEntity() throws Exception {
		String contextPath = "/" + accountName + "/" + appName + "/stores/"+entityUuid;
		this.mockMvc
				.perform(
						delete(contextPath).session(session).header("Authorization", "Bearer " + token)
								.accept(MediaType.APPLICATION_JSON).content("{'name': 'mystorename'}"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("ok"));
	}
}
