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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.daas.domain.Account;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClientBaseTest {

	private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
	
	private String host="203.196.144.235";
	
	private String port="8089";
	
	
	
	@Before
	public void SetUp() {
	
	}

	@Test
	public void testHttpClient() throws IOException, InterruptedException, ExecutionException  {
		Future<Response> f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
		Response response = f.get();
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	@Test
	public void daasClientTest() throws DaasClientException  {
		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn","testApp","admin", "admin");
	}
	
	
	@Test
	public void daasMgmtClientTest() throws DaasClientException  {
		DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
		boolean success = daasManagementClient.login("admin", "admin");
		
		List<Account> accounts = daasManagementClient.getAccounts();
		int size = accounts.size();
		Assert.assertTrue(size > 0);
		
		daasManagementClient.createAccount("new-accn-" + UUID.randomUUID().toString());
		accounts = daasManagementClient.getAccounts();
		int newsize=accounts.size();
		Assert.assertTrue(size+1 == newsize);
	}
	

	@After
	public void cleanUp() {
		asyncHttpClient.close();
	}
}
