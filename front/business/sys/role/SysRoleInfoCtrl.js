define(['app', 'Service'], function (app) {
    app.register.controller('SysRoleInfoCtrl', ['$http', '$scope', '$location', '$stateParams','Window', function ($http, $scope, $location, $stateParams, Window) {
        $scope.role = {};
        $scope.roleId = $stateParams.roleId;
        $scope.action = $stateParams.action;
        $scope.title = $scope.action == "create" ? "新增" : "修改";
        //显示遮罩层
        $scope.show_mask = function () {
            $("#mask_").css("height", $(document).height());
            $("#mask_").css("line-height", $(document).height() + "px");
            $("#mask_").css("width", $(document).width());
            $("#mask_").show();
        }

        //隐藏遮罩层
        $scope.hide_mask = function () {
            $("#mask_").hide();
        }

        $scope.saveRole = function () {
            if ($scope.role.ROLE_NAME == null || $scope.role.ROLE_NAME == "") {
                Window.alert("角色名称必填!");
                return false;
            }
            if ($scope.role.CODE == null || $scope.role.CODE == "") {
                Window.alert("编码必填!");
                return false;
            }
            var isShowPublicSearch = $("input[name='isShowPublicSearch']:checked").val();
            $scope.role.LAST_UPDATE_BY = isShowPublicSearch + "";

            $scope.show_mask();
            var url = "";
            if ($scope.role.ROLE_ID == null || '' == $scope.role.ROLE_ID) {
                url = "role/createRole.do";
            } else {
                url = "role/updateRole.do";
            }
            $http({
                method: 'post',
                url: SRV_URL + url,
                data: $.param({"json": JSON.stringify(angular.copy($scope.role))})
            }).success(function (result) {
                $scope.hide_mask();
                if (result.success) {
                    $scope.role.ID = result.result_data;
                    Window.alert("保存成功!");
                }
                else {
                    Window.alert(result.result_name);
                }
            }).error(function (data, status, headers, config) {
                $scope.hide_mask();
                Window.alert(status);
            });
        };

        // 查询会议通知信息详情
        $scope.queryRoleById = function () {
            $http({
                method: 'post',
                url: SRV_URL + "role/queryById.do",
                data: $.param({"id": $scope.roleId})
            }).success(function (result) {
                $scope.role = result.result_data;

                $("input[name='isShowPublicSearch'][value='" + $scope.role.LAST_UPDATE_BY + "']").attr("checked", "checked");
            });
        }

        $scope.cancel = function () {
            $location.path(PATH_URL_INDEX + "/SysRoleList");
        };

        $scope.initData = function () {
            if ($scope.action == "create") {
                $scope.role.NAME = '';
                $scope.role.CODE = '';
                $scope.role.EXPLAIN = '';
                $scope.role.STATE = '';
            } else {
                $scope.queryRoleById();
            }
        }
        $scope.initData();
    }]);
});