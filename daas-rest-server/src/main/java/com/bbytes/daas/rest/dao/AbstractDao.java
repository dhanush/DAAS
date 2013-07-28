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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.springframework.core.GenericTypeResolver;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.Entity;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.object.OObjectIteratorClassInterface;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Transactional("objectDB")
public class AbstractDao<E extends Entity> extends OrientDbDaoSupport implements DaasDAO<E> {

	private static final Logger LOG = Logger.getLogger(AbstractDao.class);

	private final Class<E> entityType;

	@SuppressWarnings("unchecked")
	public AbstractDao() {
		super();
		this.entityType = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractDao.class);
	}

	@Override
	public E save(E entity) throws BaasPersistentException {
		entity.setUuid(UUID.randomUUID().toString());
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		return getObjectDataBase().save(entity);
	}

	@Override
	public E update(E entity) throws BaasPersistentException {
		entity.setModificationDate(new Date());
		return getObjectDataBase().save(entity);
	}

	@Override
	public void remove(E entity) throws BaasPersistentException {
		getObjectDataBase().delete(entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E find(ORID id) throws BaasPersistentException, BaasEntityNotFoundException {
		return (E) getObjectDataBase().load(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<E> list() throws BaasPersistentException, BaasEntityNotFoundException {
		OObjectIteratorClassInterface<E> listItr = getObjectDataBase().browseClass(this.entityType);
		List<E> result = IteratorUtils.toList(listItr);
		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#count()
	 */
	@Override
	public long count() throws BaasPersistentException {
		long count = getObjectDataBase().countClass(this.entityType.getSimpleName());
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String)
	 */
	@Override
	public E find(String uuid) throws BaasPersistentException, BaasEntityNotFoundException {
		List<E> result = getObjectDataBase().query(
				new OSQLSynchQuery<E>("select * from " + this.entityType.getSimpleName() + " where uuid = '" + uuid
						+ "'"));

		if (result == null || result.size() == 0)
			throw new BaasEntityNotFoundException("Entity not found " + this.entityType.getSimpleName());

		return result.get(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#isAvailable(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean findAny(String property, String value) throws BaasPersistentException {
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
	public boolean findAny(Map<String, String> propertyToValue) throws BaasPersistentException {

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

		String sql = "SELECT COUNT(*) as count FROM " + this.entityType.getSimpleName() + "  WHERE " + whereCondition;
		long count = ((ODocument) getObjectDataBase().query(new OSQLSynchQuery<E>(sql)).get(0))
				.field("count");
		LOG.debug("SQL Result : " + sql + "  - Result - " + count);
		if (count == 0)
			return false;

		return true;
	}

}
