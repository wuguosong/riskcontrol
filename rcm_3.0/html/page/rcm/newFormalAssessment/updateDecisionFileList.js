/********
 * Created by dsl on 17/7/12.
 * 正式评审项目
 *********/
ctmApp.register.controller('updateDecisionFileList', ['$http','$scope','$location','$routeParams','$filter',
function ($http,$scope,$location,$routeParams,$filter) {
	
	//初始化页面所需数据
	$scope.initData = function(){
		$scope.getPfrNoticeFileList();
	}
	
	$scope.getPfrNoticeFileList = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "formalReport/queryPfrNoticeFileList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.pfrList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
	}
	
	
	$scope.initData();
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getPfrNoticeFileList);
}]);