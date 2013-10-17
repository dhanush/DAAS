/*
 * Copyright (C) 2013 The Daas Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bbytes.daas.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.bbytes.daas.domain.Account;
import com.bbytes.daas.domain.Application;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public interface ManagementService {

	@PreAuthorize("hasRole('ROLE_TENENT_ADMIN')")
	public List<Account> getAllAccounts() throws BaasPersistentException, BaasEntityNotFoundException;
	
	@PreAuthorize("hasRole('ROLE_TENENT_ADMIN')")
	public long getAccountCount() throws BaasPersistentException;
	
	@PreAuthorize("hasRole('ROLE_TENENT_ADMIN')")
	public Account getAccount(String accountName) throws BaasPersistentException, BaasEntityNotFoundException;

	@PreAuthorize("hasRole('ROLE_TENENT_ADMIN')")
	public Account createAccount(String accountName) throws BaasPersistentException;
	
	@PreAuthorize("hasRole('ROLE_TENENT_ADMIN')")
	public boolean deleteAccount(String accountName) throws BaasPersistentException,BaasEntityNotFoundException;

	@PreAuthorize("hasAnyRole('ROLE_TENENT_ADMIN','ROLE_ACCOUNT_ADMIN')")
	public List<Application> getAllApplications(String accountName) throws BaasPersistentException, BaasEntityNotFoundException;
	
	@PreAuthorize("hasAnyRole('ROLE_TENENT_ADMIN','ROLE_ACCOUNT_ADMIN')")
	public Application getApplication(String accountName,String applicationName) throws BaasPersistentException, BaasEntityNotFoundException;

	@PreAuthorize("hasAnyRole('ROLE_TENENT_ADMIN','ROLE_ACCOUNT_ADMIN')")
	public Application createApplication(String accountName, String applicationName, String applicationType, String applicationSubType, String applicationFullName) throws BaasPersistentException;
	
	@PreAuthorize("hasAnyRole('ROLE_TENENT_ADMIN','ROLE_ACCOUNT_ADMIN')")
	public Application editApplication(String accountName, String applicationName, String applicationType, String applicationSubType, String applicationFullName) throws BaasPersistentException;
	
	@PreAuthorize("hasAnyRole('ROLE_TENENT_ADMIN','ROLE_ACCOUNT_ADMIN')")
	public boolean deleteApplication(String accountName, String applicationName) throws BaasPersistentException,BaasEntityNotFoundException;
}
