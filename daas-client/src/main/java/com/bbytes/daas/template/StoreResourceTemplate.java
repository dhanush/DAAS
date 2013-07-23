/**
 * 
 */
package com.bbytes.daas.template;

import java.util.List;
import java.util.Map;

import com.bbytes.endure.domain.GeoLocation;
import com.bbytes.endure.domain.Store;
import com.bbytes.endure.domain.StoreInfo;

/**
 * API that extends {@link UsergridRestTemplate} to handle all {@link Store} related operations
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 * 
 */
public interface StoreResourceTemplate extends BaasRestTemplate {

	public static final String ENDPOINT = "/stores";

	// public static final String QUERY_LANGUAGE_LOCATION = "?ql=select * where location"+
	// (char)61+"\'";
	public static String QUERY_LANGUAGE_LOCATION = "ql=select%20*%20where%20location%20%3D%20\'";

	/**
	 * Creates a Store in an organization and application for a locality and custom properties.
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param accessToken
	 *            TODO
	 * @param locality
	 * @param properties
	 * @return
	 * @throws BaasException
	 */
	public Store createStore(String organizationName, String applicationName, String accessToken, String locality,
			Map<String, Object> properties) throws BaasException;

	/**
	 * Creates a Store in an organization and application for a locality, geo location and
	 * properties.
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param accessToken
	 * @param locality
	 * @param latitude
	 * @param longitude
	 * @param properties
	 * @return
	 * @throws BaasException
	 */
	public Store createStore(String organizationName, String applicationName, String accessToken, String locality,
			double latitude, double longitude, Map<String, Object> properties) throws BaasException;

	/**
	 * Returns all the stores under this organization application
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param accessToken
	 *            TODO
	 * @return
	 * @throws BaasException
	 */
	public List<Store> getStores(String organizationName, String applicationName, String accessToken)
			throws BaasException;
	
	/**
	 * Returns a store for uuid
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUUID
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	public Store getStore(String organizationName, String applicationName, String storeUUID, String accessToken) throws BaasException;

	/**
	 * Returns the stores nearby to the location
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param location
	 * @return
	 * @throws BaasException
	 */
	public List<Store> getStoresByLocality(String organizationName, String applicationName, String location,
			String accessToken) throws BaasException;

	/**
	 * Returns stored based on geo location
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param location
	 * @return
	 * @throws BaasException
	 */
	public List<Store> getStoresFromGeoLocation(String organizationName, String applicationName, GeoLocation location)
			throws BaasException;

	/**
	 * Adds the Social Media information to a Store
	 * 
	 * @param storeUUID
	 * @param fbUrl
	 * @param twitterUrl
	 * @param gPlusUrl
	 * @return
	 * @throws BaasException
	 */
	public Store addSocialMedia(String organizationName, String applicationName, String storeUUID, String fbUrl,
			String twitterUrl, String gPlusUrl, String accessToken) throws BaasException;

	/**
	 * Adds the geolocation information to a store
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUUID
	 * @param location
	 * @return
	 * @throws BaasException
	 */
	public Store addGeoLocation(String organizationName, String applicationName, String storeUUID,
			GeoLocation location, String accessToken) throws BaasException;

	/**
	 * Adds the map locator url
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUUID
	 * @param mapLocatorUrl
	 * @return
	 * @throws BaasException
	 */
	public Store addMapLocationUrl(String organizationName, String applicationName, String storeUUID,
			String mapLocatorUrl, String accessToken) throws BaasException;

	/**
	 * Adds the store info to the Store
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUUID
	 * @param accessToken
	 * @param storeInfo
	 * @return
	 * @throws BaasException
	 */
	public Store addStoreInfo(String organizationName, String applicationName, String storeUUID, String accessToken, StoreInfo storeInfo)
			throws BaasException;
	
	/**
	 * Returns all the stores that have a deal which is valid
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	public List<Store> getStoresWhereDealIsValid(String organizationName, String applicationName, String dealUUId,
			String accessToken) throws BaasException;
	
	
}
