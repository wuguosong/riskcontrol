ctmApp.register.controller('reportDataListCtrl', ['$routeParams', '$http', '$scope', '$location', '$filter', function ($routeParams, $http, $scope, $location, $filter) {
    $scope.projectName = $routeParams.projectName;
    $scope.oldUrl = $routeParams.url;
    var projectName = '';
    $scope.params = {};
    // 初始化分页组件
    $scope.paginationConf1 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 5,
        perPageOptions: [5, 10, 20, 30, 40, 50],
        queryObj: {}
    };
    // 默认查询条件中携带当前登录用户
    $scope.paginationConf1.queryObj.loginUser = $scope.credentials.UUID;
    $scope.initData = function () {
        if (!isEmpty($scope.projectName)) {
            projectName = $filter('decodeURI')($scope.projectName, "VALUE");
            $scope.params.projectName = projectName;
        }
    };
    // 查询项目看正式评审项目
    $scope.getPfrProjectList = function () {
        $scope.paginationConf1.queryObj.userId = $scope.credentials.UUID;
        if (!isEmpty($scope.params)) {
            $scope.paginationConf1.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf1.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf1.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf1.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf1.queryObj.pertainareaName = $scope.params.pertainareaName;
            $scope.paginationConf1.queryObj.wf_state = $scope.params.wf_state;
            $scope.paginationConf1.queryObj.stage = $scope.params.stage;
            $scope.paginationConf1.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf1.queryObj.applyDateEnd = $scope.params.applyDateEnd;
        }
        $http({
            method: 'post',
            url: srvUrl + "reportData/getProjectList.do",
            data: $.param({
                "page": JSON.stringify($scope.paginationConf1),
                "json": JSON.stringify($scope.paginationConf1.queryObj)
            })
        }).success(function (result) {
            $scope.projectList1 = result.result_data.list;
            $scope.paginationConf1.totalItems = result.result_data.totalItems;
        });
    };
    // 查询逻辑
    $scope.getProjectList = function () {
        $scope.getPfrProjectList();
        $("#publicProjectName").val('');
    };
    // 清除逻辑
    $scope.cancel = function () {
        if (!isEmpty($scope.params)) {
            $scope.params.projectName = '';
            $scope.params.investmentName = '';
            $scope.params.reviewPersonName = '';
            $scope.params.legalReviewPersonName = '';
            $scope.params.pertainareaName = '';
            $scope.params.stage = '';
            $scope.params.wf_state = '';
            $scope.params.applyDateEnd = '';
            $scope.params.applyDateStart = '';
            $scope.getProjectList();
        }
    };
    // 监听分页条件变化
    $scope.$watch('paginationConf1.currentPage + paginationConf1.itemsPerPage', $scope.getPfrProjectList);
    $scope.initData();
}]);
