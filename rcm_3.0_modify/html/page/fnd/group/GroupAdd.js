/**
 * Created by Administrator on 2016/8/11.
 */
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('GroupAdd', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        $scope.group = {};
        //保存操作
        $scope.save = function () {
            var postObj;
            var url ='';
            if(typeof $scope.group.UUID!=null  && $scope.group.UUID!=''  && $scope.group.UUID!="undefined"){
                url = 'fnd/Group/updateGroup';
                postObj=$scope.httpData(url,$scope.group);
            }else{
                url = 'fnd/Group/createGroup';
                postObj=$scope.httpData(url,$scope.group);
            }
            postObj.success(function(data){
                    if(data.result_code === 'S'){
                        $location.path("/GroupList/"+uid);
                    }else{
                        $.alert("编码不能重复或必填项不能为空!");
                    }
                }
            )
        };
        //表单验证
        $scope.getSysUserByID = function (uuid) {
            var aMethed = 'fnd/Group/Validate';
            $scope.httpData(aMethed,code).success(
                function (data, status, headers, config) {
                    $scope.group != data.result_data;
                }
            ).error(function (data, status, headers, config) {
                $.alert("此组织编码已存在!");
            });
        };

        //验证操作
        $scope.ValidateUser = function () {
        };
        //取消操作
        $scope.cancel = function () {
            $location.path("/GroupList/"+uid);
        };
        //查询一个用户
        $scope.getGroupByID = function (uuid) {
            var aMethed = 'fnd/Group/getGroupByID';
            $scope.httpData(aMethed,uuid).success(
                function (data, status, headers, config) {
                    $scope.group = data.result_data;
                    uid=$scope.group.PARENT_PK_VALUE;
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
        //初始化
        var uuid = $routeParams.uuid;//getUrlParam("userCode");
        //定义窗口action
        var action =$routeParams.action; //getUrlParam('action');
        if (action == 'Update') {
            //初始化状态f
            $scope.getGroupByID(uuid);
            //编辑状态则初始化下拉列表内容
        } else if (action == 'View') {
            $scope.getGroupByID(uuid);
            $('#content-wrapper input').attr("disabled",true);
            $('select').attr("disabled",true);
            $('#savebtn').hide();
            $('#cancelbtn').hide();
            $('#viewlbtn').show();
            //设置控件只读
        } else if (action == 'Create') {
            uid=uuid;
            //取默认值
            $scope.group.PARENT_PK_VALUE = uid;//
            $scope.group.UUID = '';//
            $scope.group.STATE = '';
            $scope.group.ORG_SHORT_NAME = '';
            $scope.group.NAME = '';
            $scope.group.CODE = '';//去数据库里查询
            $scope.group.ADDRESS = '';//去数据库里查询
            $scope.group.CONTACTS = '';//去数据库里查询
            $scope.group.TELEPHONE = '';
            $scope.group.POST_CODE = '';
            $scope.group.AREA_CODE = '';
            $scope.group.CHARACTER_CODE = '';
            $scope.group.NORMAL_CHARACTER = '';
            $scope.group.CATEGORY_CODE = '';
            $scope.group.CHANGE_TYPE_CODE = '';
            $scope.group.RECEIVE_ORG_CODE = '';
            $scope.group.MERGE_ORG_CODE = '';
            $scope.group.LEGAL_PERSON = '';
            $scope.group.MARKET = '';
            $scope.group.ENTITY = '';
            $scope.group.FINANCIAL_ORG_CODE = '';
            $scope.group.FAX = '';
        };
    }]);
