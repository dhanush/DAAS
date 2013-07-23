package com.bbytes.daas.template;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bbytes.endure.domain.Deal;
import com.bbytes.endure.domain.Photo;

/**
 * API interface for deals
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public interface DealResourceTemplate extends BaasRestTemplate {

	public static final String ENDPOINT = "/deals";
	public static final String DEAL_STORE_RELATION = "/valid";
	/**
	 * Creates a new deal for an organization and application
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param title
	 * @param description
	 * @param validFrom
	 * @param validTo
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	public Deal createDeal(String organizationName, String applicationName, String title, String description,
			Date validFrom, Date validTo, String accessToken) throws BaasException;

	/**
	 * Adds a deal to a store.
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUUID
	 * @param dealUUId
	 * @param accessToken
	 *            TODO
	 * @return
	 */
	public boolean addValidStore(String organizationName, String applicationName, String storeUUID, String dealUUId,
			String accessToken) throws BaasException;

	/**
	 * Removes a deal from the store
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUUID
	 * @param dealUUId
	 * @param accessToken
	 *            TODO
	 * @return
	 */
	public boolean removeValidStore(String organizationName, String applicationName, String storeUUID, String dealUUId,
			String accessToken) throws BaasException;

	/**
	 * Returns all the deals
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	public List<Deal> getAllDeals(String organizationName, String applicationName, String accessToken)
			throws BaasException;

	/**
	 * Return a specific deal
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUuid
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	public Deal getDeal(String organizationName, String applicationName, String dealUuid, String accessToken)
			throws BaasException;

	/**
	 * Deletes a deal from the system and returns it
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @return
	 */
	public Deal deleteDeal(String organizationName, String applicationName, String dealUUId, String accessToken)
			throws BaasException;

	/**
	 * Edits the existing deal information including the metadata if there is any. In case you are
	 * editing the metadata then pass it as a map with key as "metadata" in the map dealInformation
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param dealInformation
	 * @return
	 */
	public Deal editDeal(String organizationName, String applicationName, String dealUUId,
			Map<String, Object> dealInformation, String accessToken) throws BaasException;

	/**
	 * Edits only the dates of the deal
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param validFrom
	 * @param validTo
	 * @return
	 * @throws BaasException
	 */
	public Deal editDealValidity(String organizationName, String applicationName, String dealUUId, Date validFrom,
			Date validTo, String accessToken) throws BaasException;

	/**
	 * Add extra information to a deal which will go into the metadata of the entity
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param properties
	 * @return
	 */
	public Deal addDealInformation(String organizationName, String applicationName, String dealUUId,
			Map<String, Object> dealInformation, String accessToken) throws BaasException;

	/**
	 * Add photos for deals
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param photos
	 * @return
	 */
	public Deal addDealPhotos(String organizationName, String applicationName, String dealUUId, List<Photo> photos,
			String accessToken) throws BaasException;

	/**
	 * Add contact information for the deals
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param contactNumbers
	 * @return
	 */
	public Deal addDealContacts(String organizationName, String applicationName, String dealUUId,
			List<String> contactNumbers, String accessToken) throws BaasException;

	/**
	 * Add a website for a deal
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param dealUrl
	 * @return
	 */
	public Deal addDealWebsite(String organizationName, String applicationName, String dealUUId, String dealUrl,
			String accessToken) throws BaasException;

//	/**
//	 * Returns the valid stores for the deal
//	 * 
//	 * @param organizationName
//	 * @param applicationName
//	 * @param dealUUId
//	 * @return
//	 * @throws BaasException
//	 */
//	public List<Store> getValidStores(String organizationName, String applicationName, String dealUUId,
//			String accessToken) throws BaasException;
	
	/**
	 * Returns all the valid deals at a store
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param storeUuid
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	public List<Deal> getDealsValidAtStore(String organizationName, String applicationName, String storeUuid,
			String accessToken) throws BaasException;

}
