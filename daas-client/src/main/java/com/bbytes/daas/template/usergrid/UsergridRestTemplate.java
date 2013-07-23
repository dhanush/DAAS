/**
 * 
 */
package com.bbytes.daas.template.usergrid;

import com.bbytes.daas.template.BaasRestTemplate;

/**
 * A base interface to connect to user grid rest services
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public interface UsergridRestTemplate extends BaasRestTemplate {

	/**
	 * Generic endpoints used in Usergrid - Construction of a complete REST url is with the help of
	 * these constants
	 */
	public static final String MANAGEMENT_ENDPOINT = "/management";
	public static final String ORGANIZATIONS_ENDPOINT = "/orgs";
	public static final String APPLICATIONS_ENDPOINT = "/apps";
	public static final String LOGIN_ENDPOINT = "/login";
	public static final String USERS_ENDPOINT = "/users";
	public static final String TOKEN_ENDPOINT = "/token";
	
}
