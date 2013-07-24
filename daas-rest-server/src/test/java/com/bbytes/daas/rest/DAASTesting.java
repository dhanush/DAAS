package com.bbytes.daas.rest;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * 
 *
 * @author Dhanush Gopinath
 *
 * @version 
 */
public class DAASTesting {

	private static Server jettyServer;
	
	protected static final String DAAS_CONTEXT_PATH = "/daas";
	
	/**
	 * Starts a Jetty server. Applications that are loaded are based on the parameter values.
	 * 
	 * @param loadEndureWeb - if true endure-web application will be loaded
	 * @param loadUsergrid - if true usergrid-rest application will be loaded
	 * @param loadEndureRest TODO
	 * @throws Exception
	 */
	protected static void startJettyTestServer(boolean loadEndureWeb, boolean loadUsergrid, boolean loadEndureRest) throws Exception {
		if (!loadEndureWeb && !loadUsergrid && !loadEndureRest) {
			throw new Exception("All parameters cannot be false");
		}
		jettyServer = new Server(3456);
		WebAppContext userGridHandler = null;
		WebAppContext endureWebHandler = null;
		WebAppContext endureRestHandler = null;
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		if (loadUsergrid) {
			userGridHandler = new WebAppContext();
			userGridHandler.setContextPath("/usergrid-rest");
			userGridHandler.setWar("src/test/resources/usergrid/usergrid-rest-0.0.16-SNAPSHOT.war");
			contexts.addHandler(userGridHandler);
		}
		if (loadEndureWeb) {
			endureWebHandler = new WebAppContext();
			endureWebHandler.setContextPath("/endure");
			endureWebHandler.setWar("src/test/webapp");
			contexts.addHandler(endureWebHandler);
		}
		if (loadEndureRest) {
			endureRestHandler = new WebAppContext();
			endureRestHandler.setContextPath("/endure-rest");
			endureRestHandler.setWar("src/test/webapp");
			contexts.addHandler(endureRestHandler);
		}
		jettyServer.setHandler(contexts);
		jettyServer.start();
	}
	
	/**
	 * Stops the jetty server
	 * 
	 * @throws Exception
	 */
	protected static void stopJettyTestServer() throws Exception {
		if(jettyServer.isRunning()) {
			jettyServer.stop();
		}
	}
}
