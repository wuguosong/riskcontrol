define(['app', 'Service'], function (app) {
    app
        .register.controller('riskGuidelinesListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("riskGuidelinesList");


            if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
                $scope.paginationConf.queryObj = {}
                $scope.paginationConf.queryObj.type = "";
            }

            $scope.initData = function(){
                $scope.paginationConf.currentPage = 1;
                $scope.getRiskGuidelinesList();
            }
            //查询风险案例列表
            $scope.getRiskGuidelinesList = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllRiskGuidelines,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.riskGuidelines = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;

                    //初始化提示信息框
                    angular.element(document).ready(function() {
                        $("[data-toggle='tooltip']").tooltip();
                    });
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //新增风险案例
            $scope.addRiskGuideline = function(){
                /*$location.path("/riskGuidelineInfo/Create/0/"+$filter('encodeURI')('#/riskGuidelinesList'));*/
                $location.path("/index/riskGuidelineInfo/Create/0/1");
            }

            //修改风险案例
            $scope.modifyRiskGuideline = function(){
                var chkObjs = $("input[type=checkbox][name=riskGuidelines_checkbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要修改的数据！");
                    return false;
                }

                if(chkObjs.length > 1){
                    Window.alert("请只选择一条数据进行修改!");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);

                $location.path("/index/riskGuidelineInfo/Modify/" + idsStr + "/1");
            }

            //删除风险案例
            $scope.deleteRiskGuideline = function(){
                var chkObjs = $("input[type=checkbox][name=riskGuidelines_checkbox]:checked");
                if (chkObjs.length == 0) {
                    Window.alert("请选择要删除的数据！");
                    return false;
                }

                Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                    var idsStr = "";
                    for (var i = 0; i < chkObjs.length; i++) {
                        var chkValue = chkObjs[i].value.split("/");
                        var chkValue_len = chkValue.length;
                        idsStr = idsStr + chkValue[chkValue_len - 1] + ",";
                    }
                    idsStr = idsStr.substring(0, idsStr.length - 1);
                    $http({
                        method: 'post',
                        url: BEWG_URL.DeleteRiskGuidelines,
                        data: $.param({"ids": idsStr})
                    }).success(function (data) {
                        if (data.success) {
                            $scope.getRiskGuidelinesList();
                        } else {
                            Window.alert(data.result_name);
                        }
                    }).error(function (data, status, headers, config) {
                        Window.alert(status);
                    });
                });
                /*var chkObjs = $("input[type=checkbox][name=riskGuidelines_checkbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要删除的数据！");
                    return false;
                }

                Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                    var idsStr = "";
                    for(var i = 0; i < chkObjs.length; i++){
                        var chkValue = chkObjs[i].value.split("/") ;
                        var chkValue_len = chkValue.length;
                        idsStr = idsStr + chkValue[chkValue_len - 1] + ",";
                    }
                    idsStr = idsStr.substring(0, idsStr.length - 1);
                    $http({
                        method:'post',
                        url:BEWG_URL.DeleteRiskGuidelines,
                        data: $.param({"ids": idsStr})
                    }).success(function(data){
                        if(data.success){
                            $scope.getRiskGuidelinesList();
                        }else{
                            Window.alert(data.result_name);
                        }
                    }).error(function(data,status,headers, config){
                        Window.alert(status);
                    });
                });*/
            };

            $scope.toRiskGuideline = function (id) {
                $location.path("/index/riskGuidelineInfo/Modify/" + id + "/1");
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRiskGuidelinesList);

            $scope.initData();
        }]);
});
