define(['app', 'Service'], function (app) {
    app
        .register.controller('preInfoListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('preInfoList');

            var tabIndex = $stateParams.tabIndex;

            //控制具体显示那个tab标签页  012
            $('#myTab li:eq('+tabIndex+') a').tab('show');
            $scope.tabIndex = tabIndex;

            //按钮控制器
            $scope.initData = function(){
                $scope.getPreList();
                $scope.getPreSubmitedList();
            }
            //查询投标评审基本信息列表--起草中
            $scope.getPreList = function(){
                if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
                    $scope.paginationConf.queryObj = {};
                }
                $scope.paginationConf.queryObj.needCreateBy = '1';
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllPre,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.preList = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                });
            }
            //查询投标评审基本信息列表--已提交
            $scope.getPreSubmitedList = function(){
                /*show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitPre,
                    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
                }).success(function(result){
                    $scope.preSubmitedList = result.result_data.list;
                    $scope.paginationConfes.totalItems = result.result_data.totalItems;
                   /* hide_Mask();*/
                }).error(function(data, status, headers, config){
                    /*hide_Mask();*/
                });
            }

            $scope.order=function(o,v){
                if(o=="time"){
                    $scope.orderby=v;
                    $scope.orderbystate=null;
                    if(v=="asc"){
                        $("#orderasc").addClass("cur");
                        $("#orderdesc").removeClass("cur");
                    }else{
                        $("#orderdesc").addClass("cur");
                        $("#orderasc").removeClass("cur");
                    }
                }else{
                    $scope.orderbystate=v;
                    $scope.orderby=null;
                    if(v=="asc"){
                        $("#orderascstate").addClass("cur");
                        $("#orderdescstate").removeClass("cur");
                    }else{
                        $("#orderdescstate").addClass("cur");
                        $("#orderascstate").removeClass("cur");
                    }
                }
            };
            // 新增操作
            $scope.createProject=function(){
                $location.path("/index/PreInfo/0/1");
            };

            // 修改操作
            $scope.updateProject=function(){
                var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要修改的数据！");
                    return false;
                }

                if(chkObjs.length > 1){
                    Window.alert("请只选择一条数据进行修改!");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);
                $location.path("/index/PreInfo/" + idsStr + "/2");
            };

            // 删除操作
            $scope.deleteProject = function () {
                var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要删除的数据！");
                    return false;
                }
                Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                    var idsStr = "";
                    for(var i = 0; i < chkObjs.length; i++){
                        if(i == chkObjs.length-1){
                            idsStr = idsStr + chkObjs[i].value;
                        } else {
                            idsStr = idsStr + chkObjs[i].value + ",";
                        }
                    }
                    $http({
                        method:'post',
                        url: BEWG_URL.DeletePre,
                        data: $.param({"ids":JSON.stringify(idsStr)})
                    }).success(function(data){
                        if(data.success){
                            alert("执行成功");
                            $scope.initData();
                        }else{
                            Window.alert(data);
                        }
                    });
                });
            };

            // 跳转详情页面
            $scope.toPreInfo = function (id, tabIndex) {
                $location.path("/index/PreInfoView/" + id + "/" + tabIndex);
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
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getPreList);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getPreSubmitedList);
        }]);
});