/*
 * Copyright (C) 2013 The Zorba Open Source Project
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
package com.bbytes.daas.rest.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public enum DaasDefaultFields {

	ENTITY_TYPE("entityType"), 
	ENTITY_CREATED("created"), 
	FIELD_CREATION_DATE("creationDate"), 
	FIELD_MODIFICATION_DATE("modificationDate"), 
	FIELD_ACCOUNT_NAME("accountName"), 
	FIELD_APPLICATION_NAME("applicationName"), 
	FIELD_UUID("uuid"),
	ENTITY_FULL_CLASS_NAME("fullClassName");

	private String fieldName;
	
	private DaasDefaultFields(String fieldName) {
		this.fieldName = fieldName;
	}

	public String toString() {
		return fieldName;
	}

	public static String[] getFields() {
		List<String> fields = new ArrayList<String>();
		for (DaasDefaultFields r : DaasDefaultFields.values()) {
			fields.add(r.toString());
		}
		return (String[]) fields.toArray(new String[DaasDefaultFields.values().length]);
	}
}
