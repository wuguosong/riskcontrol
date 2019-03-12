ctmApp.register.controller('MeetingNoticeList', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	$scope.queryMeetingNoticeListByPage = function () {
		$scope.paginationConf.queryObj.type = "MEETING_NOTICE";
        $http({
			method:'post',  
		    url:srvUrl+"notice/queryListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.meetingNoticeList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
    $scope.executeQueryMeetingNoticeListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryMeetingNoticeListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryMeetingNoticeListByPage);
}]);
