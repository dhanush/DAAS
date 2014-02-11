/**
 * This module is used to access the Management operations of DAAS.
 * 
 */
define([ "module/http","module/config" ], function(http,config) {
	
	var _mgmtBaseUrl = config.getBaseUrl()+"management/";
	
	return {
		/**
		 * Sends a request to create a new account
		 * @param accountName
		 * @param callback
		 * @returns
		 */
		createAccount : function(accountName, callback) {
			var url = _mgmtBaseUrl+"accounts/"+accountName;
			http.post(url,callback,"json");
		},
		
		getAccount : function(accountName, callback) {
			var url = _mgmtBaseUrl+"accounts/"+accountName;
			http.get(url,callback,"json");
		}
		
	};
});