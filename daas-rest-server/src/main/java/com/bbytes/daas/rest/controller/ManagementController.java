package com.bbytes.daas.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.rest.BaasException;


/**
 * Rest service for accessing generic entities from Endure BAAS. These API's will be called by the
 * Android/iOS SDK
 * 
 * @author Dhanush Gopinath
 * 
 * @version 1.0.0
 */
@Controller
@RequestMapping("/management")
public class ManagementController {


	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody
	String login(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws BaasException {
		return null;
	}
	
	/**
	 * Returns all the entities of type entityName
	
	 */
	@RequestMapping(value = "/organizations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public @ResponseBody
	<T> T createOrganization(@RequestParam("name") String organizationName) throws BaasException {
		return null;

	}

	/**
	 * Returns a single entity of type entityName identified by the id
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @param entityName
	 * @param accessToken
	 * @return
	 * @throws BaasException
	 */
	@RequestMapping(value = "/{organizationName}/application", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> T getEntity(@PathVariable String organizationName , @RequestParam("name") String applicationName) throws BaasException {
		return null;
	}

	
}
