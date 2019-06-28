ctmApp.register.controller('RoleAndProject', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams){
	$scope.roleId =  $routeParams.roleId;
	$scope.roleCode=  $routeParams.roleCode;
	$scope.oldUrl = $routeParams.url;
	$scope.paginationConf.queryObj = {};
	$scope.paginationConf.queryObj.roleId = $scope.roleId;
	
	$scope.mappedKeyValue={"nameField":"NAME","valueField":"BUSINESSID", "typeField":"PROJECT_TYPE"};
	$scope.queryParams = {"roleId": $scope.roleId};
	$scope.checkedProjects = [];

    // 获取系统当前时间
    $scope.getDate = function () {
        var myDate = new Date();
        //获取当前年
        var year = myDate.getFullYear();
        //获取当前月
        var month = myDate.getMonth() + 1;
        //获取当前日
        var date = myDate.getDate();
        var h = myDate.getHours(); //获取当前小时数(0-23)
        var m = myDate.getMinutes(); //获取当前分钟数(0-59)
        var s = myDate.getSeconds();
        var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
        return now;
    }
	
	$scope.addRoleProject = function () {
		$http({
			method:'post',  
		    url:srvUrl+"role/addRoleProject.do",
		    data: $.param({"roleId":$scope.roleId,"create_date":$scope.getDate(),"json":JSON.stringify($scope.checkedProjects)})
		}).success(function(result){
			if(result.success){
				$scope.queryRoleProjectListByPage();
				$scope.checkedProjects = [];
			}else{
				$.alert(result.result_name);
			}
		});
	}
	
	$scope.removeRoleProject=function(){
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
	     		    url:srvUrl+"role/deleteRoleProjectById.do",
	     		    data: $.param({"id":uid})
	     		}).success(function(result){
	     			if(result.success){
	     				$scope.queryRoleProjectListByPage();
	     			}else{
	     				$.alert(result.result_name);
	     			}
	     		});
			});
        }
	}
	
	$scope.queryRoleProjectListByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"role/queryRoleProjectListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.projectList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    $scope.execRoleProjectListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryRoleProjectListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRoleProjectListByPage);
}]);

