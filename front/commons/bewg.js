define(['angular', 'ui-router', 'ng-cookies', 'ui-tpls'], function () {
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
                    url: "/userRole/:userId",
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
                    url: "/userInfo/:action/:userId",
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
                //终止流程列表
                .state("index.endFlowList", {
                    url: "/endFlowList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/endFlow/endFlowList.html',
                            controller: 'endFlowListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/endFlow/endFlowListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //流程人员变更列表
                .state("index.changeBpmnUserList", {
                    url: "/changeBpmnUserList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/changeBpmnUser/changeBpmnUserList.html',
                            controller: 'changeBpmnUserListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/changeBpmnUser/changeBpmnUserListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //流程控制
                .state("index.bpmn", {
                    url: "/bpmn",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/bpmn/bpmn.html',
                            controller: 'bpmnCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/bpmn/bpmnCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //错误日志列表
                .state("index.journalList", {
                    url: "/journalList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/journal/journalList.html',
                            controller: 'journalListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/journal/journalListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //错误日志详情
                .state("index.journalInfo", {
                    url: "/journalInfo/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/journal/journalInfo.html',
                            controller: 'journalInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/journal/journalInfoCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //错误日志详情
                .state("index.journalDetail", {
                    url: "/journalDetail/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/journal/journalDetail.html',
                            controller: 'journalDetailCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/journal/journalListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //接口重调列表
                .state("index.wscallList", {
                    url: "/wscallList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/wscall/wscallList.html',
                            controller: 'wscallListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/wscall/wscallListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                //接口重调详情
                .state("index.wscallInfo", {
                    url: "/wscallInfo/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/wscall/wscallInfo.html',
                            controller: 'wscallInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            //异步加载controller／directive/filter/service
                            require([
                                BUSINESS_PATH + 'sys/wscall/wscallInfoCtrl.js'
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
                /**********组织管理开始[Add By LiPan 2019-02-26]**************/
                /**组织列表页面**/
                .state("index.GroupList", {
                    url: "/GroupList/:orgId",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/group/GroupList.html',
                            controller: 'GroupListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/group/GroupListCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**组织详情页面**/
                .state("index.GroupAdd", {
                    url: "/GroupAdd/:action/:uuid",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/group/GroupAdd.html',
                            controller: 'GroupAddCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/group/GroupAddCtrl.js'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**区域、直接负责人列表页面**/
                .state("index.DirectUserReportingUnitList", {
                    url: "/DirectUserReportingUnitList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/directUser/DirectUserReportingUnitList.html',
                            controller: 'DirectUserReportingUnitListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/directUser/DirectUserReportingUnitListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**区域、直接负责人详情页面**/
                .state("index.DirectUserReportingUnit", {
                    url: "/DirectUserReportingUnit/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/directUser/DirectUserReportingUnit.html',
                            controller: 'DirectUserReportingUnitCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/directUser/DirectUserReportingUnitCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**单位负责人列表页面**/
                .state("index.GroupUserRepoUnitList", {
                    url: "/GroupUserRepoUnitList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/groupUser/GroupUserRepoUnitList.html',
                            controller: 'GroupUserRepoUnitListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/groupUser/GroupUserRepoUnitListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**单位负责人详情页面**/
                .state("index.GroupUserRepoUnit", {
                    url: "/GroupUserRepoUnit/:action/:id/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/groupUser/GroupUserRepoUnit.html',
                            controller: 'GroupUserRepoUnitCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/groupUser/GroupUserRepoUnitCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**业务区负责人列表页面**/
                .state("index.PertainAreaLeaderList", {
                    url: "/PertainAreaLeaderList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/pertainArea/PertainAreaLeaderList.html',
                            controller: 'PertainAreaLeaderListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/pertainArea/PertainAreaLeaderListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**业务区负责人详情页面**/
                .state("index.PertainAreaDetail", {
                    url: "/PertainAreaDetail/:action/:id/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'sys/pertainArea/PertainAreaDetail.html',
                            controller: 'PertainAreaDetailCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'sys/pertainArea/PertainAreaDetailCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
            /**********组织管理结束[Add By LiPan 2019-02-26]**************/
        }]);
    return app;
});