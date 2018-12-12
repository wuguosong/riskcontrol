ctmApp.register.controller('FormalBiddingInfoPreview', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        // 预览传来的参数
        $scope.formalPreview = $routeParams.formalPreview
        $scope.changeValue = 1;   // 标识切换的页面
        $scope.getDate = function(){
            var myDate = new Date();
            //获取当前年
            var year = myDate.getFullYear();
            //获取当前月
            var month = myDate.getMonth() + 1;
            //获取当前日
            var date = myDate.getDate();
            var h = myDate.getHours(); //获取当前小时数(0-23)
            var m = myDate.getMinutes(); //获取当前分钟数(0-59)
            var s = myDate.getSeconds();
            var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
            return now;
        }

        $scope.changeTab = function (flag) {
            var oldValue = $scope.changeValue;
            if(flag == 0){
                if (oldValue > 1){
                    $scope.changeValue = oldValue - 1;
                }
            } else {
                $scope.changeValue = oldValue + 1;
            }
        }

    }]
);