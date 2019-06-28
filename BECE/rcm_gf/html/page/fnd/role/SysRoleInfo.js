ctmApp.register.controller('SysRoleInfo', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
    $scope.role = {};
	$scope.roleId =  $routeParams.roleId;
	$scope.action =  $routeParams.action;
	$scope.title = $scope.action == "create" ? "新增" : "修改";
	
    $scope.saveRole = function () {
    	if($scope.role.ROLE_NAME == null || $scope.role.ROLE_NAME == ""){
			$.alert("角色名称必填!");return false;
		}
    	if($scope.role.CODE == null || $scope.role.CODE == ""){
			$.alert("编码必填!");return false;
		}
    	var isShowPublicSearch = $("input[name='isShowPublicSearch']:checked").val();
    	$scope.role.LAST_UPDATE_BY = isShowPublicSearch+"";
    	
		show_Mask();
		var url = "";
		if($scope.role.ROLE_ID == null || '' == $scope.role.ROLE_ID){
			url = "role/createRole.do";
		}else{
			url = "role/updateRole.do";
		}
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"json":JSON.stringify(angular.copy($scope.role))})
		}).success(function(result){
			hide_Mask();
			if (result.success) {
				$scope.role.ID = result.result_data;
				$.alert("保存成功!");
			}
			else{
				$.alert(result.result_name);
			}
		}).error(function(data,status,headers,config){
			hide_Mask();
        	$.alert(status);
        });
    };

    // 查询会议通知信息详情
	$scope.queryRoleById = function(){
		$http({
			method:'post',  
		    url: srvUrl + "role/queryById.do",
		    data: $.param({"id":$scope.roleId})
		}).success(function(result){
			$scope.role = result.result_data;
			
	    	$("input[name='isShowPublicSearch'][value='"+$scope.role.LAST_UPDATE_BY+"']").attr("checked","checked");
		});
	}
	
    $scope.cancel = function () {
        $location.path("/SysRoleList");
    };
    
    $scope.initData = function(){
    	if($scope.action == "create"){
            $scope.role.NAME = '';
            $scope.role.CODE = '';
            $scope.role.EXPLAIN = '';
            $scope.role.STATE = '';
    	}else{
    		$scope.queryRoleById();
    	}
    }
    $scope.initData();
}]);