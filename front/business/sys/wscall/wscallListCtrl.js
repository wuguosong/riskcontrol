define(['app', 'Service'], function (app) {
    app
        .register.controller('wscallListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("wscallList");

            $scope.listAll = function(){
                $http({
                    method:'post',
                    url:BEWG_URL.SelectAllWscall,
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
            $scope.repeatCallOne = function(id){
                $http({
                    method: 'post',
                    url: BEWG_URL.SelectRepeatCallOneWscall,
                    data: $.param({"id": id})
                }).success(function(data){
                    Window.alert(data.result_name);
                    $scope.listAll();
                });
            };
            $scope.toWscallInfo = function (id){
                $location.path("/index/wscallInfo/"+id);
            }

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.listAll);
        }]);
});