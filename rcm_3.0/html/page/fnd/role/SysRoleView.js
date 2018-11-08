ctmApp.register.controller('SysRoleView', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
    $scope.role = {};
	$scope.roleId =  $routeParams.roleId;
	$scope.returnUrl =  $routeParams.url;
	
	$scope.queryRoleById = function(){
		$http({
			method:'post',  
		    url: srvUrl + "role/queryById.do",
		    data: $.param({"id":$scope.roleId})
		}).success(function(result){
			$scope.role = result.result_data;
		});
	}
    
    $scope.initData = function(){
    	$scope.queryRoleById();
    }
    $scope.initData();
}]);