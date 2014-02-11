/**
 * A generic module for making the HTTP requests
 */
define([ "jquery" ], function($) {

	return {
		/**
		 * Sends a POST request to the URL specified
		 * 
		 * @param url
		 * @param data
		 * @param callback
		 * @param contentType
		 * @returns
		 */
		post :	function(url, data, callback, dataType) {
			$.post(url, data, function(data) {
				callback(data);
			}, dataType);
		},

		/**
		 * Sends a GET request to the url specified
		 * 
		 * @param url
		 * @param data
		 * @param callback
		 * @param contentType
		 * @returns
		 */
		get :function(url, callback, dataType) {
			$.get(url, function(data) {
				callback(data);
			}, dataType);
		},
		/**
		 * Sends a DELETE request to the Url Specified
		 * 
		 * @param url
		 * @param callback
		 * @param dataType
		 * @returns
		 */
		deleteRequest : function(url, callback, dataType) {
			$.ajax({
			    url: url,
			    type: 'DELETE',
			    success: function(data) {
			       callback(data);
			    }
			});
		}
		
	};
});