ctmApp.register.controller('MeetingVoteResult', ['$http','$scope','$location','$routeParams','$filter', function ($http,$scope,$location,$routeParams,$filter) {
	$scope.decisionId = $routeParams.decisionId;
	$scope.oldUrl = $routeParams.url;
	$scope.initialize = function () {
		$scope.back();
        $scope.isShowInfo();
    	$http({
			method:'post',  
		    url:srvUrl+"decision/getDecisionResultNew.do",
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
	// 判断权限，设置返回
	$scope.back = function (){
		if ($scope.oldUrl == $filter("encodeURI")("#/MeetingManageList")) {
            $scope.userRole = $scope.credentials.roles;
            var count = 0;
            angular.forEach($scope.userRole, function (data, index) {
                if (data.CODE == '3' || data.CODE == '1'){
                    count = count + 1;
                }
            });
            if (count == 0){
                $scope.oldUrl = $filter("encodeURI")("#/");
            }
		}
    };

	// 判断是否有查看详细结果功能
	$scope.isShowInfo = function () {
        var currentUserRoles = $scope.credentials.roles;
        $scope.showFlag = false;
        angular.forEach(currentUserRoles, function (data, index, array) {
            if (data.CODE == '1') { // 风控部门管理员角色
                $scope.showFlag = true;
            }
        });
	};

	// 弹出结果页面
	$scope.showResltInfo = function () {
        $('#showVoteResultModel').modal('show');
	};

    $scope.initialize();
}]);