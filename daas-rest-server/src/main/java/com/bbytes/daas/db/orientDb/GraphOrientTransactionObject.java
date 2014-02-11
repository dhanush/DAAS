package com.bbytes.daas.db.orientDb;

/**
 * Orient DB Transaction Object
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public class GraphOrientTransactionObject extends OrientTransactionObject {

	public void flush() {
		this.databaseHolder.getGraphDatabase().commit();
	}

}
