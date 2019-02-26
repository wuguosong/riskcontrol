define(['app','Service'], function (app) {
    app.register.controller('SysRoleListCtrl', ['$http', '$scope', '$location', '$stateParams', '$timeout','Window',function ($http, $scope, $location, $stateParams, $timeout, Window) {
        //分配菜单
        $scope.funcCheck = function (role_id, code) {
            $location.path(PATH_URL_INDEX + "/RoleAndFun/" + role_id + "/" + code + "/oldUrl");
        }
        //分配用户
        $scope.roleCheck = function (role_id, code) {
            $location.path(PATH_URL_INDEX + "/RoleAndUser/" + role_id + "/" + code + "/oldUrl");
        }
        //新建
        $scope.createRole = function () {
            $location.path(PATH_URL_INDEX + "/SysRoleInfo/create/0");
        };
        // 查看
        $scope.roleView = function(role_id){
            $location.path(PATH_URL_INDEX + "/SysRoleView/" + role_id + "/oldUrl");
        }

        //更新
        $scope.updateRole = function () {
            var chk_list = document.getElementsByName("checkbox");
            var uid = "", num = 0;
            for (var i = 0; i < chk_list.length; i++) {
                if (chk_list[i].checked) {
                    num++;
                    uid = uid + ',' + chk_list[i].value;
                }
            }
            if (uid != '') {
                uid = uid.substring(1, uid.length);
            }
            if (num == 0) {
                Window.alert("未选中修改的数据！");
                return false;
            } else if (num > 1) {
                Window.alert("只能选择其中一条数据进行编辑！");
                return false;
            } else {
                $location.path(PATH_URL_INDEX + "/SysRoleInfo/update/" + uid);
            }
        }
        $scope.deleteRole = function () {
            var chk_list = document.getElementsByName("checkbox");
            var uid = "", num = 0;
            for (var i = 0; i < chk_list.length; i++) {
                if (chk_list[i].checked) {
                    num++;
                    uid = uid + ',' + chk_list[i].value;
                }
            }
            if (uid != '') {
                uid = uid.substring(1, uid.length);
            }
            if (num == 0) {
                Window.alert("请选择其中一条数据进行删除！");
                return false;
            } else {
                $http({
                    method: 'post',
                    url: SRV_URL + "role/deleteRoleVali.do",
                    data: $.param({"id": uid})
                }).success(function (result) {
                    if (result.success) {
                        Window.confirm('确认', '确定要删除吗?').result.then(function(){
                            $http({
                                method: 'post',
                                url: SRV_URL + "role/deleteRoleById.do",
                                data: $.param({"id": uid})
                            }).success(function (result) {
                                if (result.success) {
                                    $scope.queryRoleListByPage();
                                } else {
                                    Window.alert(result.result_name);
                                }
                            });
                        });
                    } else {
                        Window.alert(result.result_name);
                    }
                });
            }
        }
        $scope.queryRoleListByPage = function () {
            $http({
                method: 'post',
                url: SRV_URL + "role/queryRoleListByPage.do",
                data: $.param({"page": JSON.stringify($scope.paginationConf)})
            }).success(function (result) {
                if (result.success) {
                    $scope.roleList = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                    console.log(result.result_data.list);
                    //初始化提示信息框
                    $timeout(function () {
                        angular.element(document).ready(function () {
                            var dd = $("[data-toggle='tooltip']");
                            dd.tooltip();
                        });
                    }, 10);
                } else {
                    Window.alert(result.result_name);
                }
            });
        };
        $scope.execQuerRoleListByPage = function () {
            if ($scope.paginationConf.currentPage === 1) {
                //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                $scope.queryRoleListByPage();
            } else {
                //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                $scope.paginationConf.currentPage = 1;
            }
        };
        // 配置分页基本参数
        $scope.paginationConf = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50]
        };
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRoleListByPage);
    }]);
});
