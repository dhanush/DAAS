package com.bbytes.daas.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Base entity for holding information that is available across the entities
 * 
 * @author Dhanush Gopinath
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Entity implements Serializable{

	private static final long serialVersionUID = -5534745615089076782L;

	@Id
	protected String id;
	
	@Version
	protected String version;
	
	protected String uuid;

	protected String name;

	protected String type;
	
	protected Date creationDate;
	
	protected Date modificationDate;
	
	

	public Entity(){
		
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

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the modificationDate
	 */
	public Date getModificationDate() {
		return modificationDate;
	}
	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	

}
