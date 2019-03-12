ctmApp.register.controller('FormalReviewListToday', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url; 
	$scope.queryAll = function () {
        $http({
			method:'post',  
		    url:srvUrl+"decisionReview/queryList.do"
		}).success(function(data){
			if(data.success){
				$scope.decisionReview = data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.queryAll();
}]);
