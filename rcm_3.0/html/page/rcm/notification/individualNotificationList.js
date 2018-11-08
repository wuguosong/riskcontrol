ctmApp.register.controller('individualNotificationList', ['$routeParams','$http','$scope','$location','Upload','$filter', 
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	
	$scope.oldUrl = $routeParams.url;
	if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.paginationConf.queryObj.type = "";
	
	$scope.initData = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.getIndividualNotificationList();
		$scope.queryDicCodeOfnotification();
	}
	//查询平台公告列表
	$scope.getIndividualNotificationList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "notificationFlatform/queryNotifByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.notifications = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		}).error(function(data,status,headers, config){
  			$.alert(status);
  		});
	}
	
    // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getIndividualNotificationList);
    
    //查询公告类型
	$scope.queryDicCodeOfnotification = function(){
		$http({
			method:'post',  
		    url: srvUrl + "dict/queryDictItemByDictTypeCode.do",
		    data: $.param({"code":'TYGGLX'})
		}).success(function(result){
			$scope.notificationsDicCode = result.result_data;
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
    
    $scope.initData();
}]);
