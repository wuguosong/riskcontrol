ctmApp.register.controller('projectReviewList', ['$http','$routeParams','$scope','$location','$routeParams', '$filter',function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.publicProjectName= $routeParams.projectName;
 	
	if(null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
	
	var publicProjectName = $filter('decodeURI')($scope.publicProjectName,"VALUE");
	if(null == publicProjectName || "undefined" == publicProjectName){
		publicProjectName = "";
	}
	$scope.paginationConf.queryObj.publicProjectName = publicProjectName;
	 
	$scope.queryProjectReviewListByPage = function () {
		var paramProjectName = $scope.paginationConf.queryObj.publicProjectName;
		if(null == paramProjectName || paramProjectName == ""){
			paramProjectName = "undefined";
      	}
		$scope.paramProjectName = $filter('encodeURI')(paramProjectName,"VALUE");
		show_Mask();
        $http({
			method:'post',  
		    url:srvUrl+"meetingIssue/queryProjectReviewListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.projectList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		 }).error(function(data, status, headers, config){
			hide_Mask();
		 });
    };
   
    $scope.executeQueryProjectReviewListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryProjectReviewListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryProjectReviewListByPage);
}]);
