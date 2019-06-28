ctmApp.register.controller('bulletinReportBoardList', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	$scope.tabIndex = $routeParams.tabIndex;
	$scope.paginationConf.itemsPerPage= 3,
	$scope.initData = function(){
		$scope.getCounts();
		$scope.getApplyList();
		$scope.getTaskList();
		$scope.getMeetingList();
		$scope.getOverMeeting();
		$scope.getSummaryedList();
		$scope.getEndList();
	}
	$('#myTab li:eq('+$scope.tabIndex+') a').tab('show');
	//获取统计数量
	$scope.getCounts = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/getCounts.do",
		}).success(function(result){
			if(result.success){
				$scope.applyCount = result.result_data.applyCount;
				$scope.taskCount = result.result_data.taskCount;
				$scope.meetingCount = result.result_data.meetingCount;
				$scope.overMeetingCount = result.result_data.overMeetingCount;
				$scope.summaryedCount = result.result_data.summaryedCount;
				$scope.endCount = result.result_data.endCount;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	//列表查询
	$scope.getAllList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/queryInformationListMore.do",
		    data:$.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.allList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//申请中
	$scope.getApplyList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/queryPreReportBoardList.do",
		    data:$.param({"stage":"1","wf_state":"0,1"})
		}).success(function(result){
			if(result.success){
				$scope.applyList =result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//跟进中
	$scope.getTaskList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/queryPreReportBoardList.do",
		    data:$.param({"stage":"1.5,2"})
		}).success(function(result){
			if(result.success){
				$scope.taskList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	//安排会议
	$scope.getMeetingList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/queryPreReportBoardList.do",
		    data:$.param({"stage":"3"})
		}).success(function(result){
			if(result.success){
				$scope.meetingList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//已上会
	$scope.getOverMeeting = function(){
		$http({
			method:'post',  
			url: srvUrl + "bulletinReportBoard/queryPreReportBoardList.do",
			data:$.param({"stage":"4"})
		}).success(function(result){
			if(result.success){
				$scope.overMeetingList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//会议纪要 
	$scope.getSummaryedList = function(){
		$http({
			method:'post',  
			url: srvUrl + "bulletinReportBoard/queryPreReportBoardList.do",
			data:$.param({"stage":"5"})
		}).success(function(result){
			if(result.success){
				$scope.summaryList =result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//终止阶段列表
	$scope.getEndList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/queryPreReportBoardList.do",
		    data:$.param({"stage":"1","wf_state":"3"})
		}).success(function(result){
			if(result.success){
				$scope.endList =result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	$scope.initData();
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getAllList);
}]);
