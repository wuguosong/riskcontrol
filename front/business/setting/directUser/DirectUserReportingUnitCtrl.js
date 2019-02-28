define(['app', 'commons/service', 'ztree-core'], function (app) {
    app.register.controller('DirectUserReportingUnitCtrl', ['$http', '$scope', '$location', '$stateParams', 'Window',
        function ($http, $scope, $location, $stateParams, Window) {
            $scope.columnsName = null;
            $scope.user = {};
            $scope.investmentPersonMapped = {"nameField": "INVESTMENTPERSON_NAME", "valueField": "INVESTMENTPERSON_ID"};
            $scope.directPersonMapped = {"nameField": "DIRECTPERSON_NAME", "valueField": "DIRECTPERSON_ID"};
            //保存操作
            $scope.save = function () {
                var aMethed = null, postObj = null;
                if ($scope.user.ID != null && $scope.user.ID != '' && $scope.user.ID != "undefined") {
                    aMethed = 'fnd/Group/UpdateDirectUserReportingUnit';
                    postObj = $scope.httpData(aMethed, $scope.user);
                } else {
                    aMethed = 'fnd/Group/CreateDirectUserReportingUnit';
                    postObj = $scope.httpData(aMethed, $scope.user);
                }
                postObj.success(function (data) {
                        if (data.result_code === 'S') {
                            $location.path(PATH_URL_INDEX + "/DirectUserReportingUnitList");
                        } else {
                            Window.alert("申报单位不能重复!");
                        }
                    }
                )
            };

            // back to list
            $scope.backList = function () {
                $location.path(PATH_URL_INDEX + "/DirectUserReportingUnitList");
            }
            $scope.accessScope = function (node, func) {
                var scope = angular.element(document.querySelector(node)).scope();
                scope.$apply(func);
            }

            //取消操作
            $scope.cancel = function () {
                $location.path(PATH_URL_INDEX + "/DirectUserReportingUnitList");
            };
            //查询一个用户
            $scope.GetGroupUserReportingUnit = function (id) {
                var aMethed = 'fnd/Group/getDirectUserReportingUnitByID';
                $scope.httpData(aMethed, id).success(
                    function (data, status, headers, config) {
                        $scope.user = data.result_data;
                    }
                ).error(function (data, status, headers, config) {
                    Window.alert(status);
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
            $scope.setDirectiveParam = function (columnName) {
                $scope.columnsName = columnName;
            }
            $scope.setDirectiveRadioUserList = function (id, name) {
                var paramsVal = $scope.columnsName;
                if (paramsVal == "investmentPerson") {
                    $scope.user.INVESTMENTPERSON_NAME = name;//赋值保存到数据库
                    $scope.user.INVESTMENTPERSON_ID = id;
                    $("#investmentPersonName").val(name);
                    $("label[for='investmentPersonName']").remove();
                } else if (paramsVal == "directPerson") {
                    $scope.user.DIRECTPERSON_NAME = name;//赋值保存到数据库
                    $scope.user.DIRECTPERSON_ID = id;
                    $("#directPersonName").val(name);
                    $("label[for='directPersonName']").remove();
                }
            }
            var id = $stateParams.id;
            //定义窗口action
            var action = $stateParams.action; //getUrlParam('action');
            if (action == 'Update') {
                $scope.GetGroupUserReportingUnit(id);
            } else if (action == 'View') {
                $scope.GetGroupUserReportingUnit(id);
                $("#content-wrapper input").attr("disabled", true);
                $("select").attr("disabled", true);
                $("#savebtn").hide();
                //设置控件只读
            } else if (action == 'Create') {

            }
            var ztree, setting = {
                callback: {
                    onClick: function (event, treeId, treeNode) {
                        $scope.accessScope("#reportingUnitName", function (scope) {
                            scope.user.REPORTINGUNIT_NAME = treeNode.name;
                            scope.user.REPORTINGUNIT_ID = treeNode.id;
                            $("#reportingUnitName").val(name);
                            $("label[for='reportingUnitName']").remove();
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
                ztree = $.fn.zTree.init($("#treeID1"), setting);
                $scope.addTreeNode('');
                $scope.getSyncbusinessmodel('0');
            });
        }]);
});



