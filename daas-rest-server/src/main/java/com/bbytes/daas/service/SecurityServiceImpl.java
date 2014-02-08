package com.bbytes.daas.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bbytes.daas.dao.UserDao;
import com.bbytes.daas.domain.DaasUser;
import com.bbytes.daas.rest.DaasEntityNotFoundException;

/**
 * 
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	private UserDao userDao;

	private Logger log = Logger.getLogger(SecurityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbytes.daas.service.SecurityService#getLoggedInUser()
	 */
	@Override
	public DaasUser getLoggedInUser() throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		String name = auth.getName();
		try {
			return userDao.findUser(name);
		} catch (DaasEntityNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
