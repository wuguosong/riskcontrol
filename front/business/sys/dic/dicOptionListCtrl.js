define(['app', 'Service'], function (app) {
    app.register
        .controller('dicOptionListCtrl', ['$http', '$scope', '$location', '$stateParams', 'Window', '$uibModal',
            function ($http, $scope, $location, $stateParams, Window, $uibModal) {


                $scope.back = function () {
                    $location.path("index/dicList");
                };

                var srvUrl = "/rcm-rest";
                //查义所有的操作
                $scope.queryList = function () {

                    if ($scope.paginationConf.currentPage === 1) {
                        //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                        $scope.ListAll();
                    } else {
                        //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                        $scope.paginationConf.currentPage = 1;
                    }
                };
                $scope.fk_id = $stateParams.UUID;
                $scope.ListAll = function () {
                    /*var item_name=$("#ITEM_NAME").val();
                   var item_code=$("#ITEM_CODE").val();*/
                    var queryInfo = {
                        currentPage: $scope.paginationConf.currentPage,
                        pageSize: $scope.paginationConf.itemsPerPage,
                        queryObj: {
                            'ITEM_NAME': $scope.ITEM_NAME,
                            'ITEM_CODE': $scope.ITEM_CODE,
                            "FK_UUID": $scope.fk_id
                        }
                    };
                    $http({
                        method: 'post',
                        url: srvUrl + "dict/queryDictItemByDictTypeAndPage.do",
                        data: $.param({"page": JSON.stringify(queryInfo)})
                    }).success(function (data) {
                        if (data.success) {
                            $scope.item = data.result_data.list;
                            $scope.paginationConf.totalItems = data.result_data.totalItems;
                        } else {
                            Window.alert(data.result_name);
                        }
                    });
                };

                $scope.updateItem = function (id) {
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
                        $.alert("未选择编辑的数据！");
                        return false;
                    } else if (num > 1) {
                        $.alert("请选择一条数据编辑！");
                        return false;
                    } else {
                        $scope.CreateOrEditOption("Update", uid, $scope.fk_id);
                    }
                }
                $scope.deleteItem = function () {

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
                        $.alert("未选中删除数据！");
                        return false;
                    } else {
                        Window.confirm('标题', "显示内容").result.then(function (btn) {
                            $http({
                                method: 'post',
                                url: srvUrl + "dict/deleteDictItemByIds.do",
                                data: $.param({"uuids": uid})
                            }).success(function (data) {
                                if (data.success) {
                                    $scope.queryList();
                                } else {
                                    Window.alert(data.result_name);
                                }
                            });
                        }, function (btn) {
                            console.log("这里是取消的逻辑");
                        });
                    }

                };
                $scope.paginationConf = {
                    currentPage: 1,
                    itemsPerPage: 10,
                    perPageOptions: [10, 20, 30, 40, 50]
                };
                $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);

                //新建操作
                $scope.CreateOrEditOption = function (type, uuid, fk_id) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        backdrop: false,
                        controller: 'CreateOrEditOptionCtrl',
                        templateUrl: 'business/sys/dic/createOrEditOption.html',
                        windowClass: 'big-dialog',
                        resolve: {
                            type: function () {
                                return type;
                            },
                            uuid: function () {
                                return uuid;
                            },
                            fk_id: function () {
                                return fk_id;
                            }

                        }
                    });
                    modalInstance.result.then(function () {
                        console.log("aaaaaaaa");
                        $scope.queryList();
                    }, function () {
                        console.log("bbbbbbbbb");
                    });
                };
            }]);
    app.register
        .controller('CreateOrEditOptionCtrl', ['$http', '$scope', '$location', '$stateParams', 'Window', '$uibModalInstance', 'type', 'uuid', 'fk_id',
            function ($http, $scope, $location, $stateParams, Window, $uibModalInstance, type, uuid, fk_id) {
                console.log(fk_id);
                console.log(uuid);
                console.log(type);

                // 关闭弹出框按钮
                $scope.close = function () {
                    $uibModalInstance.dismiss();
                };

                //查询一个字典项
                $scope.getOptionByID = function (uuid) {
                    $http({
                        method: 'post',
                        url: SRV_URL + "dict/getDictItemById.do",
                        data: $.param({"uuid": uuid})
                    }).success(function (data) {
                        if (data.success) {
                            $scope.item = data.result_data;
                        }
                        else {
                            Window.alert(data.result_name);
                        }
                    });
                };

                var uid = 0;
                $scope.item = {};
                //初始化
                //定义窗口action
                if (type == 'Update') {
                    //初始化状态f
                    $scope.getOptionByID(uuid);
                    //编辑状态则初始化下拉列表内容
                } else if (type == 'View') {
                    $scope.getOptionByID(uuid);
                    $scope.hide = true;
                } else if (type == 'Create') {
                    uid = uuid;
                    //取默认值
                    $scope.item.ITEM_NAME = '';
                    $scope.item.ITEM_CODE = '';
                    $scope.item.IS_ENABLED = '1';
                    $scope.item.UUID = '';
                    $scope.item.FK_DICTIONARY_UUID = fk_id;
                    $scope.item.BUSINESS_TYPE = '';
                    $http({
                        method: 'post',
                        url: SRV_URL + "dict/getDictItemLastIndexByDictType.do",
                        data: $.param({"FK_UUID": fk_id})
                    }).success(function (data) {
                        if (data.success) {
                            $scope.item.CUST_NUMBER01 = data.result_data;
                        }
                    });
                }
                ;

                //保存操作
                $scope.save = function () {
                    console.log($scope.item);
                    if ($scope.item.CUST_NUMBER01 == null || $scope.item.CUST_NUMBER01 == "") {
                        Window.alert("序号必填!");
                        return false;
                    }
                    if ($scope.item.ITEM_NAME == null || $scope.item.ITEM_NAME == "") {
                        Window.alert("字典项名称必填!");
                        return false;
                    }
                    if ($scope.item.ITEM_CODE == null || $scope.item.ITEM_CODE == "") {
                        Window.alert("字典项编码必填!");
                        return false;
                    }
                    $http({
                        method: 'post',
                        url: SRV_URL + "dict/saveOrUpdateDictItem.do",
                        data: $.param({"json": JSON.stringify($scope.item)})
                    }).success(function (data) {
                        if (data.success) {
                            $uibModalInstance.close();
                        }
                        else {
                            Window.alert(data.result_name);
                        }
                    });
                };


            }]);
});