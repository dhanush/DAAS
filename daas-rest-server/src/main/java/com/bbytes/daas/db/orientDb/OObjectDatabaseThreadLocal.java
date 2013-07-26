package com.bbytes.daas.db.orientDb;

/**
 * Thread local for OObjectDatabase , inspired by
 * <code>com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal</code>
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.exception.ODatabaseException;

public class OObjectDatabaseThreadLocal extends ThreadLocal<ODatabaseObject> {

	public static OObjectDatabaseThreadLocal INSTANCE = new OObjectDatabaseThreadLocal();

	@Override
	public ODatabaseObject get() {
		ODatabaseObject db = super.get();
		if (db == null) {
			throw new ODatabaseException(
					"Database instance is not set in current thread. 1. @Transactional(\"objectDB\") missing at class level or method level  2. Assure to set it with: OObjectDatabaseThreadLocal.INSTANCE.set(db);");
		} else {
			set(db);
		}
		return db;
	}

	@Override
	public void remove() {
		super.remove();
	}

	public ODatabaseObject getIfDefined() {
		return super.get();
	}

	public boolean isDefined() {
		return super.get() != null;
	}

}
