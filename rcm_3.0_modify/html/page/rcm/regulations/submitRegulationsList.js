ctmApp.register.controller('submitRegulationsList', ['$routeParams','$http','$scope','$location','Upload','$filter', 
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	
	if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.paginationConf.queryObj.type = "";
	
	$scope.initData = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.getRegulationsList();
	}
	//查询平台公告列表
	$scope.getRegulationsList = function(){
		$http({
			method:'post',  
			url: srvUrl + "regulationsFrom/queryRegulationsForSubmit.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.notifications = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
	
	//修改规章制度
	$scope.modifyRegulations = function(){
		var chkObjs = $("input[type=checkbox][name=notifications_checkbox]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要修改的数据！");
    		return false;
    	}
    	
    	if(chkObjs.length > 1){
    		$.alert("请只选择一条数据进行修改!");
    		return false;
    	}
    	var idsStr = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		idsStr = idsStr + chkObjs[i].value + ",";
    	}
    	idsStr = idsStr.substring(0, idsStr.length - 1);
    	
    	$location.path("/regulationsInfo/Modify/" + idsStr+"/"+$filter('encodeURI')('#/regulationsList'));
	}
	
	//文件下载
    $scope.downLoadFile = function(filePath,fileName){
        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
    }
    // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRegulationsList);
    
    $scope.initData();
}]);
