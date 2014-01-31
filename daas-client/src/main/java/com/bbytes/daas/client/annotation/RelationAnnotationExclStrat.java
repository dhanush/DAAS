/*
 * Copyright (C) 2013 The Daas Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.bbytes.daas.client.annotation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * This class will exclude the {@link Relation} annotation field to be converted to json 
 *
 * @author Thanneer
 *
 * @version 
 */
public class RelationAnnotationExclStrat implements ExclusionStrategy {

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipField(com.google.gson.FieldAttributes)
	 */
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return RelationAnnotationProcessor.isRelationField(f);
	}

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
	 */
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

}
