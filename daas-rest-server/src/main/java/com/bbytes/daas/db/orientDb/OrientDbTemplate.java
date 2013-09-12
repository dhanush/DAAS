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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

@Component
public class OrientDbTemplate {

	static ThreadLocal<ODatabaseObject> TENANT_MANAGE_DB_INSTANCE = new ThreadLocal<ODatabaseObject>();

	static ThreadLocal<OGraphDatabase> THREAD_LOCAL_DB_INSTANCE = new ThreadLocal<OGraphDatabase>();

	@Autowired
	private OrientDbConnectionManager connectionManager;

	public OrientDbTemplate() {
	}

	public OGraphDatabase getDatabase() {
		OGraphDatabase db = getThreadLocalGraphDB();
		// The database is valid and is open if its not null, so just return it
		if (db != null  && !db.isClosed())
			return db;

		db = connectionManager.getDatabase();

		THREAD_LOCAL_DB_INSTANCE.set(db);

		return db;
	}

	public ODatabaseRecord getDocumentDatabase() {
		return getDatabase();
	}

	public ODatabaseObject getTenantManagementDatabase() {

		ODatabaseObject db = getThreadLocalTenantManagementDB();
		// The database is valid and is open if its not null, so just return it
		if (db != null && !db.isClosed())
			return db;

		db = connectionManager.getTenantManagementDatabase();

		TENANT_MANAGE_DB_INSTANCE.set(db);

		return db;

	}

	protected OGraphDatabase getThreadLocalGraphDB() {
		OGraphDatabase db = THREAD_LOCAL_DB_INSTANCE.get();
		if (db != null && !db.isClosed()) {
			return db;
		}

		return null;
	}

	protected ODatabaseObject getThreadLocalTenantManagementDB() {
		ODatabaseObject db = TENANT_MANAGE_DB_INSTANCE.get();
		if (db != null && !db.isClosed()) {
			return db;
		}

		return null;
	}

}