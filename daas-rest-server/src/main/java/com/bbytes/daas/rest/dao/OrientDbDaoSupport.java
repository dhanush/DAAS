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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;

import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * Orient DB DAO support
 * 
 * @author Thanneer
 * 
 * @version
 */

public abstract class OrientDbDaoSupport extends DaoSupport {

	@Autowired
	protected OrientDbTemplate orientDbTemplate;

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		if (this.orientDbTemplate == null) {
			throw new IllegalArgumentException("'database' or 'orientDbTemplate' is required");
		}
	}

	protected OGraphDatabase getDataBase() {
		return orientDbTemplate.getDatabase();
	}

	protected ODatabaseRecord getDocumentDatabase() {
		return orientDbTemplate.getDocumentDatabase();
	}

}
