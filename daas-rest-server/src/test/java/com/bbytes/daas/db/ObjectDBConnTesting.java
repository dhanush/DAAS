
package com.bbytes.daas.db;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.orientechnologies.orient.core.exception.ODatabaseException;


/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */

public class ObjectDBConnTesting extends BaseDBTest{

	@Autowired
	private OrientDbTemplate orientDbTemplate;

	
	@Test
	@Transactional("objectDB")
	public void testObjectDBConn(){
		assertTrue(orientDbTemplate.getObjectDatabase().exists());
	}
	
	@Test(expected=ODatabaseException.class)
	@Transactional("objectDB")
	public void testGraphDBFail(){
		assertTrue(orientDbTemplate.getGraphDatabase().exists());
	}
}
