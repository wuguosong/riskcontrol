/********
 * Created by wufucan on 17/3/11.
 * 通报项目
 *********/
ctmApp.register.controller('MeetingSummary', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	$scope.tabIndex = $routeParams.tabIndex;
	$scope.initDefaultData = function(){
		var url = srvUrl + "bulletinInfo/queryListDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			var data = result.result_data;
			$scope.tbsxType = data.tbsxType;
		});
	};
	
	//查询未出纪要状态列表
	$scope.queryMettingSummary=function(){
		$http({
			method:'post',
			url:srvUrl+"bulletinInfo/queryMettingSummary.do",
			data:$.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.bulletins = data.result_data.list;
				$scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
	};
	//查询已出纪要状态列表
	$scope.queryMettingSummaryed=function(){
		show_Mask();
		$http({
			method:'post',
			url:srvUrl+"bulletinInfo/queryMettingSummaryed.do",
			data:$.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(data){
			if(data.success){
				$scope.applyedBulletins = data.result_data.list;
				$scope.paginationConfes.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
	};
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryMettingSummary);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryMettingSummaryed);
	//新增会议纪要(弹出框)
	$scope.showSubmitModal = function(){
		var checkLength = $(".px:checked").length;
		if(checkLength ==0){
			$.alert("请选择其中一条数据！");
	        return false;
		}else if(checkLength > 1){
			$.alert("只能选择其中一条数据！");
	        return false;
		}else{
			$scope.businessId = $(".px:checked").val();
			$('#submitModal').modal('show');
		}
	}
	
	$scope.initDefaultData();
	$scope.queryMettingSummary();
	$scope.queryMettingSummaryed();
}]);
