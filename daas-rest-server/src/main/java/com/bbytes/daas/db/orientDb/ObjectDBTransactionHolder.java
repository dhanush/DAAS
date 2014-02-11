package com.bbytes.daas.db.orientDb;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;

/**
 * 
 * Transaction holder, wrapping an ODatabaseObject database and a OTransaction.
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */

public class ObjectDBTransactionHolder extends ODatabaseHolder {

	public ObjectDBTransactionHolder(ODatabaseObject database) {
		super(database);
	}
}