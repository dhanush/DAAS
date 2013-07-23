/**
 * 
 */
package com.bbytes.daas.template;

import org.springframework.web.client.RestTemplate;

/**
 * A base interface to connect to user grid rest services
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public interface BaasRestTemplate {
	
	/**
	 * Returns the Spring {@link RestTemplate}
	 * @return
	 */
	public RestTemplate getSpringRestTemplate();
	
	/**
	 * Returns the base url of the BAAS application
	 * @return
	 */
	public String getBaasUrl();
	
	/**
	 * Sets the base url of the BAAS application
	 * 
	 * @param baasUrl
	 */
	public void setBaasUrl(String baasUrl);
}
