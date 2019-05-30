ctmApp.register.controller('UpdateProjectNameList', ['$http','$routeParams','$scope','$location','$routeParams', function ($http,$routeParams,$scope,$location,$routeParams) {

	//初始化列表数据
	var id=$routeParams.id;
	$scope.initData = function(){
		$scope.getAllProject();
	}
	//查询所有的项目
	$scope.getAllProject = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "updateProjectName/queryAllProject.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.allProject = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
	}
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getAllProject);
	//修改项目名称（弹出框）
	$scope.showSubmitModal = function(){
		var chked = $("input[type=checkbox][name=chk]:checked");
		var checkLength = chked.length;
		if(checkLength != 1){
			$.alert("请选择其中一条数据！");
	        return false;
		}
        var vals = chked.val().split("_");
        $("#newProjectName").val(vals[2]);
		$('#submitModal').modal('show');
		
	}
	$scope.submit = function(){
		var chked = $("input[type=checkbox][name=chk]:checked");
		var vals = chked.val().split("_");
		var params = {
			"projectName": $.trim($("#newProjectName").val()),
			"businessId": vals[0],
			"type": vals[1]
		};
		//限制80个字
		if($.trim($("#newProjectName").val()).length>80){
			$.alert("项目名称不能超过80个字！");
			return ;
		}
		$http({
			method:'post',  
			url: srvUrl + "updateProjectName/updateProject.do",
			data: $.param(params)
		}).success(function(result){
			 $('#submitModal').modal('hide');
			 $("#newProjectName").val("");
			 $.alert(result.result_name);
			 $scope.initData();
		});
	};
	$scope.cancel = function(){	
		$("#newProjectName").val("");
	}
	$scope.initData();
}]);
