define(['app', 'Service', 'datepicher'], function (app) {
    app.register.controller('BulletinMattersAuditCtrl', ['$http', '$scope', '$location', '$stateParams', 'CommonService', 'Window',
        function ($http, $scope, $location, $stateParams, CommonService, Window) {
            $scope.tabIndex = $stateParams.tabIndex;
            $scope.initDefaultData = function () {
                var url = SRV_URL + "bulletinInfo/queryListDefaultInfo.do";
                $http({
                    method: 'post',
                    url: url
                }).success(function (result) {
                    var data = result.result_data;
                    $scope.tbsxType = data.tbsxType;
                });
            };
            // 配置分页基本参数
            $scope.paginationConfes = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.initDefaultData();
            //查询起草状态列表
            $scope.queryApplyList = function () {
                $http({
                    method: 'post',
                    url: SRV_URL + "bulletinAudit/queryWaitList.do",
                    data: $.param({"page": JSON.stringify($scope.paginationConf)})
                }).success(function (data) {
                    if (data.success) {
                        $scope.bulletins = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            };
            //查询已提交列表
            $scope.queryApplyedList = function () {
                $http({
                    method: 'post',
                    url: SRV_URL + "bulletinAudit/queryAuditedList.do",
                    data: $.param({"page": JSON.stringify($scope.paginationConfes)})
                }).success(function (data) {
                    if (data.success) {
                        $scope.applyedBulletins = data.result_data.list;
                        $scope.paginationConfes.totalItems = data.result_data.totalItems;
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryApplyList);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.queryApplyedList);

            $scope.update = function () {
                var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
                if (chkObjs.length == 0 || chkObjs.length > 1) {
                    Window.alert("请选择一条数据编辑！");
                    return false;
                }
                var businessId = $(chkObjs[0]).val();
                $location.path("/BulletinMattersDetail/" + businessId);
            };

            $scope.deleteBatch = function () {
                var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
                if (chkObjs.length == 0) {
                    Window.alert("请选择要删除的数据！");
                    return false;
                }
                var idsStr = "";
                for (var i = 0; i < chkObjs.length; i++) {
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);
                $http({
                    method: 'post',
                    url: SRV_URL + "bulletinInfo/deleteByIds.do",
                    data: $.param({"ids": idsStr})
                }).success(function (data) {
                    if (data.success) {
                        $scope.queryApplyList();
                    }
                    Window.alert(data.result_name);
                });
            }

            // 查看页面
            $scope.toView = function (id) {
                $location.path(PATH_URL_INDEX + "/BulletinMattersAuditView/" + id + "/url");
            }
        }]);
});


