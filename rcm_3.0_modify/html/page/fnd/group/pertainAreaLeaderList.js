ctmApp.register.controller('pertainAreaLeaderList', ['$http','$routeParams','$scope','$location','$routeParams','$filter',function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
    $scope.initData = function(){
    	$scope.listAll();
	};
	
	$scope.listAll = function(){
		$http({
			method:'post',  
		    url:srvUrl+"pertainArea/queryList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.leaderList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	$scope.update = function(){
		
		if($("input[type=checkbox][name=checkbox]:checked").length > 1){
			$.alert("只能选择一条数据！");
			return false;
		}
		if($("input[type=checkbox][name=checkbox]:checked").length == 0){
			$.alert("请选择要修改的数据！");
			return false;
		}
		var id = $("input[type=checkbox][name=checkbox]:checked").val();
		var url = $filter("encodeURI")("#/pertainAreaLeaderList");
		
		$location.path("/pertainAreaDetail/update/"+id+"/"+url);
    };
	
    $scope.delete = function(){
		if($("input[type=checkbox][name=checkbox]:checked").length > 1){
			$.alert("只能选择一条数据！");
			return false;
		}
		if($("input[type=checkbox][name=checkbox]:checked").length == 0){
			$.alert("请选择要删除的数据！");
			return false;
		}
		var userId = $("input[type=checkbox][name=checkbox]:checked").val();
		$.confirm("确认要删除吗?", function(){
			$http({
				method:'post',  
			    url:srvUrl+"pertainArea/deleteByUserId.do",
			    data: $.param({"userId":userId})
			}).success(function(result){
				if(result.success){
					$.alert(result.result_name);
					$scope.initData();
				}else{
					$.alert(result.result_name);
				}
			});
		});
    };
    
    $scope.initData();
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.listAll);
}]);
