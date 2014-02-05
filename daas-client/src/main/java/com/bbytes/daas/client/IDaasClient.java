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
package com.bbytes.daas.client;

import java.util.List;
import java.util.Map;

import com.bbytes.daas.domain.Entity;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public interface IDaasClient {

	/**
	 * To verify if the Daas client login session is available and still valid . If the returned
	 * value is false then re-login is required.
	 * 
	 * @return If false then re-login
	 */
	public boolean isLoggedIn();

	/**
	 * To verify that it is connected to server , we just ping the server and if response is 200
	 * then we return true
	 * 
	 * @return
	 */
	public boolean isConnected();

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
			throws DaasClientException;

	/**
	 * Login to your tenant account - Async version
	 * 
	 * @param accountName
	 * @param applicationName
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	public void login(String accountName, String applicationName, String clientId, String clientSecret,
			AsyncResultHandler<Boolean> asyncResultHandler) ;

	/**
	 * Create the entity in Daas Db. The UUID will be auto assigned as it is a new entity.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T createEntity(T entity) throws DaasClientException;

	/**
	 * Create the entity in Daas Db. The UUID will be auto assigned as it is a new entity.
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends Entity> void createEntity(T entity, AsyncResultHandler<T> asyncResultHandler);

	/**
	 * Create the entity in Daas Db. The UUID will be auto assigned as it is a new entity.
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T createEntity(T entity, String entityTypeName) throws DaasClientException;

	/**
	 * Create the entity in Daas Db. The UUID will be auto assigned as it is a new entity.Async
	 * version
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @return
	 */
	public <T extends Entity> void createEntity(T entity, String entityTypeName,
			AsyncResultHandler<T> asyncResultHandler);

	/**
	 * This will update the entity with the uuid in the entity object , if the uuid is missing then
	 * it will be treated as new object.
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T updateEntity(T entity, String entityTypeName) throws DaasClientException;

	/**
	 * This will update the entity with the uuid in the entity object , if the uuid is missing then
	 * it will be treated as new object.
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @return
	 */
	public <T extends Entity> void updateEntity(T entity, String entityTypeName,
			AsyncResultHandler<T> asyncResultHandler);

	/**
	 * This will update the entity with the uuid in the entity object , if the uuid is missing then
	 * it will be treated as new object.
	 * 
	 * @param entity
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T updateEntity(T entity) throws DaasClientException;

	/**
	 * This will update the entity with the uuid in the entity object , if the uuid is missing then
	 * it will be treated as new object.
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends Entity> void updateEntity(T entity, AsyncResultHandler<T> asyncResultHandler);

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @return String 'success'
	 * @throws DaasClientException
	 */
	public <T extends Entity> String deleteEntity(T entity) throws DaasClientException;

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.Async version
	 * 
	 * @param entity
	 * @return String 'success'
	 */
	public <T extends Entity> void deleteEntity(T entity, AsyncResultHandler<String> asyncResultHandler);

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @return String 'success'
	 * @throws DaasClientException
	 */
	public <T extends Entity> String deleteEntity(T entity, String entityTypeName) throws DaasClientException;

	/**
	 * This will delete the entity with the uuid inside the entity. It return success string if it
	 * deletes the entity.Async version
	 * 
	 * @param entity
	 * @param entitytype
	 *            table name
	 * @return String 'success'
	 */
	public <T extends Entity> void deleteEntity(T entity, String entityTypeName,
			AsyncResultHandler<String> asyncResultHandler);

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
			throws DaasClientException;

	/**
	 * This will add relation between entities . The object mapping looks like : entity
	 * ---relation----> toBeRelatedEntity. Async version
	 * 
	 * @param entity
	 * @param toBeRelatedEntity
	 * @param relation
	 * @return
	 */
	public <T extends Entity> void addRelation(T entity, T toBeRelatedEntity, String relation,
			AsyncResultHandler<Boolean> asyncResultHandler);

	/**
	 * This will add relation between entities . The object mapping looks like : entity
	 * ---relation----> toBeRelatedEntity
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @param toBeRelatedEntity
	 * @param toBeRelatedEntityTypeName table name
	 * @param relation
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> boolean addRelation(T entity, String entityTypeName, T toBeRelatedEntity,
			String toBeRelatedEntityTypeName, String relation) throws DaasClientException;

	/**
	 * This will add relation between entities . The object mapping looks like : entity
	 * ---relation----> toBeRelatedEntity. Async version
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @param toBeRelatedEntity
	 * @param toBeRelatedEntityTypeName table name
	 * @param relation
	 * @return
	 */
	public <T extends Entity> void addRelation(T entity, String entityTypeName, T toBeRelatedEntity,
			String toBeRelatedEntityTypeName, String relation, AsyncResultHandler<Boolean> asyncResultHandler);

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
			throws DaasClientException;

	/**
	 * This will remove relation between entities . The object mapping that looked like : entity
	 * ---relation----> toBeRelatedEntity will no more be valid , the method will remove this
	 * relation between entity and toBeRelatedEntity. Async version
	 * 
	 * @param entity
	 * @param toBeRelatedEntity
	 * @param relation
	 * @return
	 */
	public <T extends Entity> void removeRelation(T entity, T toBeRelatedEntity, String relation,
			AsyncResultHandler<Boolean> asyncResultHandler);

	/**
	 * This will remove relation between entities . The object mapping that looked like : entity
	 * ---relation----> toBeRelatedEntity will no more be valid , the method will remove this
	 * relation between entity and toBeRelatedEntity.
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @param toBeRelatedEntity
	 * @param toBeRelatedEntityTypeName table name
	 *            table name
	 * @param relation
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> boolean removeRelation(T entity, String entityTypeName, T toBeRelatedEntity,
			String toBeRelatedEntityTypeName, String relation) throws DaasClientException;

	/**
	 * This will remove relation between entities . The object mapping that looked like : entity
	 * ---relation----> toBeRelatedEntity will no more be valid , the method will remove this
	 * relation between entity and toBeRelatedEntity.Async version
	 * 
	 * @param entity
	 * @param entityTypeName
	 *            table name
	 * @param toBeRelatedEntity
	 * @param toBeRelatedEntityTypeName table name
	 *            table name
	 * @param relation
	 * @return
	 */
	public <T extends Entity> void removeRelation(T entity, String entityTypeName, T toBeRelatedEntity,
			String toBeRelatedEntityTypeName, String relation, AsyncResultHandler<Boolean> asyncResultHandler);

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
			Map<String, String> propertyMap) throws DaasClientException;

	/**
	 * Find entity given the property map , the property check is done with OR query. If any one
	 * property name and value match then it is added to result set.Async version
	 * 
	 * @param entityTypeName
	 *            table name
	 * @param entityClassType
	 *            json to class type conversion
	 * @param propertyMap
	 *            contains property name and value
	 * @return
	 */
	public <T extends Entity> void getEntitiesByProperty(String entityTypeName, Class<T> entityClassType,
			Map<String, String> propertyMap, AsyncResultHandler<List<T>> asyncResultHandler);

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
			throws DaasClientException;

	/**
	 * Find entity given the property map , the property check is done with OR query. If any one
	 * property name and value match then it is added to result set.Async version
	 * 
	 * @param entityClassType
	 * @param propertyMap
	 *            contains property name and value
	 * @return
	 */
	public <T extends Entity> void getEntitiesByProperty(Class<T> entityClassType, Map<String, String> propertyMap,
			AsyncResultHandler<List<T>> asyncResultHandler);

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
			throws DaasClientException;

	/**
	 * It is the range query where it checks if greater than or equal to start range and smaller
	 * than or equal to end range. Any one range is required. Datatype will say what data type to be
	 * used while checking the range condition. Async version
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
	 */
	public <T extends Entity> void getEntitiesByRange(String entityTypeName, Class<T> entityClassType,
			String propertyName, String propertyDataType, String startRange, String endRange,
			AsyncResultHandler<List<T>> asyncResultHandler);

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
			String propertyDataType, String startRange, String endRange) throws DaasClientException;

	/**
	 * It is the range query where it checks if greater than or equal to start range and smaller
	 * than or equal to end range. Any one range is required. Datatype will say what data type to be
	 * used while checking the range condition.Async version
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
	 */
	public <T extends Entity> void getEntitiesByRange(Class<T> entityClassType, String propertyName,
			String propertyDataType, String startRange, String endRange,
			AsyncResultHandler<List<T>> asyncResultHandler);

	/**
	 * Get the entity given UUID
	 * 
	 * @param entityTypeName
	 *            table name
	 * @param entityClassType
	 * @param UUID
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T getEntityById(String entityTypeName, Class<T> entityClassType, String UUID)
			throws DaasClientException;
	
	/**
	 * Get the entity given UUID. Async version
	 * 
	 * @param entityTypeName
	 *            table name
	 * @param entityClassType
	 * @param UUID
	 * @return
	 */
	public <T extends Entity> void getEntityById(String entityTypeName, Class<T> entityClassType, String UUID,AsyncResultHandler<T> asyncResultHandler);

	/**
	 * Get the entity given UUID
	 * 
	 * @param entityClassType
	 * @param UUID
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> T getEntityById(Class<T> entityClassType, String UUID) throws DaasClientException;
	
	/**
	 * Get the entity given UUID. Async version
	 * 
	 * @param entityClassType
	 * @param UUID
	 * @return
	 */
	public <T extends Entity> void getEntityById(Class<T> entityClassType, String UUID,AsyncResultHandler<T> asyncResultHandler);

	/**
	 * Returns the size of the given entity type
	 * 
	 * @param entityTypeName
	 *            table name
	 * @return
	 * @throws DaasClientException
	 */
	public long getEntitySize(String entityTypeName) throws DaasClientException;
	
	/**
	 * Returns the size of the given entity type. Async version
	 * 
	 * @param entityType
	 *            table name
	 * @return
	 */
	public void getEntitySize(String entityTypeName,AsyncResultHandler<Long> asyncResultHandler);

	/**
	 * Returns the size of the given entity type
	 * 
	 * @param entityClassType
	 * @return
	 * @throws DaasClientException
	 */
	public <T extends Entity> long getEntitySize(Class<T> entityClassType) throws DaasClientException;
	
	/**
	 * Returns the size of the given entity type. Async version
	 * 
	 * @param entityClassType
	 * @return
	 */
	public <T extends Entity> void getEntitySize(Class<T> entityClassType,AsyncResultHandler<Long> asyncResultHandler);

	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 */
	public <T extends Entity> List<T> getRightSideRelatedEntities(Entity entity, String relation, String entityTypeName,
			Class<?> expectedClassType) throws DaasClientException;;
	
	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.Async version
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 */
	public <T extends Entity> void getRightSideRelatedEntities(Entity entity, String relation, String entityTypeName,
			Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

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
			Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all right side entities in a relationship . The object mapping looks like :
	 * A ---relation----> B then return all B entities that has the given relationship name.Async version
	 * 
	 * @param entity
	 *            Denotes entity A
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity B
	 * @return List of entities of type B
	 */
	public <T extends Entity> void getRightSideRelatedEntities(Entity entity, String relation,
			Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.This
	 * method does not load the member field, just fetches the main entities
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			String entityTypeName, Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.This
	 * method does not load the member field, just fetches the main entities.Async version.Async version
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 */
	public <T extends Entity> void getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			String entityTypeName, Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

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
			Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.This
	 * method does not load the member field, just fetches the main entities.Async version
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 */
	public <T extends Entity> void getLeftSideRelatedEntitiesWithOutGraph(Entity entity, String relation,
			Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			String entityTypeName, Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.Async version
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 */
	public <T extends Entity> void getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			String entityTypeName, Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

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
			Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.Async version
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 */
	public <T extends Entity> void getLeftSideRelatedEntitiesWithGraph(Entity entity, String relation,
			Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

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
			Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.Async version
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 */
	public <T extends Entity> void getLeftSideRelatedEntities(Entity entity, String relation,
			Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 * @throws DaasClientException
	 */
	public <T extends Entity> List<T> getLeftSideRelatedEntities(Entity entity, String relation, String entityTypeName,
			Class<?> expectedClassType) throws DaasClientException;
	
	/**
	 * This will return all left side entities in a relationship . The object mapping looks like : A
	 * ---relation----> B then return all A entities that has the given relationship name.Async version
	 * 
	 * @param entity
	 *            Denotes entity B
	 * @param relation
	 *            Relationship name
	 * @param entityTypeName
	 *            table name
	 * @param expectedClassType
	 *            mention the class type expected for entity A
	 * @return List of entities of type A
	 */
	public <T extends Entity> void getLeftSideRelatedEntities(Entity entity, String relation, String entityTypeName,
			Class<?> expectedClassType,AsyncResultHandler<List<T>> asyncResultHandler);

	/**
	 * Close Daas client and free resources
	 */
	public void close();

}
