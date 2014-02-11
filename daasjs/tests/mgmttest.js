define([ "daas/mgmt" ], function(mgmt) {
	return {
		crudAccount : function() {
			var currentdate = new Date();
			var _today = currentdate.getHours() + currentdate.getMinutes()
					+ currentdate.getSeconds();
			var _accName = "AC1" + _today;
			var _authToken = "";

			module("Management Module - Set 1 Account CRUD", {});

			asyncTest('Delete Account', function() {
				mgmt.deleteAccount(_accName, function(data) {
					ok(data, "Deleted account");
					start();
				}, _authToken);
			});

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
		},

		crudApplication : function() {
			var currentdate = new Date();
			var _today = currentdate.getHours() + currentdate.getMinutes()
					+ currentdate.getSeconds();
			var _accName = "AC1" + _today;
			var _appName = "APP1" + _today;
			var _authTokenSuperAdmin = "";
			var _authTokenAccAdmin = "";

			var _accountUser = "dummyUser";
			var _accountPwd = "dummyPwd";

			module("Management Module - Set 2 - Application CRUD", {});

			asyncTest('Delete Account', function() {
				mgmt.deleteAccount(_accName, function(data) {
					ok(data, "Deleted account");
					start();
				}, _authTokenSuperAdmin);
			});

			asyncTest('Delete Application', function() {
				mgmt.deleteApplication(_accName, _appName, function(data) {
					ok(data, "Deleted Application");
					start();
				}, _authTokenAccAdmin);
			});

			asyncTest('Edit Application', function() {
				var application = {
					"accountName" : _accName,
					"applicationType" : "RETAIL",
					"applicationSubType" : "BOOKS"
				};

				mgmt.editApplication(_accName, _appName, application, function(
						data) {
					equal(data.applicationSubType, "BOOKS",
							"Edit App is Successful");
					start();
				}, _authTokenAccAdmin);
			});

			asyncTest('Get Application', function() {
				mgmt.getApplication(_accName, _appName, function(data) {
					equal(data.name, _appName, "Get App is Successful");
					start();
				}, _authTokenAccAdmin);
			});

			asyncTest('Create Application', function() {
				var application = {
					"accountName" : _accName,
					"applicationType" : "RETAIL",
					"applicationSubType" : "GROCERY"
				};

				mgmt.createApplication(_accName, _appName, application,
						function(data) {
							equal(data.name, _appName,
									"Create Application is Successful");
							start();
						}, _authTokenAccAdmin);
			});

			asyncTest('Logs in as account user', function() {
				mgmt.login(_accountUser, _accountPwd, _accName, function(data) {
					_authTokenAccAdmin = data.value;
					ok(data, "login is fine");
					start();
				});
			});

			asyncTest('Create Account User', function() {
				var user = {
					"userName" : _accountUser,
					"password" : _accountPwd
				};
				mgmt.createAccountUser(_accName, user, function(data) {
					equal(data.userName, _accountUser,
							"Create Acc User is Successful");
					start();
				}, _authTokenSuperAdmin);
			});

			asyncTest('Create Account', function() {
				mgmt.createAccount(_accName, function(data) {
					equal(data.name, _accName, "Create Acc is Successful");
					start();
				}, _authTokenSuperAdmin);
			});

			asyncTest('Logs in', function() {
				mgmt.login("admin", "password", null, function(data) {
					_authTokenSuperAdmin = data.value;
					ok(data, "login is fine");
					start();
				});
			});
		}
	};
});