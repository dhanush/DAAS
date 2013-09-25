/**
 * 
 */
package com.bbytes.daas.domain;

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
	
	private String applicationType;
	
	private String applicationSubType;
	
	/**
	 * Variable to hold the brands full name. (Optional)
	 */
	private String fullName;

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

	/**
	 * @return the applicationType
	 */
	public String getApplicationType() {
		return applicationType;
	}

	/**
	 * @param applicationType the applicationType to set
	 */
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	/**
	 * @return the applicationSubType
	 */
	public String getApplicationSubType() {
		return applicationSubType;
	}

	/**
	 * @param applicationSubType the applicationSubType to set
	 */
	public void setApplicationSubType(String applicationSubType) {
		this.applicationSubType = applicationSubType;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
