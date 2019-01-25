define(['app', 'ui-router'], function (app) {
    app
        .register.controller('reportCtrl', ['$http', '$scope', '$location', '$rootScope',
        function ($http, $scope, $location, $rootScope) {
            console.log("报表");
        }]);
});