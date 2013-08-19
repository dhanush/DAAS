package com.bbytes.daas.rest.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for Tenant Routing
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class TenantUtils {

	private static final String URL_PATH_MANAGEMENT = "management";
	private static final String URL_PATH_OAUTH = "oauth/";
	private static final String URL_PATH_MANAGEMENT_ACCOUNTS = "management/accounts";
	private static final String PARAMETER_ACCOUNT = "account";

	
	
	public static boolean isOAuthRequestURL(String pathAfterUrlContext)
	{
		if(pathAfterUrlContext!=null && pathAfterUrlContext.contains(URL_PATH_OAUTH))
			return true;
		
		return false;
	}
	
	public static String getAccountFromRequest(HttpServletRequest httpRequest) {
		String accountName = httpRequest.getParameter(PARAMETER_ACCOUNT);
		return accountName;
	}
	
	/**
	 * Returns the account name from the path after the url context path
	 * 
	 * Logic: if doesnt start with management, get the first parameter from path parameter else
	 * check if it doesn't starts with management/login or management/account or management/accounts
	 * and return the second one
	 * 
	 * @param pathAfterUrlContext
	 * @return
	 */
	public static String getAccountFromURL(String pathAfterUrlContext) {
		String pathParameter = pathAfterUrlContext;
		// if it is not management controller call then set the account name as the tenant from the
		// first path parameter
		String accountName = null;
		// if the path parameter doesn't start with management then account name is the first item
		// in the path parameter
		if (!pathParameter.startsWith(URL_PATH_MANAGEMENT)) {
			accountName = pathParameter.split("/")[0];
		} else {
			
			// if the path parameter starts with management and if the path parameter doesnt start
			// with any of these context paths - get the second entry in the path parameter

			// if (!pathParameter.startsWith(URL_PATH_MANAGEMENT_LOGIN)
			// && !pathParameter.startsWith(URL_PATH_MANAGEMENT_ACCOUNTS)) {
			// accountName = pathParameter.split("/")[1];
			// }
			
			// // if the path parameter starts with management/accounts but has applications path
			// inside it then it means creating or getting application inside an account - the we
			// return the 3rd entry in the array
			if (pathParameter.startsWith(URL_PATH_MANAGEMENT_ACCOUNTS) && pathParameter.contains("/applications")) {
				accountName = pathParameter.split("/")[2];
			}
		}
		return accountName;
	}
}
