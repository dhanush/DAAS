package com.bbytes.daas.rest.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bbytes.daas.rest.RestController;
import com.bbytes.endure.domain.usergrid.ApiResponse;
import com.bbytes.endure.domain.usergrid.utils.UsergridResponseToDomainConversionUtils;

/**
 * 
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class UsergridBaasClient implements BaasClient {

	@Autowired
	private RestTemplate restTemplate;

	private Logger log = Logger.getLogger(RestController.class);
	
	public UsergridBaasClient(){
		log.debug("rest loaded");
	}

	@Override
	public <T> List<T> getAll(String url, String entityType, String accessToken) throws BaasClientException {
		ResponseEntity<ApiResponse> apiResp = get(url, accessToken);
		log.debug("JSON Response:: " + apiResp.getBody().toString());
		try {
			return UsergridResponseToDomainConversionUtils.getEntities(apiResp.getBody(), entityType);
		} catch (ClassNotFoundException | IOException e) {
			log.error(e.getMessage(), e);
			throw new BaasClientException(e);
		}
	}

	@Override
	public <T> T get(String url, String entityType, String accessToken) throws BaasClientException {
		ResponseEntity<ApiResponse> apiResp = get(url, accessToken);
		try {
			return UsergridResponseToDomainConversionUtils.getEntity(apiResp.getBody(), entityType);
		} catch (ClassNotFoundException | IOException e) {
			log.error(e.getMessage(), e);
			throw new BaasClientException(e);
		}
	}

	/**
	 * Returns the {@link ResponseEntity} object
	 * 
	 * @param url
	 * @param accessToken
	 * @return
	 * @throws BaasClientException
	 */
	@SuppressWarnings("rawtypes")
	protected ResponseEntity<ApiResponse> get(String url, String accessToken) throws BaasClientException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);
		HttpEntity request = new HttpEntity<>(headers);
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate
					.exchange(url, HttpMethod.GET, request, ApiResponse.class);
			return apiResp;
		} catch (RestClientException e) {
			throw new BaasClientException(e);
		}
	}

	@Override
	public String login(String url, String userName, String password) throws BaasClientException {

		String completeUrl = url + "/management/token";

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("password", password);
		request.put("grant_type", "password");
		request.put("username", userName);
		ApiResponse apiResp = null;
		try {
			apiResp = restTemplate.postForObject(completeUrl, request, ApiResponse.class);
			if (apiResp == null)
				return null;
		} catch (RestClientException e) {
			throw new BaasClientException(e);
		}
		return (String) apiResp.getProperties().get("access_token");
	}

}
