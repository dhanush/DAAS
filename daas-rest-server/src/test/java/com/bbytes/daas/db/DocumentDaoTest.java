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
package com.bbytes.daas.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.dao.DaasDefaultFields;
import com.bbytes.daas.dao.DocumentDao;
import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */

public class DocumentDaoTest extends BaseDBTest {

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private OrientDbTemplate orientDbTemplate;

	private ODocument doc1;

	private ODocument doc2;

	private ODocument doc3;

	@Before
	public void setup() throws BaasPersistentException, BaasEntityNotFoundException {
		
		setAuthObjectForTest("ROLE_TENENT_ADMIN");
		
		String nullString = null;
		doc1 = documentDao.create("doc1", nullString, "accnName", "appName");

		doc2 = documentDao.create("doc2", nullString, "accName", "appName");

		doc3 = documentDao.create("doc3", nullString, "accName", "appName");
		
		
	}

	@Test
	@Transactional
	public void testCreate() throws BaasPersistentException {
		String nullString = null;
		ODocument doc = documentDao.create("test4", nullString, "accnName", "appName");
		assertNotNull(doc);
	}

	@Test
	public void testRelateEntity() throws BaasPersistentException {

		ODocument relateDoc = documentDao.relate("doc1",
				doc1.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "doc2",
				doc2.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "rel1");
		assertNotNull(relateDoc);

	}
	
	@Test
	public void testRelateEntityReverseQuery() throws BaasPersistentException, BaasEntityNotFoundException {

		ODocument relateDoc = documentDao.relate("doc1",
				doc1.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "doc2",
				doc2.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "rel1");
		
		List<ODocument> docs = documentDao.findRelatedReverse("doc2", doc2.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "rel1");
		assertNotNull(docs.get(0));

	}

	@Test
	public void testGraphQueryAndUpdateRelation() throws BaasPersistentException {

		ODocument relateDoc = documentDao.relate("doc1",
				doc1.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "doc2",
				doc2.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "rel1");

		assertNotNull(relateDoc);

		OrientGraph graph = orientDbTemplate.getDatabase();

		OrientVertex vertex = ((OrientVertex) graph.getVertex(doc1.getIdentity()));

		for (Vertex v : vertex.getVertices(Direction.OUT, "rel1")) {
			assertNotNull(v);
			assertEquals(vertex.getProperty(DaasDefaultFields.FIELD_UUID.toString()),
					doc1.field(DaasDefaultFields.FIELD_UUID.toString()));
			assertEquals(v.getProperty(DaasDefaultFields.FIELD_UUID.toString()),
					doc2.field(DaasDefaultFields.FIELD_UUID.toString()));
		}

		ODocument relateDoc2 = documentDao.relate("doc1", doc1.field(DaasDefaultFields.FIELD_UUID.toString())
				.toString(), "doc3", doc3.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "rel2");

		assertNotNull(relateDoc2);

		// need this to reuse the conn
		graph = orientDbTemplate.getDatabase();

		vertex = ((OrientVertex) graph.getVertex(doc1.getIdentity()));

		for (Vertex v : vertex.getVertices(Direction.OUT, "rel2")) {
			assertNotNull(v);
			assertEquals(vertex.getProperty(DaasDefaultFields.FIELD_UUID.toString()),
					doc1.field(DaasDefaultFields.FIELD_UUID.toString()));
			assertEquals(v.getProperty(DaasDefaultFields.FIELD_UUID.toString()),
					doc3.field(DaasDefaultFields.FIELD_UUID.toString()));

			// the relation ship to doc2 is overwritten by calling relate method for doc1 -> doc3
			assertNotEquals(v.getProperty(DaasDefaultFields.FIELD_UUID.toString()),
					doc2.field(DaasDefaultFields.FIELD_UUID.toString()));
		}

	}

	
	@Test(expected=BaasEntityNotFoundException.class)
	public void testFindByID() throws BaasPersistentException, BaasEntityNotFoundException {
		ODocument document = documentDao.findById("doc1", doc1.field(DaasDefaultFields.FIELD_UUID.toString()).toString());
		assertNotNull(document);
		
		ODocument document2 = documentDao.findById("doc1", "123");
		assertNotNull(document2);
	}
	
	
	@Test
	public void testRemoveRelation() throws BaasPersistentException {

		ODocument relateDoc = documentDao.relate("doc1",
				doc1.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "doc2",
				doc2.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "testrel1");

		assertNotNull(relateDoc);

		OrientGraph graph = orientDbTemplate.getDatabase();

		OrientVertex vertex = ((OrientVertex) graph.getVertex(doc1.getIdentity()));

		assertTrue(vertex.getVertices(Direction.OUT, "testrel1").iterator().hasNext());

		documentDao.removeRelation("doc1", doc1.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "doc2",
				doc2.field(DaasDefaultFields.FIELD_UUID.toString()).toString(), "testrel1");

		// need this to reuse the conn
		graph = orientDbTemplate.getDatabase();

		vertex = ((OrientVertex) graph.getVertex(doc1.getIdentity()));

		assertFalse(vertex.getVertices(Direction.OUT, "testrel1").iterator().hasNext());

	}

}
