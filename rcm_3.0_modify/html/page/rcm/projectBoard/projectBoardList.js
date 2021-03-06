
ctmApp.register.controller('projectBoardList', ['$routeParams','$http','$scope','$location', '$filter', function ($routeParams,$http,$scope,$location,$filter) {

    $scope.projectName = $routeParams.projectName;
    $scope.oldUrl = $routeParams.url;
    var projectName = '';
    $scope.params = {};

    // 初始化分页组件
    $scope.paginationConf1 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 20,
        pagesLength: 5,
        perPageOptions: [5, 10, 20, 30, 40, 50],
        queryObj: {}
    };
    $scope.paginationConf2 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 20,
        pagesLength: 5,
        perPageOptions: [5, 10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };
    $scope.paginationConf3 = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 20,
        pagesLength: 5,
        perPageOptions: [5, 10, 20, 30, 40, 50],
        queryObj: {},
        onChange: function () {
        }
    };

    $scope.initData = function (){
        if (!isEmpty($scope.projectName)){
            projectName = $filter('decodeURI')($scope.projectName,"VALUE");
            if('undefined' == projectName){
                projectName = '';
            }
            $scope.params.projectName = projectName;
        }
    };

    // 查询项目看正式评审项目
    $scope.getPfrProjectList = function(){
        $scope.paginationConf1.queryObj.userId = $scope.credentials.UUID;
        if (!isEmpty($scope.params)) {
            $scope.paginationConf1.queryObj.vote_status = null;
            $scope.paginationConf1.queryObj.decision_result = null;
            $scope.paginationConf1.queryObj.wf_state = null;
            $scope.paginationConf1.queryObj.stage = null;
            $scope.paginationConf1.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf1.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf1.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf1.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf1.queryObj.pertainareaName = $scope.params.pertainareaName;
            // 1:查询正在申请阶段的项目
            if($scope.params.stage == "1") {
                $scope.paginationConf1.queryObj.stage = "1";
                $scope.paginationConf1.queryObj.wf_state = "1";
                // 2 :查询正在风控部门的项目
            } else if($scope.params.stage == "2"){
                $scope.paginationConf1.queryObj.stage = "'2', '3'";
                $scope.paginationConf1.queryObj.wf_state = "'1', '2'";
                // 3 :查询正在处于上会阶段的项目
            } else if($scope.params.stage == "3"){
                $scope.paginationConf1.queryObj.stage = "4";
                $scope.paginationConf1.queryObj.wf_state = "2";
                // 4 :查询已上会，已表决的项目
            } else if($scope.params.stage == "4"){
                $scope.paginationConf1.queryObj.stage = "'6', '7'";
                $scope.paginationConf1.queryObj.wf_state = "2";
                // 5 :查询已上会，未表决的项目
            } else if($scope.params.stage == "5"){
                $scope.paginationConf1.queryObj.stage = "5";
                $scope.paginationConf1.queryObj.vote_status = "0";
                $scope.paginationConf1.queryObj.decision_result = "0";
                $scope.paginationConf1.queryObj.wf_state = "2";
            } else if($scope.params.stage == "6"){
                // 6 :查询已审批，无需上会
                $scope.paginationConf1.queryObj.stage = "9";
                $scope.paginationConf1.queryObj.wf_state = "2";
            } else if($scope.params.stage == "7"){
                // 7 :查询已终止的项目
                $scope.paginationConf1.queryObj.stage = null;
                $scope.paginationConf1.queryObj.wf_state = "3";
            } else {
                $scope.paginationConf1.queryObj.wf_state = $scope.params.wf_state;
            }
            $scope.paginationConf1.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf1.queryObj.applyDateEnd = $scope.params.applyDateEnd;
            $scope.paginationConf1.queryObj.meetingTimeEnd = $scope.params.meetingTimeEnd;
            $scope.paginationConf1.queryObj.meetingTimeStart = $scope.params.meetingTimeStart;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getPfrProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf1),
                "json":JSON.stringify($scope.paginationConf1.queryObj)
            })
        }).success(function(result){
            $scope.projectList1 = result.result_data.list;
            $scope.paginationConf1.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板投标评审项目
    $scope.getPreProjectList = function(){
        $scope.paginationConf2.queryObj.userId = $scope.credentials.UUID;
        if (!isEmpty($scope.params)) {
            $scope.paginationConf2.queryObj.stage = null;
            $scope.paginationConf2.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf2.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf2.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf2.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf2.queryObj.pertainareaName = $scope.params.pertainareaName;
            // 1:查询正在申请阶段的项目
            if($scope.params.stage == "1") {
                $scope.paginationConf2.queryObj.stage = "1";
                $scope.paginationConf2.queryObj.wf_state = "1";
                // 2 :查询正在风控部门的项目
            } else if($scope.params.stage == "2"){
                $scope.paginationConf2.queryObj.stage = "'2', '3'";
                $scope.paginationConf2.queryObj.wf_state = "'1', '2'";
                // 3 :查询正在处于上会阶段的项目
            } else if($scope.params.stage == "3"){
                $scope.paginationConf2.queryObj.stage = "4";
                $scope.paginationConf2.queryObj.wf_state = "2";
                // 4 :查询已上会，已表决的项目
            } else if($scope.params.stage == "4"){
                $scope.paginationConf2.queryObj.stage = "'6', '7'";
                $scope.paginationConf2.queryObj.wf_state = "2";
                // 5 :查询已上会，未表决的项目
            } else if($scope.params.stage == "5"){
                $scope.paginationConf2.queryObj.stage = "5";
                $scope.paginationConf2.queryObj.vote_status = "0";
                $scope.paginationConf2.queryObj.decision_result = "0";
                $scope.paginationConf2.queryObj.wf_state = "2";
            } else if($scope.params.stage == "6"){
                // 6 :查询已审批，无需上会
                $scope.paginationConf2.queryObj.stage = "9";
                $scope.paginationConf2.queryObj.wf_state = "2";
            } else if($scope.params.stage == "7"){
                // 7 :查询已终止的项目
                $scope.paginationConf2.queryObj.stage = null;
                $scope.paginationConf2.queryObj.wf_state = "3";
            } else {
                $scope.paginationConf2.queryObj.wf_state = $scope.params.wf_state;
            }
            $scope.paginationConf2.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf2.queryObj.applyDateEnd = $scope.params.applyDateEnd;
            $scope.paginationConf2.queryObj.meetingTimeEnd = $scope.params.meetingTimeEnd;
            $scope.paginationConf2.queryObj.meetingTimeStart = $scope.params.meetingTimeStart;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getPreProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf2),
                "json":JSON.stringify($scope.paginationConf2.queryObj)
            })
        }).success(function(result){
            $scope.projectList2 = result.result_data.list;
            $scope.paginationConf2.totalItems = result.result_data.totalItems;
        });
    };
    // 查询项目看板其他评审项目
    $scope.getBulletinProjectList = function(){
        $scope.paginationConf3.queryObj.userId = $scope.credentials.UUID;
        if (!isEmpty($scope.params)) {
            $scope.paginationConf3.queryObj.stage = null;
            $scope.paginationConf3.queryObj.projectName = $scope.params.projectName;
            $scope.paginationConf3.queryObj.investmentName = $scope.params.investmentName;
            $scope.paginationConf3.queryObj.reviewPersonName = $scope.params.reviewPersonName;
            $scope.paginationConf3.queryObj.legalReviewPersonName = $scope.params.legalReviewPersonName;
            $scope.paginationConf3.queryObj.pertainareaName = $scope.params.pertainareaName;
            // 1:查询正在申请阶段的项目
            if($scope.params.stage == "1") {
                $scope.paginationConf3.queryObj.stage = "1";
                $scope.paginationConf3.queryObj.wf_state = "1";
                // 2 :查询正在风控部门的项目
            } else if($scope.params.stage == "2"){
                $scope.paginationConf3.queryObj.stage = "'2', '3'";
                $scope.paginationConf3.queryObj.wf_state = "'1', '2'";
                // 3 :查询正在处于上会阶段的项目
            } else if($scope.params.stage == "3"){
                $scope.paginationConf3.queryObj.stage = "4";
                $scope.paginationConf3.queryObj.wf_state = "2";
                // 4 :查询已上会，已表决的项目
            } else if($scope.params.stage == "4"){
                $scope.paginationConf3.queryObj.stage = "'6', '7'";
                $scope.paginationConf3.queryObj.wf_state = "2";
                // 5 :查询已上会，未表决的项目
            } else if($scope.params.stage == "5"){
                $scope.paginationConf3.queryObj.stage = "5";
                $scope.paginationConf3.queryObj.vote_status = "0";
                $scope.paginationConf3.queryObj.decision_result = "0";
                $scope.paginationConf3.queryObj.wf_state = "2";
            } else if($scope.params.stage == "6"){
                // 6 :查询已审批，无需上会
                $scope.paginationConf3.queryObj.stage = "9";
                $scope.paginationConf3.queryObj.wf_state = "2";
            } else if($scope.params.stage == "7"){
                // 7 :查询已终止的项目
                $scope.paginationConf3.queryObj.stage = null;
                $scope.paginationConf3.queryObj.wf_state = "3";
            } else {
                $scope.paginationConf3.queryObj.wf_state = $scope.params.wf_state;
            }
            $scope.paginationConf3.queryObj.applyDateStart = $scope.params.applyDateStart;
            $scope.paginationConf3.queryObj.applyDateEnd = $scope.params.applyDateEnd;
            $scope.paginationConf3.queryObj.meetingTimeEnd = $scope.params.meetingTimeEnd;
            $scope.paginationConf3.queryObj.meetingTimeStart = $scope.params.meetingTimeStart;
        }
        $http({
            method:'post',
            url: srvUrl + "projectBoard/getBulletinProjectList.do",
            data: $.param({
                "page":JSON.stringify($scope.paginationConf3),
                "json":JSON.stringify($scope.paginationConf3.queryObj)
            })
        }).success(function(result){
            $scope.projectList3 = result.result_data.list;
            $scope.paginationConf3.totalItems = result.result_data.totalItems;
        });
    };

    // 查询逻辑
    $scope.getProjectList = function (){
        $scope.getPfrProjectList();
        $scope.getPreProjectList();
        $scope.getBulletinProjectList();
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

    // 更多页面跳转逻辑
    $scope.toMoreList = function (flag) {
        debugger
        if (isEmpty($scope.params.projectName)){
            if (flag == 'pfr') {
                $location.path("/pfrProjectBoardList/"+$filter('encodeURI')('#/projectBoardList/JTI1MjMlMkY='));
            } else if (flag == 'pre') {
                $location.path("/preProjectBoardList/"+$filter('encodeURI')('#/projectBoardList/JTI1MjMlMkY='));
            } else {
                $location.path("/bulletinProjectBoardList/"+$filter('encodeURI')('#/projectBoardList/JTI1MjMlMkY='));
            }
        } else {
            if (flag == 'pfr') {
                $location.path("/pfrProjectBoardList/"+ $filter('encodeURI')($scope.params.projectName) +  '/' +$filter('encodeURI')('#/projectBoardList/JTI1MjMlMkY='));
            } else if (flag == 'pre') {
                $location.path("/preProjectBoardList/"+ $filter('encodeURI')($scope.params.projectName) +  '/' +$filter('encodeURI')('#/projectBoardList/JTI1MjMlMkY='));
            } else {
                $location.path("/bulletinProjectBoardList/"+ $filter('encodeURI')($scope.params.projectName) +  '/' +$filter('encodeURI')('#/projectBoardList/JTI1MjMlMkY='));
            }
        }
    };

    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf1.currentPage + paginationConf1.itemsPerPage', $scope.getPfrProjectList);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf2.currentPage + paginationConf2.itemsPerPage', $scope.getPreProjectList);
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf3.currentPage + paginationConf3.itemsPerPage', $scope.getBulletinProjectList);

    $scope.initData();

}]);
