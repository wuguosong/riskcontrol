define(['app', 'commons/service'], function (app) {
    app.register.controller('DirectUserReportingUnitListCtrl', ['$http', '$scope', '$location', '$stateParams', 'Window',
        function ($http, $scope, $location, $stateParams, Window) {
            //查看操作
            $scope.View = function (uuid) {
                $location.path(PATH_URL_INDEX + "/DirectUserReportingUnit/View/" + uuid);
            };
            //新建操作
            $scope.Create = function (id) {
                $location.path(PATH_URL_INDEX + "/DirectUserReportingUnit/Create/" + id);
            };
            $scope.updateGroup = function () {
                var chk_list = document.getElementsByName("checkbox");
                var uid = "", num = 0;
                for (var i = 0; i < chk_list.length; i++) {
                    if (chk_list[i].checked) {
                        num++;
                        uid = uid + ',' + chk_list[i].value;
                    }
                }
                if (uid != '') {
                    uid = uid.substring(1, uid.length);
                }
                if (num == 0) {
                    Window.alert("未选中编辑数据！");
                }
                else if (num > 1) {
                    Window.alert("只能选择其中一条数据进行编辑！");
                    return false;
                } else {
                    $location.path(PATH_URL_INDEX + "/DirectUserReportingUnit/Update/" + uid);
                }
            }
            $scope.deleteGroup = function () {
                var chk_list = document.getElementsByName("checkbox");
                var uid = "", num = 0;
                for (var i = 0; i < chk_list.length; i++) {
                    if (chk_list[i].checked) {
                        num++;
                        uid = uid + ',' + chk_list[i].value;
                    }
                }
                if (uid != '') {
                    uid = uid.substring(1, uid.length);
                }
                if (num == 0) {
                    Window.alert("未选中删除的数据！");
                    return false;
                } else {
                    Window.confirm('确认', '确定要删除吗?').result.then(function(){
                        var aMethed =  'fnd/Group/delectDirectUserReportingUnitByID';
                        $scope.httpData(aMethed, uid).success(
                            function (data, status, headers, config) {
                                $scope.ListAll();
                            }
                        ).error(function (data, status, headers, config) {
                            Window.alert(status);
                        });
                    });
                }
            }

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50],
                queryObj:{

                }
            };

            $scope.ListAll = function () {
                var url = 'fnd/Group/getDirectUserReportingUnitAll';
                $scope.paginationConf.queryObj = $scope.queryObj;
                $scope.httpData(url, $scope.paginationConf).success(function (data) {
                    // 变更分页的总数
                    $scope.group = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };
            $scope.getSyncbusinessmodel = function (keys) {
                //初始化一级业务类型下拉列表数据
                var url = "common/commonMethod/selectsyncbusinessmodel";
                $scope.httpData(url, keys).success(function (data) {
                    if (data.result_code === 'S') {
                        $scope.Syncbusinessmodel = data.result_data;
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            }
            angular.element(document).ready(function () {
                $scope.getSyncbusinessmodel('0');
            });
            // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
        }]);
});
