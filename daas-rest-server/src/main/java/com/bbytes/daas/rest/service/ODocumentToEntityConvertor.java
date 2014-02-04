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
package com.bbytes.daas.rest.service;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

import com.bbytes.daas.dao.DaasDefaultFields;
import com.bbytes.daas.domain.Entity;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Entity To Orient ODocument Convertor
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
public class ODocumentToEntityConvertor<T extends Entity> implements Converter<ODocument, T> {

	private static final Logger LOG = Logger.getLogger(ODocumentToEntityConvertor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T convert(ODocument source) {

		if (source == null)
			throw new IllegalArgumentException("Entity to be converted is null");

		T entity;
		ObjectMapper mapper = new ObjectMapper();
		try {
			entity =  (T) mapper.readValue(source.toJSON(),
					Class.forName(source.field(DaasDefaultFields.ENTITY_FULL_CLASS_NAME.toString()).toString()));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ConversionFailedException(TypeDescriptor.valueOf(Entity.class),
					TypeDescriptor.valueOf(ODocument.class), source, e);
		}

		return entity;

	}

}
