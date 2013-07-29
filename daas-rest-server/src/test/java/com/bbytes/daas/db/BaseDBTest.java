
package com.bbytes.daas.db;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.bbytes.daas.db.orientDb.TenantRouter;
import com.bbytes.daas.rest.DAASTesting;

/**
 * 
 *
 * @author Thanneer
 *
 * @version 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@ContextConfiguration(locations = { "classpath:/spring/rest-daas-server-context.xml" })
public class BaseDBTest extends DAASTesting{

	
	@BeforeClass
	public static void setUpTenantInfo(){
		TenantRouter.setTenantIdentifier("TEST");
	}
	
	@Test
	public void dummyMethod(){
		assertTrue(true);
	}
}
