/*
 * Copyright (C) 2013 The Zorba Open Source Project
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

import org.springframework.stereotype.Repository;

import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.AccountUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Account DAO Impl
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Repository
public class AccountUserDaoImpl extends AbstractDao<AccountUser> implements AccountUserDao {

	// to be replaced by current session user
	@Deprecated
	public ODocument getDummyCurrentUser() throws BaasPersistentException {
		AccountUser user = new AccountUser();
		user.setEmail("test@test.com");
		return (ODocument) getDocumentDatabase().save(convert(user));

	}
}