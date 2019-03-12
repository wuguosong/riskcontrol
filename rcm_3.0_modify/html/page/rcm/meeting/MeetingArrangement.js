ctmApp.register.controller('MeetingArrangement', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	//根据参数决定默认显示的选项
	$scope.tabIndex = $routeParams.tabIndex;
	
	if(null == $scope.paginationConf.queryObj || '' == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	if(null == $scope.paginationConfes.queryObj || '' == $scope.paginationConfes.queryObj){
		$scope.paginationConfes.queryObj = {}	
	}
	
	//--------------------------------------
	//分页查询可以安排上会的项目
	//--------------------------------------
	$scope.queryCanArrangeProjectListByPage = function(){
		$http({
			method:'post',  
		    url:srvUrl+"meeting/queryCanArrangeProjectListByPage.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
			   $scope.canArrangeProjectList = result.result_data.list;
	           $scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		});
	};
	//--------------------------------------
	//查询  所有 暂存未提交的项目
	//--------------------------------------
	$scope.queryNotSubmitProjectList = function(){
		$http({
			method:'post',  
		    url:srvUrl+"meeting/queryNotSubmitProjectList.do"
		}).success(function(result){
			if(result.success){
			   $scope.notSubmitProjectList = result.result_data;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//--------------------------------------
	//暂存没提交的项目
	//--------------------------------------
	$scope.toAddNotSubmitProject = function(){
		var canArrangeProjectCheckboxs = document.getElementsByName("canArrangeProjectCheckbox");
        var projectArray = new Array(),num=0;
        for(var i=0;i<canArrangeProjectCheckboxs.length;i++)
        {
            if(canArrangeProjectCheckboxs[i].checked)
            {
            	projectArray[num] = canArrangeProjectCheckboxs[i].value;
                num++;
            }
        }
        if(projectArray.length == 0){
        	$.alert("请至少选择一个项目暂存！");
            return false;
        }
        show_Mask();
        $http({
			method:'post',  
		    url:srvUrl+"meeting/addNotSubmitProject.do", 
		    data: $.param({"projectJson":JSON.stringify(projectArray)})
		}).success(function(result){
			if(result.success){
				$scope.queryCanArrangeProjectListByPage();
				$scope.queryNotSubmitProjectList();
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//--------------------------------------
	//移除未提交的项目
	//--------------------------------------
	$scope.toRemoveNotSubmitProject = function(){
		var notSubmitProjectCheckboxs = document.getElementsByName("notSubmitProjectCheckbox");
        var projectArray = new Array(),num=0;
        for(var i=0;i<notSubmitProjectCheckboxs.length;i++)
        {
            if(notSubmitProjectCheckboxs[i].checked)
            {
            	projectArray[num] = notSubmitProjectCheckboxs[i].value;
                num++;
            }
        }
        if(projectArray.length == 0){
        	$.alert("请选择其中一条或多条要移除的项目!");
            return false;
        }
		$.confirm("确认要移除选中的项目吗？", function(){
	        $http({
				method:'post',  
			    url:srvUrl+"meeting/removeNotSubmitProject.do", 
			    data: $.param({"projectJson":JSON.stringify(projectArray)})
			}).success(function(result){
				if(result.success){
					$scope.queryCanArrangeProjectListByPage();
					$scope.queryNotSubmitProjectList();
				}else{
					$.alert(result.result_name);
				}
			});
		});
	}
	//--------------------------------------
	//确认提交上会项目按钮事件
	//--------------------------------------
	$scope.toApply = function(){
		var notSubmitProjectCheckboxs = document.getElementsByName("notSubmitProjectCheckbox");
        if(0 == notSubmitProjectCheckboxs.length){
        	$.alert("请选择需要通知上会的项目！");return;
        }
	    $.confirm("提交上会 包含所有暂存项目，确认要继续吗？", function(){
	    	//使用$location.path不能立即跳转，故用原始!
	    	window.location.href="#/meeting/MeetingSubmit";
	    });
	};
	//--------------------------------------
	//根据条件分页查询已安排的会议
	//--------------------------------------
	$scope.querySubmitMeetingListByPage = function(){
		$http({
			method:'post',  
		    url:srvUrl+"meetingIssue/queryListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			if(result.success){
			   $scope.submitMeetingList = result.result_data.list;
	           $scope.paginationConfes.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	$scope.initData = function(){
		$scope.queryCanArrangeProjectListByPage();
		$scope.queryNotSubmitProjectList();
		$scope.querySubmitMeetingListByPage();
	}
	$scope.initData();
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryCanArrangeProjectListByPage);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.querySubmitMeetingListByPage);

	//--------------------------------------
	//更新会议项目信息
	//--------------------------------------
    $scope.updateMeetingInfo = function(){
    	var submitProjectCheckboxs = document.getElementsByName("submitProjectCheckbox");
        var projectArray = new Array(),num=0;
        for(var i=0;i<submitProjectCheckboxs.length;i++)
        {
            if(submitProjectCheckboxs[i].checked)
            {
            	projectArray[num] = submitProjectCheckboxs[i].value;
                num++;
            }
        }
        if(projectArray.length != 1){
        	$.alert("请选择一条 会议期次信息！");
            return false;
        }
    	$location.path("/MeetingUpdate/"+projectArray[0]);
	};
}]);