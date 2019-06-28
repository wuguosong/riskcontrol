ctmApp.register.controller('exportProjetInfo', ['$routeParams','$http','$scope','$location',
function ($routeParams,$http,$scope,$location) {
	$scope.tabIndex = $routeParams.tabIndex;
	//查询正式评审基本信息列表--起草中
	//获取当前年数组
	$scope.yearArrFunc = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/getYearArr.do"
		}).success(function(result){
			if(result.success){
				$scope.yearArr = result.result_data.yearArr;
				$scope.year = result.result_data.currYear+"";
				
			}
		});
	}
	
	$scope.finishRepeat = function(){
		$scope.paginationConf.queryObj.year = $scope.year;
	};
	$scope.finishPreRepeat = function(){
		$scope.paginationConfes.queryObj.year = $scope.year;
	};
	
	if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};
	}
	if($scope.paginationConfes.queryObj == null || $scope.paginationConfes.queryObj == ''){
		$scope.paginationConfes.queryObj = {};
	}
	
//	$scope.paginationConf.queryObj = {};
//	$scope.paginationConfes.queryObj = {};
	$scope.yearArrFunc();
	$scope.getForamlList = function(){
		var stage = $("#stage").val();
    	var wf_state = $("#stage option:selected").attr("state");
    	var auditResult = $("#stage option:selected").attr("resultData");
    	$scope.paginationConf.queryObj.auditResult = "";
    	if(stage){
    		$scope.paginationConf.queryObj.stages = stage;
    	}
    	if(wf_state){
    		$scope.paginationConf.queryObj.wf_state = wf_state;
    	}
    	if(auditResult){
    		$scope.paginationConf.queryObj.auditResult = auditResult;
    	}
		$http({
			method:'post',  
		    url: srvUrl + "formalAssessmentInfo/queryPageForExport.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.formalAssessmentList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	//查询投标评审台账
	$scope.getPreList = function(){
		var stage = $("#pstage").val();
    	var wf_state = $("#pstage option:selected").attr("state");
    	var auditResult = $("#pstage option:selected").attr("resultData");
    	if(stage){
    		$scope.paginationConfes.queryObj.stage = stage;
    	}
    	if(wf_state){
    		$scope.paginationConfes.queryObj.wf_state = wf_state;
    	}
    	if(auditResult){
    		$scope.paginationConfes.queryObj.auditResult = auditResult;
    	}
		$http({
			method:'post',  
			url: srvUrl + "preInfo/queryPageForExport.do",
			data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.preList = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
		});
	}
	//查询大区
	$scope.getPertainArea = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryFormalPertainArea.do"
		}).success(function(result){
			$scope.pertainAreaList = result.result_data;
		});
	}
	$scope.getPrePertainArea = function(){
		$http({
			method:'post',  
			url: srvUrl + "deptwork/queryPrePertainArea.do"
		}).success(function(result){
			$scope.ppertainAreaList = result.result_data;
		});
	}
	$scope.getPrePertainArea();
	$scope.getPertainArea();
	//导出正式评审项目台账
    $scope.exportFormalReportInfo = function(){
    	var stage = $("#stage").val();
    	var wf_state = $("#stage option:selected").attr("state");
    	var auditResult = $("#stage option:selected").attr("resultData");
    	if(stage){
    		$scope.paginationConf.queryObj.stage = stage;
    	}
    	if(wf_state){
    		$scope.paginationConf.queryObj.wf_state = wf_state;
    	}
    	if(auditResult){
    		$scope.paginationConf.queryObj.auditResult = auditResult;
    	}
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportFormalReportInfo.do',
    		data: $.param({"page":JSON.stringify($scope.paginationConf)})
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("正式评审-"+""+"台账."))+str;
    		}else{
    			$.alert(data.result_name);
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    //导出投标评审项目列表
    $scope.exportPreReportInfo = function(){
    	var stage = $("#pstage").val();
    	var wf_state = $("#pstage option:selected").attr("state");
    	var auditResult = $("#pstage option:selected").attr("resultData");
    	if(stage){
    		$scope.paginationConfes.queryObj.stage = stage;
    	}
    	if(wf_state){
    		$scope.paginationConfes.queryObj.wf_state = wf_state;
    	}
    	if(auditResult){
    		$scope.paginationConfes.queryObj.auditResult = auditResult;
    	}
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportPreReportInfo.do',
    		data: $.param({"page":JSON.stringify($scope.paginationConfes)})
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("投标评审-"+""+"台账."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
 // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',$scope.getForamlList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getPreList);
}]);
