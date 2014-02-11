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

import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Response;

/**
 *  Client utils class
 * 
 * @author Thanneer
 * 
 * @version 0.0.1
 */
public class DaasClientUtil {

	public static boolean isLoggedIn(OAuthToken token) {
		if (token == null || token.isExpired())
			return false;

		return true;
	}

	public static BoundRequestBuilder buildRequest(AsyncHttpClient asyncHttpClient, String httpMethodType, String url,
			OAuthToken token) {

		switch (httpMethodType.toLowerCase()) {
		case "post":
			return asyncHttpClient.preparePost(url).addHeader("Authorization", " Bearer " + token.getValue());
		case "get":
			return asyncHttpClient.prepareGet(url).addHeader("Authorization", " Bearer " + token.getValue());
		case "put":
			return asyncHttpClient.preparePut(url).addHeader("Authorization", " Bearer " + token.getValue());
		case "delete":
			return asyncHttpClient.prepareDelete(url).addHeader("Authorization", " Bearer " + token.getValue());
		default:
			return asyncHttpClient.prepareGet(url).addHeader("Authorization", " Bearer " + token.getValue());
		}
	}

	public static OAuthToken loginHelper(String clientId, String clientSecret, String baseURL,
			AsyncHttpClient asyncHttpClient, Gson gson) throws DaasClientException {
		return loginHelper(null, clientId, clientSecret, baseURL, asyncHttpClient, gson);
	}

	public static OAuthToken loginHelper(String accountName, String clientId, String clientSecret, String baseURL,
			AsyncHttpClient asyncHttpClient, Gson gson) throws DaasClientException {

		OAuthToken token = null;
		try {
			Future<Response> f = null;
			if (accountName == null) {
				f = asyncHttpClient.prepareGet(baseURL + URLConstants.LOGIN_OAUTH)
						.addQueryParameter("grant_type", "client_credentials").addQueryParameter("client_id", clientId)
						.addQueryParameter("client_secret", clientSecret).execute();
			} else {
				f = asyncHttpClient.prepareGet(baseURL + URLConstants.LOGIN_OAUTH)
						.addQueryParameter("grant_type", "client_credentials").addQueryParameter("client_id", clientId)
						.addQueryParameter("client_secret", clientSecret).addQueryParameter("account", accountName)
						.execute();
			}

			Response r = f.get();
			if (!HttpStatusUtil.isSuccess(r)){
				throw new DaasClientException("Not able to login to daas server on " + baseURL);
			}
				

			token = gson.fromJson(r.getResponseBody(), OAuthToken.class);

			if (!DaasClientUtil.isLoggedIn(token))
				throw new DaasClientException("Login to daas server on" + baseURL + " failed");

		} catch (InterruptedException | ExecutionException | IOException e) {
			throw new DaasClientException(e);
		}
		
		return token;

	}

	/**
	 * @param r
	 * @throws DaasClientException
	 * @throws IOException
	 */
	public static void checkResponse(Response r) throws DaasClientException, IOException {
		if (!HttpStatusUtil.isSuccess(r)) {

			if (r.getStatusCode() == HttpStatusUtil.NOT_FOUND)
				throw new DaasClientEntityNotFoundException("Entity Not Found");
			else
				throw new DaasClientException("Daas server error : " + r.getResponseBody());
		}

	}

}