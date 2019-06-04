
ctmApp.register.controller('projectBoardList', ['$routeParams','$http','$scope','$location', '$filter', function ($routeParams,$http,$scope,$location,$filter) {

    $scope.projectName = $routeParams.projectName;
    $scope.oldUrl = $routeParams.url;
    $scope.tabIndex = '0';

    $scope.paginationConf1 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {}
    };
    $scope.paginationConf2 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf3 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };

    // 查询项目看板分配任务及之前的项目
    $scope.getProjectList1 = function(){
        console.log("sssssssssssssss");
        if($scope.paginationConf1.queryObj == null || $scope.paginationConf1.queryObj == ''){
            $scope.paginationConf1.queryObj = {};
        }

        if ($scope.projectName != null && $scope.projectName != ''){
            var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.paginationConf1.queryObj.projectName = projectName;
        }
        $scope.paginationConf1.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf1.queryObj.stageList = ['1', '2'];
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf1),
                "json":JSON.stringify($scope.paginationConf1.queryObj)
            })
        }).success(function(result){
            $scope.projectList1 = result.result_data.list;
            $scope.paginationConf1.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板风控任务的项目
    $scope.getProjectList2 = function(){
        if($scope.paginationConf2.queryObj == null || $scope.paginationConf2.queryObj == ''){
            $scope.paginationConf2.queryObj = {};
        }

        if ($scope.projectName != null && $scope.projectName != ''){
            var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.paginationConf2.queryObj.projectName = projectName;
        }
        $scope.paginationConf2.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf2.queryObj.stageList = ['3','4','5','6'];
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf2),
                "json":JSON.stringify($scope.paginationConf2.queryObj)
            })
        }).success(function(result){
            $scope.projectList2 = result.result_data.list;
            $scope.paginationConf2.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板已完成的项目
    $scope.getProjectList3 = function(){
        if($scope.paginationConf3.queryObj == null || $scope.paginationConf3.queryObj == ''){
            $scope.paginationConf3.queryObj = {};
        }

        if ($scope.projectName != null && $scope.projectName != ''){
            var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.paginationConf3.queryObj.projectName = projectName;
        }
        $scope.paginationConf3.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf3.queryObj.stageList = ['7','9'];
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf3),
                "json":JSON.stringify($scope.paginationConf3.queryObj)
            })
        }).success(function(result){
            $scope.projectList3 = result.result_data.list;
            $scope.paginationConf3.totalItems = result.result_data.totalItems;
        });
    };



    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf1.currentPage + paginationConf1.itemsPerPage', $scope.getProjectList1);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf2.currentPage + paginationConf2.itemsPerPage', $scope.getProjectList2);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf3.currentPage + paginationConf3.itemsPerPage', $scope.getProjectList3);

}]);
