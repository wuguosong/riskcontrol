ctmApp.register.controller('changeBpmnUser', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
	
	$scope.mappedKeyValue={'nameField':'PROJECT_NAME','valueField':'BUSINESS_ID'};
	$scope.columns = [{
		"fieldName" : "项目名称" ,
		"fieldValue" : "PROJECT_NAME"
	},{
		"fieldName" : "项目类型" ,
		"fieldValue" : "TYPENAME"
	}];
	$scope.formalTaskNodeList = [{'name':'投资经理','value':'createBy'},
	                       {'name':'大区负责人','value':'largeAreaLeader'},
	                       {'name':'基层法务人员','value':'grassLegal'},
	                       {'name':'任务分配人','value':'task'},
	                       {'name':'双投中心负责人','value':'serviceTypePerson'},
	                       {'name':'评审负责人','value':'reviewLeader'},
	                       {'name':'法律负责人','value':'legalLeader'},
	                       {'name':'法律任务分配人','value':'legalTask'},
	                       {'name':'业务区负责人','value':'businessArea'}];
	$scope.preTaskNodeList = [{'name':'投资经理','value':'createBy'},
	                             {'name':'大区负责人','value':'largeAreaLeader'},
	                             {'name':'任务分配人','value':'task'},
	                             {'name':'双投中心负责人','value':'serviceTypePerson'},
	                             {'name':'评审负责人','value':'reviewLeader'},
	                             {'name':'业务区负责人','value':'businessArea'}];
	$scope.bulletinTaskNodeList = [{'name':'任务分配人','value':'task'},
	                             {'name':'评审负责人','value':'reviewLeader'},
	                             {'name':'法律负责人','value':'legalLeader'},
	                             {'name':'业务负责人','value':'businessPerson'}];
	
	$scope.getTaskPerson = function(){
		if( $scope.flow != null && $scope.flow.task != null && $scope.flow.task != ''){
			$http({
				method:'post',  
				url:srvUrl + "bpmn/getTaskPerson.do", 
				data: $.param({"flow":JSON.stringify($scope.flow)})
			}).success(function(data){
				if(data.success){
					$scope.resultInfo = data.result_data.NAME;
				}else{
					$scope.resultInfo = data.result_name;
				}
			});
		}
	}
	$scope.$watch('flow.task', $scope.getTaskPerson);
	//选择项目确认回调
	$scope.confirmCallBack = function(){
		$scope.flow.task = '';
		$scope.resultInfo = null;
		var businessId = $scope.flow.project.BUSINESS_ID;
		if($scope.flow.project.PROJECT_TYPE == "bulletin"){
			$scope.wfInfo = {processKey:'bulletin'};
			$scope.wfInfo.businessId = businessId;
			$scope.refreshImg = Math.random()+1;
			$scope.bulletinAuditLogs(businessId);
		}else if($scope.flow.project.PROJECT_TYPE == "pre"){
			$scope.wfInfo = {processKey:'preReview'};
			$scope.wfInfo.businessId = businessId;
			$scope.refreshImg = Math.random()+1;
			$scope.preAuditLogs(businessId);
			$scope.getPreById(businessId);
		}else if($scope.flow.project.PROJECT_TYPE == "pfr"){
			$scope.wfInfo = {processKey:'formalReview'};
			$scope.wfInfo.businessId = businessId;
			$scope.refreshImg = Math.random()+1;
			$scope.pfrAuditLogs(businessId);
			$scope.getFormalAssessmentByID(businessId);
		}
	}
	//正式评审基本信息
	$scope.getFormalAssessmentByID=function(id){
		
		var  url = 'formalAssessmentInfo/getFormalAssessmentByID.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"id":id})
		}).success(function(data){
			$scope.pfr  = data.result_data.formalAssessmentMongo;
			$scope.pfrOracle  = data.result_data.formalAssessmentOracle;
			$scope.pfr.oracle  = data.result_data.formalAssessmentOracle;
			//处理任务人
			var ptNameArr=[],pmNameArr=[],pthNameArr=[],fgNameArr=[];
			var pt1NameArr=[];
			var pt1=$scope.pfr.apply.serviceType;
			$scope.serviceTypes = $scope.pfr.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pfr.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pfr.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pfr.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pfr.apply.projectModel=pmNameArr.join(",");
			}
			if (null != $scope.pfr.apply.expectedContractDate) {
				$scope.changDate($scope.pfr.apply.expectedContractDate);
			}
		});
	}
	//投标评审基本信息
	$scope.getPreById = function(businessId){
		$http({
			method:'post',  
		    url:srvUrl+'preInfo/getPreByID.do', 
		    data: $.param({"businessId":businessId})
		}).success(function(data){
			$scope.pre  = data.result_data.mongo;
			$scope.pre.oracle  = data.result_data.oracle;
			$scope.pre.apply.projectModel;
			
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
	//其他评审基本信息
	
	//获取审核日志
	$scope.preAuditLogs = function (businessId){
    	var  url = 'preAudit/queryAuditedLogsById.do';
        $http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"businessId":businessId})
		}).success(function(result){
			$scope.auditLogs = result.result_data;
		});
	}
	//获取审核日志
	$scope.pfrAuditLogs = function (businessId){
    	var  url = 'formalAssessmentAudit/queryAuditedLogsById.do';
        $http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"businessId":businessId})
		}).success(function(result){
			$scope.auditLogs = result.result_data;
		});
	}
	
	$scope.bulletinAuditLogs = function(businessId){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": businessId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
			$scope.auditLogs = data.logs;
		});
	};
	$scope.otherFields = ["PROJECT_TYPE"];
	$scope.changeAuditUser = function(){
		//验证必填项
		if($scope.flow.project.BUSINESS_ID == null || $scope.flow.project.BUSINESS_ID == ''){
			$.alert("请选择项目！");
			return;
		}
		if($scope.flow.task == null || $scope.flow.task == '' || $scope.flow.user == ''){
			$.alert("请选择需要替换的节点！");
			return;
		}
		if($scope.flow.user.VALUE == null || $scope.flow.user.VALUE == ''){
			$.alert("请选择替换人员！");
			return;
		}
		
		$http({
			method:'post',  
			url:srvUrl + "bpmn/changeAuditUser.do", 
			data: $.param({"flow":JSON.stringify($scope.flow)})
		}).success(function(data){
			if(data.success){
				$.alert(data.result_name);
				var businessId = $scope.flow.project.BUSINESS_ID;
				if($scope.flow.project.PROJECT_TYPE == "bulletin"){
					$scope.bulletinAuditLogs(businessId);
				}else if($scope.flow.project.PROJECT_TYPE == "pre"){
					$scope.preAuditLogs(businessId);
					$scope.getPreById(businessId);
				}else if($scope.flow.project.PROJECT_TYPE == "pfr"){
					$scope.pfrAuditLogs(businessId);
					$scope.getFormalAssessmentByID(businessId);
				}
			}else{
				$.alert(data.result_name);
			}
		});
	}
	
}]);