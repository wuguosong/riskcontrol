define(['app', 'Service'], function (app) {
    app.register
        .controller('demoCtrl', ['$http', '$scope', '$location', '$rootScope', 'BEWG_URL', 'WindowAlert',
        function ($http, $scope, $location, $rootScope, BEWG_URL, WindowAlert) {
            console.log("demo页面");

            // WindowAlert.alert("aaaaaaaaaaa");
            console.log(BEWG_URL.LoginUrl);
            WindowAlert.confirm('标题', "显示内容").result.then(function (btn) {
                console.log("这里是确定的逻辑");
            }, function (btn) {
                console.log("这里是取消的逻辑");
            });


        }]);
});