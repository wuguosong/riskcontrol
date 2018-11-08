ctmApp.register.controller('templateFlieInfoView', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.objId =  $routeParams.id;
    $scope.actionpam =$routeParams.action;
    
    $scope.templateFlieInfo = {};
    
    $scope.init = function(){
    	$scope.getTemplateFlieInfo($scope.actionpam);
    }
    
    // 查询公告信息详情
	$scope.getTemplateFlieInfo = function(id){
		$http({
			method:'post',  
		    url: srvUrl + "templateFileFrom/queryTemalateFileInfo.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			$scope.templateFlieInfo = result.result_data;
			if($scope.templateFlieInfo.CONTENT != undefined || "" != $scope.templateFlieInfo.CONTENT){
				$scope.templateFlieInfo.CONTENT = $scope.templateFlieInfo.CONTENT.replace(/<\/br>/g,'\n');
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