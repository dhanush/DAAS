/**
 * 
 */
package com.bbytes.daas.rest.domain;

/**
 * Java represenation of User Grid's Organization JSON Object -
 * 
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public class Organization extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5673848472822451100L;

	public Organization() {
		super();
		type = Organization.class.getSimpleName();
	}

	/**
	 * Overloaded constructor
	 * 
	 * @param uuid
	 * @param name
	 */
	public Organization(String uuid, String name) {
		super();
		type = Organization.class.getSimpleName();
		this.uuid = uuid;
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
