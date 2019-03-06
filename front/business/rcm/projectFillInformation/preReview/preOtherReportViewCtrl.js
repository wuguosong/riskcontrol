define(['app', 'Service'], function (app) {
    app
        .register.controller('preOtherReportViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('preOtherReportView');

            //初始化
            $scope.paramId = $stateParams.id;
            $scope.flag = $stateParams.flag;

            $scope.pre={};

            $scope.initData = function(){
                $scope.getByID($scope.paramId);
            }

            $scope.getByID = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPreById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.pre  = data.result_data;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            $scope.downLoadFile = function(filePath,filename){
                var isExists = validFileExists(filePath);
                if(!isExists){
                    Window.alert("要下载的文件已经不存在了！");
                    return false;
                }
                if(filename!=null && filename.length>12){
                    filename = filename.substring(0, 12)+"...";
                }else{
                    filename = filename.substring(0,filename.lastIndexOf("."));
                }

                if(undefined!=filePath && null!=filePath){
                    var index = filePath.lastIndexOf(".");
                    var str = filePath.substring(index + 1, filePath.length);
                    var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("投资评审-"+filename+"报告.")) + str;

                    var a = document.createElement('a');
                    a.id = 'tagOpenWin';
                    a.target = '_blank';
                    a.href = url;
                    document.body.appendChild(a);

                    var e = document.createEvent('MouseEvent');
                    e.initEvent('click', false, false);
                    document.getElementById("tagOpenWin").dispatchEvent(e);
                    $(a).remove();
                }else{
                    Window.alert("附件未找到！");
                    return false;
                }
            }

            $scope.initData();

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/PreAuditReportList/" + $scope.flag);
            };
        }]);
});