ctmApp.register.controller('projectBulletinInfoAllView', ['$http','$scope','$location', '$routeParams', '$filter',
 function ($http,$scope,$location, $routeParams, $filter) {
	var queryParamId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.initDefaultData = function(){
		$scope.wfInfo = {processKey:'bulletin', "businessId":queryParamId};
		$scope.initData();
	};
	$scope.initData = function(){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": queryParamId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
			$scope.auditLogs = data.logs;
			$scope.initPage();
		});
	};
	$scope.initDefaultData();
	$scope.initPage = function(){
		if($scope.bulletinOracle.AUDITSTATUS=="1" || $scope.bulletinOracle.AUDITSTATUS=="2"){
			//流程已启动
			$("#submitBtn").hide();
			$scope.wfInfo.businessId = queryParamId;
			$scope.refreshImg = Math.random()+1;
		}else{
			//未启动流程
			$("#submitBtn").show();
		}
	}
	$scope.selectAll = function(){
		if($("#all").attr("checked")){
			$(":checkbox[name='choose']").attr("checked",1);
		}else{
			$(":checkbox[name='choose']").attr("checked",false);
		}
	}
}]);