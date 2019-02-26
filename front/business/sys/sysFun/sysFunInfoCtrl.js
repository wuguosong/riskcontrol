define(['app', 'Service'], function (app) {
    app
        .register.controller('sysFunInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('sysFunInfo');

            var uid=0;
            //初始化
            var func_id = $stateParams.funcId;
            var action =$stateParams.action;
            $scope.title = '新增菜单管理';
            $scope.sysfun = {};
            //保存操作
            $scope.save = function () {
                var postObj;
                if(typeof $scope.sysfun.FUNC_ID!=null  && $scope.sysfun.FUNC_ID!=''  && $scope.sysfun.FUNC_ID!="undefined"){
                    postObj=$scope.httpData(BEWG_URL.UpdateSysFun,$scope.sysfun);
                }else{
                    postObj=$scope.httpData(BEWG_URL.SaveSysFun,$scope.sysfun);
                }
                postObj.success(function(data){
                        if(data.result_code === 'S'){
                            if(action!="Update"){
                                $location.path("/index/sysFunList/"+func_id);
                            }else{
                                $location.path("/index/sysFunList/0");
                            }

                        }else{
                            Window.alert("菜单名称重复!");
                        }
                    }
                )
            };
            //表单验证
            $scope.getSysUserByID = function (func_id) {
                $scope.httpData(BEWG_URL.ValidateSysFunForm,code).success(
                    function (data, status, headers, config) {
                        $scope.sysfun != data.result_data;
                    }
                ).error(function (data, status, headers, config) {
                    Window.alert("此组织编码已存在");
                });
            };

            //验证操作
            $scope.ValidateUser = function () {
            };
            //取消操作
            $scope.cancel = function () {
                $location.path("/index/sysFunList/"+uid);
            };
            //查询一个用户
            $scope.getSysFunctionByID = function (func_id) {
                $scope.httpData(BEWG_URL.SelectSysFunById,func_id).success(
                    function (data, status, headers, config) {
                        $scope.sysfun = data.result_data;
                        uid=$scope.sysfun.FUNC_PID;
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            };
            //选择状态
            $scope.SelOrg = function(){
                var options = {
                    "backdrop" : "static"
                }
                $('#basicModal').modal(options);
            };

            //定义窗口action

            if (action == 'Update') {
                $scope.title = '修改菜单管理'
                //初始化状态f
                $scope.getSysFunctionByID(func_id);
                //编辑状态则初始化下拉列表内容
            } else if (action == 'View') {
                $scope.title = '查看菜单管理'
                $scope.getSysFunctionByID(func_id);
                $('#content-wrapper input').attr("disabled",true);
                $('select').attr("disabled",true);
                $('#savebtn').hide();
                $('#cancelbtn').hide();
                $('#viewlbtn').show();
                //设置控件只读
            } else if (action == 'Create') {
                uid=func_id;
                //取默认值
                $scope.sysfun.FUNC_PID = uid;//
                $scope.sysfun.FUNC_ID = '';//
                $scope.sysfun.STATE = '';
                $scope.sysfun.FUNC_DESC  = '';
                $scope.sysfun.FUNC_NAME = '';
                $scope.sysfun.URL = '';//去数据库里查询
                $scope.sysfun.CREATE_BY = '';//去数据库里查询
                $scope.sysfun.CREATE_DATE = '';//去数据库里查询
                $scope.sysfun.LAST_UPDATE_BY = '';
                $scope.sysfun.LAST_UPDATE_DATE = '';
                $scope.sysfun.SORT = '';
            };
        }]);
});