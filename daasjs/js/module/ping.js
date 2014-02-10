define([ "module/http","module/config" ], function(http,config) {
	return {
		ping : function(callback) {
			var url = config.getBaseUrl()+"/ping";
			http.get(url, callback, "text");
		}
	};
});