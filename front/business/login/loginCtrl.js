define(['app', 'ui-router', 'ng-cookies'], function (app) {
    app
        .config(function ($httpProvider) {
            $httpProvider.defaults.headers.post = {"Content-Type": "application/x-www-form-urlencoded"};
        })
        .register.controller('loginCtrl', ['$http', '$scope', '$location', '$cookies',
        function ($http, $scope, $location, $cookies) {

            var srvUrl = "/rcm-rest";
            var init = [];

            $scope.myTxt = "你还没有点击提交!";

            $scope.credentials = {
                userID: '',
                password: ''
            };

            //登录服务端验班上
            $scope.doLogin = function () {

                //服务端验证登录是否
                var aUrl = srvUrl + "fnd/User/Create";
                $http.post(aUrl, $scope.user)
                    .success(
                        function (data, status, headers, config) {
                            //$scope.users = data.result_data;
                            alert(data.result_name);
                            self.location = "UserList.html";

                        }
                    ).error(function (data, status, headers, config) {
                    alert(status);

                });
            };

            //登录
            $scope.login = function () {
                var params = {};
                params.json = JSON.stringify($scope.credentials);
                console.log(params);
                console.log($.param({"json": JSON.stringify($scope.credentials)}));
                $http({
                    ContentType: "application/x-www-form-urlencoded",
                    method: 'POST',
                    url: srvUrl + "user/getAUser.do",
                    data: $.param({"json": JSON.stringify($scope.credentials)})
                }).success(function (data) {
                    console.log(data);
                    if (data.success) {
                        $cookies.put('credentials', JSON.stringify(data.result_data));

                        var Days = 1 / 24;
                        var exp = new Date();
                        exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
                        $cookies.put('loged', 'true', {'expires': exp.toGMTString()});
                        //进入首页
                        $location.path('/');
                    } else {
                        $.alert(data.result_name);
                    }
                });
            }

        }]);
});