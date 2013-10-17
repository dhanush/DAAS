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

import com.ning.http.client.Response;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class HttpStatusUtil {

	public static final String SUCCESS = "success";

	public static final String RE_DIRECTION = "redirection";

	public static final String ERROR = "error";

	public static final String ERROR_INTERNAL = "error_internal";

	public static String getReponseStatus(Response response) {
		if (response.getStatusCode() / 100 == 2)
			return SUCCESS;

		if (response.getStatusCode() / 100 == 3)
			return RE_DIRECTION;

		if (response.getStatusCode() / 100 == 4)
			return ERROR;

		if (response.getStatusCode() / 100 == 5)
			return ERROR_INTERNAL;

		return ERROR;

	}
	
	public static boolean isSuccess(Response response) {
		return getReponseStatus(response).endsWith(HttpStatusUtil.SUCCESS);
	}
	
}
