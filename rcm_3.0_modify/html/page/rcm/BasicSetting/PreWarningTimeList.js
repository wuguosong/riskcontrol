/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('PreWarningTimeList', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams)
{
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            queryObj:{type:'preAssessment'}};
        var  url = 'projectPreReview/ListNotice/listWaringConfig';
        $scope.httpData(url,$scope.conf).success(function(data){
                $scope.warning  = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
        });
    };
    $scope.ListAllFormal=function(){
        $scope.conf={currentPage:$scope.paginationConfes.currentPage,pageSize:$scope.paginationConfes.itemsPerPage,
            queryObj:{type:'formalAssessment'}};
        var  url = 'projectPreReview/ListNotice/listFormalWaringConfig';
        $scope.httpData(url,$scope.conf).success(function(data){
            $scope.warningFormal  = data.result_data.list;
            $scope.paginationConfes.totalItems = data.result_data.totalItems;
        });
    };
  /*  $scope.ListAllFormal=function(type){
        // var preAssessment;
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            currentPage:$scope.paginationConfes.currentPage,pageSize:$scope.paginationConfes.itemsPerPage,
            queryObj:{'Assessment':type}};
        var  url = 'projectPreReview/ListNotice/listWaringConfig';
   $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            if(type == 'preAssessment'){
                $scope.warning  = data.result_data.list;
                console.log(data.resulet_data.list);
            }else if(type == 'formalAssessment'){
                $scope.warningFormal  = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
            }
        });
    };*/



    $scope.save=function(){

        var  url = 'projectPreReview/ListNotice/updateWaringConfig';
            postObj=$scope.httpData(url,{nodeList1:$scope.warning},{nodeList2:$scope.warningFormal});
            postObj.success(function(data){
            if(data.result_code === 'S'){
            	$.alert("保存成功！");
            }else{
            	$.alert("保存失败！");
            }
        });
    };

    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',$scope.ListAllFormal);


}]);
