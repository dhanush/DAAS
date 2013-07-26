
package com.bbytes.daas.db.orientDb;

/**
 * 
 * Transaction holder, wrapping an ODatabaseObject database and a OTransaction.
 * 
 * @author Thanneer
 * 
 * @version
 */

import com.orientechnologies.orient.core.db.object.ODatabaseObject;

public class ObjectDBTransactionHolder {

	private ODatabaseObject database;

	public ObjectDBTransactionHolder() {
		super();
	}

	public ObjectDBTransactionHolder(ODatabaseObject database) {
		super();
		this.database = database;
	}

	public ODatabaseObject getDatabase() {
		return database;
	}

	public void setDatabase(ODatabaseObject database) {
		this.database = database;
	}
}