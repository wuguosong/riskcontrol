
ctmApp.register.controller('projectBoardList', ['$routeParams','$http','$scope','$location', '$filter', function ($routeParams,$http,$scope,$location,$filter) {

    $scope.projectName = $routeParams.projectName;
    $scope.oldUrl = $routeParams.url;
    $scope.tabIndex = '0';

    $scope.initData = function(){
        $scope.getProjectList1();
        $scope.getProjectList2();
        $scope.getProjectList3();
    };
    // 查询项目看板分配任务及之前的项目
    $scope.getProjectList1 = function(){
        if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
            $scope.paginationConf.queryObj = {};
        }

        if ($scope.projectName != null && $scope.projectName != ''){
            var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.paginationConf.queryObj.projectName = projectName;
        }
        $scope.paginationConf.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf.queryObj.stageList = ['1', '2'];
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf),
                "json":JSON.stringify($scope.paginationConf.queryObj)
            })
        }).success(function(result){
            $scope.projectList1 = result.result_data.list;
            $scope.paginationConf.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板风控任务的项目
    $scope.getProjectList2 = function(){
        if($scope.paginationConfes.queryObj == null || $scope.paginationConf.queryObj == ''){
            $scope.paginationConfes.queryObj = {};
        }

        if ($scope.projectName != null && $scope.projectName != ''){
            var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.paginationConfes.queryObj.projectName = projectName;
        }
        $scope.paginationConfes.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConfes.queryObj.stageList = ['3','4','5','6'];
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConfes),
                "json":JSON.stringify($scope.paginationConfes.queryObj)
            })
        }).success(function(result){
            $scope.projectList2 = result.result_data.list;
            $scope.paginationConfes.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板已完成的项目
    $scope.getProjectList3 = function(){
        if($scope.paginationConfesd.queryObj == null || $scope.paginationConfesd.queryObj == ''){
            $scope.paginationConfesd.queryObj = {};
        }

        if ($scope.projectName != null && $scope.projectName != ''){
            var projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.paginationConfesd.queryObj.projectName = projectName;
        }
        $scope.paginationConfesd.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConfesd.queryObj.stageList = ['7','9'];
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConfesd),
                "json":JSON.stringify($scope.paginationConfesd.queryObj)
            })
        }).success(function(result){
            $scope.projectList3 = result.result_data.list;
            $scope.paginationConfesd.totalItems = result.result_data.totalItems;
        });
    };

    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {}
    };
    $scope.paginationConfes = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConfesd = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };

    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getProjectList1());
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getProjectList2());
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConfesd.currentPage + paginationConfesd.itemsPerPage', $scope.getProjectList3());
}]);
