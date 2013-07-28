package com.bbytes.daas.db.orientDb;

/**
 * 
 * Transaction holder, wrapping an ODatabaseRecord database and a OTransaction.
 * 
 * @author Thanneer
 * 
 * @version
 */

import org.springframework.transaction.support.ResourceHolderSupport;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class ODatabaseHolder extends ResourceHolderSupport {

	private boolean transactionActive;

	private ODatabaseRecord graphDatabase;
	
	private ODatabaseObject objectDatabase;


	public ODatabaseHolder() {
		super();
	}

	public ODatabaseHolder(ODatabaseRecord database) {
		super();
		this.graphDatabase = database;
		this.transactionActive = false;
	}
	
	public ODatabaseHolder(ODatabaseObject database) {
		super();
		this.objectDatabase = database;
		this.transactionActive = false;
	}

	public void setTransactionActive(boolean isTransactionActive) {
		this.transactionActive = isTransactionActive;
	}

	public boolean isTransactionActive() {
		return this.transactionActive;
	}

	public ODatabaseRecord getGraphDatabase() {
		return graphDatabase;
	}
	
	public ODatabaseObject getObjectDatabase() {
		return objectDatabase;
	}

	@Override
	public void clear() {
		super.clear();
		this.transactionActive = false;
	}
}