package com.bbytes.daas.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.db.orientDb.OrientDbConnectionManager;
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

public class ObjectDBConnTesting extends BaseDBTest {

	@Autowired
	private OrientDbTemplate orientDbTemplate;

	@Autowired
	private OrientDbConnectionManager orientDbConnectionManager;

	@BeforeClass
	public static void setUp() {
		TenantRouter.setTenantIdentifier("TEST");
	}

	@Test
	public void testObjectDBConn() {
		int index = 0;
		while (index < 2) {
			try {
				assertNotNull(TenantRouter.getTenantIdentifier());
				assertTrue(orientDbConnectionManager.getObjectDatabase().exists());
				System.out.println(orientDbConnectionManager.getObjectDatabase().exists());
				Thread.currentThread().sleep(2000);
			} catch (Exception e) {
			}
			index++;
		}

	}

	@Test
	@Transactional
	public void testGraphDBConn() {
		assertNotNull(TenantRouter.getTenantIdentifier());
		assertTrue(orientDbTemplate.getDatabase().getRawGraph().exists());
	}
}
