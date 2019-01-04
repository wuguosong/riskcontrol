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
					console.log($scope.decision);
					$scope.decision = data.result_data;
                    $scope.decision.tongYiCountStyle = [];
                    $scope.decision.tongYiCountStyle.width = $scope.decision.tongYiCount/$scope.decision.zongRenShu*100 + "%";
                    $scope.decision.buTongYiCountStyle = [];
                    $scope.decision.buTongYiCountStyle.width = $scope.decision.buTongYiCount/$scope.decision.zongRenShu*100 + "%";
                    $scope.decision.tiaoJianTongYiCountStyle = [];
                    $scope.decision.tiaoJianTongYiCountStyle.width = $scope.decision.tiaoJianTongYiCount/$scope.decision.zongRenShu*100 + "%";
                    $scope.decision.zeQiShangHuiCountStyle = [];
                    $scope.decision.zeQiShangHuiCountStyle.width = $scope.decision.zeQiShangHuiCount/$scope.decision.zongRenShu*100 + "%";
                    console.log($scope.decision);
				}
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.initialize();
}]);