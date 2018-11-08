ctmApp.register.controller('DecisionOverviewList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	//大区id
	$scope.pertainareaId = $routeParams.pertainareaId;
	//状态码
	$scope.state = $routeParams.state;
	$scope.areaName;
	
	$scope.changeAreaName = function(){
		if($scope.pertainareaId == "0001N61000000005ZBVA"){
			$scope.areaName = "城市服务";
		}else{
			$scope.areaName = $scope.projects[0].AREANAME;
		}
	}
	
	$scope.queryProjectsByPertainareaid = function(pertainareaId,state){
		$scope.paginationConf.queryObj = {};
		$scope.paginationConf.queryObj.pertainareaId = pertainareaId;
		$scope.paginationConf.queryObj.state = state;
		
		$http({
			method:'post',  
		    url:srvUrl+"reportInfo/queryProjectsByPertainareaid.do", 
		    data:$.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.areaName = data.result_data.paramMap.name;
				$scope.projects = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
//                $scope.changeAreaName()
			}else{
				$.alert(data.result_name);
			}
		});
	}
	
	
	$scope.initData = function(){
		$scope.queryProjectsByPertainareaid($scope.pertainareaId,$scope.state);
	}
	
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.initData);
	$scope.initData();
}]);