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
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;

/**
 * 
 * Spring AbstractPlatformTransactionManager impl for orient db
 * 
 * @author Thanneer
 * 
 * @version
 */
public class OrientGraphDbPoolTransactionManager extends AbstractPlatformTransactionManager implements
		InitializingBean, DisposableBean {

	private static final Logger LOG = Logger.getLogger(OrientGraphDbPoolTransactionManager.class);

	private static final long serialVersionUID = -1837116040878560582L;

	private OGraphDatabasePool pool;
	private String databaseURL;
	private String databaseName;
	private String username;
	private String password;
	private int minConnections = 1;
	private int maxConnections = 20;

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

	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.hasText(databaseURL)) {
			throw new IllegalArgumentException("'databaseURL' is required");
		}
		if (minConnections > maxConnections) {
			throw new IllegalArgumentException("minConnections > maxConnections");
		}

		pool = new OGraphDatabasePool(databaseURL + "/" + databaseName, username, password);
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
			serverAdmin.createDatabase(databaseName, "graph", "local");
		}

	}

	public void destroy() throws Exception {
		this.pool.close();
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		GraphDBTransactionHolder holder = (GraphDBTransactionHolder) TransactionSynchronizationManager
				.getResource(ODatabaseDocument.class);
		if (holder == null) {
			// create and register new transaction
			OGraphDatabase database = pool.acquire();
			OGraphDatabaseThreadLocal.INSTANCE.set(database);
			holder = new GraphDBTransactionHolder(database);
			TransactionSynchronizationManager.bindResource(ODatabaseDocument.class, holder);
		}

		return holder;
	}

	@Override
	protected void doBegin(Object transactionObject, TransactionDefinition definition) throws TransactionException {
		GraphDBTransactionHolder holder = (GraphDBTransactionHolder) transactionObject;
		holder.getDatabase().getTransaction().begin();
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		GraphDBTransactionHolder holder = (GraphDBTransactionHolder) status.getTransaction();
		try {
			holder.getDatabase().getTransaction().commit();
			releaseConnecton(holder);
		} catch (Exception ex) {
			throw new TransactionSystemException("Could not rollback OrientDB transaction", ex);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		GraphDBTransactionHolder holder = (GraphDBTransactionHolder) status.getTransaction();
		try {
			holder.getDatabase().getTransaction().rollback();
			releaseConnecton(holder);
		} catch (Exception ex) {
			throw new TransactionSystemException("Could not rollback OrientDB transaction", ex);
		}
	}

	protected void releaseConnecton(GraphDBTransactionHolder holder) {
		holder.getDatabase().close();
		try {
			TransactionSynchronizationManager.unbindResource(ODatabaseDocument.class);
		} catch (IllegalStateException e) {
			LOG.info("Previous Transaction unbound the resource ODatabaseDocument from current thread local");
		}

	}

}
