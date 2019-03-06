define(['angular', 'ui-router', 'ng-cookies', 'ui-tpls'], function () {
    var app = angular.module("myModule", ['ui.router', 'ngCookies', 'ngAnimate', 'ngSanitize', 'ui.bootstrap']);
    app.config(["$httpProvider", function ($httpProvider) {
        $httpProvider.interceptors.push('httpInterceptor');
    }])
        .factory("httpInterceptor", ["$q", "$rootScope", '$location', function ($q, $rootScope, $location) {
            return {
                request: function (config) {
                    // console.log(config);
                    var credentials = $.cookie('credentials')
                    var user = '';
                    if (credentials != undefined){
                        var user = JSON.parse(credentials);
                        config.headers = {'authorization' : user.UUID, 'Content-Type': 'application/x-www-form-urlencoded'};
                    } else {
                        config.headers = {'Content-Type': 'application/x-www-form-urlencoded'};
                    }
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
                /*************************系统管理开始************************/
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
                /*************************系统管理结束************************/


                /*************************基础设置开始************************/
                /**区域、直接负责人列表页面**/
                .state("index.DirectUserReportingUnitList", {
                    url: "/DirectUserReportingUnitList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/directUser/DirectUserReportingUnitList.html',
                            controller: 'DirectUserReportingUnitListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/directUser/DirectUserReportingUnitListCtrl.js?_v=3'
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
                            templateUrl: BUSINESS_PATH + 'setting/directUser/DirectUserReportingUnit.html',
                            controller: 'DirectUserReportingUnitCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/directUser/DirectUserReportingUnitCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 项目名称修改
                .state("index.UpdateProjectNameList", {
                    url: "/UpdateProjectNameList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/updateProjectName/updateProjectNameList.html',
                            controller: 'updateProjectNameListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/updateProjectName/updateProjectNameListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 平台公告管理列表
                .state("index.notificationList", {
                    url: "/notificationList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/notification/notificationList.html',
                            controller: 'notificationListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/notification/notificationListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 平台公告管理详情
                .state("index.notificationInfo", {
                    url: "/notificationInfo/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/notification/notificationInfo.html',
                            controller: 'notificationInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/notification/notificationInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 平台公告管理查看
                .state("index.notificationInfoView", {
                    url: "/notificationInfoView/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/notification/notificationInfoView.html',
                            controller: 'notificationInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/notification/notificationInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 商业模式添加附件列表
                .state("index.BusinessModelList", {
                    url: "/BusinessModelList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/businessModel/businessModelList.html',
                            controller: 'businessModelListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/businessModel/businessModelListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 商业列表
                .state("index.ListBusiness", {
                    url: "/ListBusiness/:UUID/:BUSINESS_NAME/:BUSINESS_TYPE",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/businessModel/listBusiness.html',
                            controller: 'listBusinessCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/businessModel/listBusinessCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 管理商业附件列表
                .state("index.ManageAttachmentList", {
                    url: "/ManageAttachmentList/:yuuid/:ybusiness_name/:business_type",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/businessModel/manageAttachmentList.html',
                            controller: 'manageAttachmentListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/businessModel/manageAttachmentListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 预评审时间预警设置列表
                .state("index.PreWarningTimeList", {
                    url: "/PreWarningTimeList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/preWarningTime/preWarningTimeList.html',
                            controller: 'preWarningTimeListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/preWarningTime/preWarningTimeListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 预评审时间预警设置详情
                .state("index.PreWarningTimeInfo", {
                    url: "/PreWarningTimeInfo/:action/:uuid",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/preWarningTime/preWarningTimeInfo.html',
                            controller: 'preWarningTimeInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/preWarningTime/preWarningTimeInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 风险案例列表
                .state("index.riskGuidelinesList", {
                    url: "/riskGuidelinesList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/riskGuidelines/riskGuidelinesList.html',
                            controller: 'riskGuidelinesListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/riskGuidelines/riskGuidelinesListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 风险案例已提交列表
                .state("index.submitRiskGuidelinesList", {
                    url: "/submitRiskGuidelinesList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/riskGuidelines/submitRiskGuidelinesList.html',
                            controller: 'submitRiskGuidelinesListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/riskGuidelines/submitRiskGuidelinesListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 风险案例详情
                .state("index.riskGuidelineInfo", {
                    url: "/riskGuidelineInfo/:action/:id/:flag",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/riskGuidelines/riskGuidelineInfo.html',
                            controller: 'riskGuidelineInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/riskGuidelines/riskGuidelineInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 风险案例查看
                .state("index.riskGuidelineInfoView", {
                    url: "/riskGuidelineInfoView/:action/:id/:flag",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/riskGuidelines/riskGuidelineInfoView.html',
                            controller: 'riskGuidelineInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/riskGuidelines/riskGuidelineInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 评审小组管理列表
                .state("index.ReviewTeamList", {
                    url: "/ReviewTeamList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/reviewTeam/reviewTeamList.html',
                            controller: 'reviewTeamListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/reviewTeam/reviewTeamListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 评审小组管理详情
                .state("index.ReviewTeamInfo", {
                    url: "/ReviewTeamInfo/:action/:uuid",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/reviewTeam/reviewTeamInfo.html',
                            controller: 'reviewTeamInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/reviewTeam/reviewTeamInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 模板文件管理列表
                .state("index.templateFlieList", {
                    url: "/templateFlieList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/templateFlie/templateFlieList.html',
                            controller: 'templateFlieListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/templateFlie/templateFlieListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 模板文件管理已提交列表
                .state("index.submitTemplateFlieList", {
                    url: "/submitTemplateFlieList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/templateFlie/submitTemplateFlieList.html',
                            controller: 'submitTemplateFlieListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/templateFlie/submitTemplateFlieListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 模板文件管理详情
                .state("index.templateFlieInfo", {
                    url: "/templateFlieInfo/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/templateFlie/templateFlieInfo.html',
                            controller: 'templateFlieInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/templateFlie/templateFlieInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 模板文件管理查看
                .state("index.templateFlieInfoView", {
                    url: "/templateFlieInfoView/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/templateFlie/templateFlieInfoView.html',
                            controller: 'templateFlieInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/templateFlie/templateFlieInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 规章制度管理列表
                .state("index.regulationsList", {
                    url: "/regulationsList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/regulations/regulationsList.html',
                            controller: 'regulationsListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/regulations/regulationsListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 规章制度管理已提交列表
                .state("index.submitRegulationsList", {
                    url: "/submitRegulationsList",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/regulations/submitRegulationsList.html',
                            controller: 'submitRegulationsListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/regulations/submitRegulationsListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 模板文件管理详情
                .state("index.regulationsInfo", {
                    url: "/regulationsInfo/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/regulations/regulationsInfo.html',
                            controller: 'regulationsInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/regulations/regulationsInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 模板文件管理查看
                .state("index.regulationsInfoView", {
                    url: "/regulationsInfoView/:action/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'setting/regulations/regulationsInfoView.html',
                            controller: 'regulationsInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'setting/regulations/regulationsInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /*************************基础设置结束************************/

                /*************************业务部分开始************************/
                // 投标评审申请列表
                .state("index.PreInfoList", {
                    url: "/PreInfoList/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/preReview/preInfoList.html',
                            controller: 'preInfoListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/preReview/preInfoListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 投标评审申请详情
                .state("index.PreInfo", {
                    url: "/PreInfo/:id/:flag",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/preReview/preInfo.html',
                            controller: 'preInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/preReview/preInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 投标评审申请查看
                .state("index.PreInfoView", {
                    url: "/PreInfoView/:id/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/preReview/preInfoView.html',
                            controller: 'preInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/preReview/preInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 正式评审申请列表
                .state("index.FormalAssessmentInfoList", {
                    url: "/FormalAssessmentInfoList/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/formalAssessment/formalAssessmentInfoList.html',
                            controller: 'formalAssessmentInfoListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/formalAssessment/formalAssessmentInfoListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 正式评审申请详情
                .state("index.FormalAssessmentInfo", {
                    url: "/FormalAssessmentInfo/:id/:flag",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/formalAssessment/formalAssessmentInfo.html',
                            controller: 'formalAssessmentInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/formalAssessment/formalAssessmentInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 正式评审申请查看
                .state("index.FormalAssessmentInfoView", {
                    url: "/FormalAssessmentInfoView/:id/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/formalAssessment/formalAssessmentInfoView.html',
                            controller: 'formalAssessmentInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/formalAssessment/formalAssessmentInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 通报类事项列表
                .state("index.BulletinMatterList", {
                    url: "/BulletinMatterList/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/bulletin/bulletinMatterList.html',
                            controller: 'bulletinMatterListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/bulletin/bulletinMatterListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 通报类事项详情
                .state("index.BulletinMatterInfo", {
                    url: "/BulletinMatterInfo/:id",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/bulletin/bulletinMatterInfo.html',
                            controller: 'bulletinMatterInfoCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/bulletin/bulletinMatterInfoCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                // 通报类事项查看
                .state("index.BulletinMatterInfoView", {
                    url: "/BulletinMatterInfoView/:id/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApplication/bulletin/bulletinMatterInfoView.html',
                            controller: 'bulletinMatterInfoViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApplication/bulletin/bulletinMatterInfoViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**投标评审审批开始**/
                /**投标评审列表页面**/
                .state("index.PreAuditList", {
                    url: "/PreAuditList/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApproval/preAudit/PreAuditList.html',
                            controller: 'PreAuditListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApproval/preAudit/PreAuditListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**投标评审审核页面**/
                .state("index.PreAuditDetailView", {
                    url: "/PreAuditDetailView/:id/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApproval/preAudit/PreAuditDetailView.html',
                            controller: 'PreAuditDetailViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApproval/preAudit/PreAuditDetailViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**投标评审审批结束**/
                /**正式评审审批开始**/
                /**正式评审审核列表页面**/
                .state("index.FormalAssessmentAuditList", {
                    url: "/FormalAssessmentAuditList/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApproval/formalAssessment/FormalAssessmentAuditList.html',
                            controller: 'FormalAssessmentAuditListCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApproval/formalAssessment/FormalAssessmentAuditListCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**正式评审审核页面**/
                .state("index.FormalAssessmentAuditDetailView", {
                    url: "/FormalAssessmentAuditDetailView/:id/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApproval/formalAssessment/FormalAssessmentAuditDetailView.html',
                            controller: 'FormalAssessmentAuditDetailViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApproval/formalAssessment/FormalAssessmentAuditDetailViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**正式评审审批结束**/
                /**其它评审审批开始**/
                /**其它评审列表页面**/
                .state("index.BulletinMattersAudit", {
                    url: "/BulletinMattersAudit/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApproval/bulletin/BulletinMattersAudit.html',
                            controller: 'BulletinMattersAuditCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApproval/bulletin/BulletinMattersAuditCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**其它评审查看页面**/
                .state("index.BulletinMattersAuditView", {
                    url: "/BulletinMattersAuditView/:id/:url",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'rcm/projectApproval/bulletin/BulletinMattersAuditView.html',
                            controller: 'BulletinMattersAuditViewCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'rcm/projectApproval/bulletin/BulletinMattersAuditViewCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                /**其它评审审批结束**/
                /*************************投资决策会管理Start************************/
                // 评审小组管理详情
                .state("index.meetingArrangement", {
                    url: "/meetingArrangement/:tabIndex",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'meet/meetingArrangement.html',
                            controller: 'MeetingArrangementCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'meet/meetingArrangementCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
                .state("index.meetingSubmit", {
                    url: "/meetingSubmit",
                    views: {
                        'business': {
                            templateUrl: BUSINESS_PATH + 'meet/meetingSubmit.html',
                            controller: 'MeetingSubmitCtrl'
                        }
                    },
                    resolve: {
                        loadCtrl: ["$q", function ($q) {
                            var deferred = $q.defer();
                            require([
                                BUSINESS_PATH + 'meet/meetingSubmitCtrl.js?_v=3'
                            ], function () {
                                deferred.resolve();
                            });
                            return deferred.promise;
                        }]
                    }
                })
            /*************************投资决策会管理End************************/
            /*************************业务部分结束************************/
        }]);
    return app;
});