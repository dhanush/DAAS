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

/**
 * Daas client entity not found exception
 * 
 * @author Thanneer
 * 
 * @version 0.0.1
 */
public class DaasClientEntityNotFoundException extends DaasClientException {

	private static final long serialVersionUID = 5033719552987110370L;

	public DaasClientEntityNotFoundException() {
		super();
	}

	public DaasClientEntityNotFoundException(String message) {
		super(message);
	}

	public DaasClientEntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaasClientEntityNotFoundException(Throwable cause) {
		super(cause);
	}

	protected DaasClientEntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
