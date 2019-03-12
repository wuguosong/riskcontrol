ctmApp.register.controller('regulationsList', ['$routeParams','$http','$scope','$location','Upload','$filter', 
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	
	if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	$scope.paginationConf.queryObj.type = "";
	
	$scope.initData = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.getRegulationsList();
	}
	//查询规章制度列表
	$scope.getRegulationsList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "regulationsFrom/queryRegulationsList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.notifications = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
			
			//初始化提示信息框
			angular.element(document).ready(function() {
				$("[data-toggle='tooltip']").tooltip();
			});
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
	
	//新增规章制度
	$scope.addRegulations = function(){
		$location.path("/regulationsInfo/Create/0/"+$filter('encodeURI')('#/regulationsList'));
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
	
	//删除规章制度
	$scope.deleteRegulations = function(){
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
    		    url:srvUrl+"regulationsFrom/deleteRegulations.do", 
    		    data: $.param({"ids": idsStr})
    		}).success(function(data){
    			if(data.success){
    				$scope.getRegulationsList();
    			}else{
    				$.alert(data.result_name);
    			}
    		}).error(function(data,status,headers, config){
        		$.alert(status);
        	});
    	});
	}
	
	
    // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRegulationsList);
    
    $scope.initData();
}]);
