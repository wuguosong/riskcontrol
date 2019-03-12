ctmApp.register.controller('PreNormalReportView',['$http','$scope','$location','$routeParams','$timeout',function ($http,$scope,$location,$routeParams,$timeout) {
	
	$scope.oldUrl = $routeParams.url;
    $scope.paramId = $routeParams.id;
    
    $scope.pre={};
    
    $scope.initData = function(){
    	$scope.getPreProjectByID($scope.paramId);
    }
    
  //查义所有的操作
    $scope.getPreProjectByID = function(id){
        $http({
    		method:'post',
    		url:srvUrl+'preAuditReport/getPreProjectFormalReviewByID.do',
    		data: $.param({"id":id})
	    	}).success(function(data){
	   		 if(data.success){
	            $scope.pre = data.result_data;
	            $("#content-wrapper input").attr("disabled","disabled");
	            $("#wordbtn").attr("disabled",false);
			 }else{
				 $.alert(data.result_name);
			 }
			}).error(function(data,status,headers, config){
				$.alert(status);
			});
    };
    
    $scope.createWord = function(id){
    	show_Mask();
    	$http({
   			method:'post',  
   		    url:srvUrl+'preAuditReport/getPreWordReport.do', 
   		    data: $.param({"id":id})
   		}).success(function(data){
   			hide_Mask();
   			if(data.success){
   				var filePath=data.result_data.filePath;
                var fileName=data.result_data.fileName;
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("投资评审-"+fileName+"报告.docx"));
   			}else{
   				$.alert("提交系统报表生成失败，请检查资料完整性；如果资料完整请联系管理员！");
   			}
   		}).error(function(data,status,headers,config){
   			hide_Mask();
   			$.alert(status);
   		});
    }
    
    $scope.initData();
}]);
