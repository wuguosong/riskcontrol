ctmApp.register.controller('RoleAndUser', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams){
	$scope.roleId =  $routeParams.roleId;
	$scope.roleCode=  $routeParams.roleCode;
	$scope.oldUrl = $routeParams.url;
	$scope.paginationConf.queryObj = {};
	$scope.paginationConf.queryObj.roleId = $scope.roleId;
	
	$scope.mappedKeyValue={"nameField":"NAME","valueField":"UUID"};
	$scope.queryParams = {"roleId": $scope.roleId};
	$scope.checkedUsers = [];
	
	$scope.addRoleUser = function () {
		$http({
			method:'post',  
		    url:srvUrl+"role/addRoleUser.do",
		    data: $.param({"roleId":$scope.roleId,"roleCode":$scope.roleCode,"json":JSON.stringify($scope.checkedUsers)})
		}).success(function(result){
			if(result.success){
				$scope.queryRoleUserListByPage();
				$scope.checkedUsers = [];
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	$scope.removeRoleUser=function(){
        var chk_list=document.getElementsByName("checkbox");
        var uid = "",num=0;
        for(var i=0;i<chk_list.length;i++)
        {
            if(chk_list[i].checked)
            {
                num++;
                uid = uid+','+chk_list[i].value;
            }
        }
        if(uid!=''){
            uid=uid.substring(1,uid.length);
        }
        if(num == 0){
        	$.alert("请选择其中一条数据进行删除！");
            return false;
        }else{
			$.confirm("确定要删除吗？", function () {
				 $http({
	     			method:'post',  
	     		    url:srvUrl+"role/deleteRoleUserById.do",
	     		    data: $.param({"id":uid})
	     		}).success(function(result){
	     			if(result.success){
	     				$scope.queryRoleUserListByPage();
	     			}else{
	     				$.alert(result.result_name);
	     			}
	     		});
			});
        }
	}
	
	$scope.queryRoleUserListByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"role/queryRoleUserListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.userList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    $scope.execRoleUserListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryRoleUserListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRoleUserListByPage);
}]);

