ctmApp.register.controller('MeetingVoteWait', ['$http','$scope','$location','$routeParams','$interval','$filter',function ($http,$scope,$location,$routeParams,$interval,$filter) {
	$scope.decisionId = $routeParams.decisionId;
	$scope.oldUrl = $routeParams.url;
	$scope.isAdmin = $routeParams.isAdmin;


	console.log($scope.decisionId);
    //获取正在决策信息
	var initializeInterval;
	$scope.initialize = function () {
		if(null != $scope.decisionId && $scope.decisionId != "" && $scope.decisionId.length > 1){
			//不在当前页面,则移除定时器
			var path = $location.path();
			if(path != "/MeetingVoteWait" && 0 != path.indexOf("/MeetingVoteWait/")){
				try{
					$interval.cancel(initializeInterval);
				}catch(e){}
				return;
			}
			$http({
				method:'post',  
			    url:srvUrl+"decision/getCurrDecisionOpinion.do",
			    data:$.param({"id":$scope.decisionId})
			}).success(function(data){
				if(data.success){
					if(data.result_data.userVoteStatus != null && data.result_data.userVoteStatus == "0"){
						$interval.cancel(initializeInterval);
						$location.path("/MeetingVote/"+$scope.oldUrl);
					}else if(data.result_data.weiJueCeRenShu == 0){
						$interval.cancel(initializeInterval);
						$location.path("/MeetingVoteResult/"+data.result_data.ID+"/"+$scope.oldUrl);
					}else{
						$scope.decision = data.result_data;
                        $scope.yesPeoples = [];
                        $scope.noPeoples = [];
						angular.forEach($scope.decision.meetingLeaders, function (data) {
                           if(data.isVote == "1") {
                               $scope.yesPeoples.push(data);
						   } else {
                               $scope.noPeoples.push(data);
						   }
                        });
					}
				}else{
					$.alert(data.result_name);
				}
			});
		}
    };
    initializeInterval = $interval(function(){
    	$scope.initialize();
    },meetDeciInteTime);
    $scope.initialize();
}]);