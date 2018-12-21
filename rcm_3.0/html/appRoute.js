
var $routeData;
var _version = 2;
ctmApp.config(["$routeProvider", "$controllerProvider", "$compileProvider",
    function ($routeProvider, $controllerProvider, $compileProvider) {

        $routeData = $routeProvider;
        ctmApp.register =
        {
            controller: $controllerProvider.register,
            directive: $compileProvider.directive
        };
    }]);
ctmApp.run(['$route', '$http', '$rootScope','$location','$interval',
    function ($route, $http, $rootScope,$location,$interval) {
		$rootScope.$on("$routeChangeStart", function(event, next, current) {
			//---------------------------------------------------------------------------
			//	是否开启定时器:
			//  1:如果是表决日期，且今日还有项目未表决完，则开启定时器
			//	2018-02-27
			//---------------------------------------------------------------------------
		    if($rootScope.hasRole('DECISION_LEADERS') && meetDeciInte == null){
			    $http({
					method:'post',  
				    url:srvUrl+"decision/isTodayDecision.do"
				}).success(function(data){
					if(data.success && meetDeciInte == null){
						meetDeciInte = $interval(function(){
							$rootScope.meetingMonitor();
						},meetDeciInteTime);
					}
				});
		    }
			if(current == null || current.scope == null || current.params == null || current.params.url != null){
				 return;
			 }
			 if(!(next != null && next.params != null && next.params.url != null)){
				 current.scope.$parent.paginationConf.queryObj = {};
				 current.scope.$parent.paginationConf.currentPage = 1;
				 current.scope.$parent.paginationConf.itemsPerPage = 10;
				 current.scope.$parent.paginationConfes.itemsPerPage = 10;
				 current.scope.$parent.paginationConfes.queryObj = {};
				 current.scope.$parent.paginationConfes.currentPage = 1;
			 }
		});
        $routeData
			//决策会查看列表
        	//面板--决策会table
	        .when('/DecisionOverviewList/:pertainareaId/:state', {
	        	controller:'DecisionOverviewList',
	        	templateUrl:'page/rcm/formalAssessment/DecisionOverviewList.html',
	        	controllerAs:'model',
	        	resolve:{
	        		resolver:['$q','$rootScope',function($q,$rootScope){
	        			var deferred = $q.defer();
	        			require(['page/rcm/formalAssessment/DecisionOverviewList.js?_v='+_version],function(){
	        				$rootScope.$apply(function(){
	        					deferred.resolve();
	        				});
	        			});
	        			return deferred.promise;
	        		}]
	        	}
	        })
            .when('/', {
                controller:'IndividualTable',
                templateUrl:'page/rcm/homePage/IndividualTable.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/IndividualTable.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/myProjectList/:url', {
                controller:'myProjectList',
                templateUrl:'page/rcm/homePage/myProjectList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/myProjectList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/myProjectInfo/:businessId/:url', {
                controller:'myProjectInfo',
                templateUrl:'page/rcm/homePage/myProjectInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/myProjectInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			.when('/DecisionOverview', {
                controller:'DecisionOverview',
                templateUrl:'page/rcm/formalAssessment/DecisionOverview.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/DecisionOverview.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/SysUserList/:orgId', {
                controller: 'SysUserList',
                templateUrl: 'page/fnd/user/SysUserList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/user/SysUserList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/SysUserAdd/:action/:uuid', {
                controller: 'SysUserAdd',
                templateUrl: 'page/fnd/user/SysUserAdd.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/user/SysUserAdd.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/sysFunAdd/:action/:func_id', {
                controller: 'sysFunAdd',
                templateUrl: 'page/fnd/sysFun/sysFunAdd.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/sysFun/sysFunAdd.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
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
            })
            .when('/SysRoleList', {
                controller: 'SysRoleList',
                templateUrl: 'page/fnd/role/sysRoleList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/sysRoleList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/SysRoleInfo/:action/:roleId', {
                controller: 'SysRoleInfo',
                templateUrl: 'page/fnd/role/SysRoleInfo.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/SysRoleInfo.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/SysRoleView/:roleId/:url', {
                controller: 'SysRoleView',
                templateUrl: 'page/fnd/role/SysRoleView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/SysRoleView.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/GroupAdd/:action/:uuid', {
                controller: 'GroupAdd',
                templateUrl: 'page/fnd/group/GroupAdd.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GroupAdd.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/RoleAndFun/:roleId/:roleCode/:url', {
                controller: 'RoleAndFun',
                templateUrl: 'page/fnd/role/RoleAndFun.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/RoleAndFun.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/RoleAndUser/:roleId/:roleCode/:url', {
                controller: 'RoleAndUser',
                templateUrl: 'page/fnd/role/RoleAndUser.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/role/RoleAndUser.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//菜单管理
            .when('/sysFunList/:func_id', {
                controller: 'sysFunList',
                templateUrl: 'page/fnd/sysFun/sysFunList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/sysFun/sysFunList.js?_v='+_version,'../javascripts/util/common.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            .when('/GroupList', {
                controller: 'GroupList',
                templateUrl: 'page/fnd/group/GroupList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GroupList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            .when('/User/:action/:userID', {
                controller: 'User',
                templateUrl: 'page/fnd/user/User.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/user/User.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			.when('/PreInfoList/:tabIndex', {
                controller: 'PreInfoList',
                templateUrl: 'page/rcm/pre/PreInfoList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreInfoList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/ProjectPreReviewReadOnly', {
                controller: 'ProjectPreReviewReadOnly',
                templateUrl: 'page/rcm/preAssessment/ProjectPreReviewReadOnly.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/preAssessment/ProjectPreReview.js?_v='+_version], function () {
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
                        require(['page/rcm/taskManagement/TaskManagement.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//已办任务
			.when('/CompletedTask',{
				controller:'CompletedTask',
				templateUrl:'page/rcm/taskManagement/CompletedTask.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/taskManagement/TaskManagement.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//已完成任务
			.when('/OverTask/:url',{
				controller:'OverTask',
				templateUrl:'page/rcm/taskManagement/OverTask.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/taskManagement/TaskManagement.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//通知列表
			.when('/NoticeList',{
				controller:'NoticeList',
				templateUrl:'page/rcm/remindManagement/NoticeList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/remindManagement/NoticeList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//预警列表
			.when('/WarningList',{
				controller:'WarningList',
				templateUrl:'page/rcm/remindManagement/WarningList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/remindManagement/WarningList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

            //投资预评审详情
            .when('/ProjectPreReviewDetail/:action/:id', {
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
			//投资预评审报告
			.when('/PreReviewReport/:action/:id',{
				controller:'PreAuditDetailView',
				templateUrl:'page/rcm/pre/PreAuditDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //单位负责人审核
            .when('/companyHeaderApprove/:id',{
                controller:'PreAuditDetailView',
                templateUrl:'page/rcm/pre/PreAuditDetailView.html',
                controllerAs:'model',
                resolve:{
                    resolver: ['$q', '$rootScope', 'Upload','$timeout',function ($q, $rootScope,Upload,$timeout) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //一级业务部门审核
            .when('/businessHeaderApprove/:id',{
                controller:'PreAuditDetailView',
                templateUrl:'page/rcm/pre/PreAuditDetailView.html',
                controllerAs:'model',
                resolve:{
                    resolver: ['$q', '$rootScope', 'Upload','$timeout',function ($q, $rootScope,Upload,$timeout) {
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//项目任务分配
			.when('/ReviewLeader/:id',{
				controller:'PreAuditDetailView',
				templateUrl:'page/rcm/pre/PreAuditDetailView.html',
				controllerAs:'model',
				resolve:{
                    resolver: ['$q', '$rootScope', 'Upload','$timeout',function ($q, $rootScope,Upload,$timeout) {
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
			//填写初步评审意见
			.when('/PreReviewComments/:action/:id',{
				controller:'PreAuditDetailView',
				templateUrl:'page/rcm/pre/PreAuditDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//初步评审意见反馈
			.when('/CommentsFeedback/:action/:id',{
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
			//专业评审意见
			.when('/ProfessionalReviewComments/:id',{
				controller:'ProfessionalReviewComments',
				templateUrl:'page/rcm/preAssessment/ProfessionalReviewComments.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/ProjectPreReview.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//专业评审意见汇总
			.when('/SummaryOfComments/:action/:id',{
				controller:'SummaryOfComments',
				templateUrl:'page/rcm/preAssessment/SummaryOfComments.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/SummaryOfComments.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //评审负责人确认
            .when('/PreReviewConfirm/:id',{
                controller:'PreReviewConfirm',
                templateUrl:'page/rcm/preAssessment/PreReviewConfirm.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/preAssessment/ProjectPreReview.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//投标项目资料
			.when('/BiddingInfo/:id',{
				controller:'BiddingInfo',
				templateUrl:'page/rcm/preAssessment/BiddingInfo.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/ProjectPreReview.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//投标结果反馈
			.when('/AuctionResultFeedback/:action/:id/:url',{
				controller:'AuctionResultFeedback',
				templateUrl:'page/rcm/preAssessment/AuctionResultFeedback.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/AuctionResultFeedback.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//预评审报告列表
			.when('/ReportList',{
				controller:'ReportList',
				templateUrl:'page/rcm/preAssessment/ReportList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/ReportList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策委员会审阅列表
			.when('/ReviewList',{
				controller:'ReviewList',
				templateUrl:'page/rcm/preAssessment/ReviewList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/ReviewList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//结果反馈列表
			.when('/FeedbackList',{
				controller:'FeedbackList',
				templateUrl:'page/rcm/preAssessment/FeedbackList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/FeedbackList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

            .when('/AuctionResultFeedvack',{
                controller:'AuctionResultFeedvack',
                templateUrl:'page/rcm/preAssessment/AuctionResultFeedvack.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/preAssessment/AuctionResultFeedvack.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			.when('/BiddingInfoReview/:id',{
				controller:'BiddingInfoReview',
				templateUrl:'page/rcm/preAssessment/BiddingInfoReview.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/preAssessment/BiddingInfoReview.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//正式评审管理列表
			.when('/ProjectFormalReviewList',{
				controller:'ProjectFormalReviewList',
				templateUrl:'page/rcm/formalAssessment/ProjectFormalReviewList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/ProjectFormalReviewList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            .when('/ProjectFormalReviewListReadOnly',{
                controller:'ProjectFormalReviewListReadOnly',
                templateUrl:'page/rcm/formalAssessment/ProjectFormalReviewListReadOnly.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/ProjectFormalReviewListReadOnly.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/ProjectPreReviewListReadOnly',{
            	controller:'ProjectPreReviewListReadOnly',
            	templateUrl:'page/rcm/pre/ProjectPreReviewListReadOnly.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/ProjectPreReviewListReadOnly.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
			//正式评审详情
//          controller:'ProjectFormalReviewDetail',
//			templateUrl:'page/rcm/formalAssessment/ProjectFormalReviewDetail.html',
//			page/rcm/formalAssessment/FormalAssessment.js
			.when('/ProjectFormalReviewDetail/:action/:id',{
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
            //正式审批查看-领导
            .when('/projectInfoAllView/:id/:url',{
                controller:'ProjectInfoAllView',
                templateUrl:'page/rcm/deptwork/projectInfoAllView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/deptwork/projectInfoAllView.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //预审批查看-部门工作情况
            .when('/projectFormalInfoAllView/:id/:url',{
                controller:'ProjectFormalInfoAllView',
                templateUrl:'page/rcm/deptwork/projectFormalInfoAllView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/deptwork/projectFormalInfoAllView.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //js:page/rcm/formalAssessment/FormalAssessment.js
            //controller:ProjectFormalReviewView
            //正式评审查看，用于起草查看评审报告
            .when('/ProjectFormalReviewView/:action/:id',{
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
            //預评审查看，用于起草查看评审报告
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
			//任务分配
			.when('/TaskAssignment/:id',{
				controller:'FormalAssessmentAuditDetailView',
				templateUrl:'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//评审负责人初步评审意见
			.when('/PreReviewCommentsR/:action/:id',{
				controller:'FormalAssessmentAuditDetailView',
				templateUrl:'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//法律评审负责人初步评审意见
			.when('/PreReviewCommentsL/:action/:id',{
				controller:'FormalAssessmentAuditDetailView',
				templateUrl:'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//投资经理反馈
			.when('/ManagerFeedback/:action/:id',{
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
			//评审小组专业意见
			.when('/ReviewTeamComments/:id',{
				controller:'FormalAssessmentAuditDetailView',
				templateUrl:'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//基层法务人员反馈
			.when('/PrimaryLegalFeedback/:id',{
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
			//正式评审报告
			.when('/FormalReportList_new/:tabIndex',{
				controller:'FormalReportList_new',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/FormalReportList_new.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/FormalReportList_new.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//正式评审报告列表
			.when('/FormalReportList',{
				controller:'FormalReportList',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/FormalReportList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/FormalReportList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//混合模式正式评审报告
			.when('/FormalReviewReport/:tabIndex/:action/:id/:url',{
				controller:'FormalReviewReport',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/FormalReviewReport.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/FormalReviewReport.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//技改项目正式评审报告
			.when('/TecTransformReport/:tabIndex/:action/:id/:url',{
				controller:'TecTransformReport',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/TecTransformReport.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/TecTransformReport.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//退出项目正式评审报告
			.when('/DropOutReport/:tabIndex/:action/:id/:url',{
				controller:'DropOutReport',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/DropOutReport.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/DropOutReport.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//危废项目正式评审报告
			.when('/HazardousWasteReport/:tabIndex/:action/:id/:url',{
				controller:'HazardousWasteReport',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/HazardousWasteReport.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/HazardousWasteReport.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//股权收购正式评审报告
			.when('/EquityAcquisitionReport/:tabIndex/:action/:id/:url',{
				controller:'EquityAcquisitionReport',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/EquityAcquisitionReport.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/EquityAcquisitionReport.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//补充投资评审报告 
			.when('/SupplementReport/:tabIndex/:action/:id/:url',{
				controller:'SupplementReport',
				templateUrl:'page/rcm/formalAssessment/forAssesmentReport/SupplementReport.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/SupplementReport.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //其它投资评审报告
            .when('/OtherReport/:tabIndex/:action/:id/:url',{
                controller:'OtherReport',
                templateUrl:'page/rcm/formalAssessment/forAssesmentReport/OtherReport.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/forAssesmentReport/OtherReport.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//决策委员会审阅列表
			.when('/FormalReviewList/:tabNum',{
				controller:'FormalReviewList',
				templateUrl:'page/rcm/formalAssessment/FormalReviewList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/FormalReviewList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //决策委员会审阅列表
            .when('/FormalReviewListToday/:url',{
                controller:'FormalReviewListToday',
                templateUrl:'page/rcm/formalAssessment/FormalReviewListToday.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/FormalReviewListToday.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//项目决策会意见汇总
			.when('/DecisionViewsList',{
				controller:'FormalReviewList',
				templateUrl:'page/rcm/formalAssessment/DecisionViewsList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						//FormalReviewList
						require(['page/rcm/formalAssessment/FormalReviewList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//项目决策会意见汇总
			.when('/DecisionViewDetail/:id',{
				controller:'DecisionViewDetail',
				templateUrl:'page/rcm/formalAssessment/DecisionViewDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/DecisionViewsList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//参会信息
	/*		.when('/MeetingInfo',{
				controller:'MeetingInfo',
				templateUrl:'page/rcm/formalAssessment/MeetingInfo.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/MeetingInfo.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
*/
			//参会信息列表
			.when('/MeetingInfoList',{
				controller:'MeetingInfoList',
				templateUrl:'page/rcm/formalAssessment/MeetingInfoList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/MeetingInfoList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//正式评审参会信息详情
			.when('/ConferenceInformationDetailView/:id/:url',{
				controller:'ConferenceInformationDetailView',
				templateUrl:'page/rcm/newFormalAssessment/ConferenceInformationDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/ConferenceInformationDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//项目参会详细信息
			.when('/MeetingInfoDetail/:action/:id',{
				controller:'MeetingInfoDetail',
				templateUrl:'page/rcm/formalAssessment/MeetingInfoDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/MeetingInfoDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//会议申请单
			.when('/MeetingApply/:id',{
				controller:'MeetingApply',
				templateUrl:'page/rcm/formalAssessment/MeetingApply.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/MeetingApply.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            .when('/MeetingApplyNotice/:id',{
                controller:'MeetingApplyNotice',
                templateUrl:'page/rcm/formalAssessment/MeetingApplyNotice.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingApplyNotice.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //其他决策事项-统计
             .when('/bulletinStatistics', {
                controller: 'BulletinStatistics',
                templateUrl: 'page/rcm/preAssessment/BulletinStatistics.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/preAssessment/BulletinStatistics.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //投标评审决策会委员材料
             //tabIndex：0待提交1已提交
            .when('/PreBiddingInfoList/:tabIndex',{
				controller:'PreBiddingInfoList',
				templateUrl:'page/rcm/pre/decision/PreBiddingInfoList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/decision/PreBiddingInfoList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			 //投标评审决策会委员材料-编辑
            .when('/PreBiddingInfo/:id/:url',{
				controller:'PreBiddingInfo',
				templateUrl:'page/rcm/pre/decision/PreBiddingInfo.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/decision/PreBiddingInfo.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			 //投标评审决策会委员材料-评审
            .when('/PreBiddingInfoReview/:id/:url',{
				controller:'PreBiddingInfoReview',
				templateUrl:'page/rcm/pre/decision/PreBiddingInfoReview.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/decision/PreBiddingInfoReview.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			 //投标评审决策会委员材料-浏览
            .when('/PreBiddingInfoView/:id/:url',{
				controller:'PreBiddingInfoView',
				templateUrl:'page/rcm/pre/decision/PreBiddingInfoView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/decision/PreBiddingInfoView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //会议安排
            //tabIndex：0待办1已办
			.when('/meeting/MeetingArrangement/:tabIndex',{
				controller:'MeetingArrangement',
				templateUrl:'page/rcm/meeting/MeetingArrangement.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/meeting/MeetingArrangement.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//会议项目提交上会
			.when('/meeting/MeetingSubmit',{
				controller:'MeetingSubmit',
				templateUrl:'page/rcm/meeting/MeetingSubmit.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/meeting/MeetingSubmit.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//修改已提交的会议项目信息
			.when('/MeetingUpdate/:meetingId',{
				controller:'MeetingUpdate',
				templateUrl:'page/rcm/meeting/MeetingUpdate.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/meeting/MeetingUpdate.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})			
			//会议已提交的项目
			.when('/MeetingProjectReviewList/:meetingId/:url',{
				controller:'MeetingProjectReviewList',
				templateUrl:'page/rcm/meeting/MeetingProjectReviewList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/meeting/MeetingProjectReviewList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //决策会管理
			.when('/MeetingManageList',{
				controller:'MeetingManageList',
				templateUrl:'page/rcm/formalAssessment/MeetingManageList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/MeetingManageList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策会表决
            .when('/MeetingVote/:url',{
                controller:'MeetingVote',
                templateUrl:'page/rcm/formalAssessment/MeetingVote.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVote.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/MeetingVoteWait/:decisionId/:url',{
                controller:'MeetingVoteWait',
                templateUrl:'page/rcm/formalAssessment/MeetingVoteWait.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVoteWait.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/MeetingVoteWait/:decisionId/:url/:isAdmin',{
                controller:'MeetingVoteWait',
                templateUrl:'page/rcm/formalAssessment/MeetingVoteWait.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVoteWait.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/MeetingVoteResult/:decisionId/:url',{
                controller:'MeetingVoteResult',
                templateUrl:'page/rcm/formalAssessment/MeetingVoteResult.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/MeetingVoteResult.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //待决策项目审阅
            .when('/waitDecisionReviewList',{
                controller:'WaitDecisionReviewList',
                templateUrl:'page/rcm/decision/waitDecisionReviewList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/decision/waitDecisionReviewList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
             //历史决策会
            .when('/historyDecisionReviewList/:url',{
                controller:'HistoryDecisionReviewList',
                templateUrl:'page/rcm/decision/historyDecisionReviewList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/decision/historyDecisionReviewList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //拟上会通知列表(管理)
            .when('/preliminaryNoticeList',{
                controller:'PreliminaryNoticeList',
                templateUrl:'page/rcm/meeting/preliminaryNoticeList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //拟上会通知查看详情(管理)
            .when('/preliminaryNoticeInfoView/:id/:url',{
                controller:'PreliminaryNoticeInfoView',
                templateUrl:'page/rcm/meeting/preliminaryNoticeInfoView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeInfoView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
             //拟上会通知查看添加|修改(管理)
            .when('/preliminaryNoticeInfo/:action/:id/:url',{
                controller:'PreliminaryNoticeInfo',
                templateUrl:'page/rcm/meeting/preliminaryNoticeInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //拟上会通知审阅列表
            .when('/preliminaryNoticeReviewList/:url',{
                controller:'PreliminaryNoticeReviewList',
                templateUrl:'page/rcm/meeting/preliminaryNoticeReviewList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeReviewList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
             //拟上会通知审阅详情
            .when('/preliminaryNoticeReviewInfo/:id/:url',{
                controller:'PreliminaryNoticeReviewInfo',
                templateUrl:'page/rcm/meeting/preliminaryNoticeReviewInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/meeting/preliminaryNoticeReviewInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
             //投标评审会议浏览
            .when('/PreBiddingInfoMeetingReview/:id/:url',{
                controller:'PreBiddingInfoMeetingReview',
                templateUrl:'page/rcm/pre/decision/PreBiddingInfoMeetingReview.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/pre/decision/PreBiddingInfoMeetingReview.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //正式评审提交决策委员会材料待提交及已提交列表
            .when('/FormalBiddingInfoList/:tabIndex',{
				controller:'FormalBiddingInfoList',
				templateUrl:'page/rcm/formalAssessment/FormalBiddingInfoList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/FormalBiddingInfoList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//正式评审提交决策委员会材料
			.when('/FormalBiddingInfo/:id/:url/:flag',{
				controller:'FormalBiddingInfo',
				templateUrl:'page/rcm/formalAssessment/FormalBiddingInfo.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/FormalBiddingInfo.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//正式评审提交决策委员会材料
			.when('/FormalBiddingInfo_view/:id/:url',{
				controller:'FormalBiddingInfo_view',
				templateUrl:'page/rcm/formalAssessment/FormalBiddingInfo_view.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/FormalBiddingInfo_view.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //正式评审预览决策委员会材料
            .when('/FormalBiddingInfoPreview',{
                controller:'FormalBiddingInfoPreview',
                templateUrl:'page/sys/preview/FormalBiddingInfoPreview.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/sys/preview/FormalBiddingInfoPreview.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//决策委员会审阅
			.when('/FormalBiddingInfoReview/:id/:url',{
				controller:'FormalBiddingInfoReview',
				templateUrl:'page/rcm/formalAssessment/FormalBiddingInfoReview.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/FormalBiddingInfoReview.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//投资决策会意见
			.when('/DecisionComments',{
				controller:'DecisionComments',
				templateUrl:'page/rcm/formalAssessment/DecisionComments.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/DecisionViewsList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//新版正式评审模块 基本信息新增
			//author yaphet
			.when('/FormalAssessmentDetailCreate', {
				controller: 'FormalAssessmentDetailCreate',
				templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentDetailCreate.html',
				controllerAs: 'model',
				resolve: {
					resolver: ['$q', '$rootScope', function ($q, $rootScope) {
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/formalAssessmentDetailCreate.js?_v='+_version,'../javascripts/util/common.js?_v='+_version], function () {
							$rootScope.$apply(function () {
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//新版正式评审模块 基本信息展示只读
			//author yaphet
            .when('/FormalAssessmentDetailView/:id/:url', {
                controller: 'FormalAssessmentDetailView',
                templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentDetailView.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/formalAssessmentDetailView.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //新版正式评审基本信息列表模块
            //author yaphet
            .when('/FormalAssessmentInfoList/:tabIndex', {
            	controller: 'FormalAssessmentInfoList',
            	templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentInfoList.html',
            	controllerAs: 'model',
            	resolve: {
            		resolver: ['$q', '$rootScope', function ($q, $rootScope) {
            			var deferred = $q.defer();
            			require(['page/rcm/newFormalAssessment/formalAssessmentInfoList.js?_v='+_version], function () {
            				$rootScope.$apply(function () {
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //新版正式评审基本信息修改
            //author yaphet
            .when('/FormalAssessmentDetail/:id', {
            	controller: 'FormalAssessmentDetail',
            	templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentDetail.html',
            	controllerAs: 'model',
            	resolve: {
            		resolver: ['$q', '$rootScope', function ($q, $rootScope) {
            			var deferred = $q.defer();
            			require(['page/rcm/newFormalAssessment/formalAssessmentDetail.js?_v='+_version], function () {
            				$rootScope.$apply(function () {
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //新版正式评审审核列表
            //author yaphet
            .when('/FormalAssessmentAuditList/:tabIndex', {
            	controller: 'FormalAssessmentAuditList',
            	templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentAuditList.html',
            	controllerAs: 'model',
            	resolve: {
            		resolver: ['$q', '$rootScope', function ($q, $rootScope) {
            			var deferred = $q.defer();
            			require(['page/rcm/newFormalAssessment/formalAssessmentAuditList.js?_v='+_version], function () {
            				$rootScope.$apply(function () {
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //新版正式评审审核页面
            //author yaphet
            //status：0待办1已办
            .when('/FormalAssessmentAuditDetailView/:id/:url', {
            	controller: 'FormalAssessmentAuditDetailView',
            	templateUrl: 'page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.html',
            	controllerAs: 'model',
            	resolve: {
            		resolver: ['$q', '$rootScope', function ($q, $rootScope) {
            			var deferred = $q.defer();
            			require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version], function () {
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
            			require(['page/rcm/newFormalAssessment/formalAssessmentAuditDetailView.js?_v='+_version], function () {
            				$rootScope.$apply(function () {
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            
			//决策通知书审阅列表
			//version2.0
			//author yaphet
			.when('/NoticeDecisionAuditList/:tabIndex',{
				controller:'NoticeDecisionAuditList',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionAuditList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionAuditList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书审阅详情
			//version2.0
			//author yaphet
			.when('/noticeDecisionAuditView/:action/:id/:url',{
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
			
			
			//决策通知书列表
			//version2.0
			//author yaphet
			.when('/NoticeDecisionList',{
				controller:'NoticeDecisionList',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书草拟
				.when('/NoticeDecisionDraftList/:tabIndex',{
				controller:'NoticeDecisionDraftList',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionDraftList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionDraftList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书仅草拟(仅查看)
				.when('/NoticeDecisionDraftDetailView/:action/:id/:url',{
				controller:'NoticeDecisionDraftDetailView',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionDraftDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionDraftDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书草拟（新增）
			.when('/NoticeDecisionDraftDetail/:action/:id',{
				controller:'NoticeDecisionDraftDetail',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionDraftDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionDraftDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书草拟(完成)
			.when('/NoticeDecisionDraftCompleteDetail/:action/:id/:url',{
				controller:'NoticeDecisionDraftCompleteDetail',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionDraftCompleteDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionDraftCompleteDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书确认
			.when('/NoticeDecisionConfirmList/:tabIndex',{
				controller:'NoticeDecisionConfirmList',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionConfirmList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionConfirmList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书仅草拟(仅查看)
			.when('/NoticeDecisionConfirmDetailView/:tabIndex/:id/:url',{
				controller:'NoticeDecisionConfirmDetailView',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionConfirmDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionConfirmDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书
			//version2.0
			//author yaphet
			//仅供查看
			.when('/NoticeDecisionDetailView/:action/:id/:url',{
				controller:'NoticeDecisionDetailView',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			
			
			//决策通知书
			//version2.0
			//author yaphet
			//修改、新增
			.when('/NoticeDecisionDetail/:action/:id',{
				controller:'NoticeDecisionDetail',
				templateUrl:'page/rcm/noticeofdecision/noticeDecisionDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/noticeDecisionDetail.js?_v='+_version],function(){
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

			//业务单位承诺列表
			.when('/BusinessUnitCommitList',{
				controller:'BusinessUnitCommitList',
				templateUrl:'page/rcm/formalAssessment/BusinessUnitCommitList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/BusinessUnitCommitList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//业务单位承诺
			.when('/BusinessUnitCommit/:action/:id/:url',{
				controller:'BusinessUnitCommit',
				templateUrl:'page/rcm/formalAssessment/BusinessUnitCommit.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/BusinessUnitCommit.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//经验总结列表
			.when('/ExperienceList',{
				controller:'ExperienceList',
				templateUrl:'page/rcm/formalAssessment/ExperienceList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/ExperienceList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
            //项目经验总结
            .when('/Experience/:action/:id/:url',{
                controller:'Experience',
                templateUrl:'page/rcm/formalAssessment/Experience.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/formalAssessment/Experience.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

			//投资项目上会特批
			.when('/SpecialApprovalMeeting/:id',{
				controller:'SpecialApprovalMeeting',
				templateUrl:'page/rcm/formalAssessment/SpecialApprovalMeeting.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/formalAssessment/forAssesmentReport/FormalReportList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

	    //法律尽调申请列表
	    .when('/LawsDoTuneList', {
                controller: 'LawsDoTuneList',
                templateUrl: 'page/rcm/lawsDoTune/LawsDoTuneList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/lawsDoTune/LawsDoTune.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
	    //法律尽调申请单
	    .when('/LawsDoTuneDetail', {
                controller: 'LawsDoTuneDetail',
                templateUrl: 'page/rcm/lawsDoTune/LawsDoTuneDetail.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/lawsDoTune/LawsDoTune.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

	    //上传法律尽职调查报告
	    .when('/UploadReport', {
                controller: 'UploadReport',
                templateUrl: 'page/rcm/lawsDoTune/UploadReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/lawsDoTune/LawsDoTune.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

	    .when('/GrassrootsUploadReport', {
                controller: 'GrassrootsUploadReport',
                templateUrl: 'page/rcm/lawsDoTune/GrassrootsUploadReport.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/lawsDoTune/LawsDoTune.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

	    //初步审核意见
	    .when('/AuditOpinion', {
                controller: 'AuditOpinion',
                templateUrl: 'page/rcm/lawsDoTune/AuditOpinion.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/lawsDoTune/LawsDoTune.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

	    //初步审核意见反馈
	    .when('/Feedback', {
                controller: 'Feedback',
                templateUrl: 'page/rcm/lawsDoTune/Feedback.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/lawsDoTune/LawsDoTune.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

	    //数据字典列表
            .when('/DataDictionaryList', {
                controller: 'DataDictionaryList',
                templateUrl: 'page/rcm/BasicSetting/DataDictionaryList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataDictionaryList.js?_v='+_version], function () {
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
                        require(['page/rcm/BasicSetting/BusinessModelList.js?_v='+_version], function () {
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
                        require(['page/rcm/BasicSetting/ManageAttachmentList.js?_v='+_version], function () {
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
                        require(['page/rcm/BasicSetting/ListBusiness.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//数据字典新增
			.when('/DataDictionaryEdit/:action/:uuid',{
				controller:'DataDictionaryEdit',
				templateUrl:'page/rcm/BasicSetting/DataDictionaryEdit.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/DataDictionaryEdit.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//数据字典查看
			.when('/DataDictionaryView/:uuid',{
				controller:'DataDictionaryView',
				templateUrl:'page/rcm/BasicSetting/DataDictionaryView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/DataDictionaryView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//数据字典项列表
			.when('/DataOptionList/:UUID',{
				controller:'DataOptionList',
				templateUrl:'page/rcm/BasicSetting/DataOptionList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/DataOptionList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//数据字典项新增
            .when('/DataOptionEdit/:action/:uuid/:fk_Id',{
                controller:'DataOptionEdit',
                templateUrl:'page/rcm/BasicSetting/DataOptionEdit.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataOptionEdit.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //数据字典项新增
            .when('/DataOptionView/:uuid/:fk_Id',{
                controller:'DataOptionView',
                templateUrl:'page/rcm/BasicSetting/DataOptionView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/DataOptionView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/BusinessModelEdit/:action/:uuid',{
                controller:'BusinessModelEdit',
                templateUrl:'page/rcm/BasicSetting/BusinessModelEdit.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/BusinessModelEdit.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
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
			})

			//正式评审时间预警设置列表
			.when('/FormalWarningTimeList',{
				controller:'FormalWarningTimeList',
				templateUrl:'page/rcm/BasicSetting/FormalWarningTimeList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/BasicSetting.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//正式评审时间预警设置新增
			.when('/FormalWarningTimeEdit',{
				controller:'FormalWarningTimeEdit',
				templateUrl:'page/rcm/BasicSetting/FormalWarningTimeEdit.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/BasicSetting.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//法律尽调时间预警设置列表
			.when('/LawWarningTimeList',{
				controller:'LawWarningTimeList',
				templateUrl:'page/rcm/BasicSetting/LawWarningTimeList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/BasicSetting.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//法律尽调时间预警设置新增
			.when('/LawWarningTimeEdit',{
				controller:'LawWarningTimeEdit',
				templateUrl:'page/rcm/BasicSetting/LawWarningTimeEdit.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/BasicSetting.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//评审小组列表
            .when('/ReviewTeamList', {
                controller: 'ReviewTeamList',
                templateUrl: 'page/rcm/BasicSetting/ReviewTeamList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ReviewTeamList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/ReviewTeamAdd/:action/:uuid',{
                controller:'ReviewTeamAdd',
                templateUrl:'page/rcm/BasicSetting/ReviewTeamAdd.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ReviewTeamAdd.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //评审小组新增
            .when('/ReviewTeamEdit/:TEAM_NAME/:TEAM_ID',{
                controller:'ReviewTeamEdit',
                templateUrl:'page/rcm/BasicSetting/ReviewTeamEdit.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/ReviewTeamEdit.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

			//评审小组新增
			.when('/ReviewTeamEdit',{
				controller:'ReviewTeamEdit',
				templateUrl:'page/rcm/BasicSetting/ReviewTeamEdit.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/BasicSetting.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})

			//首页
			.when('/homePage',{
				controller:'homePage',
				templateUrl:'page/rcm/homePage/homePage.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/homePage/homePage.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//个人工作台
			.when('/IndividualTable',{
				controller:'IndividualTable',
				templateUrl:'page/rcm/homePage/IndividualTable.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/homePage/IndividualTable.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//员工工作情况
			//改进上面的方法新增加tabIndex的参数 定位到指定的tab中
			//tabIndex类型分别有0、1、2、3
			.when('/PersonnelWork/:type/:id/:lx/:tabIndex',{
				controller:'PersonnelWork',
				templateUrl:'page/rcm/homePage/PersonnelWork.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/homePage/PersonnelWork.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//员工工作情况 正式评审
			//改进上面的方法新增加tabIndex的参数 定位到指定的tab中
			//tabIndex类型分别有0、1、2、3
			.when('/PersonnelWorkFormal/:userId/:tabIndex',{
				controller:'PersonnelWorkFormal',
				templateUrl:'page/rcm/homePage/PersonnelWorkFormal.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/homePage/PersonnelWorkFormal.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			
            //供门户调用，将待阅变为已阅
            .when('/toReadRedirect/:nid/:redirectUrl',{
                controller:'toReadRedirect',
                template:'',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/homePage/homePage.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //接口调用日志列表
            .when('/WebserviceLog',{
                controller:'WebserviceLog',
                templateUrl:'page/fnd/webserviceLog/serviceLog.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/webserviceLog/serviceLog.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //接口日志详情
            .when('/wsLogDetail/:id',{
                controller:'wsLogDetail',
                templateUrl:'page/fnd/webserviceLog/logDetail.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/webserviceLog/serviceLog.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/GrouUserRepoUnitList', {
                controller: 'GrouUserRepoUnitList',
                templateUrl: 'page/fnd/group/GrouUserRepoUnitList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GrouUserRepoUnitList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/GrouUserRepoUnit/:action/:id/:url', {
                controller: 'GrouUserRepoUnit',
                templateUrl: 'page/fnd/group/GrouUserRepoUnit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/GrouUserRepoUnit.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/DirectUserReportingUnitList', {
                controller: 'DirectUserReportingUnitList',
                templateUrl: 'page/fnd/group/DirectUserReportingUnitList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/DirectUserReportingUnit.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            .when('/DirectUserReportingUnit/:action/:id', {
                controller: 'DirectUserReportingUnit',
                templateUrl: 'page/fnd/group/DirectUserReportingUnit.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/fnd/group/DirectUserReportingUnit.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //日志列表
            .when('/Journal',{
                controller:'Journal',
                templateUrl:'page/fnd/journal/sysJournal.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/journal/journal.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //日志详情
            .when('/JournalInfo/:id/:url',{
                controller:'JournalInfo',
                templateUrl:'page/fnd/journal/JournalInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/journal/JournalInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //接口列表
            .when('/wscall',{
                controller:'wscall',
                templateUrl:'page/fnd/wscall/wscall.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/wscall/wscall.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //接口详情
            .when('/wscallDetail/:id/:url',{
            	controller:'wscallDetail',
            	templateUrl:'page/fnd/wscall/wscallDetail.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/fnd/wscall/wscallDetail.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //日志详情
            .when('/JournalDetail/:id',{
                controller:'JournalDetail',
                templateUrl:'page/fnd/Journal/logDetail.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/Journal/serviceLog.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //日志详情
            .when('/bpmn',{
                controller:'bpmn',
                templateUrl:'page/fnd/bpmn/bpmn_index.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/fnd/bpmn/bpmn.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报事项审阅列表
            .when('/BulletinReview/:tabIndex',{
                controller:'BulletinReview',
                templateUrl:'page/rcm/bulletinMatters/BulletinReview.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinReview.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项列表
            .when('/BulletinMattersAudit/:tabIndex',{
                controller:'BulletinMattersAudit',
                templateUrl:'page/rcm/bulletinMatters/BulletinMattersAudit.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMattersAudit.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项列表
            .when('/BulletinMatters/:tabIndex',{
                controller:'BulletinMatters',
                templateUrl:'page/rcm/bulletinMatters/BulletinMatters.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项会议纪要列表
            .when('/MeetingSummary/:tabIndex',{
                controller:'MeetingSummary',
                templateUrl:'page/rcm/bulletinMatters/MeetingSummary.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/MeetingSummary.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项详情录入编辑
            .when('/BulletinMattersDetail/:id',{
                controller:'BulletinMattersDetail',
                templateUrl:'page/rcm/bulletinMatters/BulletinMattersDetail.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项详情录入编辑
            .when('/BulletinMattersDetailView/:id/:url',{
                controller:'BulletinMattersDetailView',
                templateUrl:'page/rcm/bulletinMatters/BulletinMattersDetailView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMatters.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项详情录入编辑
            .when('/BulletinMattersAuditView/:id/:url',{
                controller:'BulletinMattersAuditView',
                templateUrl:'page/rcm/bulletinMatters/BulletinMattersAuditView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinMattersAudit.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //通报类事项提交决策委员会材料
            .when('/BulletinReviewDetail/:id/:url',{
                controller:'BulletinReviewDetail',
                templateUrl:'page/rcm/bulletinMatters/BulletinReviewDetail.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinReview.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })   
            //正式评审会议信息列表
            .when('/ConferenceInformation/:tabIndex',{
                controller:'ConferenceInformation',
                templateUrl:'page/rcm/newFormalAssessment/ConferenceInformation.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/ConferenceInformation.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //正式评审新增会议信息
            .when('/ConferenceInformationCreate/:id/:url',{
                controller:'ConferenceInformationCreate',
                templateUrl:'page/rcm/newFormalAssessment/ConferenceInformationCreate.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/ConferenceInformationCreate.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })

            //系统功能建设中
            .when('/Build',{
                controller:'Build',
                templateUrl:'page/rcm/errorMessage/Build.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/errorMessage/Build.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //yaphet
            //预评审基本信息页面
            .when('/PreDetailView/:id/:url',{
            	controller:'PreDetailView',
            	templateUrl:'page/rcm/pre/PreDetailView.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreDetailView.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //yaphet
            //预评审审核页面
            .when('/PreAuditDetailView/:id/:url',{
            	controller:'PreAuditDetailView',
            	templateUrl:'page/rcm/pre/PreAuditDetailView.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreAuditDetailView.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //yaphet
            //预评审审核列表
            .when('/PreAuditList/:tabIndex',{
            	controller:'PreAuditList',
            	templateUrl:'page/rcm/pre/PreAuditList.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreAuditList.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })            
            //帮助页面
            .when('/Help',{
                controller:'Help',
                templateUrl:'page/rcm/help/Help.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/help/Help.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //项目看板---->正式评审
            //author  yaphet
            .when('/formalReportBoardList/:tabIndex',{
                controller:'formalReportBoardList',
                templateUrl:'page/rcm/projectBoard/formalReportBoardList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/formalReportBoardList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //项目看板---->正式评审
            //author  yaphet
            .when('/formalReportBoardListMore/:stage/:wf_state/:url',{
                controller:'formalReportBoardListMore',
                templateUrl:'page/rcm/projectBoard/formalReportBoardListMore.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/formalReportBoardListMore.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //其他需决策事项参会信息列表
            .when('/BulletinConferenceInformation/:tabIndex',{
                controller:'BulletinConferenceInformation',
                templateUrl:'page/rcm/bulletinMatters/BulletinConferenceInformation.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/bulletinMatters/BulletinConferenceInformation.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
             //专业评审负责人列表
            .when('/ProfessionList', {
                controller: 'ProfessionList',
                templateUrl: 'page/rcm/newFormalAssessment/ProfessionList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/ProfessionList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //专业评审负责人新增
              .when('/ProfessionTeamAdd/:action/:uuid/:url',{
                controller:'ProfessionTeamAdd',
                templateUrl:'page/rcm/newFormalAssessment/ProfessionTeamAdd.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/newFormalAssessment/ProfessionTeamAdd.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //平台公告
            .when('/notificationList',{
                controller:'notificationList',
                templateUrl:'page/rcm/notification/notificationList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/notification/notificationList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
             //平台公告-新增/修改页面
            .when('/notificationInfo/:action/:id/:url',{
                controller:'notificationInfo',
                templateUrl:'page/rcm/notification/notificationInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/notification/notificationInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //平台公告-查看页面
            .when('/notificationInfoView/:action/:id/:url',{
                controller:'notificationInfoView',
                templateUrl:'page/rcm/notification/notificationInfoView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/notification/notificationInfoView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
             //个人待办--平台公告--更多
            .when('/individualNotificationList/:url',{
                controller:'individualNotificationList',
                templateUrl:'page/rcm/notification/individualNotificationList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/notification/individualNotificationList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
             //个人待办--平台公告--公告详情，个人待办--公告平台--更多--公告详情
            .when('/individualNotificationInfo/:action/:id/:url',{
                controller:'individualNotificationInfo',
                templateUrl:'page/rcm/notification/individualNotificationInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/notification/individualNotificationInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //dsl
            //投标评审报告列表
            .when('/PreAuditReportList/:tabIndex',{
            	controller:'PreAuditReportList',
            	templateUrl:'page/rcm/pre/PreAuditReportList.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreAuditReportList.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //dsl    新建投标评审报告(PreOtherReport)
            .when('/PreOtherReport/:pmodel/:action/:id/:url',{
            	controller:'PreOtherReport',
            	templateUrl:'page/rcm/pre/PreOtherReport.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreOtherReport.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //dsl    查看投标评审报告(PreOtherReportView)
            .when('/PreOtherReportView/:id/:url',{
            	controller:'PreOtherReportView',
            	templateUrl:'page/rcm/pre/PreOtherReportView.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreOtherReportView.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //dsl    新建投标评审报告(PreNormalReport)
            .when('/PreNormalReport/:pmodel/:action/:id/:url',{
            	controller:'PreNormalReport',
            	templateUrl:'page/rcm/pre/PreNormalReport.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreNormalReport.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //dsl    查看投标评审报告(PreNormalReportView)
            .when('/PreNormalReportView/:id/:url',{
            	controller:'PreNormalReportView',
            	templateUrl:'page/rcm/pre/PreNormalReportView.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/pre/PreNormalReportView.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //投标评审参会信息列表
            .when('/PreMeetingInfoList/:tabIndex',{
                controller:'PreMeetingInfoList',
                templateUrl:'page/rcm/pre/PreMeetingInfoList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreMeetingInfoList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //投标评审新增参会信息
            .when('/PreMeetingInfoCreate/:id/:url',{
                controller:'PreMeetingInfoCreate',
                templateUrl:'page/rcm/pre/PreMeetingInfoCreate.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/pre/PreMeetingInfoCreate.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
           //投标评审参会信息详情
			.when('/PreMeetingInfoDetailView/:id/:url',{
				controller:'PreMeetingInfoDetailView',
				templateUrl:'page/rcm/pre/PreMeetingInfoDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/PreMeetingInfoDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			
			 //风险案例列表
            .when('/riskGuidelinesList',{
                controller:'riskGuidelinesList',
                templateUrl:'page/rcm/riskGuidelines/riskGuidelinesList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/riskGuidelinesList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //风险案例已提交列表
            .when('/submitRiskGuidelinesList',{
                controller:'submitRiskGuidelinesList',
                templateUrl:'page/rcm/riskGuidelines/submitRiskGuidelinesList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/submitRiskGuidelinesList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //风险指引-新增/修改页面
            .when('/riskGuidelineInfo/:action/:id/:url',{
                controller:'riskGuidelineInfo',
                templateUrl:'page/rcm/riskGuidelines/riskGuidelineInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/riskGuidelineInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //风险管理-查看页面
            .when('/riskGuidelineInfoView/:action/:id/:url',{
                controller:'riskGuidelineInfoView',
                templateUrl:'page/rcm/riskGuidelines/riskGuidelineInfoView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/riskGuidelines/riskGuidelineInfoView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            
            //模板文件列表
            .when('/templateFlieList',{
                controller:'templateFlieList',
                templateUrl:'page/rcm/templateFile/templateFlieList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/templateFlieList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //模板文件已提交列表
            .when('/submitTemplateFlieList',{
                controller:'submitTemplateFlieList',
                templateUrl:'page/rcm/templateFile/submitTemplateFlieList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/submitTemplateFlieList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //模板文件-新增/修改页面
            .when('/templateFlieInfo/:action/:id/:url',{
                controller:'templateFlieInfo',
                templateUrl:'page/rcm/templateFile/templateFlieInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/templateFlieInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //模板文件-查看页面
            .when('/templateFlieInfoView/:action/:id/:url',{
                controller:'templateFlieInfoView',
                templateUrl:'page/rcm/templateFile/templateFlieInfoView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/templateFile/templateFlieInfoView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //规章制度列表
            .when('/regulationsList',{
                controller:'regulationsList',
                templateUrl:'page/rcm/regulations/regulationsList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/regulationsList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //模板文件已提交列表
            .when('/submitRegulationsList',{
                controller:'submitRegulationsList',
                templateUrl:'page/rcm/regulations/submitRegulationsList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/submitRegulationsList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //规章制度-新增/修改页面
            .when('/regulationsInfo/:action/:id/:url',{
                controller:'regulationsInfo',
                templateUrl:'page/rcm/regulations/regulationsInfo.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/regulationsInfo.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //规章制度-查看页面
            .when('/regulationsInfoView/:action/:id/:url',{
                controller:'regulationsInfoView',
                templateUrl:'page/rcm/regulations/regulationsInfoView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/regulations/regulationsInfoView.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
          //正式评审可替换决策材料附件列表
			//yaphet
			.when('/updateDecisionFileList',{
				controller:'updateDecisionFileList',
				templateUrl:'page/rcm/newFormalAssessment/updateDecisionFileList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/updateDecisionFileList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//正式评审提交决策委员会材料
			.when('/formalBiddingFileInfo/:id/:url',{
				controller:'FormalBiddingInfo',
				templateUrl:'page/rcm/newFormalAssessment/formalBiddingFileInfo.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/newFormalAssessment/formalBiddingFileInfo.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			
			//项目看板---->投标评审项目列表
            //author  yaphet
            .when('/preReportBoardList/:tabIndex',{
                controller:'preReportBoardList',
                templateUrl:'page/rcm/projectBoard/preReportBoardList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/preReportBoardList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //项目看板---->投标评审项目详情
            //author  yaphet
            .when('/preReportBoardListMore/:stage/:wf_state/:url',{
                controller:'preReportBoardListMore',
                templateUrl:'page/rcm/projectBoard/preReportBoardListMore.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/preReportBoardListMore.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //投标评审看板详情
            .when('/projectPreInfoAllView/:id/:url',{
                controller:'ProjectPreInfoAllView',
                templateUrl:'page/rcm/deptwork/projectPreInfoAllView.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/deptwork/projectPreInfoAllView.js?_v='+_version,'../javascripts/util/common.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
			//修改项目名称
            .when('/UpdateProjectNameList', {
                controller: 'UpdateProjectNameList',
                templateUrl: 'page/rcm/BasicSetting/UpdateProjectNameList.html',
                controllerAs: 'model',
                resolve: {
                    resolver: ['$q', '$rootScope', function ($q, $rootScope) {
                        var deferred = $q.defer();
                        require(['page/rcm/BasicSetting/UpdateProjectNameList.js?_v='+_version], function () {
                            $rootScope.$apply(function () {
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            
            //评审台账页面
            .when('/reviewStatistics',{
                controller:'reviewStatistics',
                templateUrl:'page/rcm/reviewStatistics/reviewStatistics.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/reviewStatistics/reviewStatistics.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //投资决策统计
            .when('/NoticeReviewListReadOnly',{
            	controller:'NoticeReviewListReadOnly',
            	templateUrl:'page/rcm/meeting/NoticeReviewListReadOnly.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/meeting/NoticeReviewListReadOnly.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //项目看板---->其他决策事项评审项目列表
            .when('/bulletinReportBoardList/:tabIndex',{
                controller:'bulletinReportBoardList',
                templateUrl:'page/rcm/projectBoard/bulletinReportBoardList.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/bulletinReportBoardList.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //项目看板---->其他决策事项项目详情
            .when('/bulletinReportBoardListMore/:stage/:wf_state/:url',{
                controller:'bulletinReportBoardListMore',
                templateUrl:'page/rcm/projectBoard/bulletinReportBoardListMore.html',
                controllerAs:'model',
                resolve:{
                    resolver:['$q','$rootScope',function($q,$rootScope){
                        var deferred = $q.defer();
                        require(['page/rcm/projectBoard/bulletinReportBoardListMore.js?_v='+_version],function(){
                            $rootScope.$apply(function(){
                                deferred.resolve();
                            });
                        });
                        return deferred.promise;
                    }]
                }
            })
            //正式评审项目列表
            .when('/formalDeptWorkList/:serviceTypeId/:areaId/:stages/:oldUrl',{
            	controller:'formalDeptWorkList',
            	templateUrl:'page/rcm/deptwork/formalDeptWorkList.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/deptwork/formalDeptWorkList.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //投标评审项目列表
            .when('/preDeptWorkList/:serviceTypeId/:areaId/:stages/:oldUrl',{
            	controller:'preDeptWorkList',
            	templateUrl:'page/rcm/deptwork/preDeptWorkList.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/deptwork/preDeptWorkList.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
            //其他评审项目列表
            .when('/bulletinDeptWorkList/:serviceTypeId/:areaId/:stages/:oldUrl',{
            	controller:'bulletinDeptWorkList',
            	templateUrl:'page/rcm/deptwork/bulletinDeptWorkList.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/deptwork/bulletinDeptWorkList.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});
            			return deferred.promise;
            		}]
            	}
            })
                        //决策通知书草拟(投标评审)
				.when('/preNoticeDecisionDraftList/:tabIndex',{
				controller:'preNoticeDecisionDraftList',
				templateUrl:'page/rcm/noticeofdecision/preNoticeDecisionDraftList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/preNoticeDecisionDraftList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书草拟（新增）(投标评审)
			.when('/preNoticeDecisionDraftDetail/:action/:id',{
				controller:'preNoticeDecisionDraftDetail',
				templateUrl:'page/rcm/noticeofdecision/preNoticeDecisionDraftDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/preNoticeDecisionDraftDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书仅草拟(仅查看)(投标评审)
				.when('/preNoticeDecisionDraftDetailView/:action/:id/:url',{
				controller:'preNoticeDecisionDraftDetailView',
				templateUrl:'page/rcm/noticeofdecision/preNoticeDecisionDraftDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/preNoticeDecisionDraftDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书草拟(完成)(投标评审)
			.when('/preNoticeDecisionDraftCompleteDetail/:action/:id/:url',{
				controller:'preNoticeDecisionDraftCompleteDetail',
				templateUrl:'page/rcm/noticeofdecision/preNoticeDecisionDraftCompleteDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/preNoticeDecisionDraftCompleteDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书确认(投标评审)
			.when('/preNoticeDecisionConfirmList/:tabIndex',{
				controller:'preNoticeDecisionConfirmList',
				templateUrl:'page/rcm/noticeofdecision/preNoticeDecisionConfirmList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/preNoticeDecisionConfirmList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//决策通知书仅草拟(仅查看)(投标评审)
				.when('/preNoticeDecisionConfirmDetailView/:action/:id/:url',{
				controller:'preNoticeDecisionConfirmDetailView',
				templateUrl:'page/rcm/noticeofdecision/preNoticeDecisionConfirmDetailView.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/noticeofdecision/preNoticeDecisionConfirmDetailView.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//其他决策事项(查看全部信息)
			.when('/projectBulletinInfoAllView/:id/:url',{
			controller:'projectBulletinInfoAllView',
			templateUrl:'page/rcm/bulletinMatters/projectBulletinInfoAllView.html',
			controllerAs:'model',
			resolve:{
				resolver:['$q','$rootScope',function($q,$rootScope){
					var deferred = $q.defer();
					require(['page/rcm/bulletinMatters/projectBulletinInfoAllView.js?_v='+_version],function(){
						$rootScope.$apply(function(){
							deferred.resolve();
						});
					});
					return deferred.promise;
				}]
			}
			})
			//导出台账菜单
            .when('/exportProjetInfo/:tabIndex',{
            	controller:'exportProjetInfo',
            	templateUrl:'page/rcm/deptwork/exportProjetInfo.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/rcm/deptwork/exportProjetInfo.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
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
            }).when('/DecisionLeadersInfo/:action/:id/:dictId/:url',{
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
            //结束流程菜单
            .when('/endFlow',{
            	controller:'endFlow',
            	templateUrl:'page/fnd/sysFun/endFlow.html',
            	controllerAs:'model',
            	resolve:{
            		resolver:['$q','$rootScope',function($q,$rootScope){
            			var deferred = $q.defer();
            			require(['page/fnd/sysFun/endFlow.js?_v='+_version],function(){
            				$rootScope.$apply(function(){
            					deferred.resolve();
            				});
            			});            			return deferred.promise;
            		}]
            	}
            })
            //投标评审附件替换页面
			.when('/preBiddingFileInfo/:id/:url',{
				controller:'PreBiddingFileInfo',
				templateUrl:'page/rcm/pre/decision/PreBiddingFileInfo.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/pre/decision/PreBiddingFileInfo.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//其他评审附件替换页面
			.when('/bulletinReviewFileDetail/:id/:url',{
				controller:'bulletinReviewFileDetail',
				templateUrl:'page/rcm/bulletinMatters/BulletinReviewFileDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/bulletinMatters/BulletinReviewFileDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			
			//替换前台文件
			.when('/changeFile',{
				controller:'changeFile',
				templateUrl:'page/rcm/BasicSetting/changeFile.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/rcm/BasicSetting/changeFile.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//大区分管领导维护列表
			.when('/pertainAreaLeaderList',{
				controller:'pertainAreaLeaderList',
				templateUrl:'page/fnd/group/pertainAreaLeaderList.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/fnd/group/pertainAreaLeaderList.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//大区分管领导维护编辑
			.when('/pertainAreaDetail/:action/:id/:url',{
				controller:'pertainAreaDetail',
				templateUrl:'page/fnd/group/pertainAreaDetail.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/fnd/group/pertainAreaDetail.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//用户分配角色
			.when('/sysUserRole/:userId/:url',{
				controller:'sysUserRole',
				templateUrl:'page/fnd/user/sysUserRole.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/fnd/user/sysUserRole.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
			//修改流程人员
			.when('/changeBpmnUser',{
				controller:'changeBpmnUser',
				templateUrl:'page/fnd/bpmn/changeBpmnUser.html',
				controllerAs:'model',
				resolve:{
					resolver:['$q','$rootScope',function($q,$rootScope){
						var deferred = $q.defer();
						require(['page/fnd/bpmn/changeBpmnUser.js?_v='+_version],function(){
							$rootScope.$apply(function(){
								deferred.resolve();
							});
						});
						return deferred.promise;
					}]
				}
			})
    }]);

function resolver($q, $rootScope, dependencies) {
    var deferred = $q.defer();
    require(dependencies, function () {
        $rootScope.$apply(function () {
            deferred.resolve();
        });
    });
    return deferred.promise;
}
