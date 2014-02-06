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
package com.bbytes.daas.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.domain.DataType;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery;
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

	protected String fetchPlan = ":0";

	private Logger log = Logger.getLogger(DocumentDaoImpl.class);

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

		OrientGraph db = null;

		try {
			db = getDataBase();
			DocumentUtils.createEntityType(db, entityType);
			DocumentUtils.createEdgeType(db, DaasDefaultFields.ENTITY_CREATED.toString());

			ODocument entityVertex = null;

			if (propertyMap == null) {
				entityVertex = new ODocument(entityType);
			} else {
				entityVertex = new ODocument(entityType, propertyMap);
			}

			if (entityInJson != null)
				entityVertex = entityVertex.fromJSON(entityInJson);

			entityVertex = DocumentUtils.applyDefaultFields(entityVertex, entityType, accountName, appName);

			// // logic to relate the current user to this entity using edge CREATED . Mainly for
			// audit purpose.
			// DaasUser currentDaasUser = null;
			// try {
			// currentDaasUser = securityService.getLoggedInUser();
			// if (currentDaasUser == null) {
			// throw new BaasPersistentException("User is not Logged in");
			// }
			//
			// // fix for close db conn , this will reopen the conn
			// db = getDataBase();
			//
			// // add entity owner as a vertex with edge as 'created'
			// Map<String, Object> ownerPropertyMap = new HashMap<String, Object>();
			// ownerPropertyMap.put(DaasDefaultFields.ENTITY_TYPE.toString(),
			// DaasUser.class.getSimpleName());
			// ownerPropertyMap.put("owner_uuid", currentDaasUser.getUuid());
			// ownerPropertyMap.put("owner_username", currentDaasUser.getUserName());
			// ODocument entityOwnerVertex = db.createVertex(entityType, ownerPropertyMap);
			//
			// ODocument createdEdge = db.createEdge(entityOwnerVertex, entityVertex,
			// DaasDefaultFields.ENTITY_CREATED.toString());
			// entityOwnerVertex.save();
			entityVertex.save();
			// createdEdge.save();

			/*
			 * // need to have another rest like /entity/connections // in connections and out
			 * connections to be displayed // System.out.println("out " + //
			 * getGraphDataBase().getOutEdges(entityVertex.getIdentity())); //
			 * System.out.println("in "+ //
			 * getGraphDataBase().getInEdges(entityVertex.getIdentity()));
			 */
			return entityVertex;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BaasPersistentException(e);
		} finally {
			closeDB(db);
		}
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

		OrientGraph db = null;
		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);
			ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);

			db = getDataBase();
			db.addVertex(null); // 1st OPERATION: IMPLICITLY BEGIN A TRANSACTION

			Edge edge = db.addEdge(null, db.getVertex(primaryEntity.getIdentity()),
					db.getVertex(secondaryEntity.getIdentity()), relationName);
			db.commit();
			return ((OrientEdge) edge).getRecord();

		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		} finally {
			closeDB(db);
		}
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

		OrientGraph db = getDataBase();
		try {
			ODocument docToBeRemoved = db.getRawGraph().load(entity.getIdentity());
			if (docToBeRemoved == null)
				throw new BaasPersistentException("Document to be deleted doesnt exist in DB");

			db.getRawGraph().delete(entity.getIdentity());
			db.commit();
		} finally {
			closeDB(db);
		}

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
		OrientGraph db = null;
		ODocument docToBeRemoved;
		try {
			docToBeRemoved = findById(entityType, uuid);
			if (docToBeRemoved == null)
				throw new BaasPersistentException("Document to be deleted doesnt exist in DB");
			db = getDataBase();

			Vertex vertex = db.getVertex(docToBeRemoved.getIdentity());
			db.removeVertex(vertex);
			db.commit();
		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		} finally {
			closeDB(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#removeRelation(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public boolean removeRelation(String primartyEntityType, String primaryEntityId, String secondaryEntityType,
			String secondaryEntityId, String relationName) throws BaasPersistentException {

		OrientGraph db = null;

		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);
			ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);
			if (primaryEntity == null || secondaryEntity == null)
				return false;

			db = getDataBase();

			Vertex vertex = db.getVertex(primaryEntity.getIdentity());
			Vertex secondartyVertex = db.getVertex(secondaryEntity.getIdentity());

			for (Edge e : vertex.getEdges(Direction.OUT, relationName)) {
				Vertex secondary = e.getVertex(Direction.IN);
				if (secondartyVertex != null && secondary != null && secondartyVertex.getId().equals(secondary.getId())) {
					db.removeEdge(e);
				}
			}
			db.commit();
			return true;

		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		} finally {
			closeDB(db);
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
	@Transactional
	public List<ODocument> findRelated(String primartyEntityType, String primaryEntityId, String secondaryEntityType,
			String relationName) throws BaasEntityNotFoundException {
		List<ODocument> result = new ArrayList<>();

		OrientGraph db = null;
		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);

			db = getDataBase();

			Vertex vertex = db.getVertex(primaryEntity.getIdentity());

			for (Vertex v : vertex.getVertices(Direction.OUT, relationName)) {
				ODocument doc = ((OrientVertex) v).getRecord();
				if (secondaryEntityType == null
						|| doc.field(DaasDefaultFields.ENTITY_TYPE.toString()).equals(secondaryEntityType)) {
					result.add(doc);
				}
			}

			return result;
		} finally {
			closeDB(db);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findRelatedReverse(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public List<ODocument> findRelatedReverse(String secondaryEntityType, String secondaryEntityId,
			String primartyEntityType, String relationName) throws BaasEntityNotFoundException {
		List<ODocument> result = new ArrayList<>();

		OrientGraph db = null;
		try {
			ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);
			db = getDataBase();
			Vertex vertex = db.getVertex(secondaryEntity.getIdentity());
			for (Vertex v : vertex.getVertices(Direction.IN, relationName)) {
				ODocument doc = ((OrientVertex) v).getRecord();
				if (primartyEntityType == null
						|| doc.field(DaasDefaultFields.ENTITY_TYPE.toString()).equals(primartyEntityType)) {
					result.add(doc);
				}
			}

			return result;
		} finally {
			closeDB(db);
		}
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
	@Transactional
	public boolean findAny(String entityType, String property, String value) throws BaasPersistentException {
		Map<String, String> propertyToValue = new HashMap<String, String>();
		propertyToValue.put(property, value);
		return findAny(entityType, propertyToValue);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findAny(java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional
	public boolean findAny(String entityType, Map<String, String> propertyToValue) throws BaasPersistentException {

		if (propertyToValue == null)
			throw new IllegalArgumentException("Null value passed as arg");

		OrientGraph db = getDataBase();
		try {
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
			OSQLSynchQuery<ODocument> synchQuery = new OSQLSynchQuery<ODocument>(sql);
			synchQuery.setFetchPlan(fetchPlan);

			long count = ((ODocument) db.getRawGraph().query(synchQuery).get(0)).field("count");

			if (count == 0)
				return false;

			return true;
		} finally {
			closeDB(db);
		}
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

		OrientGraph db = getDataBase();
		try {
			if (propertyMap != null)
				docToBeUpdated = DocumentUtils.applyProperties(docToBeUpdated, propertyMap);

			ODocument originalDoc = db.getRawGraph().load(docToBeUpdated.getIdentity());
			if (originalDoc == null)
				throw new BaasPersistentException(
						"Document to be updated doesnt exist in DB , use create method to save the object to DB");

			ODocument docToBeSaved = DocumentUtils.update(originalDoc, docToBeUpdated);
			// update modification time
			docToBeSaved.field(DaasDefaultFields.FIELD_MODIFICATION_DATE.toString(), new Date());
			return docToBeSaved.save();
		} finally {
			closeDB(db);
		}

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
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(com.orientechnologies.orient.core.id.ORID)
	 */
	@Override
	@Transactional
	public ODocument find(ORID id) throws BaasEntityNotFoundException {
		OrientGraph db = getDataBase();
		try {
			ODocument result = db.getRawGraph().load(id);

			if (result == null)
				throw new BaasEntityNotFoundException();

			return result;

		} finally {
			closeDB(db);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public ODocument findById(String entityType, String uuid) throws BaasEntityNotFoundException {
		OrientGraph db = getDataBase();

		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + DaasDefaultFields.FIELD_UUID + " = " + "'" + uuid
					+ "'";
			OSQLSynchQuery<ODocument> synchQuery = new OSQLSynchQuery<ODocument>(sql);
			synchQuery.setFetchPlan(fetchPlan);
			synchQuery.setLimit(1);
			synchQuery.setUseCache(false);
			List<ODocument> result = db.getRawGraph().query(synchQuery);

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException();

			return result.get(0);
		} finally {
			closeDB(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findByProperty(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public List<ODocument> findByProperty(String entityType, String propertyName, String propertyValue)
			throws BaasEntityNotFoundException {

		OrientGraph db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " = " + "'" + propertyValue + "'";
			OSQLAsynchQuery<ODocument> asynchQuery = new OSQLSynchQuery<ODocument>(sql);
			asynchQuery.setFetchPlan(fetchPlan);
			List<ODocument> result = db.getRawGraph().query(asynchQuery);

			return result;
		} finally {
			closeDB(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#findByProperty(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public List<ODocument> findByProperty(String applicationName, String entityType, String propertyName,
			String propertyValue) throws BaasEntityNotFoundException {
		OrientGraph db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " = " + "'" + propertyValue + "'"
					+ " and " + DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = " + "'" + applicationName
					+ "'";
			OSQLAsynchQuery<ODocument> asynchQuery = new OSQLSynchQuery<ODocument>(sql);
			asynchQuery.setFetchPlan(fetchPlan);

			List<ODocument> result = db.getRawGraph().query(asynchQuery);

			return result;
		} finally {
			closeDB(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#findByPropertyRange(java.lang.String,java.lang.String,
	 * java.lang.String,com.bbytes.daas.domain.DataType, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public List<ODocument> findByPropertyRange(String applicationName, String entityType, String propertyName,
			DataType propertyDataType, String startRange, String endRange) throws BaasEntityNotFoundException {
		if (startRange == null && endRange == null) {
			throw new IllegalArgumentException("Start range and End range cannot be null");
		}
		OrientGraph db = getDataBase();
		switch (propertyDataType) {
		case DATE:
			propertyName = propertyName + ".asDate()";
			break;
		case DATETIME:
			propertyName = propertyName + ".asDateTime()";
			break;
		case BOOLEAN:
			propertyName = propertyName + ".asBoolean()";
			break;
		case FLOAT:
			propertyName = propertyName + ".asFloat()";
			break;
		case INTEGER:
			propertyName = propertyName + ".asInteger()";
			break;
		case LONG:
			propertyName = propertyName + ".asLong()";
			break;
		default:
			propertyName = propertyName + ".asString()";
			break;
		}
		try {
			String sql = "";
			if (startRange == null) {
				sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " <= " + "'" + endRange + "'"
						+ " and " + DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = " + "'" + applicationName
						+ "'";
			} else if (endRange == null) {
				sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " >= " + "'" + startRange + "'"
						+ " and " + DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = " + "'" + applicationName
						+ "'";
			} else {
				sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " >= " + "'" + startRange + "'"
						+ " and " + propertyName + " <= " + "'" + endRange + "'" + " and "
						+ DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = " + "'" + applicationName + "'";
			}

			OSQLAsynchQuery<ODocument> asynchQuery = new OSQLSynchQuery<ODocument>(sql);
			asynchQuery.setFetchPlan(fetchPlan);

			List<ODocument> result = db.getRawGraph().query(asynchQuery);

			return result;
		} finally {
			closeDB(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#list(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public List<ODocument> list(String entityType, String appName) throws BaasPersistentException {
		OrientGraph db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + DaasDefaultFields.FIELD_APPLICATION_NAME + " = "
					+ "'" + appName + "'";

			OSQLAsynchQuery<ODocument> asynchQuery = new OSQLSynchQuery<ODocument>(sql);
			asynchQuery.setFetchPlan(fetchPlan);

			List<ODocument> result = db.getRawGraph().query(asynchQuery);
			return result;
		} finally {
			closeDB(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#count(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public long count(String entityType, String appName) {

		OrientGraph db = getDataBase();
		try {
			String sql = "SELECT COUNT(*) as count FROM " + entityType + "  WHERE "
					+ DaasDefaultFields.FIELD_APPLICATION_NAME + " = " + "'" + appName + "'";

			OSQLAsynchQuery<ODocument> asynchQuery = new OSQLSynchQuery<ODocument>(sql);
			asynchQuery.setFetchPlan(fetchPlan);

			long count = ((ODocument) db.getRawGraph().query(asynchQuery).get(0)).field("count");
			return count;
		} finally {
			closeDB(db);
		}
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

	
	protected void closeDB(OrientGraph db) {
		if (db != null && !db.isClosed())
			db.shutdown();
	}

	
}
