ctmApp.register.controller('PreOtherReportView',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
	$scope.oldUrl = $routeParams.url;
	$scope.paramId = $routeParams.id;
	
    $scope.pre={};
    
    $scope.initData = function(){
    	$scope.getByID($scope.paramId);
    }
    
    $scope.getByID = function(id){
    	$http({
   			method:'post',  
   		    url:srvUrl+'preAuditReport/getById.do', 
   		    data: $.param({"id":id})
   		}).success(function(data){
            $scope.pre  = data.result_data;
   		}).error(function(data,status,headers,config){
   			$.alert(status);
   		});
    }

    $scope.downLoadFile = function(filePath,filename){
    	var isExists = validFileExists(filePath);
    	if(!isExists){
    		$.alert("要下载的文件已经不存在了！");
    		return false;
    	}
    	if(filename!=null && filename.length>12){
    		filename = filename.substring(0, 12)+"...";
    	}else{
    		filename = filename.substring(0,filename.lastIndexOf("."));
    	}
    	
        if(undefined!=filePath && null!=filePath){
            var index = filePath.lastIndexOf(".");
            var str = filePath.substring(index + 1, filePath.length);
            var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("投资评审-"+filename+"报告.")) + str;
            
            var a = document.createElement('a');
    	    a.id = 'tagOpenWin';
    	    a.target = '_blank';
    	    a.href = url;
    	    document.body.appendChild(a);

    	    var e = document.createEvent('MouseEvent');     
    	    e.initEvent('click', false, false);     
    	    document.getElementById("tagOpenWin").dispatchEvent(e);
    		$(a).remove();
        }else{
            $.alert("附件未找到！");
            return false;
        }
    }
    
    $scope.initData();
}]);
