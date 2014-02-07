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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.domain.Application;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Application DAO
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Repository
@Transactional
public class ApplicationDaoImpl extends AbstractDao<Application> implements ApplicationDao {

	@Override
	public Application save(Application app) throws BaasPersistentException {
		// check if the org name and app name combo is unique
		Map<String, String> propertyToValue = new HashMap<String, String>();
		propertyToValue.put("name", app.getName());
		propertyToValue.put("accountName", app.getAccountName());

		if (!findAny(propertyToValue)) {
			app = super.save(app);
		} else {
			throw new BaasPersistentException("Application name has to be unique,  " + app.getName()
					+ " under account " + app.getAccountName() + " is already taken ");
		}
		return app;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.rest.dao.ApplicationDao#findForAccount(java.lang.String)
	 */
	@Override
	public List<Application> findForAccount(String accountName) throws BaasPersistentException {

		OrientGraph db = getDataBase();
		try {
			String sql = "SELECT *  FROM " + Application.class.getSimpleName() + "  WHERE  accountName = " + "'"
					+ accountName + "'";

			List<Application> result = db.getRawGraph().query(new OSQLSynchQuery<Application>(sql));

			return result;
		} finally {
			if (db != null)
				db.shutdown();
		}
	}
}
