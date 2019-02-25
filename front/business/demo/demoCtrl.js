define(['app', 'Service'], function (app) {
    app.register
        .controller('demoCtrl', ['$http', '$scope', '$location', '$rootScope', 'BEWG_URL', 'Alert',
        function ($http, $scope, $location, $rootScope, BEWG_URL, Alert) {
            console.log("demo页面");

            booksService.alert("aaaaaaaaaaa");
            console.log(BEWG_URL.LoginUrl);


        }]);
});