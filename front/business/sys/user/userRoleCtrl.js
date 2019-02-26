define(['app'], function (app) {
    app
        .register.controller('userRoleCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL,  Window) {
            console.log("userRole");

            $scope.userId = $stateParams.userId;
            $scope.initData = function() {
                $scope.getAllRole();
            }
            $scope.getAllRole = function() {
                $http({
                    method : 'post',
                    url : BEWG_URL.srvUrl + 'user/getAllRole.do'
                }).success(function(data) {
                    if (data.success) {
                        $scope.roleList = data.result_data;
                        // 获取用户角色关联
                        $scope.getUserRole();
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            }
            $scope.getUserRole = function() {
                $http({
                    method : 'post',
                    url : BEWG_URL.srvUrl + 'user/getRoleByUserId.do',
                    data : $.param({
                        "userId" : $scope.userId
                    })
                }).success(function(data) {
                    if (data.success) {
                        $scope.roleStr = "";
                        for(var i in data.result_data){
                            $scope.roleStr += ","+data.result_data[i].ROLE_ID;
                        }
                        $scope.roleStr = $scope.roleStr.substring(1);

                        //初始化提示信息框
                        angular.element(document).ready(function() {
                            $("[data-toggle='tooltip']").tooltip();
                        });

                    } else {
                        Window.alert(data.result_name);
                    }
                });
            }
            $scope.save = function(){
                var roleList = $(":checkbox[name='checkRole']:checked");
                var roleArr = [];
                for(var i = 0;i<roleList.length;i++){
                    roleArr.push(JSON.parse(roleList[i].value));
                }
                $http({
                    method : 'post',
                    url : BEWG_URL.srvUrl + 'user/saveUserRole.do',
                    traditional: true,
                    data : $.param({
                        "userId" : $scope.userId,
                        "roleArr": JSON.stringify(roleArr)
                    })
                }).success(function(data) {
                    if (data.success) {
                        Window.alert(data.result_name);
                        $scope.getAllRole();
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            }
            $scope.return = function(){
                $location.path("index/userList");
            }
            $scope.initData();
        }]);
});