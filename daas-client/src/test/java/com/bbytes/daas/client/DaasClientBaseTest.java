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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

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

	protected AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	protected String host = "localhost";

	protected String port = "8080";

	@Test
	public void testHttpClient() throws IOException, InterruptedException, ExecutionException  {
		Future<Response> f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
		Response response = f.get();
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	protected DaasClient getDaasClient() throws DaasClientException {
		DaasClient daasClient = new DaasClient(host, port);
		boolean success = daasClient.login("testAccn", "testApp", "admin", "admin");
		if(success)
			return daasClient;
		
		return null;
	}

	protected DaasManagementClient getDaasMgmtClient() throws DaasClientException {
		DaasManagementClient daasManagementClient = new DaasManagementClient(host, port);
		boolean success = daasManagementClient.login("admin", "admin");
		if(success)
			return daasManagementClient;
		
		return null;
	}


	
	
}
