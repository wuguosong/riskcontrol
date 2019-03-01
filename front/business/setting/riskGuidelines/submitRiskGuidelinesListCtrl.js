define(['app', 'Service'], function (app) {
    app
        .register.controller('submitRiskGuidelinesListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("submitRiskGuidelinesList");


            if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
                $scope.paginationConf.queryObj = {}
                $scope.paginationConf.queryObj.type = "";
            }

            $scope.initData = function(){
                $scope.paginationConf.currentPage = 1;
                $scope.getRiskGuidelinesList();
            }
            //查询风险案例列表
            $scope.getRiskGuidelinesList = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitRiskGuidelines,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.submitRiskGuidelines = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            };

            $scope.toRiskGuideline = function (id) {
                $location.path("/index/riskGuidelineInfoView/View/" + id + "/0");
            };

            //文件下载
            $scope.downLoadFile = function(filePath,fileName){
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getRiskGuidelinesList);

            $scope.initData();
        }]);
});
