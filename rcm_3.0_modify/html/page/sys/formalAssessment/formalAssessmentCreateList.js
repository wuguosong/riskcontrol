
ctmApp.register.controller('formalAssessmentCreateList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化页面所需数据
    $scope.initData = function(){
        if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
            $scope.paginationConf.queryObj = {};
        }
        $http({
            method:'post',
            url: srvUrl + "formalAssessmentInfoCreate/getNewProjectList.do",
            data: $.param({"page":JSON.stringify($scope.paginationConf)})
        }).success(function(result){
            $scope.projectList = result.result_data.list;
            $scope.paginationConf.totalItems = result.result_data.totalItems;
        });
    }

    // 新增操作
    $scope.createProject=function(){
        $location.path("/formalAssessmentCreate/0/1");
    };

    // 修改操作
    $scope.updateProject=function(){
        var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
        if(chkObjs.length == 0){
            $.alert("请选择要修改的数据！");
            return false;
        }

        if(chkObjs.length > 1){
            $.alert("请只选择一条数据进行修改!");
            return false;
        }
        var idsStr = "";
        for(var i = 0; i < chkObjs.length; i++){
            idsStr = idsStr + chkObjs[i].value + ",";
        }
        idsStr = idsStr.substring(0, idsStr.length - 1);
        $location.path("/formalAssessmentCreate/" + idsStr + "/2");
    };

    $scope.deleteProject = function () {
        var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
        if(chkObjs.length == 0){
            $.alert("请选择要删除的数据！");
            return false;
        }
        $.confirm("删除后不可恢复，确认删除吗？", function() {
            var idsStr = "";
            for(var i = 0; i < chkObjs.length; i++){
                if(i == chkObjs.length-1){
                    idsStr = idsStr + chkObjs[i].value;
                } else {
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
            }
            $http({
                method:'post',
                url: srvUrl + "formalAssessmentInfoCreate/deleteProject.do",
                data: $.param({"ids":JSON.stringify(idsStr)})
            }).success(function(data){
                if(data.success){
                    $scope.initData();
                    $.alert("执行成功");
                }else{
                    $.alert(data);
                }
            });
        });
    }

    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.initData());
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.initData());

    $scope.initData();
}]);