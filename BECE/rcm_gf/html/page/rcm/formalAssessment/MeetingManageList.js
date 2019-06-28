ctmApp.register.controller('MeetingManageList', ['$http','$scope','$location','$filter', function ($http,$scope,$location,$filter) {
	$scope.mappedKeyValue={"nameField":"NAME","valueField":"VALUE"};
	$scope.myCheckedUser = [];
	$scope.queryAll = function () {
        $http({
			method:'post',  
		    url:srvUrl+"decision/queryList.do"
		}).success(function(data){
			if(data.success){
				$scope.projects = data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.entryDecision=function(){
    	var decisionArray = getSelectProject();
    	if (decisionArray.length != 1) {
        	$.alert("请选择一个要决策的项目！");
            return false;
        }
        //验证是否有表决，如果没有正在表决中的项目，则会弹出确认框
        $http({
			method:'post',  
		    url:srvUrl+"decision/verificationEntryDecision.do",
		    data: $.param({"id":decisionArray[0].DECISION_ID})
		}).success(function(data){
			if(data.success){
				$scope.meetingLeaderQueryParams = {
	        			meetingIssueId: decisionArray[0].MEETING_ISSUE_ID
			    };
				$.confirm("确认开始表决吗？", function() {
					$("#confirmMeetingLeaderModal").modal('show');
		    	});
			}else{
				$.alert(data.result_name);
			}
		});
    };
    //确认决策会委员的回调函数，该方法会更新表决项目的决策会委员
    $scope.confirmMeetingLeaderCallBack = function(checkedLeaders){
    	if(checkedLeaders == null || !$.isArray(checkedLeaders) || checkedLeaders.length == 0){
    		$.alert("参与表决的决策会委员不能为空！");
    		return;
    	}
    	var uid = $(":input[name=radio][type=radio]:checked").val();
    	$http({
			method:'post',  
		    url:srvUrl+"decision/entryDecision.do", 
		    data: $.param({"id":uid, "meetingLeaders":JSON.stringify(checkedLeaders)})
		}).success(function(data){
			if(data.success){
				var goUrlParam = $filter('encodeURI')("#/MeetingManageList","VALUE");
				$location.path("/MeetingVoteWait/"+uid+"/"+goUrlParam+"/1");
			}else{
				$.alert(data.result_name);
			}
		});
    }
    $scope.cancelDecision = function(){
    	$http({
   			method:'post',  
   		    url:srvUrl+"decision/isCurrUnderwayProject.do"
   		}).success(function(data){
   			if(data.success){
   				$.alert("暂无项目正在表决中！");
   			}else{
				$.confirm("确认开始撤消吗？", function() {
			    	$http({
						method:'post',  
					    url:srvUrl+"decision/cancelDecision.do"
					}).success(function(data){
						if(data.success){
						    $scope.queryAll();
						}else{
							$.alert(data.result_name);
						}
					});
		    	});
			}
   		});
    }
    $scope.resetDecision = function(){
    	var decisionArray = getSelectProject();
    	if (decisionArray.length != 1) {
        	$.alert("请选择一个已过会的项目！");
            return false;
        }
    	$http({
   			method:'post',  
   		    url:srvUrl+"decision/queryById.do",
		    data: $.param({"id":decisionArray[0].DECISION_ID})
   		}).success(function(data){
   			if(data.result_data.VOTE_STATUS != 2){
   				$.alert("请选择一个已过会的项目！");
   			}else if(data.success){
				$.confirm("确认要重新表决吗？", function() {
			    	$http({
						method:'post',  
					    url:srvUrl+"decision/resetDecision.do",
					    data: $.param({"id":decisionArray[0].DECISION_ID})
					}).success(function(data){
						if(data.success){
							decisionArray[0].VOTE_STATUS = 0;
							decisionArray[0].DECISION_RESULT = 0;
							decisionArray[0].DECISION_DATE = null;
							$scope.meetingLeaderQueryParams = {
				        			meetingIssueId: decisionArray[0].MEETING_ISSUE_ID
						    };
							$("#confirmMeetingLeaderModal").modal('show');
						}else{
							$.alert(data.result_name);
						}
					});
		    	});
			}
   		});
    }
    //获取选中的项目
    function getSelectProject(){
    	var chk_list = document.getElementsByName("radio");
        var projectArray = new Array();
        for (var i = 0,length = chk_list.length; i < length; i++) {
            if (chk_list[i].checked) {
            	for(var x = 0,size = $scope.projects.length; x < size; x++){
    	        	if(chk_list[i].value == $scope.projects[x].DECISION_ID){
    	        		var project = $scope.projects[x];
    	        		projectArray.push(project);
    	        		break;
    	        	}
    	        }
            }
        }
        return projectArray;
    }
    //完成决策会表决
    $scope.completeDecisionNotice=function(){
    	var decisionArray = getSelectProject();
    	if (decisionArray.length != 1 || decisionArray[0].PROJECT_TYPE != 'pfr') {
        	$.alert("请选择一个正式评审项目！");
            return false;
        }
//    	$http({
//   			method:'post',  
//   		    url:srvUrl+"decision/queryById.do",
//		    data: $.param({"id":decisionArray[0].ID})
//   		}).success(function(data){
//   			if(data.result_data.VOTE_STATUS == "2"){
//   				var returnUrl = $filter("encodeURI")("#/MeetingManageList");
//   		    	$location.path("/NoticeDecisionDraftCompleteDetail/Complete/" + decisionArray[0].FORMAL_ID+"/"+returnUrl);
//   			}else{
//   				$.alert("项目未完成表决，不能完成决策会通知!");
//			}
//   		});
    	var returnUrl = $filter("encodeURI")("#/MeetingManageList");
	    $location.path("/NoticeDecisionDraftCompleteDetail/Complete/" + decisionArray[0].BUSINESS_ID+"/"+returnUrl);
    };
    $scope.queryAll();
}]);