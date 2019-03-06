define(['app', 'Service'], function (app) {
    app
        .register.controller('bulletinMatterListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('bulletinMatterList');

            $scope.tabIndex = $stateParams.tabIndex;


            $scope.initDefaultData = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectBulletinInfo
                }).success(function(result){
                    var data = result.result_data;
                    $scope.tbsxType = data.tbsxType;
                });
            };
            $scope.initDefaultData();
            //查询起草状态列表
            $scope.queryApplyList=function(){
                $http({
                    method:'post',
                    url:BEWG_URL.SelectAllBulletin,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    if(data.success){
                        $scope.bulletins = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            };
            //查询已提交列表
            $scope.queryApplyedList=function(){
               /* show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitBulletin,
                    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
                }).success(function(data){
                    if(data.success){
                        $scope.applyedBulletins = data.result_data.list;
                        $scope.paginationConfes.totalItems = data.result_data.totalItems;
                    }else{
                        Window.alert(data.result_name);
                    }
                    /*hide_Mask();*/
                }).error(function(data, status, headers, config){
                    /*hide_Mask();*/
                });
            };

            $scope.add = function (){
                $location.path("/index/BulletinMatterInfo/0");
            };

            $scope.update = function(){
                var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
                if(chkObjs.length == 0 || chkObjs.length > 1){
                    Window.alert("请选择一条数据编辑！");
                    return false;
                }
                var businessId = $(chkObjs[0]).val();
                $location.path("/index/BulletinMatterInfo/"+businessId);
            };

            $scope.deleteBatch = function(){
                var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要删除的数据！");
                    return false;
                }
                Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn)  {
                    var idsStr = "";
                    for(var i = 0; i < chkObjs.length; i++){
                        idsStr = idsStr + chkObjs[i].value + ",";
                    }
                    idsStr = idsStr.substring(0, idsStr.length - 1);
                    $http({
                        method:'post',
                        url: BEWG_URL.DeleteBulletin,
                        data: $.param({"ids": idsStr})
                    }).success(function(data){
                        if(data.success){
                            $scope.queryApplyList();
                        }
                        Window.alert(data.result_name);
                    });
                });
            }

            $scope.startBatchFlow = function(){
                var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要提交的数据！");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);
                /*show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.StartBatchFlow,
                    data: $.param({"ids": idsStr})
                }).success(function(data){
                    /*hide_Mask();*/
                    if(data.success){
                        $scope.queryApplyList();
                        $scope.queryApplyedList();
                    }
                    Window.alert(data.result_name);
                });
            }

            // 跳转详情页面
            $scope.toBulletinInfo = function (id, tabIndex) {
                $location.path("/index/BulletinMatterInfoView/" + id + "/" + tabIndex);
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.paginationConfes = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
        }]);
});