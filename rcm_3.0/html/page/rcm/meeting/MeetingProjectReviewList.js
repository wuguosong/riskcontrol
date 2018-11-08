ctmApp.register.controller('MeetingProjectReviewList', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {
	$scope.meetingId = $routeParams.meetingId;
	$scope.oldUrl = $location.path();
	
	if(null == $scope.paginationConf.queryObj || '' == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.paginationConf.queryObj.meetingId = $scope.meetingId;
	
	$scope.queryMeetingProjectListByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"meetingIssue/queryMeetingProjectListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.projectList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    
    //1:验证是否为未过会
    $scope.cancelProject = function () {
    	var projectArray = getSelectProject();
    	if (projectArray.length == 0) {
        	$.alert("请至少选择一个未过会的项目！");
            return false;
        }
    	$http({
   			method:'post',  
   		    url:srvUrl+"decision/queryByIds.do",
		    data: $.param({"projectJson":JSON.stringify(projectArray)})
   		}).success(function(data){
   			var isGo = data.success;
   			var decisions = data.result_data;
   			for(var x = 0; x < decisions.length; x++){
   				if(decisions[x].VOTE_STATUS != 0){
   					isGo = false;
   	   				break;
   	   			}
   			}
   			if(isGo){
   				$scope.cancelProject2(projectArray);
   			}else{
   				$.alert("选中项目含有[表决中或已表决或已暂存]的项目，请选择一个未表决的项目！");
			}
   		});
    }
    
    //2:验证项目撤消的项目是否为最后一个
    $scope.cancelProject2 = function (projectArray) {
    	$http({
   			method:'post',  
   		    url:srvUrl+"meeting/countProject.do",
		    data: $.param({"meetingIssueId":projectArray[0].MEETING_ISSUE_ID})
   		}).success(function(data){
   			var message = "确认要撤消吗？";
   			var countProject = data.result_data;
   			if(countProject == projectArray.length){
   				message = "如果撤消选中的项目则会撤消本次会议！确认是否要继续？";
			}
   			$scope.removeMeetingProject(projectArray,message,countProject);
   		});
    }
    
    //3:撤消上会
    $scope.removeMeetingProject = function (projectArray,message,countProject) {
		$.confirm(message, function() {
	    	$http({
				method:'post',  
			    url:srvUrl+"meeting/removeMeetingProject.do",
			    data: $.param({"projectJson":JSON.stringify(projectArray)})
			}).success(function(data){
				if(data.success){
					//撤消整个会议后，跳转到会议已安排页面
					if(countProject == projectArray.length){
						$location.path("/meeting/MeetingArrangement/1");
					}else{
						$scope.queryMeetingProjectListByPage();
					}
				}else{
					$.alert(data.result_name);
				}
			});
    	});
    }
    
    //获取选中的项目(决策表ID)
    function getSelectProject(){
    	var chk_list = document.getElementsByName("checkbox");
        var projectArray = new Array();
        for (var i = 0,length = chk_list.length; i < length; i++) {
            if (chk_list[i].checked) {
            	for(var x = 0,size = $scope.projectList.length; x < size; x++){
    	        	if(chk_list[i].value == $scope.projectList[x].DECISION_ID){
    	        		var project = $scope.projectList[x];
    	        		projectArray.push(project);
    	        		break;
    	        	}
    	        }
            }
        }
        return projectArray;
    }

    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryMeetingProjectListByPage);
}]);
