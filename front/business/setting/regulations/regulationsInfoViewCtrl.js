define(['app', 'Service'], function (app) {
    app
        .register.controller('regulationsInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('regulationsInfoView');

            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.init = function(){
                $scope.getRegulationsInfo($scope.actionpam);
            };

            // 查询模板文件详情
            $scope.getRegulationsInfo = function(id){

                $http({
                    method:'post',
                    url: BEWG_URL.SelectRegulationById,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    $scope.regulationsInfo = result.result_data;
                    if($scope.regulationsInfo.CONTENT != undefined || "" != $scope.regulationsInfo.CONTENT){
                        $scope.regulationsInfo.CONTENT = $scope.regulationsInfo.CONTENT.replace(/<\/br>/g,'\n');
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            };

            //文件下载
            $scope.downLoadFile = function(filePath,fileName){
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
            };

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/submitRegulationsList");
            }
            $scope.init();
        }]);
});