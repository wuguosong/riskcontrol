ctmApp.register.controller('JournalInfo', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
	$scope.id = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.getByID = function(){
        $http({
			method:'POST',  
		    url:srvUrl+"journal/queryById.do", 
		    data: $.param({"id":$scope.id})
		}).success(function(data){
			if(data.success){
				$scope.log = data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
    }
    $scope.getByID();
}]);
