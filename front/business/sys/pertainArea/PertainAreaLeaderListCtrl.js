define(['app', 'Service'], function (app) {
    app.register.controller('PertainAreaLeaderListCtrl', ['$http', '$stateParams', '$scope', '$location', '$stateParams', '$filter', 'Window', function ($http, $stateParams, $scope, $location, $stateParams, $filter, Window) {
        $scope.initData = function () {
            $scope.listAll();
        };

        $scope.add = function () {
            $location.path(PATH_URL_INDEX + "/PertainAreaDetail/create/0/listUrl");
        }

        // 配置分页基本参数
        $scope.paginationConf = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50]
        };

        $scope.listAll = function () {
            $http({
                method: 'post',
                url: SRV_URL + "pertainArea/queryList.do",
                data: $.param({"page": JSON.stringify($scope.paginationConf)})
            }).success(function (result) {
                if (result.success) {
                    $scope.leaderList = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                } else {
                    Window.alert(result.result_name);
                }
            });
        }

        $scope.update = function () {

            if ($("input[type=checkbox][name=checkbox]:checked").length > 1) {
                Window.alert("只能选择一条数据！");
                return false;
            }
            if ($("input[type=checkbox][name=checkbox]:checked").length == 0) {
                Window.alert("请选择要修改的数据！");
                return false;
            }
            var id = $("input[type=checkbox][name=checkbox]:checked").val();
            /*var url = $filter("encodeURI")("#/pertainAreaLeaderList");
             $location.path("/pertainAreaDetail/update/"+id+"/"+url);*/
            $location.path(PATH_URL_INDEX + "/PertainAreaDetail/update/" + id + "/url");
        };

        $scope.delete = function () {
            if ($("input[type=checkbox][name=checkbox]:checked").length > 1) {
                Window.alert("只能选择一条数据！");
                return false;
            }
            if ($("input[type=checkbox][name=checkbox]:checked").length == 0) {
                Window.alert("请选择要删除的数据！");
                return false;
            }
            var userId = $("input[type=checkbox][name=checkbox]:checked").val();
            Window.confirm('确认', '确认要删除吗?').result.then(function () {
                $http({
                    method: 'post',
                    url: SRV_URL + "pertainArea/deleteByUserId.do",
                    data: $.param({"userId": userId})
                }).success(function (result) {
                    if (result.success) {
                        Window.alert(result.result_name);
                        $scope.initData();
                    } else {
                        Window.alert(result.result_name);
                    }
                });
            });
        };

        $scope.initData();
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.listAll);
    }]);
});
