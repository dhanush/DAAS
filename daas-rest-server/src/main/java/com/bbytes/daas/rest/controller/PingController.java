package com.bbytes.daas.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Rest service for testing the daas server status
 * 
 * @author Thanneer
 * 
 * @version 1.0.0
 */
@Controller
public class PingController {

	/**
	 * Ping method to test if the rest server is up
	 * @return
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String ping() {
		return "DAAS Server, it works !!";
	}

	

}
