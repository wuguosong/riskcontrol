define(['app', 'Service'], function (app) {
    app
        .register.controller('preWarningTimeListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("preWarningTimeList");

            $scope.ListAll=function(){
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
                    queryObj:{type:'preAssessment'}};
                $scope.httpData(BEWG_URL.SelectAllPreWaringConfig,$scope.conf).success(function(data){
                    $scope.warning  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };
            $scope.ListAllFormal=function(){
                $scope.conf={currentPage:$scope.paginationConfes.currentPage,pageSize:$scope.paginationConfes.itemsPerPage,
                    queryObj:{type:'formalAssessment'}};
                $scope.httpData(BEWG_URL.SelectAllFormalWaringConfig,$scope.conf).success(function(data){
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

                postObj=$scope.httpData(BEWG_URL.UpdateWaringConfig,{nodeList1:$scope.warning},{nodeList2:$scope.warningFormal});
                postObj.success(function(data){
                    if(data.result_code === 'S'){
                        Window.alert("保存成功！");
                    }else{
                        Window.alert("保存失败！");
                    }
                });
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.paginationConfes = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',$scope.ListAllFormal);
        }]);
});
