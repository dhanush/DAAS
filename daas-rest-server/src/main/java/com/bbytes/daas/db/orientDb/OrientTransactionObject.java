
package com.bbytes.daas.db.orientDb;

import org.springframework.transaction.support.SmartTransactionObject;


/**
 * Orient Transaction Object that holds the db and does roolback or commits
 *
 * @author Thanneer
 *
 * @version 1.0.0
 */
public abstract class  OrientTransactionObject implements SmartTransactionObject {

	protected ODatabaseHolder databaseHolder;

	protected Object transactionData;
	
	protected boolean rollbackOnly;
	
	public OrientTransactionObject() {
		this.rollbackOnly = false;
	}

	public void setODatabaseRecordHolder(ODatabaseHolder databaseHolder) {
		this.databaseHolder = databaseHolder;
	}

	public ODatabaseHolder getDatabaseHolder() {
		return this.databaseHolder;
	}


	public boolean hasTransaction() {
		return (this.databaseHolder != null && this.databaseHolder.isTransactionActive());
	}

	// TODO: For now transaction Data is always null, if its not needed this
	// method should be changed
	public void setTransactionData(Object transactionData) {
		this.transactionData = transactionData;
		this.databaseHolder.setTransactionActive(true);
	}

	public Object getTransactionData() {
		return this.transactionData;
	}

	public void setRollbackOnly() {
		this.rollbackOnly = true;
		this.databaseHolder.setRollbackOnly();
	}

	public boolean isRollbackOnly() {
		return this.rollbackOnly;
	}

	

}
