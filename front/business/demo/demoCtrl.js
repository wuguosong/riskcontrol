define(['app', 'Service'], function (app) {
    app.register
        .controller('demoCtrl', ['$http', '$scope', '$location', '$rootScope', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $rootScope, BEWG_URL, Window) {
            console.log("demo页面");

            // WindowAlert.alert("aaaaaaaaaaa");
            console.log(BEWG_URL.LoginUrl);
            Window.confirm('标题', "显示内容").result.then(function (data) {
                console.log("这里是确定的逻辑");
            }, function (btn) {
                console.log("这里是取消的逻辑");
            });


        }]);
});