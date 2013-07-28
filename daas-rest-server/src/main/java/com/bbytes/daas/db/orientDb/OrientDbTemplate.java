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

@Component
public class OrientDbTemplate {

	public static ThreadLocal<ODatabaseObject> TENANT_MANAGE_DB_INSTANCE = new ThreadLocal<ODatabaseObject>();

	@Autowired
	private OrientDbConnectionManager connectionManager;

	public OrientDbTemplate() {
	}

	public OGraphDatabase getGraphDatabase() {
		OGraphDatabase db = getThreadLocalGraphDB();
		// The database is valid and is open if its not null, so just return it
		if (db != null)
			return db;

		db = connectionManager.getGraphDatabase();

		OGraphDatabaseThreadLocal.INSTANCE.set(db);

		return db;
	}

	public ODatabaseObject getObjectDatabase() {

		ODatabaseObject db = getThreadLocalObjectDB();
		// The database is valid and is open if its not null, so just return it
		if (db != null)
			return db;

		db = connectionManager.getObjectDatabase();

		System.out.println("Created new conn :" + db.hashCode());
		OObjectDatabaseThreadLocal.INSTANCE.set(db);

		return db;

	}

	public ODatabaseObject getTenantManagementDatabase() {

		ODatabaseObject db = getThreadLocalTenantManagementDB();
		// The database is valid and is open if its not null, so just return it
		if (db != null)
			return db;

		db = connectionManager.getTenantManagementDatabase();

		TENANT_MANAGE_DB_INSTANCE.set(db);

		return db;

	}

	protected OGraphDatabase getThreadLocalGraphDB() {
		OGraphDatabase db = OGraphDatabaseThreadLocal.INSTANCE.get();
		if (db != null && !db.isClosed()) {
			return db;
		}

		return null;
	}

	protected ODatabaseObject getThreadLocalObjectDB() {
		ODatabaseObject db = OObjectDatabaseThreadLocal.INSTANCE.get();
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