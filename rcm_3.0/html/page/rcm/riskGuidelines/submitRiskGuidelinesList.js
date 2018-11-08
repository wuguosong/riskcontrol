ctmApp.register.controller('submitRiskGuidelinesList', ['$routeParams','$http','$scope','$location','Upload','$filter', 
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	
	if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.paginationConf.queryObj.type = "";
	
	$scope.initData = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.getRiskGuidelinesList();
		$scope.queryDicCodeOfnotification();
	}
	//查询平台公告列表
	$scope.getRiskGuidelinesList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "riskGuidelinesform/queryRiskGuidelinesForSubmit.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.notifications = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
	
	//新增公告
	$scope.addRiskGuideline = function(){
		$location.path("/riskGuidelineInfo/Create/0/"+$filter('encodeURI')('#/riskGuidelinesList'));
	}
	
	//修改公告
	$scope.modifyRiskGuideline = function(){
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
    	
    	$location.path("/riskGuidelineInfo/Modify/" + idsStr+"/"+$filter('encodeURI')('#/riskGuidelinesList'));
	}
	
	//删除公告
	$scope.deleteRiskGuideline = function(){
		var chkObjs = $("input[type=checkbox][name=notifications_checkbox]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要删除的数据！");
    		return false;
    	}
    	
    	$.confirm("删除后不可恢复，确认删除吗？", function() {
    		var idsStr = "";
        	for(var i = 0; i < chkObjs.length; i++){
        		var chkValue = chkObjs[i].value.split("/") ;
        		var chkValue_len = chkValue.length;
        		idsStr = idsStr + chkValue[chkValue_len - 1] + ",";
        	}
        	idsStr = idsStr.substring(0, idsStr.length - 1);
        	$http({
    			method:'post',  
    		    url:srvUrl+"riskGuidelinesform/deleteRiskGuideline.do", 
    		    data: $.param({"ids": idsStr})
    		}).success(function(data){
    			if(data.success){
    				$scope.getRiskGuidelinesList();
    			}else{
    				$.alert(data.result_name);
    			}
    		}).error(function(data,status,headers, config){
        		$.alert(status);
        	});
    	});
	}
	
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
	//文件下载
    $scope.downLoadFile = function(filePath,fileName){
        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
    }
    // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRiskGuidelinesList);
    
    $scope.initData();
}]);
