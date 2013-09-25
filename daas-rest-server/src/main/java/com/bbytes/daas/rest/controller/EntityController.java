package com.bbytes.daas.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.dao.DocumentDao;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;

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

	@Autowired
	private DocumentDao documentDao;

	/**
	 * Returns all the entities of type entityType
	 * 
	 * @param accountName
	 * @param applicationName
	 * @param entityType
	 * @param accessToken
	 * @returnmm
	 * @throws BaasException
	 * @throws BaasPersistentException
	 * @throws BaasEntityNotFoundException
	 */
	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String getEntities(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, String pName, String pValue, HttpServletRequest request)
			throws BaasException, BaasPersistentException, BaasEntityNotFoundException {
		List<ODocument> documents = null;
		if (pName != null && pValue != null) {
			documents = documentDao.findByProperty(applicationName, entityType, pName, pValue);
		} else {
			documents = documentDao.list(entityType, applicationName);
		}

		return OJSONWriter.listToJSON(documents, null);

	}

	/**
	 * Returns a single entity of type entityType identified by the id , the id is the uuid property
	 * that is considered to query
	 * 
	 * @param accountName
	 * @param applicationName
	 * @param entityType
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 * @throws BaasEntityNotFoundException
	 */
	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}/{entityUuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String getEntity(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityUuid, HttpServletRequest request)
			throws BaasException, BaasEntityNotFoundException {
		ODocument document = documentDao.findById(entityType, entityUuid);
		return document.toJSON();
	}

	/**
	 * Returns all the related objects B of the entity A identified by entityId defined by the
	 * relation R. A->B by R then return B
	 * 
	 * @param accountName
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
	 * @throws BaasEntityNotFoundException
	 */
	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}/{entityId}/{relation}/{relatedEntityType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String getRelatedEntities(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityId, @PathVariable String relation,
			@PathVariable String relatedEntityType, HttpServletRequest request) throws BaasException,
			BaasEntityNotFoundException {
		List<ODocument> documents = documentDao.findRelated(entityType, entityId, relatedEntityType, relation);
		return OJSONWriter.listToJSON(documents, null);
	}

	/**
	 * This is the reverse of
	 * {@link #getRelatedEntities(String, String, String, String, String, String, String)}
	 * 
	 * Returns the relating (connecting) objects A from the related Entity B defned by relation R.
	 * A->B by R then return A
	 * 
	 * @param accountName
	 * @param applicationName
	 * @param entityType
	 * @param entityId
	 * @param relation
	 * @param relatedEntityType
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 * @throws BaasEntityNotFoundException
	 */
	@RequestMapping(value = "/{accountName}/{applicationName}/{relatedEntityType}/{relatedEntityId}/connecting/{relation}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String getConnectingEntities(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String relatedEntityType, @PathVariable String relatedEntityId,
			@PathVariable String relation, HttpServletRequest request) throws BaasException,
			BaasEntityNotFoundException {
		List<ODocument> documents = documentDao.findRelatedReverse(relatedEntityType, relatedEntityId, relation);
		return OJSONWriter.listToJSON(documents, null);
	}

	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String createEntity(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @RequestBody String entityJson, HttpServletRequest request)
			throws BaasException, BaasPersistentException {
		// create the document with the property sent in request body
		ODocument document = documentDao.create(entityType, entityJson, accountName, applicationName);
		return document.toJSON();
	}

	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}/{entityUuid}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String updateEntity(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityUuid, @RequestBody String entityJson,
			HttpServletRequest request) throws BaasException, BaasPersistentException {
		ODocument document = documentDao.update(entityUuid, entityType, entityJson, accountName, applicationName);
		return document.toJSON();
	}

	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}/{entityUuid}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody
	String deleteEntity(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityUuid, HttpServletRequest request)
			throws BaasException, BaasPersistentException {
		documentDao.remove(entityUuid, entityType, accountName, applicationName);
		return "success";

	}

	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}/{entityId}/{relation}/{relatedEntityType}/{relatedEntityId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String addConnection(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityId, @PathVariable String relation,
			@PathVariable String relatedEntityType, @PathVariable String relatedEntityId, HttpServletRequest request)
			throws BaasException, BaasPersistentException {
		ODocument document = documentDao.relate(entityType, entityId, relatedEntityType, relatedEntityId, relation);
		return document.toJSON();
	}

	@RequestMapping(value = "/{accountName}/{applicationName}/{entityType}/{entityId}/{relation}/{relatedEntityType}/{relatedEntityId}", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody
	String deleteConnection(@PathVariable String accountName, @PathVariable String applicationName,
			@PathVariable String entityType, @PathVariable String entityId, @PathVariable String relation,
			@PathVariable String relatedEntityType, @PathVariable String relatedEntityId, HttpServletRequest request)
			throws BaasException, BaasPersistentException {
		documentDao.removeRelation(entityType, entityId, relatedEntityType, relatedEntityId, relation);
		return "success";
	}

}
