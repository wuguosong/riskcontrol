ctmApp.register.controller('MeetingUpdate', ['$http','$scope','$location','$routeParams','$filter',function ($http,$scope,$location,$routeParams,$filter) {
	$scope.meetingId = $routeParams.meetingId;
	$scope.isEditable = "false";
	
	$scope.getMeetingInfoById = function(){
		$http({
			method:'post',  
		    url:srvUrl+"meeting/getMeetingInfoById.do",
		    data: $.param({"meetingId":$scope.meetingId})
		}).success(function(result){
			if(result.success){
				if(result.result_data.MEETING_LEADERS == null){
					result.result_data.MEETING_LEADERS = [];
				}
				$scope.meetingIssue = result.result_data;
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
    	$scope.getMeetingInfoById();
	};
    $scope.removeHashKey = function(obj) {
    	return angular.copy(obj)
    }

    //------------------------------------------------------
	//验证表单  start
	//------------------------------------------------------
    $scope.toUpdateMeeting = function () {

		if(null == $scope.meetingIssue.MEETING_TIME || ""== $scope.meetingIssue.MEETING_TIME){
			$.alert("上会时间不能为空！");
			return false;
		}
		if($scope.meetingIssue.MEETING_TYPE == null || $scope.meetingIssue.MEETING_TYPE == ""){
    		$.alert("会议类型不能为空!");
    		return false;
    	}
    	if($scope.meetingIssue.MEETING_LEADERS == null || $scope.meetingIssue.MEETING_LEADERS.length == 0){
    		$.alert("决策委员会委员不能为空!");
    		return false;
    	}
    	
    	var projectIndexs = $("input[ng-model$='DECISION_ORDER_INDEX']");
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
    		$.confirm("序号有重复,确认要继续保存吗？", function(){
    			$scope.updateMeetingInfo();
        	});
    	}
    	else
    	{
    		$scope.updateMeetingInfo();
    	}
    }
    //------------------------------------------------------
	//验证表单  end
	//------------------------------------------------------
    
    $scope.updateMeetingInfo = function(){
    	//------------------------------------------------------
    	//验证委员中是否有决策会议主席 
    	//------------------------------------------------------
        var postObj = $scope.httpData('meetingIssue/isIncludeChairman.do',$.param({"meetingLeaders": JSON.stringify($scope.meetingIssue.MEETING_LEADERS)}));
        postObj.success(function (data) {
        	if (data.success) {
        		show_Mask();
		    	var httpParam = $.param({"meetingIssueJson": JSON.stringify($scope.removeHashKey($scope.meetingIssue))});
			    var postObj = $scope.httpData('meeting/updateSubmitMeetingInfo.do',httpParam);
		        postObj.success(function (data) {
		        	hide_Mask();
		        	if (data.success) {
		                $.alert("保存成功!");
		            } else {
		            	$.alert(data.result_name);
		            }
		        });
            }else{
            	$.alert(data.result_name);
            }
        });
    }
    
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
}]);

//阻止事件冒泡函数
function stopBubble(e)
{
    if (e && e.stopPropagation)
        e.stopPropagation()
    else
        window.event.cancelBubble=true
}
