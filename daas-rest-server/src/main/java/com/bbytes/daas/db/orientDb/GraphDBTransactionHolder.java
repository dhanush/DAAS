
package com.bbytes.daas.db.orientDb;

/**
 * 
 * Transaction holder, wrapping an ODatabaseRecord database and a OTransaction.
 * 
 * @author Thanneer
 * 
 * @version
 */

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class GraphDBTransactionHolder {

	private ODatabaseRecord database;

	public GraphDBTransactionHolder() {
		super();
	}

	public GraphDBTransactionHolder(ODatabaseRecord database) {
		super();
		this.database = database;
	}

	public ODatabaseRecord getDatabase() {
		return database;
	}

	public void setDatabase(ODatabaseRecord database) {
		this.database = database;
	}
}