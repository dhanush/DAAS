package com.bbytes.daas.rest;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * 
 * 
 * @author Dhanush Gopinath
 * 
 * @version
 */
public class CustomResultHandlerImpl implements ResultHandler {

	private String resultJson = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.test.web.servlet.ResultHandler#handle(org.springframework.test.web.servlet
	 * .MvcResult)
	 */
	@Override
	public void handle(MvcResult result) throws Exception {
		// TODO Auto-generated method stub
		resultJson = result.getResponse().getContentAsString();
	}

	/**
	 * Returns the actual result in json format
	 * @return
	 */
	public String getJsonResult() {
		return resultJson;
	}

}
