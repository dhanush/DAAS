package com.bbytes.daas.client;

import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.Future;

import com.bbytes.daas.domain.Account;
import com.google.gson.reflect.TypeToken;
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
 * Daas mgmt client
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasManagementClient extends DaasClient {

	public DaasManagementClient(String host, String port) {
		super(host, port);
	}

	public boolean login(String clientId, String clientSecret) throws DaasClientException {
		// first verify if port and host name is correct using the ping url
		if (!pingSuccess())
			throw new DaasClientException("Not able to reach daas server on" + baseURL);

		token = DaasClientUtil.loginHelper(clientId, clientSecret, baseURL, asyncHttpClient, gson);
		if (token == null) {
			throw new DaasClientException("Not able to login to daas server on" + baseURL);
		}

		return true;
	}

	public Account createAccount(String accName) throws DaasClientException {
		
		try {
			accName = URLEncoder.encode(accName,"UTF-8");
			String url = baseURL + URLConstants.MANAGEMENT_CONTEXT + String.format(URLConstants.CREATE_ACCOUNT, accName);
			Future<Response> f = buildRequest("post", url).execute();
			Response r = f.get();
			if (!HttpStatusUtil.isSuccess(r))
				throw new DaasClientException("Account creation failed : " + r.getResponseBody());

			Account account = gson.fromJson(r.getResponseBody(), Account.class);
			return account;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	public List<Account> getAccounts() throws DaasClientException {
		String url = baseURL + URLConstants.MANAGEMENT_CONTEXT + URLConstants.GET_ALL_ACCOUNT;
		try {
			Future<Response> f = buildRequest("get", url).execute();
			Response r = f.get();
			if (!HttpStatusUtil.isSuccess(r))
				throw new DaasClientException("Could not fetch all accounts : " +  r.getResponseBody());

			List<Account> accounts = gson.fromJson(r.getResponseBody(), new TypeToken<List<Account>>() {
			}.getType());
			return accounts;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

}
