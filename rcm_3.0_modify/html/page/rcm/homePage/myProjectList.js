ctmApp.register.controller('myProjectList', ['$http','$routeParams','$scope','$location','$filter',function ($http,$routeParams,$scope,$location,$filter) {
	$scope.returnUrl = $routeParams.url;
 	
	if(null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	
	$scope.queryMyProjectInfoByPage = function () {
		show_Mask();
        $http({
			method:'post',  
		    url:srvUrl+"workFlow/queryMyProjectInfoByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.myProjectList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		 }).error(function(data, status, headers, config){
			hide_Mask();
		 });
    };
   
    $scope.executeQueryMyProjectInfoByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryMyProjectInfoByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryMyProjectInfoByPage);
}]);
