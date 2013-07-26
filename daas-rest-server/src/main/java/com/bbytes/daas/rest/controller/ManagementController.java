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
 * Management Rest service to create Apps and Organizations
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
	 * Create org
	 * @param organizationName
	 * @return
	 * @throws BaasException
	 */
	@RequestMapping(value = "/organizations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public @ResponseBody
	<T> T createOrganization(@RequestParam("name") String organizationName) throws BaasException {
		return null;
//		TODO: return type should of class type organization 
	}

	/**
	 * Create App inside org
	 * @param organizationName
	 * @param applicationName
	 * @return
	 * @throws BaasException
	 */
	@RequestMapping(value = "/{organizationName}/application", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	<T> T createApplication(@PathVariable String organizationName , @RequestParam("name") String applicationName) throws BaasException {
		return null;
//		TODO: return type should of class type application  
	}

	
}
