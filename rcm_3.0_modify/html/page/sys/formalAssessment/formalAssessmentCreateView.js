ctmApp.register.controller('formalAssessmentCreateView', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    // 获取参数
    $scope.id = $routeParams.id;

    // 数据绑定初始化
    $scope.title = "项目正式评审查看";

    // 初始化数据
    $scope.initData = function(){
        var  url = 'formalAssessmentInfoCreate/getProjectByID.do';
        $http({
            method:'post',
            url:srvUrl+url,
            data: $.param({"id":$scope.id})
        }).success(function(data){
            console.log(data.result_data.mongoData._id);
            $scope.pfr  = data.result_data.mongoData;
            $scope.pfrOracle  = data.result_data.oracleDate;

            debugger
            var smNameArr=[],pmNameArr=[]

            // 回显数据-业务类型
            var sm=$scope.pfr.apply.serviceType;
            if(null!=sm && sm.length>0){
                for(var j=0;j<sm.length;j++){
                    smNameArr.push(sm[j].VALUE);
                }
                $scope.pfr.apply.serviceType=smNameArr.join(",");
            }

            // 回显数据-投资模式
            var pm=$scope.pfr.apply.projectModel;
            if(null!=pm && pm.length>0){
                for(var j=0;j<pm.length;j++){
                    pmNameArr.push(pm[j].VALUE);
                }
                $scope.pfr.apply.projectModel=pmNameArr.join(",");
            }
        });
    }

    $scope.initData();
}]);