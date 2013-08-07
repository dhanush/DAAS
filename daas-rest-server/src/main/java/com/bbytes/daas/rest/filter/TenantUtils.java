package com.bbytes.daas.rest.filter;


/**
 * Utility class for Tenant Routing
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class TenantUtils {

	private static final String URL_PATH_MANAGEMENT = "management";
	private static final String URL_PATH_MANAGEMENT_LOGIN = "management/login";
	private static final String URL_PATH_MANAGEMENT_ACCOUNT = "management/account";
	private static final String URL_PATH_MANAGEMENT_ACCOUNTS = "management/accounts";

	/**
	 * Returns the account name from the path after the url context path 
	 * 
	 * Logic: if doesnt start with management,
	 * get the first parameter from path parameter else check if it doesn't starts with management/login or
	 * management/account or management/accounts and return the second one
	 * 
	 * @param pathAfterUrlContext
	 * @return
	 */
	public static String getAccountFromURL(String pathAfterUrlContext) {
		System.out.println(pathAfterUrlContext);
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
			if (!pathParameter.startsWith(URL_PATH_MANAGEMENT_LOGIN)
					&& !pathParameter.startsWith(URL_PATH_MANAGEMENT_ACCOUNT)
					&& !pathParameter.startsWith(URL_PATH_MANAGEMENT_ACCOUNTS)) {
				accountName = pathParameter.split("/")[1];
			}
		}
		return accountName;
	}
}
