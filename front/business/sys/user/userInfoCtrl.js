define(['app'], function (app) {
    app
        .register.controller('userInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log("userInfo");

            var uid=0;
            $scope.sysUser = {};
            //保存操作
            $scope.save = function () {
                var postObj;
                var url ='';
                if(typeof $scope.sysUser.UUID!=null  && $scope.sysUser.UUID!=''  && $scope.sysUser.UUID!="undefined"){
                    url = 'fnd/SysUser/updateSysUser';
                    postObj=$scope.httpData(url,$scope.sysUser);
                }else{
                    url = 'fnd/SysUser/createSysUser';
                    postObj=$scope.httpData(url,$scope.sysUser);
                }
                postObj.success(function(data){
                        if(data.result_code === 'S'){
                            $location.path("index/userList");
                        }else{
                            Window.alert(data.result_name);
                        }
                    }
                )
            };
            //验证操作
            $scope.ValidateUser = function () {
            };
            //取消操作
            $scope.cancel = function () {
                $location.path("index/userList");
            };
            //查询一个用户
            $scope.getSysUserByID = function (uuid) {
                $http({
                    method:'post',
                    url: BEWG_URL.srvUrl + "user/getSysUserByID.do",
                    data:$.param({"id":uuid})
                }).success(function(data){
                    if(data.success){
                        console.log(data.result_data.userDate)
                        $scope.sysUser = data.result_data.userDate;
                        $scope.userPotition = data.result_data.postition;
                        uid=$scope.sysUser.ORG_CODE;
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            };
            //选择组织
            $scope.SelOrg = function(){
                var options = {
                    "backdrop" : "static"
                }
                $('#basicModal').modal(options);
            };
            //初始化
            var uuid = $stateParams.userId;//getUrlParam("userCode");
            //定义窗口action
            var action =$stateParams.action; //getUrlParam('action');
            if (action == 'Update') {
                //初始化状态
                $scope.getSysUserByID(uuid);
                //编辑状态则初始化下拉列表内容
            } else if (action == 'View') {
                $scope.getSysUserByID(uuid);
                $('#savebtn').hide();
                $('#cancelbtn').hide();
                $('#viewlbtn').show();
                //设置控件只读
            } else if (action == 'Create') {
                uid=uuid;
                //取默认值
                $scope.sysUser.TYPE_CODE = '';//
                $scope.sysUser.UUID = '';//
                $scope.sysUser.NAME = '';
                $scope.sysUser.CODE = '';//去数据库里查询
                $scope.sysUser.SEX_CODE = '';
                $scope.sysUser.DEPT_CODE = '';//去数据库里查询
                $scope.sysUser.ACCOUNT = '';
                $scope.sysUser.EMAIL = '';
                $scope.sysUser.ORG_CODE = uid;//去数据库里查询
                $scope.sysUser.STATE = '';
                $scope.sysUser.ID_CARD = '';//去数据库里查询
            };
        }]);
});