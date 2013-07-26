/**
 * 
 */
package com.bbytes.daas.rest.domain;

/**
 * @author Dhanush Gopinath
 * 
 */
public class Application extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2202750001669949099L;

	private String organizationName;

	public Application() {
		super();
		type = Application.class.getSimpleName();
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

}
