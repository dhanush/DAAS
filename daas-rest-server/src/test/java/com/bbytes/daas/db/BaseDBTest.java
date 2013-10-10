package com.bbytes.daas.db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bbytes.daas.dao.AccountDao;
import com.bbytes.daas.dao.UserDao;
import com.bbytes.daas.db.orientDb.TenantRouter;
import com.bbytes.daas.domain.Account;
import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.rest.BaasEntityNotFoundException;
import com.bbytes.daas.rest.BaasPersistentException;
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
public class BaseDBTest extends DAASTesting {

	protected static String DB_NAME = "TEST";

	@Autowired
	protected UserDao userDao;

	@Autowired
	protected AccountDao accountDao;

	@BeforeClass
	public static void setUpTenantInfo() {
		TenantRouter.setTenantIdentifier(DB_NAME);
	}

	@Before
	@Transactional
	public void baseSetup() throws BaasPersistentException {
		if (!accountDao.findAny("name", DB_NAME)) {
			Account account = new Account(UUID.randomUUID().toString(), DB_NAME);
			accountDao.save(account);
		}

		if (!userDao.findAny("userName", "test_user")) {
			DaasUser daasUser = new DaasUser();
			daasUser.setAccountName(DB_NAME);
			daasUser.setApplicationName(DB_NAME);
			daasUser.setUserName("test_user");
			userDao.save(daasUser);
		}

	}

	protected void setAuthObjectForTest(String role) throws BaasPersistentException, BaasEntityNotFoundException {
		List<DaasUser> userList = userDao.findAll();
		DaasUser user = userList.get(0);
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
		grantedAuthorities.add(grantedAuthority);
		Authentication auth = new UsernamePasswordAuthenticationToken(user.getUserName(), "test123", grantedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
