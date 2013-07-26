package com.bbytes.daas.rest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.domain.Entity;

/**
 * Rest service for accessing generic entities from Endure BAAS. These API's will be called by the
 * Android/iOS SDK
 * 
 * @author Dhanush Gopinath
 * 
 * @version 1.0.0
 */
@Controller
public class EntityController {

	/**
	 * Returns all the entities of type entityType
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityType
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> List<T> getEntities(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @RequestHeader("Authorization") String accessToken,
			HttpServletRequest request) throws BaasException {
		return null;

	}

	/**
	 * Returns a single entity of type entityType identified by the id
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityType
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}/{entityId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> T getEntity(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityId,
			@RequestHeader("Authorization") String accessToken, HttpServletRequest request) throws BaasException {
		return null;
	}

	/**
	 * Returns all the related objects B of the entity A identified by entityId defined by the
	 * relation R. A->B by R then return B
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityType
	 *            - name of the primary entity of whose related entities are to be found
	 * @param entityId
	 *            - id of the primary entity
	 * @param relation
	 *            - relation defined b/w 2 entities
	 * @param relatedEntityType
	 *            - related entity that we want to return
	 * @param accessToken
	 * @return - List<T> - a list object consisting of objects of relatedEntityName
	 * @throws BaasException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}/{entityId}/{relation}/{relatedEntityType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> List<T> getRelatedEntities(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @RequestHeader("Authorization") String accessToken,
			HttpServletRequest request) throws BaasException {
		return null;
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
	 * @param entityType
	 * @param entityId
	 * @param relation
	 * @param relatedEntityType
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	@RequestMapping(value = "/{organizationName}/{applicationName}/{relatedEntityType}/{relatedEntityId}/connecting/{relation}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> List<T> getConnectingEntities(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String relatedEntityType, @PathVariable String relatedEntityId,
			@PathVariable String relation, @RequestHeader("Authorization") String accessToken,
			HttpServletRequest request) throws BaasException {
		return null;
	}

	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String createEntity(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @RequestHeader("Authorization") String accessToken,
			@RequestBody String entityJson, HttpServletRequest request) throws BaasException {
		// TODO: replace the actual impl
		System.out.println(entityJson);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode entity = mapper.readTree(entityJson);
			// TODO : parse and create entity?
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{'status': 'ok'}";
	}

	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}/{entityUuid}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String updateEntity(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityUuid,
			@RequestHeader("Authorization") String accessToken, @RequestBody String entityJson,
			HttpServletRequest request) throws BaasException {
		return "{'status': 'ok'}";

	}

	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}/{entityUuid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String deleteEntity(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityUuid,
			@RequestHeader("Authorization") String accessToken, HttpServletRequest request) throws BaasException {
		return "{'status': 'ok'}";

	}

	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}/{entityId}/{relation}/{relatedEntityType}/{relatedEntityId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String addConnection(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityId, @PathVariable String relation,
			@PathVariable String relatedEntityType, @PathVariable String relatedEntityId,
			@RequestHeader("Authorization") String accessToken, HttpServletRequest request) throws BaasException {

		return "";
	}


	@RequestMapping(value = "/{organizationName}/{applicationName}/{entityType}/{entityId}/{relation}/{relatedEntityType}/{relatedEntityId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String deleteConnection(@PathVariable String organizationName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityId, @PathVariable String relation,
			@PathVariable String relatedEntityType, @PathVariable String relatedEntityId,
			@RequestHeader("Authorization") String accessToken, HttpServletRequest request) throws BaasException {

		return "";
	}

}
