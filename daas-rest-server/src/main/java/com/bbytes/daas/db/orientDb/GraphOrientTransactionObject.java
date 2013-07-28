
package com.bbytes.daas.db.orientDb;



/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
public class  GraphOrientTransactionObject  extends OrientTransactionObject {


	public void flush() {
		this.databaseHolder.getGraphDatabase().commit();
	}

}
