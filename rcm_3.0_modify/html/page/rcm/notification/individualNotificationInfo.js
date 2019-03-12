ctmApp.register.controller('individualNotificationInfo', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.objId =  $routeParams.id;
    $scope.actionpam =$routeParams.action;
    
    $scope.notificationInfo = {};
    
    $scope.formatDate = function() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+date.getDate();
    }
    
    $scope.init = function(){
            $scope.getNotificationInfo($scope.actionpam);
    }
    
    // 查询公告信息详情
	$scope.getNotificationInfo = function(id){
		
		$http({
			method:'post',  
		    url: srvUrl + "notificationFlatform/queryNotificationInfo.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			$scope.notificationInfo = result.result_data;
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