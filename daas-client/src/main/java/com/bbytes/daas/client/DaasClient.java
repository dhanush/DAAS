package com.bbytes.daas.client;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.Response;

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

/**
 * Daas client
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClient {

	protected String clientId;

	protected String clientSecret;

	protected AsyncHttpClient asyncHttpClient;

	protected String host;

	protected String port;

	protected String baseURL;

	protected Gson gson;

	protected OAuthToken token;

	protected String applicationName;

	protected String accountName;

	protected DaasManagementClient daasManagementClient;

	public DaasClient(String host, String port) {

		this.host = host;
		this.port = port;

		baseURL = "http://" + host + ":" + port + URLConstants.SERVER_CONTEXT;

		Builder builder = new AsyncHttpClientConfig.Builder();
		builder.setCompressionEnabled(true).setAllowPoolingConnection(true).setConnectionTimeoutInMs(30000).build();

		asyncHttpClient = new AsyncHttpClient(builder.build());

		gson = new GsonBuilder().registerTypeAdapter(Date.class, SerializerUtil.getSerializerForDate())
				.registerTypeAdapter(Date.class, SerializerUtil.getDeSerializerForDate()).create();

	}

	protected boolean pingSuccess() {
		try {
			Future<Response> f = asyncHttpClient.prepareGet(baseURL + "/ping").execute();
			Response r = f.get();
			if (!HttpStatusUtil.getReponseStatus(r).endsWith(HttpStatusUtil.SUCCESS))
				return false;

		} catch (InterruptedException | ExecutionException | IOException e) {
			return false;
		}

		return true;
	}

	public boolean login(String accountName, String applicationName, String clientId, String clientSecret) throws DaasClientException
			 {
		this.accountName = accountName;
		this.applicationName = applicationName;

		// first verify if port and host name is correct using the ping url
		if (!pingSuccess())
			throw new DaasClientException("Not able to reach daas server on" + baseURL);

		token = DaasClientUtil.loginHelper(clientId, clientSecret, baseURL, asyncHttpClient, gson);
		if (token == null) {
			throw new DaasClientException("Not able to login to daas server on" + baseURL);
		}

		return true;
	}

	protected BoundRequestBuilder buildRequest(String httpMethodType, String url) {
		return DaasClientUtil.buildRequest(asyncHttpClient, httpMethodType, url, token);
	}

}
