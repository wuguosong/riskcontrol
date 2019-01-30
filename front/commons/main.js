const LIBS_PATH = '/libs/';
const COMMONS_PATH = '/commons/';
const BUSINESS_PATH = '/business/';

require.config({
    paths: {
        'jquery': LIBS_PATH + 'jquery/jquery-3.1.0.min',
        'angular': LIBS_PATH + 'angular-1.5.8/angular.min',
        'ng-cookies': LIBS_PATH + 'angular-1.5.8/angular-cookies',
        'ui-router': LIBS_PATH + 'ui-router/angular-ui-router',
        'app': COMMONS_PATH + 'bewg'

        // 'booksService': COMMONS_PATH + 'service/booksServicessssss'
    },
    shim: {
        'angular': {
            exports: 'angular',
            deps: [
            	'jquery',
			]
        },
        'ng-cookies': {
            deps: ['angular']
        },
        'ui-router': {
            deps: ['angular']
        },
        'app': {
            deps: ['ui-router']
        }
    }
})
// 初始化myModule模块
require(['app'],function(){
    angular.bootstrap(document, ['myModule'])
})