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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;

/**
 * Creates new connections to Orient database. It use global connection pull for establishing
 * connection.
 * Tenant aware conn manager
 * 
 * @author Thanneer
 * 
 * @version
 */
public class OrientDbConnectionManager implements InitializingBean ,DisposableBean{

	private static final Logger logger = Logger.getLogger(OrientDbConnectionManager.class);

	private Map<String, OObjectDatabasePool> tenantToObjectDbConnPoolMap = new HashMap<String, OObjectDatabasePool>();

	private Map<String, OGraphDatabasePool> tenantToGraphDbConnPoolMap = new HashMap<String, OGraphDatabasePool>();

	private OObjectDatabasePool defaultDbPool;

	private String databaseURL;
	private String defaultDatabaseName;
	private String username;
	private String password;
	private int minConnections = 1;
	private int maxConnections = 50;
	private String domainClassPackage;

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	/**
	 * @param defaultDatabaseName
	 *            the defaultDatabaseName to set
	 */
	public void setDefaultDatabaseName(String defaultDatabaseName) {
		this.defaultDatabaseName = defaultDatabaseName;
	}

	/**
	 * @return the defaultDatabaseName
	 */
	public String getDefaultDatabaseName() {
		return defaultDatabaseName;
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

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.hasText(databaseURL)) {
			throw new IllegalArgumentException("'databaseURL' is required");
		}
		if (minConnections > maxConnections) {
			throw new IllegalArgumentException("minConnections > maxConnections");
		}

		defaultDbPool = new OObjectDatabasePool(databaseURL + "/" + defaultDatabaseName, username, password);
		defaultDbPool.setup(minConnections, maxConnections);

		initTenantManagementDB();
	}

	/**
	 * Check if Database is there if not create one in orientdb for tenant management
	 * 
	 * @throws IOException
	 * 
	 */
	private void initTenantManagementDB() throws IOException {
		OServerAdmin serverAdmin = new OServerAdmin(databaseURL).connect(username, password);
		if (!serverAdmin.listDatabases().keySet().contains(defaultDatabaseName)) {
			serverAdmin.createDatabase(defaultDatabaseName, "document", "local");
		}

		ODatabaseObject database = defaultDbPool.acquire();
		// Before to use persistent POJOs OrientDB needs to know which classes are persistent
		// (between thousands in your classpath) by registering the persistent packages and/or
		// classes.
		database.getEntityManager().registerEntityClasses(this.domainClassPackage);
		database.close();
	}

	public ODatabaseObject getTenantManagementDatabase() {
		return defaultDbPool.acquire();
	}

	/**
	 * Acquire new database connection from database pool.
	 * 
	 * @return new database connection
	 */
	public OGraphDatabase getGraphDatabase() {
		String tenantDbName = TenantRouter.getTenantIdentifier();

		if (tenantDbName == null)
			throw new IllegalArgumentException(
					"Tenant name or identifier is null, please set the tenant name on TenantRouter");

		OGraphDatabasePool tenantGraphDatabasePool = tenantToGraphDbConnPoolMap.get(tenantDbName);
		if (tenantGraphDatabasePool == null) {
			try {
				logger.debug("Creating Graph database for tenant - " + tenantDbName);
				tenantGraphDatabasePool = new OGraphDatabasePool(databaseURL + "/" + tenantDbName, username, password);
				tenantGraphDatabasePool.setup(minConnections, maxConnections);
				tenantToGraphDbConnPoolMap.put(tenantDbName, tenantGraphDatabasePool);
				return tenantGraphDatabasePool.acquire();
			} catch (OConfigurationException e) {
				try {
					OServerAdmin serverAdmin = new OServerAdmin(databaseURL).connect(username, password);

					if (!serverAdmin.listDatabases().keySet().contains(tenantDbName)) {
						serverAdmin.createDatabase(tenantDbName, "graph", "local");
					}
				} catch (IOException ex) {
					logger.error(ex);
				}
			}
		}

		return tenantGraphDatabasePool.acquire();
	}

	/**
	 * Acquire new database connection from database pool.
	 * 
	 * @return new database connection
	 */
	public ODatabaseObject getObjectDatabase() {
		String tenantDbName = TenantRouter.getTenantIdentifier();

		if (tenantDbName == null)
			throw new IllegalArgumentException(
					"Tenant name or identifier is null, please set the tenant name on TenantRouter");

		OObjectDatabasePool tenantObjectDatabasePool = tenantToObjectDbConnPoolMap.get(tenantDbName);

		// if not connected then connect once to create pool
		if (tenantObjectDatabasePool == null) {
			try {
				logger.debug("Creating Object database for tenant - " + tenantDbName);
				tenantObjectDatabasePool = new OObjectDatabasePool(databaseURL + "/" + tenantDbName, username, password);
				tenantObjectDatabasePool.setup(1, 5);
				
				ODatabaseObject database = tenantObjectDatabasePool.acquire();
				// Before to use persistent POJOs OrientDB needs to know which classes are persistent
				// (between thousands in your classpath) by registering the persistent packages and/or
				// classes.
				database.getEntityManager().registerEntityClasses(this.domainClassPackage);
				tenantToObjectDbConnPoolMap.put(tenantDbName, tenantObjectDatabasePool);
				return database;
			} catch (OConfigurationException e) {
				// if db not available then create
				try {
					OServerAdmin serverAdmin = new OServerAdmin(databaseURL).connect(username, password);

					if (!serverAdmin.listDatabases().keySet().contains(tenantDbName)) {
						serverAdmin.createDatabase(tenantDbName, "document", "local");
					}
				} catch (IOException ex) {
					logger.error(ex);
				}
			}
		}
		return tenantObjectDatabasePool.acquire();

	}

	@Override
	public void destroy() throws Exception {
		this.defaultDbPool.close();
		for (OObjectDatabasePool objectDatabasePool : tenantToObjectDbConnPoolMap.values()) {
			objectDatabasePool.close();
		}

		for (OGraphDatabasePool graphDatabasePool : tenantToGraphDbConnPoolMap.values()) {
			graphDatabasePool.close();
		}

		tenantToObjectDbConnPoolMap.clear();
		tenantToGraphDbConnPoolMap.clear();

	}

}
