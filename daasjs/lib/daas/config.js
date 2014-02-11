define(function() {
	var baseUrl = "http://localhost:8080/daas-rest-server/";

	return {
		CONTENT_TYPE_JSON: "application/json; charset=utf-8",
		CONTENT_TYPE_FORM_ENCODED: "application/x-www-form-urlencoded; charset=UTF-8",
		
		getBaseUrl : function() {
			return baseUrl;
		}
	};
});