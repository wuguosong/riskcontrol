define(['app', 'Service'], function (app) {
    app
        .register.controller('templateFlieListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("templateFlieList");

            if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
                $scope.paginationConf.queryObj = {}
                $scope.paginationConf.queryObj.type = "";
            }

            $scope.initData = function(){
                $scope.paginationConf.currentPage = 1;
                $scope.getTemplateFlieList();
            }
            //查询模板文件列表
            $scope.getTemplateFlieList = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllTemplateFile,
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

            //新增模板文件
            $scope.addTemplateFile = function(){
                $location.path("/index/templateFlieInfo/Create/0");
            }

            //修改模板文件
            $scope.modifyTemplateFile = function(){
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

                $location.path("/index/templateFlieInfo/Modify/" + idsStr);
            }

            //删除模板文件
            $scope.deleteTemplateFile = function(){
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
                        url: BEWG_URL.DeleteAllTemplateFile,
                        data: $.param({"ids": idsStr})
                    }).success(function(data){
                        if(data.success){
                            $scope.getTemplateFlieList();
                        }else{
                            Window.alert(data.result_name);
                        }
                    }).error(function(data,status,headers, config){
                        Window.alert(status);
                    });
                });
            };

            // 跳转详情页面
            $scope.toTemplateFlieInfo = function (id) {
                $location.path("/index/templateFlieInfo/Modify/" + id);
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getTemplateFlieList);

            $scope.initData();
        }]);
});
