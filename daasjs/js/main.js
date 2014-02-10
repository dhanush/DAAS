require.config({
	baseUrl : 'js/lib',
	paths : {
		// the left side is the module ID,
		// the right side is the path to
		// the jQuery file, relative to baseUrl.
		// Also, the path should NOT include
		// the '.js' file extension. This example
		// is using jQuery 1.9.0 located at
		// js/lib/jquery-1.9.0.js, relative to
		// the HTML page.
		jquery : 'jquery-1.11.0',
		module: '../module'
			
	}
});

// Start the main app logic.
requirejs([ 'jquery', 'module/ping' ], function($, ping) {

	ping.ping("http://localhost:8080/daas-rest-server/ping", function(data){
		alert(data);
	});
});


