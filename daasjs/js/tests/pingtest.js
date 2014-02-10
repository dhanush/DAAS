define([ "module/ping" ], function(ping) {
	return {
		run : function() {
			asyncTest('Returns the Ping Status.', function() {
				ping.ping("http://localhost:8080/daas-rest-server/ping",
						function(data) {
							equal(data, "DAAS Server, it works !!",
									"Ping is Successful");
							start();
						});
			});
		}
	};
});