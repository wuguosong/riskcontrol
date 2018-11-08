ctmApp.register.controller('MeetingVoteResult', ['$http','$scope','$location','$routeParams','$filter', function ($http,$scope,$location,$routeParams,$filter) {
	$scope.decisionId = $routeParams.decisionId;
	$scope.oldUrl = $routeParams.url;
	$scope.initialize = function () {
    	$http({
			method:'post',  
		    url:srvUrl+"decision/getDecisionResult.do",
		    data:$.param({"id":$scope.decisionId})
		}).success(function(data){
			if(data.success){
				if(data.result_data.userVoteStatus != null && data.result_data.userVoteStatus =="1"){
					$location.path("/MeetingVoteWait/"+data.result_data.ID+"/"+$scope.oldUrl);
				}else{
					$scope.decision = data.result_data;
				}
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.initialize();
}]);