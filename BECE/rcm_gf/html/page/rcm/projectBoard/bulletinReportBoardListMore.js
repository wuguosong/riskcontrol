ctmApp.register.controller('bulletinReportBoardListMore', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	$scope.stage = $routeParams.stage;
	$scope.wf_state = $routeParams.wf_state;
	$scope.paginationConf.queryObj = {'stage':$scope.stage,"wf_state":$scope.wf_state};
	$scope.oldUrl = $routeParams.url;
	
	//var durl = unescape(decodeURIComponent(window.atob($scope.oldUrl)));
	//var tabIndex = durl.substring(durl.length-1, durl.length);
	if("1" == $scope.stage){
		$scope.prefix="申请阶段";
	}else if("1.5,2" == $scope.stage){
		$scope.prefix="跟进中";
	}else if("3" == $scope.stage){
		$scope.prefix="安排过会";
	}else if("4" == $scope.stage){
		$scope.prefix="已过会";
	}else if("5" == $scope.stage){
		$scope.prefix="会议纪要";
	}else if("3" == $scope.wf_state){
		$scope.prefix="已终止";
	}
	
	$scope.initData = function(){
		$scope.getMoreList();
	}
	
	
	$scope.getMoreList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "bulletinReportBoard/queryInformationListMore.do",
		    data:$.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
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
