
ctmApp.register.controller('bpmn', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
	$scope.wait={};
	$scope.deploy_deploy = function(){
		var url = srvUrl+"bpmn/deploy.do";
		var params={"bpmnType": $scope.deploy_bpmnType};
		$http({
			method:'post',  
		    url:url, 
		    params: params
		}).success(function(data){
			if(data.success){
				$.alert("执行成功！");
			}else{
				$.alert(data.result_name);
			}
		});
	};
	$scope.process_clear = function(){
		var url = srvUrl+"bpmn/stopProcess.do";
		
		var params = {
			"bpmnType": $scope.process_bpmnType,
			"businessKey": $scope.process_businessId
		};
		$http({
			method:'post',  
		    url:url, 
		    params: params
		}).success(function(data){
			if(data.success){
				$.alert("执行成功！");
			}else{
				$.alert(data.result_name);
			}
		});
	};
	$scope.project_clear = function(){
		$.confirm("删除后不可恢复，请慎重！",function(a){
			var url = srvUrl+"bpmn/clear.do";
			var params = {
				"bpmnType": $scope.project_bpmnType,
				"businessKey": $scope.project_businessId
			};
			$http({
				method:'post',  
			    url:url, 
			    params: params
			}).success(function(data){
				if(data.success){
					$.alert("执行成功！");
				}else{
					$.alert(data.result_name);
				}
			});
		});
	};
	$scope.notice_repeatCallOne = function(){
		var url = srvUrl + "wscall/repeatCallByNoticeId.do";
		var params = {
			"id": $scope.noticeOfDecisionId
		};
		$http({
			method:'post',  
		    url:url, 
		    params: params
		}).success(function(data){
			$.alert(data.result_name);
		});
		
	}
	
	$scope.notice_initReportStatus = function(){
		var url = srvUrl + "wscall/initReportStatus.do";
		var params = {
			"id": $scope.noticeOfDecisionId
		};
		$http({
			method:'post',  
		    url:url, 
		    params:{"params":$scope.daiBan}
		}).success(function(data){
			$.alert(data.result_name);
		});
	}
	$scope.sendTask = function(){
		
		if($scope.wait.taskId == "" || $scope.wait.taskId == null || $scope.wait.taskId == undefined){
			$.alert("taskId不能为空！");
			return;
		}
		if($scope.wait.url == "" || $scope.wait.url == null || $scope.wait.url == undefined){
			$.alert("路径不能为空！");
			return;
		}
		if($scope.wait.createDate == "" || $scope.wait.createDate == null || $scope.wait.createDate == undefined){
			$.alert("创建日期不能为空！");
			return;
		}
		if($scope.wait.title == "" || $scope.wait.title == null || $scope.wait.title == undefined){
			$.alert("标题不能为空！");
			return;
		}
		if($scope.wait.owner == "" || $scope.wait.owner == null || $scope.wait.owner == undefined){
			$.alert("接受者ID不能为空！");
			return;
		}
		if($scope.wait.status == "" || $scope.wait.status == null || $scope.wait.status == undefined){
			$.alert("状态必选！");
			return;
		}
		if($scope.wait.sender == "" || $scope.wait.sender == null || $scope.wait.sender == undefined){
			$.alert("发送人姓名不能为空！");
			return;
		}
		
		var url = srvUrl + "wscall/sendTask.do";
		$http({
			method:'post',  
			url:url, 
			data: $.param({"json":JSON.stringify($scope.wait)})
		}).success(function(data){
			if(data.success){
				$.alert("发送成功！");
			}else{
				$.alert(data.result_name);
			}
		});
	}
	$scope.executeTargetInterface = function(){
		$http({
			method:'post',  
			url:srvUrl + "wscall/initWithJson.do", 
			data: $.param({"beanName":$scope.beanName,"json":$scope.beanParam})
		}).success(function(data){
			if(data.success){
				$.alert(data.result_name);
			}else{
				$.alert(data.result_name);
			}
		});
	}
}]);