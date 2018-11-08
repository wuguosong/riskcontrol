/**
 * Created by yaphet on 2018/3/20.
 */

ctmApp.register.controller('pertainAreaDetail', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	
	$scope.initData = function(){
		if('create' == $routeParams.action){
			$scope.action = "新增";
		}else if ('update' == $routeParams.action){
			$scope.action = "修改";
			$scope.userId = $routeParams.id;
			//查询数据
			$scope.getById();
		}
		$scope.getServiceType();
	}
	$scope.getServiceType = function(){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,"14").success(function(data){
            if(data.result_code == 'S'){
                $scope.serviceTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
	}
	
	$scope.columns = [{
		"fieldName" : "单位名称" ,
		"fieldValue" : "NAME"
	},{
		"fieldName" : "上级单位名称" ,
		"fieldValue" : "PNAME"
	}];
	
	$scope.orgMappedKeyValue={"nameField":"NAME","valueField":"ORGPKVALUE"};
	$scope.directPersonMapped={"nameField":"DIRECTPERSON_NAME","valueField":"DIRECTPERSON_ID"};
    
	$scope.getById = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "pertainArea/getByUserId.do",
		    data: $.param({"userId":$scope.userId})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$scope.leader.user = {"NAME":result.result_data.LEADERNAME,"VALUE":result.result_data.LEADERID};
				$scope.leader.org = {"NAME":result.result_data.ORGNAME,"ORGPKVALUE":result.result_data.ORGPKVALUE};
				$scope.leader.TYPE = result.result_data.TYPE;
				$scope.leader.SERVICETYPE = result.result_data.SERVICETYPE;
				$scope.leader.oldUserId = result.result_data.LEADERID;
			}else{
				$.alert(result.result_name);
			}
		});
	}
	$scope.selectAll = function(){
		if($("#selectAll").attr("checked")){
			$(":checkbox[name='serviceType']").attr("checked",1);
		}else{
			$(":checkbox[name='serviceType']").attr("checked",false);
		}
	}
	$scope.save = function(){
		//数据验证
		if($scope.leader.org.ORGPKVALUE == null || $scope.leader.org.ORGPKVALUE == '' ){
			$.alert("请选择所属单位!");
			return;
		}
		if($scope.leader.user.VALUE == null || $scope.leader.user.VALUE == ''){
			$.alert("请选择负责人!");
			return;
		}
		if($scope.leader.TYPE == null || $scope.leader.TYPE == ''){
			$.alert("请选择人员职务!");
			return;
		}
		
		if($scope.leader.TYPE == '1'){
			var serviceTypeIds = "";
			$(":checkbox[name='serviceType']:checked").each(function(){
				serviceTypeIds+=","+this.value;
			});
			$scope.leader.SERVICETYPE = serviceTypeIds.substring(1);
			
			if($scope.leader.SERVICETYPE.indexOf('all')>=0)$scope.leader.SERVICETYPE = 'all';
			if($scope.leader.SERVICETYPE == null || $scope.leader.SERVICETYPE == ''){
				$.alert("请选择负责的项目类型!");
				return;
			}
		}
		show_Mask();
		var url;
		if($routeParams.action == "create"){
			url = "pertainArea/save.do";
		}else if($routeParams.action == "update"){
			url = "pertainArea/updateByUserId.do";
		}
		
		if($scope.leader.TYPE != '1'){
			$scope.leader.SERVICETYPE = null;
		}
		
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"json":JSON.stringify({
		    		"orgId":$scope.leader.org.ORGPKVALUE,
		    		"userId":$scope.leader.user.VALUE,
		    		"type":$scope.leader.TYPE,
		    		"oldUserId":$scope.leader.oldUserId,
		    		"serviceType":$scope.leader.SERVICETYPE}
		    		)})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$.alert(result.result_name);
				$location.path("/pertainAreaLeaderList");
			}else{
				$.alert(result.result_name);
			}
		});
		
	}
	
	$scope.initData();
}]);
