/**
 * 
 */
package com.bbytes.daas.template.usergrid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.bbytes.daas.template.BaasException;
import com.bbytes.daas.template.DealResourceTemplate;
import com.bbytes.daas.template.StoreResourceTemplate;
import com.bbytes.endure.domain.GeoLocation;
import com.bbytes.endure.domain.Store;
import com.bbytes.endure.domain.StoreInfo;
import com.bbytes.endure.domain.usergrid.ApiResponse;
import com.bbytes.endure.domain.usergrid.utils.UsergridResponseToDomainConversionUtils;

/**
 * Rest Client to deal with all {@link Store} related operations
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 * 
 */
public class UsergridStoreResourceTemplateImpl extends AbstractUsergridRestTemplateImpl implements StoreResourceTemplate {

	private Logger log = Logger.getLogger(UsergridStoreResourceTemplateImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.endure.rest.usergrid.StoreResourceTemplate#createStore(java.lang.String,
	 * java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public Store createStore(String organizationName, String applicationName, String accessToken, String locality,
			Map<String, Object> properties) throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("locality", locality);
		map.put("custom", properties);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(map, headers);

		Store store = null;
		try {
			ApiResponse apiResp = restTemplate.postForObject(completeUrl, request, ApiResponse.class);
			store = UsergridResponseToDomainConversionUtils.getStore(apiResp);
		} catch (RestClientException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.endure.rest.usergrid.StoreResourceTemplate#getStores(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<Store> getStores(String organizationName, String applicationName, String accessToken)
			throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity request = new HttpEntity<>(headers);

		ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
				ApiResponse.class);
		List<Store> stores = UsergridResponseToDomainConversionUtils.getStores(apiResp.getBody());
		return stores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.endure.rest.usergrid.StoreResourceTemplate#getStoresByLocation(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<Store> getStoresByLocality(String organizationName, String applicationName, String location,
			String accessToken) throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT
				+ QUERY_LANGUAGE_LOCATION + location + "\'";
		;
		// String queryParameter="?"+QUERY_LANGUAGE_LOCATION + location + "\'";
		// try {
		// completeUrl = completeUrl + URLEncoder.encode(queryParameter,"ASCII");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// try {
		// URI uri = new URI(completeUrl);
		// completeUrl = uri.toASCIIString();
		// } catch (URISyntaxException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity request = new HttpEntity<>(headers);

		// Map<String,String> queryParams = new HashMap<>();
		// queryParams.put("ql", queryParameter);

		ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
				ApiResponse.class);
		List<Store> stores = UsergridResponseToDomainConversionUtils.getStores(apiResp.getBody());
		return stores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.endure.rest.usergrid.StoreResourceTemplate#getStoresFromGeoLocation(java.lang.
	 * String, java.lang.String, com.bbytes.endure.domain.GeoLocation)
	 */
	@Override
	public List<Store> getStoresFromGeoLocation(String organizationName, String applicationName, GeoLocation geoLocation)
			throws BaasException {

		// ql=location%20within%2016093%20of%2037.776753%2C%20-122.407846&_=1337570474469

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Store createStore(String organizationName, String applicationName, String accessToken, String locality,
			double latitude, double longitude, Map<String, Object> properties) throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("locality", locality);
		map.put("custom", properties);

		Map<String, Object> geoLocation = new HashMap<String, Object>();
		geoLocation.put("latitude", latitude);
		geoLocation.put("longitude", longitude);

		map.put("location", geoLocation);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(map, headers);

		Store store = null;
		try {
			ApiResponse apiResp = restTemplate.postForObject(completeUrl, request, ApiResponse.class);
			store = UsergridResponseToDomainConversionUtils.getStore(apiResp);
		} catch (RestClientException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return store;
	}

	@Override
	public Store addSocialMedia(String organizationName, String applicationName, String storeUUID, String fbUrl,
			String twitterUrl, String gPlusUrl, String accessToken) throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + storeUUID;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fbUrl", fbUrl);
		map.put("twitterUrl", twitterUrl);
		map.put("gPlusUrl", gPlusUrl);

		Map<String, Object> socialMedia = new HashMap<String, Object>();
		socialMedia.put("socialMedia", map);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(socialMedia, headers);

		ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.PUT, request,
				ApiResponse.class);
		Store store = UsergridResponseToDomainConversionUtils.getStore(apiResp.getBody());
		return store;
	}

	@Override
	public Store addGeoLocation(String organizationName, String applicationName, String storeUUID,
			GeoLocation location, String accessToken) throws BaasException {

		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + storeUUID;
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> geoLocation = new HashMap<String, Object>();
		geoLocation.put("latitude", location.getLatitude());
		geoLocation.put("longitude", location.getLongitude());
		map.put("location", geoLocation);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
		ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.PUT, request,
				ApiResponse.class);
		Store store = UsergridResponseToDomainConversionUtils.getStore(apiResp.getBody());
		return store;
	}

	@Override
	public Store addMapLocationUrl(String organizationName, String applicationName, String storeUUID,
			String mapLocatorUrl, String accessToken) throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + storeUUID;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mapLocatorUrl", mapLocatorUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
		ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.PUT, request,
				ApiResponse.class);
		Store store = UsergridResponseToDomainConversionUtils.getStore(apiResp.getBody());
		return store;
	}

	@Override
	public Store addStoreInfo(String organizationName, String applicationName, String storeUUID, String accessToken,
			StoreInfo storeInfo) throws BaasException {
		if (storeInfo == null) {
			return getStore(organizationName, applicationName, storeUUID, accessToken);
		}
		// TODO:
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Store getStore(String organizationName, String applicationName, String storeUUID, String accessToken)
			throws BaasException {
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + ENDPOINT + "/" + storeUUID;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity request = new HttpEntity<>(headers);
		ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
				ApiResponse.class);
		Store store = UsergridResponseToDomainConversionUtils.getStore(apiResp.getBody());
		return store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbytes.endure.baas.template.StoreResourceTemplate#getStoresWhereDealIsValid(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Store> getStoresWhereDealIsValid(String organizationName, String applicationName, String dealUUId,
			String accessToken) throws BaasException {
		validateOrganizationAndApplication(organizationName, applicationName);
		validateString(dealUUId);
		String completeUrl = getBaasUrl() + "/" + organizationName + "/" + applicationName + DealResourceTemplate.ENDPOINT+ "/" + dealUUId
				+ DealResourceTemplate.DEAL_STORE_RELATION + ENDPOINT;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(headers);
		List<Store> stores = null;
		try {
			ResponseEntity<ApiResponse> apiResp = restTemplate.exchange(completeUrl, HttpMethod.GET, request,
					ApiResponse.class);
			stores = UsergridResponseToDomainConversionUtils.getStores(apiResp.getBody());
		} catch (RestClientException e) {
			throwBaasException(e);
		}
		return stores;
	}

}
