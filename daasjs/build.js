requirejs.config({
	// all modules loaded are relative to this path
	// e.g. require(["grid/core"]) would grab /lib/grid/core.js
	baseUrl : "./lib",

	// specify custom module name paths
	paths : {
		jquery : '../vendor/jquery-1.11.0',
		QUnit : '../vendor/qunit-1.14.0',
		tests : '../tests',
		module : '../module',
	},

	// target amd loader shim as the main module, path is relative to baseUrl.
	name : "../vendor/almond",

	optimize : "none",

	// files to include along with almond. only lib/daas.js is defined, as
	// it pulls in the rest of the dependencies automatically.
	include : [ "daas" ],

	// code to wrap around the start / end of the resulting build file
	// the global variable used to expose the API is defined here
	wrap : {
		start : "(function(global, define) {\n" +
		// check for amd loader on global namespace
		"  var globalDefine = global.define;\n",

		end : "  var library = require('daas');\n"
				+ "  if(typeof module !== 'undefined' && module.exports) {\n" +
				// export library for node
				"    module.exports = library;\n"
				+ "  } else if(globalDefine) {\n" +
				// define library for global amd loader that is already present
				"    (function (define) {\n"
				+ "      define(function () { return library; });\n"
				+ "    }(globalDefine));\n" + "  } else {\n" +
				// define library on global namespace for inline script loading
				"    global['daas'] = library;\n" + "  }\n" + "}(this));\n"
	},

	// build file destination, relative to the build file itself
	out : "./dist/daas-1.0.0.js"
})