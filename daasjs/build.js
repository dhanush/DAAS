({
	baseUrl : '.',
	paths : {
		jquery : 'js/lib/jquery-1.11.0',
		QUnit : 'js/lib/qunit-1.14.0',
		module : 'js/module',
		tests : 'js/tests',
		daas : "js/daas"
	},
	name : "almond",
	include : [ 'daas' ],
	out : "daas-1.0.0.js",
	insertRequire : [ 'daas' ],
	wrap : true,
	shim : {
		'QUnit' : {
			exports : 'QUnit',
			init : function() {
				QUnit.config.autoload = false;
				QUnit.config.autostart = false;
			}
		}
	}
})
