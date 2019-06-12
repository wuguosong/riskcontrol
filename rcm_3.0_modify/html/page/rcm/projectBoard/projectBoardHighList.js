
ctmApp.register.controller('projectBoardHighList', ['$routeParams','$http','$scope','$location', '$filter', function ($routeParams,$http,$scope,$location,$filter) {

    /*$scope.projectName = $routeParams.projectName;*/
    $scope.oldUrl = $routeParams.url;
    $scope.tabIndex = '0';
    /*var projectName = '';*/
    $scope.params = {};

    // 初始化分页组件
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
    $scope.paginationConf4 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };

    /*$scope.initData = function (){
        if (!isEmpty($scope.projectName)){
            projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            $scope.params.projectName = projectName;
        }
    };*/

    // 查询项目看 - 分配前
    $scope.getProjectList1 = function(){
        $scope.paginationConf1.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf1.queryObj.stageList = ['1', '2'];
        $scope.paginationConf1.queryObj.wf_state_n = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf1.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf1.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf1.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf1.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf1.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf1.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf1.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf1.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
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
    // 查询项目看板 - 风控任务
    $scope.getProjectList2 = function(){
        $scope.paginationConf2.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf2.queryObj.stageList = ['3','4','5'];
        $scope.paginationConf2.queryObj.wf_state_n = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf2.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf2.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf2.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf2.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf2.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf2.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf2.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf2.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
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
    // 查询项目看板 - 已完成
    $scope.getProjectList3 = function(){
        $scope.paginationConf3.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf3.queryObj.stageList = ['6','7','9'];
        $scope.paginationConf3.queryObj.wf_state_n = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf3.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf3.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf3.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf3.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf3.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf3.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf3.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf3.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
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

    // 查询项目看板 - 已终止
    $scope.getProjectList4 = function(){
        $scope.paginationConf4.queryObj.userId = $scope.credentials.UUID;
        $scope.paginationConf4.queryObj.wf_state = '3';
        if (!isEmpty($scope.params)) {
            $scope.paginationConf4.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf4.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf4.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf4.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf4.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf4.queryObj.projectType = $scope.params.projectType;
            $scope.paginationConf4.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf4.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf4),
                "json":JSON.stringify($scope.paginationConf4.queryObj)
            })
        }).success(function(result){
            $scope.projectList4 = result.result_data.list;
            $scope.paginationConf4.totalItems = result.result_data.totalItems;
        });
    };

    $scope.getProjectList = function (){
        $scope.getProjectList1();
        $scope.getProjectList2();
        $scope.getProjectList3();
        $scope.getProjectList4();
        $("#publicProjectName").val('');
    };

    $scope.cancel = function () {
        if (!isEmpty($scope.params)) {
            $scope.params.projectName = '';
            $scope.params.investmentName = '';
            $scope.params.reviewPersonName = '';
            $scope.params.legalReviewPersonName = '';
            $scope.params.pertainareaName = '';
            $scope.params.projectType = '';
            $scope.params.applyDateEnd = '';
            $scope.params.applyDateStart = '';
            $scope.getProjectList();
        }
    };

    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf1.currentPage + paginationConf1.itemsPerPage', $scope.getProjectList1);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf2.currentPage + paginationConf2.itemsPerPage', $scope.getProjectList2);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf3.currentPage + paginationConf3.itemsPerPage', $scope.getProjectList3);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf4.currentPage + paginationConf4.itemsPerPage', $scope.getProjectList4);

    /*$scope.initData();*/

}]);
