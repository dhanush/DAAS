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
package com.bbytes.daas.client.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.bbytes.daas.domain.Entity;

/**
 * The class that is used to process the {@link Relation} annotation.
 * 
 * @author Thanneer
 * 
 * @version
 */
public class RelationAnnotationProcessor {

	public static <T extends Entity> Map<String, Field> getRelationAndEntity(T entity) throws IllegalArgumentException,
			IllegalAccessException {
		Map<String, Field> relationNameAndEntity = new HashMap<String, Field>();

		for (Field field : entity.getClass().getDeclaredFields()) {
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Relation) {
					field.setAccessible(true);
					if (field.get(entity) != null) {
						relationNameAndEntity.put("REL_"+((Relation) annotation).name(), field);
					}
				}
			}
		}
		return relationNameAndEntity;

	}

	
	public static boolean isCascadeCreate(Field field) {
		Annotation[] annotations = field.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Relation) {
				CascadeType[] cascadeTypes = ((Relation) annotation).cascadeTypes();
				for (CascadeType cascadeType : cascadeTypes) {
					if (cascadeType.equals(CascadeType.CREATE) || cascadeType.equals(CascadeType.ALL))
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isCascadeUpdate(Field field) {
		Annotation[] annotations = field.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Relation) {
				CascadeType[] cascadeTypes = ((Relation) annotation).cascadeTypes();
				for (CascadeType cascadeType : cascadeTypes) {
					if (cascadeType.equals(CascadeType.UPDATE) || cascadeType.equals(CascadeType.ALL))
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isCascadeDelete(Field field) {
		Annotation[] annotations = field.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Relation) {
				CascadeType[] cascadeTypes = ((Relation) annotation).cascadeTypes();
				for (CascadeType cascadeType : cascadeTypes) {
					if (cascadeType.equals(CascadeType.DELETE) || cascadeType.equals(CascadeType.ALL))
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean islazy(Field field) {
		Annotation[] annotations = field.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Relation) {
				return ((Relation) annotation).lazy();
			}
		}
		return false;
	}
}
