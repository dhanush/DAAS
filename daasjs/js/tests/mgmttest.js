define([ "module/mgmt" ], function(mgmt) {
	return {
		run : function() {
			var today = new Date().getTime() / 1000;
			var accName = "AC" + today;

			asyncTest('Creates & Returns Account', function() {
				mgmt.createAccount(accName, function(data) {
					equal(data.name, accName, "Create Acc is Successful");
					start();
				});
			});

			asyncTest('Returns the account.', function() {
				mgmt.getAccount(accName, function(data) {
					equal(data.name, accName, "Get Account is Successful");
					start();
				});
			});

		}
	};
});