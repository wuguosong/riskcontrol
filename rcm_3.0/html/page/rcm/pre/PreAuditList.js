ctmApp.register.controller('PreAuditList', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	
	var tabIndex = $routeParams.tabIndex;
	
	//控制具体显示那个tab标签页  012
	$('#myTab li:eq('+tabIndex+') a').tab('show');
	$scope.tabIndex = tabIndex;
	
	//按钮控制器
	$scope.initData = function(){
		$scope.getPreWaitList();
		$scope.getPreAuditList();
	}
	
	//查询正式评审列表--待办
	$scope.getPreWaitList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preAudit/queryWaitList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.preWaitList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	
	//查询正式评审列表--已办
	$scope.getPreAuditList = function(){
		show_Mask();
		$http({
			method:'post',  
			url: srvUrl + "preAudit/queryAuditedList.do",
			data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.preAuditList = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
	}
	
    $scope.order=function(o,v){
        if(o=="time"){
            $scope.orderby=v;
            $scope.orderbystate=null;
            if(v=="asc"){
                $("#orderasc").addClass("cur");
                $("#orderdesc").removeClass("cur");
            }else{
                $("#orderdesc").addClass("cur");
                $("#orderasc").removeClass("cur");
            }
        }else{
            $scope.orderbystate=v;
            $scope.orderby=null;
            if(v=="asc"){
                $("#orderascstate").addClass("cur");
                $("#orderdescstate").removeClass("cur");
            }else{
                $("#orderdescstate").addClass("cur");
                $("#orderascstate").removeClass("cur");
            }
        }
//        $scope.getForamlAssessmentList();
    }
    
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getPreWaitList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getPreAuditList);
}]);
