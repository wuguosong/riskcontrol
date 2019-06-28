ctmApp.register.controller('WaitDecisionReviewList', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {
	$scope.queryWaitDecisionListByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"decisionReview/queryWaitDecisionListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.waitDecisionList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    $scope.queryWaitDecisionListByPage();
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryWaitDecisionListByPage);
}]);
