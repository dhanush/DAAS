/**
 * A generic module for making the HTTP requests
 */
define([ "jquery" ], function($) {

	return {
		post :
		/**
		 * Sends a POST request to the URL specified
		 * 
		 * @param url
		 * @param data
		 * @param callback
		 * @param contentType
		 * @returns
		 */
		function(url, data, callback, dataType) {
			$.post(url, data, function(data) {
				callback(data);
			}, dataType);
		},

		get :
		/**
		 * Sends a GET request to the url specified
		 * 
		 * @param url
		 * @param data
		 * @param callback
		 * @param contentType
		 * @returns
		 */
		function(url, callback, dataType) {
			$.get(url, function(data) {
				callback(data);
			}, dataType);
		}
	};
});