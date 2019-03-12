ctmApp.register.controller('GrouUserRepoUnit', ['$http','$routeParams','$scope','$location','$routeParams', '$filter',function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
	
	$scope.id =  $routeParams.id;
	$scope.action =  $routeParams.action;
	$scope.returnUrl =  $routeParams.url;
	$scope.title = $scope.action == "Create" ? "新增" : "修改";
	$scope.title = $scope.action == "View" ? "浏览" : $scope.title;
	$scope.isEditable = $scope.action == "View" ? 'false' : 'true';
	
    $scope.initData = function(){
    	//申报单位
        $scope.zrdwMappedKeyValue={"nameField":"NAME","valueField":"ORGPKVALUE"};
        //单位负责人
        $scope.compHeadMapped={"nameField":"NAME","valueField":"VALUE"};
        //基层负责人
        $scope.grasLegaStafMapped={"nameField":"NAME","valueField":"VALUE"};
    	$scope.columns = [{
    		"fieldName" : "单位名称" ,
    		"fieldValue" : "NAME"
    	},{
    		"fieldName" : "上级单位名称" ,
    		"fieldValue" : "PNAME"
    	}];
    	if($scope.action == "Create"){
    		$scope.grouUserRepoUnitInfo = {};
    		$scope.grouUserRepoUnitInfo.reportingunit  = {};
    		$scope.grouUserRepoUnitInfo.compHead  = {};
    		$scope.grouUserRepoUnitInfo.grasLegaStaf  = {};
    	}else{
    		$scope.queryInfo();
    	}
    }
    
 // 查询会议通知信息详情
	$scope.save = function(){
		if($scope.grouUserRepoUnitInfo.reportingunit.ORGPKVALUE == null || $scope.grouUserRepoUnitInfo.reportingunit.ORGPKVALUE == ""){
			$.alert("单位名称不能为空!");return false;
		}
		if($scope.grouUserRepoUnitInfo.compHead.VALUE == null || $scope.grouUserRepoUnitInfo.compHead.VALUE == ""){
			$.alert("单位负责人不能为空!");return false;
		}
//		if($scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_ID == null || $scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_ID == ""){
//			$.alert("基层负责人不能为空!");return false;
//		}
		$scope.grouUserRepoUnitInfo.REPORTINGUNIT_ID = $scope.grouUserRepoUnitInfo.reportingunit.ORGPKVALUE;
		$scope.grouUserRepoUnitInfo.REPORTINGUNIT_NAME = $scope.grouUserRepoUnitInfo.reportingunit.NAME;
		
		$scope.grouUserRepoUnitInfo.COMPANYHEADER_ID = $scope.grouUserRepoUnitInfo.compHead.VALUE;
		$scope.grouUserRepoUnitInfo.COMPANYHEADER_NAME = $scope.grouUserRepoUnitInfo.compHead.NAME;
		
		if(null != $scope.grouUserRepoUnitInfo.grasLegaStaf.VALUE){
			$scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_ID = $scope.grouUserRepoUnitInfo.grasLegaStaf.VALUE;
			$scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_NAME = $scope.grouUserRepoUnitInfo.grasLegaStaf.NAME;
		}
		show_Mask();
		var url = "";
		if($scope.grouUserRepoUnitInfo.ID == null || '' == $scope.grouUserRepoUnitInfo.ID){
			url = "repoUnitUser/create.do";
		}else{
			url = "repoUnitUser/update.do";
		}
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"json":JSON.stringify($scope.removeHashKey($scope.grouUserRepoUnitInfo))})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$.alert("保存成功!");
				$scope.grouUserRepoUnitInfo.ID = result.result_data;
			}else{
				$.alert(result.result_name);
			}
        });
	}
	
    $scope.removeHashKey = function(obj) {
    	return angular.copy(obj)
    }
    
 // 查询详情
	$scope.queryInfo = function(){
		$http({
			method:'post',  
		    url: srvUrl + "repoUnitUser/queryById.do",
		    data: $.param({"id":$scope.id})
		}).success(function(result){
			$scope.grouUserRepoUnitInfo = result.result_data;
    		
			$scope.grouUserRepoUnitInfo.reportingunit  = {};
			$scope.grouUserRepoUnitInfo.reportingunit.ORGPKVALUE = $scope.grouUserRepoUnitInfo.REPORTINGUNIT_ID;
			$scope.grouUserRepoUnitInfo.reportingunit.NAME = $scope.grouUserRepoUnitInfo.REPORTINGUNIT_NAME;

			$scope.grouUserRepoUnitInfo.compHead  = {};
    		$scope.grouUserRepoUnitInfo.grasLegaStaf  = {};
    		
			if(null != $scope.grouUserRepoUnitInfo.COMPANYHEADER_ID){
				$scope.grouUserRepoUnitInfo.compHead.VALUE = $scope.grouUserRepoUnitInfo.COMPANYHEADER_ID;
				$scope.grouUserRepoUnitInfo.compHead.NAME = $scope.grouUserRepoUnitInfo.COMPANYHEADER_NAME;
			}
			if(null != $scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_ID){
				$scope.grouUserRepoUnitInfo.grasLegaStaf.VALUE = $scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_ID;
				$scope.grouUserRepoUnitInfo.grasLegaStaf.NAME = $scope.grouUserRepoUnitInfo.GRASSROOTSLEGALSTAFF_NAME;
			}
		});
	}
    
    $scope.initData();
}]);
