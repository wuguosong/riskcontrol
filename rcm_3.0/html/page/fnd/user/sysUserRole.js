ctmApp.register.controller('sysUserRole', [ '$http', '$scope', '$location',
'$routeParams', function($http, $scope, $location, $routeParams) {
	$scope.userId = $routeParams.userId;
	$scope.oldUrl = $routeParams.url;
	$scope.initData = function() {
		$scope.getAllRole();
	}
	$scope.getAllRole = function() {
		$http({
			method : 'post',
			url : srvUrl + 'user/getAllRole.do'
		}).success(function(data) {
			if (data.success) {
				$scope.roleList = data.result_data;
				// 获取用户角色关联
				$scope.getUserRole();
			} else {
				$.alert(data.result_name);
			}
		});
	}
	$scope.getUserRole = function() {
		$http({
			method : 'post',
			url : srvUrl + 'user/getRoleByUserId.do',
			data : $.param({
				"userId" : $scope.userId
			})
		}).success(function(data) {
			if (data.success) {
				$scope.roleStr = "";
				for(var i in data.result_data){
					$scope.roleStr += ","+data.result_data[i].ROLE_ID;
				}
				$scope.roleStr = $scope.roleStr.substring(1);
				
				//初始化提示信息框
				angular.element(document).ready(function() {
					$("[data-toggle='tooltip']").tooltip();
				});
				
			} else {
				$.alert(data.result_name);
			}
		});
	}
	$scope.save = function(){
		var roleList = $(":checkbox[name='checkRole']:checked");
		var roleArr = [];
		for(var i = 0;i<roleList.length;i++){
			roleArr.push(JSON.parse(roleList[i].value));
		}
		$http({
			method : 'post',
			url : srvUrl + 'user/saveUserRole.do',
			traditional: true,
			data : $.param({
				"userId" : $scope.userId,
				"roleArr": JSON.stringify(roleArr)
			})
		}).success(function(data) {
			if (data.success) {
				$.alert(data.result_name);
				$scope.getAllRole();
//				$location.path("/SysUserList/0");
			} else {
				$.alert(data.result_name);
			}
		});
	}	
	$scope.initData();
} ]);