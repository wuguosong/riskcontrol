define(['app','dtree', 'dtree-menu','Service'], function (app) {
    app.register.controller('RoleAndUserCtrl', ['$http', '$scope', '$location', '$stateParams','Window',function ($http, $scope, $location, $stateParams,Window) {
        $scope.roleId = $stateParams.roleId;
        $scope.roleCode = $stateParams.roleCode;
        $scope.oldUrl = $stateParams.url;
        $scope.paginationConf = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50],
            queryObj :{
                roleId:$scope.roleId
            }
        };
        // $scope.paginationConf.queryObj = {};
        // $scope.paginationConf.queryObj.roleId = $scope.roleId;
        console.log($stateParams);
        $scope.mappedKeyValue = {"nameField": "NAME", "valueField": "UUID"};
        $scope.queryParams = {"roleId": $scope.roleId};
        $scope.checkedUsers = [];
        $scope.addRoleUser = function () {
            $http({
                method: 'post',
                url: SRV_URL + "role/addRoleUser.do",
                data: $.param({
                    "roleId": $scope.roleId,
                    "roleCode": $scope.roleCode,
                    "json": JSON.stringify($scope.checkedUsers)
                })
            }).success(function (result) {
                if (result.success) {
                    $scope.queryRoleUserListByPage();
                    $scope.checkedUsers = [];
                } else {
                    Window.alert(result.result_name);
                }
            });
        };

        $scope.cancel = function () {
            $location.path(PATH_URL_INDEX + "/SysRoleList");
        };

        $scope.removeRoleUser = function () {
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
                Window.confirm('确认', '确定要删除吗?').result.then(function(){
                    $http({
                        method: 'post',
                        url: SRV_URL + "role/deleteRoleUserById.do",
                        data: $.param({"id": uid})
                    }).success(function (result) {
                        if (result.success) {
                            $scope.queryRoleUserListByPage();
                        } else {
                            Window.alert(result.result_name);
                        }
                    });
                });
            }
        }

        $scope.queryRoleUserListByPage = function () {
            $http({
                method: 'post',
                url: SRV_URL + "role/queryRoleUserListByPage.do",
                data: $.param({"page": JSON.stringify($scope.paginationConf)})
            }).success(function (result) {
                if (result.success) {
                    $scope.userList = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                } else {
                    Window.alert(result.result_name);
                }
            });
        };
        $scope.execRoleUserListByPage = function () {
            $scope.paginationConf.currentPage = 1;
            $scope.queryRoleUserListByPage();
        };
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRoleUserListByPage);
    }]);
});

