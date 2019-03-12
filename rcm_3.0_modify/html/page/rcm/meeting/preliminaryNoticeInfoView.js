ctmApp.register.controller('PreliminaryNoticeInfoView', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.id =  $routeParams.id;
	
    $scope.initData = function(){
    	$scope.noticeWyUserMapped={"nameField":"NAME","valueField":"VALUE"};
    	$scope.noticeFkUserMapped={"nameField":"ITEM_NAME","valueField":"ITEM_CODE"};
    	$scope.noticeQtUserMapped={"nameField":"NAME","valueField":"VALUE"};
    	$scope.isEditable = "false";
    	$scope.queryInfo();
    }
    // 查询会议通知信息详情
	$scope.queryInfo = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preliminaryNotice/queryById.do",
		    data: $.param({"id":$scope.id})
		}).success(function(result){
			$scope.noticeInfo = result.result_data;
			$scope.noticeInfo.ATTACHMENT_OBJECT = {"fileName":$scope.noticeInfo.ATTACHMENT_NAME,"filePath":$scope.noticeInfo.ATTACHMENT}
		});
	}
	$scope.initData();
}]);