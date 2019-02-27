define(['app', 'Service'], function (app) {
    app.register.controller('GroupUserRepoUnitListCtrl', ['$http', '$stateParams', '$scope', '$location', '$stateParams', '$filter', 'Window', function ($http, $stateParams, $scope, $location, $stateParams, $filter, Window) {
        //显示遮罩层
        $scope.show_mask = function () {
            $("#mask_").css("height", $(document).height());
            $("#mask_").css("line-height", $(document).height() + "px");
            $("#mask_").css("width", $(document).width());
            $("#mask_").show();
        }

        //隐藏遮罩层
        $scope.hide_mask = function () {
            $("#mask_").hide();
        }

        $scope.paginationConf = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50],
            queryObj: {}
        };
        $scope.queryRepoUnitByPage = function () {
            $scope.show_mask();
            $http({
                method: 'post',
                url: SRV_URL + "repoUnitUser/queryByPage.do",
                data: $.param({"page": JSON.stringify($scope.paginationConf)})
            }).success(function (result) {
                if (result.success) {
                    $scope.repoUnitList = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                } else {
                    Window.alert(result.result_name);
                }
                $scope.hide_mask();
            }).error(function (data, status, headers, config) {
                $scope.hide_mask();
            });
        };
        $scope.executeQueryRepoUnitByPage = function () {
            $scope.paginationConf.currentPage = 1;
            $scope.queryRepoUnitByPage();
        };
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRepoUnitByPage);

        $scope.getSelectInfo = function (elementName) {
            var checkboxs = document.getElementsByName(elementName);
            var array = new Array(), num = 0;
            for (var i = 0, length = checkboxs.length; i < length; i++) {
                if (checkboxs[i].checked) {
                    array[num] = checkboxs[i].value;
                    num++;
                }
            }
            return array;
        }

        $scope.createGroup = function () {
            /*var returnUrl = $filter('encodeURI')("#/GrouUserRepoUnitList", "VALUE");
             $location.path("/GrouUserRepoUnit/Create/0" + "/" + returnUrl);*/
            $location.path(PATH_URL_INDEX + "/GroupUserRepoUnit/Create/0" + "/" + 'GroupUserRepoUnitList');
        };

        $scope.updateGroup = function () {
            var array = $scope.getSelectInfo("checkbox");
            if (1 != array.length) {
                Window.alert("请选择一条信息！");
                return false;
            }
            /*var returnUrl = $filter('encodeURI')("#/GrouUserRepoUnitList", "VALUE");
             $location.path("/GrouUserRepoUnit/Update/" + array[0] + "/" + returnUrl);*/
            $location.path(PATH_URL_INDEX + "/GroupUserRepoUnit/Update/" + array[0] + "/" + 'GroupUserRepoUnitList');
        };

        $scope.deleteGroup = function () {
            var array = $scope.getSelectInfo("checkbox");
            if (1 != array.length) {
                Window.alert("请选择一条信息！");
                return false;
            }
            Window.confirm('确认', '确定要删除吗?').result.then(function () {
                $http({
                    method: 'post',
                    url: "repoUnitUser/deleteById.do",
                    data: $.param({"id": array[0]})
                }).success(function (result) {
                    if (result.success) {
                        $scope.queryRepoUnitByPage();
                    } else {
                        Window.alert(result.result_name);
                    }
                });
            });
        };
        //查看操作
        $scope.viewInfo = function (id) {
            $location.path(PATH_URL_INDEX + "/GroupUserRepoUnit/View/" + id + "/oldUrl");
        };
    }]);
});
