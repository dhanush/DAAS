//package com.bbytes.daas.rest;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.joda.time.DateTime;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
///**
// * Unit test for Endure Rest Services using Usergrid as the BAAS
// * 
// * @author Dhanush Gopinath
// * 
// * @version
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@ContextConfiguration(locations = "classpath:/spring/test-rest-servlet.xml")
//public class EntityControllerTest extends DAASTesting {
//
//	@Autowired
//	protected WebApplicationContext wac;
//	@Autowired
//	protected MockHttpSession session;
//	@Autowired
//	protected MockHttpServletRequest request;
//
//	protected MockMvc mockMvc;
//
//	protected String password;
//	protected String username;
//	protected String appName;
//	protected String orgName;
//	protected String token;
//	protected Store store;
//
//	@BeforeClass
//	public static void beforeClass() throws Exception {
//
//	}
//
//	@AfterClass
//	public static void afterClass() throws Exception {
//
//	}
//
//	@Before
//	public void setup() throws BaasException {
//		// appending date to make them unique as retesting will create duplicate entry error
//		long date = DateTime.now().getMillis();
//		password = "usertest";
//		username = password + date;
//		appName = "myapp" + date;
//		orgName = "TestORG" + date;
//		token = "token";
//		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//
//	}
//
//	@Test
//	public void testCreateEntity() throws Exception {
//		String contextPath = "/" + orgName + "/" + appName+"/stores" ;
//		
//		this.mockMvc
//				.perform(
//						post(contextPath).session(session).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token)
//								.accept(MediaType.APPLICATION_JSON).content("{'name': 'mystorename'}") ).andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.status").value("ok"));
//	}
//
//	@Test
//	public void testUpdateEntity() throws Exception {
//		String contextPath = "/" + orgName + "/" + appName+"/stores/123" ;
//		this.mockMvc
//				.perform(
//						put(contextPath).session(session).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token)
//								.accept(MediaType.APPLICATION_JSON).content("{'name': 'mystorename'}") ).andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.status").value("ok"));
//	}
//	
//	@Test
//	public void testDeleteEntity() throws Exception {
//		String contextPath = "/" + orgName + "/" + appName+"/stores/123" ;
//		
//		this.mockMvc
//				.perform(
//						delete(contextPath).session(session).header("Authorization", "Bearer " + token)
//								.accept(MediaType.APPLICATION_JSON).content("{'name': 'mystorename'}") ).andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.status").value("ok"));
//	}
//	
//	
//	
//}
