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
import java.util.Map;

import com.bbytes.daas.domain.Entity;
import com.bbytes.daas.rest.DaasEntityNotFoundException;
import com.bbytes.daas.rest.DaasPersistentException;
import com.orientechnologies.orient.core.id.ORID;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public interface DaasDAO<E extends Entity> {

	public E save(E entity) throws DaasPersistentException;

	public E update(E entity) throws DaasPersistentException;

	public void remove(E entity) throws DaasPersistentException;

	public E find(ORID id) throws DaasPersistentException, DaasEntityNotFoundException;

	public E find(String uuid) throws DaasPersistentException, DaasEntityNotFoundException;

	public List<E> findAll() throws DaasPersistentException, DaasEntityNotFoundException;

	public long count() throws DaasPersistentException;

	public List<E> find(String property, String value) throws DaasEntityNotFoundException;

	public boolean findAny(String property, String value);

	public boolean findAny(Map<String, String> propertyToValue);
}
