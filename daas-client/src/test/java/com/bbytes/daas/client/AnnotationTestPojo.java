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
package com.bbytes.daas.client;

import java.util.Date;

import com.bbytes.daas.client.annotation.CascadeType;
import com.bbytes.daas.client.annotation.Relation;
import com.bbytes.daas.domain.Entity;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class AnnotationTestPojo extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3164908222790879537L;

	private String field1Related;

	private Integer field2Related;
	
	@Relation(name="testPojoOnlyDelete", lazy=true,cascadeTypes=CascadeType.DELETE)
	private TestPojo testPojoOnlyDelete;
	
	@Relation(name="testPojoRelated")
	private TestPojoRelated testPojoRelated;
	
	@Relation(name="testPojoCreateDelete", lazy=true,cascadeTypes={
	        CascadeType.CREATE,CascadeType.DELETE })
	private TestPojo testPojoCreateDelete;
	
	@Relation(name="testPojoRelatedUpdateDelete" , lazy=false,cascadeTypes={
	        CascadeType.UPDATE,CascadeType.DELETE })
	private TestPojoRelated testPojoRelatedUpdateDelete;
	
	@Relation(name="testPojoRelatedUpdate" , lazy=false,cascadeTypes={
	        CascadeType.UPDATE })
	private TestPojoRelated testPojoRelatedUpdate;

	public AnnotationTestPojo() {
		field1Related = "todays date " + new Date();
		field2Related = 1;
	}

	/**
	 * @return the field1Related
	 */
	public String getField1Related() {
		return field1Related;
	}

	/**
	 * @param field1Related the field1Related to set
	 */
	public void setField1Related(String field1Related) {
		this.field1Related = field1Related;
	}

	/**
	 * @return the field2Related
	 */
	public Integer getField2Related() {
		return field2Related;
	}

	/**
	 * @param field2Related the field2Related to set
	 */
	public void setField2Related(Integer field2Related) {
		this.field2Related = field2Related;
	}

	/**
	 * @return the testPojoRelated
	 */
	public TestPojoRelated getTestPojoRelated() {
		return testPojoRelated;
	}

	/**
	 * @param testPojoRelated the testPojoRelated to set
	 */
	public void setTestPojoRelated(TestPojoRelated testPojoRelated) {
		this.testPojoRelated = testPojoRelated;
	}

	/**
	 * @return the testPojoCreateDelete
	 */
	public TestPojo getTestPojoCreateDelete() {
		return testPojoCreateDelete;
	}

	/**
	 * @param testPojoCreateDelete the testPojoCreateDelete to set
	 */
	public void setTestPojoCreateDelete(TestPojo testPojoCreateDelete) {
		this.testPojoCreateDelete = testPojoCreateDelete;
	}

	/**
	 * @return the testPojoRelatedUpdateDelete
	 */
	public TestPojoRelated getTestPojoRelatedUpdateDelete() {
		return testPojoRelatedUpdateDelete;
	}

	/**
	 * @param testPojoRelatedUpdateDelete the testPojoRelatedUpdateDelete to set
	 */
	public void setTestPojoRelatedUpdateDelete(TestPojoRelated testPojoRelatedUpdateDelete) {
		this.testPojoRelatedUpdateDelete = testPojoRelatedUpdateDelete;
	}

	/**
	 * @return the testPojoOnlyDelete
	 */
	public TestPojo getTestPojoOnlyDelete() {
		return testPojoOnlyDelete;
	}

	/**
	 * @param testPojoOnlyDelete the testPojoOnlyDelete to set
	 */
	public void setTestPojoOnlyDelete(TestPojo testPojoOnlyDelete) {
		this.testPojoOnlyDelete = testPojoOnlyDelete;
	}

	/**
	 * @return the testPojoRelatedUpdate
	 */
	public TestPojoRelated getTestPojoRelatedUpdate() {
		return testPojoRelatedUpdate;
	}

	/**
	 * @param testPojoRelatedUpdate the testPojoRelatedUpdate to set
	 */
	public void setTestPojoRelatedUpdate(TestPojoRelated testPojoRelatedUpdate) {
		this.testPojoRelatedUpdate = testPojoRelatedUpdate;
	}

	
}
