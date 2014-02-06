package com.bbytes.daas.db.orientDb;

/**
 * 
 * Transaction holder, wrapping an ODatabaseRecord database and a OTransaction.
 * 
 * @author Thanneer
 * 
 * @version
 */

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class GraphDBTransactionHolder extends ODatabaseHolder {

	public GraphDBTransactionHolder(OrientGraph database) {
		super(database);
	}
}