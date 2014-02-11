/**
 * This module is used to access the Management operations of DAAS.
 * 
 */
define([ "module/http", "module/config" ], function(http, config) {

	var _mgmtBaseUrl = config.getBaseUrl() + "management/";
	var _outhBaseUrl = config.getBaseUrl()
			+ "/oauth/token?grant_type=client_credentials";

	return {
		/**
		 * Login for super admin or account admin user
		 * 
		 * @param userName
		 * @param password
		 * @param accountName - if accountName is null, checks if the user is super admin
		 * @param callback
		 * @returns
		 */
		login : function(userName, password, accountName, callback) {
			var url;
			if (accountName) {
				url = _outhBaseUrl + "&client_id=" + userName
						+ "&client_secret=" + password + "&account="
						+ accountName;
			} else {
				url = _outhBaseUrl + "&client_id=" + userName
						+ "&client_secret=" + password;
			}
			http.get(url, callback, "json");
		},

		/**
		 * Sends a request to create a new account
		 * 
		 * @param accountName
		 * @param callback
		 * @returns
		 */
		createAccount : function(accountName, callback) {
			var url = _mgmtBaseUrl + "accounts/" + accountName;
			http.post(url, callback, "json");
		},

		getAccount : function(accountName, callback) {
			var url = _mgmtBaseUrl + "accounts/" + accountName;
			http.get(url, callback, "json");
		}

	};
});