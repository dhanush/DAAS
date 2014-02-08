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
package com.bbytes.daas.dao;

import java.util.List;

import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.rest.DaasEntityNotFoundException;
import com.bbytes.daas.rest.DaasPersistentException;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * User DAO for all types of users like account user , application user .
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */

public interface UserDao extends DaasDAO<DaasUser> {

	public DaasUser saveAccountUser(DaasUser user) throws DaasPersistentException;

	public DaasUser saveAppUser(DaasUser user) throws DaasPersistentException;

	public DaasUser findUser(String userName) throws DaasEntityNotFoundException;
	
	public ODocument findUserAsRecord(String userName) throws DaasEntityNotFoundException;

	public DaasUser findUser(String accountName, String userName) throws DaasEntityNotFoundException;

	public DaasUser findUser(String accountName, String appName, String userName) throws DaasEntityNotFoundException;

	public List<DaasUser> findUserByRole(String accountName, String role) throws DaasEntityNotFoundException;

	public List<DaasUser> findUserByRole(String accountName, String applicationName, String role)
			throws DaasEntityNotFoundException;

}
