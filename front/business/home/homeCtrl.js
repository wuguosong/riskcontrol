define(['app', 'ui-router', 'ng-cookies'], function (app) {
    app
        .register.controller('homeCtrl', ['$http', '$scope', '$location', '$cookies', '$rootScope',
        function ($http, $scope, $location, $cookies, $rootScope) {
            console.log("这是首页");
        }]);
});