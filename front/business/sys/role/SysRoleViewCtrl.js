define(['app'], function (app) {
    app.register.controller('SysRoleViewCtrl', ['$http', '$scope', '$location', '$stateParams', function ($http, $scope, $location, $stateParams) {
        $scope.role = {};
        $scope.roleId = $stateParams.roleId;
        $scope.returnUrl = $stateParams.url;
        console.log("$stateParams...start");
        console.log($stateParams);
        console.log("$stateParams...end");

        $scope.queryRoleById = function () {
            $http({
                method: 'post',
                url: SRV_URL + "role/queryById.do",
                data: $.param({"id": $scope.roleId})
            }).success(function (result) {
                $scope.role = result.result_data;
            });
        }

        $scope.initData = function () {
            $scope.queryRoleById();
        }
        $scope.initData();

        $scope.cancel = function () {
            $location.path(PATH_URL_INDEX + "/SysRoleList");
        };

    }]);
});