define(['app', 'Service'], function (app) {
    app
        .register.controller('preNormalReportViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('preNormalReportView');


            $scope.paramId = $stateParams.id;
            $scope.flag = $stateParams.flag;

            $scope.pre={};

            $scope.initData = function(){
                $scope.getPreProjectByID($scope.paramId);
            };

            //查义所有的操作
            $scope.getPreProjectByID = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectPreProjectById,
                    data: $.param({"id":id})
                }).success(function(data){
                    if(data.success){
                        $scope.pre = data.result_data;
                        $("#content-wrapper input").attr("disabled","disabled");
                        $("#wordbtn").attr("disabled",false);
                    }else{
                        Window.alert(data.result_name);
                    }
                }).error(function(data,status,headers, config){
                    Window.alert(status);
                });
            };

            $scope.createWord = function(id){
                show_Mask();
                $http({
                    method:'post',
                    url: BEWG_URL.CreateReportPreWord,
                    data: $.param({"id":id})
                }).success(function(data){
                    hide_Mask();
                    if(data.success){
                        var filePath=data.result_data.filePath;
                        var fileName=data.result_data.fileName;
                        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("投资评审-"+fileName+"报告.docx"));
                    }else{
                        Window.alert("提交系统报表生成失败，请检查资料完整性；如果资料完整请联系管理员！");
                    }
                }).error(function(data,status,headers,config){
                    hide_Mask();
                    Window.alert(status);
                });
            }

            $scope.initData();

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/PreAuditReportList/" + $scope.flag);
            };
        }]);
});