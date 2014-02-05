package com.bbytes.daas.client;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.bbytes.daas.client.annotation.Relation;
import com.bbytes.daas.client.annotation.RelationAnnotationExclStrat;
import com.bbytes.daas.client.annotation.RelationAnnotationProcessor;
import com.bbytes.daas.domain.Entity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

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

/**
 * Daas client
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClient implements IDaasClient {

	protected String clientId;

	protected String clientSecret;

	protected AsyncHttpClient asyncHttpClient;

	protected String host;

	protected String port;

	protected String baseURL;

	protected Gson gson;

	protected OAuthToken token;

	protected String applicationName;

	protected String accountName;

	protected DaasManagementClient daasManagementClient;

	public DaasClient(String host, String port) {

		this.host = host;
		this.port = port;

		baseURL = "http://" + host + ":" + port + URLConstants.SERVER_CONTEXT;

		Builder builder = new AsyncHttpClientConfig.Builder();
		builder.setCompressionEnabled(true).setAllowPoolingConnection(true).setConnectionTimeoutInMs(30000).build();

		asyncHttpClient = new AsyncHttpClient(builder.build());

		gson = new GsonBuilder().registerTypeAdapter(Date.class, SerializerUtil.getSerializerForDate())
				.registerTypeAdapter(Date.class, SerializerUtil.getDeSerializerForDate())
				.setExclusionStrategies(new RelationAnnotationExclStrat()).create();

	}

	/**
	 * To verify if the Daas client login session is available and still valid . If the returned
	 * value is false then re-login is required.
	 * 
	 * @return If false then re-login
	 */
	public boolean isLoggedIn() {
		return DaasClientUtil.isLoggedIn(token);
	}

	protected boolean pingSuccess() {
		try {
			Future<Response> f = asyncHttpClient.prepareGet(baseURL + "/ping").execute();
			Response r = f.get();
			if (!HttpStatusUtil.getReponseStatus(r).endsWith(HttpStatusUtil.SUCCESS))
				return false;

		} catch (InterruptedException | ExecutionException | IOException e) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return pingSuccess();
	}

	/**
	 * Login to your tenant account
	 * 
	 * @param accountName
	 * @param applicationName
	 * @param clientId
	 * @param clientSecret
	 * @return
	 * @throws DaasClientException
	 */
	public boolean login(String accountName, String applicationName, String clientId, String clientSecret)
			throws DaasClientException {
		this.accountName = accountName;
		this.applicationName = applicationName;

		// first verify if port and host name is correct using the ping url
		if (!pingSuccess())
			throw new DaasClientException("Not able to reach daas server on" + baseURL);

		token = DaasClientUtil.loginHelper(accountName, clientId, clientSecret, baseURL, asyncHttpClient, gson);
		if (token == null) {
			throw new DaasClientException("Not able to login to daas server on" + baseURL);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#login(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public void login(String accountName, String applicationName, String clientId, String clientSecret,
			AsyncResultHandler<Boolean> asyncResultHandler) {
		try {
			boolean result = login(accountName, applicationName, clientId, clientSecret);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Load the lazy relation in a entity. The lazy objects will be queried and set on member
	 * variables.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T loadLazyRelation(T entity) throws DaasClientException {
		return loadEntityFullGraph(entity, true);
	}

	/**
	 * Load the relation entity in a main entity. The lazy relations are ignore , to load that call
	 * {@link DaasClient} loadLazyRelation(T entity) method.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	private <T extends Entity> T loadRelation(T entity) throws DaasClientException {
		return loadEntityFullGraph(entity, false);
	}

	private <T extends Entity> T loadEntityFullGraph(T entity, boolean loadLazy) throws DaasClientException {

		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		try {
			Map<String, Field> relationNameAndEntity = RelationAnnotationProcessor.getRelationAndEntity(entity);
			for (Iterator<String> iterator = relationNameAndEntity.keySet().iterator(); iterator.hasNext();) {
				boolean applyLoading = true;
				String relation = iterator.next();
				Field toBeRelatedEntityField = relationNameAndEntity.get(relation);

				// if lazy annotation is present and loadlazy is true or load lazy is false and lazy
				// annotation is true
				if ((RelationAnnotationProcessor.islazy(toBeRelatedEntityField) && !loadLazy)
						|| (!RelationAnnotationProcessor.islazy(toBeRelatedEntityField) && loadLazy)) {
					applyLoading = false;
				}

				if (applyLoading) {
					List<Entity> entityList = getRightSideRelatedEntitiesWithOutGraph((Entity) entity, relation,
							toBeRelatedEntityField.getType());
					if (entityList != null && entityList.size() > 0)
						toBeRelatedEntityField.set(entity, entityList.get(0));
				}

			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DaasClientException(e);
		}

		return entity;
	}

	/**
	 * Find entity given the property map , the property check is done with OR query. If any one
	 * property name and value match then it is added to result set.
	 * 
	 * @param entityTypeName
	 *            table name
	 * @param entityClassType
	 *            json to class type conversion
	 * @param propertyMap
	 *            contains property name and value
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getEntitiesByProperty(String entityTypeName, Class<T> entityClassType,
			Map<String, String> propertyMap) throws DaasClientException {
		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityTypeName;

			// create parameter map from propertyMap
			Map<String, Collection<String>> parameters = new HashMap<String, Collection<String>>();
			for (Iterator<String> iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				List<String> values = new ArrayList<String>();
				values.add(propertyMap.get(key));
				parameters.put(key, values);
			}

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json")
					.setQueryParameters(new FluentStringsMap(parameters)).execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			@SuppressWarnings("unchecked")
			T[] result = (T[]) gson.fromJson(r.getResponseBody(), Array.newInstance(entityClassType, 0).getClass());

			// load the relation member field and return.
			List<T> resultList = Arrays.asList(result);
			List<T> resultListWithRelationLoaded = new ArrayList<>();
			for (T t : resultList) {
				t = loadRelation(t);
				resultListWithRelationLoaded.add(t);
			}

			return resultListWithRelationLoaded;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntitiesByProperty(java.lang.String,
	 * java.lang.Class, java.util.Map, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntitiesByProperty(String entityTypeName, Class<T> entityClassType,
			Map<String, String> propertyMap, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;
		try {
			List<T> result = getEntitiesByProperty(entityTypeName, entityClassType, propertyMap);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Find entity given the property map , the property check is done with OR query. If any one
	 * property name and value match then it is added to result set.
	 * 
	 * @param entityClassType
	 * @param propertyMap
	 *            contains property name and value
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getEntitiesByProperty(Class<T> entityClassType, Map<String, String> propertyMap)
			throws DaasClientException {
		return getEntitiesByProperty(entityClassType.getSimpleName(), entityClassType, propertyMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntitiesByProperty(java.lang.Class, java.util.Map,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntitiesByProperty(Class<T> entityClassType, Map<String, String> propertyMap,
			AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;
		try {
			List<T> result = getEntitiesByProperty(entityClassType, propertyMap);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * It is the range query where it checks if greater than or equal to start range and smaller
	 * than or equal to end range. Any one range is required. Datatype will say what data type to be
	 * used while checking the range condition
	 * 
	 * @param entityTypeName
	 *            table name
	 * @param entityClassType
	 * @param propertyName
	 * @param propertyDataType
	 *            Possible values : date,datetime,long,float,integer,string and boolean
	 * @param startRange
	 *            the start range , the check is greater than or equals to would be applied
	 *            (optional - can be null)
	 * @param endRange
	 *            the end range ,the check is less than or equals to would be applied (optional -
	 *            can be null)
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getEntitiesByRange(String entityTypeName, Class<T> entityClassType,
			String propertyName, String propertyDataType, String startRange, String endRange)
			throws DaasClientException {
		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityTypeName + "/range";

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json")
					.addQueryParameter("propertyName", propertyName)
					.addQueryParameter("propertyDataType", propertyDataType)
					.addQueryParameter("startRange", startRange).addQueryParameter("endRange", endRange).execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			@SuppressWarnings("unchecked")
			T[] result = (T[]) gson.fromJson(r.getResponseBody(), Array.newInstance(entityClassType, 0).getClass());

			// load the relation member field and return.
			List<T> resultList = Arrays.asList(result);
			List<T> resultListWithRelationLoaded = new ArrayList<>();
			for (T t : resultList) {
				t = loadRelation(t);
				resultListWithRelationLoaded.add(t);
			}

			return resultListWithRelationLoaded;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntitiesByRange(java.lang.String, java.lang.Class,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntitiesByRange(String entityTypeName, Class<T> entityClassType,
			String propertyName, String propertyDataType, String startRange, String endRange,
			AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getEntitiesByRange(entityTypeName, entityClassType, propertyName, propertyDataType,
					startRange, endRange);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * It is the range query where it checks if greater than or equal to start range and smaller
	 * than or equal to end range. Any one range is required. Datatype will say what data type to be
	 * used while checking the range condition
	 * 
	 * @param entityClassType
	 * @param propertyName
	 * @param propertyDataType
	 *            Possible values : date,datetime,long,float,integer,string and boolean
	 * @param startRange
	 *            the start range , the check is greater than or equals to would be applied
	 *            (optional - can be null)
	 * @param endRange
	 *            the end range ,the check is less than or equals to would be applied (optional -
	 *            can be null)
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getEntitiesByRange(Class<T> entityClassType, String propertyName,
			String propertyDataType, String startRange, String endRange) throws DaasClientException {
		return getEntitiesByRange(entityClassType.getSimpleName(), entityClassType, propertyName, propertyDataType,
				startRange, endRange);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntitiesByRange(java.lang.Class, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntitiesByRange(Class<T> entityClassType, String propertyName,
			String propertyDataType, String startRange, String endRange, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getEntitiesByRange(entityClassType, propertyName, propertyDataType, startRange, endRange);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Get the entity given UUID
	 * 
	 * @param entityType
	 *            table name
	 * @param entityClassType
	 * @param UUID
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T getEntityById(String entityType, Class<T> entityClassType, String UUID)
			throws DaasClientException {
		if (entityClassType == null || UUID == null)
			throw new IllegalArgumentException("The entity class type or UUID cannot be null");

		try {

			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityType + "/" + UUID;

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			T t = gson.fromJson(r.getResponseBody(), entityClassType);

			return loadRelation(t);

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntityById(java.lang.String, java.lang.Class,
	 * java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntityById(String entityTypeName, Class<T> entityClassType, String UUID,
			AsyncResultHandler<T> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			T result = getEntityById(entityTypeName, entityClassType, UUID);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Get the entity given UUID
	 * 
	 * @param entityClassType
	 * @param UUID
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T getEntityById(Class<T> entityClassType, String UUID) throws DaasClientException {
		return getEntityById(entityClassType.getSimpleName(), entityClassType, UUID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntityById(java.lang.Class, java.lang.String,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntityById(Class<T> entityClassType, String UUID,
			AsyncResultHandler<T> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			T result = getEntityById(entityClassType, UUID);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Returns the size of the given entity type
	 * 
	 * @param entityType
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	public long getEntitySize(String entityType) throws DaasClientException {

		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityType + "/size";

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			JsonObject obj = (JsonObject) new JsonParser().parse(r.getResponseBody());
			JsonElement size = obj.get("size");

			if (size == null)
				return 0L;

			return size.getAsLong();

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntitySize(java.lang.String,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public void getEntitySize(String entityTypeName, AsyncResultHandler<Long> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			Long result = getEntitySize(entityTypeName);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Returns the size of the given entity type
	 * 
	 * @param entityClassType
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> long getEntitySize(Class<T> entityClassType) throws DaasClientException {
		return getEntitySize(entityClassType.getSimpleName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#getEntitySize(java.lang.Class,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getEntitySize(Class<T> entityClassType, AsyncResultHandler<Long> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			Long result = getEntitySize(entityClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name. This
	 * method does not load the member field, just fetches the main entities
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param entityType
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 * @throws DaasClientException
	 */
	private <T extends Entity> List<T> getRightSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			String entityType, Class<?> expectedClassType) throws DaasClientException {
		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entity.getClass().getSimpleName()
					+ "/" + entity.getUuid() + "/" + relation + "/" + entityType;

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			@SuppressWarnings("unchecked")
			T[] result = (T[]) gson.fromJson(r.getResponseBody(), Array.newInstance(expectedClassType, 0).getClass());

			return Arrays.asList(result);

		} catch (Exception e) {
			throw new DaasClientException(e);
		}

	}

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name. This
	 * method does not load the member field, just fetches the main entities
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 * @throws DaasClientException
	 */
	private <T extends Entity> List<T> getRightSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			Class<?> expectedClassType) throws DaasClientException {
		return getRightSideRelatedEntitiesWithOutGraph(entity, relation, expectedClassType.getSimpleName(),
				expectedClassType);
	}

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param entitytype
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 * @throws DaasClientException
	 */
	private <T extends Entity> List<T> getRightSideRelatedEntitiesWithGraph(Entity entity, String relation,
			String entityType, Class<?> expectedClassType) throws DaasClientException {
		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entity.getClass().getSimpleName()
					+ "/" + entity.getUuid() + "/" + relation + "/" + entityType;

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			@SuppressWarnings("unchecked")
			T[] result = (T[]) gson.fromJson(r.getResponseBody(), Array.newInstance(expectedClassType, 0).getClass());

			// load the relation member field and return. The graph loading logic
			List<T> resultList = Arrays.asList(result);
			List<T> resultListWithRelationLoaded = new ArrayList<>();
			for (T t : resultList) {
				t = loadRelation(t);
				resultListWithRelationLoaded.add(t);
			}

			return resultListWithRelationLoaded;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}

	}

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 * @throws DaasClientException
	 */
	private <T extends Entity> List<T> getRightSideRelatedEntitiesWithGraph(Entity entity, String relation,
			Class<?> expectedClassType) throws DaasClientException {
		return getRightSideRelatedEntitiesWithGraph(entity, relation, expectedClassType.getSimpleName(),
				expectedClassType);

	}

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param entitytype
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getRightSideRelatedEntities(Entity entity, String relation, String entityType,
			Class<?> expectedClassType) throws DaasClientException {
		return getRightSideRelatedEntitiesWithGraph(entity, relation, entityType, expectedClassType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getRightSideRelatedEntities(com.bbytes.daas.domain.Entity,
	 * java.lang.String, java.lang.String, java.lang.Class,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getRightSideRelatedEntities(Entity entity, String relation, String entityTypeName,
			Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getRightSideRelatedEntities(entity, relation, entityTypeName, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getRightSideRelatedEntities(Entity entity, String relation,
			Class<?> expectedClassType) throws DaasClientException {
		return getRightSideRelatedEntitiesWithGraph(entity, relation, expectedClassType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getRightSideRelatedEntities(com.bbytes.daas.domain.Entity,
	 * java.lang.String, java.lang.Class, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getRightSideRelatedEntities(Entity entity, String relation,
			Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getRightSideRelatedEntities(entity, relation, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.This
	 * method does not load the member field, just fetches the main entities
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entitytype
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			String entityType, Class<?> expectedClassType) throws DaasClientException {

		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entity.getClass().getSimpleName()
					+ "/" + entity.getUuid() + "/connecting/" + relation + "/" + entityType;

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			@SuppressWarnings("unchecked")
			T[] result = (T[]) gson.fromJson(r.getResponseBody(), Array.newInstance(expectedClassType, 0).getClass());

			return Arrays.asList(result);

		} catch (Exception e) {
			throw new DaasClientException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getLeftSideRelatedEntitiesWithOutGraph(com.bbytes.daas
	 * .domain.Entity, java.lang.String, java.lang.String, java.lang.Class,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			String entityTypeName, Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getLeftSideRelatedEntitiesWithOutGraph(entity, relation, entityTypeName, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.This
	 * method does not load the member field, just fetches the main entities
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			Class<?> expectedClassType) throws DaasClientException {
		return getLeftSideRelatedEntitiesWithOutGraph(entity, relation, expectedClassType.getSimpleName(),
				expectedClassType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getLeftSideRelatedEntitiesWithOutGraph(com.bbytes.daas
	 * .domain.Entity, java.lang.String, java.lang.Class, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getLeftSideRelatedEntitiesWithOutGraph(entity, relation, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entitytype
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			String entityType, Class<?> expectedClassType) throws DaasClientException {

		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entity.getClass().getSimpleName()
					+ "/" + entity.getUuid() + "/connecting/" + relation + "/" + entityType;

			Future<Response> f = buildRequest("get", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			@SuppressWarnings("unchecked")
			T[] result = (T[]) gson.fromJson(r.getResponseBody(), Array.newInstance(expectedClassType, 0).getClass());

			// load the relation member field and return. The graph loading logic
			List<T> resultList = Arrays.asList(result);
			List<T> resultListWithRelationLoaded = new ArrayList<>();
			for (T t : resultList) {
				t = loadRelation(t);
				resultListWithRelationLoaded.add(t);
			}

			return resultListWithRelationLoaded;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getLeftSideRelatedEntitiesWithGraph(com.bbytes.daas.domain
	 * .Entity, java.lang.String, java.lang.String, java.lang.Class,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			String entityTypeName, Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getLeftSideRelatedEntitiesWithGraph(entity, relation, entityTypeName, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			Class<?> expectedClassType) throws DaasClientException {
		return getLeftSideRelatedEntitiesWithGraph(entity, relation, expectedClassType.getSimpleName(),
				expectedClassType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getLeftSideRelatedEntitiesWithGraph(com.bbytes.daas.domain
	 * .Entity, java.lang.String, java.lang.Class, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getLeftSideRelatedEntitiesWithGraph(entity, relation, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntities(Entity entity, String relation,
			Class<?> expectedClassType) throws DaasClientException {
		return getLeftSideRelatedEntitiesWithGraph(entity, relation, expectedClassType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getLeftSideRelatedEntities(com.bbytes.daas.domain.Entity,
	 * java.lang.String, java.lang.Class, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getLeftSideRelatedEntities(Entity entity, String relation,
			Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getLeftSideRelatedEntities(entity, relation, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}
	}

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entitytype
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntities(Entity entity, String relation, String entityType,
			Class<?> expectedClassType) throws DaasClientException {
		return getLeftSideRelatedEntitiesWithGraph(entity, relation, entityType, expectedClassType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.daas.client.IDaasClient#getLeftSideRelatedEntities(com.bbytes.daas.domain.Entity,
	 * java.lang.String, java.lang.String, java.lang.Class,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void getLeftSideRelatedEntities(Entity entity, String relation, String entityTypeName,
			Class<?> expectedClassType, AsyncResultHandler<List<T>> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			List<T> result = getLeftSideRelatedEntities(entity, relation, entityTypeName, expectedClassType);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will add relation between entities . The object mapping looks like : entity
	 * ---relation----> toBeRelatedEntity
	 * 
	 * @param entity
	 * @param toBeRelatedEntity
	 * @param relation
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> boolean addRelation(T entity, T toBeRelatedEntity, String relation)
			throws DaasClientException {
		return relateOrRemoveRelationBetweenEntity(entity, entity.getClass().getSimpleName(), toBeRelatedEntity,
				toBeRelatedEntity.getClass().getSimpleName(), relation, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#addRelation(com.bbytes.daas.domain.Entity,
	 * java.lang.String, com.bbytes.daas.domain.Entity, java.lang.String, java.lang.String,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void addRelation(T entity, String entityTypeName, T toBeRelatedEntity,
			String toBeRelatedEntityType, String relation, AsyncResultHandler<Boolean> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			Boolean result = addRelation(entity, entityTypeName, toBeRelatedEntity, toBeRelatedEntityType, relation);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will add relation between entities . The object mapping looks like : entity
	 * ---relation----> toBeRelatedEntity
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @param toBeRelatedEntity
	 * @param relation
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> boolean addRelation(T entity, String entityType, T toBeRelatedEntity,
			String toBeRelatedEntityType, String relation) throws DaasClientException {
		return relateOrRemoveRelationBetweenEntity(entity, entityType, toBeRelatedEntity, toBeRelatedEntityType,
				relation, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#addRelation(com.bbytes.daas.domain.Entity,
	 * com.bbytes.daas.domain.Entity, java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void addRelation(T entity, T toBeRelatedEntity, String relation,
			AsyncResultHandler<Boolean> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			Boolean result = addRelation(entity, toBeRelatedEntity, relation);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will remove relation between entities . The object mapping that looked like : entity
	 * ---relation----> toBeRelatedEntity will no more be valid , the method will remove this
	 * relation between entity and toBeRelatedEntity.
	 * 
	 * @param entity
	 * @param toBeRelatedEntity
	 * @param relation
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> boolean removeRelation(T entity, T toBeRelatedEntity, String relation)
			throws DaasClientException {
		return relateOrRemoveRelationBetweenEntity(entity, entity.getClass().getSimpleName(), toBeRelatedEntity,
				toBeRelatedEntity.getClass().getSimpleName(), relation, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#removeRelation(com.bbytes.daas.domain.Entity,
	 * com.bbytes.daas.domain.Entity, java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void removeRelation(T entity, T toBeRelatedEntity, String relation,
			AsyncResultHandler<Boolean> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			Boolean result = removeRelation(entity, toBeRelatedEntity, relation);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will remove relation between entities . The object mapping that looked like : entity
	 * ---relation----> toBeRelatedEntity will no more be valid , the method will remove this
	 * relation between entity and toBeRelatedEntity.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @param toBeRelatedEntity
	 * @param toBeRelatedEntityType
	 *            table name
	 * @param relation
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> boolean removeRelation(T entity, String entityType, T toBeRelatedEntity,
			String toBeRelatedEntityType, String relation) throws DaasClientException {
		return relateOrRemoveRelationBetweenEntity(entity, entityType, toBeRelatedEntity, toBeRelatedEntityType,
				relation, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#removeRelation(com.bbytes.daas.domain.Entity,
	 * java.lang.String, com.bbytes.daas.domain.Entity, java.lang.String, java.lang.String,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void removeRelation(T entity, String entityTypeName, T toBeRelatedEntity,
			String toBeRelatedEntityTypeName, String relation, AsyncResultHandler<Boolean> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			Boolean result = removeRelation(entity, entityTypeName, toBeRelatedEntity, toBeRelatedEntityTypeName,
					relation);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	private <T extends Entity> boolean relateOrRemoveRelationBetweenEntity(T entity, String entityType,
			T toBeRelatedEntity, String toBeRelatedEntityType, String relation, boolean relate)
			throws DaasClientException {
		try {

			if (entity == null || toBeRelatedEntity == null || relation == null) {
				throw new IllegalArgumentException("method args cannot be null");
			}

			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityType + "/"
					+ entity.getUuid() + "/" + relation + "/" + toBeRelatedEntityType + "/"
					+ toBeRelatedEntity.getUuid();

			Future<Response> f = null;
			if (relate) {
				f = buildRequest("post", url).setHeader("Content-Type", "application/json").execute();
			} else {
				f = buildRequest("delete", url).setHeader("Content-Type", "application/json").execute();
			}

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			return true;

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/**
	 * Create the entity in Daas Db. The UUID will be auto assigned as it is a new entity.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T createEntity(T entity) throws DaasClientException {
		return createOrUpdateEntityFullGraph(entity, "create");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#createEntity(com.bbytes.daas.domain.Entity,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void createEntity(T entity, AsyncResultHandler<T> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			T result = createEntity(entity);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Create the entity in Daas Db. The UUID will be auto assigned as it is a new entity.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T createEntity(T entity, String entityType) throws DaasClientException {
		return createOrUpdateEntityFullGraph(entity, entityType, "create");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#createEntity(com.bbytes.daas.domain.Entity,
	 * java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void createEntity(T entity, String entityTypeName,
			AsyncResultHandler<T> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			T result = createEntity(entity, entityTypeName);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will update the entity with the uuid in the entity object , if the uuid is missing then
	 * it will be treated as new object.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T updateEntity(T entity, String entityType) throws DaasClientException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}
		if (entity.getUuid() == null || entity.getUuid().isEmpty())
			return createOrUpdateEntityFullGraph(entity, entityType, "create");

		return createOrUpdateEntityFullGraph(entity, entityType, "update");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#updateEntity(com.bbytes.daas.domain.Entity,
	 * java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void updateEntity(T entity, String entityTypeName,
			AsyncResultHandler<T> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			T result = updateEntity(entity, entityTypeName);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will update the entity with the uuid in the entity object , if the uuid is missing then
	 * it will be treated as new object.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T updateEntity(T entity) throws DaasClientException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}
		if (entity.getUuid() == null || entity.getUuid().isEmpty())
			return createOrUpdateEntityFullGraph(entity, "create");

		return createOrUpdateEntityFullGraph(entity, "update");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#updateEntity(com.bbytes.daas.domain.Entity,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void updateEntity(T entity, AsyncResultHandler<T> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			T result = updateEntity(entity);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * Doesn't read the annotation {@link Relation} to store full graph but jus saves or updates the
	 * entity passed as single entity and not graph.
	 * 
	 * @param entity
	 * @param action
	 *            can be 'create' or 'update'
	 * @return
	 * @throws DaasClientException
	 */
	@SuppressWarnings("unused")
	private <T extends Entity> T createOrUpdateSingleEntity(T entity, String action) throws DaasClientException {
		return createOrUpdateSingleEntity(entity, entity.getClass().getSimpleName(), action);
	}

	/**
	 * Doesn't read the annotation {@link Relation} to store full graph but jus saves or updates the
	 * entity passed as single entity and not graph.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @param action
	 *            can be 'create' or 'update'
	 * @return
	 * @throws DaasClientException
	 */
	@SuppressWarnings("unchecked")
	private <T extends Entity> T createOrUpdateSingleEntity(T entity, String entityType, String action)
			throws DaasClientException {

		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		try {
			String url;
			Future<Response> f = null;
			if (action.equals("create")) {
				url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityType;
				f = buildRequest("post", url).setBody(gson.toJson(entity))
						.setHeader("Content-Type", "application/json").execute();
			} else {
				url = baseURL + "/" + accountName + "/" + applicationName + "/" + entity.getClass().getSimpleName()
						+ "/" + entity.getUuid();
				f = buildRequest("put", url).setBody(gson.toJson(entity)).setHeader("Content-Type", "application/json")
						.execute();
			}
			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			return (T) gson.fromJson(r.getResponseBody(), entity.getClass());

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/**
	 * Reads the {@link Relation} annotation and saves/updates the entire object graph recursively
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @param action
	 *            can be 'create' or 'update'
	 * @return
	 * @throws DaasClientException
	 */
	private <T extends Entity> T createOrUpdateEntityFullGraph(T entity, String enityType, String action)
			throws DaasClientException {

		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		try {
			Map<String, Field> relationNameAndEntity = RelationAnnotationProcessor.getRelationAndEntity(entity);
			Map<String, T> relationToEntity = new HashMap<String, T>();

			for (Iterator<String> iterator = relationNameAndEntity.keySet().iterator(); iterator.hasNext();) {
				boolean applyCascading = true;
				String relation = iterator.next();
				Field toBeRelatedEntityField = relationNameAndEntity.get(relation);

				// apply cascade logic . Here only update and create cascade type makes sense so we
				// check only those. if the cascading annotation is missing we treat it as
				// CascadeType.ALL
				if (action.equals("create") && !RelationAnnotationProcessor.isCascadeCreate(toBeRelatedEntityField)) {
					applyCascading = false;
				} else if (action.equals("update")
						&& !RelationAnnotationProcessor.isCascadeUpdate(toBeRelatedEntityField)) {
					applyCascading = false;
				}

				if (applyCascading) {
					// only when cascading logic makes sense we save/update entire object graph
					// based on relation name
					@SuppressWarnings("unchecked")
					T toBeRelatedEntity = (T) toBeRelatedEntityField.get(entity);
					if (toBeRelatedEntity == null)
						continue;

					toBeRelatedEntity = updateEntity(toBeRelatedEntity);
					relationToEntity.put(relation, toBeRelatedEntity);

				}

			}

			entity = createOrUpdateSingleEntity(entity, enityType, action);

			for (Iterator<String> iterator = relationToEntity.keySet().iterator(); iterator.hasNext();) {
				String relation = iterator.next();

				Field toBeRelatedEntityField = relationNameAndEntity.get(relation);
				T toBeRelatedEntity = relationToEntity.get(relation);
				toBeRelatedEntityField.set(entity, toBeRelatedEntity);

				boolean success = addRelation(entity, toBeRelatedEntity, relation);
				if (!success)
					throw new DaasClientException("Failed while creating relation between entities of type "
							+ enityType + " and " + toBeRelatedEntity.getClass().getSimpleName() + " with ids "
							+ entity.getUuid() + " and " + toBeRelatedEntity.getUuid() + " respectively");
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DaasClientException(e);
		}

		return entity;
	}

	/**
	 * Reads the {@link Relation} annotation and saves/updates the entire object graph recursively
	 * 
	 * @param entity
	 * @param action
	 *            can be 'create' or 'update'
	 * @return
	 * @throws DaasClientException
	 */
	private <T extends Entity> T createOrUpdateEntityFullGraph(T entity, String action) throws DaasClientException {
		return createOrUpdateEntityFullGraph(entity, entity.getClass().getSimpleName(), action);
	}

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @return String 'success'
	 * @throws DaasClientException
	 */
	public <T extends Entity> String deleteEntity(T entity) throws DaasClientException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}
		return deleteEntityFullGraph(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#deleteEntity(com.bbytes.daas.domain.Entity,
	 * com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void deleteEntity(T entity, AsyncResultHandler<String> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;

		try {
			String result = deleteEntity(entity);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @return String 'success'
	 * @throws DaasClientException
	 */
	public <T extends Entity> String deleteEntity(T entity, String entityType) throws DaasClientException {
		if (entity == null || entityType == null) {
			throw new IllegalArgumentException("Entity or Type cannot be null");
		}
		return deleteEntityFullGraph(entity, entityType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.client.IDaasClient#deleteEntity(com.bbytes.daas.domain.Entity,
	 * java.lang.String, com.bbytes.daas.client.AsyncResultHandler)
	 */
	@Override
	public <T extends Entity> void deleteEntity(T entity, String entityTypeName,
			AsyncResultHandler<String> asyncResultHandler) {
		if (asyncResultHandler == null)
			return;
		
		try {
			String result = deleteEntity(entity,entityTypeName);
			asyncResultHandler.onComplete(result);
		} catch (DaasClientException e) {
			asyncResultHandler.onError(e);
		}

	}

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @return String 'success'
	 * @throws DaasClientException
	 */
	private <T extends Entity> String deleteSingleEntity(T entity) throws DaasClientException {
		return deleteSingleEntity(entity, entity.getClass().getSimpleName());
	}

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @return String 'success'
	 * @throws DaasClientException
	 */
	private <T extends Entity> String deleteSingleEntity(T entity, String entityType) throws DaasClientException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		if (entity.getUuid() == null) {
			throw new IllegalArgumentException(
					"Trying to delete a new entity that is not saved in DB as the UUID is missing");
		}

		try {
			String url = baseURL + "/" + accountName + "/" + applicationName + "/" + entityType + "/"
					+ entity.getUuid();
			Future<Response> f = buildRequest("delete", url).setHeader("Content-Type", "application/json").execute();

			Response r = f.get();

			DaasClientUtil.checkResponse(r);

			return r.getResponseBody();

		} catch (Exception e) {
			throw new DaasClientException(e);
		}
	}

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	private <T extends Entity> String deleteEntityFullGraph(T entity) throws DaasClientException {
		return deleteEntityFullGraph(entity, entity.getClass().getSimpleName());

	}

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	private <T extends Entity> String deleteEntityFullGraph(T entity, String entityType) throws DaasClientException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		try {
			Map<String, Field> relationNameAndEntity = RelationAnnotationProcessor.getRelationAndEntity(entity);
			for (Iterator<String> iterator = relationNameAndEntity.keySet().iterator(); iterator.hasNext();) {

				String relation = iterator.next();
				Field toBeDeletedEntityField = relationNameAndEntity.get(relation);

				// apply cascade logic . Here only delete cascade type makes sense so we
				// check only that . if the cascading annotation is missing we treat it as
				// CascadeType.ALL
				if (RelationAnnotationProcessor.isCascadeDelete(toBeDeletedEntityField)) {
					// only when cascading logic makes sense we delete entire object graph
					// based on relation name
					@SuppressWarnings("unchecked")
					T toBeDeletedEntity = (T) toBeDeletedEntityField.get(entity);

					if (toBeDeletedEntity == null || toBeDeletedEntity.getUuid() == null)
						continue;

					// remove relation before deleting
					boolean success = removeRelation(entity, toBeDeletedEntity, relation);

					if (!success)
						throw new DaasClientException("Failed while removing relation between entities of type "
								+ entity.getClass().getSimpleName() + " and "
								+ toBeDeletedEntity.getClass().getSimpleName() + " with ids " + entity.getUuid()
								+ " and " + toBeDeletedEntity.getUuid() + " respectively");

					// after removing the relation , delete the entity.
					String result = deleteSingleEntity(toBeDeletedEntity);

					if (!result.equals("success"))
						throw new DaasClientException("Failed while deleting entity of type "
								+ toBeDeletedEntity.getClass().getSimpleName() + " and with id "
								+ toBeDeletedEntity.getUuid());
				}

			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DaasClientException(e);
		}

		// finally delete the entity and return result
		return deleteSingleEntity(entity, entityType);

	}

	protected BoundRequestBuilder buildRequest(String httpMethodType, String url) {
		return DaasClientUtil.buildRequest(asyncHttpClient, httpMethodType, url, token);
	}

	public void close() {
		if (asyncHttpClient != null && !asyncHttpClient.isClosed())
			asyncHttpClient.close();
	}

}
