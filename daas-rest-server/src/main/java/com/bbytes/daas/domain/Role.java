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
package com.bbytes.daas.domain;

/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
public class Role extends Entity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5164891492488139181L;
	public static final String ROLE_TENENT_ADMIN= "ROLE_TENENT_ADMIN";
	public static final String ROLE_ACCOUNT_ADMIN= "ROLE_ACCOUNT_ADMIN";
	public static final String ROLE_APPLICATION_USER= "ROLE_APPLICATION_USER";

	private String value;

	/**
	 * @param roleApplicationUser
	 */
	public Role(String value) {
		this.value=value;
	}
	
	public Role(){
		// jus for serialization
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
	
	
}
