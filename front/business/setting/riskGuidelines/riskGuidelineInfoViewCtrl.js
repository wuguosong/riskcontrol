define(['app', 'Service'], function (app) {
    app
        .register.controller('riskGuidelineInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('riskGuidelineInfoView');

            $scope.flag = $stateParams.flag;
            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.rideGuidelineInfo = {};

            $scope.init = function(){
                $scope.getRiskGuidelineInfo($scope.objId);
            };

            // 查询风险信息详情
            $scope.getRiskGuidelineInfo = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectRiskGuidelineByIdForView,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    $scope.rideGuidelineInfo = result.result_data;
                    if($scope.rideGuidelineInfo.CONTENT != undefined || "" != $scope.rideGuidelineInfo.CONTENT){
                        $scope.rideGuidelineInfo.CONTENT = $scope.rideGuidelineInfo.CONTENT.replace(/<\/br>/g,'\n');
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            };

            // 返回列表
            $scope.cancel = function (){
                if ($scope.flag == 1){
                    $location.path("/index/riskGuidelinesList");
                } else {
                    $location.path("/index/submitRiskGuidelinesList");
                }
            };

            //文件下载
            $scope.downLoadFile = function(filePath,fileName){
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
            };

            $scope.init();
        }]);
});