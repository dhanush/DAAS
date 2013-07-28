
package com.bbytes.daas.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.bbytes.daas.db.orientDb.TenantRouter;
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

	
	
	@BeforeClass
	public static void setUp(){
		TenantRouter.setTenantIdentifier("TEST");
	}
	
	@Test
	@Transactional("objectDB")
	public void testObjectDBConn(){
		assertNotNull(TenantRouter.getTenantIdentifier());
		assertTrue(orientDbTemplate.getObjectDatabase().exists());
	}
	
	@Test(expected=ODatabaseException.class)
	@Transactional("objectDB")
	public void testGraphDBFail(){
		assertNotNull(TenantRouter.getTenantIdentifier());
		assertTrue(orientDbTemplate.getGraphDatabase().exists());
	}
}
