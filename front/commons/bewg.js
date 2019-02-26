﻿define(['angular', 'ui-router', 'ng-cookies', 'ui-tpls'], function () {
    var app = angular.module("myModule", ['ui.router', 'ngCookies', 'ngAnimate', 'ngSanitize', 'ui.bootstrap']);
    app.config(["$httpProvider", function ($httpProvider) {
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
            // $urlRouterProvider.otherwise('/index/');
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
                .state("index.dic", {
                    url: "/dic",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/dic/dicList.html',
                            controller: 'dicListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/dic/dicListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                .state("index.dicList", {
                    url: "/dicList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/dic/dicList.html',
                            controller: 'dicListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/dic/dicListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //数据字典项列表
                .state("index.dicOptionList", {
                    url: "/dicOptionList/:UUID",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/dic/dicOptionList.html',
                            controller: 'dicOptionListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/dic/dicOptionListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //用户列表
                .state("index.userList", {
                    url: "/userList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/user/userList.html',
                            controller: 'userListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/user/userListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //给用户分配角色
                .state("index.userRole", {
                    url: "/userRole/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/user/userRole.html',
                            controller: 'userRoleCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/user/userRoleCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //用户详情信息
                .state("index.userInfo", {
                    url: "/userInfo/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/user/userInfo.html',
                            controller: 'userInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/user/userInfoCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //菜单列表页面
                .state("index.sysFunList", {
                    url: "/sysFunList/:funcId",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/sysFun/sysFunList.html',
                            controller: 'sysFunListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/sysFun/sysFunListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //菜单详情页面
                .state("index.sysFunInfo", {
                    url: "/sysFunInfo/:action/:funcId",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/sysFun/sysFunInfo.html',
                            controller: 'sysFunInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/sysFun/sysFunInfoCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**********系统角色开始[Add By LiPan 2019-02-26]**************/
                /**角色列表页面**/
                .state("index.SysRoleList", {
                    url: "/SysRoleList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/role/SysRoleList.html',
                            controller: 'SysRoleListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/role/SysRoleListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**角色新增或者更新页面**/
                .state("index.SysRoleInfo", {
                    url: "/SysRoleInfo/:action/:roleId",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/role/SysRoleInfo.html',
                            controller: 'SysRoleInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/role/SysRoleInfoCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**角色查看页面**/
                .state("index.SysRoleView", {
                    url: "/SysRoleView/:roleId/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/role/SysRoleView.html',
                            controller: 'SysRoleViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/role/SysRoleViewCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**分配菜单页面**/
                .state("index.RoleAndFun", {
                    url: "/RoleAndFun/:roleId/:roleCode/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/role/RoleAndFun.html',
                            controller: 'RoleAndFunCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/role/RoleAndFunCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**分配用户页面**/
                .state("index.RoleAndUser", {
                    url: "/RoleAndUser/:roleId/:roleCode/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/role/RoleAndUser.html',
                            controller: 'RoleAndUserCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/role/RoleAndUserCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
            /**********系统角色结束[Add By LiPan 2019-02-26]**************/
        }]);
    return app;
});