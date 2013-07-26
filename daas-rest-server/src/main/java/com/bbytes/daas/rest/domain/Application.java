/**
 * 
 */
package com.bbytes.daas.rest.domain;

import java.io.Serializable;

/**
 * @author Dhanush Gopinath
 * 
 */
public class Application implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2202750001669949099L;

	private String uuid;

	private String name;

	private String organizationName;

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

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * @param organizationName
	 *            the organizationName to set
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
