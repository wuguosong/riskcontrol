define(['app', 'Service'], function (app) {
    app
        .register.controller('journalInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('journalInfo');

            $scope.id = $stateParams.id;
            $scope.getByID = function(){
                $http({
                    method:'POST',
                    url:BEWG_URL.SelectJournalById,
                    data: $.param({"id":$scope.id})
                }).success(function(data){
                    if(data.success){
                        $scope.log = data.result_data;
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            };

            $scope.cancel = function () {
              $location.path("/index/journalList");
            };
            $scope.getByID();
        }]);
});