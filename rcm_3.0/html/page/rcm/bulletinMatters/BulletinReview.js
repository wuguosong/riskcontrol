/********
 * Created by wufucan on 17/3/19.
 * 通报项目审阅
 *********/
ctmApp.register.controller('BulletinReview', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	if($routeParams.tabIndex){
		$scope.tabIndex = $routeParams.tabIndex;
	}else{
		$scope.tabIndex = 0;
	}
	$scope.initDefaultData = function(){
		var url = srvUrl + "bulletinReview/queryListDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			var data = result.result_data;
			$scope.tbsxType = data.tbsxType;
		});
	};
	$scope.initDefaultData();
	//查询待审阅列表
	$scope.queryApplyList=function(){
		$http({
			method:'post',  
		    url:srvUrl+"bulletinReview/queryWaitList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.bulletins = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    //查询已提交列表
    $scope.queryApplyedList=function(){
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinReview/queryAuditedList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(data){
			if(data.success){
				$scope.applyedBulletins = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
}]);
ctmApp.register.controller('BulletinReviewDetail', ['$http','$scope','$location', '$routeParams', '$filter', '$routeParams', 
function ($http,$scope,$location, $routeParams, $filter,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	var routeParams = $routeParams.id.split("_");
	var queryParamId = routeParams[0];
	$scope.tabIndex = routeParams[1];
	$scope.initDefaultData = function(){
		$scope.initData();
	};
	$scope.initData = function(){
		var url = srvUrl + "bulletinReview/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": queryParamId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
		});
	};
	$scope.save = function(){
		var params = {
			businessId: queryParamId,
			decisionOpinionList: $scope.selfOpinion
		};
		$http({
			method:'post',  
		    url:srvUrl+"meeting/saveReviewInfo.do",
		    data: $.param({"data":JSON.stringify(params)})
		}).success(function(result){
			$.alert(result.result_name);
			$scope.initDefaultData();
		});
	};
	$scope.initDefaultData();
}]);
