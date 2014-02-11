define([ "daas/ping" ], function(ping) {
	return {
		run : function() {
			asyncTest('Returns the Ping Status.', function() {
				ping.ping(function(data) {
					equal(data, "DAAS Server, it works !!",
							"Ping is Successful");
					start();
				});
			});
		}
	};
});