define(['app', 'Service'], function (app) {
    app.register
        .controller('homeCtrl', ['$http', '$scope', '$location', '$cookies', '$rootScope', 'booksService', 'BEWG_URL',
            function ($http, $scope, $location, $cookies, $rootScope, booksService, BEWG_URL) {
                // booksService.alert("aaaaaaa");
                console.log(BEWG_URL.port);
                console.log("这是首页");
            }])
});