ctmApp.register.controller('notificationInfoView', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.objId =  $routeParams.id;
    $scope.actionpam =$routeParams.action;
    
    $scope.notificationInfo = {};
	$scope.isNotExpire = true;
    
    $scope.init = function(){
    	$scope.getNotificationInfo($scope.actionpam);
    }
    
  //提交公告信息
	$scope.submitNotification = function(){
		$http({
			method:'post',  
		    url: srvUrl + "notificationFlatform/submitNotification.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			if(result.success){
				$scope.getNotificationInfo();
			}else{
				$.alert(result.result_name);
			}
        });
	}
	
	
    // 查询公告信息详情
	$scope.getNotificationInfo = function(id){
		$http({
			method:'post',  
		    url: srvUrl + "notificationFlatform/queryNotificationInfoForView.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			var notiInfo = result.result_data;
			if(notiInfo.CONTENT != null && "" != notiInfo.CONTENT){
				notiInfo.CONTENT = notiInfo.CONTENT.replace(/<\/br>/g,'\n');
			}
			$scope.isNotExpire = notiInfo.EXPIRE_DATE == null || notiInfo.EXPIRE_DATE == '';
			
			$scope.notificationInfo = notiInfo;
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