ctmApp.register.controller('regulationsInfoView', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.objId =  $routeParams.id;
    $scope.actionpam =$routeParams.action;
    
    $scope.regulationsInfo = {};
    
    $scope.init = function(){
    	$scope.getRegulationsInfo($scope.actionpam);
    }
    
    // 查询规章制度详情
	$scope.getRegulationsInfo = function(id){
		$http({
			method:'post',  
		    url: srvUrl + "regulationsFrom/queryRegulationsInfo.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			$scope.regulationsInfo = result.result_data;
			if($scope.regulationsInfo.CONTENT != undefined || "" != $scope.regulationsInfo.CONTENT){
				$scope.regulationsInfo.CONTENT = $scope.regulationsInfo.CONTENT.replace(/<\/br>/g,'\n');
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