/**
 * 
 */
package com.bbytes.daas.rest.client;

/**
 * Any exception related to Baas calls will be wrapped in this
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public class BaasClientException extends Exception {

	private static final long serialVersionUID = 6289917997937599218L;

	public BaasClientException() {
		super();
	}

	public BaasClientException(String jsonResponse, String message, Throwable cause) {
		super(message, cause);
	}

	public BaasClientException(String message) {
		super(message);
	}

	public BaasClientException(Throwable cause) {
		super(cause);
	}

}
