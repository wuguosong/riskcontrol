ctmApp.register.controller('formalReportBoardList', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	$scope.tabIndex = $routeParams.tabIndex;
	$scope.paginationConf.itemsPerPage= 3,
	$scope.initData = function(){
		$scope.getCounts();
		$scope.getApplyList();
		$scope.getTaskList();
		$scope.getRiskList();
		$scope.getMeetingList();
		$scope.getOverMeetingList();
		$scope.getDecisionList();
		$scope.getEndList();
	}
	$('#myTab li:eq('+$scope.tabIndex+') a').tab('show');
	//获取统计数量
	$scope.getCounts = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/getCounts.do",
		}).success(function(result){
			if(result.success){
				$scope.endCount = result.result_data.endCount;
				$scope.applyCount = result.result_data.applyCount;
				$scope.taskCount = result.result_data.taskCount;
				$scope.meetingCount = result.result_data.meetingCount;
				$scope.overMeetingCount = result.result_data.overMeetingCount;
				$scope.riskCount = result.result_data.riskCount;
				$scope.decisionCount = result.result_data.decisionCount;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	//列表查询
	$scope.getAllList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/queryFormalReportBoardListMore.do",
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
	
	//申请阶段列表
	$scope.getApplyList = function(){
		$http({
			method:'post',  
			url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
			data:$.param({"stage":"1","wf_state":"0,1"})
		}).success(function(result){
			if(result.success){
				
				$scope.applyList =result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//终止阶段列表
	$scope.getEndList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
		    data:$.param({"stage":"1","wf_state":"3"})
		}).success(function(result){
			if(result.success){
				
				$scope.endList =result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//分配任务
	$scope.getTaskList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
		    data:$.param({"stage":"2"})
		}).success(function(result){
			if(result.success){
				$scope.taskList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//风控阶段
	$scope.getRiskList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
		    data:$.param({"stage":"3,3.5"})
		}).success(function(result){
			if(result.success){
				$scope.riskList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//安排会议阶段
	$scope.getMeetingList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
		    data:$.param({"stage":"4,5"})
		}).success(function(result){
			if(result.success){
				$scope.meetingList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//已上会议阶段
	$scope.getOverMeetingList = function(){
		$http({
			method:'post',  
			url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
			data:$.param({"stage":"6"})
		}).success(function(result){
			if(result.success){
				$scope.overMeetingList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	//已确认决策通知书阶段
	$scope.getDecisionList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "formalReportBoard/queryFormalReportBoardList.do",
		    data:$.param({"stage":"7"})
		}).success(function(result){
			if(result.success){
				$scope.decisionList = result.result_data.splice(0,3);
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	$scope.initData();
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getAllList);
}]);
