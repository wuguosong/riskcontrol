/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('BusinessModelList', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams)
{
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
        var  url = 'rcm/BusinessModel/getAll';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            $scope.item  = data.result_data.list;
            $scope.paginationConf.totalItems = data.result_data.totalItems;
        });
    };


    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);

}]);
function callbackV(code,name){

}