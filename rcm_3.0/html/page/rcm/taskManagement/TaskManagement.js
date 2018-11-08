//待办
ctmApp.register.controller('MyTask', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	$scope.queryMyTaskByPage = function(){
		$http({
			method:'post',  
		    url:srvUrl+"workFlow/queryMyTaskByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				  $scope.tasks = result.result_data.list;
		          $scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.executeQueryMyTaskByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryMyTaskByPage();
	};
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryMyTaskByPage);
}]);

//已办
ctmApp.register.controller('OverTask', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	$scope.queryOverTaskByPage = function(){
		$http({
			method:'post',  
		    url:srvUrl+"workFlow/queryOverTaskByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				  $scope.tasks = result.result_data.list;
		          $scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.executequeryOverTaskByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryOverTaskByPage();
	};
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryOverTaskByPage);
}]);

//已完成
ctmApp.register.controller('CompletedTask', ['$http','$scope','$location', function ($http,$scope,$location) {
	$scope.queryCompletedTaskByPage = function(){
		$http({
			method:'post',  
		    url:srvUrl+"workFlow/queryCompletedTaskByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				  $scope.tasks = result.result_data.list;
		          $scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.executeQueryCompletedTaskByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryCompletedTaskByPage();
	};
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryCompletedTaskByPage);
}]);