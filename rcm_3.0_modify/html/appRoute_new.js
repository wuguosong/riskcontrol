var $routeData;
var _version = 3;
ctmApp.config(["$routeProvider", "$controllerProvider", "$compileProvider",
    function ($routeProvider, $controllerProvider, $compileProvider) {

        $routeData = $routeProvider;
        ctmApp.register = {
            controller: $controllerProvider.register,
            directive: $compileProvider.directive
        };
    }
]);
ctmApp.run(['$route', '$http', '$rootScope', '$location', '$interval',
    function ($route, $http, $rootScope, $location, $interval) {
        $rootScope.$on("$routeChangeStart", function (event, next, current) {
            //---------------------------------------------------------------------------
            //	是否开启定时器:
            //  1:如果是表决日期，且今日还有项目未表决完，则开启定时器
            //	2018-02-27
            //---------------------------------------------------------------------------
            if ($rootScope.hasRole('DECISION_LEADERS') && meetDeciInte == null) {
                $http({
                    method: 'post',
                    url: srvUrl + "decision/isTodayDecision.do"
                }).success(function (data) {
                    if (data.success && meetDeciInte == null) {
                        meetDeciInte = $interval(function () {
                            $rootScope.meetingMonitor();
                        }, meetDeciInteTime);
                    }
                });
            }
            if (current == null || current.scope == null || current.params == null || current.params.url != null) {
                return;
            }
            if (!(next != null && next.params != null && next.params.url != null)) {
                current.scope.$parent.paginationConf.queryObj = {};
                current.scope.$parent.paginationConf.currentPage = 1;
                current.scope.$parent.paginationConf.itemsPerPage = 10;
                current.scope.$parent.paginationConfes.itemsPerPage = 10;
                current.scope.$parent.paginationConfes.queryObj = {};
                current.scope.$parent.paginationConfes.currentPage = 1;
            }
        });
        $routeData
        /*******************************公共页面开始********************************/
        //首页
            .when('/homePage', {
                controller: 'homePage',
                templateUrl: 'page/rcm/homePage/homePage.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/homePage.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //帮助页面
            .when('/Help', {
                controller: 'Help',
                templateUrl: 'page/rcm/help/Help.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/help/Help.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 工作面板-默认
            .when('/', {
                controller: 'IndividualTable',
                templateUrl: 'page/rcm/homePage/IndividualTable.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/IndividualTable.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //员工工作情况
            //tabIndex类型分别有0、1、2、3
            .when('/PersonnelWork/:type/:id/:lx/:tabIndex', {
                controller: 'PersonnelWork',
                templateUrl: 'page/rcm/homePage/PersonnelWork.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/PersonnelWork.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //个人工作台
            .when('/IndividualTable', {
                controller: 'IndividualTable',
                templateUrl: 'page/rcm/homePage/IndividualTable.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/IndividualTable.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //待办
            .when('/MyTask/:url', {
                controller: 'MyTask',
                templateUrl: 'page/rcm/taskManagement/MyTask.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/taskManagement/TaskManagement.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //已完成任务
            .when('/OverTask/:url', {
                controller: 'OverTask',
                templateUrl: 'page/rcm/taskManagement/OverTask.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/taskManagement/TaskManagement.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //今日决策会项目
            .when('/todayMeetingManageList/:url', {
                controller: 'todayMeetingManageList',
                templateUrl: 'page/rcm/homePageMore/todayMeetingManageList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/homePageMore/moreData.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************公共页面结束******************************/


            /*********************************系统设置页面开始******************************/
            // 用户管理列表
            .when('/SysUserList/:orgId', {
                controller: 'SysUserList',
                templateUrl: 'page/fnd/user/SysUserList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/user/SysUserList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 用户分配角色
            .when('/sysUserRole/:userId/:url', {
                controller: 'sysUserRole',
                templateUrl: 'page/fnd/user/sysUserRole.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/user/sysUserRole.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 用户详情
            .when('/SysUserAdd/:action/:uuid', {
                controller: 'SysUserAdd',
                templateUrl: 'page/fnd/user/SysUserAdd.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/user/SysUserAdd.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 角色列表
            .when('/SysRoleList', {
                controller: 'SysRoleList',
                templateUrl: 'page/fnd/role/sysRoleList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/sysRoleList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 给角色分配菜单
            .when('/RoleAndFun/:roleId/:roleCode/:url', {
                controller: 'RoleAndFun',
                templateUrl: 'page/fnd/role/RoleAndFun.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/RoleAndFun.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 给角色分配组织
            .when('/RoleAndOrg/:roleId/:roleCode/:url', {
                controller: 'RoleAndOrg',
                templateUrl: 'page/fnd/role/RoleAndOrg.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/RoleAndOrg.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 给角色分配用户
            .when('/RoleAndUser/:roleId/:roleCode/:url', {
                controller: 'RoleAndUser',
                templateUrl: 'page/fnd/role/RoleAndUser.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/RoleAndUser.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 给角色分配项目
            .when('/RoleAndProject/:roleId/:roleCode/:url', {
                controller: 'RoleAndProject',
                templateUrl: 'page/fnd/role/RoleAndProject.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/RoleAndProject.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 角色详情
            .when('/SysRoleInfo/:action/:roleId', {
                controller: 'SysRoleInfo',
                templateUrl: 'page/fnd/role/SysRoleInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/SysRoleInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 角色详情查看
            .when('/SysRoleView/:roleId/:url', {
                controller: 'SysRoleView',
                templateUrl: 'page/fnd/role/SysRoleView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/SysRoleView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 菜单管理
            .when('/sysFunList/:func_id', {
                controller: 'sysFunList',
                templateUrl: 'page/fnd/sysFun/sysFunList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/sysFun/sysFunList.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 菜单详情
            .when('/sysFunAdd/:action/:func_id', {
                controller: 'sysFunAdd',
                templateUrl: 'page/fnd/sysFun/sysFunAdd.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/sysFun/sysFunAdd.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 数据字典列表
            .when('/DataDictionaryList', {
                controller: 'DataDictionaryList',
                templateUrl: 'page/rcm/BasicSetting/DataDictionaryList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataDictionaryList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            .when('/DecisionLeadersList/:id',{
                controller:'DecisionLeadersListController',
                templateUrl:'page/rcm/meeting/DecisionLeadersList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/DecisionLeadersList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            .when('/DecisionLeadersInfo/:action/:id/:dictId/:url',{
                controller:'DecisionLeadersInfoController',
                templateUrl:'page/rcm/meeting/DecisionLeadersInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/DecisionLeadersInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 数据字典查看
            .when('/DataDictionaryView/:uuid', {
                controller: 'DataDictionaryView',
                templateUrl: 'page/rcm/BasicSetting/DataDictionaryView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataDictionaryView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 数据字典详情
            .when('/DataDictionaryEdit/:action/:uuid', {
                controller: 'DataDictionaryEdit',
                templateUrl: 'page/rcm/BasicSetting/DataDictionaryEdit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataDictionaryEdit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 数据字典项列表
            .when('/DataOptionList/:UUID', {
                controller: 'DataOptionList',
                templateUrl: 'page/rcm/BasicSetting/DataOptionList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataOptionList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 数据字典项详情
            .when('/DataOptionEdit/:action/:uuid/:fk_Id', {
                controller: 'DataOptionEdit',
                templateUrl: 'page/rcm/BasicSetting/DataOptionEdit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataOptionEdit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            /*// 组织管理
            .when('/GroupList/:orgId', {
                controller: 'GroupList',
                templateUrl: 'page/fnd/group/GroupList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GroupList.js?_v='+_version,'../javascripts/util/common.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })*/

            // 结束流程菜单
            .when('/endFlow', {
                controller: 'endFlow',
                templateUrl: 'page/fnd/sysFun/endFlow.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/sysFun/endFlow.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 修改流程人员
            .when('/changeBpmnUser', {
                controller: 'changeBpmnUser',
                templateUrl: 'page/fnd/bpmn/changeBpmnUser.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/bpmn/changeBpmnUser.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 业务区负责人设置列表
            .when('/pertainAreaLeaderList', {
                controller: 'pertainAreaLeaderList',
                templateUrl: 'page/fnd/group/pertainAreaLeaderList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/pertainAreaLeaderList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 业务区负责人设置详情
            .when('/pertainAreaDetail/:action/:id/:url', {
                controller: 'pertainAreaDetail',
                templateUrl: 'page/fnd/group/pertainAreaDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/pertainAreaDetail.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 单位负责人设置列表
            .when('/GrouUserRepoUnitList', {
                controller: 'GrouUserRepoUnitList',
                templateUrl: 'page/fnd/group/GrouUserRepoUnitList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GrouUserRepoUnitList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 单位负责人设置详情
            .when('/GrouUserRepoUnit/:action/:id/:url', {
                controller: 'GrouUserRepoUnit',
                templateUrl: 'page/fnd/group/GrouUserRepoUnit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GrouUserRepoUnit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 流程控制
            .when('/bpmn', {
                controller: 'bpmn',
                templateUrl: 'page/fnd/bpmn/bpmn_index.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/bpmn/bpmn.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 日志列表
            .when('/Journal', {
                controller: 'Journal',
                templateUrl: 'page/fnd/journal/sysJournal.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/journal/journal.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 日志详情
            .when('/JournalInfo/:id/:url', {
                controller: 'JournalInfo',
                templateUrl: 'page/fnd/journal/JournalInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/journal/JournalInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 接口列表
            .when('/wscall', {
                controller: 'wscall',
                templateUrl: 'page/fnd/wscall/wscall.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/wscall/wscall.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 接口详情
            .when('/wscallDetail/:id/:url', {
                controller: 'wscallDetail',
                templateUrl: 'page/fnd/wscall/wscallDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/wscall/wscallDetail.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************系统设置页面结束******************************/


            /*********************************业务设置页面开始******************************/
            //修改项目名称
            .when('/UpdateProjectNameList', {
                controller: 'UpdateProjectNameList',
                templateUrl: 'page/rcm/BasicSetting/UpdateProjectNameList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/UpdateProjectNameList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 平台公告列表
            .when('/notificationList', {
                controller: 'notificationList',
                templateUrl: 'page/rcm/notification/notificationList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/notification/notificationList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 平台公告详情
            .when('/notificationInfo/:action/:id/:url', {
                controller: 'notificationInfo',
                templateUrl: 'page/rcm/notification/notificationInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/notification/notificationInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 平台公告查看
            .when('/notificationInfoView/:action/:id/:url', {
                controller: 'notificationInfoView',
                templateUrl: 'page/rcm/notification/notificationInfoView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/notification/notificationInfoView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            /* // 商业模式添加附件列表
             .when('/BusinessModelList', {
                 controller: 'BusinessModelList',
                 templateUrl: 'page/rcm/BasicSetting/BusinessModelList.html',
                 controllerAs: 'model',
                 resolve: {
                     resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                         var deferred = $q.defer();
                         require(['page/rcm/BasicSetting/BusinessModelList.js?_v='+_version], function () {
                             $rootScope.$apply(function () {
                                 deferred.resolve();
                             });
                         });
                         return deferred.promise;
                     }]
                 }
             })
             // 商业模式列表
             .when('/ListBusiness/:UUID/:BUSINESS_NAME/:BUSINESS_TYPE', {
                 controller: 'ListBusiness',
                 templateUrl: 'page/rcm/BasicSetting/ListBusiness.html',
                 controllerAs: 'model',
                 resolve: {
                     resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                         var deferred = $q.defer();
                         require(['page/rcm/BasicSetting/ListBusiness.js?_v='+_version], function () {
                             $rootScope.$apply(function () {
                                 deferred.resolve();
                             });
                         });
                         return deferred.promise;
                     }]
                 }
             })
             // 管理商业模式附件列表
             .when('/ManageAttachmentList/:yuuid/:ybusiness_name/:business_type', {
                 controller: 'ManageAttachmentList',
                 templateUrl: 'page/rcm/BasicSetting/ManageAttachmentList.html',
                 controllerAs: 'model',
                 resolve: {
                     resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                         var deferred = $q.defer();
                         require(['page/rcm/BasicSetting/ManageAttachmentList.js?_v='+_version], function () {
                             $rootScope.$apply(function () {
                                 deferred.resolve();
                             });
                         });
                         return deferred.promise;
                     }]
                 }
             })
             //预评审时间预警设置列表
             .when('/PreWarningTimeList',{
                 controller:'PreWarningTimeList',
                 templateUrl:'page/rcm/BasicSetting/PreWarningTimeList.html',
                 controllerAs:'model',
                 resolve:{
                     resolver:['$q','$rootScope',function($q,$rootScope){
                         var deferred = $q.defer();
                         require(['page/rcm/BasicSetting/PreWarningTimeList.js?_v='+_version],function(){
                             $rootScope.$apply(function(){
                                 deferred.resolve();
                             });
                         });
                         return deferred.promise;
                     }]
                 }
             })

             //预评审时间预警设置新增
             .when('/PreWarningTimeEdit/:action/:uuid',{
                 controller:'PreWarningTimeEdit',
                 templateUrl:'page/rcm/BasicSetting/PreWarningTimeEdit.html',
                 controllerAs:'model',
                 resolve:{
                     resolver:['$q','$rootScope',function($q,$rootScope){
                         var deferred = $q.defer();
                         require(['page/rcm/BasicSetting/PreWarningTimeEdit.js?_v='+_version],function(){
                             $rootScope.$apply(function(){
                                 deferred.resolve();
                             });
                         });
                         return deferred.promise;
                     }]
                 }
             })*/

            // 风险指引管理列表
            .when('/riskGuidelinesList', {
                controller: 'riskGuidelinesList',
                templateUrl: 'page/rcm/riskGuidelines/riskGuidelinesList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/riskGuidelinesList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 风险指引详情
            .when('/riskGuidelineInfo/:action/:id/:url', {
                controller: 'riskGuidelineInfo',
                templateUrl: 'page/rcm/riskGuidelines/riskGuidelineInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/riskGuidelineInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 风险指引管理已提交列表
            .when('/submitRiskGuidelinesList', {
                controller: 'submitRiskGuidelinesList',
                templateUrl: 'page/rcm/riskGuidelines/submitRiskGuidelinesList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/submitRiskGuidelinesList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 风险指引查看
            .when('/riskGuidelineInfoView/:action/:id/:url', {
                controller: 'riskGuidelineInfoView',
                templateUrl: 'page/rcm/riskGuidelines/riskGuidelineInfoView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/riskGuidelineInfoView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 评审小组管理列表
            .when('/ReviewTeamList', {
                controller: 'ReviewTeamList',
                templateUrl: 'page/rcm/BasicSetting/ReviewTeamList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ReviewTeamList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 评审小组详情
            .when('/ReviewTeamAdd/:action/:uuid', {
                controller: 'ReviewTeamAdd',
                templateUrl: 'page/rcm/BasicSetting/ReviewTeamAdd.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ReviewTeamAdd.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 模板文件管理列表
            .when('/templateFlieList', {
                controller: 'templateFlieList',
                templateUrl: 'page/rcm/templateFile/templateFlieList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/templateFlieList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 模板文件详情
            .when('/templateFlieInfo/:action/:id/:url', {
                controller: 'templateFlieInfo',
                templateUrl: 'page/rcm/templateFile/templateFlieInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/templateFlieInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 模板文件已提交列表
            .when('/submitTemplateFlieList', {
                controller: 'submitTemplateFlieList',
                templateUrl: 'page/rcm/templateFile/submitTemplateFlieList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/submitTemplateFlieList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 模板文件查看
            .when('/templateFlieInfoView/:action/:id/:url', {
                controller: 'templateFlieInfoView',
                templateUrl: 'page/rcm/templateFile/templateFlieInfoView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/templateFlieInfoView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 规章制度管理列表
            .when('/regulationsList', {
                controller: 'regulationsList',
                templateUrl: 'page/rcm/regulations/regulationsList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/regulationsList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 规章制度详情
            .when('/regulationsInfo/:action/:id/:url', {
                controller: 'regulationsInfo',
                templateUrl: 'page/rcm/regulations/regulationsInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/regulationsInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 规章制度管理已提交列表
            .when('/submitRegulationsList', {
                controller: 'submitRegulationsList',
                templateUrl: 'page/rcm/regulations/submitRegulationsList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/submitRegulationsList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 规章制度查看
            .when('/regulationsInfoView/:action/:id/:url', {
                controller: 'regulationsInfoView',
                templateUrl: 'page/rcm/regulations/regulationsInfoView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/regulationsInfoView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 	区域、直接-负责人列表
            .when('/DirectUserReportingUnitList', {
                controller: 'DirectUserReportingUnitList',
                templateUrl: 'page/fnd/group/DirectUserReportingUnitList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/DirectUserReportingUnit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 区域、直接-负责人详情
            .when('/DirectUserReportingUnit/:action/:id', {
                controller: 'DirectUserReportingUnit',
                templateUrl: 'page/fnd/group/DirectUserReportingUnit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/DirectUserReportingUnit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 案例库列表
            .when('/ExperienceList', {
                controller: 'ExperienceList',
                templateUrl: 'page/rcm/formalAssessment/ExperienceList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/ExperienceList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 案例详情
            .when('/Experience/:action/:id/:url', {
                controller: 'Experience',
                templateUrl: 'page/rcm/formalAssessment/Experience.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/Experience.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************业务设置页面结束******************************/


            /*********************************业务页面开始******************************/
            /*********************************投标评审开始******************************/
            // 投标评审申请列表
            .when('/PreInfoList/:tabIndex', {
                controller: 'PreInfoList',
                templateUrl: 'page/rcm/pre/PreInfoList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreInfoList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审申请详情
            .when('/preInfo/:id/:flag', {
                controller: 'preInfo',
                templateUrl: 'page/rcm/pre/preInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/preInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审申请查看
            .when('/PreDetailView/:id/:url', {
                controller: 'PreDetailView',
                templateUrl: 'page/rcm/pre/PreDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审审批列表
            .when('/PreAuditList/:tabIndex', {
                controller: 'PreAuditList',
                templateUrl: 'page/rcm/pre/PreAuditList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审审批详情
            .when('/PreAuditDetailView/:id/:url', {
                controller: 'PreAuditDetailView',
                templateUrl: 'page/rcm/pre/PreAuditDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审查看，用于起草查看评审报告
            .when('/ProjectPreReviewViewReport/:action/:id',{
                controller:'PreAuditDetailView',
                templateUrl:'page/rcm/pre/PreAuditDetailView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审流程查看页面
            .when('/ProjectPreReviewView/:id', {
                controller: 'PreAuditDetailView',
                templateUrl: 'page/rcm/pre/PreAuditDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', 'Upload','$timeout',function ($q, $rootScope,Upload,$timeout) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version,'../javascripts/util/common.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审报告列表
            .when('/PreAuditReportList/:tabIndex', {
                controller: 'PreAuditReportList',
                templateUrl: 'page/rcm/pre/PreAuditReportList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditReportList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审报告详情(PreOtherReport)
            .when('/PreOtherReport/:pmodel/:action/:id/:url', {
                controller: 'PreOtherReport',
                templateUrl: 'page/rcm/pre/PreOtherReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreOtherReport.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审报告查看(PreOtherReportView)
            .when('/PreOtherReportView/:id/:url', {
                controller: 'PreOtherReportView',
                templateUrl: 'page/rcm/pre/PreOtherReportView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreOtherReportView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审报告详情(PreNormalReport)
            .when('/PreNormalReport/:pmodel/:action/:id/:url', {
                controller: 'PreNormalReport',
                templateUrl: 'page/rcm/pre/PreNormalReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreNormalReport.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审报告查看(PreNormalReportView)
            .when('/PreNormalReportView/:id/:url', {
                controller: 'PreNormalReportView',
                templateUrl: 'page/rcm/pre/PreNormalReportView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreNormalReportView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审提交决策会材料列表
            .when('/PreBiddingInfoList/:tabIndex', {
                controller: 'PreBiddingInfoList',
                templateUrl: 'page/rcm/pre/decision/PreBiddingInfoList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/decision/PreBiddingInfoList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审提交决策会材料详情
            .when('/PreBiddingInfo/:id/:url/:flag', {
                controller: 'PreBiddingInfo',
                templateUrl: 'page/rcm/pre/decision/PreBiddingInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/decision/PreBiddingInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审提交决策会材料查看
            .when('/PreBiddingInfoView/:id/:url', {
                controller: 'PreBiddingInfoView',
                templateUrl: 'page/rcm/pre/decision/PreBiddingInfoView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/decision/PreBiddingInfoView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审决策委员会材料预览
            .when('/PreBiddingInfoPreview', {
                controller: 'PreBiddingInfoPreview',
                templateUrl: 'page/rcm/pre/prePreview/PreBiddingInfoPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/prePreview/PreBiddingInfoPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审决策委员会材料预览-待决策项目审阅入口
            .when('/PreBiddingInfoPreview/:id/:url/:flag', {
                controller: 'PreBiddingInfoPreview',
                templateUrl: 'page/rcm/pre/prePreview/PreBiddingInfoPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/prePreview/PreBiddingInfoPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审参会信息列表
            .when('/PreMeetingInfoList/:tabIndex', {
                controller: 'PreMeetingInfoList',
                templateUrl: 'page/rcm/pre/PreMeetingInfoList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreMeetingInfoList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审参会信息详情
            .when('/PreMeetingInfoCreate/:id/:url', {
                controller: 'PreMeetingInfoCreate',
                templateUrl: 'page/rcm/pre/PreMeetingInfoCreate.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreMeetingInfoCreate.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审参会信息查看
            .when('/PreMeetingInfoDetailView/:id/:url', {
                controller: 'PreMeetingInfoDetailView',
                templateUrl: 'page/rcm/pre/PreMeetingInfoDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreMeetingInfoDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审结果反馈列表
            .when('/FeedbackList', {
                controller: 'FeedbackList',
                templateUrl: 'page/rcm/preAssessment/FeedbackList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/preAssessment/FeedbackList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审结果反馈详情
            .when('/AuctionResultFeedback/:action/:id/:url', {
                controller: 'AuctionResultFeedback',
                templateUrl: 'page/rcm/preAssessment/AuctionResultFeedback.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/preAssessment/AuctionResultFeedback.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审附件替换页面
            .when('/preBiddingFileInfo/:id/:url', {
                controller: 'PreBiddingFileInfo',
                templateUrl: 'page/rcm/pre/decision/PreBiddingFileInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/decision/PreBiddingFileInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************投标评审结束******************************/

            /*********************************正式评审开始******************************/
            // 正式评审申请列表
            .when('/FormalAssessmentInfoList/:tabIndex', {
                controller: 'FormalAssessmentInfoList',
                templateUrl: 'page/rcm/formalAssessment/formalAssessmentInfoList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalAssessmentInfoList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审申请详情
            .when('/formalAssessmentInfo/:id/:flag', {
                controller: 'formalAssessmentInfo',
                templateUrl: 'page/rcm/formalAssessment/formalAssessmentInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalAssessmentInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审申请查看
            .when('/FormalAssessmentDetailView/:id/:url', {
                controller: 'FormalAssessmentDetailView',
                templateUrl: 'page/rcm/formalAssessment/formalAssessmentDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalAssessmentDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审申请列表-环卫和危废
            .when('/FormalEnvirProjectList/:tabIndex', {
                controller: 'FormalEnvirProjectList',
                templateUrl: 'page/rcm/formalAssessment/formalEnvirProjectList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalEnvirProjectList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审申请详情-环卫和危废
            .when('/formalEnvirProjectInfo/:id/:flag', {
                controller: 'formalEnvirProjectInfo',
                templateUrl: 'page/rcm/formalAssessment/formalEnvirProjectInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalEnvirProjectInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审申请查看-环卫和危废
            .when('/FormalEnvirProjectDetailView/:id/:url', {
                controller: 'FormalEnvirProjectDetailView',
                templateUrl: 'page/rcm/formalAssessment/formalEnvirProjectDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalEnvirProjectDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审审批列表
            .when('/FormalAssessmentAuditList/:tabIndex', {
                controller: 'FormalAssessmentAuditList',
                templateUrl: 'page/rcm/formalAssessment/formalAssessmentAuditList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalAssessmentAuditList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //正式审批查看
            .when('/ProjectFormalReviewDetailView/:action/:id',{
                controller:'FormalAssessmentAuditDetailView',
                templateUrl:'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审审批查看
            // 正式评审报告列表
            .when('/FormalReportList_new/:tabIndex', {
                controller: 'FormalReportList_new',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/FormalReportList_new.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/FormalReportList_new.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 混合模式正式评审报告
            .when('/FormalReviewReport/:tabIndex/:action/:id/:url', {
                controller: 'FormalReviewReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/FormalReviewReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/FormalReviewReport.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 技改项目正式评审报告
            .when('/TecTransformReport/:tabIndex/:action/:id/:url', {
                controller: 'TecTransformReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/TecTransformReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/TecTransformReport.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 退出项目正式评审报告
            .when('/DropOutReport/:tabIndex/:action/:id/:url', {
                controller: 'DropOutReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/DropOutReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/DropOutReport.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 危废项目正式评审报告
            .when('/HazardousWasteReport/:tabIndex/:action/:id/:url', {
                controller: 'HazardousWasteReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/HazardousWasteReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/HazardousWasteReport.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 股权收购正式评审报告
            .when('/EquityAcquisitionReport/:tabIndex/:action/:id/:url', {
                controller: 'EquityAcquisitionReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/EquityAcquisitionReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/EquityAcquisitionReport.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 补充投资评审报告
            .when('/SupplementReport/:tabIndex/:action/:id/:url', {
                controller: 'SupplementReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/SupplementReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/SupplementReport.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其它投资评审报告
            .when('/OtherReport/:tabIndex/:action/:id/:url', {
                controller: 'OtherReport',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentReport/OtherReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/OtherReport.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其它投资评决策会材料提交文件
            .when('/OtherBidding/:tabIndex/:action/:id/:url', {
                controller: 'OtherBidding',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentBidding/OtherBidding.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentBidding/OtherBidding.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审提交决策会材料列表
            .when('/FormalBiddingInfoList/:tabIndex', {
                controller: 'FormalBiddingInfoList',
                templateUrl: 'page/rcm/formalAssessment/FormalBiddingInfoList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/FormalBiddingInfoList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审提交决策会材料详情(旧)
            .when('/FormalBiddingInfo_view/:id/:url', {
                controller: 'FormalBiddingInfo_view',
                templateUrl: 'page/rcm/formalAssessment/FormalBiddingInfo_view.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/FormalBiddingInfo_view.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审提交决策会材料详情(新)
            .when('/FormalBiddingInfo/:id/:url/:flag', {
                controller: 'FormalBiddingInfo',
                templateUrl: 'page/rcm/formalAssessment/FormalBiddingInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/FormalBiddingInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审决策委员会材料预览
            .when('/FormalBiddingInfoPreview', {
                controller: 'FormalBiddingInfoPreview',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentPreview/FormalBiddingInfoPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentPreview/FormalBiddingInfoPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审决策委员会材料预览-待决策项目审阅入口
            .when('/FormalBiddingInfoPreview/:id/:url/:flag', {
                controller: 'FormalBiddingInfoPreview',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentPreview/FormalBiddingInfoPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentPreview/FormalBiddingInfoPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审决策委员会材料PPT预览
            .when('/OtherBiddingInfoPreview', {
                controller: 'OtherBiddingInfoPreview',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentPreview/OtherBiddingInfoPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentPreview/OtherBiddingInfoPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/OtherBiddingInfoPreview/:id/:url/:flag', {
                controller: 'OtherBiddingInfoPreview',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentPreview/OtherBiddingInfoPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentPreview/OtherBiddingInfoPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            /* // 正式评审决策委员会材料预览旧版新页面
             .when('/FormalBiddingInfoPreviewOld/:id/:url/:flag', {
                 controller: 'FormalBiddingInfoPreviewOld',
                 templateUrl: 'page/rcm/formalAssessment/forAssesmentPreviewOld/FormalBiddingInfoPreviewOld.html',
                 controllerAs: 'model',
                 resolve: {
                     resolver: ['$q', '$rootScope', function($q, $rootScope) {
                         var deferred = $q.defer();
                         require(['page/rcm/formalAssessment/forAssesmentPreviewOld/FormalBiddingInfoPreviewOld.js?_v=' + _version], function() {
                             $rootScope.$apply(function() {
                                 deferred.resolve();
                                 });
                             });
                         return deferred.promise;
                         }]
                 }
             })*/

            // 正式评审附件替换页面
            .when('/formalBiddingFileInfo/:id/:url', {
                controller: 'FormalBiddingInfo',
                templateUrl: 'page/rcm/formalAssessment/formalBiddingFileInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/formalBiddingFileInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审决策委员会材料预览旧页面新版
            .when('/FormalBiddingInfoPreviewOld/:id/:url/:flag', {
                controller: 'FormalBiddingInfoPreviewOld',
                templateUrl: 'page/rcm/formalAssessment/forAssesmentPreviewOld/FormalBiddingInfoPreviewOld.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentPreviewOld/FormalBiddingInfoPreviewOld.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************正式评审结束******************************/

            /*********************************其他评审开始******************************/
            // 其他评审申请列表
            .when('/BulletinMatters/:tabIndex', {
                controller: 'BulletinMatters',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMatters.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审申请详情
            .when('/BulletinMattersDetail/:id', {
                controller: 'BulletinMattersDetail',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMattersDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审申请查看
            .when('/BulletinMattersDetailView/:id/:url', {
                controller: 'BulletinMattersDetailView',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMattersDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审审批列表
            .when('/BulletinMattersAudit/:tabIndex', {
                controller: 'BulletinMattersAudit',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMattersAudit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMattersAudit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审审批详情
            .when('/BulletinMattersAuditView/:id/:url', {
                controller: 'BulletinMattersAuditView',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMattersAuditView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMattersAudit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审会议纪要列表
            .when('/MeetingSummary/:tabIndex', {
                controller: 'MeetingSummary',
                templateUrl: 'page/rcm/bulletinMatters/MeetingSummary.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/MeetingSummary.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审会议纪要详情
            .when('/BulletinMattersDetail/:id', {
                controller: 'BulletinMattersDetail',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMattersDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审会议纪要查看
            .when('/BulletinMattersDetailView/:id/:url', {
                controller: 'BulletinMattersDetailView',
                templateUrl: 'page/rcm/bulletinMatters/BulletinMattersDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审预览决策委员会材料-混合入口
            .when('/BulletinReviewDetailPreview/:id/:url/:flag', {
                controller: 'BulletinReviewDetailPreview',
                templateUrl: 'page/rcm/bulletinMatters/preview/BulletinReviewDetailPreview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/preview/BulletinReviewDetailPreview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审附件替换页面
            .when('/bulletinReviewFileDetail/:id/:url', {
                controller: 'bulletinReviewFileDetail',
                templateUrl: 'page/rcm/bulletinMatters/BulletinReviewFileDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinReviewFileDetail.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************其他评审结束******************************/

            /*********************************其余业务相关开始******************************/
            /*********************************决策通知书开始******************************/
            // 决策通知书草拟
            .when('/NoticeDecisionDraftList/:tabIndex', {
                controller: 'NoticeDecisionDraftList',
                templateUrl: 'page/rcm/noticeofdecision/noticeDecisionDraftList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionDraftList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策通知书草拟详情
            .when('/NoticeDecisionDraftDetail/:action/:id', {
                controller: 'NoticeDecisionDraftDetail',
                templateUrl: 'page/rcm/noticeofdecision/noticeDecisionDraftDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionDraftDetail.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策通知书草拟查看
            .when('/NoticeDecisionDraftDetailView/:action/:id/:url', {
                controller: 'NoticeDecisionDraftDetailView',
                templateUrl: 'page/rcm/noticeofdecision/noticeDecisionDraftDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionDraftDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策通知书确认列表
            .when('/NoticeDecisionConfirmList/:tabIndex', {
                controller: 'NoticeDecisionConfirmList',
                templateUrl: 'page/rcm/noticeofdecision/noticeDecisionConfirmList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionConfirmList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策通知书确认详情
            .when('/NoticeDecisionDraftCompleteDetail/:action/:id/:url', {
                controller: 'NoticeDecisionDraftCompleteDetail',
                templateUrl: 'page/rcm/noticeofdecision/noticeDecisionDraftCompleteDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionDraftCompleteDetail.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策通知书确认查看
            .when('/NoticeDecisionConfirmDetailView/:tabIndex/:id/:url', {
                controller: 'NoticeDecisionConfirmDetailView',
                templateUrl: 'page/rcm/noticeofdecision/noticeDecisionConfirmDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionConfirmDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************决策通知书结束******************************/

            /*********************************会议相关开始******************************/
            // 参会信息列表
            .when('/ConferenceInformation/:tabIndex', {
                controller: 'ConferenceInformation',
                templateUrl: 'page/rcm/meeting/ConferenceInformation/ConferenceInformation.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/ConferenceInformation/ConferenceInformation.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 参会信息详情
            .when('/ConferenceInformationCreate/:id/:url/:flag', {
                controller: 'ConferenceInformationCreate',
                templateUrl: 'page/rcm/meeting/ConferenceInformation/ConferenceInformationCreate.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/ConferenceInformation/ConferenceInformationCreate.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 参会信息查看
            .when('/ConferenceInformationDetailView/:id/:url/:flag', {
                controller: 'ConferenceInformationDetailView',
                templateUrl: 'page/rcm/meeting/ConferenceInformation/ConferenceInformationDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/ConferenceInformation/ConferenceInformationDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 拟上会通知列表
            .when('/preliminaryNoticeList', {
                controller: 'PreliminaryNoticeList',
                templateUrl: 'page/rcm/meeting/preliminaryNoticeList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 拟上会通知详情
            .when('/preliminaryNoticeInfo/:action/:id/:url', {
                controller: 'PreliminaryNoticeInfo',
                templateUrl: 'page/rcm/meeting/preliminaryNoticeInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 拟上会通知查看
            .when('/preliminaryNoticeInfoView/:id/:url', {
                controller: 'PreliminaryNoticeInfoView',
                templateUrl: 'page/rcm/meeting/preliminaryNoticeInfoView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeInfoView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //
            .when('/meeting/MeetingArrangement/:tabIndex', {
                controller: 'MeetingArrangement',
                templateUrl: 'page/rcm/meeting/MeetingArrangement.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/MeetingArrangement.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 会议项目提交上会
            .when('/meeting/MeetingSubmit', {
                controller: 'MeetingSubmit',
                templateUrl: 'page/rcm/meeting/MeetingSubmit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/MeetingSubmit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 会议已提交的项目
            .when('/MeetingProjectReviewList/:meetingId/:url', {
                controller: 'MeetingProjectReviewList',
                templateUrl: 'page/rcm/meeting/MeetingProjectReviewList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/MeetingProjectReviewList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 修改已提交的会议项目信息
            .when('/MeetingUpdate/:meetingId', {
                controller: 'MeetingUpdate',
                templateUrl: 'page/rcm/meeting/MeetingUpdate.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/MeetingUpdate.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************会议相关结束******************************/


            /*********************************项目看板开始******************************/
            // 投标评审列表
            .when('/preReportBoardList/:tabIndex', {
                controller: 'preReportBoardList',
                templateUrl: 'page/rcm/projectBoard/preReportBoardList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/preReportBoardList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审详情
            .when('/preReportBoardListMore/:stage/:wf_state/:url', {
                controller: 'preReportBoardListMore',
                templateUrl: 'page/rcm/projectBoard/preReportBoardListMore.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/preReportBoardListMore.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审列表
            .when('/formalReportBoardList/:tabIndex', {
                controller: 'formalReportBoardList',
                templateUrl: 'page/rcm/projectBoard/formalReportBoardList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/formalReportBoardList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审详情
            .when('/formalReportBoardListMore/:stage/:wf_state/:url', {
                controller: 'formalReportBoardListMore',
                templateUrl: 'page/rcm/projectBoard/formalReportBoardListMore.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/formalReportBoardListMore.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审审批
            .when('/FormalAssessmentAuditDetailView/:id/:url', {
                controller: 'FormalAssessmentAuditDetailView',
                templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/FormalAssessmentAuditDetailView/:id/:taskMark/:url', {
                controller: 'FormalAssessmentAuditDetailView',
                templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            .when('/FormalAssessmentAuditDetailView/:id/:taskMark/:url', {
                controller: 'FormalAssessmentAuditDetailView',
                templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审列表
            .when('/bulletinReportBoardList/:tabIndex', {
                controller: 'bulletinReportBoardList',
                templateUrl: 'page/rcm/projectBoard/bulletinReportBoardList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/bulletinReportBoardList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审详情
            .when('/bulletinReportBoardListMore/:stage/:wf_state/:url', {
                controller: 'bulletinReportBoardListMore',
                templateUrl: 'page/rcm/projectBoard/bulletinReportBoardListMore.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/bulletinReportBoardListMore.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 项目看板（新）
            .when('/projectBoardList/:url', {
                controller: 'projectBoardList',
                templateUrl: 'page/rcm/projectBoard/projectBoardList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/projectBoardList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 项目看板 - 首页项目查询
            .when('/projectBoardList/:projectName/:url', {
                controller: 'projectBoardList',
                templateUrl: 'page/rcm/projectBoard/projectBoardList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/projectBoardList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 资料填写面板（新）
            .when('/fillMaterialList/:tabIndex/:url', {
                controller: 'fillMaterialList',
                templateUrl: 'page/rcm/fillMaterial/FillMaterialList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/fillMaterial/FillMaterialList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审项目信息查看（新）
            .when('/projectInfoAllBoardView/:id/:url', {
                controller: 'ProjectInfoAllBoardView',
                templateUrl: 'page/rcm/projectBoard/projectInfoAllBoardView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/projectInfoAllBoardView.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审项目信息查看（新）
            .when('/projectPreInfoAllBoardView/:id/:url', {
                controller: 'ProjectPreInfoAllBoardView',
                templateUrl: 'page/rcm/projectBoard/projectPreInfoAllBoardView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/projectPreInfoAllBoardView.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审项目信息查看（新）
            .when('/projectBulletinInfoAllBoardView/:id/:url', {
                controller: 'projectBulletinInfoAllBoardView',
                templateUrl: 'page/rcm/projectBoard/projectBulletinInfoAllBoardView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/projectBulletinInfoAllBoardView.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************项目看板结束******************************/

            /*********************************决策会开始******************************/
            //决策会管理
            .when('/MeetingManageList', {
                controller: 'MeetingManageList',
                templateUrl: 'page/rcm/formalAssessment/MeetingManageList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingManageList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策会表决
            .when('/MeetingVote/:url', {
                controller: 'MeetingVote',
                templateUrl: 'page/rcm/formalAssessment/MeetingVote.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVote.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/MeetingVoteWait/:decisionId/:url', {
                controller: 'MeetingVoteWait',
                templateUrl: 'page/rcm/formalAssessment/MeetingVoteWait.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVoteWait.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/MeetingVoteWait/:decisionId/:url/:isAdmin', {
                controller: 'MeetingVoteWait',
                templateUrl: 'page/rcm/formalAssessment/MeetingVoteWait.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVoteWait.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策会表决结果
            .when('/MeetingVoteResult/:decisionId/:url', {
                controller: 'MeetingVoteResult',
                templateUrl: 'page/rcm/formalAssessment/MeetingVoteResult.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVoteResult.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 待决策项目审阅
            .when('/waitDecisionReviewList', {
                controller: 'WaitDecisionReviewList',
                templateUrl: 'page/rcm/decision/waitDecisionReviewList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/decision/waitDecisionReviewList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 今日决策会看板
            .when('/DecisionOverview', {
                controller: 'DecisionOverview',
                templateUrl: 'page/rcm/formalAssessment/DecisionOverview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/DecisionOverview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 历史决策会
            .when('/historyDecisionReviewList/:url', {
                controller: 'HistoryDecisionReviewList',
                templateUrl: 'page/rcm/decision/historyDecisionReviewList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/decision/historyDecisionReviewList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策委员会审阅-正式评审
            .when('/FormalBiddingInfoReview/:id/:url', {
                controller: 'FormalBiddingInfoReview',
                templateUrl: 'page/rcm/formalAssessment/FormalBiddingInfoReview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/FormalBiddingInfoReview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策委员会审阅-投标评审
            .when('/PreBiddingInfoMeetingReview/:id/:url', {
                controller: 'PreBiddingInfoMeetingReview',
                templateUrl: 'page/rcm/pre/decision/PreBiddingInfoMeetingReview.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/decision/PreBiddingInfoMeetingReview.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************决策会结束******************************/

            // 导出台账菜单
            .when('/exportProjetInfo/:tabIndex', {
                controller: 'exportProjetInfo',
                templateUrl: 'page/rcm/deptwork/exportProjetInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/deptwork/exportProjetInfo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审项目信息查看
            .when('/projectInfoAllView/:id/:url', {
                controller: 'ProjectInfoAllView',
                templateUrl: 'page/rcm/deptwork/projectInfoAllView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/deptwork/projectInfoAllView.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 投标评审项目信息查看
            .when('/projectPreInfoAllView/:id/:url', {
                controller: 'ProjectPreInfoAllView',
                templateUrl: 'page/rcm/deptwork/projectPreInfoAllView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/deptwork/projectPreInfoAllView.js?_v=' + _version, '../javascripts/util/common.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 其他评审项目信息查看
            .when('/projectBulletinInfoAllView/:id/:url', {
                controller: 'projectBulletinInfoAllView',
                templateUrl: 'page/rcm/bulletinMatters/projectBulletinInfoAllView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/projectBulletinInfoAllView.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 业务单位承诺列表
            .when('/BusinessUnitCommitList', {
                controller: 'BusinessUnitCommitList',
                templateUrl: 'page/rcm/formalAssessment/BusinessUnitCommitList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/BusinessUnitCommitList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 业务单位承诺详情
            .when('/BusinessUnitCommit/:action/:id/:url', {
                controller: 'BusinessUnitCommit',
                templateUrl: 'page/rcm/formalAssessment/BusinessUnitCommit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/BusinessUnitCommit.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审决策会附件替换
            .when('/updateDecisionFileList', {
                controller: 'updateDecisionFileList',
                templateUrl: 'page/rcm/formalAssessment/updateDecisionFileList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/updateDecisionFileList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            /*********************************其余业务相关结束******************************/
            /*********************************业务页面结束******************************/
            // demo
            .when('/demo', {
                controller: 'demo',
                templateUrl: 'page/sys/demo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/sys/demo.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/message/share/:id', {
                controller: 'shareMessageCtrl',
                templateUrl: 'page/sys/common/message_share.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/sys/common/message_share.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 决策会约会列表
            .when('/AppointmentMeeTingManage', {
                controller: 'AppointmentMeeTingManage',
                templateUrl: 'page/rcm/AppointmentMeeTingManage/AppointmentMeeTingManage.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/AppointmentMeeTingManage/AppointmentMeeTingManage.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            .when('/BusinessModelList', {
                controller: 'BusinessModelList',
                templateUrl: 'page/rcm/BasicSetting/BusinessModelList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/BusinessModelList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //:action/:uuid
            .when('/ManageAttachmentList/:yuuid/:ybusiness_name/:business_type', {
                controller: 'ManageAttachmentList',
                templateUrl: 'page/rcm/BasicSetting/ManageAttachmentList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ManageAttachmentList.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/ListBusiness/:UUID/:BUSINESS_NAME/:BUSINESS_TYPE', {
                controller: 'ListBusiness',
                templateUrl: 'page/rcm/BasicSetting/ListBusiness.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ListBusiness.js?_v=' + _version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/projectReviewList/:projectName/:url',{
                controller:'projectReviewList',
                templateUrl:'page/rcm/decision/projectReviewList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/decision/projectReviewList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            // 待阅
            .when('/MyReading/:url', {
                controller: 'MyReadingCtrl',
                templateUrl: 'page/rcm/taskManagement/MyReading.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/taskManagement/MyReadManager.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //已阅
            .when('/MyRead/:url',{
                controller:'MyReadCtrl',
                templateUrl:'page/rcm/taskManagement/MyRead.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/taskManagement/MyReadManager.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/report',{
                controller:'reportCtrl',
                templateUrl:'page/sys/report/report.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/sys/report/report.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/initfile',{
                controller:'initFileCtrl',
                templateUrl:'page/sys/initfile/initfile.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/sys/initfile/initfile.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            // 正式评审流程打回基础法务编辑页面
            .when('/FormalAssessmentAuditDetailLegalView/:id/:url',{
                controller:'FormalAssessmentAuditDetailLegalView',
                templateUrl:'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView_legal.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView_legal.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            // 投标评审流程打回基础法务编辑页面
            .when('/PreAuditDetailLegalView/:id/:url',{
                controller:'PreAuditDetailLegalView',
                templateUrl:'page/rcm/pre/PreAuditDetailView_legal.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditDetailView_legal.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //决策通知书
            .when('/NoticeOfDecision/:action/:id',{
                controller:'NoticeDecisionAuditView',
                templateUrl:'page/rcm/noticeofdecision/noticeDecisionAuditView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/noticeofdecision/noticeDecisionAuditView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
    }
]);

function resolver($q, $rootScope, dependencies) {
    var deferred = $q.defer();
    require(dependencies, function () {
        $rootScope.$apply(function () {
            deferred.resolve();
        });
    });
    return deferred.promise;
}