ctmApp.register.controller('PreInfoList', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	
	var tabIndex = $routeParams.tabIndex;
	
	//控制具体显示那个tab标签页  012
	$('#myTab li:eq('+tabIndex+') a').tab('show');
	$scope.tabIndex = tabIndex;
	
	//按钮控制器
	$scope.initData = function(){
		$scope.getPreList();
		$scope.getPreSubmitedList();
	}
	//查询正式评审基本信息列表--起草中
	$scope.getPreList = function(){
		if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
			$scope.paginationConf.queryObj = {};
		}
		$scope.paginationConf.queryObj.needCreateBy = '1';
		$http({
			method:'post',  
		    url: srvUrl + "preInfo/queryPreList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.preList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	//查询正式评审基本信息列表--已提交
	$scope.getPreSubmitedList = function(){
		show_Mask();
		$http({
			method:'post',  
			url: srvUrl + "preInfo/queryPreSubmitedList.do",
			data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.preSubmitedList = result.result_data.list;
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
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getPreList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getPreSubmitedList);
}]);
