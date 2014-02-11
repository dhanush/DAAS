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
		 * @param dataType
		 * @param contentType
		 * @param authToken
		 * @returns
		 */
		post : function(url, data, callback, dataType,contentType, authToken) {
			//stringiyf the data to json
			if(dataType =="json") {
				data = JSON.stringify(data);
			}
			
			$.ajax({
				url : url,
				type : 'POST',
				success : function(data) {
					callback(data);
				},
				contentType: contentType,
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
		put : function(url, data, callback, dataType, contentType, authToken) {
			//stringiyf the data to json
			if(dataType =="json") {
				data = JSON.stringify(data);
			}
			$.ajax({
				url : url,
				type : 'PUT',
				data : data,
				contentType : contentType,
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