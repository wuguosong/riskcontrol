ctmApp.register.controller('PreDetailView', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	var businessId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	
	//初始化数据
	$scope.initData = function(){
		$scope.getPreById(businessId);
		$scope.queryAuditLogsByBusinessId(businessId);
		$scope.initPage();
	}
	
	//流程图相关
	$scope.initPage = function(){
		$scope.wfInfo = {processKey:'preReview'};
		$scope.wfInfo.businessId = businessId;
		$scope.refreshImg = Math.random()+1;
	}
	
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
	
	//获取预评审信息
	$scope.getPreById = function(businessId){
		$http({
			method:'post',  
		    url:srvUrl+'preInfo/getPreByID.do', 
		    data: $.param({"businessId":businessId})
		}).success(function(data){
			$scope.pre  = data.result_data.mongo;
			$scope.pre.oracle  = data.result_data.oracle;
			$scope.pre.apply.projectModel;
			
			$scope.attach = data.result_data.attach;
			//处理附件
            $scope.reduceAttachment(data.result_data.mongo.attachment);
			
			
			var ptNameArr=[],pmNameArr=[],pthNameArr=[],fgNameArr=[],pt1NameArr=[];
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
			}
			
			var pt1=$scope.pre.apply.serviceType;
			$scope.serviceTypes = $scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
		});
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
	
	//弹出审批框新版
	//提交
	$scope.showSubmitModal = function(){
		$scope.approve = {
			operateType: "submit",
			processKey: "formalAssessment",
			businessId: businessId,
			callbackSuccess: function(result){
				$.alert(result.result_name);
				$('#submitModal').modal('hide');
				$("#submibtnn").hide();
				$scope.initData();
			},
			callbackFail: function(result){
				$.alert(result.result_name);
			}
		};
		$('#submitModal').modal('show');
	}
	$scope.initData();
}]);
