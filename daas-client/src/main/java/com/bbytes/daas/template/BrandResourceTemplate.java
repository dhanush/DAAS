package com.bbytes.daas.template;

import java.util.List;

import com.bbytes.endure.domain.Brand;
import com.bbytes.endure.domain.Photo;

/**
 * Interface for Brands
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public interface BrandResourceTemplate extends BaasRestTemplate {

	/**
	 * Creates a Brand Entity under an application
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param brandName
	 * @param tagLine
	 * @param logo
	 * @return
	 */
	public Brand createBrand(String organizationName, String applicationName, String brandName, String tagLine,
			Photo logo) throws BaasException;

	/**
	 * Replaces the existing logo of the brand. If logo is not available created the logo
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param brandUuid
	 * @param logo
	 * @return
	 */
	public Brand addLogo(String organizationName, String applicationName, String brandUuid, Photo logo)
			throws BaasException;

	/**
	 * Removes the logo
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param brandUuid
	 * @return
	 */
	public Brand removeLogo(String organizationName, String applicationName, String brandUuid)
			throws BaasException;

	/**
	 * Edit the tagline
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param brandUuid
	 * @param newTagLine
	 * @return
	 */
	public Brand editTagline(String organizationName, String applicationName, String brandUuid, String newTagLine)
			throws BaasException;

	/**
	 * Add deals to a brand
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param brandUuid
	 * @param deals
	 * @return
	 */
	public Brand addDeals(String organizationName, String applicationName, String brandUuid, List<String> dealUuids)
			throws BaasException;

	public Brand addDeal(String organizationName, String applicationName, String brandUuid, String dealUuid)
			throws BaasException;

	/**
	 * Removes a specific deal from the brand, but does not delete the deal
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param brandUuid
	 * @param dealUuid
	 * @return
	 */
	public Brand removeDeal(String organizationName, String applicationName, String brandUuid, String dealUuid)
			throws BaasException;
}
