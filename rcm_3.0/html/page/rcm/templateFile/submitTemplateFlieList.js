ctmApp.register.controller('submitTemplateFlieList', ['$routeParams','$http','$scope','$location','Upload','$filter', 
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	
	if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.paginationConf.queryObj.type = "";
	
	$scope.initData = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.getTemplateFlieList();
	}
	//查询平台公告列表
	$scope.getTemplateFlieList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "templateFileFrom/queryRiskGuidelinesForSubmit.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.notifications = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
	
	//文件下载
    $scope.downLoadFile = function(filePath,fileName){
        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
    }
    // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getTemplateFlieList);
    
    $scope.initData();
}]);
