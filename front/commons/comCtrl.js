/**
 * Created by zhangsl on 2016/06/15.
 */
define(['app'], function (app) {
    app.register
        .controller('alert', ['$uibModalInstance', '$scope', '$location', '$cookies', '$rootScope',
        function ($uibModalInstance, $scope, $location, $cookies, $rootScope) {
            // Alert弹出框
            $scope.confirm = function () {
                $uibModalInstance.dismiss("0");
            };
        }]);
});