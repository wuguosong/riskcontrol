/**
 * Created by Administrator on 2019/5/14 0014.
 */
// 待阅
ctmApp.register.controller('MyReadingCtrl', ['$http','$scope','$location','$routeParams','DirPipeSrv', function ($http,$scope,$location,$routeParams, DirPipeSrv) {
    $scope.oldUrl = $routeParams.url;
    $scope.queryMyTaskByPage = function(){
        $http({
            method:'post',
            url:srvUrl+"notify/queryMyReadingPage.do",
            data: $.param({"page":JSON.stringify($scope.paginationConf)})
        }).success(function(result){
            if(result.success){
                $scope.MyReadingPage = result.result_data.list;
                $scope.paginationConf.totalItems = result.result_data.totalItems;
            }else{
                $.alert(result.result_name);
            }
        });
    };
    if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
        $scope.paginationConf.queryObj = {}
    }
    $scope.executeQueryMyTaskByPage = function(){
        $scope.paginationConf.currentPage = 1;
        $scope.queryMyTaskByPage();
    };
    $scope.notify_UpdateStatus = function(id,t){
        notify_UpdateStatus(id, 2);
        // 判断是待阅类型：消息/知会
        if(t['NOTIFY_TYPE'] == 'MESSAGE'){
            DirPipeSrv._setNotifyInfo(t);
        }
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryMyTaskByPage);
}]);

// 已阅
ctmApp.register.controller('MyReadCtrl', ['$http','$scope','$location','$routeParams', 'DirPipeSrv', function ($http,$scope,$location,$routeParams,DirPipeSrv) {
    $scope.oldUrl = $routeParams.url;
    $scope.queryOverTaskByPage = function(){
        $http({
            method:'post',
            url:srvUrl+"notify/queryMyReadPage.do",
            data: $.param({"page":JSON.stringify($scope.paginationConf)})
        }).success(function(result){
            if(result.success){
                $scope.MyReadPage = result.result_data.list;
                $scope.paginationConf.totalItems = result.result_data.totalItems;
            }else{
                $.alert(result.result_name);
            }
        });
    };
    if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
        $scope.paginationConf.queryObj = {}
    }
    $scope.executeQueryOverTaskByPage = function(){
        $scope.paginationConf.currentPage = 1;
        $scope.queryOverTaskByPage();
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryOverTaskByPage);
}]);

