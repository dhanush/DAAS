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

	private String accountName;

	public Application() {
		super();
		type = Application.class.getSimpleName();
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName
	 *            the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

}
