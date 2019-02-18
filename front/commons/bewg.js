define(['angular', 'ui-router', 'ng-cookies', 'ui-tpls'], function () {
    var app = angular.module("myModule", ['ui.router', 'ngCookies', 'ngAnimate', 'ui.bootstrap'])
    app
        .config(["$httpProvider", function ($httpProvider) {
            $httpProvider.interceptors.push('httpInterceptor');
        }])
        .factory("httpInterceptor", ["$q", "$rootScope", '$location', function ($q, $rootScope, $location) {
            return {
                request: function (config) {
                    // console.log(config);

                    config.headers = {'Content-Type': 'application/x-www-form-urlencoded'};
                    // do something on request success
                    return config || $q.when(config);
                },
                requestError: function (rejection) {
                    // console.log(rejection);
                    // do something on request error
                    return $q.reject(rejection)
                },
                response: function (response) {
                    // console.log(response);
                    // $location.path('/login');
                    // do something on response success
                    return response || $q.when(response);
                },
                responseError: function (rejection) {
                    // console.log(rejection);
                    // do something on response error
                    return $q.reject(rejection);
                }
            };
        }])
        .config(function ($controllerProvider, $compileProvider, $filterProvider, $provide) {
            app.register = {
                //得到$controllerProvider的引用
                controller: $controllerProvider.register,
                //同样的，这里也可以保存directive／filter／service的引用
                directive: $compileProvider.directive,
                filter: $filterProvider.register,
                service: $provide.service
            };
        })
        .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.otherwise('/index/');
            $stateProvider
                .state("index", {
                    url: "/index",
                    views: {
                        'index': {
                            templateUrl: BUSINESS_PATH + 'index/index.html',
                            controller: 'indexCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'index/indexCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                .state("login", {
                    url: "/login",
                    views: {
                        'index': {
                            templateUrl: BUSINESS_PATH + 'login/login.html',
                            controller: 'loginCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'login/loginCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                .state("index.home", {
                    url: "/",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'home/home.html',
                            controller: 'homeCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'home/homeCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                .state("index.report", {
                    url: "/report",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'report/report.html',
                            controller: 'reportCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'report/reportCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                .state("index.demo", {
                    url: "/demo",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'demo/demo.html',
                            controller: 'demoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'demo/demoCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
        }]);
    return app;
});