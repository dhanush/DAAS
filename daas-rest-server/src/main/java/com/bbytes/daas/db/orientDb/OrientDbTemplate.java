package com.bbytes.daas.db.orientDb;

/**
 * 
 * 
 * 
 * Helper class that simplifies OrientDb data access code.
 * 
 * @author Thanneer
 * 
 * @version
 */

import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;

@Component
public class OrientDbTemplate {

	public OrientDbTemplate() {
	}

	public OGraphDatabase getGraphDatabase() {
		return OGraphDatabaseThreadLocal.INSTANCE.get();
	}
	
	public ODatabaseObject getObjectDatabase() {
		return OObjectDatabaseThreadLocal.INSTANCE.get();
	}

}