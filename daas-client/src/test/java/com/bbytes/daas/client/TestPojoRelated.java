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
public class TestPojoRelated extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3164908222790879537L;

	private String field1Related;

	private Integer field2Related;

	public TestPojoRelated() {
		field1Related = "todays date " + new Date();
		field2Related = 1;
	}

	/**
	 * @return the field1Related
	 */
	public String getField1Related() {
		return field1Related;
	}

	/**
	 * @param field1Related the field1Related to set
	 */
	public void setField1Related(String field1Related) {
		this.field1Related = field1Related;
	}

	/**
	 * @return the field2Related
	 */
	public Integer getField2Related() {
		return field2Related;
	}

	/**
	 * @param field2Related the field2Related to set
	 */
	public void setField2Related(Integer field2Related) {
		this.field2Related = field2Related;
	}

	
}
