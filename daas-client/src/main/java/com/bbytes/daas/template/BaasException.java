/**
 * 
 */
package com.bbytes.daas.template;

/**
 * Any exception related to Baas calls will be wrapped in this
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public class BaasException extends Exception {

	/**
	 * variable for holding the json response for an exception from usergrid
	 * 
	 */
	private String jsonResponse;
	
	private static final long serialVersionUID = 6289917997937599218L;

	public BaasException() {
		super();
	}


	public BaasException(String jsonResponse, String message, Throwable cause) {
		super(message, cause);
	}

	public BaasException(String jsonResponse,String message) {
		super(message);
	}

	public BaasException(String jsonResponse,Throwable cause) {
		super(cause);
	}


	public String getJsonResponse() {
		return jsonResponse;
	}


	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
}
