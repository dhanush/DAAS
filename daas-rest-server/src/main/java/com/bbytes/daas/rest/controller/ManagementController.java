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
import com.bbytes.daas.rest.dao.AccountDao;
import com.bbytes.daas.rest.domain.Application;
import com.bbytes.daas.rest.domain.Account;

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
	@RequestMapping(value = "/accounts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Account createAccount(@RequestParam("name") String accountName) throws BaasException,
			BaasPersistentException {
		Account account = new Account();
		account.setName(accountName);
		return accountDao.save(account);
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
	@RequestMapping(value = "/{accountName}/application", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Application createApplication(@PathVariable String accountName, @RequestParam("name") String applicationName)
			throws BaasException, BaasPersistentException {
		// check if org is available
		if (!accountDao.findAny("name", accountName)) {
			throw new BaasException("Given Account '" + accountName
					+ "' is not available, create one before creating applications under that account ");
		}

		Application app = new Application();
		app.setAccountName(accountName);
		app.setName(applicationName);
		return applicationDao.save(app);
	}
}
