require.config({
	baseUrl: 'javascripts',
	paths: {
		'app': 'bewg',
		'angular': 'angular.min',
		'router': 'angular-ui-router',
        'booksService': 'service/booksServicessssss'
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