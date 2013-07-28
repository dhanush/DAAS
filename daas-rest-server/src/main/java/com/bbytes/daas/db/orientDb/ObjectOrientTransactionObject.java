
package com.bbytes.daas.db.orientDb;



/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
public class  ObjectOrientTransactionObject  extends OrientTransactionObject {

	public void flush() {
		this.databaseHolder.getObjectDatabase().commit();
	}

}
