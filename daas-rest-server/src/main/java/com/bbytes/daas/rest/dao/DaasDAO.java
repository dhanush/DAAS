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

import java.util.List;
import java.util.Map;

import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
import com.bbytes.daas.rest.domain.Entity;
import com.orientechnologies.orient.core.id.ORID;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public interface DaasDAO<E extends Entity> {

	public E save(E entity) throws BaasPersistentException;

	public E update(E entity) throws BaasPersistentException;

	public void remove(E entity) throws BaasPersistentException;

	public E find(ORID id) throws BaasPersistentException,BaasEntityNotFoundException;
	
	public E find(String uuid) throws BaasPersistentException,BaasEntityNotFoundException;

	public List<E> findAll() throws BaasPersistentException,BaasEntityNotFoundException;
	
	public long count() throws BaasPersistentException;
	
	public boolean findAny(String property , String value) throws BaasPersistentException;
	
	public boolean findAny(Map<String,String> propertyToValue) throws BaasPersistentException;
}
