/*
 * Copyright (C) 2013 The Zorba Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bbytes.daas.template.usergrid;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.bbytes.daas.template.BaasException;
import com.bbytes.daas.template.DealResourceTemplate;
import com.bbytes.daas.template.StoreResourceTemplate;
import com.bbytes.endure.domain.Deal;
import com.bbytes.endure.domain.Photo;
import com.bbytes.endure.domain.usergrid.ApiResponse;
import com.bbytes.endure.domain.usergrid.utils.UsergridResponseToDomainConversionUtils;

/**
 * Implementation of {@link DealResourceTemplate}.
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class UsergridDealResourceTemplateImpl extends AbstractUsergridRestTemplateImpl implements DealResourceTemplate {

	private Logger log = Logger.getLogger(UsergridDealResourceTemplateImpl.class);

	@Override
	public Deal createDeal(String organizationName, String applicationName, String title, String details,
			Date validFrom, Date validTo, String accessToken) throws BaasException {

		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("details", details);
		map.put("validFrom", validFrom);
		map.put("validTo", validTo);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(map, headers);

		Deal deal = null;
		try {
			ApiResponse apiResp = restTemplate.postForObject(completeUrl, request, ApiResponse.class);
			deal = UsergridResponseToDomainConversionUtils.getDeal(apiResp);
		} catch (RestClientException e) {
			throwBaasException(e);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deal;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Deal> getAllDeals(String organizationName, String applicationName, String accessToken)
			throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity request = new HttpEntity<>(headers);
		List<Deal> deals = null;
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
					ApiResponse.class);
			deals = UsergridResponseToDomainConversionUtils.getDeals(apiResp.getBody());
		} catch (RestClientException e) {
			throwBaasException(e);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deals;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Deal getDeal(String organizationName, String applicationName, String dealUuid, String accessToken)
			throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + dealUuid;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity request = new HttpEntity<>(headers);
		Deal deal = null;
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
					ApiResponse.class);
			deal = UsergridResponseToDomainConversionUtils.getDeal(apiResp.getBody());
		} catch (RestClientException e) {
			throwBaasException(e);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deal;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Deal deleteDeal(String organizationName, String applicationName, String dealUUId, String accessToken)
			throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + dealUUId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity request = new HttpEntity<>(headers);

		Deal deal = null;
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.DELETE, request,
					ApiResponse.class);
			deal = UsergridResponseToDomainConversionUtils.getDeal(apiResp.getBody());
		} catch (RestClientException e) {
			throwBaasException(e);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deal;
	}

	@Override
	public Deal editDeal(String organizationName, String applicationName, String dealUUId,
			Map<String, Object> dealInformation, String accessToken) throws BaasException {
		// TODO: Do we need to loop through this deal information and update properly?
		return updateDeal(organizationName, applicationName, dealUUId, accessToken, dealInformation);
	}

	@Override
	public Deal editDealValidity(String organizationName, String applicationName, String dealUUId, Date validFrom,
			Date validTo, String accessToken) throws BaasException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("validFrom", validFrom);
		map.put("validTo", validTo);
		return updateDeal(organizationName, applicationName, dealUUId, accessToken, map);
	}

	@Override
	public Deal addDealInformation(String organizationName, String applicationName, String dealUUId,
			Map<String, Object> dealInformation, String accessToken) throws BaasException {
		Map<String, Object> map = new HashMap<>();
		map.put("custom", dealInformation);
		return updateDeal(organizationName, applicationName, dealUUId, accessToken, map);
	}

	@Override
	public Deal addDealPhotos(String organizationName, String applicationName, String dealUUId, List<Photo> photos,
			String accessToken) throws BaasException {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dealPhotos", photos);

		return updateDeal(organizationName, applicationName, dealUUId, accessToken, map);
	}

	@Override
	public Deal addDealContacts(String organizationName, String applicationName, String dealUUId,
			List<String> contactNumbers, String accessToken) throws BaasException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dealContactNumbers", contactNumbers);
		return updateDeal(organizationName, applicationName, dealUUId, accessToken, map);
	}

	@Override
	public Deal addDealWebsite(String organizationName, String applicationName, String dealUUId, String dealUrl,
			String accessToken) throws BaasException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dealUrl", dealUrl);
		return updateDeal(organizationName, applicationName, dealUUId, accessToken, map);
	}

	/**
	 * Method that finally adds a request to the deal
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param dealUUId
	 * @param accessToken
	 * @param requestMap
	 * @return
	 * @throws BaasException
	 */
	protected Deal updateDeal(String organizationName, String applicationName, String dealUUId, String accessToken,
			Map<String, Object> requestMap) throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + dealUUId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);
		Deal deal = null;
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.PUT, request,
					ApiResponse.class);
			deal = UsergridResponseToDomainConversionUtils.getDeal(apiResp.getBody());
		} catch (RestClientException e) {
			throwBaasException(e);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deal;
	}

	@Override
	public boolean addValidStore(String organizationName, String applicationName, String storeUUID, String dealUUId,
			String accessToken) throws BaasException {
		validateOrganizationAndApplication(organizationName, applicationName);
		validateString(storeUUID);
		validateString(dealUUId);
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + dealUUId
				+ DEAL_STORE_RELATION + StoreResourceTemplate.ENDPOINT + "/" + storeUUID;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.POST, request,
					ApiResponse.class);
			if (apiResp.getStatusCode().value() == HttpStatus.SC_OK) {
				return true;
			}
		} catch (RestClientException e) {
			throwBaasException(e);
		}
		return false;
	}

	@Override
	public boolean removeValidStore(String organizationName, String applicationName, String storeUUID, String dealUUId,
			String accessToken) throws BaasException {
		validateOrganizationAndApplication(organizationName, applicationName);
		validateString(storeUUID);
		validateString(dealUUId);

		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + dealUUId
				+ DEAL_STORE_RELATION + StoreResourceTemplate.ENDPOINT + "/" + storeUUID;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.DELETE, request,
					ApiResponse.class);
			if (apiResp.getStatusCode().value() == HttpStatus.SC_OK) {
				return true;
			}
		} catch (RestClientException e) {
			throwBaasException(e);
		}
		return false;

	}

	@Override
	public List<Deal> getDealsValidAtStore(String organizationName, String applicationName, String storeUuid,
			String accessToken) throws BaasException {
		validateOrganizationAndApplication(organizationName, applicationName);
		validateString(storeUuid);
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName
				+ StoreResourceTemplate.ENDPOINT + "/" + storeUuid + "/connecting"
				+ DealResourceTemplate.DEAL_STORE_RELATION;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(headers);
		List<Deal> stores = null;
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
					ApiResponse.class);
			stores = UsergridResponseToDomainConversionUtils.getDeals(apiResp.getBody());
		} catch (RestClientException e) {
			throwBaasException(e);
		} catch (MalformedURLException e) {
			throwBaasException(new RestClientException(e.getMessage()));
		}
		return stores;
	}
}
