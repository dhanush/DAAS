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

public class GraphDBTransactionHolder extends ODatabaseHolder {

	public GraphDBTransactionHolder(ODatabaseRecord database) {
		super(database);
	}
}