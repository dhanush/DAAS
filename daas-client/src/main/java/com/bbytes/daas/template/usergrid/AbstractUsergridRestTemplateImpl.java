/**
 * 
 */
package com.bbytes.daas.template.usergrid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bbytes.daas.template.BaasException;

/**
 * Abstract level implementation of {@link UsergridRestTemplate}
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public abstract class AbstractUsergridRestTemplateImpl implements UsergridRestTemplate {
	
	private Logger log = Logger.getLogger(AbstractUsergridRestTemplateImpl.class);
	
	@Autowired
	protected RestTemplate restTemplate;

	private String baasUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.endure.rest.usergrid.UsergridRestTemplate#getSpringRestTemplate()
	 */
	@Override
	public RestTemplate getSpringRestTemplate() {
		return restTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.endure.rest.usergrid.UsergridRestTemplate#getUsergridRestUrl()
	 */
	@Override
	public String getBaasUrl(){
		return baasUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.endure.rest.usergrid.UsergridRestTemplate#setUsergridRestUrl()
	 */
	@Override
	public void setBaasUrl(String usergridRestUrl) {
		this.baasUrl = usergridRestUrl;

	}

	/**
	 * Throws the custom exception after capturing the {@link RestClientException}
	 * 
	 * @param e
	 * @throws UsergridAccessException
	 */
	protected void throwBaasException(RestClientException e) throws BaasException {
		HttpClientErrorException he = (HttpClientErrorException) e;
		String jsonResponse = he.getResponseBodyAsString();
		log.error(e.getLocalizedMessage() + " :: " + jsonResponse, e);
		throw new BaasException(jsonResponse, e.getMessage(), e);
	}
	
	/**
	 * Validates the organization name and application name
	 * @param orgName
	 * @param appName
	 * @throws UsergridAccessException
	 */
	protected void validateOrganizationAndApplication(String orgName, String appName) throws BaasException {
		if(!validateString(orgName)) {
			throwBaasException(new RestClientException("Organization is Null or Empty"));
		}
		if(!validateString(appName)) {
			throwBaasException(new RestClientException("Application is Null or Empty"));
		}
	}

	/**
	 * Validates a string
	 * @param string
	 * @return
	 */
	protected boolean validateString(String string) {
		if(string == null || string.isEmpty()){
			return false;
		}
		return true;
	}
}
