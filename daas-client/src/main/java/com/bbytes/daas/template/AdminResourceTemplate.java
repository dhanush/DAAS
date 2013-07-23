/**
 * 
 */
package com.bbytes.daas.template;

import com.bbytes.endure.domain.usergrid.ApplicationInfo;
import com.bbytes.endure.domain.usergrid.ClientCredentialsInfo;
import com.bbytes.endure.domain.usergrid.OrganizationOwnerInfo;
import com.bbytes.endure.domain.usergrid.User;

/**
 * An interface that consists of the API's that are required to access the
 * Usergrid Rest Service
 * 
 * @author Dhanush Gopinath
 * @since 1.0.0
 * 
 * 
 */
public interface AdminResourceTemplate extends BaasRestTemplate {

	/**
	 * Logins to the Usergird application
	 * 
	 * @param username
	 * @param password
	 * @param organizationName
	 *            TODO
	 * @param applicationName
	 *            TODO
	 * @return
	 * @throws BaasException
	 */
	public User login(String username, String password,
			String organizationName, String applicationName)
			throws BaasException;

	/**
	 * Create
	 * 
	 * @param organizationName
	 * @param username
	 * @param name
	 * @param email
	 * @param password
	 * @return
	 * @throws BaasException
	 */
	public OrganizationOwnerInfo createNewOrganization(String organizationName,
			String username, String name, String email, String password)
			throws BaasException;

	/**
	 * Creates an application under the organization
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param accessToken TODO
	 * @return
	 * @throws BaasException
	 */
	public ApplicationInfo createNewApplication(String organizationName,
			String applicationName, String accessToken) throws BaasException;

	/**
	 * Returns the access token at organization level based on the the grantType
	 * 
	 * @param username
	 * @param password
	 * @param grantType
	 * @param credentials
	 * @return
	 * @throws BaasException 
	 */
	public String getOrganizationLevelAccessToken(String username,
			String password, String grantType, ClientCredentialsInfo credentials) throws BaasException;

	/**
	 * Returns the access token at application level based on the grant type
	 * 
	 * @param username
	 * @param password
	 * @param grantType
	 * @param credentials
	 * @param organizationName
	 * @param applicationName
	 * @return
	 * @throws BaasException 
	 */
	public String getApplicationLevelAccessToken(String username,
			String password, String grantType,
			ClientCredentialsInfo credentials, String organizationName,
			String applicationName) throws BaasException;
	
	/**
	 * Creates new {@link ClientCredentialsInfo} and returns it
	 * 
	 * @param organizationName
	 * @return
	 */
	public ClientCredentialsInfo generateOrganizationCredentials(String organizationName);
	/**
	 * Returns the existing client credentials for organization
	 * 
	 * @param organizationName
	 * @return
	 */
	public ClientCredentialsInfo getOrganizationCredentials(String organizationName);
	
	/**
	 * Generates and returns the {@link ClientCredentialsInfo} object for application 
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @return
	 */
	public ClientCredentialsInfo generateApplicationCredentials(String organizationName, String applicationName);
	/**
	 * Returns the {@link ClientCredentialsInfo} object for application 
	 * @param organizationName
	 * @param applicationName
	 * @return
	 */
	public ClientCredentialsInfo getApplicationCredentials(String organizationName, String applicationName);
	
}
