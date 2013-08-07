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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#create(java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument create(String entityType, Map<String, Object> propertyMap, String accountName, String appName)
			throws BaasPersistentException {
		return createDocument(entityType, propertyMap, null, accountName, appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#create(java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument create(String entityType, String entityInJson, String accountName, String appName)
			throws BaasPersistentException {
		return createDocument(entityType, null, entityInJson, accountName, appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#create(com.orientechnologies.orient.core.record.impl
	 * .ODocument, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument create(ODocument entity, String accountName, String appName) throws BaasPersistentException {
		entity = DocumentUtils.applyDefaultFields(entity, entity.getClassName(), accountName, appName);
		return entity.save();
	}

	/**
	 * @param entityType
	 * @param propertyMap
	 * @param accountName
	 * @param appName
	 * @return
	 * @throws BaasPersistentException
	 */
	@Transactional
	private ODocument createDocument(String entityType, Map<String, Object> propertyMap, String entityInJson,
			String accountName, String appName) throws BaasPersistentException {

		DocumentUtils.createEntityType(getDataBase(), entityType);
		DocumentUtils.createEdgeType(getDataBase(), DaasDefaultFields.ENTITY_CREATED.toString());

		ODocument entityVertex = null;

		if (propertyMap == null) {
			entityVertex = getDataBase().createVertex(entityType);
		} else {
			entityVertex = getDataBase().createVertex(entityType, propertyMap);
		}

		if (entityInJson != null)
			entityVertex = entityVertex.fromJSON(entityInJson);

		entityVertex = DocumentUtils.applyDefaultFields(entityVertex, entityType, accountName, appName);

		// ODocument currentUser = (ODocument) getObjectDataBase().getRecordByUserObject(
		// sessionStore.getSessionUser() ,false);

		ODocument currentUser = userDao.getDummyCurrentUser();

		ODocument createdEdge = getDataBase().createEdge(currentUser, entityVertex,
				DaasDefaultFields.ENTITY_CREATED.toString());

		entityVertex.save();
		createdEdge.save();

		// need to have another rest like /entity/connections
		// in connections and out connections to be displayed
		// System.out.println("out " + getGraphDataBase().getOutEdges(entityVertex.getIdentity()));
		// System.out.println("in "+ getGraphDataBase().getInEdges(entityVertex.getIdentity()));
		return entityVertex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#relate(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument relate(String primartyEntityType, String primaryEntityId, String secondaryEntityType,
			String secondaryEntityId, String relationName) throws BaasPersistentException {
		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);
			ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);

			OrientGraph graph = new OrientGraph(getDataBase());
			graph.addVertex(null); // 1st OPERATION: IMPLICITLY BEGIN A TRANSACTION

			Edge edge = graph.addEdge(null, graph.getVertex(primaryEntity.getIdentity()),
					graph.getVertex(secondaryEntity.getIdentity()), relationName);
			graph.commit();
			return ((OrientEdge) edge).getRawEdge();

		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#removeRelation(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean removeRelation(String primartyEntityType, String primaryEntityId, String secondaryEntityType,
			String secondaryEntityId, String relationName) throws BaasPersistentException {
		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);
			// ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);

			OrientGraph graph = new OrientGraph(getDataBase());

			Vertex vertex = graph.getVertex(primaryEntity.getIdentity());

			for (Edge e : vertex.getEdges(Direction.OUT, relationName)) {
				graph.removeEdge(e);
			}

			return true;

		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findRelated(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<ODocument> findRelated(String primartyEntityType, String primaryEntityId, String relationName)
			throws BaasEntityNotFoundException {
		return findRelated(primartyEntityType, primaryEntityId, null, relationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findRelated(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<ODocument> findRelated(String primartyEntityType, String primaryEntityId, String secondaryEntityType,
			String relationName) throws BaasEntityNotFoundException {
		List<ODocument> result = new ArrayList<>();

		ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);

		OrientGraph graph = new OrientGraph(getDataBase());

		Vertex vertex = graph.getVertex(primaryEntity.getIdentity());

		for (Vertex v : vertex.getVertices(Direction.OUT, relationName)) {
			ODocument doc = ((OrientVertex) v).getRawVertex();
			if (secondaryEntityType == null
					|| doc.field(DaasDefaultFields.ENTITY_TYPE.toString()).equals(secondaryEntityType)) {
				result.add(doc);
			}
		}

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findRelatedReverse(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ODocument> findRelatedReverse(String secondaryEntityType, String secondaryEntityId,
			String primartyEntityType, String relationName) throws BaasEntityNotFoundException {
		List<ODocument> result = new ArrayList<>();

		ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);

		OrientGraph graph = new OrientGraph(getDataBase());

		Vertex vertex = graph.getVertex(secondaryEntity.getIdentity());

		for (Vertex v : vertex.getVertices(Direction.IN, relationName)) {
			ODocument doc = ((OrientVertex) v).getRawVertex();
			if (secondaryEntityType == null
					|| doc.field(DaasDefaultFields.ENTITY_TYPE.toString()).equals(primartyEntityType)) {
				result.add(doc);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findRelatedReverse(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ODocument> findRelatedReverse(String secondaryEntityType, String secondaryEntityId, String relationName)
			throws BaasEntityNotFoundException {
		return findRelatedReverse(secondaryEntityType, secondaryEntityId, null, relationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findAny(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean findAny(String entityType, String property, String value) throws BaasPersistentException {
		OIndex<?> oIndex = getDataBase().getMetadata().getIndexManager().getIndex(entityType + "." + property);
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

		if (propertyToValue == null)
			throw new IllegalArgumentException("Null value passed as arg");

		String whereCondition = "";
		int index = 0;
		for (Iterator<String> iterator = propertyToValue.keySet().iterator(); iterator.hasNext();) {
			String property = iterator.next();
			String value = propertyToValue.get(property);
			if (index == 0) {
				whereCondition = whereCondition + property + " = " + "'" + value + "'";
			} else {
				whereCondition = whereCondition + " and " + property + " = " + "'" + value + "'";
			}
			index++;

		}

		String sql = "SELECT COUNT(*) as count FROM " + entityType + "  WHERE " + whereCondition;
		long count = ((ODocument) getDataBase().query(new OSQLSynchQuery<ODocument>(sql)).get(0)).field("count");

		if (count == 0)
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl
	 * .ODocument, java.util.Map, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument update(ODocument docToBeUpdated, Map<String, Object> propertyMap, String accountName,
			String appName) throws BaasPersistentException {

		if (propertyMap != null)
			docToBeUpdated = DocumentUtils.applyProperties(docToBeUpdated, propertyMap);

		ODocument originalDoc = getDataBase().load(docToBeUpdated.getIdentity());
		if (originalDoc == null)
			throw new BaasPersistentException(
					"Document to be updated doesnt exist in DB , use create method to save the object to DB");

		ODocument docToBeSaved = DocumentUtils.update(originalDoc, docToBeUpdated);
		// update modification time
		docToBeSaved.field(DaasDefaultFields.FIELD_MODIFICATION_DATE.toString(), new Date());
		return docToBeSaved.save();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#update(com.orientechnologies.orient.core.record.impl
	 * .ODocument, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument update(ODocument entity, String accountName, String appName) throws BaasPersistentException {
		return update(entity, null, accountName, appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#update(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument update(String uuid, String entityType, String entityJson, String accountName, String appName)
			throws BaasPersistentException {
		ODocument originalDocument;
		try {
			originalDocument = findById(entityType, uuid);
		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		}
		ODocument documentToMerge = new ODocument().fromJSON(entityJson);
		documentToMerge = DocumentUtils.update(originalDocument, documentToMerge);
		return documentToMerge.save();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#remove(com.orientechnologies.orient.core.record.impl
	 * .ODocument, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void remove(ODocument entity, String accountName, String appName) throws BaasPersistentException {

		ODocument docToBeRemoved = getDataBase().load(entity.getIdentity());
		if (docToBeRemoved == null)
			throw new BaasPersistentException("Document to be deleted doesnt exist in DB");

		getDataBase().removeVertex(entity.getIdentity());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#remove(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public void remove(String uuid, String entityType, String accountName, String appName)
			throws BaasPersistentException {
		ODocument docToBeRemoved;
		try {
			docToBeRemoved = findById(entityType, uuid);
			if (docToBeRemoved == null)
				throw new BaasPersistentException("Document to be deleted doesnt exist in DB");
			getDataBase().removeVertex(docToBeRemoved.getIdentity());
		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(com.orientechnologies.orient.core.id.ORID)
	 */
	@Override
	public ODocument find(ORID id) throws BaasEntityNotFoundException {
		ODocument result = getDataBase().load(id);

		if (result == null)
			throw new BaasEntityNotFoundException();

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(java.lang.String, java.lang.String)
	 */
	@Override
	public ODocument findById(String entityType, String uuid) throws BaasEntityNotFoundException {
		String sql = "SELECT * FROM " + entityType + "  WHERE " + DaasDefaultFields.FIELD_UUID + " = " + "'" + uuid
				+ "'";
		List<ODocument> result = getDataBase().query(new OSQLSynchQuery<ODocument>(sql));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException();

		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findByProperty(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<ODocument> findByProperty(String entityType, String propertyName, String propertyValue)
			throws BaasEntityNotFoundException {

		String sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " = " + "'" + propertyValue + "'";
		List<ODocument> result = getDataBase().query(new OSQLSynchQuery<ODocument>(sql));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("No entity found for given property name " + propertyName
					+ " and value " + propertyValue + "for entity type " + entityType);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findByProperty(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ODocument> findByProperty(String applicationName, String entityType, String propertyName,
			String propertyValue) throws BaasEntityNotFoundException {
		String sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " = " + "'" + propertyValue + "'"
				+ " and " + DaasDefaultFields.FIELD_APPLICATION_NAME.toString()+" = " +"'" + applicationName+"'";
		
		List<ODocument> result = getDataBase().query(new OSQLSynchQuery<ODocument>(sql));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("No entity found for given property name " + propertyName
					+ " and value " + propertyValue + "for entity type " + entityType);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#list(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ODocument> list(String entityType, String appName) throws BaasPersistentException {
		String sql = "SELECT * FROM " + entityType + "  WHERE " + DaasDefaultFields.FIELD_APPLICATION_NAME + " = "
				+ "'" + appName + "'";
		List<ODocument> result = getDataBase().query(new OSQLSynchQuery<ODocument>(sql));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#count(java.lang.String, java.lang.String)
	 */
	@Override
	public long count(String entityType, String appName) {
		String sql = "SELECT COUNT(*) as count FROM " + entityType + "  WHERE "
				+ DaasDefaultFields.FIELD_APPLICATION_NAME + " = " + "'" + appName + "'";
		long count = ((ODocument) getDataBase().query(new OSQLSynchQuery<ODocument>(sql)).get(0)).field("count");
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#exists(com.orientechnologies.orient.core.record.impl
	 * .ODocument)
	 */
	@Override
	public boolean exists(ODocument document) {
		return exists(document.getRecord().getIdentity());
	}

	@Override
	public boolean exists(ORID rid) {
		ODocument doc = null;
		try {
			doc = find(rid);
		} catch (BaasEntityNotFoundException e) {
			return false;
		}
		return (doc != null);
	}

}
