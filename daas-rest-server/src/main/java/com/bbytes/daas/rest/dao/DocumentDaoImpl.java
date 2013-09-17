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
package com.bbytes.daas.rest.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.DaasUser;
import com.bbytes.daas.service.SecurityService;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
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
	private SecurityService securityService;

	@Autowired
	private ConversionService conversionService;

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

		OGraphDatabase db = null;

		try {
			db = getDataBase();
			DocumentUtils.createEntityType(db, entityType);
			DocumentUtils.createEdgeType(db, DaasDefaultFields.ENTITY_CREATED.toString());

			ODocument entityVertex = null;

			if (propertyMap == null) {
				entityVertex = db.createVertex(entityType);
			} else {
				entityVertex = db.createVertex(entityType, propertyMap);
			}

			if (entityInJson != null)
				entityVertex = entityVertex.fromJSON(entityInJson);

			entityVertex = DocumentUtils.applyDefaultFields(entityVertex, entityType, accountName, appName);

			// ODocument currentUser = (ODocument) getObjectDataBase().getRecordByUserObject(
			// sessionStore.getSessionUser() ,false);

			DaasUser currentDaasUser = null;
			try {
				currentDaasUser = securityService.getLoggedInUser();
				if (currentDaasUser == null) {
					throw new BaasPersistentException("User is not Logged in");
				}

				// fix for close db conn , this will reopen the conn
				db = getDataBase();

				ODocument currentUser = conversionService.convert(currentDaasUser, ODocument.class);
				ODocument createdEdge = db.createEdge(currentUser, entityVertex,
						DaasDefaultFields.ENTITY_CREATED.toString());
				entityVertex.save();
				createdEdge.save();

				// need to have another rest like /entity/connections
				// in connections and out connections to be displayed
				// System.out.println("out " +
				// getGraphDataBase().getOutEdges(entityVertex.getIdentity()));
				// System.out.println("in "+
				// getGraphDataBase().getInEdges(entityVertex.getIdentity()));
				return entityVertex;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new BaasPersistentException(e);
			}
		} finally {
			if (db != null)
				db.close();
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

		OGraphDatabase db = null;
		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);
			ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);

			db = getDataBase();
			OrientGraph graph = new OrientGraph(db);
			graph.addVertex(null); // 1st OPERATION: IMPLICITLY BEGIN A TRANSACTION

			Edge edge = graph.addEdge(null, graph.getVertex(primaryEntity.getIdentity()),
					graph.getVertex(secondaryEntity.getIdentity()), relationName);
			graph.commit();
			return ((OrientEdge) edge).getRecord();

		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		} finally {
			if (db != null)
				db.close();
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

		OGraphDatabase db = null;

		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);
			// ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);

			db = getDataBase();
			OrientGraph graph = new OrientGraph(db);

			Vertex vertex = graph.getVertex(primaryEntity.getIdentity());

			for (Edge e : vertex.getEdges(Direction.OUT, relationName)) {
				graph.removeEdge(e);
			}

			return true;

		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		} finally {
			if (db != null)
				db.close();
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

		OGraphDatabase db = null;
		try {
			ODocument primaryEntity = findById(primartyEntityType, primaryEntityId);

			db = getDataBase();
			OrientGraph graph = new OrientGraph(db);

			Vertex vertex = graph.getVertex(primaryEntity.getIdentity());

			for (Vertex v : vertex.getVertices(Direction.OUT, relationName)) {
				ODocument doc = ((OrientVertex) v).getRecord();
				if (secondaryEntityType == null
						|| doc.field(DaasDefaultFields.ENTITY_TYPE.toString()).equals(secondaryEntityType)) {
					result.add(doc);
				}
			}

			return result;
		} finally {
			if (db != null)
				db.close();
		}

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

		OGraphDatabase db = null;
		try {
			ODocument secondaryEntity = findById(secondaryEntityType, secondaryEntityId);
			db = getDataBase();
			OrientGraph graph = new OrientGraph(db);
			Vertex vertex = graph.getVertex(secondaryEntity.getIdentity());
			for (Vertex v : vertex.getVertices(Direction.IN, relationName)) {
				ODocument doc = ((OrientVertex) v).getRecord();
				if (primartyEntityType == null
						|| doc.field(DaasDefaultFields.ENTITY_TYPE.toString()).equals(primartyEntityType)) {
					result.add(doc);
				}
			}

			return result;
		} finally {
			db.close();
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
	public boolean findAny(String entityType, String property, String value) throws BaasPersistentException {
		OGraphDatabase db = getDataBase();
		try {
			OIndex<?> oIndex = db.getMetadata().getIndexManager().getIndex(entityType + "." + property);
			if (oIndex == null)
				throw new BaasPersistentException("Index for " + entityType + "." + property + " is missing");
			Collection<String> values = new ArrayList<>();
			values.add(value);
			Collection<OIdentifiable> oIdentifiable = oIndex.getValues(values);
			if (oIdentifiable != null)
				return true;

			return false;
		} finally {
			db.close();
		}

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

		OGraphDatabase db = getDataBase();
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
			long count = ((ODocument) getDataBase().query(new OSQLSynchQuery<ODocument>(sql)).get(0)).field("count");

			if (count == 0)
				return false;

			return true;
		} finally {
			db.close();
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

		OGraphDatabase db = getDataBase();
		try {
			if (propertyMap != null)
				docToBeUpdated = DocumentUtils.applyProperties(docToBeUpdated, propertyMap);

			ODocument originalDoc = db.load(docToBeUpdated.getIdentity());
			if (originalDoc == null)
				throw new BaasPersistentException(
						"Document to be updated doesnt exist in DB , use create method to save the object to DB");

			ODocument docToBeSaved = DocumentUtils.update(originalDoc, docToBeUpdated);
			// update modification time
			docToBeSaved.field(DaasDefaultFields.FIELD_MODIFICATION_DATE.toString(), new Date());
			return docToBeSaved.save();
		} finally {
			db.close();
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
	 * @see
	 * com.bbytes.daas.rest.dao.DocumentDao#remove(com.orientechnologies.orient.core.record.impl
	 * .ODocument, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void remove(ODocument entity, String accountName, String appName) throws BaasPersistentException {

		OGraphDatabase db = getDataBase();
		try {
			ODocument docToBeRemoved = db.load(entity.getIdentity());
			if (docToBeRemoved == null)
				throw new BaasPersistentException("Document to be deleted doesnt exist in DB");

			db.removeVertex(entity.getIdentity());

		} finally {
			db.close();
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
		OGraphDatabase db = null;
		ODocument docToBeRemoved;
		try {
			docToBeRemoved = findById(entityType, uuid);
			if (docToBeRemoved == null)
				throw new BaasPersistentException("Document to be deleted doesnt exist in DB");
			db = getDataBase();
			db.removeVertex(docToBeRemoved.getIdentity());
		} catch (BaasEntityNotFoundException e) {
			throw new BaasPersistentException(e);
		} finally {
			if (db != null)
				db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(com.orientechnologies.orient.core.id.ORID)
	 */
	@Override
	public ODocument find(ORID id) throws BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
			ODocument result = db.load(id);

			if (result == null)
				throw new BaasEntityNotFoundException();

			return result;

		} finally {
			db.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#find(java.lang.String, java.lang.String)
	 */
	@Override
	public ODocument findById(String entityType, String uuid) throws BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + DaasDefaultFields.FIELD_UUID + " = " + "'" + uuid
					+ "'";
			List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(sql));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException();

			return result.get(0);
		} finally {
			db.close();
		}
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

		OGraphDatabase db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " = " + "'" + propertyValue + "'";
			List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(sql));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException("No entity found for given property name " + propertyName
						+ " and value " + propertyValue + "for entity type " + entityType);

			return result;
		} finally {
			db.close();
		}
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
		OGraphDatabase db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + propertyName + " = " + "'" + propertyValue + "'"
					+ " and " + DaasDefaultFields.FIELD_APPLICATION_NAME.toString() + " = " + "'" + applicationName
					+ "'";

			List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(sql));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException("No entity found for given property name " + propertyName
						+ " and value " + propertyValue + "for entity type " + entityType);

			return result;
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#list(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ODocument> list(String entityType, String appName) throws BaasPersistentException {
		OGraphDatabase db = getDataBase();
		try {
			String sql = "SELECT * FROM " + entityType + "  WHERE " + DaasDefaultFields.FIELD_APPLICATION_NAME + " = "
					+ "'" + appName + "'";
			List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>(sql));
			return result;
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DocumentDao#count(java.lang.String, java.lang.String)
	 */
	@Override
	public long count(String entityType, String appName) {

		OGraphDatabase db = getDataBase();
		try {
			String sql = "SELECT COUNT(*) as count FROM " + entityType + "  WHERE "
					+ DaasDefaultFields.FIELD_APPLICATION_NAME + " = " + "'" + appName + "'";
			long count = ((ODocument) db.query(new OSQLSynchQuery<ODocument>(sql)).get(0)).field("count");
			return count;
		} finally {
			db.close();
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

}
