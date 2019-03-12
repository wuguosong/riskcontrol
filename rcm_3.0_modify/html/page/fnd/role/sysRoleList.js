ctmApp.register.controller('SysRoleList', ['$http','$routeParams','$scope','$location','$routeParams','$timeout', function ($http,$routeParams,$scope,$location,$routeParams,$timeout) {
	//分配菜单
	$scope.funcCheck=function(role_id,code){
        $location.path("/RoleAndFun/"+role_id+"/"+code);
    }
	//分配用户
	$scope.roleCheck=function(role_id,code){
        $location.path("/RoleAndUser/"+role_id+"/"+code);
    }
	//新建
    $scope.createRole=function(){
        $location.path("/SysRoleInfo/create/0");
    };
    //更新
    $scope.updateRole=function ()
    {
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
        if(num==0){
        	$.alert("未选中修改的数据！");
            return false;
        }else if(num>1){
        	$.alert("只能选择其中一条数据进行编辑！");
            return false;
        }else{
            $location.path("/SysRoleInfo/update/"+uid);
        }
    }
    $scope.deleteRole=function(){
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
        	 $http({
     			method:'post',  
     		    url:srvUrl+"role/deleteRoleVali.do",
     		    data: $.param({"id":uid})
     		}).success(function(result){
     			if(result.success){
     				$.confirm("确定要删除 吗？", function () {
     					 $http({
     		     			method:'post',  
     		     		    url:srvUrl+"role/deleteRoleById.do",
     		     		    data: $.param({"id":uid})
     		     		}).success(function(result){
     		     			if(result.success){
     		     				$scope.queryRoleListByPage();
     		     			}else{
     		     				$.alert(result.result_name);
     		     			}
     		     		});
     				});
     			}else{
     				$.alert(result.result_name);
     			}
     		});
        }
	}
    
	$scope.queryRoleListByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"role/queryRoleListByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.roleList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
				
				//初始化提示信息框
				 $timeout(function(){
					angular.element(document).ready(function() {
						var dd = $("[data-toggle='tooltip']");
						dd.tooltip();
					 });
				 },10);
			}else{
				$.alert(result.result_name);
			}
		});
    };
    $scope.execQuerRoleListByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryRoleListByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRoleListByPage);
}]);
