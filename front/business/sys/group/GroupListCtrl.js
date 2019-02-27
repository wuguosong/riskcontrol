define(['app', 'Service', 'ztree-core'], function (app) {
    app.register.controller('GroupListCtrl', ['$http', '$scope', '$location', '$stateParams', 'Window', function ($http, $scope, $location, $stateParams, Window) {
        $scope.paginationConf = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50],
            queryObj: {}
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
        //查看操作
        $scope.View = function (uuid) {
            $location.path(PATH_URL_INDEX + "/GroupAdd/View/" + uuid);
        };
        //新建操作
        $scope.Create = function (id) {
            $location.path(PATH_URL_INDEX + "/GroupAdd/Create/" + id);
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
                $location.path(PATH_URL_INDEX + "/GroupAdd/Update/" + uid);
            }
        }
        $scope.viewGroup = function () {
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
                Window.alert("未选择查看数据！");
                return false;
            } else if (num > 1) {
                Window.alert("只能选择其中一条数据查看！");
                return false;
            } else {
                // location.href = PATH_URL_INDEX + "/GroupAdd/View/" + uid;
                $location.path(PATH_URL_INDEX + "/GroupAdd/View/" + uid);

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
                var aMethed =  'fnd/Group/SelectCode';
                $scope.httpData(aMethed, uid).success(
                    function (data, status, headers, config) {
                        if (data.result_data == false) {
                            Window.alert('子目录未删除！');
                            return false;
                        } else {
                            Window.confirm('确认', '确定要删除吗?').result.then(function () {
                                var aMethed = 'fnd/Group/delectGroupByID';
                                $scope.httpData(aMethed, uid).success(
                                    function (data, status, headers, config) {
                                        $scope.ListAll();
                                        $scope.getOrgAll();
                                    }
                                ).error(function (data, status, headers, config) {
                                    Window.alert(status);
                                });
                            });
                        }
                    }
                ).error(function (data, status, headers, config) {
                    Window.alert(status);
                });

            }
        }
        //查询
        $scope.Select = function () {
            $scope.conf = {
                currentPage: $scope.paginationConf.currentPage,
                pageSize: $scope.paginationConf.itemsPerPage
            };
            var url = 'fnd/Group/Select';
            if (orgs != 0) {
                var cc = $scope.conf.queryObj = {};
                cc.ORGID = orgs;
            }
            $scope.httpData(url, $scope.conf).success(function (data) {
                // 变更分页的总数
                $scope.group = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            });
        };
        $scope.updateGroupState = function (state) {
            Window.confirm('确认', '确定要启用?').result.then(function () {
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
                    Window.alert("请选择其中一条或多条数据启用！");
                    return false;
                } else {
                    statevar = {"uuid": uid, "state": state};
                    var url = 'fnd/Group/updateGroupState';
                    $scope.httpData(url, statevar).success(function (data) {
                        $scope.getAll();
                    });
                    $scope.httpData(url, statevar).success(
                        function (data, status, headers, config) {
                            $scope.getAll();
                        }
                    ).error(function (data, status, headers, config) {
                        Window.alert(status);
                    });
                }
            });
        }
        var orgs = $stateParams.orgId;

        $scope.ListAll = function () {
            var url = 'fnd/Group/getAll';
            $scope.paginationConf.queryObj = $scope.queryObj;
            $scope.httpData(url, $scope.paginationConf).success(function (data) {
                // 变更分页的总数
                $scope.group = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            });
        };

        $scope.accessScope = function(node, func){
            var scope = angular.element(document.querySelector(node)).scope();
            scope.$apply(func);
        }

        // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage + queryObj.ORGID', $scope.ListAll);

        var ztree, setting = {
            callback: {
                onClick: function (event, treeId, treeNode) {
                    $scope.accessScope("#ORGID", function (scope) {
                        scope.queryObj = {};
                        scope.queryObj.ORGID = treeNode.id;
                    });
                },
                beforeExpand: function (treeId, treeNode) {
                    if (typeof(treeNode.children) == 'undefined') {
                        $scope.addTreeNode(treeNode);
                    }
                }
            }
        };
        $scope.addTreeNode = function (parentNode) {
            var pid = '';
            if (parentNode && parentNode.id) pid = parentNode.id;
            $scope.httpData('fnd/Group/getOrg', {parentId: pid}).success(function (data) {
                if (!data || data.result_code != 'S') return null;
                var nodeArray = data.result_data;
                if (nodeArray < 1) return null;
                for (var i = 0; i < nodeArray.length; i++) {
                    curNode = nodeArray[i];
                    var iconUrl = LIBS_PATH + 'javascripts/zTree/css/zTreeStyle/img/department.png';
                    if (curNode.cat && curNode.cat == 'Org') {
                        iconUrl = LIBS_PATH + 'javascripts/zTree/css/zTreeStyle/img/org.png';
                    }
                    curNode.icon = iconUrl;
                }
                if (pid == '') {//当前加载的是根节点
                    ztree.addNodes(null, nodeArray);
                    var rootNode = ztree.getNodes()[0];
                    $scope.addTreeNode(rootNode);
                    rootNode.open = true;
                    ztree.refresh();
                } else {
                    ztree.addNodes(parentNode, nodeArray, true);
                }
            });
        }
        angular.element(document).ready(function () {
            ztree = $.fn.zTree.init($("#treeID5"), setting);
            $scope.addTreeNode('');
        });
    }]);
});



