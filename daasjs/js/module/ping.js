define([ "module/http" ], function(http) {
	return {
		ping : function(url, callback) {
			http.get(url, callback, "text");
		}
	};
});