package com.bbytes.daas.rest.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.domain.Account;
import com.bbytes.daas.domain.Application;
import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.service.ManagementService;
import com.bbytes.daas.service.UserService;

/**
 * Management Rest service to create Apps and Account
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Controller
@RequestMapping("/management")
public class ManagementController {

	private static final Logger LOG = Logger.getLogger(ManagementController.class);

	@Autowired
	private ManagementService managementService;
	
	@Autowired
	private UserService userService;



	/**
	 * Create account
	 * 
	 * @param accountName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts/{accountName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Account createAccount(@PathVariable("accountName") String accountName) throws BaasPersistentException {
		LOG.debug("Request to create account : " + accountName);
		return managementService.createAccount(accountName);
	}
	
	/**
	 * Get account
	 * 
	 * @param accountName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 * @throws BaasEntityNotFoundException 
	 */
	@RequestMapping(value = "/accounts/{accountName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Account getAccount(@PathVariable("accountName") String accountName) throws BaasPersistentException, BaasEntityNotFoundException {
		return managementService.getAccount(accountName);
	}
	
	/**
	 * Delete account
	 * 
	 * @param accountName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 * @throws BaasEntityNotFoundException 
	 */
	@RequestMapping(value = "/accounts/{accountName}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	boolean deleteAccount(@PathVariable("accountName") String accountName) throws BaasPersistentException, BaasEntityNotFoundException {
		return managementService.deleteAccount(accountName);
	}

	/**
	 * Create account user
	 * 
	 * @param accountName
	 * @param user
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts/{accountName}/user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	DaasUser createAccountUser(@PathVariable("accountName") String accountName, @RequestBody DaasUser user)
			throws BaasException, BaasPersistentException {
		return userService.createAccountUser(accountName, user);
	}
	
	@RequestMapping(value = "/accounts/{accountName}/user/{role}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<DaasUser> getAccountUsers(@PathVariable("accountName") String accountName, @PathVariable("role") String role)
			throws BaasException, BaasPersistentException {
		return userService.getAccountUsers(accountName, role);
	}

	/**
	 * Get all Accounts
	 * 
	 * @return
	 * @throws BaasEntityNotFoundException
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<Account> getAccounts() throws BaasPersistentException, BaasEntityNotFoundException {
		LOG.debug("Request to get all accounts");
		return managementService.getAllAccounts();
	}

	/**
	 * Create App inside account
	 * 
	 * @param accountName
	 * @param applicationName
	 * @param application TODO
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts/{accountName}/applications/{applicationName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Application createApplication(@PathVariable String accountName,
			@PathVariable("applicationName") String applicationName, @RequestBody Application application)
			throws BaasException, BaasPersistentException {
		if (application == null) {
			return managementService.createApplication(accountName, applicationName, null, null, null);
		} else {
			return managementService.createApplication(accountName, applicationName, application.getApplicationType(),
					application.getApplicationSubType(), application.getFullName());
		}
	}
	
	/**
	 * Get App 
	 * 
	 * @param accountName
	 * @param applicationName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 * @throws BaasEntityNotFoundException 
	 */
	@RequestMapping(value = "/accounts/{accountName}/applications/{applicationName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Application getApplication(@PathVariable String accountName,
			@PathVariable("applicationName") String applicationName) throws BaasPersistentException, BaasEntityNotFoundException {
		return managementService.getApplication(accountName, applicationName);
	}
	
	/**
	 * Delete App inside account
	 * 
	 * @param accountName
	 * @param applicationName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 * @throws BaasEntityNotFoundException 
	 */
	@RequestMapping(value = "/accounts/{accountName}/applications/{applicationName}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	boolean deleteApplication(@PathVariable String accountName,
			@PathVariable("applicationName") String applicationName) throws BaasPersistentException, BaasEntityNotFoundException {
		return managementService.deleteApplication(accountName, applicationName);
	}

	/**
	 * Create account user
	 * 
	 * @param accountName
	 * @param user
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts/{accountName}/applications/{applicationName}/user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	DaasUser createAppUser(@PathVariable("accountName") String accountName,
			@PathVariable("applicationName") String applicationName, @RequestBody DaasUser user) throws BaasException,
			BaasPersistentException {
		return userService.createApplicationUser(accountName, applicationName, user);
	}

	/**
	 *  get all Apps inside account
	 *  
	 * @param accountName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 * @throws BaasEntityNotFoundException 
	 */
	@RequestMapping(value = "/accounts/{accountName}/applications", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<Application> getApplications(@PathVariable String accountName) throws BaasPersistentException, BaasEntityNotFoundException {
		return managementService.getAllApplications(accountName);
	}
}
