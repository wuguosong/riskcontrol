define(['app', 'ui-router',], function (app) {
    app
        .register.controller('indexCtrl', ['$http', '$scope', '$location', '$cookies', '$rootScope',
        function ($http, $scope, $location, $cookies, $rootScope) {
            console.log("这是菜单栏");
            var srvUrl = "/rcm-rest";
            //服务端地址信息
            $scope.srvInfo = {
                srvUrl: srvUrl
            };
            //登录信息
            $scope.credentials = $.parseJSON($cookies.get('credentials'));
            $rootScope.credentials = $.parseJSON($cookies.get('credentials'));
            //获取服务端的数据
            $scope.httpData=function(pMethod,pData){
                if(pData == null){
                    pData = "";
                }
                var aUrl=$scope.srvInfo.srvUrl+pMethod;
                //加入授权信息
                // var aheaders = {authorization: "Basic " + btoa($scope.credentials.userID + ":" + $scope.credentials.password)};
                //请求参数
                var req = {
                    method: "post",
                    url: aUrl,
                    data:pData,
                    withCredentials:true
                };
                return $http(req);
            }

            $scope.sysFuncListing=function(){
                var  url = 'common/commonMethod/getSysFuncList';
                $scope.httpData(url,$scope.credentials.UUID).success(function(data){
                    console.log(data);
                        if(data.result_code == "S") {
                            $scope.func = data.result_data;
                            var funclist = $scope.func;
                            for (var i = 0; i < funclist.length; i++) {
                                var subFuncList = funclist[i].subFunc;
                                for (var k = 0; k < subFuncList.length; k++) {
                                    if ($scope.globalURLID == subFuncList[k].URL) {
                                        subFuncList[k].isFuncTF = true;
                                    } else {
                                        subFuncList[k].isFuncTF = false;
                                    }

                                    //三层菜单
                                    if(subFuncList[k].subFunc != null){
                                        var subsubFuncList = subFuncList[k].subFunc;
                                        for (var j = 0; j < subsubFuncList.length; j++) {
                                            if ($scope.globalURLID == subsubFuncList[j].URL) {
                                                subsubFuncList[j].isFuncTF = true;
                                            } else {
                                                subsubFuncList[j].isFuncTF = false;
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }
                ).error(function (data, status, headers, config) {
                    $.alert("登录已失效，请重新登录");
                    if(srvUrl == 'http://riskcontrol.bewg.net.cn/rcm-rest/'){
                        window.location.href="http://sso.bewg.net.cn";
                    }else{
                        window.location.href="signin.html";
                    }

                });
            }
            $scope.sysFuncListing();
        }]);
});