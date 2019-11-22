// 列表页面
ctmApp.register.controller('announceListCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        $scope.announceList = {};
        //清空查询条件
        $scope.clearQueryObj = function () {
            $scope.pageList();
        };
        // 创建查询条件
        $scope.crateQueryObj = function () {
            $scope.announceManagerConfiguration.queryObj = {};
        };
        // 分页查询
        $scope.pageList = function () {
            $scope.crateQueryObj();
            $http({
                method: 'post',
                url: srvUrl + "announce/pageList.do",
                data: $.param({
                    "page": JSON.stringify($scope.announceManagerConfiguration)
                })
                , async: false
            }).success(function (data) {
                if (!isEmpty(data)) {
                    if ('S' == data['result_code']) {
                        $scope.announceManagerConfiguration.totalItems = data['result_data'].totalItems;
                        $scope.announceList = data['result_data'].list;
                    } else {
                        $.alert(data['result_name']);
                    }
                }
            });
        };
        // 初始化分页条件
        $scope.announceManagerConfiguration = {};
        $scope.announceManagerConfiguration.queryObj = {};
        $scope.announceManagerConfiguration.itemsPerPage = 10;
        $scope.announceManagerConfiguration.perPageOptions = [10, 20, 30, 40, 50];
        // 分页监听
        $scope.$watch('announceManagerConfiguration.currentPage + announceManagerConfiguration.itemsPerPage', $scope.pageList);
        $scope.pageList();
        // 删除
        $scope.delete = function (id) {
            $.confirm('确认删除该条记录？', function () {
                $.ajax({
                    url: srvUrl + 'announce/delete.do',
                    type: "POST",
                    dataType: "json",
                    data: {id: id},
                    async: false,
                    success: function (data) {
                        if (!isEmpty(data)) {
                            if ('S' == data['result_code']) {
                                $scope.pageList();
                                $.alert('删除成功！');
                            } else {
                                $.alert(data['result_name']);
                            }
                        }
                    }
                });
            });
        };
        // 状态更新
        $scope.updateStatus = function (id, status) {
            $.ajax({
                url: srvUrl + 'announce/updateStatus.do',
                type: "POST",
                dataType: "json",
                data: {id: id, status: status},
                async: false,
                success: function (data) {
                    if (!isEmpty(data)) {
                        if ('S' == data['result_code']) {
                            if ('0' == status) {
                                $.alert('停止成功！');
                            } else {
                                $.alert('发布成功！');
                            }
                            $scope.pageList();
                        } else {
                            $.alert(data['result_name']);
                        }
                    }
                }
            });
        };
    }]);
// 查看页面
ctmApp.register.controller('announceViewCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        $scope.announceList = {};
        // //清空查询条件
        // $scope.clearQueryObj = function () {
        //     $scope.pageList();
        // };
        // // 创建查询条件
        // $scope.crateQueryObj = function () {
        //     $scope.announceManagerConfiguration.queryObj.status = '1';
        // };
        // // 分页查询
        // $scope.pageList = function () {
        //     $scope.crateQueryObj();
        //     $http({
        //         method: 'post',
        //         url: srvUrl + "announce/pageList.do",
        //         data: $.param({
        //             "page": JSON.stringify($scope.announceManagerConfiguration)
        //         })
        //         , async: false
        //     }).success(function (data) {
        //         if (!isEmpty(data)) {
        //             if ('S' == data['result_code']) {
        //                 $scope.announceManagerConfiguration.totalItems = data['result_data'].totalItems;
        //                 $scope.announceList = data['result_data'].list;
        //             } else {
        //                 $.alert(data['result_name']);
        //             }
        //         }
        //     });
        // };
        // // 初始化分页条件
        // $scope.announceManagerConfiguration = {};
        // $scope.announceManagerConfiguration.queryObj = {};
        // $scope.announceManagerConfiguration.itemsPerPage = 1;
        // $scope.announceManagerConfiguration.perPageOptions = [1, 2, 3, 4, 5];
        // // 分页监听
        // $scope.$watch('announceManagerConfiguration.currentPage + announceManagerConfiguration.itemsPerPage', $scope.pageList);
        // $scope.pageList();
        // 列表查询
        $scope.findList = function () {
            $.ajax({
                url: srvUrl + 'announce/findList.do',
                type: "POST",
                dataType: "json",
                data: {params: JSON.stringify({status: '1'})},
                async: false,
                success: function (data) {
                    if (!isEmpty(data)) {
                        if ('S' == data['result_code']) {
                            $scope.announceList = data['result_data'];
                        } else {
                            $.alert(data['result_name']);
                        }
                    }
                }
            });
        };
        $scope.findList();
    }]);
// 新增或者编辑页面
ctmApp.register.controller('announceEditCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        var id = $filter('decodeURI')($routeParams.id);
        $scope.announceId = id;
        $scope.ruleForm = {};
        $scope.findOne = function (id) {
            $.ajax({
                url: srvUrl + 'announce/findOne.do',
                type: "POST",
                dataType: "json",
                data: {"id": id},
                async: true,
                success: function (data) {
                    if (!isEmpty(data)) {
                        if ('S' == data['result_code']) {
                            $scope.ruleForm = data['result_data'];
                            $scope.announceId = $scope.ruleForm.announceId;
                        } else {
                            $.alert(data['result_name']);
                        }
                    }
                }
            });
        };
        $scope.findOne(id);
        // 保存
        $scope.saveOrUpdate = function () {
            $.ajax({
                url: srvUrl + 'announce/saveOrUpdate.do',
                type: "POST",
                dataType: "json",
                data: $scope.ruleForm,
                async: false,
                success: function (data) {
                    if (!isEmpty(data)) {
                        if ('S' == data['result_code']) {
                            $scope.ruleForm = data['result_data'];
                            $scope.announceId = $scope.ruleForm.announceId;
                            $.alert('保存成功！');
                        } else {
                            $.alert(data['result_name']);
                        }
                    }
                }
            });
        };
        // 状态更新
        $scope.updateStatus = function (id, status) {
            $.ajax({
                url: srvUrl + 'announce/updateStatus.do',
                type: "POST",
                dataType: "json",
                data: {id: id, status: status},
                async: false,
                success: function (data) {
                    if (!isEmpty(data)) {
                        if ('S' == data['result_code']) {
                            if ('0' == status) {
                                $.alert('停止成功！');
                            } else {
                                $.alert('发布成功！');
                            }
                            $scope.ruleForm = data['result_data'];
                            $scope.announceId = $scope.ruleForm.announceId;
                        } else {
                            $.alert(data['result_name']);
                        }
                    }
                }
            });
        };
    }]);
