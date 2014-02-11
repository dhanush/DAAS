/**
 * A module for making the HTTP requests specifically to DAAS Rest Services
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
		post : function(url, data, callback, dataType, authToken) {
			$.ajax({
				url : url,
				type : 'POST',
				success : function(data) {
					callback(data);
				},
				dataType : dataType,
				beforeSend : function(xhr) {
					if (authToken) {
						xhr.setRequestHeader('Authorization', 'Bearer '
								+ authToken);
					}
				},
				data : data
			});

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
		get : function(url, callback, dataType, authToken) {
			$.ajax({
				url : url,
				type : 'GET',
				success : function(data) {
					callback(data);
				},
				dataType : dataType,
				beforeSend : function(xhr) {
					if (authToken) {
						xhr.setRequestHeader('Authorization', 'Bearer '
								+ authToken);
					}
				}
			});
		},
		/**
		 * Sends a DELETE request to the Url Specified
		 * 
		 * @param url
		 * @param callback
		 * @param dataType
		 * @returns
		 */
		deleteRequest : function(url, callback, dataType, authToken) {
			$.ajax({
				url : url,
				type : 'DELETE',
				success : function(data) {
					callback(data);
				},
				dataType : dataType,
				beforeSend : function(xhr) {
					if (authToken) {
						xhr.setRequestHeader('Authorization', 'Bearer '
								+ authToken);
					}
				}
			});
		},
		/**
		 * Sends a PUT request to the URL Specifed
		 * 
		 * @param url
		 * @param callback
		 * @param dataType
		 * @param authToken
		 * @returns
		 */
		put : function(url, data, callback, dataType, authToken) {
			$.ajax({
				url : url,
				type : 'PUT',
				data : data,
				success : function(data) {
					callback(data);
				},
				dataType : dataType,
				beforeSend : function(xhr) {
					if (authToken) {
						xhr.setRequestHeader('Authorization', 'Bearer '
								+ authToken);
					}
				}
			});
		}

	};
});