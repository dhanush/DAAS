package com.bbytes.daas.db.orientDb;

/**
 * Thread local for OObjectDatabase , inspired by
 * <code>com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal</code>
 * 
 * @author Thanneer
 * 
 * @version
 */

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.exception.ODatabaseException;

/**
 * 
 * ThreadLocal for OGraphDatabase
 *
 * @author Thanneer
 *
 * @version 1.0.0
 */
public class OGraphDatabaseThreadLocal extends ThreadLocal<OGraphDatabase> {

	public static OGraphDatabaseThreadLocal INSTANCE = new OGraphDatabaseThreadLocal();

	@Override
	public OGraphDatabase get() {
		OGraphDatabase db = super.get();
		if (db == null) {
			throw new ODatabaseException(
					"Database instance is not set in current thread. Possible reasons : 1. @Transactional missing at class level or method level  2.Assure to set it with: GraphDatabaseThreadLocal.INSTANCE.set(db);");
		} else {
			set(db);
		}
		return db;
	}

	@Override
	public void remove() {
		super.remove();
	}

	public OGraphDatabase getIfDefined() {
		return super.get();
	}

	public boolean isDefined() {
		return super.get() != null;
	}

}
