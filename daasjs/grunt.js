module.exports = function(grunt) {

	// Configure Grunt
	grunt.initConfig({

		requirejs : {
			compile : {
				options : {
					mainConfigFile : "build.js"
				}
			}
		},
		// minify the optimized library file
		min : {
			"dist/daas.min.js" : "dist/daas.js"
		},
		// kick off jasmine, showing results at the cli
		// run jasmine tests any time watched files change
		watch : {
			files : [ 'lib/**/*', 'test/spec/**/*' ]
		}
	});

};