ctmApp.register.controller('HistoryDecisionReviewList', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	$scope.queryHistoryDecisionReviewListByPage = function () {
		show_Mask();
		$http({
			method:'post',  
		    url:srvUrl+"decisionReview/queryHistoryDecisionReviewListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.historyDecisionReviewList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
    };
    if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
    $scope.executeQueryHistoryDecisionReviewListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryHistoryDecisionReviewListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryHistoryDecisionReviewListByPage);
}]);
