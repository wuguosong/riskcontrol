/********
 * 正式评审项目
 *********/
ctmApp.register.controller('reviewStatistics', ['$http','$scope','$location','$routeParams','$filter',
function ($http,$scope,$location,$routeParams,$filter) {
	$scope.tabIndex = '1';
	$scope.queryObject = {};
	$scope.hi_queryObject = {};
	
	//初始化页面所需数据
	$scope.initData = function(){
		//正式评审
		$scope.initCompleteFormalReport();
		//投标评审
		$scope.initTenderReport();
	}
	
	//获取正式评审已过会，无需上会的项目
	$scope.initCompleteFormalReport=function(){	
		$scope.dataJson = {
				projectName:$scope.queryObject.PROJECT_NAME,
				createBy:$scope.queryObject.CREATEBY,
				pertainareaname:$scope.queryObject.PERTAINAREANAME
		}
		$http({
			method:'post',  
		    url:srvUrl+"reviewStatisticsform/queryCompleteFormalReportByPage.do", 
		    data: $.param({
		    			"page":JSON.stringify($scope.paginationConf),
		    			"json":JSON.stringify($scope.dataJson)
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
    
    //获取投标评审已完成的项目  6 和 9
    $scope.initTenderReport = function(){
    	$scope.dataJson = {
    			projectName:$scope.hi_queryObject.HI_PROJECT_NAME,
    			createBy:$scope.hi_queryObject.HI_CREATEBY,
    			pertainareaname:$scope.hi_queryObject.HI_PERTAINAREANAME
    	}
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/queryTenderReportByPage.do',
    		 data: $.param({
	    			"page":JSON.stringify($scope.paginationConfes),
	    			"json":JSON.stringify($scope.dataJson)
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
    
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initCompleteFormalReport);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.initTenderReport);
    
    
    
    //导出正式评审项目列表
    $scope.exportFormaReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportFormaReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("正式评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    
    //导出投标评审项目列表
    $scope.exportTenderReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportTenderReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("投标评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    
	
	$scope.initData();

}]);