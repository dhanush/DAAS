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
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.domain.Entity;
import com.bbytes.daas.rest.DaasEntityNotFoundException;
import com.bbytes.daas.rest.DaasPersistentException;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * 
 * 
 * @author Thanneer
 * @version 1.0.0
 */
@Transactional
public class AbstractDao<E extends Entity> extends OrientDbDaoSupport implements DaasDAO<E> {

	private static final Logger LOG = Logger.getLogger(AbstractDao.class);

	protected final Class<E> entityType;

	@Autowired
	protected ConversionService conversionService;

	@SuppressWarnings("unchecked")
	public AbstractDao() {
		super();
		this.entityType = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractDao.class);
	}
	
	@Override
	public E save(E entity) throws DaasPersistentException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		entity.setUuid(UUID.randomUUID().toString());
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		E e = dbTx.save(entity);
		return detach(e, dbTx);
	}

	@Override
	public E update(E entity) throws DaasPersistentException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		entity.setModificationDate(new Date());
		E e = dbTx.save(entity);
		return detach(e, dbTx);

	}

	@Override
	public void remove(E entity) throws DaasPersistentException {
		ODatabaseObject db = getObjectDatabase();
		db.delete(entity);
	}

	@Override
	public E find(ORID id) throws DaasPersistentException, DaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		E e = dbTx.load(id);
		return detach(e, dbTx);
	}

	@Override
	public List<E> findAll() throws DaasPersistentException, DaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
			List<E> result = dbTx.query(new OSQLSynchQuery<E>("select * from " + this.entityType.getSimpleName()));
			if (result == null || result.size() == 0)
				throw new DaasEntityNotFoundException();

			return detach(result, dbTx);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ArrayList<E>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#count()
	 */
	@Override
	public long count() throws DaasPersistentException {
		ODatabaseObject db = getObjectDatabase();
		long count = db.countClass(this.entityType.getSimpleName());
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String)
	 */
	@Override
	public E find(String uuid) throws DaasPersistentException, DaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
			List<E> result = dbTx.query(new OSQLSynchQuery<E>("select * from " + this.entityType.getSimpleName()
					+ " where uuid = '" + uuid + "'"));

			if (result == null || result.size() == 0)
				throw new DaasEntityNotFoundException("Entity not found " + this.entityType.getSimpleName());

			return detach(result.get(0), dbTx);

		} catch (Exception e) {
			throw new DaasEntityNotFoundException("Entity not found " + this.entityType.getSimpleName());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#isAvailable(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean findAny(String property, String value) {
		Map<String, String> propertyToValue = new HashMap<String, String>();
		propertyToValue.put(property, value);
		return findAny(propertyToValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#findAny(java.util.Map)
	 */
	@Override
	public boolean findAny(Map<String, String> propertyToValue) {

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

			String sql = "SELECT COUNT(*) as count FROM " + this.entityType.getSimpleName() + "  WHERE "
					+ whereCondition;
			long count = ((ODocument) db.getRawGraph().query(new OSQLSynchQuery<E>(sql)).get(0)).field("count");
			LOG.debug("SQL Result : " + sql + "  - Result - " + count);
			if (count == 0)
				return false;
			else
				return true;

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String, java.lang.String)
	 */
	@Override
	public List<E> find(String property, String value) throws DaasEntityNotFoundException {
		OObjectDatabaseTx dbTx = (OObjectDatabaseTx) getObjectDatabase();
		try {
			List<E> result = dbTx.query(new OSQLSynchQuery<E>("select * from " + this.entityType.getSimpleName()
					+ " where " + property + " = " + "'" + value + "'"));

			if (result == null || result.size() == 0)
				throw new DaasEntityNotFoundException("Entity not found " + this.entityType.getSimpleName());

			return detach(result, dbTx);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ArrayList<E>();
	}

	protected List<E> detach(List<E> entityList, OObjectDatabaseTx db) {
		List<E> result = new ArrayList<>();
		for (E e : entityList) {
			e = detach(e, db);
			result.add(e);
		}
		return result;

		// return db.detachAll(entityList, true);
	}

	protected E detach(E entity, OObjectDatabaseTx db) {
		return db.detachAll(entity, true);
	}
	
	
}
