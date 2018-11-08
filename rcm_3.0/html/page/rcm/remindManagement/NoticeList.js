

ctmApp.register.controller('NoticeList',  ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams)
{
    var uuid=$scope.credentials.UUID;

    $scope.queryList=function(){

        if($scope.paginationConf.currentPage === 1){
            //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
            $scope.ListAll();
        }else{
            //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
            $scope.paginationConf.currentPage = 1;
        }
    };
    $scope.STATE="1";
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            queryObj:{'CUST_TEXT01':$scope.CUST_TEXT01,'NOTICE_TIME':$scope.NOTICE_TIME,'STATE':$scope.STATE,reader:$scope.credentials.UUID}}
     /*   STATE=1;*/
        var aMethed = 'projectPreReview/ListNotice/listNotice';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                if(data.result_code == "S") {
                    $scope.notice = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                }
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };

    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    //初始化
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
    //将预警或者通知的状态改为已读
    $scope.changeReadState = function(nid){
       $scope.httpData('rcm/NoticeInfo/update',{id:nid,state:'2'});
    }
}]);
function callbackV(code,name){

}