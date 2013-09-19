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

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.Account;
import com.bbytes.daas.rest.domain.Application;
import com.bbytes.daas.rest.domain.DaasUser;
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
	Account createAccount(@PathVariable("accountName") String accountName, @RequestBody Account account) throws BaasPersistentException {
		LOG.debug("Request to create account : " + accountName);
		if(account == null) {
			return managementService.createAccount(accountName, null, null, null);
		}
		else {
			return managementService.createAccount(accountName, account.getAccountType(), account.getAccountSubType(), account.getFullName());
		}
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
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts/{accountName}/applications/{applicationName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Application createApplication(@PathVariable String accountName,
			@PathVariable("applicationName") String applicationName) throws BaasException, BaasPersistentException {
		return managementService.createApplication(accountName, applicationName);
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
