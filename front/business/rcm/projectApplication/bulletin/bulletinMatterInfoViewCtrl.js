define(['app', 'Service'], function (app) {
    app
        .register.controller('bulletinMatterInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('bulletinMatterInfoView');

            var queryParamId = $stateParams.id;
            var tabIndex = $stateParams.tabIndex;
            $scope.initDefaultData = function(){
                $scope.wfInfo = {processKey:'bulletin', "businessId":queryParamId};
                $scope.initData();
            };
            $scope.initData = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectBulletinById,
                    data: $.param({"businessId": queryParamId})
                }).success(function(result){
                    var data = result.result_data;
                    $scope.bulletinOracle = data.bulletinOracle;
                    $scope.bulletin = data.bulletinMongo;
                    $scope.auditLogs = data.logs;
                    $scope.initPage();
                });
            };
            $scope.initDefaultData();
            $scope.initPage = function(){
                if($scope.bulletinOracle.AUDITSTATUS=="1" || $scope.bulletinOracle.AUDITSTATUS=="2"){
                    //流程已启动
                    $("#submitBtn").hide();
                    $scope.wfInfo.businessId = queryParamId;
                    $scope.refreshImg = Math.random()+1;
                }else{
                    //未启动流程
                    $("#submitBtn").show();
                }
            }
            //提交
            $scope.showSubmitModal = function(){
                $scope.approve = {
                    operateType: "submit",
                    processKey: "bulletin",
                    businessId: queryParamId,
                    callbackSuccess: function(result){
                        Window.alert(result.result_name);
                        $('#submitModal').modal('hide');
                        $("#submitBtn").hide();
                        $scope.initData();
                    },
                    callbackFail: function(result){
                        Window.alert(result.result_name);
                    }
                };
                $('#submitModal').modal('show');
            };
            $scope.selectAll = function(){
                if($("#all").attr("checked")){
                    $(":checkbox[name='choose']").attr("checked",1);
                }else{
                    $(":checkbox[name='choose']").attr("checked",false);
                }
            }

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/BulletinMatterList/"+tabIndex);
            }

        }]);
});