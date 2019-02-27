define(['app', 'Service'], function (app) {
    app
        .register.controller('dicListCtrl', ['$http', '$scope', '$location', '$uibModal', 'Window',
        function ($http, $scope, $location, $uibModal, Window) {

            $scope.toDicOptionList = function (uuid) {
                $location.path("index/dicOptionList/" + uuid);
            };

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
            var srvUrl = "/rcm-rest";
            $scope.ListAll = function () {
                // var dictionary_code=$("#DICTIONARY_CODE").val();
                // var dictionary_name=$("#DICTIONARY_NAME").val();
                var queryInfo = {
                    currentPage: $scope.paginationConf.currentPage,
                    pageSize: $scope.paginationConf.itemsPerPage,
                    queryObj: {'DICTIONARY_CODE': $scope.DICTIONARY_CODE, 'DICTIONARY_NAME': $scope.DICTIONARY_NAME}
                };
                $http({
                    method: 'post',
                    url: srvUrl + "dict/queryDictTypeByPage.do",
                    data: $.param({"page": JSON.stringify(queryInfo)})
                }).success(function (data) {
                    console.log(data);
                    if (data.success) {
                        $scope.dictionary = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            };

            //新建操作
            $scope.CreateOrEdit = function (type, uuid) {
                var modalInstance = $uibModal.open({
                    animation: true,
                    backdrop: false,
                    controller: 'CreateOrEditCtrl',
                    templateUrl: 'business/sys/dic/createOrEditDic.html',
                    windowClass: 'big-dialog',
                    resolve: {
                        type: function () {
                            return type;
                        },
                        uuid: function () {
                            return uuid;
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

            $scope.updateDictionary = function () {
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
                    Window.alert("未选择编辑的数据！");
                    return false;
                } else if (num > 1) {
                    Window.alert("请选择一条数据编辑！");
                    return false;
                } else {
                    $scope.CreateOrEdit("Update", uid);
                }
            };

            $scope.deleteDictionary = function () {
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
                    Window.alert("未选择删除的数据！");
                    return false;
                } else {
                    $http({
                        method: 'post',
                        url: srvUrl + "dict/queryDictItemByDictTypeIds.do",
                        data: $.param({"uuids": uid})
                    }).success(function (data) {
                        if (data.success) {
                            $.confirm("确定要删除？", function () {
                                $http({
                                    method: 'post',
                                    url: srvUrl + "dict/deleteDictTypeByIds.do",
                                    data: $.param({"uuids": uid})
                                }).success(function (data) {
                                    if (data.success) {
                                        $scope.queryList();
                                    } else {
                                        $.alert(data.result_name);
                                    }
                                });
                            });
                        } else {
                            Window.alert(data.result_name);
                        }
                    });
                }
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);

        }]);
    app
        .register.controller('CreateOrEditCtrl', ['$http', '$scope', '$location', '$uibModal', '$uibModalInstance', 'BEWG_URL', 'type', 'uuid',
        function ($http, $scope, $location, $uibModal, $uibModalInstance, BEWG_URL, type, uuid) {
            var uid = 0;
            var srvUrl = "/rcm-rest";
            //回显数据
            $scope.getDictionaryByID = function (uuid) {
                $http({
                    method: 'post',
                    url: srvUrl + "dict/getDictTypeById.do",
                    data: $.param({"uuid": uuid})
                }).success(function (data) {
                    if (data.success) {
                        $scope.dictionary = data.result_data;
                    }
                    else {
                        $.alert(data.result_name);
                    }
                });
            };
            //初始化
            //定义窗口action
            if (type == 'Update') {
                //初始化状态f
                $scope.getDictionaryByID(uuid);
                //编辑状态则初始化下拉列表内容
            } else if (type == 'View') {
                $scope.getDictionaryByID(uuid);
                $scope.hide = true;
                console.log($scope.hide);

            } else if (type == 'Create') {
                uid = uuid;
                //取默认值
                $scope.dictionary.DICTIONARY_NAME = '';
                $scope.dictionary.DICTIONARY_CODE = '';
                $scope.dictionary.DICTIONARY_DESC = '';
                $scope.dictionary.UUID = '';
            }
            ;

            // 关闭弹出框按钮
            $scope.close = function () {
                $uibModalInstance.dismiss();
            };


            //保存操作
            $scope.save = function () {
                if ($scope.dictionary.DICTIONARY_NAME == null || $scope.dictionary.DICTIONARY_NAME == "") {
                    Window.alert("字典名称必填!");
                    return false;
                }
                if ($scope.dictionary.DICTIONARY_CODE == null || $scope.dictionary.DICTIONARY_CODE == "") {
                    Window.alert("字典编码必填!");
                    return false;
                }
                $http({
                    method: 'post',
                    url: BEWG_URL.SaveOrUpdateDict,
                    data: $.param({"json": JSON.stringify($scope.dictionary)})
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