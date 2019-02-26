define(['app', 'Service'], function (app) {
    app
        .register.controller('endFlowListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window', '$uibModal',
        function ($http, $scope, $location, $stateParams, BEWG_URL,  Window, $uibModal) {
            console.log("endFlowList");

            $scope.initData = function(){
                $scope.getProjectList();
            }

            $scope.getProjectList = function(){
                /*show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.SelectProjectListEndFlow,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    if(result.success){
                        $scope.pList = result.result_data.list;
                        $scope.paginationConf.totalItems = result.result_data.totalItems;
                    }else{
                        Window.alert(result.result_name);
                    }
                    /*hide_Mask();*/
                });
            }

            /*$scope.endFlow = function(type,id,reason){
                if("" == reason || null == reason){
                    Window.alert("终止原因不能为空！");
                    return;
                }
                $http({
                    method:'post',
                    url: BEWG_URL.doEndFlow,
                    data: $.param({"type":type,"businessId":id,"reason":reason
                    })
                }).success(function(result){
                    if(result.success){
                        Window.alert("操作成功！");
                        $scope.getProjectList();
                    }else{
                        Window.alert(result.result_name);
                    }
                });
            }*/

            // 跳转到项目详情页面
            $scope.toProjectInfo = function (id, projectType) {
                if(projectType == 'pfr'){
                    $location.path("");
                } else if (projectType == 'pre') {
                    $location.path("");
                } else {
                    $location.path("");
                }
            }

            $scope.endFlowPop = function (obj) {
                var modalInstance = $uibModal.open({
                    animation: true,
                    backdrop: false,
                    controller: 'endFlowPop',
                    templateUrl: 'business/sys/endFlow/endFlowPop.html',
                    windowClass: 'big-dialog',
                    resolve: {
                        obj: function () {
                            return obj;
                        }
                    }
                });
                modalInstance.result.then(function () {
                    Window.alert("操作成功！");
                    $scope.getProjectList();
                }, function () {
                    console.log("bbbbbbbbb");
                });
            }

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getProjectList);
        }]);
    app
        .register.controller('endFlowPop', ['$http', '$scope', '$uibModal', '$uibModalInstance', 'BEWG_URL', 'Window', 'obj',
        function ($http, $scope, $uibModal, $uibModalInstance, BEWG_URL, Window, obj) {

            $scope.obj = obj;
            // 关闭弹出框按钮
            $scope.close = function () {
                $uibModalInstance.dismiss();
            };


            //终止操作
            $scope.endFlow = function(type,id,reason){
                if("" == reason || null == reason){
                    Window.alert("终止原因不能为空！");
                    return;
                }
                $http({
                    method:'post',
                    url: BEWG_URL.doEndFlow,
                    data: $.param({"type":type,"businessId":id,"reason":reason
                    })
                }).success(function (data) {
                    if (data.success) {
                        $uibModalInstance.close();
                    }
                    else{
                        $.alert(data.result_name);
                    }
                });
            };

        }])
});