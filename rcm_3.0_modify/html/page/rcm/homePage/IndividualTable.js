ctmApp.register.controller('IndividualTable', ['$routeParams','$http', '$scope', '$location', 'DirPipeSrv', function ($routeParams,$http, $scope, $location, DirPipeSrv) {

    $scope.initData = function () {
        $scope.initializeCurrUserTaskInfo();
        $scope.initNotificationFlatformInfo();
        $scope.queryMeetingNoticeTop();
        $scope.queryMyToReadInformation();
        $scope.getTodayProject();
        $scope.getProjectReport();
        $scope.showFlag = 'NATURAL';
        $scope.pertainareaName = '';
        // 判断首页快捷菜单栏显示
        var currentUserRoles = $scope.credentials.roles;
        for (var i=0;i<currentUserRoles.length;i++)
        {
            console.log(currentUserRoles[i].CODE);
            if (currentUserRoles[i].CODE == 'JUDGES_CONFIG') {
                // 首页配置-评委
                $scope.showFlag = 'JUDGES';
                break;
            } else if (currentUserRoles[i].CODE == 'RISK_SYS_CONFIG') {
                // 首页配置-风控管理员
                $scope.showFlag = 'RISK_SYS';
                break;
            } else if (currentUserRoles[i].CODE == 'ASSESSOR_CONFIG') {
                // 首页配置-评审负责人
                $scope.showFlag = 'ASSESSOR';
                break;
            } else if (currentUserRoles[i].CODE == 'LEGAL_CONFIG') {
                // 首页配置-法务负责人
                $scope.showFlag = 'LEGAL';
                break;
            } else if (currentUserRoles[i].CODE == 'INVESTMENT_CONFIG') {
                // 首页配置-投资经理
                $scope.showFlag = 'INVESTMENT';
                break;
            } else if (currentUserRoles[i].CODE == 'NATURAL_CONFIG') {
                // 首页配置-自然人
                $scope.showFlag = 'NATURAL';
                break;
            }else if (currentUserRoles[i].CODE == 'REGIONAL_CONFIG') {
                // 首页配置-自然人
                $scope.showFlag = 'REGIONAL';
                break;
            }
        }

        if ($scope.showFlag == 'NATURAL') {
            $(".panel-index1").addClass("panel-index2");
        }
    };

    //初始化获取个人任务信息
    $scope.initializeCurrUserTaskInfo = function () {
        $http({
            method: 'post',
            url: srvUrl + "workFlow/initializeCurrUserTaskInfo.do"
        }).success(function (result) {
            if (result.success) {
                var resultData = result.result_data;
                $scope.now = resultData.now.substring(0, 10);
                var d = new Date($scope.now);
                var weekDay = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
                $scope.week = weekDay[d.getDay()];
                $scope.myTaskCount = resultData.myTaskCount;
                $scope.overTaskCount = resultData.overTaskCount;
//				$scope.completedTaskCount = resultData.completedTaskCount;

                $scope.myTaskList = resultData.myTaskList;
                $scope.overTaskList = resultData.overTaskList;
//				$scope.completedTaskList = resultData.completedTaskList;
                console.log($scope.myTaskList);
            } else {
                $.alert(result.result_name);
            }
        });
    };

    //初始化公告平台信息
    $scope.initNotificationFlatformInfo = function () {
        $http({
            method: 'post',
            url: srvUrl + "notificationFlatform/queryNotifTop.do"
        }).success(function (result) {
            if (result.success) {
                $scope.notificationFlatform = result.result_data;
                for (var int = 0; int < $scope.notificationFlatform.length; int++) {
                    var topic = $scope.notificationFlatform[int].TOPIC;
                    if (topic.length > 17) {//为避免页面样式变形，如果公告标题长度超过17位，则以'...'替代标题17位之后的文字
                        $scope.notificationFlatform[int].TOPIC = topic.substring(0, 17) + "...";
                    }
                }
            } else {
                $.alert(result.result_name);
            }
        }).error(function (data, status, headers, config) {
            $.alert(status);
        });
    }
    //初始化会议通知
    $scope.queryMeetingNoticeTop = function () {
        $http({
            method: 'post',
            url: srvUrl + "preliminaryNotice/queryTop.do"
        }).success(function (result) {
            $scope.meetingNoticeList = result.result_data;
        }).error(function (data, status, headers, config) {
            $.alert(status);
        });
    };

    // 初始化待阅/已阅信息
    $scope.queryMyToReadInformation = function () {
        $http({
            method: 'post',
            url: srvUrl + "notify/queryNotifyInfo.do"
        }).success(function (_jsonObj) {
            if (_jsonObj.myReadingList.length <= 5) {
                $scope.myReadingList = _jsonObj.myReadingList;
            } else {
                $scope.myReadingList = _jsonObj.myReadingList.slice(0, 4);
            }
            if (_jsonObj.myReadList.length <= 5) {
                $scope.myReadList = _jsonObj.myReadList;
            } else {
                $scope.myReadList = _jsonObj.myReadList.slice(0, 4);
            }
            $scope.myReadingCount = _jsonObj.myReadingCount;
            $scope.myReadCount = _jsonObj.myReadCount;
        });
    };
    // 更新只会信息状态
    $scope.notify_UpdateStatus = function(id,t){
        notify_UpdateStatus(id, 2);// 待阅->已阅
        // 判断是待阅类型：消息/知会
        if(t['NOTIFY_TYPE'] == 'MESSAGE'){
            DirPipeSrv._setNotifyInfo(t);
        }
    };


    // 获取今日评审项目列表
    $scope.getTodayProject = function () {
        $http({
            method:'post',
            url:srvUrl+"decision/queryList.do"
        }).success(function(data){
            if(data.success){
                $scope.projects = data.result_data;
                $scope.projectNum = $scope.projects.length;
            }else{
                $.alert(data.result_name);
            }
        });
    };

    // 工作量统计数据
    $scope.getProjectReport=function(){
        $http({
            method:'post',
            url: srvUrl + "deptwork/getProjectReport.do"
        }).success(function(result){
            $scope.projectReport0922ByYw = result.result_data.getProjectReport0922ByYw;
            $scope.projectReport0922ByFL = result.result_data.getProjectReport0922ByFL;
            $scope.queryPsfzrUsers = result.result_data.queryPsfzrUsers;
            $scope.queryFlfzrUsers = result.result_data.queryFlfzrUsers;

        });
    };

    $scope.queryFZR = function($event,uuid) {
        $http({
            method: 'post',
            url: srvUrl + "deptwork/showFzr.do",
            data: $.param({"uuid":uuid})
        }).success(function(result){
            var data = result.result_data.data;
            var html  ;
            var inderHtml ;
            //张三</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span>313</span></td><td colspan="2" align="left"><span>333</span></td></tr><tr id="'+uuid+'ls"><td  align="left"><a href="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;栗色</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span >22</span></td><td colspan="2" align="left"><span>313</span></td></tr>';
            for (var i = 0; i < data.length; i++) {
                var name = data[i].TEAM_MEMBER_NAME;
                var wgh = data[i].FORMAL_GOING + data[i].PRE_GOING + data[i].BULLETIN_GOING;
                var ywc = data[i].FORMAL_DEALED + data[i].PRE_DEALED+ data[i].BULLETIN_DEALED;
                var total = data[i].TOTAL_NUM;
                inderHtml = '<tr name="'+uuid+'zs">' + '<td align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#/PersonnelWork/user/'+data[i].USERID+'/YW/0/0">' + name + '</a></td>'
                    + '<td colspan="2" align="left"><span >'+ wgh +'</span></td>'
                    + '<td colspan="2" align="left"><span>' + ywc + '</span></td>'
                    + '<td colspan="2" align="left"><span>' + total + '</span></td></tr>' ;
                html = html + inderHtml;
            }
            $('#'+uuid).after(html);
            $($event.target).hide();
            $('#'+uuid+'sq').show();
        });



    };

    $scope.queryFlFZR = function($event,uuid) {
        $http({
            method: 'post',
            url: srvUrl + "deptwork/showFlFzr.do",
            data: $.param({"uuid":uuid})
        }).success(function(result){
            var data = result.result_data.data;
            var html  ;
            var inderHtml ;
            //张三</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span>313</span></td><td colspan="2" align="left"><span>333</span></td></tr><tr id="'+uuid+'ls"><td  align="left"><a href="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;栗色</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span >22</span></td><td colspan="2" align="left"><span>313</span></td></tr>';
            for (var i = 0; i < data.length; i++) {
                var name = data[i].TEAM_MEMBER_NAME;
                var wgh = data[i].FORMAL_GOING + data[i].BULLETIN_GOING;
                var ywc = data[i].FORMAL_DEALED + data[i].BULLETIN_DEALED;
                var total = data[i].TOTAL_NUM;
                inderHtml = '<tr name="'+uuid+'zs">' + '<td align="left"><a href="#/PersonnelWork/user/'+data[i].USERID+'/FL/0/0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + name + '</a></td>'
                    + '<td colspan="2" align="left"><span >'+ wgh +'</span></td>'
                    + '<td colspan="2" align="left"><span>' + ywc + '</span></td>'
                    + '<td colspan="2" align="left"><span>' + total + '</span></td></tr>' ;
                html = html + inderHtml;
            }
            $('#'+uuid).after(html);
            $($event.target).hide();
            $('#'+uuid+'sq').show();
        });



    };

    $scope.sqFZR = function($event,uuid) {
        //$('#'+uuid+'zs').remove();
        $("tr[name='"+uuid+"zs']").remove();
        //$('#'+uuid+'ls').remove();
        $($event.target).hide();
        $('#'+uuid+'zk').show();
    };
    $scope.initData();

    //新增
    $scope.tabIndex = '0';
    /*var projectName = '';*/
    $scope.params = {};

    // 初始化分页组件
    $scope.paginationConf1 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {}
    };
    $scope.paginationConf2 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf3 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf4 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf5 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };

    // 初始化分页组件
    $scope.paginationConf6 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {}
    };
    $scope.paginationConf7 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf8 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf9 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf10 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 10,
        perPageOptions: [5, 10, 20, 50],
        queryObj: {},
        onChange: function () {
        }
    };

    /*$scope.initData = function (){
        if (!isEmpty($scope.projectName)){
            projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.params.projectName = projectName;
        }
    };*/

    // 查询项目看 - 分配前
    $scope.getProjectList1 = function(){
        $scope.paginationConf1.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf1.queryObj.stageList = ['1'];
        $scope.paginationConf1.queryObj.wf_state = '1';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf1.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf1.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf1.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf1.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            //$scope.paginationConf1.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf1.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf1.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf1.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectListForCompanyHead.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf1),
                "json":JSON.stringify($scope.paginationConf1.queryObj)
            })
        }).success(function(result){
            $scope.projectList1 = result.result_data.list;
            $scope.paginationConf1.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板 - 风控评审
    $scope.getProjectList2 = function(){
        $scope.paginationConf2.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf2.queryObj.stageList = ['2', '3'];
        $scope.paginationConf2.queryObj.wf_state = "'1', '2'";
        if (!isEmpty($scope.params)) {
            $scope.paginationConf2.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf2.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf2.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf2.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            //$scope.paginationConf2.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf2.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf2.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf2.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectListForCompanyHead.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf2),
                "json":JSON.stringify($scope.paginationConf2.queryObj)
            })
        }).success(function(result){
            $scope.projectList2 = result.result_data.list;
            $scope.paginationConf2.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板 - 准备上会
    $scope.getProjectList3 = function(){
        $scope.paginationConf3.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf3.queryObj.stageList = ['4'];
        $scope.paginationConf3.queryObj.wf_state = '2';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf3.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf3.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf3.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf3.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            //$scope.paginationConf3.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf3.queryObj.vote_status = "0";
            $scope.paginationConf3.queryObj.decision_result = "0";
            $scope.paginationConf3.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf3.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf3.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectListForCompanyHead.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf3),
                "json":JSON.stringify($scope.paginationConf3.queryObj)
            })
        }).success(function(result){
            $scope.projectList3 = result.result_data.list;
            $scope.paginationConf3.totalItems = result.result_data.totalItems;
        });
    };

    // 查询项目看板 - 已决策
    $scope.getProjectList4 = function(){
        $scope.paginationConf4.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf4.queryObj.stageList = ['6', '7'];
        $scope.paginationConf4.queryObj.wf_state = '2';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf4.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf4.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf4.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf4.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            //$scope.paginationConf4.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf4.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf4.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf4.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectListForCompanyHead.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf4),
                "json":JSON.stringify($scope.paginationConf4.queryObj)
            })
        }).success(function(result){
            $scope.projectList4 = result.result_data.list;
            $scope.paginationConf4.totalItems = result.result_data.totalItems;
        });
    };

    // 查询项目看板 - 历史数据
    $scope.getProjectList5 = function(){
        $scope.paginationConf5.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf5.queryObj.wf_state = '5';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf5.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf5.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf5.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf5.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            //$scope.paginationConf5.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf5.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf5.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf5.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectListForCompanyHead.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf5),
                "json":JSON.stringify($scope.paginationConf5.queryObj)
            })
        }).success(function(result){
            $scope.projectList5 = result.result_data.list;
            $scope.paginationConf5.totalItems = result.result_data.totalItems;
        });
    };

    $scope.getProjectList = function (){
        $scope.getProjectList1();
        $scope.getProjectList2();
        $scope.getProjectList3();
        $scope.getProjectList4();
        $scope.getProjectList5();
    };
    $scope.cancel = function(){
        $scope.params.projectName=null;
        $scope.params.investmentName=null;
        $scope.params.reviewPersonName=null;
        $scope.params.legalReviewPersonName=null;
        $scope.params.projectType=null;
        $scope.params.applyDateStart=null;
        $scope.params.applyDateEnd=null;
        $scope.getProjectList();
    };
    // 查询项目看 - 分配前
    $scope.getProjectHighList6 = function(){
        $scope.paginationConf6.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf6.queryObj.stageList = ['1'];
        $scope.paginationConf6.queryObj.wf_state_n = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf6.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf6.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf6.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf6.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf6.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf6.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf6.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf6.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf6),
                "json":JSON.stringify($scope.paginationConf6.queryObj)
            })
        }).success(function(result){
            debugger;
            $scope.projectList6 = result.result_data.list;
            $scope.paginationConf6.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板 - 风控任务
    $scope.getProjectHighList7 = function(){
        $scope.paginationConf7.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf7.queryObj.stageList = ['2', '3'];
        $scope.paginationConf7.queryObj.wf_state_n = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf7.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf7.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf7.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf7.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf7.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf7.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf7.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf7.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf7),
                "json":JSON.stringify($scope.paginationConf7.queryObj)
            })
        }).success(function(result){
            $scope.projectList7 = result.result_data.list;
            $scope.paginationConf7.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板 - 已完成
    $scope.getProjectHighList8 = function(){
        $scope.paginationConf8.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf8.queryObj.stageList = ['4', '5'];
        $scope.paginationConf8.queryObj.wf_state_n = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf8.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf8.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf8.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf8.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf8.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf8.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf8.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf8.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf8),
                "json":JSON.stringify($scope.paginationConf8.queryObj)
            })
        }).success(function(result){
            $scope.projectList8 = result.result_data.list;
            $scope.paginationConf8.totalItems = result.result_data.totalItems;
        });
    };

    // 查询项目看板 - 已终止
    $scope.getProjectHighList9 = function(){
        $scope.paginationConf9.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf9.queryObj.stageList = ['6', '7'];
        $scope.paginationConf9.queryObj.wf_state = '2';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf9.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf9.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf9.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf9.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf9.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf9.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf9.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf9.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf9),
                "json":JSON.stringify($scope.paginationConf9.queryObj)
            })
        }).success(function(result){
            $scope.projectList9 = result.result_data.list;
            $scope.paginationConf9.totalItems = result.result_data.totalItems;
        });
    };

    // 查询项目看板 - 历史数据
    $scope.getProjectHighList10 = function(){
        $scope.paginationConf10.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf10.queryObj.wf_state = '5';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf10.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf10.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf10.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf10.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf10.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf10.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf10.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf10.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf10),
                "json":JSON.stringify($scope.paginationConf10.queryObj)
            })
        }).success(function(result){
            $scope.projectList10 = result.result_data.list;
            $scope.paginationConf10.totalItems = result.result_data.totalItems;
        });
    };

    $scope.getProjectHighList = function (){
        $scope.getProjectHighList6();
        $scope.getProjectHighList7();
        $scope.getProjectHighList8();
        $scope.getProjectHighList9();
        $scope.getProjectHighList10();
    };
    $scope.cancelHighList = function (){
        $scope.params.projectName=null;
        $scope.params.investmentName=null;
        $scope.params.reviewPersonName=null;
        $scope.params.legalReviewPersonName=null;
        $scope.params.pertainareaName=null;
        $scope.params.projectType=null;
        $scope.params.applyDateStart=null;
        $scope.params.applyDateEnd=null;
        $scope.getProjectHighList();
    };
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf1.currentPage + paginationConf1.itemsPerPage', $scope.getProjectList1);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf2.currentPage + paginationConf2.itemsPerPage', $scope.getProjectList2);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf3.currentPage + paginationConf3.itemsPerPage', $scope.getProjectList3);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf4.currentPage + paginationConf4.itemsPerPage', $scope.getProjectList4);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf5.currentPage + paginationConf5.itemsPerPage', $scope.getProjectList5);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf6.currentPage + paginationConf6.itemsPerPage', $scope.getProjectHighList6);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf7.currentPage + paginationConf7.itemsPerPage', $scope.getProjectHighList7);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf8.currentPage + paginationConf8.itemsPerPage', $scope.getProjectHighList8);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf9.currentPage + paginationConf9.itemsPerPage', $scope.getProjectHighList9);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf10.currentPage + paginationConf10.itemsPerPage', $scope.getProjectHighList10);

}]);