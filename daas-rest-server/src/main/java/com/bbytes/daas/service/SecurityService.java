package com.bbytes.daas.service;

import com.bbytes.daas.rest.domain.DaasUser;


/**
 * 
 *
 * @author Dhanush Gopinath
 *
 * @version 
 */
public interface SecurityService {

	/**
	 * Returns the logged in {@link DaasUser}
	 * 
	 * @return
	 */
	public DaasUser getLoggedInUser() throws Exception;
}
