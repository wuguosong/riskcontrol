define(['app', 'Service'], function (app) {
    app
        .register.controller('updateProjectNameListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window', '$uibModal',
        function ($http, $scope, $location, $stateParams, BEWG_URL,  Window, $uibModal) {
            console.log("updateProjectNameList");

            $scope.initData = function(){
                $scope.getProjectList();
            }

            $scope.getProjectList = function(){
                /*show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllProject,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    if(result.success){
                        $scope.allProject = result.result_data.list;
                        $scope.paginationConf.totalItems = result.result_data.totalItems;
                    }else{
                        Window.alert(result.result_name);
                    }
                    /*hide_Mask();*/
                });
            };

            // 跳转到项目详情页面
            $scope.toProjectInfo = function (id, projectType,flag) {
                if( projectType == 'formal' && flag != '1'){
                    $location.path("");
                } else if (projectType == 'formal' && flag == '1') {
                    $location.path("");
                } else if (projectType == 'pre' && flag != '1') {
                    $location.path("");
                } else if (projectType == 'pre' && flag == '1') {
                    $location.path("");
                } else {
                    $location.path("");
                }
            }

            $scope.updateProjectNamePop = function () {
                debugger
                var chked = $("input[type=checkbox][name=chk]:checked");
                var checkLength = chked.length;
                if(checkLength != 1){
                    Window.alert("请选择其中一条数据！");
                    return false;
                }
                var vals = chked.val().split("_");
                var obj = {
                    "businessId": vals[0],
                    "type": vals[1]
                };
                var modalInstance = $uibModal.open({
                    animation: true,
                    backdrop: false,
                    controller: 'updateProjectNamePop',
                    templateUrl: 'business/setting/updateProjectName/updateProjectNamePop.html',
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
        .register.controller('updateProjectNamePop', ['$http', '$scope', '$uibModal', '$uibModalInstance', 'BEWG_URL', 'Window', 'obj',
        function ($http, $scope, $uibModal, $uibModalInstance, BEWG_URL, Window, obj) {

            $scope.obj = obj;
            // 关闭弹出框按钮
            $scope.close = function () {
                $uibModalInstance.dismiss();
            };


            // 修改项目名称
            $scope.updateProjectName = function(type,id,reason){
                debugger
                if($scope.obj.projectName.length>80){
                    Window.alert("项目名称不能超过80个字！");
                    return ;
                }
                $http({
                    method:'post',
                    url: BEWG_URL.UpdateProjectName,
                    data: $.param($scope.obj)
                }).success(function (data) {
                    if (data.success) {
                        $uibModalInstance.close();
                    }
                    else{
                        Window.alert(data.result_name);
                    }
                });
            };

        }])
});