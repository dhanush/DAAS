package com.bbytes.daas.rest.client;

import java.util.List;

import com.bbytes.endure.domain.EntityType;

/**
 * Generic interface for accessing the Rest URL's of BAAS. Should support all HTTP CRUD operations,
 * POST, GET, UPDATE and DELETE and login
 * 
 * @author Dhanush Gopinath
 * 
 * @version 1.0.0
 */
public interface BaasClient {

	/**
	 * Returns all the Entities
	 * 
	 * @param completeUrl
	 * @param entityType - - should be the name or type of {@link EntityType}
	 *            TODO
	 * @param accessToken
	 * @return
	 */
	<T> List<T> getAll(String completeUrl, String entityType, String accessToken) throws BaasClientException;

	/**
	 * Returns the specific entity
	 * 
	 * @param completeUrl
	 * @param entityType - should be the name or type of {@link EntityType}
	 *            TODO
	 * @param accessToken
	 * @return
	 */
	<T> T get(String completeUrl, String entityType, String accessToken) throws BaasClientException;
	
	/**
	 * Logins to the Baas Application
	 * @param baseUrl TODO
	 * @param userName
	 * @param password
	 * @return
	 * @throws BaasClientException
	 */
	String login(String baseUrl, String userName, String password) throws BaasClientException;
}
