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
package com.bbytes.daas.rest.dao;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.DaasUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *  User DAO for all types of users like account user , application user . 
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */

public interface UserDao extends DaasDAO<DaasUser> {

	public DaasUser saveAccountUser(DaasUser user) throws BaasPersistentException ;
	
	public DaasUser saveAppUser(DaasUser user) throws BaasPersistentException ;
	
	public DaasUser findUser(String userName) throws BaasEntityNotFoundException ;
	
	public DaasUser findUser(String accountName, String userName) throws BaasEntityNotFoundException ;
	
	public DaasUser findUser(String accountName, String appName, String userName) throws BaasEntityNotFoundException ;
	
	
}
