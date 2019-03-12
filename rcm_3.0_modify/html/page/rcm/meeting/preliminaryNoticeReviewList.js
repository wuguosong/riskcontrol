ctmApp.register.controller('PreliminaryNoticeReviewList', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	
	$scope.queryReviewByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"preliminaryNotice/queryReviewByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				var noticeList = result.result_data.list;
				for(var x = 0; x < noticeList.length; x++)
				{
					noticeList[x].ATTACHMENT_OBJECT = {"fileName":noticeList[x].ATTACHMENT_NAME,"filePath":noticeList[x].ATTACHMENT};
				}
				$scope.noticeList = noticeList;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    
    if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
    
    $scope.executeQueryReviewByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryReviewByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryReviewByPage);
}]);
