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

import java.util.Date;

import com.bbytes.daas.domain.Entity;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class TestPojo extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3164908222790879537L;

	private String field1;

	private Integer field2;

	public TestPojo() {
		field1 = "todays date " + new Date();
		field2 = 5;
	}

	/**
	 * @return the field1
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @param field1
	 *            the field1 to set
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	/**
	 * @return the field2
	 */
	public Integer getField2() {
		return field2;
	}

	/**
	 * @param field2
	 *            the field2 to set
	 */
	public void setField2(Integer field2) {
		this.field2 = field2;
	}
}
