define([ "module/mgmt" ], function(mgmt) {
	return {
		run : function() {
			var currentdate = new Date(); 
			
			var _today = currentdate.getHours() +  currentdate.getMinutes() + currentdate.getSeconds();
			var _accName = "AC1" + _today;
			var _authToken = "";
			
			asyncTest('Get Account', function() {
				mgmt.getAccount(_accName, function(data) {
					equal(data.name, _accName, "Get Account is Successful");
					start();
				}, _authToken);
			});
			
			asyncTest('Create Account', function() {
				mgmt.createAccount(_accName, function(data) {
					equal(data.name, _accName, "Create Acc is Successful");
					start();
				}, _authToken);
			});
			asyncTest('Logs in', function() {
				mgmt.login("admin", "password", null, function(data) {
					_authToken = data.value;
					ok(data, "login is fine");
					start();
				});
			});

		}
	};
});