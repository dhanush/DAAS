/**
 * This module is used to access the Management operations of DAAS.
 * 
 */
define([ "module/http", "module/config" ], function(http, config) {

	var _mgmtBaseUrl = config.getBaseUrl() + "management/";
	var _outhBaseUrl = config.getBaseUrl()
			+ "oauth/token?grant_type=client_credentials";

	return {
		/**
		 * Login for super admin or account admin user
		 * 
		 * @param userName
		 * @param password
		 * @param accountName -
		 *            if accountName is null, checks if the user is super admin
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
		createAccount : function(accountName, callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName;
			http.post(url, null, callback, "json", config.CONTENT_TYPE_JSON,
					authToken);
		},
		/**
		 * Sends a request to get a created account
		 * 
		 * @param accountName
		 * @param callback
		 * @param authToken
		 * @returns
		 */
		getAccount : function(accountName, callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName;
			http.get(url, callback, "json", authToken);
		},
		/**
		 * Sends request to delete the account
		 * 
		 * @param accountName
		 * @param callback
		 * @param authToken
		 * @returns
		 */
		deleteAccount : function(accountName, callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName;
			http.deleteRequest(url, callback, "json", authToken);
		},

		createAccountUser : function(accountName, user, callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName + "/user";
			http.post(url, user, callback, "json", config.CONTENT_TYPE_JSON,
					authToken);
		},

		getAccountUsers : function(accountName, callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName + "/user";
			http.get(url, callback, "json", authToken);
		},

		createApplication : function(accountName, applicationName, application,
				callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName
					+ "/applications/" + applicationName;
			http.post(url, application, callback, "json",
					config.CONTENT_TYPE_JSON, authToken);
		},

		editApplication : function(accountName, applicationName, application,
				callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName
					+ "/applications/" + applicationName + "/edit";
			http.put(url, application, callback, "json",
					config.CONTENT_TYPE_JSON, authToken);
		},

		getApplication : function(accountName, applicationName, callback,
				authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName
					+ "/applications/" + applicationName;
			http.get(url, callback, "json", authToken);
		},

		deleteApplication : function(accountName, applicationName, callback,
				authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName
					+ "/applications/" + applicationName;
			http.deleteRequest(url, callback, "json", authToken);
		},

		createApplicationUser : function(accountName, applicationName, user,
				callback, authToken) {
			var url = _mgmtBaseUrl + "accounts/" + accountName
					+ "/applications/" + applicationName + "/user";
			http.post(url, application, callback, "json",
					config.CONTENT_TYPE_JSON, authToken);
		},
	};
});