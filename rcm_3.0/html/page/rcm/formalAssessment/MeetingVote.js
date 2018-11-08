ctmApp.register.controller('MeetingVote', ['$http','$scope','$location','$routeParams','$interval','$filter', function ($http,$scope,$location,$routeParams,$interval,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.myCurrUrl = "#"+$location.path();
	var initializeInterval;
	$scope.initialize = function () {
		//不在当前页面,则移除定时器
		var path = $location.path();
		if(path != "/MeetingVote" && 0 != path.indexOf("/MeetingVote/")){
			try{
				$interval.cancel(initializeInterval);
			}catch(e){}
			return;
		}
    	$http({
			method:'post',  
		    url:srvUrl+"decision/initialize.do"
		}).success(function(data){
			//取消定时器
			try{
				$interval.cancel(initializeInterval);
			}catch(e){}
			$scope.historyDecisions = null;
			$scope.decision = null;
			$scope.isLoad = false;
			if(data.success){
				//无数据时,启动定时器刷新
				if(null == data.result_data){
					initializeInterval = $interval(function(){
				    	$scope.initialize();
				    },meetDeciInteTime);
				}else if(null != data.result_data.decisionsData){
					//如果当前用户已表决，则直接跳转到等待页
					if(data.result_data.decisionsData.userVoteStatus != null && data.result_data.decisionsData.userVoteStatus =="1"){
						$interval.cancel(initializeInterval);
						$location.path("/MeetingVoteWait/"+data.result_data.decisionsData.ID+"/"+$scope.oldUrl);
					}else{
						$scope.decision = data.result_data.decisionsData;
					}
				}else if(null != data.result_data.historyDecisions){
					initializeInterval = $interval(function(){
						$scope.initialize();
					},meetDeciInteTime);
					
					$scope.historyDecisions = data.result_data.historyDecisions;
				}
				$scope.isLoad = true;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    
    //确认意见
    $scope.enterDecisionOpinion= function () {
    	var aagreeOrDisagree = $("#submitModal input[name='aagreeOrDisagree']").val();
    	$http({
			method:'post',  
		    url:srvUrl+"decision/addDecisionOpinion.do",
		    data: $.param({"id":$scope.decision.ID,"formalId":$scope.decision.FORMAL_ID,"formalType":$scope.decision.FORMAL_TYPE,"aagreeOrDisagree":aagreeOrDisagree})
		}).success(function(data){
			if(data.success){
				if(null != data.result_name && "0" == data.result_name){
					location.reload();
				}else{
					$location.path("/MeetingVoteWait/"+$scope.decision.ID+"/"+$scope.oldUrl);
				}
			}else{
				$.alert(data.result_name);
			}
		});
    }
    $scope.initialize();
}]);