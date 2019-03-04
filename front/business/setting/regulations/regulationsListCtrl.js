define(['app', 'Service'], function (app) {
    app
        .register.controller('regulationsListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("regulationsList");

            if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
                $scope.paginationConf.queryObj = {};
                $scope.paginationConf.queryObj.type = "";
            }

            $scope.initData = function(){
                $scope.paginationConf.currentPage = 1;
                $scope.getRegulationsList();
            }
            //查询规章制度列表
            $scope.getRegulationsList = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllRegulation,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.notifications = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;

                    //初始化提示信息框
                    angular.element(document).ready(function() {
                        $("[data-toggle='tooltip']").tooltip();
                    });
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //新增规章制度
            $scope.addRegulations = function(){
                $location.path("/index/regulationsInfo/Create/0");
            }

            //修改规章制度
            $scope.modifyRegulations = function(){
                var chkObjs = $("input[type=checkbox][name=notifications_checkbox]:checked");
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

                $location.path("/index/regulationsInfo/Modify/" + idsStr);
            }

            //删除规章制度
            $scope.deleteRegulations = function(){
                var chkObjs = $("input[type=checkbox][name=notifications_checkbox]:checked");
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
                        url: BEWG_URL.DeleteRegulation,
                        data: $.param({"ids": idsStr})
                    }).success(function(data){
                        if(data.success){
                            $scope.getRegulationsList();
                        }else{
                            Window.alert(data.result_name);
                        }
                    }).error(function(data,status,headers, config){
                        Window.alert(status);
                    });
                });
            };

            // 跳转规章制度
            $scope.toRegulationInfo = function(id){
                $location.path("/index/regulationsInfo/Modify/"+id);
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRegulationsList);

            $scope.initData();
        }]);
});
