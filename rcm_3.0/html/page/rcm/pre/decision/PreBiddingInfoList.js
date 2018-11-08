ctmApp.register.controller('PreBiddingInfoList', ['$http','$scope','$location','$routeParams','$filter',
function ($http,$scope,$location,$routeParams,$filter) {
	
	$scope.tabIndex = $routeParams.tabIndex;
//	$scope.queryObject = {};
//	$scope.hi_queryObject = {};
	if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};
	}
	if($scope.paginationConfes.queryObj == null || $scope.paginationConfes.queryObj == ''){
		$scope.paginationConfes.queryObj = {};
	}
	
	//初始化页面所需数据
	$scope.initData = function(){
		$scope.queryUncommittedByPage();
		$scope.querySubmittedByPage();
	}
	
	//获取待提交评审报告的项目
	$scope.queryUncommittedByPage = function(){	
//		$scope.dataJson = {
//				projectName:$scope.queryObject.PROJECT_NAME,
//				createBy:$scope.queryObject.CREATEBY,
//				pertainareaname:$scope.queryObject.PERTAINAREANAME
//		}
		$http({
			method:'post',  
		    url:srvUrl+"preBidding/queryUncommittedByPage.do", 
		    data: $.param({
		    			"page":JSON.stringify($scope.paginationConf),
		    			"json":JSON.stringify($scope.paginationConf.queryObj)
		    			})
		}).success(function(data){
			if(data.success){
				$scope.uncommittedReport = data.result_data.list;
				$scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		}).error(function(data,status,headers,config){
			$.alert(status);
		});
    };
    
    //获取已提交评审报告的项目
    $scope.querySubmittedByPage = function(){
//    	$scope.dataJson = {
//    			projectName:$scope.queryObject.HI_PROJECT_NAME,
//    			createBy:$scope.hi_queryObject.HI_CREATEBY,
//    			pertainareaname:$scope.hi_queryObject.HI_PERTAINAREANAME
//    	}
    	$http({
    		method:'post',
    		url:srvUrl+'preBidding/querySubmittedByPage.do',
    		 data: $.param({
	    			"page":JSON.stringify($scope.paginationConfes),
	    			"json":JSON.stringify($scope.paginationConfes.queryObj)
	    			})
    	}).success(function(data){
    		if(data.success){
    			$scope.submittedReport = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
    		}else{
    			$.alert(data.result_name);
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    };
    
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryUncommittedByPage);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.querySubmittedByPage);
	
	$scope.toFormalBiddingInfo = function(){
		var chkObjs = $("input[type=checkbox][name=uncommittedDecisionMaterialCheckbox]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要提交的数据！");
    		return false;
    	}
    	if(chkObjs.length > 1){
    		$.alert("请只选择一条数据进行提交!");
    		return false;
    	}
    	var idsStr = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		var chkValue = chkObjs[i].value.split("/") ;
    		var chkValue_len = chkValue.length;
    		idsStr = idsStr + chkValue[chkValue_len - 1];
    	}
		$location.path("/PreBiddingInfo/"+idsStr+"@0/"+$filter('encodeURI')('#/PreBiddingInfoList/0'));
	}
	$scope.initData();
}]);