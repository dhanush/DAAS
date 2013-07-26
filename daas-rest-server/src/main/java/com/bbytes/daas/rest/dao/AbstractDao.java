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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.transaction.annotation.Transactional;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.object.OObjectIteratorClassInterface;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Transactional("objectDB")
public class AbstractDao<E extends Serializable> extends OrientDbDaoSupport implements DaasDAO<E> {

	private final Class<E> entityType;

	@SuppressWarnings("unchecked")
	public AbstractDao() {
		super();
		this.entityType = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractDao.class);
	}

	@Override
	public void add(E entity) {
		getObjectDataBase().save(entity);
	}

	@Override
	public void update(E entity) {
		getObjectDataBase().save(entity);
	}

	@Override
	public void remove(E entity) {
		getObjectDataBase().delete(entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E find(ORID id) {
		return (E) getObjectDataBase().load(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<E> list() {
		OObjectIteratorClassInterface<E> listItr = getObjectDataBase().browseClass(this.entityType);
		List<E> result = IteratorUtils.toList(listItr);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#count()
	 */
	@Override
	public long count() {
		long count = getObjectDataBase().countClass(this.entityType.getSimpleName());
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.DaasDAO#find(java.lang.String)
	 */
	@Override
	public E find(String uuid) {
		List<E> result = orientDbTemplate.getObjectDatabase().query(
				new OSQLSynchQuery<E>("select * from " + this.entityType.getSimpleName() + " where uuid = '" + uuid
						+ "'"));
		if (result != null && result.size() > 0)
			return result.get(0);
		
		return null;
	}
}
