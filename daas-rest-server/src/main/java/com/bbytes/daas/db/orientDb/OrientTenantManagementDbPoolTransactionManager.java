/*
 * Copyright (C) 2013 The Daas Open Source Project
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
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.exception.OTransactionException;

/**
 * Spring Transaction Manager impl for Orient Graph Db 
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public class OrientTenantManagementDbPoolTransactionManager extends OrientGraphDbPoolTransactionManager {

	private static final long serialVersionUID = -5497970042226024082L;

	private static final Logger LOG = Logger.getLogger(OrientTenantManagementDbPoolTransactionManager.class);

	@Override
	protected void doBegin(Object transactionObject, TransactionDefinition definition) throws TransactionException {
		LOG.debug("Came into TenantManagement doBegin");
		GraphOrientTransactionObject txObject = (GraphOrientTransactionObject) transactionObject;

		ODatabaseObject tenantMgtDb = orientDbTemplate.getTenantManagementDatabase();

		try {
			txObject.setODatabaseRecordHolder(new ODatabaseHolder(tenantMgtDb));

			// Sets TransactionActive = true in the Database Holder
			txObject.setTransactionData(null);

			// Bind the DatabaseHolder to the thread
			if (!TransactionSynchronizationManager.hasResource(orientDbTemplate))
				TransactionSynchronizationManager.bindResource(orientDbTemplate, txObject.getDatabaseHolder());

			txObject.getDatabaseHolder().setSynchronizedWithTransaction(true);

			txObject.getDatabaseHolder().getObjectDatabase().begin();
		} catch (Exception e) {
			closeDatabaseConnectionAfterFailedBegin(txObject);
			throw new OrientDbTransactionException("Could open a Transaction with TenantManagement Db: " + txObject, e);
		}

	}

	protected void closeDatabaseConnectionAfterFailedBegin(OrientTransactionObject txObject) {
		ODatabaseObject tenantMgtDb = txObject.getDatabaseHolder().getObjectDatabase();

		if (tenantMgtDb == null || tenantMgtDb.isClosed())
			return;

		try {
			if (tenantMgtDb.getTransaction().isActive()) {
				tenantMgtDb.rollback();
			}
		} catch (Throwable ex) {
			logger.debug("Could not rollback OTransaction after failed transaction begin", ex);
		} finally {
			releaseDatabase(txObject.getDatabaseHolder());
		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		LOG.debug("Came into TenantManagement doCommit");

		GraphOrientTransactionObject txObject = (GraphOrientTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Committing transaction on TenantManagement DB ["
					+ txObject.getDatabaseHolder().getObjectDatabase() + "]");
		}
		try {
			ODatabaseObject tenantMgtDb = txObject.getDatabaseHolder().getObjectDatabase();

			if (tenantMgtDb == null || tenantMgtDb.isClosed())
				return;

			tenantMgtDb.commit();
		} catch (OTransactionException ex) {
			throw new TransactionSystemException("Could not commit TenantManagement OrientDB transaction", ex);
		} catch (RuntimeException ex) {
			throw ex;
		}

	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		LOG.debug("Came into TenantManagement doRollback");
		GraphOrientTransactionObject txObject = (GraphOrientTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Rolling back TenantManagement OrientDB  transaction on DB ["
					+ txObject.getDatabaseHolder().getObjectDatabase() + "]");
		}
		try {
			ODatabaseObject tenantMgtDb = txObject.getDatabaseHolder().getObjectDatabase();

			if (tenantMgtDb == null || tenantMgtDb.isClosed())
				return;

			tenantMgtDb.rollback();
		} catch (OTransactionException ex) {
			throw new TransactionSystemException("Could not commit TenantManagement OrientDB transaction", ex);
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

		if (holder.getObjectDatabase() == null || holder.getObjectDatabase().isClosed())
			return;

		LOG.debug("Came into release TenantManagement db");
		holder.getObjectDatabase().close();
	}
}
