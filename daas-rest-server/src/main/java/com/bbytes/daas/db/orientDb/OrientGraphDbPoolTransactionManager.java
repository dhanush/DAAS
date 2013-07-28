/*
 * Copyright (C) 2013 The Zorba Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bbytes.daas.db.orientDb;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;

/**
 * 
 * Spring AbstractPlatformTransactionManager impl for orient db
 * 
 * @author Thanneer
 * 
 * @version
 */
public class OrientGraphDbPoolTransactionManager extends AbstractPlatformTransactionManager implements
		ResourceTransactionManager, DisposableBean {

	private static final Logger LOG = Logger.getLogger(OrientGraphDbPoolTransactionManager.class);

	private static final long serialVersionUID = -1837116040878560582L;

	@Autowired
	private OrientDbTemplate orientDbTemplate;

	@Override
	protected Object doGetTransaction() throws TransactionException {
		GraphOrientTransactionObject txObject = new GraphOrientTransactionObject();
		return txObject;
	}

	@Override
	protected void doBegin(Object transactionObject, TransactionDefinition definition) throws TransactionException {
		LOG.debug("Came into doBegin");
		GraphOrientTransactionObject txObject = (GraphOrientTransactionObject) transactionObject;
		OGraphDatabase db = (OGraphDatabase) orientDbTemplate.getGraphDatabase();

		try {
			txObject.setODatabaseRecordHolder(new ODatabaseHolder(db));

			// Sets TransactionActive = true in the Database Holder
			txObject.setTransactionData(null);

			// Bind the DatabaseHolder to the thread
			if (!TransactionSynchronizationManager.hasResource(orientDbTemplate))
				TransactionSynchronizationManager.bindResource(orientDbTemplate, txObject.getDatabaseHolder());

			txObject.getDatabaseHolder().setSynchronizedWithTransaction(true);

			txObject.getDatabaseHolder().getGraphDatabase().begin();
		} catch (Exception e) {
			closeDatabaseConnectionAfterFailedBegin(txObject);
			throw new OrientDbTransactionException("Could open a Transaction with Graph: " + txObject, e);
		}

	}

	protected void closeDatabaseConnectionAfterFailedBegin(OrientTransactionObject txObject) {
		ODatabaseRecord db = txObject.getDatabaseHolder().getGraphDatabase();
		try {
			if (db.getTransaction().isActive()) {
				db.rollback();
			}
		} catch (Throwable ex) {
			logger.debug("Could not rollback OTransaction after failed transaction begin", ex);
		} finally {
			releaseDatabase(txObject.getDatabaseHolder());
		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		LOG.debug("Came into doCommit");

		GraphOrientTransactionObject txObject = (GraphOrientTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Committing transaction on DB [" + txObject.getDatabaseHolder().getGraphDatabase() + "]");
		}
		try {
			ODatabaseRecord db = txObject.getDatabaseHolder().getGraphDatabase();
			db.commit();
		} catch (OTransactionException ex) {
			throw new TransactionSystemException("Could not commit OrientDB transaction", ex);
		} catch (RuntimeException ex) {
			throw ex;
		}

	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		LOG.debug("Came into doRollback");
		GraphOrientTransactionObject txObject = (GraphOrientTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Rolling back OrientDB transaction on DB [" + txObject.getDatabaseHolder().getGraphDatabase()
					+ "]");
		}
		try {
			ODatabaseRecord db = txObject.getDatabaseHolder().getGraphDatabase();
			db.rollback();
		} catch (OTransactionException ex) {
			throw new TransactionSystemException("Could not commit OrientDB transaction", ex);
		} catch (RuntimeException ex) {
			throw ex;
		}
	}



	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		OrientTransactionObject txObject = (OrientTransactionObject) transaction;

		// Remove the DatabaseHolder from the thread.
		if (TransactionSynchronizationManager.hasResource(orientDbTemplate))
			TransactionSynchronizationManager.unbindResource(orientDbTemplate);
		
		txObject.getDatabaseHolder().clear();

		releaseDatabase(txObject.getDatabaseHolder());
	}

	
	protected void releaseDatabase(ODatabaseHolder holder) {
		LOG.debug("Came into release db");
		holder.getGraphDatabase().close();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.transaction.support.ResourceTransactionManager#getResourceFactory()
	 */
	@Override
	public Object getResourceFactory() {
		return orientDbTemplate;
	}

}
