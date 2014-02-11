package com.bbytes.daas.service;

import com.bbytes.daas.domain.DaasUser;


/**
 * Security Service interface
 *
 * @author Dhanush Gopinath
 *
 * @version 1.0.0
 */
public interface SecurityService {

	/**
	 * Returns the logged in {@link DaasUser}
	 * 
	 * @return
	 */
	public DaasUser getLoggedInUser() throws Exception;
}
