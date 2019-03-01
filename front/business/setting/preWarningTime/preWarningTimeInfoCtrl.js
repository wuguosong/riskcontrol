define(['app', 'Service'], function (app) {
    app
        .register.controller('preWarningTimeInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('preWarningTimeInfo');

            var uid=0;
            $scope.warning = {};
            //保存操作
            $scope.save = function () {
                var postObj;
                var url ='';
                if(typeof $scope.warning.UUID!=null  && $scope.warning.UUID!=''  && $scope.warning.UUID!="undefined"){
                    url = BEWG_URL.UpdateWaringConfigInfo;
                }else{
                    url = BEWG_URL.SaveWaringConfigInfo;
                }
                $scope.httpData(url,$scope.warning).success(function(data){
                    if(data.result_code === 'S'){
                        $location.path("/index/PreWarningTimeList");
                    }else{
                        Window.alert("编码不能重复或必填项不能为空!");
                    }
                });


            };

            //查询一个用户
            $scope.getWarningByID = function (uuid) {
                $scope.httpData(BEWG_URL.SelectPreWaringConfigById,uuid).success(
                    function (data, status, headers, config) {
                        $scope.warning = data.result_data;
                        //uid=$scope.warning.uuid;
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            };

            //初始化
            var uuid = $stateParams.uuid;//getUrlParam("userCode");

            //定义窗口action
            var action =$stateParams.action; //getUrlParam('action');
            if (action == 'Update') {
                //初始化状态f
                $scope.getWarningByID(uuid);
                //编辑状态则初始化下拉列表内容
            }else if (action == 'Create') {

                uid=uuid;
                //取默认值
                $scope.warning.MODEL_CODE = '';
                $scope.warning.MODEL_NAME = '';
                $scope.warning.WARNING_DAY = '';
                $scope.warning.STATE = '';
                $scope.warning.UUID = '';
            };

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/PreWarningTimeList");
            }
        }]);
});

