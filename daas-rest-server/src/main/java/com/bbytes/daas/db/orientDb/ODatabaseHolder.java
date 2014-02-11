package com.bbytes.daas.db.orientDb;

/**
 * 
 * Transaction holder, wrapping an ODatabaseRecord database and a OTransaction.
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */

import org.springframework.transaction.support.ResourceHolderSupport;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class ODatabaseHolder extends ResourceHolderSupport {

	private boolean transactionActive;

	private OrientGraph graphDatabase;
	
	private ODatabaseObject objectDatabase;


	public ODatabaseHolder() {
		super();
	}

	public ODatabaseHolder(OrientGraph database) {
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

	public OrientGraph getGraphDatabase() {
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