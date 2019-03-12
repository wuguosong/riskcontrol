ctmApp.register.controller('DataDictionaryView', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
    //初始化
	$scope.uuid = $routeParams.uuid;//getUrlParam("userCode");

    //回显数据
    $scope.getDictionaryByID = function () {
        $http({
			method:'post', 
			url: srvUrl + "dict/getDictTypeById.do",
			data: $.param({"uuid": $scope.uuid})
		}).success(function (data) {
			if (data.success) {
				$scope.dictionary = data.result_data;
			}
			else{
				$.alert(data.result_name);
			}
		});
    };

    $scope.getDictionaryByID();
}]);
