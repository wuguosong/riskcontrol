ctmApp.register.controller('HistoryDecisionReviewList', ['$http','$routeParams','$scope','$location','$routeParams', '$filter', function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.queryHistoryDecisionReviewListByPage = function () {
		show_Mask();
		$http({
			method:'post',  
		    url:srvUrl+"decisionReview/queryHistoryDecisionReviewListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.historyDecisionReviewList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
    };
    if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
	}
    $scope.executeQueryHistoryDecisionReviewListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryHistoryDecisionReviewListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryHistoryDecisionReviewListByPage);

    /**
	 * 查询项目部分信息，查看是新项目还是老项目，决定跳转路径
	 * */
    $scope.getInfo = function (id) {
        $http({
            method: 'post',
            url: srvUrl + "formalReport/findFormalAndReport.do",
            data: $.param({"projectFormalId": id})
        }).success(function (data) {
            $scope.projectSummary = data.result_data.summary;

            var path = $filter('encodeURI')('#/historyDecisionReviewList/'+$scope.oldUrl);
            if ($scope.projectSummary == null || $scope.projectSummary == undefined){
            	// $location.path("/FormalBiddingInfo_view/"+ id + "/" + path);
                $location.path("/FormalBiddingInfoReview/"+ id + "/" + path);
            } else {
                $location.path("/FormalBiddingInfoPreview/"+ id + "/" + path + "/7");
			}
        }).error(function (data, status, header, config) {
            $.alert(status);
        });
    }
}]);
