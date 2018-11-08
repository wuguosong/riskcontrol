ctmApp.register.controller('NoticeDecisionConfirmList', ['$http','$scope','$routeParams','$location', 
function ($http,$scope,$routeParams,$location) {
	$scope.tabIndex =  $routeParams.tabIndex;
	$scope.orderby='desc';
//    $scope.paginationConf.queryObj = {userId:$scope.credentials.UUID};
//    $scope.paginationConfes.queryObj = {userId:$scope.credentials.UUID};

    
	//查询决策通知书待确认列表
	$scope.queryWaitConfirm = function(){
		$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionConfirmInfo/queryWaitConfirm.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.formalAssessmentList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	//查询决策通知书已确认列表
	$scope.queryConfirmed = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionConfirmInfo/queryConfirmed.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.formalAssessmentListed = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
			hide_Mask();
		 }).error(function(data, status, headers, config){
			hide_Mask();
		 });
	}
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryWaitConfirm);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.queryConfirmed);

}]);
