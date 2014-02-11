/**
 * A module to test the ping status of DAAs
 */
define([ "module/http","module/config" ], function(http,config) {
	return {
		ping : function(callback) {
			var url = config.getBaseUrl()+"ping";
			http.get(url, callback, "text");
		}
	};
});