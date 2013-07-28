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

public class OObjectDatabaseThreadLocal extends ThreadLocal<ODatabaseObject> {

	public static OObjectDatabaseThreadLocal INSTANCE = new OObjectDatabaseThreadLocal();

	@Override
	public ODatabaseObject get() {
		return super.get();
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
