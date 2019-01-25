const LIBS_PATH = '/libs/';
const COMMONS_PATH = '/COMMONS/';
const BUSINESS_PATH = '/business/';

require.config({
	paths: {
        'angular': LIBS_PATH + 'angular-1.5.8/angular.min',
        'router': LIBS_PATH + 'ui-router/angular-ui-router',
		'app': COMMONS_PATH + 'bewg',
		'booksService': COMMONS_PATH + 'service/booksServicessssss'
	},
	shim: {
		'angular': {
			exports: 'angular'
		},
		'router': {
			deps: ['angular']
		},
		'app': {
			deps: ['router']
		},
        'booksService': {
            deps: ['app']
        }
	}
})
// 初始化myModule模块
require(['app'],function(){
	angular.bootstrap(document, ['myModule'])		
})