define(['app'], function (app) {
    app.register
        .controller('MeetingSubmitCtrl', ['$http', '$scope', '$location', '$rootScope',
            function ($http, $scope, $location, $rootScope) {
                //--------------------------------------
                //待上会—— 查询  所有 暂存未提交的项目
                //--------------------------------------
                $scope.initQueryNotSubmProjList = function(){
                    $http({
                        method:'post',
                        url:SRV_URL+"meeting/initQueryNotSubmProjList.do"
                    }).success(function(result){
                        if(result.success){
                            if(result.result_data.MEETING_LEADERS == null){
                                result.result_data.MEETING_LEADERS = [];
                            }
                            $scope.meetingIssue = result.result_data;
                            $scope.meetingIssue.MEETING_TYPE = $scope.meetingIssue.MEETING_TYPE == null || $scope.meetingIssue.MEETING_TYPE == '' ? '4':'7';
                        }else{
                            $.alert(result.result_name);
                        }
                    });
                };
                $scope.initData = function(){
                    $scope.formalDivisionMapped={"nameField":"name","valueField":"value"};
                    $scope.formalInvestmentMapped={"nameField":"name","valueField":"value"};
                    $scope.formalAgenda={"nameField":"name","valueField":"value"};
                    $scope.formalContactsMapped={"nameField":"name","valueField":"value"};

                    $scope.preDivisionMapped={"nameField":"name","valueField":"value"};
                    $scope.preInvestmentMapped={"nameField":"name","valueField":"value"};
                    $scope.preAgenda={"nameField":"name","valueField":"value"};
                    $scope.preContactsMapped={"nameField":"name","valueField":"value"};

                    $scope.meetingOtherPerson={"nameField":"name","valueField":"value"};

                    $scope.nameValue=[{name:'A级（困难评审)',value:1},{name:'B级（中级评审)',value:2},{name:'C级（简单评审)',value:3}];

                    //初始化项目
                    $scope.initQueryNotSubmProjList();
                };
                $scope.changeMeetingTime = function(){
                    if(null == $scope.meetingIssue.MEETING_TIME || "" == $scope.meetingIssue.MEETING_TIME){
                        $scope.meetingIssue.MEETING_ISSUE_NUMBER = "";
                        $scope.meetingIssue.MEETING_ISSUE = "";
                    }else{
                        var postObj = $scope.httpData('meeting/getMaxMeetingIssue.do',$.param({"meetingTime": $scope.meetingIssue.MEETING_TIME}));
                        postObj.success(function (result) {
                            if (result.success) {
                                $scope.meetingIssue.MEETING_ISSUE_NUMBER = result.result_data;
                                $scope.meetingIssue.MEETING_ISSUE = $scope.meetingIssue.MEETING_TIME.substring(0,4) + $scope.meetingIssue.MEETING_ISSUE_FORMAT.replace("%s",result.result_data);
                            }else{
                                $.alert(result.result_name);
                            }
                        });
                    }
                }
                $scope.removeHashKey = function(obj) {
                    return angular.copy(obj)
                }
                //------------------------------------------------------
                // 保存未提交的会议信息(含项目信息)   start
                //------------------------------------------------------
                $scope.saveNotSubmitMeetingIssue = function () {
                    show_Mask();
                    var httpParam = $.param({"meetingIssueJson": JSON.stringify($scope.removeHashKey($scope.meetingIssue))});
                    var postObj = $scope.httpData('meeting/saveNotSubmitMeetingIssue.do',httpParam);
                    postObj.success(function (result) {
                        hide_Mask();
                        if (result.success) {
                            $scope.meetingIssue.ID = result.result_data;
                            $.alert("保存成功!");
                        }else{
                            $.alert(result.result_name);
                        }
                    });
                }
                //------------------------------------------------------
                // 保存未提交的会议信息(含项目信息)   end
                //------------------------------------------------------

                //------------------------------------------------------
                //验证表单  start
                //------------------------------------------------------
                $scope.toSubmitMeeting = function () {
                    show_Mask();
                    if (!$("#Meeting_Apply").valid()) {
                        hide_Mask();
                        return false;
                    }
                    if(null == $scope.meetingIssue.MEETING_TIME || ""== $scope.meetingIssue.MEETING_TIME){
                        $.alert("上会时间不能为空！");
                        hide_Mask();
                        return false;
                    }
                    if($scope.meetingIssue.MEETING_TYPE == null || $scope.meetingIssue.MEETING_TYPE == ""){
                        $.alert("会议类型不能为空!");
                        hide_Mask();
                        return false;
                    }
                    if($scope.meetingIssue.MEETING_LEADERS == null || $scope.meetingIssue.MEETING_LEADERS.length == 0){
                        $.alert("决策委员会委员不能为空!");
                        hide_Mask();
                        return false;
                    }
                    var meetingIssueNumberInput = $scope.meetingIssue.MEETING_ISSUE_NUMBER;
                    var meetingIssueNumber = parseInt(meetingIssueNumberInput);
                    if (meetingIssueNumberInput == null || meetingIssueNumberInput == "" || isNaN(meetingIssueNumber) || meetingIssueNumber.toString().length > 5)
                    {
                        $.alert("会议期次不能为空 或 长度不能大于5位的数字!");
                        hide_Mask();
                        return false;
                    }
                    $scope.meetingIssue.MEETING_ISSUE_NUMBER = meetingIssueNumber.toString();

                    var projectIndexs = $("input[ng-model$='projectIndex']");
                    var projectIndexCount = projectIndexs.length;
                    for(var x = 0; x < projectIndexCount; x++){
                        var projectIndexInput = projectIndexs.get(x).value;
                        var projectIndexNumber = parseInt(projectIndexInput);
                        if(projectIndexInput == ""){
                            continue;
                        }
                        if (isNaN(projectIndexNumber) || projectIndexNumber < 1 || projectIndexNumber > 99)
                        {
                            $.alert("序号要大于0  且 长度小于2位的数字 !");
                            hide_Mask();
                            return false;
                        }else{
                            for(var y = 0; y < projectIndexCount; y++){
                                if(x != y && projectIndexInput == projectIndexs.get(y).value){
                                    projectIndexCount = -1;
                                }
                            }
                        }
                    }
                    if(-1 == projectIndexCount)
                    {
                        $.confirm("序号有重复,确认要继续提交吗？", function(){
                            $scope.submitMeetingIssue();
                        });
                    }
                    else
                    {
                        $scope.submitMeetingIssue();
                    }
                }
                //------------------------------------------------------
                //验证表单  end
                //------------------------------------------------------

                //------------------------(提交会议项目  start)------------------------------
                $scope.submitMeetingIssue = function(){
                    //------------------------------------------------------
                    //验证委员中是否有决策会议主席
                    //------------------------------------------------------
                    var postObj = $scope.httpData('meetingIssue/isIncludeChairman.do',$.param({"meetingLeaders": JSON.stringify($scope.meetingIssue.MEETING_LEADERS)}));
                    postObj.success(function (data) {
                        if (data.success) {
                            var httpParam = $.param({"meetingIssueJson": JSON.stringify($scope.removeHashKey($scope.meetingIssue))});
                            var postObj = $scope.httpData('meeting/submitMeetingIssue.do',httpParam);
                            postObj.success(function (data) {
                                hide_Mask();
                                if (data.success) {
                                    $.alert("提交成功!");
                                    $location.path("/meeting/MeetingArrangement/0");
                                } else {
                                    $.alert(data.result_name);
                                }
                            });
                        }else{
                            hide_Mask();
                            $.alert(data.result_name);
                        }
                    });
                }
                //------------------------(提交会议项目  end)------------------------------

                //------------------------(上会时间点处理  start)------------------------------
                //  project:项目信息
                //  tag:标识(0:表示开始时间,1:结束时间)
                //
                $scope.changDate=function(project,tag){
                    if(null == project.startTime && null == project.endTime && null == project.meeting){
                        return;
                    }
                    var startTime = project.startTime == null ? project.meeting.startTime : project.startTime;
                    var endTime = project.endTime == null ? project.meeting.endTime : project.endTime;
                    var minute = 0;
                    if(null!=startTime && ""!=startTime){
                        var AP=  startTime.substring(startTime.length-2, startTime.length);
                        startTime=  startTime.substring(0,startTime.length-3);
                        if(AP=="PM"){
                            var strs= new Array(); //定义一数组
                            strs=startTime.split(":");
                            var s= strs[0]*1+12;
                            startTime=s+":"+strs[1];
                        }
                    }
                    if(null!=endTime && ""!=endTime){
                        var APd=  endTime.substring(endTime.length-2, endTime.length);
                        endTime=  endTime.substring(0,endTime.length-3);
                        if(APd=="PM"){
                            var strsd= new Array(); //定义一数组
                            strsd=endTime.split(":");
                            var sd= strsd[0]*1+12;
                            endTime=sd+":"+strsd[1];
                        }
                    }
                    if(undefined!=startTime && undefined!=endTime){
                        s=startTime.split(":");
                        e=endTime.split(":");
                        var daya = new Date();
                        var dayb = new Date();

                        daya.setHours(s[0]);
                        dayb.setHours(e[0]);
                        daya.setMinutes(s[1]);
                        dayb.setMinutes(e[1]);
                        var m = (dayb-daya)/1000/60;
                        if(isNaN(m) || m<0){
                            //$.alert("时间不能为负数,请修改");
                            minute = 0;
                        }else{
                            minute = m;
                        }
                    }
                    if(minute == 0){
                        //交换
                        if(tag == 0){
                            startTime = project.endTime == null ? project.meeting.endTime : project.endTime;
                        }else{
                            endTime = project.startTime == null ? project.meeting.startTime : project.startTime;
                        }
                    }
                    if(project.startTime == null){
                        project.meeting.minute = minute;
                    }else{
                        project.minute = minute;
                    }
                }
                //------------------------(上会时间点处理  end)------------------------------
                $scope.initData();
            }])
});