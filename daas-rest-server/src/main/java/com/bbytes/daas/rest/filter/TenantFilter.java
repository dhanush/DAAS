package com.bbytes.daas.rest.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bbytes.daas.db.orientDb.TenantRouter;

/**
 * Filter to set the correct {@link TenantRouter} by parsing the path parameter
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class TenantFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String uri = httpRequest.getRequestURI();
		String contextPath = httpRequest.getContextPath();
		String accountName = null;
		
//		// do length check to avoid out of bound error in substring
//		if(contextPath.length() < uri.length()){
			String pathAfterUrlContext = uri.substring(contextPath.length() + 1);
			
			if (TenantUtils.isOAuthRequestURL(uri)) {
				accountName = TenantUtils.getAccountFromRequest(httpRequest);
			} else {
				accountName = TenantUtils.getAccountFromURL(pathAfterUrlContext);
			}
//		}
		
		if (accountName != null)
			TenantRouter.setTenantIdentifier(accountName);
		chain.doFilter(httpRequest, httpResponse);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
