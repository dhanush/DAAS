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

import java.util.List;
import java.util.Map;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Entity DAO
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public interface DocumentDao {

	public static final String ENTITY_TYPE = "entityType";
	
	public static final String ENTITY_CREATED = "created";

	public static final String FIELD_CREATION_DATE = "creationDate";

	public static final String FIELD_MODIFICATION_DATE = "modificationDate";

	public static final String FIELD_ORGANIZATION_NAME = "organizationName";

	public static final String FIELD_APPLICATION_NAME = "applicationName";
	
	public static final String FIELD_UUID = "uuid";

	public ODocument create(String entityType, Map<String, Object> propertyMap, String orgName, String appName)
			throws BaasPersistentException;

	public ODocument save(ODocument entity) throws BaasPersistentException;

	public ODocument update(ODocument entity, Map<String, Object> propertyMap) throws BaasPersistentException;

	public ODocument update(ODocument entity) throws BaasPersistentException;

	public void remove(ODocument entity) throws BaasPersistentException;

	public ODocument find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException;

	public ODocument find(String entityType , String uuid) throws BaasPersistentException, BaasEntityNotFoundException;

	public List<ODocument> list(String entityType) throws BaasPersistentException, BaasEntityNotFoundException;

	public long count(String entityType) throws BaasPersistentException;

	public boolean findAny(String entityType,String property, String value) throws BaasPersistentException;

	public boolean findAny(String entityType,Map<String, String> propertyToValue) throws BaasPersistentException;

	public void update(ODocument originalDocument, ODocument documentToMerge);

	public boolean exists(ODocument document) throws BaasException;

}
