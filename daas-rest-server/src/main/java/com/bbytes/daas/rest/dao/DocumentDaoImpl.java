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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.SessionStore;
import com.bbytes.daas.rest.domain.AccountUser;
import com.bbytes.daas.rest.domain.Entity;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Document Dao Impl
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Repository
public class DocumentDaoImpl extends OrientDbDaoSupport implements DocumentDao {

	@Autowired
	private AccountUserDao userDao;

	@Autowired
	private SessionStore sessionStore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#create(java.lang.String)
	 */
	@Override
	public ODocument create(String entityType, Map<String, Object> propertyMap, String accountName, String appName)
			throws BaasPersistentException {

		initEntityType(entityType);

		ODocument entityVertex = null;
		if (propertyMap == null) {
			entityVertex = getGraphDataBase().createVertex(entityType);
		} else {
			entityVertex = getGraphDataBase().createVertex(entityType, propertyMap);
		}

		entityVertex.field(DocumentDao.FIELD_CREATION_DATE, new Date());
		entityVertex.field(DocumentDao.ENTITY_TYPE, entityType);
		entityVertex.field(DocumentDao.FIELD_MODIFICATION_DATE, new Date());
		entityVertex.field(DocumentDao.FIELD_ACCOUNT_NAME, accountName);
		entityVertex.field(DocumentDao.FIELD_APPLICATION_NAME, appName);
		entityVertex.field(DocumentDao.FIELD_UUID, UUID.randomUUID().toString());

		// ODocument currentUser = (ODocument) getObjectDataBase().getRecordByUserObject(
		// sessionStore.getSessionUser() ,false);

		ODocument currentUser = getDummyCurrentUser();

		initEdgeType(DocumentDao.ENTITY_CREATED);

		ODocument createdEdge = getGraphDataBase().createEdge(currentUser, entityVertex, DocumentDao.ENTITY_CREATED);

		entityVertex.save();
		createdEdge.save();

		// need to have another rest like /entity/connections
		// in connections and out connections to be displayed
		// System.out.println("out " + getGraphDataBase().getOutEdges(entityVertex.getIdentity()));
		// System.out.println("in "+ getGraphDataBase().getInEdges(entityVertex.getIdentity()));
		return entityVertex;
	}

	/**
	 * Create edge type if not available
	 * 
	 * @param edgeType
	 */
	private void initEdgeType(String edgeType) {
	
		
		OClass entityEdgeType = getGraphDataBase().getEdgeType(edgeType);
		if (entityEdgeType == null) {
			entityEdgeType = getGraphDataBase().createEdgeType(edgeType);
		}
	}

	/**
	 * Create entity type if not available and index too
	 * 
	 * @param entityType
	 */
	private void initEntityType(String entityType) {
		

		OClass entityVertexType = getGraphDataBase().getVertexType(entityType);
		if (entityVertexType == null) {
			
			entityVertexType = getGraphDataBase().createVertexType(entityType);
			entityVertexType.createProperty(DocumentDao.FIELD_ACCOUNT_NAME, OType.STRING);
			entityVertexType.createProperty(DocumentDao.FIELD_APPLICATION_NAME, OType.STRING);
			entityVertexType.createProperty(DocumentDao.FIELD_UUID, OType.STRING);
			// create index - for app and org name
			entityVertexType.createIndex(entityType, OClass.INDEX_TYPE.NOTUNIQUE, DocumentDao.FIELD_ACCOUNT_NAME,
					DocumentDao.FIELD_APPLICATION_NAME);
			// create index - uuid based
			entityVertexType.createIndex(entityType + "." + DocumentDao.FIELD_UUID, OClass.INDEX_TYPE.UNIQUE,
					DocumentDao.FIELD_UUID);

		}
	}

	// to be replaced by current session user
	@Deprecated
	private ODocument getDummyCurrentUser() throws BaasPersistentException {
		AccountUser user = new AccountUser();
		user.setEmail("test@test.com");
		user = userDao.update(user);
		return (ODocument) getObjectDataBase().getRecordByUserObject(user, false);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#save(com.orientechnologies.orient.core.record.impl.ODocument
	 * )
	 */
	@Override
	public ODocument save(ODocument entity) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl
	 * .ODocument)
	 */
	@Override
	public ODocument update(ODocument entity) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl
	 * .ODocument)
	 */
	@Override
	public ODocument update(ODocument entity, Map<String, Object> propertyMap) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#remove(com.orientechnologies.orient.core.record.impl
	 * .ODocument)
	 */
	@Override
	public void remove(ODocument entity) throws BaasPersistentException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(com.orientechnologies.orient.core.id.ORID)
	 */
	@Override
	public ODocument find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl
	 * .ODocument, com.orientechnologies.orient.core.record.impl.ODocument)
	 */
	@Override
	public void update(ODocument originalDocument, ODocument documentToMerge) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#exists(com.orientechnologies.orient.core.record.impl
	 * .ODocument)
	 */
	@Override
	public boolean exists(ODocument document) throws BaasException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(java.lang.String, java.lang.String)
	 */
	@Override
	public ODocument find(String entityType, String uuid) throws BaasPersistentException, BaasEntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#list(java.lang.String)
	 */
	@Override
	public List<ODocument> list(String entityType) throws BaasPersistentException, BaasEntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#count(java.lang.String)
	 */
	@Override
	public long count(String entityType) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findAny(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean findAny(String entityType, String property, String value) throws BaasPersistentException {
		OIndex<?> oIndex = getGraphDataBase().getMetadata().getIndexManager().getIndex(entityType + "." + property);
		if (oIndex == null)
			throw new BaasPersistentException("Index for " + entityType + "." + property + " is missing");
		Collection<String> values = new ArrayList<>();
		values.add(value);
		Collection<OIdentifiable> oIdentifiable = oIndex.getValues(values);
		if (oIdentifiable != null)
			return true;

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findAny(java.lang.String, java.util.Map)
	 */
	@Override
	public boolean findAny(String entityType, Map<String, String> propertyToValue) throws BaasPersistentException {
		// TODO Auto-generated method stub
		return false;
	}

}
