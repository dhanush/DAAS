/*
 * Copyright (C) 2013 The Zorba Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.bbytes.daas.db;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.dao.DocumentDao;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */

public class DocumentDaoTest  extends BaseDBTest{

	@Autowired
	private DocumentDao documentDao;
	
	@Test
	@Transactional
	public void testCreate() throws BaasPersistentException{
		ODocument doc = documentDao.create("test4", null, "orgName", "appName");
		assertNotNull(doc);
	}
}
