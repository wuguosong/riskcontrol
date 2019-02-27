define(['app', 'Service'], function (app) {
    app
        .register.controller('journalListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('journalList');

            $scope.listAll = function(){
                $http({
                    method:'post',
                    url:BEWG_URL.SelectAllJournal,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    if(data.success){
                        $scope.logs = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            };
            $scope.toJournalInfo = function (id){
                $location.path("/index/journalInfo/"+id);
            };
            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.listAll);
        }]);


    app
        .register.controller('journalDetailCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('journalDetail');

            $scope.findById = function(id){
                $scope.httpData(BEWG_URL.SelectJournalDetailById,{id:id}).success(function(data){
                    if(data.result_code == "S"){
                        $scope.l = data.result_data;
                    }
                });
            }
            $scope.findById($stateParams.id);
        }]);
});