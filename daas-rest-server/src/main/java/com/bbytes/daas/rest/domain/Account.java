/**
 * 
 */
package com.bbytes.daas.rest.domain;

/**
 * Java represenation of Account 
 * 
 * 
 * @author Dhanush Gopinath
 * @version 1.0.0
 */
public class Account extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5673848472822451100L;
	
	private String accountType;
	
	private String accountSubType;
	
	/**
	 * Variable to hold the brands full name. (Optional)
	 */
	private String fullName;


	public Account() {
		super();
		type = Account.class.getSimpleName();
	}

	/**
	 * Overloaded constructor
	 * 
	 * @param uuid
	 * @param name
	 */
	public Account(String uuid, String name) {
		super();
		type = Account.class.getSimpleName();
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

	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the accountSubType
	 */
	public String getAccountSubType() {
		return accountSubType;
	}

	/**
	 * @param accountSubType the accountSubType to set
	 */
	public void setAccountSubType(String accountSubType) {
		this.accountSubType = accountSubType;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the account full name. This can be the brand name also. This is an optional item
	 * 
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
