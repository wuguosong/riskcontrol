ctmApp.register.controller('ProjectFormalInfoAllView', 
		['$http','$scope','$location','$routeParams','Upload','$timeout', '$filter',
        function ($http,$scope,$location,$routeParams,Upload,$timeout,$filter) {
	//初始化
	var objId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.initData = function(){
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjectAllViewById.do", 
		    data: $.param({"businessId": objId})
		}).success(function(data){
			if(data.success){
				//1、查正式评审基本信息
				$scope.pre = data.result_data.projectInfo;
				$scope.pre.apply.serviceType = $filter("keyValueNames")($scope.pre.apply.serviceType, "VALUE");
				$scope.pre.apply.projectType = $filter("keyValueNames")($scope.pre.apply.projectType, "VALUE"); 
				$scope.pre.apply.projectModel = $filter("keyValueNames")($scope.pre.apply.projectModel, "VALUE");
				//2、一级业务单位意见
				$scope.firstLevelOpinion = data.result_data.firstLevelOpinion;
				//3、风控意见
				$scope.approveAttachment = data.result_data.approveAttachment;
				$scope.fileName=[];
			    var filenames=$scope.pre.attachment;
			    for(var i=0;i<filenames.length;i++){
			        var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
			        $scope.fileName.push(arr);
			    }
			    
			    $timeout(function(){
	            	angular.element(document).ready(function() {
		            	$('ul.bs-tabdrop').tabdrop();
		            });
	        	},5);
			}else{
				$.alert(data.result_name);
			}
		});
	};
	//在后台调用公共方法，把他们两个都放入一个map中  在前台取一下。
	 $scope.getSelectTypeByCode=function(typeCode){
         var  url = 'common/commonMethod/selectDataDictionByCode';
         $scope.httpData(url,typeCode).success(function(data){
             if(data.result_code === 'S'){
                 $scope.optionTypeList=data.result_data;
             }else{
                 alert(data.result_name);
             }
         });
     }
 $scope.getSelectTypeByCodeL=function(typeCode){
     var  url = 'common/commonMethod/selectDataDictionByCode';
     $scope.httpData(url,typeCode).success(function(data){
         if(data.result_code === 'S'){

             $scope.optionTypeListL=data.result_data;
         }else{
             alert(data.result_name);
         }
     });
 }
 angular.element(document).ready(function() {
     $scope.getSelectTypeByCodeL("09");
     $scope.getSelectTypeByCode("06");
 });
	$scope.initData();
}]);