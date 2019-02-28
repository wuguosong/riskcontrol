define(['app', 'Service'], function (app) {
    app
        .register.controller('notificationInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('notificationInfoView');

            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.notificationInfo = {};
            $scope.isNotExpire = true;

            $scope.init = function(){
                $scope.getNotificationInfo($scope.actionpam);
            }

            //提交公告信息
            $scope.submitNotification = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SubmitNotification,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    if(result.success){
                        $scope.getNotificationInfo();
                    }else{
                        Window.alert(result.result_name);
                    }
                });
            };

            // 查询公告信息详情
            $scope.getNotificationInfo = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectNotificationForViewById,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    var notiInfo = result.result_data;
                    if(notiInfo.CONTENT != null && "" != notiInfo.CONTENT){
                        notiInfo.CONTENT = notiInfo.CONTENT.replace(/<\/br>/g,'\n');
                    }
                    $scope.isNotExpire = notiInfo.EXPIRE_DATE == null || notiInfo.EXPIRE_DATE == '';

                    $scope.notificationInfo = notiInfo;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //文件下载
            $scope.downLoadFile = function(filePath,fileName){
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
            }

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/notificationList");
            }

            $scope.init();
        }]);
});