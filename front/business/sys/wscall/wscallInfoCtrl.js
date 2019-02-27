define(['app', 'Service'], function (app) {
    app
        .register.controller('wscallInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log("wscallInfo");

            var id = $stateParams.id;

            $http({
                method : 'post',
                url : BEWG_URL.SelectWscallById,
                data : $.param({
                    "id" : id
                })
            }).success(function(data) {
                if (data.success) {
                    $scope.log = data.result_data;
                } else {
                    Window.alert(data.result_name);
                }
            });
            $scope.cancel = function () {
                $location.path("/index/wscallList")
            }
        }]);
});