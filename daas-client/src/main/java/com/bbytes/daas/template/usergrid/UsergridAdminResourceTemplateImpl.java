/**
 * 
 */
package com.bbytes.daas.template.usergrid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import com.bbytes.daas.template.AdminResourceTemplate;
import com.bbytes.daas.template.BaasException;
import com.bbytes.endure.domain.usergrid.ApiResponse;
import com.bbytes.endure.domain.usergrid.ApplicationInfo;
import com.bbytes.endure.domain.usergrid.ClientCredentialsInfo;
import com.bbytes.endure.domain.usergrid.OrganizationOwnerInfo;
import com.bbytes.endure.domain.usergrid.User;
import com.bbytes.endure.domain.usergrid.utils.UsergridResponseToDomainConversionUtils;

/**
 * Default implementation for {@link AdminResourceTemplate} . Implements all the
 * admin related rest calls.
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public class UsergridAdminResourceTemplateImpl extends AbstractUsergridRestTemplateImpl
		implements AdminResourceTemplate {

	@SuppressWarnings("unchecked")
	@Override
	public User login(String username, String password,
			String organizationName, String applicationName)
			throws BaasException {

		String completeUrl = getBaasUrl() + "/" + organizationName
				+ "/" + applicationName + USERS_ENDPOINT + LOGIN_ENDPOINT;

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("username", username);
		map.add("password", password);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map,headers);
		
		User user = null;
		try {
			MultiValueMap<String, Object> apiResp = restTemplate.postForObject(completeUrl,
					request, MultiValueMap.class);
//			 user = DomainConversionUtils.getUser(apiResp);
			System.out.println(apiResp);
		} catch (RestClientException e) {
			throwBaasException(e);
		}
		return user;
	}

	@Override
	public OrganizationOwnerInfo createNewOrganization(String organizationName,
			String username, String name, String email, String password)
			throws BaasException {
		String completeUrl = getBaasUrl() + MANAGEMENT_ENDPOINT
				+ ORGANIZATIONS_ENDPOINT;

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("password", password);
		request.put("email", email);
		request.put("name", name);
		request.put("username", username);
		request.put("organization", organizationName);
		OrganizationOwnerInfo org = null;
		try {
			ApiResponse apiResp = restTemplate.postForObject(completeUrl,
					request, ApiResponse.class);
			org = UsergridResponseToDomainConversionUtils.getOrganizationOwnerInfo(apiResp);
		} catch (RestClientException e) {
			throwBaasException(e);
		}
		return org;
	}


	@Override
	public ApplicationInfo createNewApplication(String organizationName,
			String applicationName, String accessToken) throws BaasException {
		String completeUrl = getBaasUrl() + MANAGEMENT_ENDPOINT
				+ ORGANIZATIONS_ENDPOINT + "/" + organizationName
				+ APPLICATIONS_ENDPOINT;
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", applicationName);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(map,headers);
		ApiResponse apiResp = null;
		ApplicationInfo app = null;
		try {
			apiResp = restTemplate.postForObject(completeUrl,
					request, ApiResponse.class);
			app = UsergridResponseToDomainConversionUtils.getApplicationInfo(apiResp);
		} catch (RestClientException e) {
			throwBaasException(e);
		}
		return app;
	}

	@Override
	public String getOrganizationLevelAccessToken(String username,
			String password, String grantType, ClientCredentialsInfo credentials) throws BaasException {
		String completeUrl = getBaasUrl() + MANAGEMENT_ENDPOINT
				+ TOKEN_ENDPOINT;
		return getAccessToken(username, password, grantType, credentials,
				completeUrl);
	}

	@Override
	public String getApplicationLevelAccessToken(String username,
			String password, String grantType, ClientCredentialsInfo credentials, String organizationName, String applicationName) throws BaasException {
		String completeUrl = getBaasUrl() +"/"+organizationName+"/"+applicationName	+ TOKEN_ENDPOINT;
		return getAccessToken(username, password, grantType, credentials,
				completeUrl);
	}

	/**
	 * @param username
	 * @param password
	 * @param grantType
	 * @param credentials
	 * @param completeUrl
	 * @return
	 * @throws BaasException 
	 */
	private String getAccessToken(String username, String password,
			String grantType, ClientCredentialsInfo credentials,
			String completeUrl) throws BaasException {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("password", password);
		request.put("grant_type", grantType);
		request.put("username", username);
		if ("client_credentials".equals(grantType) && credentials != null) {
			request.put("client_id", credentials.getId());
			request.put("client_secret", credentials.getSecret());
		}
		ApiResponse apiResp = null;
		try {
			apiResp = restTemplate.postForObject(completeUrl,
					request, ApiResponse.class);
			if (apiResp == null)
				return null;
		} catch (RestClientException e) {
			throwBaasException(e);
		}

		return (String) apiResp.getProperties().get("access_token");
	}

	@Override
	public ClientCredentialsInfo generateOrganizationCredentials(
			String organizationName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientCredentialsInfo getOrganizationCredentials(
			String organizationName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientCredentialsInfo generateApplicationCredentials(
			String organizationName, String applicationName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientCredentialsInfo getApplicationCredentials(
			String organizationName, String applicationName) {
		// TODO Auto-generated method stub
		return null;
	}

}
