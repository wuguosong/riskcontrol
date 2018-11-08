/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/

ctmApp.register.controller('ReviewList', ['$http','$scope','$location', function ($http,$scope,$location) {

    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'user_id':$scope.credentials.UUID,'PROJECT_NAME':$scope.PROJECT_NAME,'WF_STATE':'2','APPLY_TIME':$scope.APPLY_TIME,'ASCDESC':$scope.orderby}};
        var url =  'projectPreReview/ProjectPreReview/getAll';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            $scope.review  = data.result_data.list;
            $scope.paginationConf.totalItems = data.result_data.totalItems;
        });
    };

    //初始化
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
    $scope.order=function(v){
            $scope.orderby=v;
            if(v=="asc"){
                $("#orderasc").addClass("cur");
                $("#orderdesc").removeClass("cur");
            }else{
                $("#orderdesc").addClass("cur");
                $("#orderasc").removeClass("cur");
            }
        $scope.ListAll();
    }
    $scope.import=function(){
        var aMethed =  'projectPreReview/ProjectPreReview/importReviewListAll';
        $scope.httpData(aMethed,$scope.credentials.UUID).success(
            function (data) {
                if(data.result_code=="S"){
                    var file=data.result_data.filePath;
                    var fileName=data.result_data.fileName;
                    window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(file)+"&fileName="+encodeURI(fileName);
                }
            }

        ).error(function (data, status, headers, config) {
            alert(status);
        });
    }
}]);
