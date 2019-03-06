const LIBS_PATH = '/libs/';
const COMMONS_PATH = '/commons/';
const BUSINESS_PATH = '/business/';
const SRV_URL = "/rcm-rest";
const PATH_URL_INDEX = "/index";
require.config({
    paths: {
        'jquery': LIBS_PATH + 'jquery/jquery-3.1.0.min',
        'jquery-cookie':  LIBS_PATH + 'jquery/jquery.cookie',
        'jqvalidate': LIBS_PATH + 'javascripts/jquery.validate-1.11.1/jquery.validate',
        'angular': LIBS_PATH + 'angular-1.5.8/angular.min',
        'ngSanitize': LIBS_PATH + 'angular-1.5.8/angular-sanitize',
        'ng-animate': LIBS_PATH + 'angular-1.5.8/angular-animate',
        'ng-cookies': LIBS_PATH + 'angular-1.5.8/angular-cookies',
        'ng-route': LIBS_PATH + 'angular-1.5.8/angular-route',
        'ng-file-upload': LIBS_PATH + 'angular-1.5.8/upload/ng-file-upload',
        'ng-file-upload-shim': LIBS_PATH + 'angular-1.5.8/upload/ng-file-upload-shim',
        'ui-router': LIBS_PATH + 'ui-router/angular-ui-router',
        'ui-bootstrap': LIBS_PATH + 'ui-bootstrap/js/bootstrap.min',
        'ui-tpls': LIBS_PATH + 'ui-bootstrap/js/ui-bootstrap-tpls-1.3.3.min',
        'dtree': LIBS_PATH + "javascripts/dtree/js/dtree",
        'dtree-menu': LIBS_PATH + 'javascripts/dtree/js/dtree-menu',
        'ztree-edit': LIBS_PATH + 'javascripts/zTree/js/jquery.ztree.exedit',
        'ztree-check': LIBS_PATH + 'javascripts/zTree/js/jquery.ztree.excheck',
        'ztree-core': LIBS_PATH + 'javascripts/zTree/js/jquery.ztree.core',
        'ztree-hide': LIBS_PATH + 'javascripts/zTree/js/jquery.ztree.exhide',
        'datepicher': LIBS_PATH + 'javascripts/bootstrap-datepicker-1.3.0/bootstrap-datepicker',
        'app': COMMONS_PATH + 'bewg',
        'Service': COMMONS_PATH + 'service',
        'Directive': COMMONS_PATH + 'directive',
        'Filter': COMMONS_PATH + 'filter',
        'ComCtrl': COMMONS_PATH + 'comCtrl',
        'Constants': COMMONS_PATH + 'constants'
    },
    shim: {
        'angular': {
            deps: [
                'jquery',
                'jquery-cookie',
            ],
            exports: 'angular',
        },
        'ui-bootstrap': {
            deps: [
                'jquery',
            ],
            exports: 'ui-bootstrap',
        },
        'ui-tpls': {
            deps: [
                'angular',
                'ng-animate',
                'ui-bootstrap'
            ],
            exports: 'ui-tpls',
        },
        'ng-animate': {
            deps: ['angular'],
            exports: 'ng-animate',
        },
        'ngSanitize': {
            deps: ['angular'],
            exports: 'ngSanitize',
        },
        'ng-cookies': {
            deps: ['angular'],
            exports: 'ng-cookies',
        },
        'ui-router': {
            deps: ['angular'],
            exports: 'ng-router',
        },
        'app': {
            deps: ['ui-router']
        }
    }
});
// 初始化myModule模块
require(['jquery', 'angular', 'ngSanitize', 'app', 'ng-animate', 'ui-tpls', 'Constants', 'Directive','datepicher'], function () {
    angular.bootstrap(document, ['myModule']);
});