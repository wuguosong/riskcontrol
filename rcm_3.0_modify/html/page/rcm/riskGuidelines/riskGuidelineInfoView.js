ctmApp.register.controller('riskGuidelineInfoView', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.objId =  $routeParams.id;
    $scope.actionpam =$routeParams.action;
    
    $scope.rideGuidelineInfo = {};
    
    $scope.init = function(){
    	$scope.getRiskGuidelineInfo($scope.actionpam);
    }
    
    // 查询公告信息详情
	$scope.getRiskGuidelineInfo = function(id){
		$http({
			method:'post',  
		    url: srvUrl + "riskGuidelinesform/queryRideGuidelineInfoForView.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			$scope.rideGuidelineInfo = result.result_data;
			if($scope.rideGuidelineInfo.CONTENT != undefined || "" != $scope.rideGuidelineInfo.CONTENT){
				$scope.rideGuidelineInfo.CONTENT = $scope.rideGuidelineInfo.CONTENT.replace(/<\/br>/g,'\n');
			}
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
	
	    //文件下载
	    $scope.downLoadFile = function(filePath,fileName){
	        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
	    }
	    
	    $scope.init();
}]);