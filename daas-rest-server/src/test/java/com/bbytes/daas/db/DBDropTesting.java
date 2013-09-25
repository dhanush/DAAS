package com.bbytes.daas.db;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.dao.AccountDao;
import com.bbytes.daas.db.orientDb.OrientDbConnectionManager;
import com.bbytes.daas.db.orientDb.OrientDbTemplate;
import com.bbytes.daas.db.orientDb.TenantRouter;
import com.bbytes.daas.domain.Account;
import com.bbytes.daas.rest.BaasPersistentException;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */

public class DBDropTesting extends BaseDBTest {

	@Autowired
	private OrientDbConnectionManager connectionManager;

	@Autowired
	private AccountDao accountDao;
	
	protected static String DB_NAME="TEST";

	@BeforeClass
	public static void setUp() {
		TenantRouter.setTenantIdentifier(DB_NAME);
	}

	@Before
	public void setup() throws BaasPersistentException {
		if (!accountDao.findAny("name", DB_NAME)) {
			Account acc = new Account();
			acc.setName(DB_NAME);
			acc.setType(DB_NAME);
			accountDao.save(acc);
		}

	}

	@Test
	public void testDBExist() {
		connectionManager.getDatabase();
		assertTrue(connectionManager.databaseExist(DB_NAME));
	}
	
	@Test
	public void testDropDB() {
		assertTrue(connectionManager.dropDatabase(DB_NAME));
	}

}
