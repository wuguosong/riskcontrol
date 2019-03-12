ctmApp.register.controller('BulletinReviewDetailPreview', ['$http','$scope','$location', '$routeParams', '$filter', '$routeParams',
    function ($http,$scope,$location, $routeParams, $filter,$routeParams) {
        $scope.oldUrl = $routeParams.url;
        var routeParams = $routeParams.id.split("_");
        $scope.flag = $routeParams.flag;
        var queryParamId = routeParams[0];
        $scope.tabIndex = routeParams[1];
        $scope.initDefaultData = function(){
            $scope.initData();
        };
        $scope.initData = function(){
            var url = srvUrl + "bulletinReview/queryViewDefaultInfo.do";
            $http({
                method:'post',
                url: url,
                data: $.param({"businessId": queryParamId})
            }).success(function(result){
                var data = result.result_data;
                $scope.bulletinOracle = data.bulletinOracle;
                $scope.bulletin = data.bulletinMongo;
            });
        };
        $scope.save = function(){
            var params = {
                businessId: queryParamId,
                decisionOpinionList: $scope.selfOpinion
            };
            $http({
                method:'post',
                url:srvUrl+"meeting/saveReviewInfo.do",
                data: $.param({"data":JSON.stringify(params)})
            }).success(function(result){
                $.alert(result.result_name);
                $scope.initDefaultData();
            });
        };

        // 滑动切换时，上面的过程跟着切换
        $scope.changeStyle = function (num) {
            var tabId = ['decisionMatters', 'windControlAdvice', 'relevantAttachments'];
            angular.forEach(tabId, function (data, index, array) {
                if (index != num) {
                    angular.element("#"+data).removeClass('chose');
                } else {
                    angular.element("#"+data).addClass('chose');
                }
            });
        }

        $scope.initDefaultData();
    }]);
