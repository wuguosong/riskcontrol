// 今日评审项目
ctmApp.register.controller('todayMeetingManageList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	$scope.oldUrl = $routeParams.url;
    $scope.getTodayProject = function () {
        $http({
            method:'post',
            url:srvUrl+"decision/queryList.do"
        }).success(function(data){
            if(data.success){
                $scope.projects = data.result_data;
               /* $scope.paginationConf.totalItems = data.result_data.totalItems;*/
            }else{
                $.alert(data.result_name);
            }
        });
    };
    $scope.getTodayProject();
}]);