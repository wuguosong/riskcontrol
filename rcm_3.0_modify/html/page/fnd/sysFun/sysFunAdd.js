/**
 * Created by Administrator on 2016/8/11.
 */
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('sysFunAdd', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        //初始化
        var func_id = $routeParams.func_id;
        var action =$routeParams.action;
        $scope.sysfun = {};
        //保存操作
        $scope.save = function () {
            var postObj;
            var url ='';
            if(typeof $scope.sysfun.FUNC_ID!=null  && $scope.sysfun.FUNC_ID!=''  && $scope.sysfun.FUNC_ID!="undefined"){
                url = 'fnd/SysFunction/updateSysFunction';
                postObj=$scope.httpData(url,$scope.sysfun);
            }else{
                url = 'fnd/SysFunction/createSysFunction';
                postObj=$scope.httpData(url,$scope.sysfun);
            }
            postObj.success(function(data){
                    if(data.result_code === 'S'){
                       if(action!="Update"){
                           $location.path("/sysFunList/"+func_id);
                       }else{
                           $location.path("/sysFunList/0");
                       }

                    }else{
                    	$.alert("菜单名称重复!");
                    }
                }
            )
        };
        //表单验证
        $scope.getSysUserByID = function (func_id) {
            var aMethed = 'fnd/SysFunction/Validate';
            $scope.httpData(aMethed,code).success(
                function (data, status, headers, config) {
                    $scope.sysfun != data.result_data;
                }
            ).error(function (data, status, headers, config) {
            	$.alert("此组织编码已存在");
            });
        };

        //验证操作
        $scope.ValidateUser = function () {
        };
        //取消操作
        $scope.cancel = function () {
            $location.path("/sysFunList/"+uid);
        };
        //查询一个用户
        $scope.getSysFunctionByID = function (func_id) {
            var aMethed = 'fnd/SysFunction/getSysFunctionByID';
            $scope.httpData(aMethed,func_id).success(
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
            //初始化状态f
            $scope.getSysFunctionByID(func_id);
            //编辑状态则初始化下拉列表内容
        } else if (action == 'View') {
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
