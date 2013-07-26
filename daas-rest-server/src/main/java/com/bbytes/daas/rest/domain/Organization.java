/**
 * 
 */
package com.bbytes.daas.rest.domain;

import java.io.Serializable;

/**
 * Java represenation of User Grid's Organization JSON Object -
 * 
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public class Organization implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5673848472822451100L;

	
	/**
	 * UUID of the organization
	 */
	private String uuid;

	
	/**
	 * The name of the organization.
	 */
	private String name;

	
	
	public Organization() {
	}


	/**
	 * Overloaded constructor
	 * 
	 * @param uuid
	 * @param name
	 */
	public Organization(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}


	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}


	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
