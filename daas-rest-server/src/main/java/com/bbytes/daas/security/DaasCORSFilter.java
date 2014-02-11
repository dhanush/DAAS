package com.bbytes.daas.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * CORS Filter for Enabling Cross Origin Requests for DAAS calls
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class DaasCORSFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletResponse httpResp = (HttpServletResponse) response;
		httpResp.setHeader("Access-Control-Allow-Origin", "*");
		httpResp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		httpResp.setHeader("Access-Control-Max-Age", "3600");
		Enumeration<String> headersEnum = ((HttpServletRequest) request).getHeaders("Access-Control-Request-Headers");
		StringBuilder headers = new StringBuilder();
		String delim = "";
		while (headersEnum.hasMoreElements()) {
			headers.append(delim).append(headersEnum.nextElement());
			delim = ", ";
		}
		httpResp.setHeader("Access-Control-Allow-Headers", headers.toString());
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
