/*
 * Copyright (C) 2013 The Daas Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.bbytes.daas.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Any exception related to Baas calls will be wrapped in this
 *
 * @author Thanneer
 *
 * @version 1.0.0
 */
@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Failed during persistance")  // 500
public class DaasPersistentException extends RuntimeException {

	private static final long serialVersionUID = 6289917997937599218L;

	public DaasPersistentException() {
		super();
	}

	public DaasPersistentException(String jsonResponse, String message, Throwable cause) {
		super(message, cause);
	}

	public DaasPersistentException(String message) {
		super(message);
	}

	public DaasPersistentException(Throwable cause) {
		super(cause);
	}

}
