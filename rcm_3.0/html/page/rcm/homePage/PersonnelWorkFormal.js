ctmApp.register.controller('PersonnelWorkFormal', ['$http','$scope','$location','$routeParams','$filter', function ($http,$scope,$location,$routeParams,$filter) {
	//保存传递的参数
	$scope.userId = $routeParams.userId;
	$scope.tabIndex = $routeParams.tabIndex;
	
	$scope.paginationConf2 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
        }
    };	
	$scope.initData = function(){
		$scope.getProjectReport();
		$scope.getProjectReport2();
	};
	//正式评审跟进中
	$scope.getProjectReport = function(){
		var queryInfo = {currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'projectName':$scope.projectName,'userId':$scope.userId,'type':'formalAssessment','isDealed':'0'}};
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/getProjectReportDetails0710.do",
		    data: $.param({"page":JSON.stringify(queryInfo)})
		}).success(function(result){
			if(result.success){
					$scope.projectReport = result.result_data.list;
		          $scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//正式评审已上会
	$scope.getProjectReport2 = function(){
		var queryInfo = {currentPage:$scope.paginationConf2.currentPage,pageSize:$scope.paginationConf2.itemsPerPage,queryObj:{'projectName':$scope.projectName,'userId':$scope.userId,'type':'formalAssessment','isDealed':'1'}};
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/getProjectReportDetails0710.do",
		    data: $.param({"page":JSON.stringify(queryInfo)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport2 = result.result_data.list;
		          $scope.paginationConf2.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	
	$scope.selectByCondition = function(){
		var activeLi = $(".bs-tabdrop-example li[class=active]");
		var index = activeLi.index();
		if(0==index){
			$scope.getProjectReport();
		}else{
			$scope.getProjectReport2();
		}
	}
	
	$scope.initData();
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getProjectReport);
    $scope.$watch('paginationConf2.currentPage + paginationConf2.itemsPerPage', $scope.getProjectReport2);
}]);
