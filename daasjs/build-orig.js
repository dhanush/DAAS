({
	baseUrl : '../',
	paths : {
		jquery : 'js/lib/jquery-1.11.0',
		QUnit : 'js/lib/qunit-1.14.0',
		module : 'js/module',
		tests : 'js/tests',
		daas : "js/daas"
	},
	name : "build/almond",
	include : [ 'daas' ],
	out : "../daas-1.0.0.js",
	insertRequire : [ 'daas' ],
	wrap : {
		startFile : 'start.frag',
		endFile : 'end.frag'
	}

})
