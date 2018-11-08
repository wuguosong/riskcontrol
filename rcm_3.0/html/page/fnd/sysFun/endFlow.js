ctmApp.register.controller('endFlow', ['$routeParams','$http','$scope','$location','Upload','$filter', 
	function ($routeParams,$http,$scope,$location,Upload,$filter) {
	
	$scope.initData = function(){
		$scope.getProjectList();
	}
	
	$scope.getProjectList = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "bpmn/queryProjectListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.pList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		});
	}
	
	$scope.endFlow = function(type,id,reason){
		if("" == reason || null == reason){
			$.alert("终止原因不能为空！");
			return;
		}
//		$.confirm("流程终止后无法恢复！请确认！",function(){
			$http({
				method:'post',  
			    url: srvUrl + "bpmn/endFlow.do",
			    data: $.param({"type":type,"businessId":id,"reason":reason
			    })
			}).success(function(result){
				if(result.success){
					$.alert("操作成功！");
					$scope.getProjectList();
				}else{
					$.alert(result.result_name);
				}
			});
//		});
	}
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getProjectList);
}]);
