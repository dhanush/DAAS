package com.bbytes.daas.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.dao.AccountDao;
import com.bbytes.daas.rest.dao.ApplicationDao;
import com.bbytes.daas.rest.domain.Account;
import com.bbytes.daas.rest.domain.Application;

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
	private AccountDao accountDao;

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
	 * @param accountName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/account/{accountName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Account createAccount(@PathVariable("accountName") String accountName) throws BaasException, BaasPersistentException {
		Account account = new Account();
		account.setName(accountName);
		return accountDao.save(account);
	}

	/**
	 * Get all Accounts
	 * 
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<Account> getAccounts() throws BaasException, BaasPersistentException {

		try {
			return accountDao.findAll();
		} catch (BaasEntityNotFoundException e) {
			return new ArrayList<Account>();
		}
	}

	/**
	 * Create App inside org
	 * 
	 * @param accountName
	 * @param applicationName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/{accountName}/application/{applicationName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Application createApplication(@PathVariable String accountName, @PathVariable("applicationName") String applicationName)
			throws BaasException, BaasPersistentException {
		Application app = new Application();
		app.setAccountName(accountName);
		app.setName(applicationName);
		return applicationDao.save(app);
	}

	/**
	 * Create App inside org
	 * 
	 * @param accountName
	 * @param applicationName
	 * @return
	 * @throws BaasException
	 * @throws BaasPersistentException
	 */
	@RequestMapping(value = "/{accountName}/applications", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	List<Application> getApplications(@PathVariable String accountName) throws BaasException, BaasPersistentException {
		try {
			// we dont have to filter based on account name as we have separate  database per account
			return applicationDao.findAll();
		} catch (BaasEntityNotFoundException e) {
			return new ArrayList<Application>();
		}
	}
}
