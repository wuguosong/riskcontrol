
ctmApp.register.controller('projectBoardList', ['$routeParams','$http','$scope','$location', '$filter', function ($routeParams,$http,$scope,$location,$filter) {

    $scope.projectName = $routeParams.projectName;
    $scope.oldUrl = $routeParams.url;

    $scope.initData = function(){
        $scope.getProjectList();
    };
    //查询正式评审基本信息列表--起草中
    $scope.getProjectList = function(){
        if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
            $scope.paginationConf.queryObj = {};
        }
        var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
        if(projectName != null && projectName != ''){
            $scope.paginationConf.queryObj.projectName = projectName;
        }
        $scope.paginationConf.queryObj.userId = $scope.credentials.UUID;
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf),
                "json":JSON.stringify($scope.paginationConf.queryObj)
            })
        }).success(function(result){
            $scope.projectList = result.result_data.list;
            $scope.paginationConf.totalItems = result.result_data.totalItems;
        });
    };

    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getProjectList);
}]);
