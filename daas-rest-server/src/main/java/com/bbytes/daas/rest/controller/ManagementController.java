package com.bbytes.daas.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.dao.ApplicationDao;
import com.bbytes.daas.rest.dao.OrganizationDao;
import com.bbytes.daas.rest.domain.Application;
import com.bbytes.daas.rest.domain.Organization;

/**
 * Management Rest service to create Apps and Organizations
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Controller
@RequestMapping("/management")
public class ManagementController {

	@Autowired
	private OrganizationDao organizationDao;

	@Autowired
	private ApplicationDao applicationDao;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody
	String login(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws BaasException {
		return null;
	}

	/**
	 * Create org
	 * 
	 * @param organizationName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/organizations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Organization createOrganization(@RequestParam("name") String organizationName) throws BaasException,
			BaasPersistentException {
		Organization organization = new Organization();
		organization.setName(organizationName);
		return organizationDao.save(organization);
	}

	/**
	 * Create App inside org
	 * 
	 * @param organizationName
	 * @param applicationName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException 
	 */
	@RequestMapping(value = "/{organizationName}/application", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Application createApplication(@PathVariable String organizationName, @RequestParam("name") String applicationName)
			throws BaasException, BaasPersistentException {
		// check if org is available
		if (!organizationDao.findAny("name", organizationName)) {
			throw new BaasException("Given Organization '" + organizationName
					+ "' is not available, create one before creating applications under that organization ");
		}

		Application app = new Application();
		app.setOrganizationName(organizationName);
		app.setName(applicationName);
		return applicationDao.save(app);
	}
}
