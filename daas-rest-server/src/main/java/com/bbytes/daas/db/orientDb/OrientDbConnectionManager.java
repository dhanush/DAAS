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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.bbytes.daas.dao.AccountDao;
import com.bbytes.daas.rest.DaasTenantCreationException;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.common.reflection.OReflectionHelper;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Creates new connections to Orient database. It use global connection pull for establishing
 * connection. Tenant aware conn manager
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public class OrientDbConnectionManager implements InitializingBean, DisposableBean {

	private static final Logger logger = Logger.getLogger(OrientDbConnectionManager.class);

	private Map<String, ODatabaseDocumentPool> tenantToGraphDbConnPoolMap = new HashMap<String, ODatabaseDocumentPool>();

	private Map<String, OObjectDatabasePool> tenantToObjectDbConnPoolMap = new HashMap<String, OObjectDatabasePool>();

	private OObjectDatabasePool defaultTenantManageDbPool;

	@Autowired
	private AccountDao accountDao;

	private String databaseURL;
	private String tenantManagementDBName;
	private String username;
	private String password;
	private int minConnections = 1;
	private int maxConnections = 20;
	private String domainClassBasePackage;

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	/**
	 * @param defaultDatabaseName
	 *            the defaultDatabaseName to set
	 */
	public void setTenantManagementDBName(String tenantManagementDBName) {
		this.tenantManagementDBName = tenantManagementDBName;
	}

	/**
	 * @return the defaultDatabaseName
	 */
	public String getDefaultDatabaseName() {
		return tenantManagementDBName;
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
	public void setDomainClassBasePackage(String domainClassBasePackage) {
		this.domainClassBasePackage = domainClassBasePackage;
	}

	@Override
	/**
	 * Init all the DB pools both graph and object dbs
	 */
	public void afterPropertiesSet() {
		if (!StringUtils.hasText(databaseURL)) {
			throw new IllegalArgumentException("'databaseURL' is required");
		}
		if (minConnections > maxConnections) {
			throw new IllegalArgumentException("minConnections > maxConnections");
		}

		defaultTenantManageDbPool = new OObjectDatabasePool(databaseURL + "/" + tenantManagementDBName, username,
				password);
		defaultTenantManageDbPool.setup(minConnections, maxConnections);

		initTenantManagementDB();

		OGlobalConfiguration.TX_USE_LOG.setValue(false);
		OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
		OGlobalConfiguration.CLIENT_CHANNEL_MAX_POOL.setValue(100);
		OGlobalConfiguration.MVRBTREE_TIMEOUT.setValue(30000);
		OGlobalConfiguration.STORAGE_RECORD_LOCK_TIMEOUT.setValue(30000);
		OGlobalConfiguration.STORAGE_LOCK_TIMEOUT.setValue(30000);
		OGlobalConfiguration.CLIENT_CONNECT_POOL_WAIT_TIMEOUT.setValue(40000);
	}

	/**
	 * Check if Database is there if not create one in orientdb for tenant management
	 */
	private void initTenantManagementDB() {
		try {
			OServerAdmin serverAdmin = new OServerAdmin(databaseURL).connect(username, password);
			if (!serverAdmin.listDatabases().keySet().contains(tenantManagementDBName)) {
				serverAdmin.createDatabase(tenantManagementDBName, "document", "plocal");
			}

			ODatabaseObject database = defaultTenantManageDbPool.acquire();
			// Before to use persistent POJOs OrientDB needs to know which classes are persistent
			// (between thousands in your classpath) by registering the persistent packages and/or
			// classes.
			database.getEntityManager().registerEntityClasses(this.domainClassBasePackage);
			database.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public ODatabaseObject getTenantManagementDatabase() {
		return defaultTenantManageDbPool.acquire();
	}

	public ODatabaseObject getObjectDatabase() {

		ODatabaseObject objectDatabase = null;
		String tenantDbName = TenantRouter.getTenantIdentifier();

		if (tenantDbName == null)
			throw new IllegalArgumentException(
					"Account information missing in HTTP parameter or URL for tenant identification");

		OObjectDatabasePool tenantObjectDatabasePool = tenantToObjectDbConnPoolMap.get(tenantDbName);

		// if accn does not exist then throw cannot create new tenant db
		if (tenantObjectDatabasePool == null) {
			if (!accountDao.findAny("name", tenantDbName)) {
				throw new DaasTenantCreationException("Failed to create tenant DB " + tenantDbName
						+ " as there is no account created with name " + tenantDbName);
			}

			tenantObjectDatabasePool = (OObjectDatabasePool) createDatabase(tenantDbName, "object");

		}
		objectDatabase = tenantObjectDatabasePool.acquire();

		return objectDatabase;
	}

	/**
	 * Acquire new database connection from database pool.
	 * 
	 * @return new database connection
	 */
	public OrientGraph getDatabase() {
		OrientGraph graphDatabase = null;
		String tenantDbName = TenantRouter.getTenantIdentifier();

		if (tenantDbName == null)
			throw new IllegalArgumentException(
					"Account information missing in HTTP parameter or URL for tenant identification");

		ODatabaseDocumentPool tenantGraphDatabasePool = tenantToGraphDbConnPoolMap.get(tenantDbName);
		// if accn does not exist then throw cannot create new tenant db

		if (tenantGraphDatabasePool == null) {
			if (!accountDao.findAny("name", tenantDbName)) {
				throw new DaasTenantCreationException("Failed to create tenant DB " + tenantDbName
						+ " as there is no account created with name " + tenantDbName);
			}

			tenantGraphDatabasePool = (ODatabaseDocumentPool) createDatabase(tenantDbName, "graph");

		}
		graphDatabase = new OrientGraph(tenantGraphDatabasePool.acquire());

		return graphDatabase;
	}

	/**
	 * Method that does the DB creation if not available . Does both types : graph or object db
	 * 
	 * @param databaseName
	 * @param dbType
	 * @return db pool
	 */
	public ODatabasePoolBase<?> createDatabase(String databaseName, String dbType) {
		OServerAdmin serverAdmin;
		try {
			serverAdmin = new OServerAdmin(databaseURL).connect(username, password);
			if (!serverAdmin.listDatabases().keySet().contains(databaseName)) {
				serverAdmin.createDatabase(databaseName, "graph", "plocal");
			}
		} catch (IOException e) {
			logger.error(e);
		}

		if (dbType.equals("graph")) {
			ODatabaseDocumentPool tenantGraphDatabasePool = new ODatabaseDocumentPool(databaseURL + "/" + databaseName,
					username, password);
			tenantGraphDatabasePool.setup(minConnections, maxConnections);
			tenantToGraphDbConnPoolMap.put(databaseName, tenantGraphDatabasePool);
			ODatabaseDocumentTx graphDatabase = tenantGraphDatabasePool.acquire();
			registerClassUnderPackageToDb(graphDatabase, domainClassBasePackage);
			return tenantGraphDatabasePool;
		} else {
			OObjectDatabasePool tenantObjectDatabasePool = new OObjectDatabasePool(databaseURL + "/" + databaseName,
					username, password);
			tenantObjectDatabasePool.setup(minConnections, maxConnections);
			tenantToObjectDbConnPoolMap.put(databaseName, tenantObjectDatabasePool);
			ODatabaseObject objectDatabase = tenantObjectDatabasePool.acquire();
			registerClassUnderPackageToDb(objectDatabase, domainClassBasePackage);
			objectDatabase.getEntityManager().registerEntityClasses(domainClassBasePackage);
			return tenantObjectDatabasePool;
		}

	}

	/**
	 * Register the class as tables in db when the db is created. POJO to tables conversion
	 * 
	 * @param graphDatabase
	 * @param classPackage
	 */
	private void registerClassUnderPackageToDb(ODatabaseRecord graphDatabase, final String classPackage) {
		List<Class<?>> classes = null;
		try {
			classes = OReflectionHelper.getClassesFor(classPackage, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new OException(e);
		}

		for (Class<?> c : classes) {
			if (!graphDatabase.getMetadata().getSchema().existsClass(c.getSimpleName())) {
				graphDatabase.getMetadata().getSchema().createClass(c);
			}
		}

		graphDatabase.close();
	}

	/**
	 * Register the class as tables in db when the db is created. POJO to tables conversion for
	 * object db
	 * 
	 * @param graphDatabase
	 * @param classPackage
	 */
	private void registerClassUnderPackageToDb(ODatabaseObject objectDatabase, final String classPackage) {
		List<Class<?>> classes = null;
		try {
			classes = OReflectionHelper.getClassesFor(classPackage, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new OException(e);
		}

		for (Class<?> c : classes) {
			if (!objectDatabase.getMetadata().getSchema().existsClass(c.getSimpleName())) {
				objectDatabase.getMetadata().getSchema().createClass(c);
			}
		}

		objectDatabase.close();
	}

	/**
	 * Method to drop the DB
	 * 
	 * @param databaseName
	 * @return
	 */
	public boolean dropDatabase(String databaseName) {
		if (databaseName == null || databaseName.equals(tenantManagementDBName))
			return false;
		OServerAdmin serverAdmin = null;
		try {
			String dbURL = databaseURL + "/" + databaseName;
			serverAdmin = new OServerAdmin(dbURL).connect(username, password);
			if (serverAdmin.existsDatabase("graph")) {
				serverAdmin.dropDatabase(databaseName);
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (serverAdmin != null)
				serverAdmin.close();
		}

		return false;
	}

	/**
	 * Check if DB exists
	 * 
	 * @param databaseName
	 * @return
	 */
	public boolean databaseExist(String databaseName) {
		OServerAdmin serverAdmin = null;
		try {
			String dbURL = databaseURL + "/" + databaseName;
			serverAdmin = new OServerAdmin(dbURL).connect(username, password);
			return serverAdmin.existsDatabase("graph");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (serverAdmin != null)
				serverAdmin.close();
		}

		return false;
	}

	@Override
	public void destroy() throws Exception {
		this.defaultTenantManageDbPool.close();

		for (ODatabaseDocumentPool graphDatabasePool : tenantToGraphDbConnPoolMap.values()) {
			graphDatabasePool.close();
		}

		tenantToGraphDbConnPoolMap.clear();

	}

}
