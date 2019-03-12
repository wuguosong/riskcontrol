/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/

ctmApp.register.controller('User', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {



    $scope.user = {};


    //保存操作
    $scope.save = function () {

        var aMethed = 'fnd/User/Create';

        $scope.httpData(aMethed,$scope.user).success(
            function (data, status, headers, config) {
                $location.path("/UserList");
            }
        ).error(function (data, status, headers, config) {
            alert(status);

        });



    };
    //验证操作
    $scope.ValidateUser = function () {


    };

    //取消操作
    $scope.cancel = function () {

        $location.path("/UserList");

    };
    //查询一个用户
    $scope.GetUser = function (userID) {

        //var aUrl = 'http://127.0.0.1:8080/ctm-rest/fnd/User/GetByCode';
      //  $scope.cond = {"user_code": userCode};

        var aMethed = 'fnd/User/GetByID';

        $scope.httpData(aMethed,userID).success(
            function (data, status, headers, config) {
                $scope.user = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);

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
    var userID = $routeParams.userID;//getUrlParam("userCode");

   //定义窗口action
    var action =$routeParams.action; //getUrlParam('action');

    if (action == 'Edit') {

        //初始化状态
        $scope.GetUser(userID);
        //编辑状态则初始化下拉列表内容

        var aMethed = 'fnd/group/GetGroup';

        //$scope.httpData(aMethed,{"user_code": userCode}).success(
        //    function (data, status, headers, config) {
        //        $scope.groups = data.result_data;
        //    }
        //).error(function (data, status, headers, config) {
        //    alert(status);
        //
        //});



    } else if (action == 'View') {
        $scope.GetUser(userCode);

        //设置控件只读

    } else if (action == 'Create') {

        //取默认值
        $scope.user.user_code = '';//
        $scope.user.user_name = '';
        $scope.user.user_role = '';//去数据库里查询
        $scope.user.user_group = '';
    };



//        alert(userCode+"--"+action);
//action='Edit';



}]);




