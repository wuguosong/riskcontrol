define(['app', 'Service'], function (app) {
    app
        .register.controller('templateFlieInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('templateFlieInfoView');

            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.init = function(){
                $scope.getTemplateFlieInfo($scope.actionpam);
            };

            // 查询模板文件详情
            $scope.getTemplateFlieInfo = function(id){

                $http({
                    method:'post',
                    url: BEWG_URL.SelectTemplateFileById,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    $scope.templateFlieInfo = result.result_data;
                    if($scope.templateFlieInfo.CONTENT != undefined || "" != $scope.templateFlieInfo.CONTENT){
                        $scope.templateFlieInfo.CONTENT = $scope.templateFlieInfo.CONTENT.replace(/<\/br>/g,'\n');
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
                $location.path("/index/submitTemplateFlieList");
            }
            $scope.init();
        }]);
});