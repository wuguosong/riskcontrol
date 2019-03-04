define(['app', 'Service'], function (app) {
    app
        .register.controller('submitRegulationsListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("submitRegulationsList");

            if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
                $scope.paginationConf.queryObj = {}
                $scope.paginationConf.queryObj.type = "";
            }

            $scope.initData = function(){
                $scope.paginationConf.currentPage = 1;
                $scope.getRegulationsList();
            }
            //查询规章制度列表
            $scope.getRegulationsList = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitRegulation,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.notifications = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;

                    //初始化提示信息框
                    angular.element(document).ready(function() {
                        $("[data-toggle='tooltip']").tooltip();
                    });
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            // 跳转规章制度
            $scope.toRegulationInfo = function(id){
                $location.path("/index/regulationsInfoView/View/"+id);
            }

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRegulationsList);

            $scope.initData();
        }]);
});
