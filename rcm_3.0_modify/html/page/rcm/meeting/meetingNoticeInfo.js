ctmApp.register.controller('MeetingNoticeInfo', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.noticeId =  $routeParams.id;
	$scope.currUrl = "#"+$location.path();
	
    $scope.initData = function(){
    	$scope.queryMeetingNoticeInfo();
    }
    // 查询会议通知信息详情
	$scope.queryMeetingNoticeInfo = function(id){
		$http({
			method:'post',  
		    url: srvUrl + "notice/queryInfo.do",
		    data: $.param({"id":$scope.noticeId})
		}).success(function(result){
			$scope.noticeInfo = result.result_data;
			$scope.noticeInfo.ATTACHMENT = {"fileName":$scope.noticeInfo.ATTACHMENT_NAME,"filePath":$scope.noticeInfo.ATTACHMENT};
			if($scope.noticeInfo.REVIEW_STATUS == "1"){
				$scope.updateReviewStatus();
			}
			$scope.queryMeetingInfo($scope.noticeInfo.RELATION_ID);
		});
	}
	$scope.updateReviewStatus = function(){
		$http({
			method:'post',  
		    url: srvUrl + "notice/updateReviewStatus.do",
		    data: $.param({"noticeId":$scope.noticeId})
		}).success(function(result){
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
//	$scope.queryMeetingInfo = function(meetingIssueId){
//		$http({
//			method:'post',  
//		    url: srvUrl + "meeting/queryById.do",
//		    data: $.param({"meetingIssueId":meetingIssueId})
//		}).success(function(result){
//			$scope.meetingIssue = result.result_data;
//        });
//	}
	$scope.initData();
}]);