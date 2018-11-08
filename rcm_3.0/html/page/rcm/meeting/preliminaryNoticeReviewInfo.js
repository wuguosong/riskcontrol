ctmApp.register.controller('PreliminaryNoticeReviewInfo', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.noticeId =  $routeParams.id;
	
    $scope.initData = function(){
    	$scope.queryReviewById();
    }
    // 查询会议通知信息详情
	$scope.queryReviewById = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preliminaryNotice/queryReviewById.do",
		    data: $.param({"id":$scope.noticeId})
		}).success(function(result){
			$scope.noticeInfo = result.result_data;
			$scope.noticeInfo.ATTACHMENT_OBJECT = {"fileName":$scope.noticeInfo.ATTACHMENT_NAME,"filePath":$scope.noticeInfo.ATTACHMENT}
			if($scope.noticeInfo.REVIEW_STATUS != "2"){
				$scope.updateUsreReviewStatus();
			}
		});
	}
	$scope.updateUsreReviewStatus = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preliminaryNotice/updateUsreReviewStatus.do",
		    data: $.param({"noticeId":$scope.noticeId})
		}).success(function(result){
		}).error(function(data,status,headers,config){
        });
	}
	$scope.initData();
}]);