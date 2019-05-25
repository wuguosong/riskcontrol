/**
 * 报表Controller
 */
ctmApp.register.controller('reportCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window', 'DirPipeSrv',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window, DirPipeSrv) {
        /*初始化*/
        $scope.fineBI = 'http://114.116.71.153:8081/FineBI/decision';
        $scope.fineBILogin = '/login/cross/domain';
        $scope.fineBILogout = '/logout/cross/domain';
        $scope.fineBIUserName = 'rcm';
        $scope.fineBIPassword = '5ce7c7f97ad1b44eb48bfe4a';
        /*ajax跨域单点登录*/
        $scope.loginApi = function () {
            jQuery.ajax({
                url: $scope.fineBI + $scope.fineBILogin + '?fine_username=' + $scope.fineBIUserName + '&fine_password=' + $scope.fineBIPassword + '&validity=-1',
                timeout: 5000,
                dataType: "jsonp",
                jsonp: "callback",
                success: function (data) {
                    if (data.errorCode) {
                        $.alert('登录失败，错误信息：' + data.errorMsg);
                    } else {
                        $.confirm('登录成功，是否跳转到报表登录？', function () {
                            $window.open($scope.fineBI);
                        });
                    }
                },
                error: function () {
                    alert("超时或服务器其他错误");
                },
                async:false
            });
        };
        /*注销登录*/
        $scope.logoutApi = function () {
            jQuery.ajax({
                url: $scope.fineBI + $scope.fineBILogout,
                dataType: "jsonp",
                jsonp: "callback",
                timeout: 5000,
                success: function (data) {
                    if (data.status === "success") {
                        $.confirm('注销成功，是否跳转到报表登录页面？', function () {
                            $window.open($scope.fineBI);
                        });
                    }
                },
                error: function () {
                    $.alert('注销失败！');
                }
            });
        };
        /*iframe登录*/
        $scope.iframeLogin = function () {
            $.ajax({
                url: $scope.fineBI + $scope.fineBILogin + '?fine_username=' + $scope.fineBIUserName + '&fine_password=' + $scope.fineBIPassword + '&validity=-1',
                timeout: 5000,
                dataType: "jsonp",
                jsonp: "callback",
                success: function (data) {
                    if (data.errorCode) {
                        $.alert('登录失败，错误信息：' + data.errorMsg);
                    } else {
                        var iframe = document.createElement("iframe");
                        iframe.id = 'reportIframe';
                        iframe.src = $scope.fineBI;
                        iframe.width = '100%';
                        iframe.height = '850px';
                        document.getElementById("iframeDiv").appendChild(iframe);
                    }
                },
                error: function () {
                },
                async:false
            });
        };
        $scope.iframeLogin();
    }
]);

