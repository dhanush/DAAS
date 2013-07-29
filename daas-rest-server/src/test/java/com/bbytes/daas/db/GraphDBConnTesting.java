package com.bbytes.daas.db;

import static org.junit.Assert.*;

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

public class GraphDBConnTesting extends BaseDBTest {

	@Autowired
	private OrientDbTemplate orientDbTemplate;

	@BeforeClass
	public static void setUp(){
		TenantRouter.setTenantIdentifier("TEST");
	}
	
	@Test
	@Transactional
	public void testGraphDBConn() {
		assertTrue(orientDbTemplate.getDatabase().exists());
	}


	@Transactional
	public void testObjectDBFail() {
		assertNotNull(orientDbTemplate.getDatabase());
	}

}
