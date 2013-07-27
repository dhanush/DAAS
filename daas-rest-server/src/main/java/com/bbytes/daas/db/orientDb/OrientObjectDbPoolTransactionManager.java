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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;

/**
 * 
 * Spring AbstractPlatformTransactionManager impl for orient db
 * 
 * @author Thanneer
 * 
 * @version
 */
public class OrientObjectDbPoolTransactionManager extends AbstractPlatformTransactionManager implements
		InitializingBean, DisposableBean {

	private static final Logger LOG = Logger.getLogger(OrientObjectDbPoolTransactionManager.class);

	private static final long serialVersionUID = -1837116040878560582L;

	private OObjectDatabasePool pool;
	private String databaseURL;
	private String databaseName;
	private String username;
	private String password;
	private int minConnections = 1;
	private int maxConnections = 50;

	private String domainClassPackage;

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	/**
	 * @param databaseName
	 *            the databaseName to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	/**
	 * @param domainClassPackage
	 *            the domainClassPackage to set
	 */
	public void setDomainClassPackage(String domainClassPackage) {
		this.domainClassPackage = domainClassPackage;
	}

	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.hasText(databaseURL)) {
			throw new IllegalArgumentException("'databaseURL' is required");
		}
		if (minConnections > maxConnections) {
			throw new IllegalArgumentException("minConnections > maxConnections");
		}

		pool = new OObjectDatabasePool(databaseURL + "/" + databaseName, username, password);
		pool.setup(minConnections, maxConnections);

		init();
	}

	/**
	 * Check if Database is there if not create one in orientdb
	 * 
	 * @throws IOException
	 * 
	 */
	private void init() throws IOException {
		OServerAdmin serverAdmin = new OServerAdmin(databaseURL).connect(username, password);
		if (!serverAdmin.listDatabases().keySet().contains(databaseName)) {
			serverAdmin.createDatabase(databaseName, "document", "local");
		}

		ODatabaseObject database = pool.acquire();
		// Before to use persistent POJOs OrientDB needs to know which classes are persistent
		// (between thousands in your classpath) by registering the persistent packages and/or
		// classes.
		database.getEntityManager().registerEntityClasses(this.domainClassPackage);
		database.close();
	}

	public void destroy() throws Exception {
		this.pool.close();
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		LOG.debug("Came into doGetTransaction");
		ObjectDBTransactionHolder holder = (ObjectDBTransactionHolder) TransactionSynchronizationManager
				.getResource(ODatabase.class);
		if (holder == null) {
			// create and register new transaction
			ODatabaseObject database = pool.acquire();
			OObjectDatabaseThreadLocal.INSTANCE.set(database);
			holder = new ObjectDBTransactionHolder(database);
			TransactionSynchronizationManager.bindResource(ODatabase.class, holder);
		}

		return holder;
	}

	@Override
	protected void doBegin(Object transactionObject, TransactionDefinition definition) throws TransactionException {
		LOG.debug("Came into doBegin");
		ObjectDBTransactionHolder holder = (ObjectDBTransactionHolder) transactionObject;
		holder.getDatabase().getTransaction().begin();
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		LOG.debug("Came into doCommit");
		ObjectDBTransactionHolder holder = (ObjectDBTransactionHolder) status.getTransaction();
		try {
			holder.getDatabase().getTransaction().commit();
			releaseConnecton(holder);
		} catch (Exception ex) {
			throw new TransactionSystemException("Could not rollback OrientDB transaction", ex);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		LOG.debug("Came into doRollback");
		ObjectDBTransactionHolder holder = (ObjectDBTransactionHolder) status.getTransaction();
		try {
			holder.getDatabase().getTransaction().rollback();
			releaseConnecton(holder);
		} catch (Exception ex) {
			throw new TransactionSystemException("Could not rollback OrientDB transaction", ex);
		}
	}

	protected void releaseConnecton(ObjectDBTransactionHolder holder) {
		LOG.debug("Came into unBind");
		holder.getDatabase().close();
		try {
			TransactionSynchronizationManager.unbindResource(ODatabase.class);
		} catch (IllegalStateException e) {
			LOG.info("Previous Transaction unbound the resource ODatabase from current thread local");
		}
	}

}
