package com.bbytes.daas.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.rest.client.BaasClient;
import com.bbytes.daas.rest.client.BaasClientException;
import com.bbytes.endure.domain.EntityType;

/**
 * Rest service for accessing generic entities from Endure BAAS. These API's will be called by the
 * Android/iOS SDK
 * 
 * @author Dhanush Gopinath
 * 
 * @version 1.0.0
 */
@Controller
public class RestController {

	@Value("#{props['baas.url']}")
	private String baasUrl;

	@Autowired
	private BaasClient baasClient;
	
	private final static String CONTEXT_PATH="/rest";

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody
	String login(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws BaasClientException {
		return baasClient.login(baasUrl, userName, password);
	}

	
	/**
	 * Filters the requestURI if it starts with "/rest"
	 * @param requestURI
	 * @return
	 */
	private String contextURI(String requestURI) {
		if(!requestURI.startsWith(CONTEXT_PATH)) {
			return requestURI;
		}
		return requestURI.substring(CONTEXT_PATH.length());
	}
	
	/**
	 * Returns all the entities of type entityName
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityName
	 * @param accessToken
	 * @return
	 * @throws BaasClientException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
	public @ResponseBody
	<T> List<T> getEntities(@PathVariable String entityName, @RequestHeader("Authorization") String accessToken,
			HttpServletRequest request) throws BaasClientException {
		String completeUrl = baasUrl + contextURI(request.getRequestURI());
		return baasClient.getAll(completeUrl, EntityType.type(entityName), accessToken);

	}

	/**
	 * Returns a single entity of type entityName identified by the id
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityName
	 * @param accessToken
	 * @return
	 * @throws BaasClientException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityName}/{entityId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> T getEntity(@PathVariable String entityName, @PathVariable String entityId,
			@RequestHeader("Authorization") String accessToken, HttpServletRequest request) throws BaasClientException {
		String completeUrl = baasUrl + contextURI(request.getRequestURI());
		return baasClient.get(completeUrl, EntityType.type(entityName), accessToken);
	}

	/**
	 * Returns all the related objects B of the entity A identified by entityId defined by the
	 * relation R. A->B by R then return B
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityName
	 *            - name of the primary entity of whose related entities are to be found
	 * @param entityId
	 *            - id of the primary entity
	 * @param relation
	 *            - relation defined b/w 2 entities
	 * @param relatedEntityName
	 *            - related entity that we want to return
	 * @param accessToken
	 * @return - List<T> - a list object consisting of objects of relatedEntityName
	 * @throws BaasClientException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityName}/{entityId}/{relation}/{relatedEntityName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> List<T> getRelatedEntities(@PathVariable String entityName, @RequestHeader("Authorization") String accessToken,
			HttpServletRequest request) throws BaasClientException {
		String completeUrl = baasUrl + contextURI(request.getRequestURI());
		return baasClient.getAll(completeUrl, EntityType.type(entityName), accessToken);
	}

	/**
	 * This is the reverse of
	 * {@link #getRelatedEntities(String, String, String, String, String, String, String)}
	 * 
	 * Returns the relating (connecting) objects A from the related Entity B defned by relation R.
	 * A->B by R then return A
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityName
	 * @param entityId
	 * @param relation
	 * @param relatedEntityName
	 * @param accessToken
	 * @return
	 * @throws BaasClientException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{relatedEntityName}/{relatedEntityId}/connecting/{relation}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> List<T> getConnectingEntities(@PathVariable String relatedEntityName,
			@RequestHeader("Authorization") String accessToken, HttpServletRequest request) throws BaasClientException {
		String completeUrl = baasUrl + contextURI(request.getRequestURI());
		return baasClient.getAll(completeUrl, EntityType.type(relatedEntityName), accessToken);
	}

	// @RequestMapping(value =
	// "/{organizationName}/{applicationName}/{entityName}/{entityId}/{relatedEntityName}/{relatedEntityId}",
	// method = RequestMethod.GET, produces = "application/json")
	// public @ResponseBody
	// <T> List<T> getRelations(@PathVariable String organizationName, @PathVariable String
	// applicationName,
	// @PathVariable String entityName, @PathVariable String entityId, @PathVariable String
	// relation,
	// @PathVariable String relatedEntityName, @PathVariable String relatedEntityId,
	// @RequestHeader("Authorization") String accessToken) throws BaasClientException {
	// String completeUrl = baasUrl + "/" + organizationName + "/" + applicationName + "/" +
	// relatedEntityName + "/"
	// + relatedEntityId + "/" + relation + "/" + relation;
	// return baasClient.getAll(completeUrl, relatedEntityName, accessToken);
	// }
}
