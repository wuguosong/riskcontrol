ctmApp.register.controller('DataOptionView', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
	$scope.uuid = $routeParams.uuid;
    $scope.fk_id = $routeParams.fk_Id;

    //取消操作
    $scope.cancel = function () {
        $location.path("/DataOptionList/"+$scope.fk_id);
    };
    
    $scope.getOptionByID = function () {
        $http({
			method:'post', 
			url: srvUrl + "dict/getDictItemById.do",
			data: $.param({"uuid": $scope.uuid})
		}).success(function (data) {
			if (data.success) {
				$scope.item = data.result_data;
			}
			else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.getOptionByID();
}]);
