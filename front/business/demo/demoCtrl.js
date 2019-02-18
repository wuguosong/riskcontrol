define(['app'], function (app) {
    app.register
        .controller('demoCtrl', ['$http', '$scope', '$location', '$rootScope',
        function ($http, $scope, $location, $rootScope) {
            console.log("demo页面");
        }]);
});