ctmApp.register.controller('preReportBoardListMore', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	$scope.stage = $routeParams.stage;
	$scope.wf_state = $routeParams.wf_state;
	$scope.paginationConf.queryObj = {'stage':$scope.stage,"wf_state":$scope.wf_state};
	$scope.oldUrl = $routeParams.url;
	
	var durl = unescape(decodeURIComponent(window.atob($scope.oldUrl)));
	var tabIndex = durl.substring(durl.length-1, durl.length);
	if("0" == tabIndex){
		$scope.prefix="申请阶段";
	}else if("1" == tabIndex){
		$scope.prefix="分配任务阶段";
	}else if("2" == tabIndex){
		$scope.prefix="风控处理阶段";
	}else if("3" == tabIndex){
		$scope.prefix="安排过会阶段";
	}else if("4" == tabIndex){
		$scope.prefix="已过会阶段";
	}else if("5" == tabIndex){
		$scope.prefix="已确认决策通知书阶段";
	}else if("6" == tabIndex){
		$scope.prefix="终止阶段";
	}
	
	$scope.initData = function(){
		$scope.getMoreList();
	}
	
	
	$scope.getMoreList = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "preReportBoard/queryPreReportBoardListMore.do",
		    data:$.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$scope.moreList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	$scope.initData();
     //通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getMoreList);
}]);
