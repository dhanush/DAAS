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
		return  super.get();
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
