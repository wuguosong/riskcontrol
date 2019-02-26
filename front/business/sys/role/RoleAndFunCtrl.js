/**
 * Created by gl on 2016/8/16.
 */
define(['app', 'Service'], function (app) {
    app.register.controller('RoleAndFunCtrl', ['$http', '$scope', '$location', '$stateParams', function ($http, $scope, $location, $stateParams) {
        $scope.roleId = $stateParams.roleId;
        $scope.roleCode = $stateParams.roleCode;
        $scope.oldUrl = $stateParams.url;
        $scope.sysRole = {};
        //保存操作
        $scope.save = function () {
            var str = "", num = 0;
            $("input[type='checkbox']:checked").each(function () {
                str += $(this).attr("value") + ",";
                num++;
            });
            if (str != '') {
                str = str.substring(0, str.length - 1);
            }
            $http({
                method: 'post',
                url: SRV_URL + "role/createOrUpdateRoleAndFunc.do",
                data: $.param({"UUID": str, "ROLE_ID": $scope.roleId, 'ROLE_CODE': $scope.roleCode})
            }).success(function (result) {
                alert("保存成功！");
            });
        };
        //根据roleId获取菜单列表
        $scope.getRoleAndFunc = function () {
            $("#RoleTreeID a").attr("class", "");
            dtrees3.setDefaultIsCheck();
            $http({
                method: 'post',
                url: SRV_URL + "role/getRoleAndFunc.do",
                data: $.param({"roleId": $scope.roleId})
            }).success(function (result) {
                var funcs = result.result_data;
                var strs = '';
                for (var j = 0; j < funcs.length; j++) {
                    strs = strs + "," + funcs[j];
                }
                strs = strs.substring(1, strs.length);
                if (null != strs && '' != strs && strs.length > 0) {
                    dtrees3.setDefaultCheck(strs);
                }
                $("#" + $scope.roleId).attr("class", "selected");
            });
        };

        // 返回
        $scope.cancel = function () {
            $location.path(PATH_URL_INDEX + "/SysRoleList");
        };

        //获取菜单结构
        $scope.getFunc = function () {
            $http({
                method: 'post',
                url: SRV_URL + 'role/queryFunc.do'
            }).success(function (result) {
                var orgArr = result.result_data;
                dtrees3 = new dTree('dtrees3');
                dtrees3.add(0, -1, '菜单管理');
                for (var i = 0; i < orgArr.length; i++) {
                    dtrees3.add(orgArr[i].FUNC_ID, orgArr[i].FUNC_PID, orgArr[i].FUNC_NAME);
                }
                dtrees3.config.check = true;
                document.getElementById("treeID3").innerHTML = dtrees3;
                $scope.getRoleAndFunc();
            });
        };

        //初始化
        //getUrlParam("userCode");
        $scope.getFunc();
    }]);
});
