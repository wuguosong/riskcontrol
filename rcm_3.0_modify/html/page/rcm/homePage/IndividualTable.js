ctmApp.register.controller('IndividualTable', ['$http', '$scope', '$location', 'DirPipeSrv', function ($http, $scope, $location, DirPipeSrv) {

    $scope.initData = function () {
        $scope.initializeCurrUserTaskInfo();
        $scope.initNotificationFlatformInfo();
        $scope.queryMeetingNoticeTop();
        $scope.queryMyToReadInformation();
        $scope.getTodayProject();
        $scope.getProjectReport();
        $scope.showFlag = 'NATURAL';

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
}]);