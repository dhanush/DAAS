
/**
 * Main daas module 
 */
define(function(require) {
	var daas = require('daas/core');
	daas.mgmt = require('daas/mgmt');
	daas.ping = require('daas/ping');
	return daas;
});