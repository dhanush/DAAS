/*
 * Copyright (C) 2013 The Zorba Open Source Project 
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
package com.bbytes.daas.rest.dao;

import java.util.List;
import java.util.Map;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Document Dao Impl
 *
 * @author Thanneer
 *
 * @version 1.0.0
 */
public class DocumentDaoImpl implements DocumentDao {

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#create(java.lang.String)
	 */
	@Override
	public ODocument create(String entityType,Map<String,Object> propertyMap) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#save(com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public ODocument save(ODocument entity) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public ODocument update(ODocument entity) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public ODocument update(ODocument entity , Map<String,Object> propertyMap) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#remove(com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public void remove(ODocument entity) throws BaasPersistentException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(com.orientechnologies.orient.core.id.ORID)
	 */
	@Override
	public ODocument find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(java.lang.String)
	 */
	@Override
	public ODocument find(String uuid) throws BaasPersistentException, BaasEntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#list()
	 */
	@Override
	public List<ODocument> list() throws BaasPersistentException, BaasEntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#count()
	 */
	@Override
	public long count() throws BaasPersistentException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findAny(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean findAny(String property, String value) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findAny(java.util.Map)
	 */
	@Override
	public boolean findAny(Map<String, String> propertyToValue) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#checkEntityDocument(com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public void checkEntityDocument(ODocument doc) throws BaasException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl.ODocument, com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public void update(ODocument originalDocument, ODocument documentToMerge) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bbytes.daas.rest.dao.DocumentDao#exists(com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public boolean exists(ODocument document) throws BaasException {
		// TODO Auto-generated method stub
		return false;
	}



}
