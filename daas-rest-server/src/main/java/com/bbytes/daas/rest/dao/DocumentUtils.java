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
import java.util.Map;
import java.util.UUID;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.tx.OTransaction;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DocumentUtils {

	/**
	 * Create entity type if not available and index too
	 * 
	 * @param entityType
	 */
	public static void createEntityType(OGraphDatabase graphDatabase, String entityType) {

		// cannot create new class inside active transaction so we need to close the current
		// transaction and
		// then begin transaction once again after the class creation is done
		OTransaction transaction = graphDatabase.getTransaction();
		if (transaction != null && transaction.isActive()) {
			transaction.close();
		}

		OClass entityVertexType = graphDatabase.getVertexType(entityType);
		if (entityVertexType == null) {

			entityVertexType = graphDatabase.createVertexType(entityType);
			entityVertexType.createProperty(DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(), OType.STRING);
			entityVertexType.createProperty(DaasDefaultFields.FIELD_APPLICATION_NAME.toString(), OType.STRING);
			entityVertexType.createProperty(DaasDefaultFields.FIELD_UUID.toString(), OType.STRING);
			// create index - for app and org name
			entityVertexType.createIndex(entityType, OClass.INDEX_TYPE.NOTUNIQUE,
					DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(),
					DaasDefaultFields.FIELD_APPLICATION_NAME.toString());
			// create index - uuid based
			entityVertexType.createIndex(entityType + "." + DaasDefaultFields.FIELD_UUID.toString(),
					OClass.INDEX_TYPE.UNIQUE, DaasDefaultFields.FIELD_UUID.toString());

			// now we can begin the transaction
			if (transaction != null) {
				transaction.begin();
			}
		}
	}

	/**
	 * Create edge type if not available
	 * 
	 * @param edgeType
	 */
	public static void createEdgeType(OGraphDatabase graphDatabase, String edgeType) {

		// cannot create new class inside active transaction so we need to close the current
		// transaction and
		// then begin transaction once again after the class creation is done
		OTransaction transaction = graphDatabase.getTransaction();
		if (transaction != null && transaction.isActive()) {
			transaction.close();
		}

		OClass entityEdgeType = graphDatabase.getEdgeType(edgeType);
		if (entityEdgeType == null) {
			entityEdgeType = graphDatabase.createEdgeType(edgeType);
		}

		// now we can begin the transaction
		if (transaction != null) {
			transaction.begin();
		}
	}

	public static ODocument applyDefaultFields(ODocument document, String entityType, String accountName, String appName) {

		if (document == null)
			return document;

		document.field(DaasDefaultFields.FIELD_CREATION_DATE.toString(), new Date());
		document.field(DaasDefaultFields.ENTITY_TYPE.toString(), entityType);
		document.field(DaasDefaultFields.FIELD_MODIFICATION_DATE.toString(), new Date());
		document.field(DaasDefaultFields.FIELD_ACCOUNT_NAME.toString(), accountName);
		document.field(DaasDefaultFields.FIELD_APPLICATION_NAME.toString(), appName);
		document.field(DaasDefaultFields.FIELD_UUID.toString(), UUID.randomUUID().toString());
		return document;
	}

	public static Map<String, Object> backupDefaultFields(ODocument document) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (DaasDefaultFields r : DaasDefaultFields.values()) {
			map.put(r.toString(), document.field(r.toString()));
		}
		return map;
	}

	public static ODocument restoreDefaultFields(ODocument document, Map<String, Object> map) {
		for (DaasDefaultFields r : DaasDefaultFields.values()) {
			document.fields(r.toString(), map.get(r.toString()));
		}
		return document;
	}

	public static ODocument update(ODocument originalDocument, ODocument documentToMerge) {
		// backup the baasbox's fields
		Map<String, Object> map = backupDefaultFields(originalDocument);
		// update the document
		originalDocument.merge(documentToMerge, false, false);
		// restore the baasbox's fields
		restoreDefaultFields(originalDocument, map);
		return originalDocument;
	}

	public static ODocument applyProperties(ODocument doc, final Object... iFields) {
		if (iFields != null)
			// SET THE FIELDS
			if (iFields != null)
				if (iFields.length == 1) {
					Object f = iFields[0];
					if (f instanceof Map<?, ?>)
						doc.fields((Map<String, Object>) f);
					else
						throw new IllegalArgumentException(
								"Invalid fields: expecting a pairs of fields as String,Object or a single Map<String,Object>, but found: "
										+ f);
				} else
					// SET THE FIELDS
					for (int i = 0; i < iFields.length; i += 2)
						doc.field(iFields[i].toString(), iFields[i + 1]);

		return doc;
	}

}
