
ctmApp.register.controller('wscall', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {

    $scope.listAll = function(){
        $http({
			method:'post',  
		    url:srvUrl+"wscall/queryByPage.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.logs = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    }
    $scope.repeatCallOne = function(id){
    	$http({
			method: 'post',  
		    url: srvUrl + "wscall/repeatCallOne.do",
		    data: $.param({"id": id})
		}).success(function(data){
			$.alert(data.result_name);
			$scope.listAll();
		});
    }
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.listAll);
}]);


ctmApp.register.controller('JournalDetail', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
    $scope.findById = function(id){
        $scope.httpData('exception/Journal/getByID',{id:id}).success(function(data){
            if(data.result_code == "S"){
                $scope.l = data.result_data;
            }
        });
    }
    $scope.findById($routeParams.id);
}]);
