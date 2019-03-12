ctmApp.register.controller('preDeptWorkList', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	$scope.mypaginationConf = {
        lastCurrentTimeStamp:'',
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
        },
        queryObj : {
        }
    };
	$scope.serviceTypeId = $routeParams.serviceTypeId;
	$scope.areaId = $routeParams.areaId;
	$scope.stages = $routeParams.stages;
	$scope.oldUrl = $routeParams.oldUrl;
	
	if($scope.stages.indexOf("6")>-1){
		$scope.stageName = "已决策";
	}else if($scope.stages.indexOf("5")>-1){
		$scope.stageName = "跟进中";
	}
	
	if($scope.serviceTypeId != null && $scope.serviceTypeId !="" && $scope.serviceTypeId !="\"\""){
		$scope.mypaginationConf.queryObj.serviceTypeId = $scope.serviceTypeId;
		$http({
			method:'post',  
		    url: srvUrl + "dict/getDictItemByItemCode.do",
		    data:$.param({"itemCode":$scope.serviceTypeId})
		}).success(function(result){
			if(result.success){
				$scope.serviceTypeName = result.result_data.ITEM_NAME;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	if($scope.stages != null && $scope.stages !=""){
		$scope.mypaginationConf.queryObj.stages = $scope.stages;
	}
	
	
	if($scope.areaId != null && $scope.areaId !="" && $scope.areaId !="\"\""){
		$scope.mypaginationConf.queryObj.areaId = $scope.areaId;
		$http({
			method:'post',  
		    url: srvUrl + "org/queryByPkvalue.do",
		    data:$.param({"orgPKValue":$scope.areaId})
		}).success(function(result){
			if(result.success){
				$scope.areaName = result.result_data.NAME;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	$scope.getForamlAssessmentList = function(){
		show_Mask();
		$scope.mypaginationConf.queryObj.wf_state = "1,2";
		$scope.mypaginationConf.queryObj.needCreateBy = "0";
		//查询正式评审基本信息列表
		$http({
			method:'post',  
		    url: srvUrl + "preInfo/queryPreList.do",
		    data:$.param({"page":JSON.stringify($scope.mypaginationConf)})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$scope.formalAssessmentList = result.result_data.list;
				$scope.mypaginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	}
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('mypaginationConf.currentPage + mypaginationConf.itemsPerPage', $scope.getForamlAssessmentList);
}]);
