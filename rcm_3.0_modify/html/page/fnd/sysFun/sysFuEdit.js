/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/

ctmApp.register.controller('FormBuilder', ['$http','$scope','$location', function ($http,$scope,$location) {

    //查义所有的操作
    $scope.ListAll=function(){

        var aMethed = 'sys/formbuilder/GetAll';

        $scope.httpData(aMethed,{}).success(
            function (data, status, headers, config) {
                $scope.users = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);

        });

    };
    //按条件查询操作
    $scope.ListBy=function(){

        var aMethed = 'sys/formbuilder/GetAll';

        $scope.httpData(aMethed,{}).success(
            function (data, status, headers, config) {
                $scope.users = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);

        });

    };

    //新建操作
    $scope.Create=function(){

        $location.path("/User/Create/0");
    };

    //更新操作
    $scope.Update=function(aUserID){

        $location.path("/User/Edit/"+aUserID);
    };

    //删除操作
    $scope.Delete=function(aUserID){

        //$location.path("/User");

        aUser={"user_code": aUserID};

        $.confirm("确定要删除？", function(){

            var aMethed = 'fnd/User/Delete';

            $scope.httpData(aMethed,aUser).success(
                function (data, status, headers, config) {
                    $scope.ListAll();
                }
            ).error(function (data, status, headers, config) {
                alert(status);

            });

        });

    };
    //查看操作
    $scope.View=function(aUserID){

        $location.path("/User/View/"+aUserID);
    };


    //控制器初始化方法
    $scope.ListAll();



}]);


