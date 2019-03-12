

ctmApp.register.controller('PreWarningTimeEdit', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        $scope.warning = {};
        //保存操作
        $scope.save = function () {
            var postObj;
            var url ='';
            if(typeof $scope.warning.UUID!=null  && $scope.warning.UUID!=''  && $scope.warning.UUID!="undefined"){
                url = 'rcm/Warning/updateWarning';
                postObj=$scope.httpData(url,$scope.warning);
            }else{
                url = 'rcm/Warning/createWarning';
                postObj=$scope.httpData(url,$scope.warning);
            }
            postObj.success(function(data){
                if(data.result_code === 'S'){
                    $location.path("/PreWarningTimeList");
                }else{
                	$.alert("编码不能重复或必填项不能为空!");
                }
            });


        };

//查询一个用户
        $scope.getWarningByID = function (uuid) {
            var aMethed = 'rcm/Warning/getWarningByID';
            $scope.httpData(aMethed,uuid).success(
                function (data, status, headers, config) {
                    $scope.warning = data.result_data;
                    //uid=$scope.warning.uuid;
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        };
        //取消操作
        $scope.cancel = function () {
            $location.path("/PreWarningTimeList");
        };


        //初始化
        var uuid = $routeParams.uuid;//getUrlParam("userCode");

        //定义窗口action
        var action =$routeParams.action; //getUrlParam('action');
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
    }]);
