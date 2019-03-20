ctmApp.register.controller('ProjectPreInfoAllBoardView',
		['$http','$scope','$location','$routeParams','Upload','$timeout', '$filter',
        function ($http,$scope,$location,$routeParams,Upload,$timeout,$filter) {
	//初始化
	var objId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	
	//判断新旧流程
	if(objId.indexOf("@")>0){
		var params = $routeParams.id.split("@");
		objId = params[0];
		$scope.isOldFlow = true;//新旧流程标识
	}
	
	//流程相关
	$scope.initPage = function(){
		if($scope.isOldFlow ){
			//旧流程
			if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
				$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2],processKey:'preAssessment',businessId:objId};
			}else{//未启动流程
				$scope.wfInfo = {processKey:'preAssessment',businessId:objId};
			}
		}else{
			//新流程
			$scope.wfInfo = {processKey:'preReview'};
			$scope.wfInfo.businessId = objId;
			$scope.refreshImg = Math.random()+1;
		}
	}
	
	//获取审核日志
	$scope.queryAuditLogsByBusinessId = function (businessId){
    	var  url = 'preAudit/queryAuditedLogsById.do';
        $http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"businessId":businessId})
		}).success(function(result){
			$scope.auditLogs = result.result_data;
		});
	}
	
	$scope.initData = function(){
		$scope.queryAuditLogsByBusinessId(objId)
		$scope.initPage(); 
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryPreAllViewById.do", 
		    data: $.param({"businessId": objId})
		}).success(function(data){
			debugger
			if(data.success){
				//1、查正式评审基本信息
				$scope.pre = data.result_data.projectInfo;
				$scope.pre.apply.serviceType = $filter("keyValueNames")($scope.pre.apply.serviceType, "VALUE");
				$scope.pre.apply.projectType = $filter("keyValueNames")($scope.pre.apply.projectType, "VALUE"); 
				$scope.pre.apply.projectModel = $filter("keyValueNames")($scope.pre.apply.projectModel, "VALUE");
				//2、一级业务单位意见
				$scope.firstLevelOpinion = data.result_data.firstLevelOpinion;
				//3、风控意见
				$scope.fengkongOpinion = data.result_data.fengkongOpinion;
				$scope.fileName=[];
			    var filenames=$scope.pre.attachment;
			    for(var i=0;i<filenames.length;i++){
			        var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
			        $scope.fileName.push(arr);
			    }
				//4、投资评审报告
				$scope.report = data.result_data.report;
				//5、投资决策通知书
				$scope.nod = data.result_data.noticeOfDecisionInfo;
				//7、项目经验总结
				$scope.arf = data.result_data.projectExperience;
				//处理附件
	            $scope.reduceAttachment(data.result_data.projectInfo.attachment);
	            
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
	//处理附件列表
    $scope.reduceAttachment = function(attachment){
    	$scope.newAttachment = [];
    	for(var i in attachment){
    		var files = attachment[i].files;
    		if(files!=null && undefined!=files){
    			var item_name = attachment[i].ITEM_NAME;
    			var uuid = attachment[i].UUID;
    			for(var j in files){
    				files[j].ITEM_NAME=item_name;
    				files[j].UUID=uuid;
    				$scope.newAttachment.push(files[j]);
    			}
    		}
    		
    	}
    }
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