package com.bbytes.daas.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class BaseDBTest extends DAASTesting {

	protected static String DB_NAME = "TEST";

	@BeforeClass
	public static void setUpTenantInfo() {
		TenantRouter.setTenantIdentifier(DB_NAME);
	}

	protected void setAuthObjectForTest(String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
		grantedAuthorities.add(grantedAuthority);
		Authentication auth = new UsernamePasswordAuthenticationToken("test", "test123", grantedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
