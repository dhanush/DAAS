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

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.domain.Entity;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.service.DaasGenericList;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public class AbstractDao<E extends Entity> extends OrientDbDaoSupport implements DaasDAO<E> {

	private static final Logger LOG = Logger.getLogger(AbstractDao.class);

	private final Class<E> entityType;

	@Autowired
	protected ConversionService conversionService;

	@SuppressWarnings("unchecked")
	public AbstractDao() {
		super();
		this.entityType = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractDao.class);
	}

	@Override
	@Transactional
	public E save(E entity) throws BaasPersistentException {
		
		ODatabaseRecord db = getDocumentDatabase();
		try {
			entity.setUuid(UUID.randomUUID().toString());
			entity.setCreationDate(new Date());
			entity.setModificationDate(new Date());
			return convertToEntity((ODocument) db.save(convert(entity)));
		} finally {
			db.close();
		}
	}

	@Override
	@Transactional
	public E update(E entity) throws BaasPersistentException {
		ODatabaseRecord db = getDocumentDatabase();
		try {
			entity.setModificationDate(new Date());
			return convertToEntity((ODocument) db.save(convert(entity)));
		} finally {
			db.close();
		}
		
		
	}

	@Override
	@Transactional
	public void remove(E entity) throws BaasPersistentException {
		ODatabaseRecord db = getDocumentDatabase();
		try {
			db.delete(convert(entity));
		} finally {
			db.close();
		}
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public E find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException {
		ODatabaseRecord db = getDocumentDatabase();
		try {
			return (E) db.load(id);
		} finally {
			db.close();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<E> findAll() throws BaasPersistentException, BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
			ORecordIteratorClass<ODocument> listItr = db.browseClass(this.entityType.getSimpleName());
			List<ODocument> result = IteratorUtils.toList(listItr);
			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException();

			return convertToEntity(result);
		} finally {
			db.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#count()
	 */
	@Override
	public long count() throws BaasPersistentException {
		OGraphDatabase db = getDataBase();
		try {
			long count = db.countClass(this.entityType.getSimpleName());
			return count;
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String)
	 */
	@Override
	public E find(String uuid) throws BaasPersistentException, BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
			List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>("select * from "
					+ this.entityType.getSimpleName() + " where uuid = '" + uuid + "'"));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException("Entity not found " + this.entityType.getSimpleName());

			return convertToEntity(result.get(0));
		} finally {
			db.close();
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

			String sql = "SELECT COUNT(*) as count FROM " + this.entityType.getSimpleName() + "  WHERE "
					+ whereCondition;
			long count = ((ODocument) db.query(new OSQLSynchQuery<E>(sql)).get(0)).field("count");
			LOG.debug("SQL Result : " + sql + "  - Result - " + count);
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
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String, java.lang.String)
	 */
	@Override
	public List<E> find(String property, String value) throws BaasEntityNotFoundException {
		OGraphDatabase db = getDataBase();
		try {
			List<ODocument> result = db.query(new OSQLSynchQuery<ODocument>("select * from "
					+ this.entityType.getSimpleName() + " where " + property + " = " + "'" + value + "'"));

			if (result == null || result.size() == 0)
				throw new BaasEntityNotFoundException("Entity not found " + this.entityType.getSimpleName());

			return convertToEntity(result);
		} finally {
			db.close();
		}
	}

	/**
	 * @param entity
	 * @return
	 */
	protected ODocument convert(E entity) {
		return (ODocument) conversionService.convert(entity, TypeDescriptor.valueOf(Entity.class),
				TypeDescriptor.valueOf(ODocument.class));
	}

	/**
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected E convertToEntity(ODocument entity) {
		return (E) conversionService.convert(entity, TypeDescriptor.valueOf(ODocument.class),
				TypeDescriptor.valueOf(Entity.class));
	}

	/**
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<E> convertToEntity(List<ODocument> entityList) {
		return (List<E>) conversionService.convert(entityList, DaasGenericList.class).getData();
	}

	protected List<E> detach(List<E> entityList, OObjectDatabaseTx db) {
		List<E> result = new ArrayList<>();
		for (E e : entityList) {
			e = db.detach(e, true);
			result.add(e);
		}

		return result;
	}

}
