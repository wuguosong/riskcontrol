define(['app', 'Service'], function (app) {
    app
        .register.controller('businessModelListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('businessModelList');

            //查义所有的操作
            $scope.queryList=function(){

                if($scope.paginationConf.currentPage === 1){
                    //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                    $scope.ListAll();
                }else{
                    //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                    $scope.paginationConf.currentPage = 1;
                }
            };

            $scope.ListAll=function(){
                //  var value=$("#VALUE").val();
                //  var key=$("#KEY").val();
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'VALUE':$scope.VALUE,'KEY':$scope.KEY}};
                $scope.httpData(BEWG_URL.SelectAllBusinessModel,$scope.conf).success(function(data){
                    // 变更分页的总数
                    $scope.item  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };

            // 跳转商业列表页面
            $scope.toListBusiness = function (id, value, code) {
                $location.path("index/ListBusiness/"+id+"/"+value+"/"+code);
            };

            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
        }]);
});