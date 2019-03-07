/**
 * Created by gaohe on 2016/06/06.
 */
define(['app', 'ztree-core', 'Service'], function (app) {
    var srvUrl = "/rcm-rest";
    app
    /*使输入的数字自动加上千位符*/
        .directive('toChange', ['$parse',
            function ($parse) {
                return {
                    link: function (scope, element, attrs, ctrl) {
                        //控制输入框只能输入数字和小数点
                        function limit() {
                            var limitV = element[0].value;
                            limitV = limitV.replace(/[^0-9.]/g, "");
                            element[0].value = limitV;
                            $parse(attrs['ngModel']).assign(scope, limitV);
                            format();
                        }

                        //对输入数字的整数部分插入千位分隔符
                        function format() {
                            var formatV = element[0].value;
                            var array = new Array();
                            array = formatV.split(".");
                            var re = /(-?\d+)(\d{3})/;
                            while (re.test(array[0])) {
                                array[0] = array[0].replace(re, "$1,$2")
                            }
                            var returnV = array[0];
                            for (var i = 1; i < array.length; i++) {
                                returnV += "." + array[i];
                            }
                            element[0].value = returnV;
                            $parse(attrs['ngModel']).assign(scope, formatV);
                        }

                        scope.$watch(attrs.ngModel, function () {
                            limit();
                        })
                    }
                };
            }])
        /**
         * name: tm.pagination
         * Version: 1.0.0 beta
         */
        .directive('tmPagination', [function () {
            return {
                restrict: 'EA',
                template: '<div class="page-list">' +
                '<ul class="pagination" ng-show="conf.totalItems > 0">' +
                '<li ng-class="{disabled: conf.currentPage == 1}" ng-click="prevPage()"><span>&laquo;</span></li>' +
                '<li ng-repeat="item in pageList track by $index" ng-class="{active: item == conf.currentPage, separate: item == \'...\'}" ' +
                'ng-click="changeCurrentPage(item)">' +
                '<span>{{ item }}</span>' +
                '</li>' +
                '<li ng-class="{disabled: conf.currentPage == conf.numberOfPages}" ng-click="nextPage()"><span>&raquo;</span></li>' +
                '</ul>' +
                '<div class="page-total" ng-show="conf.totalItems > 0">' +
                '每页<select ng-model="conf.itemsPerPage" ng-options="option for option in conf.perPageOptions " ng-change="changeItemsPerPage()"></select>' +
                '/共<strong>{{ conf.totalItems }}</strong>条 ' +
                '跳转至<input type="text" ng-model="jumpPageNum" ng-keyup="jumpPageKeyUp($event)"/>' +
                '</div>' +
                '<div class="no-items" ng-show="conf.totalItems <= 0">暂无数据</div>' +
                '</div>',
                replace: true,
                scope: {
                    conf: '='
                },
                link: function (scope, element, attrs) {

                    var conf = scope.conf;

                    // 默认分页长度
                    var defaultPagesLength = 9;

                    // 默认分页选项可调整每页显示的条数
                    var defaultPerPageOptions = [10, 20, 30, 40];

                    // 默认每页的个数
                    var defaultPerPage = 10;

                    // 获取分页长度
                    if (conf.pagesLength) {
                        // 判断一下分页长度
                        conf.pagesLength = parseInt(conf.pagesLength, 10);

                        if (!conf.pagesLength) {
                            conf.pagesLength = defaultPagesLength;
                        }

                        // 分页长度必须为奇数，如果传偶数时，自动处理
                        if (conf.pagesLength % 2 === 0) {
                            conf.pagesLength += 1;
                        }

                    } else {
                        conf.pagesLength = defaultPagesLength
                    }

                    // 分页选项可调整每页显示的条数
                    if (!conf.perPageOptions) {
                        conf.perPageOptions = defaultPagesLength;
                    }

                    // pageList数组
                    function getPagination(newValue, oldValue) {

                        // conf.currentPage
                        if (conf.currentPage) {
                            conf.currentPage = parseInt(scope.conf.currentPage, 10);
                        }

                        if (!conf.currentPage) {
                            conf.currentPage = 1;
                        }

                        // conf.totalItems
                        if (conf.totalItems) {
                            conf.totalItems = parseInt(conf.totalItems, 10);
                        }

                        // conf.totalItems
                        if (!conf.totalItems) {
                            conf.totalItems = 0;
                            return;
                        }

                        // conf.itemsPerPage
                        if (conf.itemsPerPage) {
                            conf.itemsPerPage = parseInt(conf.itemsPerPage, 10);
                        }
                        if (!conf.itemsPerPage) {
                            conf.itemsPerPage = defaultPerPage;
                        }

                        // numberOfPages
                        conf.numberOfPages = Math.ceil(conf.totalItems / conf.itemsPerPage);

                        // 如果分页总数>0，并且当前页大于分页总数
                        if (scope.conf.numberOfPages > 0 && scope.conf.currentPage > scope.conf.numberOfPages) {
                            scope.conf.currentPage = scope.conf.numberOfPages;
                        }

                        // 如果itemsPerPage在不在perPageOptions数组中，就把itemsPerPage加入这个数组中
                        var perPageOptionsLength = scope.conf.perPageOptions.length;

                        // 定义状态
                        var perPageOptionsStatus;
                        for (var i = 0; i < perPageOptionsLength; i++) {
                            if (conf.perPageOptions[i] == conf.itemsPerPage) {
                                perPageOptionsStatus = true;
                            }
                        }
                        // 如果itemsPerPage在不在perPageOptions数组中，就把itemsPerPage加入这个数组中
                        if (!perPageOptionsStatus) {
                            conf.perPageOptions.push(conf.itemsPerPage);
                        }

                        // 对选项进行sort
                        conf.perPageOptions.sort(function (a, b) {
                            return a - b
                        });


                        // 页码相关
                        scope.pageList = [];
                        if (conf.numberOfPages <= conf.pagesLength) {
                            // 判断总页数如果小于等于分页的长度，若小于则直接显示
                            for (i = 1; i <= conf.numberOfPages; i++) {
                                scope.pageList.push(i);
                            }
                        } else {
                            // 总页数大于分页长度（此时分为三种情况：1.左边没有...2.右边没有...3.左右都有...）
                            // 计算中心偏移量
                            var offset = (conf.pagesLength - 1) / 2;
                            if (conf.currentPage <= offset) {
                                // 左边没有...
                                for (i = 1; i <= offset + 1; i++) {
                                    scope.pageList.push(i);
                                }
                                scope.pageList.push('...');
                                scope.pageList.push(conf.numberOfPages);
                            } else if (conf.currentPage > conf.numberOfPages - offset) {
                                scope.pageList.push(1);
                                scope.pageList.push('...');
                                for (i = offset + 1; i >= 1; i--) {
                                    scope.pageList.push(conf.numberOfPages - i);
                                }
                                scope.pageList.push(conf.numberOfPages);
                            } else {
                                // 最后一种情况，两边都有...
                                scope.pageList.push(1);
                                scope.pageList.push('...');

                                for (i = Math.ceil(offset / 2); i >= 1; i--) {
                                    scope.pageList.push(conf.currentPage - i);
                                }
                                scope.pageList.push(conf.currentPage);
                                for (i = 1; i <= offset / 2; i++) {
                                    scope.pageList.push(conf.currentPage + i);
                                }

                                scope.pageList.push('...');
                                scope.pageList.push(conf.numberOfPages);
                            }
                        }

                        scope.$parent.conf = conf;
                    }

                    // prevPage
                    scope.prevPage = function () {
                        if (conf.currentPage > 1) {
                            conf.currentPage -= 1;
                        }
                    };

                    // nextPage
                    scope.nextPage = function () {
                        if (conf.currentPage < conf.numberOfPages) {
                            conf.currentPage += 1;
                        }
                    };

                    // 变更当前页
                    scope.changeCurrentPage = function (item) {

                        if (item == '...') {
                            return;
                        } else {
                            conf.currentPage = item;
                            getPagination();
                            // conf.onChange()函数
                            if (conf.onChange) {
                                conf.onChange();
                            }
                        }
                    };

                    // 修改每页展示的条数
                    scope.changeItemsPerPage = function () {

                        // 一发展示条数变更，当前页将重置为1
                        conf.currentPage = 1;

                        getPagination();
                        // conf.onChange()函数
                        if (conf.onChange) {
                            conf.onChange();
                        }
                    };

                    // 跳转页
                    scope.jumpToPage = function () {
                        num = scope.jumpPageNum;
                        if (num.match(/\d+/)) {
                            num = parseInt(num, 10);

                            if (num && num != conf.currentPage) {
                                if (num > conf.numberOfPages) {
                                    num = conf.numberOfPages;
                                }

                                // 跳转
                                conf.currentPage = num;
                                getPagination();
                                // conf.onChange()函数
                                if (conf.onChange) {
                                    conf.onChange();
                                }
                                scope.jumpPageNum = '';
                            }
                        }

                    };

                    scope.jumpPageKeyUp = function (e) {
                        var keycode = window.event ? e.keyCode : e.which;

                        if (keycode == 13) {
                            scope.jumpToPage();
                        }
                    }

                    scope.$watch('conf.totalItems', function (value, oldValue) {

                        // 在无值或值相等的时候，去执行onChange事件
                        if (!value || value == oldValue) {

                            if (conf.onChange) {
                                conf.onChange();
                            }
                        }
                        getPagination();
                    })

                }
            };
        }])
        // 用户多选列表
        .directive('directUserMultiSelect', ['Window', 'UserSelectDialog', function (Window, UserSelectDialog) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directUserMultiSelect.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    //查询参数
                    queryParams: "=",
                    isEditable: "=?bind",
                    //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
                    checkedUsers: "=",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    callback: "="
                },
                controller: function ($scope, $http, $element, $timeout) {
                    $scope.int = 0;
                    $scope.openSelectedUserDialog = function () {
                        if ($scope.int === 0) {
                            console.log("b");
                            UserSelectDialog.multi('标题', "显示内容", $scope.checkedUsers, '', $scope.mappedKeyValue).result.then(function (data) {
                                console.log(data);
                            }, function (btn) {
                                console.log("这里是取消的逻辑");
                            });
                        }

                    };

                    if ($scope.mappedKeyValue == null) {
                        $scope.mappedKeyValue = {nameField: 'NAME', valueField: 'VALUE'};
                    }
                    if ($scope.checkedUsers == null) {
                        $scope.checkedUsers = [];
                    }
                    $scope.initDefaultData = function () {
                        if ($scope.title == null) {
                            $scope.title = "人员选择";
                        }
                        if ($scope.isEditable == null || ($scope.isEditable != "true" && $scope.isEditable != "false")) {
                            $scope.isEditable = "true";
                        }
                    };
                    $scope.initDefaultData();
                    $scope.removeSelectedUser = function (user) {
                        $scope.int = 1;
                        console.log("a");
                        for (var i = 0; i < $scope.checkedUsers.length; i++) {
                            if (user[$scope.mappedKeyValue.valueField] == $scope.checkedUsers[i][$scope.mappedKeyValue.valueField]) {
                                $scope.checkedUsers.splice(i, 1);
                                break;
                            }
                        }
                        var toDo = function () {
                            $scope.int = 0;
                        };
                        $timeout(toDo, 100)
                    };
                }
            };
        }])
        /***用户多选弹窗指令开始[Add By LiPan 2019-02-26]***/
        .directive('directUserMultiDialog', function (Window, CommonService) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directUserMultiDialog.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    url: "@",
                    //查询参数
                    queryParams: "=",
                    //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
                    checkedUsers: "=",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    callback: "="
                    //移除选中的人员，调用父scope中的同名方法
                    // removeSelectedUser: "&"
                },
                controller: function ($scope, $http, $element) {
                    if ($scope.url == null || '' == $scope.url) {
                        $scope.url = "user/queryUserForSelected.do";
                    }
                    $scope.paginationConf = {
                        lastCurrentTimeStamp: '',
                        currentPage: 1,
                        totalItems: 0,
                        itemsPerPage: 10,
                        pagesLength: 10,
                        queryObj: {},
                        perPageOptions: [10, 20, 30, 40, 50],
                        onChange: function () {
                        }
                    };
                    if (null != $scope.queryParams) {
                        $scope.paginationConf.queryObj = $scope.queryParams;
                    }
                    $scope.queryUser = function () {
                        $http({
                            method: 'post',
                            url: srvUrl + $scope.url,
                            data: $.param({"page": JSON.stringify($scope.paginationConf)})
                        }).success(function (data) {
                            if (data.success) {
                                $scope.users = data.result_data.list;
                                $scope.paginationConf.totalItems = data.result_data.totalItems;
                            } else {
                                Window.alert(data.result_name);
                            }
                        });
                    }
                    $scope.removeSelectedUser = function (user) {
                        for (var i = 0; i < $scope.tempCheckedUsers.length; i++) {
                            if (user.VALUE == $scope.tempCheckedUsers[i].VALUE) {
                                $scope.tempCheckedUsers.splice(i, 1);
                                break;
                            }
                        }
                    };
                    $scope.isChecked = function (user) {
                        for (var i = 0; i < $scope.tempCheckedUsers.length; i++) {
                            if (user.UUID == $scope.tempCheckedUsers[i].VALUE) {
                                return true;
                            }
                        }
                        return false;
                    };
                    $scope.toggleChecked = function (user) {
                        //是否选中
                        var isChecked = $("#chk_" + $scope.id + "_" + user.UUID).prop("checked");
                        //是否已经存在
                        var flag = false;
                        for (var i = 0; i < $scope.tempCheckedUsers.length; i++) {
                            if (user.UUID == $scope.tempCheckedUsers[i].VALUE) {
                                flag = true;
                                if (!isChecked) {
                                    $scope.tempCheckedUsers.splice(i, 1);
                                    break;
                                }
                            }
                        }
                        if (isChecked && !flag) {
                            //如果已经选中，但是不存在，添加
                            $scope.tempCheckedUsers.push({"VALUE": user.UUID, "NAME": user.NAME});
                        }
                    };

                    $scope.cancelSelected = function () {
                        $scope.initData();
                    }
                    $scope.saveSelected = function () {
                        var cus = $scope.tempCheckedUsers;
                        $scope.checkedUsers.splice(0, $scope.checkedUsers.length)
                        for (var i = 0; i < cus.length; i++) {
                            var user = {};
                            user[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                            user[$scope.mappedKeyValue.valueField] = cus[i].VALUE;

                            $scope.checkedUsers.push(user);
                            delete user.$$hashKey;
                        }
                        if ($scope.callback != null) {
                            $scope.callback();
                        }
                    }
                    $scope.initData = function () {
                        var cus = $.parseJSON(JSON.stringify($scope.checkedUsers));
                        $scope.tempCheckedUsers = [];
                        for (var i = 0; i < cus.length; i++) {
                            var user = {};
                            user.NAME = cus[i][$scope.mappedKeyValue.nameField];
                            user.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                            $scope.tempCheckedUsers.push(user);
                        }
                        $scope.paginationConf.queryObj.username = '';
                        $scope.queryUser();
                    }
                    $scope.$watch('checkedUsers', $scope.initData, true);
                    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryUser);
                }
            };
        })
        /***用户多选弹窗结束[Add By LiPan 2019-02-26]***/
        /***用户单选列表开始[Add By LiPan 2019-02-27]***/
        .directive('directiveUserRadioList', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directUserRadioList.html',
                replace: true,
                scope: {},
                controller: function ($scope, $http, $element) {
                    //获取父作用域
                    var carouselUserScope = $element.parent().scope();
                    $scope.selectUserCode = null;
                    $scope.selectUserName = null;
                    $scope.setSelection = function (code, name) {
                        $scope.selectUserCode = code;
                        $scope.selectUserName = name;
                    }
                    $scope.paginationConfes = {
                        currentPage: 1,
                        queryObj: {},
                        itemsPerPage: 10,
                        perPageOptions: [10]
                    };
                    $scope.queryuserradioList = function () {
                        var cp = $scope.paginationConfes.currentPage;
                        if (cp == 1) {
                            $scope.queryuserradio();
                        } else {
                            $scope.paginationConfes.currentPage = 1;
                        }
                    }
                    $scope.queryuserradio = function () {
                        $scope.paginationConfes.queryObj = $scope.queryObj;

                        $http({
                            method: 'post',
                            url: srvUrl + "user/getDirectiveUserAll.do",
                            data: $.param({"page": JSON.stringify($scope.paginationConfes)})
                        }).success(function (data) {
                            // 变更分页的总数
                            if (data.success) {
                                $scope.sysUserradio = data.result_data.list;
                                $scope.paginationConfes.totalItems = data.result_data.totalItems;
                            } else {
                                Window.alert(data.result_name);
                            }
                        });
                    };
                    $scope.accessScope = function (node, func) {
                        var scope = angular.element(document.querySelector(node)).scope();
                        scope.$apply(func);
                    }

                    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage + queryObj.ORGIDRADIO', $scope.queryuserradio);
                    //获取组织结构角色
                    var ztree3, setting3 = {
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                $scope.accessScope("#ORGIDRADIO", function (scope) {
                                    scope.queryObj = {};
                                    scope.queryObj.ORGIDRADIO = treeNode.id;
                                    scope.queryObj.categoryCode = treeNode.cat;
                                });
                            },
                            beforeExpand: function (treeId, treeNode) {
                                if (typeof(treeNode.children) == 'undefined') {
                                    $scope.addTreeNode3(treeNode);
                                }
                            }
                        }
                    };
                    $scope.addTreeNode3 = function (parentNode) {
                        var pid = '';
                        if (parentNode && parentNode.id) pid = parentNode.id;
                        $scope.$parent.httpData('fnd/Group/getOrg', {parentId: pid}).success(function (data) {
                            if (!data || data.result_code != 'S') return null;
                            var nodeArray = data.result_data;
                            if (nodeArray < 1) return null;
                            for (var i = 0; i < nodeArray.length; i++) {
                                curNode = nodeArray[i];
                                var iconUrl = 'javascripts/zTree/css/zTreeStyle/img/department.png';
                                if (curNode.cat && curNode.cat == 'Org') {
                                    iconUrl = 'javascripts/zTree/css/zTreeStyle/img/org.png';
                                }
                                curNode.icon = LIBS_PATH + iconUrl;
                            }
                            if (pid == '') {//当前加载的是根节点
                                ztree3.addNodes(null, nodeArray);
                                var rootNode = ztree3.getNodes()[0];
                                $scope.addTreeNode3(rootNode);
                                rootNode.open = true;
                                ztree3.refresh();
                            } else {
                                ztree3.addNodes(parentNode, nodeArray, true);
                            }
                        });
                    }

                    $scope.resetRadioUserList = function () {
                        $scope.selectUserCode = null;
                        $scope.selectUserName = null
                        $("input[name='RaidoNAME']").removeAttr("checked");

                        $scope.queryObj = {};
                        $scope.queryObj.ORGIDRADIO = null;
                        $scope.queryObj.categoryCode = null;

                    }
                    $scope.saveUserRadioListforDiretive = function () {
                        carouselUserScope.setDirectiveRadioUserList($scope.selectUserCode, $scope.selectUserName);
                        $scope.selectUserCode = null;
                        $scope.selectUserName = null;
                        $("input[name='RaidoNAME']").removeAttr("checked");
                        $scope.queryObj = {};
                        $scope.queryObj.ORGIDRADIO = null;
                        $scope.queryObj.categoryCode = null;
                    }
                    angular.element(document).ready(function () {
                        ztree3 = $.fn.zTree.init($("#treeIDuser5"), setting3);
                        $scope.addTreeNode3('');
                        $scope.selectUserCode = null;
                        $scope.selectUserName = null;
                        $scope.queryObj = {};
                        $scope.queryObj.ORGIDRADIO = null;
                        $scope.queryObj.categoryCode = null;
                    });
                }
            };
        })
        /***用户单选列表结束[Add By LiPan 2019-02-27]***/
        /***用户单选下拉开始[Add By LiPan 2019-02-27]***/
        .directive('directUserSingleSelect', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directUserSingleSelect.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    //查询参数
                    queryParams: "=",
                    //是否可编辑
                    isEditable: "=?bind",
                    //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
                    checkedUser: "=",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    callback: "="
                },
                controller: function ($scope, $http, $element) {
                    $scope.aa = function () {
                        console.log("aaa");
                    }

                    if ($scope.mappedKeyValue == null) {
                        $scope.mappedKeyValue = {nameField: 'NAME', valueField: 'VALUE'};
                    }
                    if ($scope.checkedUser == null) {
                        $scope.checkedUser = {};
                    }
                    $scope.initDefaultData = function () {
                        if ($scope.title == null) {
                            $scope.title = "人员选择";
                        }
                        if ($scope.isEditable == null || ($scope.isEditable != "true" && $scope.isEditable != "false")) {
                            $scope.isEditable = "true";
                        }
                    };
                    $scope.removeSelectedUser = function () {
                        $scope.checkedUser = {};
                    };
                    $scope.initDefaultData();
                }
            };
        })
        /***用户单选下拉结束[Add By LiPan 2019-02-27]***/
        /***用户单选Dialog开始[Add By LiPan 2019-02-27]***/
        .directive('directUserSingleDialog', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directUserSingleDialog.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    //查询参数
                    queryParams: "=",
                    //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
                    checkedUser: "=",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    callback: "="
                    //移除选中的人员，调用父scope中的同名方法
                    //removeSelectedUser: "&"
                },
                controller: function ($scope, $http, $element) {
                    $scope.initData = function () {
                        var cus = $.parseJSON(JSON.stringify($scope.checkedUser));
                        $scope.tempCheckedUser = {};
                        $scope.tempCheckedUser.NAME = cus[$scope.mappedKeyValue.nameField];
                        $scope.tempCheckedUser.VALUE = cus[$scope.mappedKeyValue.valueField];
                    }
                    $scope.paginationConf = {
                        lastCurrentTimeStamp: '',
                        currentPage: 1,
                        totalItems: 0,
                        itemsPerPage: 10,
                        pagesLength: 10,
                        queryObj: {},
                        perPageOptions: [10, 20, 30, 40, 50],
                        onChange: function () {
                        }
                    };
                    $scope.queryUser = function () {
                        $http({
                            method: 'post',
                            url: srvUrl + "user/queryUserForSelected.do",
                            data: $.param({"page": JSON.stringify($scope.paginationConf)})
                        }).success(function (data) {
                            /*hide_Mask();*/
                            if (data.success) {
                                $scope.users = data.result_data.list;
                                $scope.paginationConf.totalItems = data.result_data.totalItems;
                            } else {
                                Window.alert(data.result_name);
                            }
                        });
                    }
                    $scope.removeSelectedUser = function () {
                        $scope.tempCheckedUser = {};
                    };
                    $scope.isChecked = function (user) {
                        if (user.UUID == $scope.tempCheckedUser.VALUE) {
                            return true;
                        }
                        return false;
                    };
                    $scope.toggleChecked = function (user) {
                        //是否选中
                        var isChecked = $("#chk_" + $scope.id + "_" + user.UUID).prop("checked");
                        //是否已经存在
                        var flag = false;
                        if (user.UUID == $scope.tempCheckedUser.VALUE) {
                            flag = true;
                            if (!isChecked) {
                                $scope.tempCheckedUser = {};
                            }
                        }
                        if (isChecked && !flag) {
                            //如果已经选中，但是不存在，添加
                            $scope.tempCheckedUser = {"VALUE": user.UUID, "NAME": user.NAME};
                        }
                    };

                    $scope.cancelSelected = function () {
                        $scope.initData();
                    }
                    $scope.saveSelected = function () {
                        var cus = $scope.tempCheckedUser;
                        if (cus.VALUE == null || cus.VALUE == "") {
                            delete $scope.checkedUser[$scope.mappedKeyValue.nameField];
                            delete $scope.checkedUser[$scope.mappedKeyValue.valueField];
                        } else {
                            $scope.checkedUser[$scope.mappedKeyValue.nameField] = cus.NAME;
                            $scope.checkedUser[$scope.mappedKeyValue.valueField] = cus.VALUE;
                        }
                        if ($scope.callback != null) {
                            $scope.callback();
                        }
                    }
                    $scope.$watch('checkedUser', $scope.initData);
                    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryUser);
                }
            };
        })
        /***用户单选Dialog结束[Add By LiPan 2019-02-27]***/
        /***用户列表开始[Add By LiPan 2019-02-27]***/
        .directive('directiveUserList', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directUserList.html',
                replace: true,
                scope: {},
                controller: function ($scope, $http, $element) {
                    //获取父作用域
                    var carouselScope = $element.parent().scope();
                    $scope.selected = [];
                    $scope.selectedTags = [];
                    $scope.selectedNameValue = [];
                    $("#arrUserName").val("");
                    $scope.delDom = function (dom) {
                        $(dom).parent("li").remove();
                    }
                    var updateSelected = function (action, id, name) {
                        if (action == 'add' && $scope.selected.indexOf(id) == -1) {
                            var objs = {name: name, value: id};
                            /*封装成Object对象*/
                            $scope.selected.push(id);
                            $scope.selectedTags.push(name);
                            $scope.selectedNameValue.push(objs);
                        }
                        if (action == 'remove' && $scope.selected.indexOf(id) != -1) {
                            var objs = {name: name, value: id};
                            var idx = $scope.selected.indexOf(id);
                            $scope.selected.splice(idx, 1);
                            $scope.selectedTags.splice(idx, 1);
                            $scope.selectedNameValue.splice(objs, 1);
                        }
                        var arrName = $scope.selectedTags;
                        var arrNameToString = arrName.join(",");                 //将数组 [] 转为 String
                        $("#arrUserName").val(arrNameToString);
                        var arrIDD = $scope.selected;
                        arrIDD = arrIDD.join(",");

                        $("#selectedName").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
                        var selectedName = [];
                        var selectedId = [];
                        selectedName = arrNameToString.split(",");
                        selectedId = arrIDD.split(",");
                        var leftstr = "<li class=\"select2-search-choice\"><div>";
                        var centerstr = "</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"$scope.delDom(this)\" href=\"javascript:delCommonname('";
                        var addID = "');\"></a><input type=\"hidden\" id=\"\"  value=\"";
                        var rightstr = "\"></li>";
                        for (var i = 0; i < selectedName.length; i++) {
                            $("#selectedName").find(".select2-search-field").before(leftstr + selectedName[i] + centerstr + selectedId[i] + addID + selectedId[i] + rightstr);
                        }
                    }

                    $scope.updateSelection = function ($event, id) {
                        var checkbox = $event.target;
                        var action = (checkbox.checked ? 'add' : 'remove');
                        updateSelected(action, id, checkbox.name);
                    }

                    $scope.isSelected = function (id) {
                        return $scope.selected.indexOf(id) >= 0;
                    }
                    $scope.paginationConf = {
                        currentPage: 1,
                        itemsPerPage: 10,
                        queryObj: {},
                        perPageOptions: [10]
                    };
                    $scope.queryUserList = function () {
                        var cp = $scope.paginationConf.currentPage;
                        if (cp == 1) {
                            $scope.queryUser();
                        } else {
                            $scope.paginationConf.currentPage = 1;
                        }
                    }
                    $scope.queryUser = function () {
                        $scope.paginationConf.queryObj = $scope.queryObj;
                        var url = 'fnd/SysUser/getAll';
                        $scope.$parent.httpData(url, $scope.paginationConf).success(function (data) {
                            // 变更分页的总数
                            if (data.result_code == "S") {
                                $scope.sysUserList = data.result_data.list;
                                $scope.paginationConf.totalItems = data.result_data.totalItems;
                            }
                        });
                    };
                    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage + queryObj.ORGID', $scope.queryUser);
                    $scope.accessScope = function (node, func) {
                        var scope = angular.element(document.querySelector(node)).scope();
                        scope.$apply(func);
                    }
                    //获取组织结构角色
                    var ztree, setting = {
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                $scope.accessScope("#ORGID", function (scope) {
                                    scope.queryObj = {};
                                    scope.queryObj.ORGID = treeNode.id;
                                    scope.queryObj.categoryCode = treeNode.cat;
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
                        $scope.$parent.httpData('fnd/Group/getOrg', {parentId: pid}).success(function (data) {
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

                    $scope.resetUserList = function () {
                        $scope.selected = [];
                        $scope.selectedTags = [];
                        $scope.selectedNameValue = [];
                        $("#arrUserName").val("");
                        var d = " <div class=\"select2-success\">";
                        d += "<div id=\"s2id_projectModel\" class=\"select2-container select2-container-multi form-control ng-untouched ng-valid ng-dirty ng-valid-parse\">";
                        d += "<ul class=\"select2-choices\"> <li class=\"select2-search-field\">";
                        d += "<input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\">";
                        d += " </li></ul></div></div>";
                        $("#selectedName").html(d);
                        $scope.queryObj = {};
                        $scope.queryObj.ORGID = null;
                        $scope.queryObj.categoryCode = null;
                    }
                    $scope.saveUserListforDiretive = function () {
                        var arrUserIDs = $scope.selected;
                        var arrUserNames = $scope.selectedTags;
                        var arrUserNamesValue = $scope.selectedNameValue;
                        carouselScope.setDirectiveUserList(arrUserIDs, arrUserNames, arrUserNamesValue);
                        $scope.selected = [];
                        $scope.selectedTags = [];
                        $scope.selectedNameValue = [];
                        $("#arrUserName").val("");
                        var d = " <div class=\"select2-success\">";
                        d += "<div id=\"s2id_projectModel\" class=\"select2-container select2-container-multi form-control ng-untouched ng-valid ng-dirty ng-valid-parse\">";
                        d += "<ul class=\"select2-choices\"> <li class=\"select2-search-field\">";
                        d += "<input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\">";
                        d += " </li></ul></div></div>";
                        $("#selectedName").html(d);
                        $scope.queryObj = {};
                        $scope.queryObj.ORGID = null;
                        $scope.queryObj.categoryCode = null;
                    }

                    angular.element(document).ready(function () {
                        ztree = $.fn.zTree.init($("#treeID5"), setting);
                        $scope.addTreeNode('');

                        $scope.selected = [];
                        $scope.selectedTags = [];
                        $scope.selectedNameValue = [];
                        $("#arrUserName").val("");
                        $scope.queryObj = {};
                        $scope.queryObj.ORGID = null;
                        $scope.queryObj.categoryCode = null;
                    });
                }
            };
        })
        /***用户列表结束[Add By LiPan 2019-02-27]***/
        // 选择项目
        .directive('directReportOrgSelect', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directReportOrgSelect.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“单位选择”
                    title: "@",
                    //必填，查询的url
                    url: "@",
                    //是否可编辑，默认为true
                    isEditable: "=",
                    //是否分页，默认为false
                    isPage: "=",
                    //查询参数，非必填
                    queryParams: "=",
                    //默认选中的单位，必填,必须有键和值,可以附带其它字段,例：{"NAME":"北控中国","VALUE":"单位uuid","其它字段1":"v1",...}
                    checkedOrg: "=",
                    //映射的key，value，如果checkedOrg的键不是默认的，该字段需给出，{nameField:'orgname',valueField:'uuid'}，
                    //默认键应为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    //其它附加字段的key组成的数组，非必填，例：["orgFzr","orgParent","orgPertainArea"]
                    otherFields: "=",
                    //必填，表格的列
                    columns: "=",
                    //确定按钮的回调方法，非必填
                    callback: "="
                },
                controller: function ($scope, $http, $element) {
                    if ($scope.mappedKeyValue == null) {
                        $scope.mappedKeyValue = {nameField: 'NAME', valueField: 'VALUE'};
                    }
                    if ($scope.isPage == null) {
                        $scope.isPage = "false";
                    }
                    if ($scope.checkedOrg == null) {
                        $scope.checkedOrg = {};
                    }
                    $scope.initDefaultData = function () {
                        if ($scope.title == null) {
                            $scope.title = "单位选择";
                        }
                        if ($scope.isEditable == null || ($scope.isEditable != "true" && $scope.isEditable != "false")) {
                            $scope.isEditable = "true";
                        }
                    };
                    $scope.removeSelectedOrg = function () {
                        /*delete $scope.checkedOrg[$scope.mappedKeyValue.nameField];
                         delete $scope.checkedOrg[$scope.mappedKeyValue.valueField];
                         for (var i = 0; $scope.otherFields != null && i < $scope.otherFields.length; i++) {
                         delete $scope.checkedOrg[$scope.otherFields[i]];
                         }*/
                        $scope.checkedOrg = {};
                    };
                    $scope.initDefaultData();
                }
            };
        })
        .directive('directReportOrgDialog', function (Window, CommonService) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directReportOrgDialog.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    url: "@",
                    //查询参数
                    queryParams: "=",
                    isPage: "=",
                    //默认选中的单位，必填,必须有键和值,可以附带其它字段,例：{"NAME":"北控中国","VALUE":"单位uuid","其它字段1":"v1",...}
                    checkedOrg: "=",
                    //映射的key，value，如果checkedOrg的键不是默认的，该字段需给出，{nameField:'orgname',valueField:'uuid'}，
                    //默认键应为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    //其它附加字段的key组成的数组，非必填，例：["orgFzr","orgParent","orgPertainArea"]
                    otherFields: "=",
                    //必填，表格的列
                    columns: "=",
                    callback: "="
                },
                controller: function ($scope, $http, $element) {
                    $scope.initData = function () {
                        var cus = $.parseJSON(JSON.stringify($scope.checkedOrg));
                        $scope.tempCheckedOrg = {};
                        $scope.tempCheckedOrg.NAME = cus[$scope.mappedKeyValue.nameField];
                        $scope.tempCheckedOrg.VALUE = cus[$scope.mappedKeyValue.valueField];
                        for (var i = 0; $scope.otherFields != null && i < $scope.otherFields.length; i++) {
                            $scope.tempCheckedOrg[$scope.otherFields[i]] = cus[$scope.otherFields[i]];
                        }
                        $scope.queryOrg();
                    }
                    $scope.paginationConf = {
                        lastCurrentTimeStamp: '',
                        currentPage: 1,
                        totalItems: 0,
                        itemsPerPage: 10,
                        pagesLength: 10,
                        queryObj: {},
                        perPageOptions: [10, 20, 30, 40, 50],
                        onChange: function () {
                        }
                    };
                    /*$scope.queryOrg = function(){
                     $http({
                     method:'post',
                     url:srvUrl+"user/queryUserForSelected.do",
                     data: $.param({"page":JSON.stringify($scope.paginationConf)})
                     }).success(function(data){
                     if(data.success){
                     $scope.users = data.result_data.list;
                     $scope.paginationConf.totalItems = data.result_data.totalItems;
                     }else{
                     Window.alert(data.result_name);
                     }
                     });
                     }*/
                    $scope.queryOrg = function () {
                        var config = {
                            method: 'post',
                            url: srvUrl + $scope.url
                        };
                        if ("true" == $scope.isPage) {
                            //分页
                            if ($scope.queryParams != null) {
                                $scope.paginationConf.queryObj = $scope.queryParams;
                            }
                            config.data = $.param({"page": JSON.stringify($scope.paginationConf)})
                        } else {
                            //不分页
                            if ($scope.queryParams != null) {
                                config.data = $.param($scope.queryParams)
                            }
                        }
                        $http(config).success(function (data) {
                            if (data.success) {
                                if ("true" == $scope.isPage) {
                                    $scope.orgs = data.result_data.list;
                                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                                } else {
                                    $scope.orgs = data.result_data;
                                }
                            } else {
                                Window.alert(data.result_name);
                            }
                        });
                    }
                    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryOrg);
                    $scope.removeSelectedOrg = function () {
                        $scope.tempCheckedOrg = {};
                    };
                    $scope.isChecked = function (org) {
                        if ($scope.tempCheckedOrg != null && $scope.tempCheckedOrg.VALUE != null
                            && org[$scope.mappedKeyValue.valueField] == $scope.tempCheckedOrg.VALUE) {
                            return true;
                        }
                        return false;
                    };
                    $scope.toggleChecked = function (org) {
                        //是否选中
                        var isChecked = $("#chk_" + $scope.id + "_" + org[$scope.mappedKeyValue.valueField]).prop("checked");
                        //是否已经存在
                        var flag = false;
                        if ($scope.tempCheckedOrg != null && $scope.tempCheckedOrg.VALUE != null &&
                            org[$scope.mappedKeyValue.valueField] == $scope.tempCheckedOrg.VALUE) {
                            flag = true;
                            if (!isChecked) {
                                $scope.tempCheckedOrg = {};
                            }
                        }
                        if (isChecked && !flag) {
                            //如果已经选中，但是不存在，添加
                            $scope.tempCheckedOrg = {
                                "VALUE": org[$scope.mappedKeyValue.valueField],
                                "NAME": org[$scope.mappedKeyValue.nameField]
                            };
                            for (var i = 0; $scope.otherFields != null && i < $scope.otherFields.length; i++) {
                                $scope.tempCheckedOrg[$scope.otherFields[i]] = org[$scope.otherFields[i]];
                            }
                        }
                    };

                    $scope.cancelSelected = function () {
                        $scope.initData();
                    }
                    $scope.saveSelected = function () {
                        var cus = $scope.tempCheckedOrg;
                        if (cus.VALUE == null || cus.VALUE == "") {
                            delete $scope.checkedOrg[$scope.mappedKeyValue.nameField];
                            delete $scope.checkedOrg[$scope.mappedKeyValue.valueField];
                            for (var i = 0; $scope.otherFields != null && i < $scope.otherFields.length; i++) {
                                delete $scope.checkedOrg[$scope.otherFields[i]];
                            }
                        } else {
                            $scope.checkedOrg[$scope.mappedKeyValue.nameField] = cus.NAME;
                            $scope.checkedOrg[$scope.mappedKeyValue.valueField] = cus.VALUE;
                            for (var i = 0; $scope.otherFields != null && i < $scope.otherFields.length; i++) {
                                $scope.checkedOrg[$scope.otherFields[i]] = cus[$scope.otherFields[i]];
                            }
                        }
                        if ($scope.callback != null) {
                            $scope.callback(cus);
                        }
                    }
                    $scope.$watch('checkedOrg', $scope.initData);
                }
            };
        })
        // 流程图相关页面
        .directive('directiveProcessPage', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directiveProcessPage.html',
                replace: true,
                link: function (scope, element, attr) {
                },
                controller: function ($scope, $http, $element) {
                    $scope.wf = {};
                    //获取流程图
                    $scope.$watch("refreshImg", function () {
                        if ($scope.wfInfo != null && $scope.wfInfo.businessId != null) {
                            //如果businessId不为空，说明流程已经提交，要获取当前节点位置
                            $scope.$parent.httpData('bpm/WorkFlow/getActiveActivityIds', $scope.wfInfo).success(function (data) {
                                $scope.wf.currentNodes = data.result_data;
                            });
                        }
                        $scope.$parent.httpData('bpm/WorkFlow/getProcessDefinitionId', $scope.wfInfo).success(function (data) {
                            $scope.wf.processDefinitionId = data.result_data.processDefinitionId;
                        });
                    });
                }
            };
        })

        // 选择项目
        .directive('directiveCompanyList', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directiveCompanyList.html',
                replace: true,
                scope: {},
                controller: function ($scope, $http, $element) {
                    //获取父作用域
                    var carouselScope = $element.parent().scope();
                    $scope.selectCode = null;
                    $scope.selectName = null;
                    $scope.getSelection = function (code, name) {
                        $scope.selectCode = code;
                        $scope.selectName = name;
                    }
                    $scope.paginationConf = {
                        currentPage: 1,
                        itemsPerPage: 10,
                        queryObj: {},
                        perPageOptions: [10]
                    };
                    $scope.queryCompanyList = function () {
                        var cp = $scope.paginationConf.currentPage;
                        if (cp == 1) {
                            $scope.queryCompany();
                        } else {
                            $scope.paginationConf.currentPage = 1;
                        }
                    }
                    $scope.queryCompany = function () {
                        $scope.paginationConf.queryObj = $scope.queryObj;
                        var url = 'common/commonMethod/getDirectiveCompanyList';
                        $scope.$parent.httpData(url, $scope.paginationConf).success(function (data) {
                            // 变更分页的总数
                            if (data.result_code == "S") {
                                $scope.sysCompany = data.result_data.list;
                                $scope.paginationConf.totalItems = data.result_data.totalItems;
                            }
                        });
                    };

                    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryCompany);
                    $scope.resetCompanyList = function () {
                        $scope.selectCode = null;
                        $scope.selectName = null;
                    }
                    $scope.saveCompanyListforDiretive = function () {
                        carouselScope.setDirectiveCompanyList($scope.selectCode, $scope.selectName);
                        $scope.selectCode = null;
                        $scope.selectName = null;
                    }
                    angular.element(document).ready(function () {
                        $scope.selectCode = null;
                        $scope.selectName = null;
                    });
                }
            };
        })
        // 负责人单选框
        .directive('directFzrSingleSelect', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directFzrSingleSelect.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    //必填，查询用户的url
                    url: "@",
                    //是否可编辑
                    isEditable: "=?bind",
                    //是否分组
                    isGroup: "@",
                    //是否可以多选，'true':可以多选，'false':不可以多选，默认为'false'
                    isMultiSelect: "@",
                    //查询参数
                    queryParams: "=?",
                    //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
                    checkedUser: "=?",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=?",
                    callback: "=?",
                    //字符串，'true','false',是否默认选中全部，默认为'false'
                    isCheckedAll: "@"
                },
                controller: function ($scope, $http, $element) {
                    if ($scope.mappedKeyValue == null) {
                        $scope.mappedKeyValue = {nameField: 'NAME', valueField: 'VALUE'};
                    }
                    $scope.initDefaultData = function () {
                        if ($scope.title == null) {
                            $scope.title = "人员选择";
                        }
                        if ($scope.isGroup == null) {
                            $scope.isGroup = "false";
                        }
                        if ($scope.isEditable == null || ($scope.isEditable != "true" && $scope.isEditable != "false")) {
                            $scope.isEditable = "true";
                        }
                        if ($scope.isMultiSelect == null || ($scope.isMultiSelect != "true" && $scope.isMultiSelect != "false")) {
                            $scope.isMultiSelect = "false";
                        }
                        if ($scope.checkedUser == null && $scope.isMultiSelect == "false") {
                            $scope.checkedUser = {};
                        } else if ($scope.checkedUser == null && $scope.isMultiSelect == "true") {
                            $scope.checkedUser = [];
                        } else if ($scope.checkedUser != null && $.isArray($scope.checkedUser)) {
                            //$scope.checkedUser = [];
                            $scope.isMultiSelect == "true";
                        } else if ($scope.checkedUser != null && !$.isArray($scope.checkedUser)) {
                            $scope.checkedUser = {};
                            $scope.isMultiSelect == "false";
                        }
                        if ($scope.isCheckedAll == null || ($scope.isCheckedAll != "true" && $scope.isCheckedAll != "false")) {
                            $scope.isCheckedAll = "false";
                        }
                    };
                    $scope.removeSelectedUser = function (user) {
                        if ($scope.isMultiSelect == "false") {
                            $scope.checkedUser = {};
                        } else if ($scope.isMultiSelect == "true") {
                            for (var i = 0; i < $scope.checkedUser.length; i++) {
                                if (user[$scope.mappedKeyValue.valueField] == $scope.checkedUser[i][$scope.mappedKeyValue.valueField]) {
                                    $scope.checkedUser.splice(i, 1);
                                    break;
                                }
                            }
                        }
                    };
                    $scope.initDefaultData();
                }
            };
        })
        // 负责人单选框
        .directive('directFzrSingleDialog', ['Window', function (Window) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directFzrSingleDialog.html',
                replace: true,
                scope: {
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    url: "@",
                    //查询参数
                    queryParams: "=",
                    //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
                    checkedUser: "=",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    callback: "=",
                    //是否分组
                    isGroup: "@",
                    //是否可以多选，'true':可以多选，'false':不可以多选，默认为'false'
                    isMultiSelect: "@",
                    //字符串，'true','false',是否默认选中全部，默认为'false'
                    isCheckedAll: "@"
                },
                controller: function ($scope, $http, $element) {
                    $scope.initData = function () {
                        if (null == $scope.checkedUser) {
                            return;
                        }
                        var cus = $.parseJSON(JSON.stringify($scope.checkedUser));
                        if ($scope.isMultiSelect == "false") {
                            $scope.tempCheckedUser = {};
                            $scope.tempCheckedUser.NAME = cus[$scope.mappedKeyValue.nameField];
                            $scope.tempCheckedUser.VALUE = cus[$scope.mappedKeyValue.valueField];
                        } else if ($scope.isMultiSelect == "true") {
                            $scope.tempCheckedUser = [];
                            for (var i = 0; i < cus.length; i++) {
                                var tmpUser = {};
                                tmpUser.NAME = cus[i][$scope.mappedKeyValue.nameField];
                                tmpUser.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                                $scope.tempCheckedUser.push(tmpUser);
                            }
                        }
                        $scope.queryUser();
                    }
                    $scope.queryUser = function () {
                        var config = {
                            method: 'post',
                            url: srvUrl + $scope.url
                        };
                        if ($scope.queryParams != null) {
                            config.data = $.param($scope.queryParams)
                        }
                        $http(config).success(function (data) {
                            if (data.success) {
                                $scope.users = data.result_data;
                                if ($scope.isCheckedAll == "true" && $scope.users != null) {
                                    for (var k = 0; k < $scope.users.length; k++) {
                                        //是否已经存在
                                        var flag = false;
                                        for (var i = 0; i < $scope.tempCheckedUser.length; i++) {
                                            if ($scope.users[k][$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE) {
                                                flag = true;
                                            }
                                        }
                                        if (!flag) {
                                            //如果已经选中，但是不存在，添加
                                            $scope.tempCheckedUser.push({
                                                "VALUE": $scope.users[k][$scope.mappedKeyValue.valueField],
                                                "NAME": $scope.users[k][$scope.mappedKeyValue.nameField]
                                            });
                                        }
                                    }
                                }
                            } else {
                                Window.alert(data.result_name);
                            }
                        });
                    }
                    $scope.removeSelectedUser = function (user) {
                        if ($scope.isMultiSelect == "false") {
                            $scope.tempCheckedUser = {};
                        } else if ($scope.isMultiSelect == "true") {
                            for (var i = 0; i < $scope.tempCheckedUser.length; i++) {
                                if (user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE) {
                                    $scope.tempCheckedUser.splice(i, 1);
                                    break;
                                }
                            }
                        }
                    };
                    $scope.isChecked = function (user) {
                        if ($scope.isMultiSelect == "false") {
                            if ($scope.tempCheckedUser != null && $scope.tempCheckedUser.VALUE != null && user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser.VALUE) {
                                return true;
                            }
                            return false;
                        } else if ($scope.isMultiSelect == "true") {
                            for (var i = 0; i < $scope.tempCheckedUser.length; i++) {
                                if (user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    $scope.toggleChecked = function (user) {
                        //是否选中
                        var isChecked = $("#chk_" + $scope.id + "_" + user[$scope.mappedKeyValue.valueField]).prop("checked");
                        //是否已经存在
                        var flag = false;
                        if ($scope.isMultiSelect == "false") {
                            if ($scope.tempCheckedUser != null && $scope.tempCheckedUser.VALUE != null && user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser.VALUE) {
                                flag = true;
                                if (!isChecked) {
                                    $scope.tempCheckedUser = {};
                                }
                            }
                            if (isChecked && !flag) {
                                //如果已经选中，但是不存在，添加
                                $scope.tempCheckedUser = {
                                    "VALUE": user[$scope.mappedKeyValue.valueField],
                                    "NAME": user[$scope.mappedKeyValue.nameField]
                                };
                            }
                        } else if ($scope.isMultiSelect == "true") {
                            for (var i = 0; i < $scope.tempCheckedUser.length; i++) {
                                if (user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE) {
                                    flag = true;
                                    if (!isChecked) {
                                        $scope.tempCheckedUser.splice(i, 1);
                                        break;
                                    }
                                }
                            }
                            if (isChecked && !flag) {
                                //如果已经选中，但是不存在，添加
                                $scope.tempCheckedUser.push({
                                    "VALUE": user[$scope.mappedKeyValue.valueField],
                                    "NAME": user[$scope.mappedKeyValue.nameField]
                                });
                            }
                        }
                    };

                    $scope.cancelSelected = function () {
                        $scope.initData();
                    }
                    $scope.saveSelected = function () {
                        var cus = $scope.tempCheckedUser;
                        if ($scope.isMultiSelect == "false") {
                            if (cus.VALUE == null || cus.VALUE == "") {
                                delete $scope.checkedUser[$scope.mappedKeyValue.nameField];
                                delete $scope.checkedUser[$scope.mappedKeyValue.valueField];
                            } else {
                                $scope.checkedUser[$scope.mappedKeyValue.nameField] = cus.NAME;
                                $scope.checkedUser[$scope.mappedKeyValue.valueField] = cus.VALUE;
                            }
                        } else if ($scope.isMultiSelect == "true") {
                            var cus = $scope.tempCheckedUser;
                            $scope.checkedUser.splice(0, $scope.checkedUser.length)
                            for (var i = 0; i < cus.length; i++) {
                                var user = {};
                                user[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                                user[$scope.mappedKeyValue.valueField] = cus[i].VALUE;
                                $scope.checkedUser.push(user);
                            }
                        }
                        if ($scope.callback != null) {
                            $scope.callback($scope.checkedUser);
                        }
                    }
                    $scope.$watch('checkedUser + queryParams', $scope.initData, true);
                }
            };
        }])
        // 组织列表
        .directive('directiveOrgList', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/common/directiveOrgList.html',
                replace: true,
                scope: {},
                controller: function ($scope, $http, $element) {
                    //获取父作用域
                    var carouselScope = $element.parent().scope();
                    var paramId = null;
                    var categoryCode = null;
                    //获取组织结构角色
                    var ztree, setting = {
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                paramId = treeNode.id;
                                categoryCode = treeNode.name;
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
                        $scope.$parent.httpData('fnd/Group/getCommonOrg', {parentId: pid}).success(function (data) {
                            if (!data || data.result_code != 'S') return null;
                            var nodeArray = data.result_data;
                            if (nodeArray < 1) return null;
                            for (var i = 0; i < nodeArray.length; i++) {
                                curNode = nodeArray[i];
                                var iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/department.png';
                                if (curNode.cat && curNode.cat == 'Org') {
                                    iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/org.png';
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
                    $scope.cancelBtn = function () {
                        paramId = null;
                        categoryCode = null;
                    }
                    $scope.saveOrgListforDiretive = function () {
                        carouselScope.setDirectiveOrgList(paramId, categoryCode);
                        paramId = null;
                        categoryCode = null;
                    }

                    angular.element(document).ready(function () {
                        ztree = $.fn.zTree.init($("#treeIDpor1"), setting);
                        $scope.addTreeNode('');
                    });
                }
            };
        })
        /******* 正式评审项目相关指令 ********/
        // 正式评审项目详情
        .directive('directiveProjectFormalAssessmentInfo', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/directiveProjectFormalAssessmentInfo.html',
                replace: true,
                link: function (scope, element, attr) {
                },
                controller: function ($scope, $http, $element) {
                }
            };
        })
        // 相关资源
        .directive('directiveProjectFormalFileList', function (Window, CommonService) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/directiveProjectFormalFileList.html',
                replace: true,
                link: function (scope, element, attr) {
                },
                controller: function ($scope, $http, $element) {
                    $scope.batchDownload = function () {
                        var filenames = "";
                        var filepaths = "";
                        $("input[type=checkbox][name=choose]:checked").each(function () {
                            filenames += $(this).attr("filename") + ",";
                            filepaths += $(this).attr("filepath") + ",";
                        });
                        if (filenames.length == 0 || filepaths.length == 0) {
                            Window.alert("请选择要打包下载的文件！");
                            return false;
                        }
                        filenames = filenames.substring(0, filenames.length - 1);
                        filepaths = filepaths.substring(0, filepaths.length - 1);
                        CommonService.downloadBatch(filenames, filepaths);
                    }

                    $scope.selectAll = function () {
                        if ($("#all").attr("checked")) {
                            $(":checkbox[name='choose']").attr("checked", 1);
                        } else {
                            $(":checkbox[name='choose']").attr("checked", false);
                        }
                    }

                    //删除数组
                    $scope.deleteFile = function (item) {
                        Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                            //根据UUID和版本号定位删除
                            $http({
                                method: 'post',
                                url: srvUrl + "formalAssessmentAudit/deleteAttachment.do",
                                data: $.param({
                                    "json": JSON.stringify({
                                        "UUID": item.UUID,
                                        "version": item.version,
                                        "businessId": $scope.pfr.id
                                    })
                                })
                            }).success(function (data) {
                                if (data.success) {
                                    $scope.newAttachment.splice(jQuery.inArray(item, $scope.newAttachment), 1);
                                    $scope.getFormalAssessmentByID($scope.pfr.id);
                                    Window.alert("文件删除成功！");
                                } else {
                                    Window.alert(data.result_name);
                                }
                            });
                        })
                    }

                    //新增数组
                    $scope.addOneNewFile = function () {
                        function addBlankRow1(array) {
                            var blankRow = {
                                newFile: true
                            }
                            var size = array.length;
                            array[size] = blankRow;
                        }

                        if (undefined == $scope.newAttachment) {
                            $scope.newAttachment = {files: []};
                        }
                        addBlankRow1($scope.newAttachment);
                    }

                    $scope.upload = function (file, errorFile, outId, item) {
                        if (errorFile && errorFile.length > 0) {
                            if (errorFile[0].$error == 'maxSize') {
                                var errorMsg = fileErrorMsg(errorFile);
                                Window.alert(errorMsg);
                            } else {
                                Window.alert("文件错误！");
                            }
                        } else if (file) {

                            if (item.approved == null || item.approved == "" || item.approved.NAME == null || item.approved.NAME == "") {
                                Window.alert("请选择审核人！");
                                return;
                            }
                            if (item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == "") {
                                Window.alert("请选择编制人！");
                                return;
                            }

                            $.confirm("是否确认替换？", function () {
                                Upload.upload({
                                    url: srvUrl + 'common/RcmFile/upload',
                                    data: {file: file, typeKey: 'formalAssessmentPath'}
                                }).then(function (resp) {
                                    var retData = resp.data.result_data[0];

                                    //根据UUID和版本号定位修改
                                    $http({
                                        method: 'post',
                                        url: srvUrl + "formalAssessmentAudit/updateAttachment.do",
                                        data: $.param({
                                            "json": JSON.stringify({
                                                "UUID": item.UUID,
                                                "version": item.version,
                                                "businessId": $scope.pfr.id,
                                                "fileName": retData.fileName,
                                                "filePath": retData.filePath,
                                                "programmed": item.programmed,
                                                "approved": item.approved
                                            })
                                        })
                                    }).success(function (data) {
                                        if (data.success) {
                                            $scope.getFormalAssessmentByID($scope.pfr.id);
                                            Window.alert("文件替换成功！");
                                        } else {
                                            Window.alert(data.result_name);
                                        }
                                    });

                                    $scope.newAttachment[outId].fileName = retData.fileName;
                                    $scope.newAttachment[outId].filePath = retData.filePath;
                                }, function (resp) {
                                    console.log('Error status: ' + resp.status);
                                }, function (evt) {
                                    //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                                });
                            });
                        }
                    };

                    $scope.uploadNew = function (file, errorFile, outId, item) {
                        if (errorFile && errorFile.length > 0) {
                            if (errorFile[0].$error == 'maxSize') {
                                var errorMsg = CommonService.fileErrorMsg(errorFile);
                                Window.alert(errorMsg);
                            } else {
                                Window.alert("文件错误！");
                            }
                        } else if (file) {
                            if (item.newItem == null || item.newItem.UUID == null || item.newItem.UUID == "") {
                                Window.alert("请选择资源类型！");
                                return;
                            }
                            if (item.approved == null || item.approved == "" || item.approved.NAME == null || item.approved.NAME == "") {
                                Window.alert("请选择审核人！");
                                return;
                            }
                            if (item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == "") {
                                Window.alert("请选择编制人！");
                                return;
                            }
                            Upload.upload({
                                url: srvUrl + 'common/RcmFile/upload',
                                data: {file: file, folder: '', typeKey: 'formalAssessmentPath'}
                            }).then(function (resp) {
                                var retData = resp.data.result_data[0];

                                //根据UUID处理版本号
                                var v = 0;
                                for (var i in $scope.newAttachment) {

                                    if ($scope.newAttachment[i].newFile) {
                                        $scope.newAttachment[i] = $scope.newAttachment[i].newItem;
                                    }
                                    if ($scope.newAttachment[i].UUID == item.newItem.UUID) {
                                        if ($scope.newAttachment[i].version != undefined && $scope.newAttachment[i].version != null && $scope.newAttachment[i].version != "") {
                                            if ($scope.newAttachment[i].version > v) {
                                                v = $scope.newAttachment[i].version;
                                            }
                                        }

                                    }
                                }
                                v++;
                                item.fileName = retData.fileName;
                                item.filePath = retData.filePath;
                                //根据UUID判断文件所属类别
                                $http({
                                    method: 'post',
                                    url: srvUrl + "formalAssessmentAudit/addNewAttachment.do",
                                    data: $.param({
                                        "json": JSON.stringify({
                                            "UUID": item.newItem.UUID,
                                            "version": v,
                                            "businessId": $scope.pfr.id,
                                            "item": item
                                        })
                                    })
                                }).success(function (data) {
                                    if (data.success) {
                                        $scope.getFormalAssessmentByID($scope.pfr.id);
                                    } else {
                                        Window.alert(data.result_name);
                                    }
                                });
                                $scope.newAttachment[outId].fileName = retData.fileName;
                                $scope.newAttachment[outId].filePath = retData.filePath;
                            }, function (resp) {
                                console.log('Error status: ' + resp.status);
                            }, function (evt) {
                                //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                            });
                        }
                    };

                }
            };
        })
        // 正式评审测算意见
        .directive('directiveFormalCesuanOpinion', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/directiveFormalCesuanOpinion.html',
                replace: true,
                controller: function ($scope, $http, $element, $location, $routeParams) {

                }
            };
        })
        // 正式评审协议意见
        .directive('directiveFormalProtocolOpinion', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/directiveFormalProtocolOpinion.html',
                replace: true,
                controller: function ($scope, $http, $element, $location, $routeParams) {

                }
            };
        })
        // 正式评审流程提交弹出窗
        .directive('formalAssessmentBpmnPopWin', function (Window, CommonService) {
            return {
                restrrict: 'AE',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/formalAssessmentBpmnPopWin.html',
                replace: 'true',
                scope: {approve: '='},
                controller: function ($scope, $location, $http) {
                    //初始化提示信息框
                    $("[data-toggle='tooltip']").tooltip();

                    //验证任务人员
                    $scope.callfunction = function (functionName) {
                        var func = eval(functionName);
                        //创建函数对象，并调用
                        return new func(arguments[1]);
                    }

                    //判断是否显示toConfirm的打分项
                    $scope.showMarkMethod = function (documentation) {

                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.mark == "reviewPassMark") {
                                $scope.showReviewToConfirm = true;
                                $scope.showReviewConfirmToEnd = false;
                                $scope.showMark = true;
                            }
                            if (docObj.mark == "legalPassMark") {
                                $scope.showMark = true;
                                $scope.showLegalToConfirm = true;

                            }
                            if (docObj.mark == "toEnd") {
                                $scope.showMark = false;
                                $scope.showLegalToConfirm = false;
                                $scope.showReviewToConfirm = false;
                                $scope.showReviewConfirmToEnd = true;
                            }
                        } else {
//					$scope.showMark = false;
                            $scope.showLegalToConfirm = false;
                            $scope.showReviewToConfirm = false;
                            $scope.showReviewConfirmToEnd = false;
                        }
                    }

                    $scope.checkMark = function () {
                        if ($scope.approve == null) {
                            return;
                        }
                        var processOptions = $scope.approve.processOptions;

                        if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
                            var docObj = JSON.parse(processOptions[0].documentation);

                            if (docObj.mark == "reviewPassMark") {
                                $scope.showReviewToConfirm = true;
                            }
                            if (docObj.mark == "legalPassMark") {
                                $scope.showLegalToConfirm = true;
                            }
                        }

                        //流程选项
                        for (var i in processOptions) {
                            var documentation = processOptions[i].documentation;
                            if (documentation != null && documentation != "") {
                                var docObj = JSON.parse(documentation);
                                if (docObj.mark == "toEnd") {
                                    $scope.newEndOption = true;
                                }
                                if (docObj.mark == "reviewPassMark" || docObj.mark == "legalPassMark") {
//							$scope.showMark = true;
                                    if (i == 0) {
                                        $scope.showMark = true;
                                    }
                                    //查询后台的评价记录
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "formalMark/queryMarks.do",
                                        data: $.param({"businessId": $scope.approve.businessId}),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (result.success) {
                                                if (result.result_data == null) {
                                                    if (docObj.mark == "reviewPassMark") {
                                                        $scope.showReviewFirstMark = true;
                                                        $scope.showMark = true;
                                                    }
                                                    if (docObj.mark == "legalPassMark") {
                                                        $scope.showLegalFirstMark = true;
                                                        $scope.showMark = true;
                                                    }
                                                } else {
                                                    if (docObj.mark == "reviewPassMark") {
                                                        if (result.result_data.REVIEWFILEACCURACY == null || result.result_data.REVIEWFILEACCURACY == "" || result.result_data.FLOWMARK == null || result.result_data.FLOWMARK == '') {
                                                            $scope.showReviewFirstMark = true;
                                                        }
                                                    }
                                                    if (docObj.mark == "legalPassMark") {
                                                        if (result.result_data.LEGALFILEACCURACY == null || result.result_data.LEGALFILEACCURACY == "") {
                                                            $scope.showLegalFirstMark = true;
                                                        }
                                                    }
                                                }
                                            } else {
                                                Window.alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                    var validServiceType = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1401' && $scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1402') {
                            result.success = false;
                            result.result_name = "此项目非传统水务、水环境项目！无法选择该选项	！";
                        }
                        return result;
                    };
                    var validCheckedFzr = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.myTaskallocation == null || $scope.$parent.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.approve.showController.isSignLegal == null || $scope.approve.showController.isSignLegal == '') {
                            if ($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == "") {
                                result.success = false;
                                result.result_name = "请选择法律评审负责人！";
                            }
                        }
                        if ($scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择评审负责人！";
                        }
                        return result;
                    };
                    var validCheckedFLFzr = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.myTaskallocation == null || $scope.$parent.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择法律评审负责人！";
                        }
                        return result;
                    };

                    var validCheckedMajor = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.myTaskallocation.professionalReviewers == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers == "") {
                            result.success = false;
                            result.result_name = "请选择专业评审人员！";
                        }
                        return result;
                    };
                    $scope.submitInfo = {};
                    $scope.submitInfo.currentTaskVar = {};
                    $scope.submitNext = function () {
                        if ("submit" == $scope.approve.operateType) {
                            $scope.submit();
                        } else if ("audit" == $scope.approve.operateType) {
                            if ($scope.showReviewConfirmToEnd) {
                                $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？", function () {
                                    $scope.auditSingle();
                                });
                            } else if ($scope.showLegalToConfirm) {
                                $.confirm("您选择了评审负责人确认，意味着您已经和基层法务沟通完毕，流程将进入下一环节！是否确认？", function () {
                                    $scope.auditSingle();
                                });
                            } else if ($scope.showReviewToConfirm) {
                                $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？", function () {
                                    $scope.auditSingle();
                                });
                            } else {
                                $scope.auditSingle();
                            }
                        } else {
                            Window.alert("操作状态不明确！");
                        }
                    };
                    $scope.submit = function () {
                        var url = srvUrl + "formalAssessmentAudit/startSingleFlow.do";
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    //jquery判断是否对象非空
                    function isEmptyObject(e) {
                        var t;
                        for (t in e)
                            return !1;
                        return !0
                    }

                    $(".mark").keyup(function () {
                        if (this.value.length == 1) {
                            this.value = this.value.replace(/[^0-9]/g, '');
                        } else {
                            this.value = this.value.replace(/\D/g, '')
                        }
                        if (this.value > this.attributes.max.value * 1) {
                            this.value = null;
                        }
                    });

                    $scope.auditSingle = function () {
                        //打分项验证-start
                        if ($scope.showMark && ($scope.showReviewFirstMark || $scope.showReviewToConfirm || $scope.showLegalToConfirm)) {
                            if (!$scope.mark) {
                                Window.alert("请评分！");
                                return;
                            }
                            if ($scope.showReviewFirstMark) {
                                if ($scope.mark.flowMark == null) {
                                    Window.alert("请对审批流程熟悉度评分！");
                                    return;
                                }
                                if ($scope.mark.moneyCalculate == null) {
                                    Window.alert("请对核心财务测算能力评分！");
                                    return;
                                }
                                if ($scope.mark.reviewFileAccuracy == null) {
                                    Window.alert("请对资料的准确性评分！");
                                    return;
                                }
                                if ($scope.mark.planDesign == null) {
                                    Window.alert("请对核心的方案设计能力评分！");
                                    return;
                                }
                            }
                            if ($scope.showLegalFirstMark) {
                            }
                            if ($scope.showReviewToConfirm) {
                                if ($scope.mark.fileContent == null) {
                                    Window.alert("请对资料的完整性评分！");
                                    return;
                                }
                                if ($scope.mark.fileTime == null) {
                                    Window.alert("请对资料的及时性评分！");
                                    return;
                                }
                                if ($scope.mark.riskControl == null) {
                                    Window.alert("请对核心风险识别及规避能力评分！");
                                    return;
                                }

                            }
                            if ($scope.showLegalToConfirm) {
                                if ($scope.mark.legalFileAccuracy == null) {
                                    Window.alert("请对资料的准确性评分！");
                                    return;
                                }
                                if ($scope.mark.talks == null) {
                                    Window.alert("请对核心的协议谈判能力评分！");
                                    return;
                                }
                            }

                            //存分
                            $.ajax({
                                type: 'post',
                                url: srvUrl + "formalMark/saveOrUpdate.do",
                                data: $.param({
                                    "json": JSON.stringify($scope.mark),
                                    "businessId": $scope.approve.businessId
                                }),
                                dataType: "json",
                                async: false,
                                success: function (result) {
                                    if (!result.success) {
                                        Window.alert(result.result_name);
                                        return;
                                    }
                                }
                            });

                        }
                        //打分项验证-end

                        if ($scope.approve.showController.isServiewType) {
                            if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                                Window.alert("测算文件意见不能为空！");
                                return;
                            }
                            if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                                Window.alert("投资协议意见不能为空！");
                                return;
                            }
                            else {
                                //保存意见到mongo
                                $http({
                                    method: 'post',
                                    url: srvUrl + "formalAssessmentInfo/updateServiceTypeOpinion.do",
                                    data: $.param({
                                        "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
                                        "businessId": $scope.approve.businessId
                                    })
                                }).success(function (result) {

                                });
                            }
                        }
                        if ($scope.flowVariables == null || $scope.flowVariables == 'undefined' || $scope.flowVariables.opinion == undefined || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                            Window.alert("审批意见不能为空！");
                            return;
                        }
                        if ($scope.flowVariables.opinion.length > 650) {
                            Window.alert("审批意见不能超过650字！");
                            return;
                        }
                        if ($scope.approve.showController.isGroupMember) {
                            //保存小组成员意见到mongo

                            var json = {
                                "opinion": $scope.flowVariables.opinion,
                                "businessId": $scope.approve.businessId,
                                "user": $scope.$parent.$parent.credentials
                            };

                            $http({
                                method: 'post',
                                url: srvUrl + "formalAssessmentInfo/saveFixGroupOption.do",
                                data: $.param({"json": JSON.stringify(json)})
                            }).success(function (result) {
                            });
                        }
                        var url = srvUrl + "formalAssessmentAudit/auditSingle.do";
                        var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.preAction) {
                                var preActionArr = docObj.preAction;
                                for (var i in preActionArr) {
                                    if (preActionArr[i].callback == 'validServiceType') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedFzr') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存任务人员信息
                                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                                data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedMajor') {

                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存专业评审人员信息
                                            if ($scope.approve.showController.isTask && $scope.$parent.$parent.professionalReviewers.NAME == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers.NAME == "") {
                                                Window.alert("请选择专业评审人员！");
                                                return;
                                            }
                                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                                data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedFLFzr') {

                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存专业评审人员信息
                                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                                data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        /*show_Mask();*/
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId,
                                "opinion": $scope.flowVariables.opinion,
                                "processOption": $("input[name='bpmnProcessOption']:checked").val()
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    $scope.$watch("approve", $scope.checkMark);

                }
            }
        })
        /******* 投标评审项目相关指令 ********/
        // 投标评审项目详情
        .directive('directiveProjectPreReviewView', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pre/directiveProjectPreReviewView.html',
                replace: true,
                link: function (scope, element, attr) {
                },
                controller: function ($scope, $http, $element) {
                }
            };
        })
        // 相关资源列表
        .directive('directiveFileList', function (Window) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pre/directiveFileList.html',
                replace: true,
                link: function (scope, element, attr) {
                },
                controller: function ($scope, $http, $element) {
                    $scope.selectAll = function () {
                        if ($("#all").attr("checked")) {
                            $(":checkbox[name='choose']").attr("checked", 1);
                        } else {
                            $(":checkbox[name='choose']").attr("checked", false);
                        }
                    }
                    $scope.batchDownload = function () {
                        var filenames = "";
                        var filepaths = "";
                        $("input[type=checkbox][name=choose]:checked").each(function () {
                            filenames += $(this).attr("filename") + ",";
                            filepaths += $(this).attr("filepath") + ",";
                        });
                        if (filenames.length == 0 || filepaths.length == 0) {
                            Window.alert("请选择要打包下载的文件！");
                            return false;
                        }
                        filenames = filenames.substring(0, filenames.length - 1);
                        filepaths = filepaths.substring(0, filepaths.length - 1);
                        downloadBatch(filenames, filepaths);
                    }

                    //删除数组
                    $scope.deleteFile = function (item) {
                        Window.confirm('注意', "确认要删除该文件？").result.then(function (btn) {
                            //根据UUID和版本号定位删除
                            $http({
                                method: 'post',
                                url: srvUrl + "preInfo/deleteAttachment.do",
                                data: $.param({
                                    "json": JSON.stringify({
                                        "UUID": item.UUID,
                                        "version": item.version,
                                        "businessId": $scope.pre.id
                                    })
                                })
                            }).success(function (data) {
                                if (data.success) {
                                    $scope.newAttachment.splice(jQuery.inArray(item, $scope.newAttachment), 1);
                                    $scope.getPreById($scope.pre.id);
                                    Window.alert("文件删除成功！");
                                } else {
                                    Window.alert(data.result_name);
                                }
                            });
                        })
                    }

                    //新增数组
                    $scope.addOneNewFile = function () {
                        function addBlankRow1(array) {
                            var blankRow = {
                                newFile: true
                            }
                            var size = array.length;
                            array[size] = blankRow;
                        }

                        if (undefined == $scope.newAttachment) {
                            $scope.newAttachment = {files: []};
                        }
                        addBlankRow1($scope.newAttachment);
                    }

                    $scope.upload = function (file, errorFile, outId, item) {
                        if (errorFile && errorFile.length > 0) {
                            if (errorFile[0].$error == 'maxSize') {
                                var errorMsg = fileErrorMsg(errorFile);
                                Window.alert(errorMsg);
                            } else {
                                Window.alert("文件错误！");
                            }
                        } else if (file) {

                            if (item.approved == null || item.approved == "" || item.approved.NAME == null || item.approved.NAME == "") {
                                Window.alert("请选择审核人！");
                                return;
                            }
                            if (item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == "") {
                                Window.alert("请选择编制人！");
                                return;
                            }

                            Window.confirm('注意', "是否确认替换？").result.then(function (btn) {
                                Upload.upload({
                                    url: srvUrl + 'common/RcmFile/upload',
                                    data: {file: file, folder: '', typeKey: 'preAssessmentPath'}
                                }).then(function (resp) {
                                    var retData = resp.data.result_data[0];

                                    //根据UUID和版本号定位修改
                                    $http({
                                        method: 'post',
                                        url: srvUrl + "preInfo/updateAttachment.do",
                                        data: $.param({
                                            "json": JSON.stringify({
                                                "UUID": item.UUID,
                                                "version": item.version,
                                                "businessId": $scope.pre.id,
                                                "fileName": retData.fileName,
                                                "filePath": retData.filePath,
                                                "programmed": item.programmed,
                                                "approved": item.approved
                                            })
                                        })
                                    }).success(function (data) {
                                        if (data.success) {
                                            $scope.getPreById($scope.pre.id);
                                            Window.alert("文件替换成功！");
                                        } else {
                                            Window.alert(data.result_name);
                                        }
                                    });

                                    $scope.newAttachment[outId].fileName = retData.fileName;
                                    $scope.newAttachment[outId].filePath = retData.filePath;
                                }, function (resp) {
                                    console.log('Error status: ' + resp.status);
                                }, function (evt) {
                                    //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                                });
                            });
                        }
                    };

                    $scope.uploadNew = function (file, errorFile, outId, item) {
                        if (errorFile && errorFile.length > 0) {
                            if (errorFile[0].$error == 'maxSize') {
                                var errorMsg = fileErrorMsg(errorFile);
                                Window.alert(errorMsg);
                            } else {
                                Window.alert("文件错误！");
                            }
                        } else if (file) {
                            if (item.newItem == null || item.newItem.UUID == null || item.newItem.UUID == "") {
                                Window.alert("请选择资源类型！");
                                return;
                            }
                            if (item.approved == null || item.approved == "" || item.approved.NAME == null || item.approved.NAME == "") {
                                Window.alert("请选择审核人！");
                                return;
                            }
                            if (item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == "") {
                                Window.alert("请选择编制人！");
                                return;
                            }
                            Upload.upload({
                                url: srvUrl + 'common/RcmFile/upload',
                                data: {file: file, folder: '', typeKey: 'preAssessmentPath'}
                            }).then(function (resp) {
                                var retData = resp.data.result_data[0];

                                //根据UUID处理版本号
                                var v = 0;
                                for (var i in $scope.newAttachment) {

                                    if ($scope.newAttachment[i].newFile) {
                                        $scope.newAttachment[i] = $scope.newAttachment[i].newItem;
                                    }
                                    if ($scope.newAttachment[i].UUID == item.newItem.UUID) {
                                        if ($scope.newAttachment[i].version != undefined && $scope.newAttachment[i].version != null && $scope.newAttachment[i].version != "") {
                                            if ($scope.newAttachment[i].version > v) {
                                                v = $scope.newAttachment[i].version;
                                            }
                                        }

                                    }
                                }
                                v++;
                                item.fileName = retData.fileName;
                                item.filePath = retData.filePath;
//            				item.programmed={"NAME":$scope.credentials.userName,"VALUE":$scope.credentials.UUID}
//            				item.approved={"NAME":$scope.credentials.userName,"VALUE":$scope.credentials.UUID}

                                //根据UUID判断文件所属类别
                                $http({
                                    method: 'post',
                                    url: srvUrl + "preInfo/addNewAttachment.do",
                                    data: $.param({
                                        "json": JSON.stringify({
                                            "UUID": item.newItem.UUID,
                                            "version": v,
                                            "businessId": $scope.pre.id,
                                            "item": item
                                        })
                                    })
                                }).success(function (data) {
                                    if (data.success) {
                                        $scope.getPreById($scope.pre.id);
                                    } else {
                                        Window.alert(data.result_name);
                                    }
                                });
                                $scope.newAttachment[outId].fileName = retData.fileName;
                                $scope.newAttachment[outId].filePath = retData.filePath;
                            }, function (resp) {
                                console.log('Error status: ' + resp.status);
                            }, function (evt) {
                                //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                            });
                        }
                    };
                }
            };
        })
        // 投标评审项目详情
        .directive('directivePreCreateReport', ['$location', '$filter', function ($location, $filter) {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/business/pre/directivePreCreateReport.html',
                replace: true,
                scope: {btnText: "@btnText", textValue: "@textValue"},
                link: function (scope, element, attr) {
                },
                controller: function ($scope, $http, $element) {
                    $scope.x = {};
                    $scope.listProjectName = function () {
                        if ($scope.$parent.pre && $scope.$parent.pre._id) {//如果已经明确知道预评审项目
                            $scope.pprs = [{
                                BUSINESS_ID: $scope.$parent.pre._id,
                                PROJECT_NAME: $scope.$parent.pre.apply.projectName
                            }];
                            $scope.x.UUID = $scope.$parent.pre._id;
                        } else {
                            $http({
                                method: 'post',
                                url: srvUrl + 'preAuditReport/queryNotNewlyPreAuditProject.do'
                            }).success(function (data) {
                                if (data.success) {
                                    $scope.pprs = data.result_data;
                                }
                            }).error(function (data, status, headers, config) {
                                Window.alert(status);
                            });
                        }
                        $scope.x.pmodel = "normal";
                    };
                    $scope.forReport = function (model, uuid, comId) {
                        if (model == null || model == "") {
                            Window.alert("请选择项目模式!");
                            return false;
                        } else if (uuid == null || uuid == "") {
                            Window.alert("请选择项目!");
                            return false;
                        } else {
                            $("#addModal").modal('hide');
                            var routePath = "";
                            if (model == "normal") {
                                routePath = "PreNormalReport";
                            }

                            if (model == "other") {
                                routePath = "PreOtherReport";
                            }
                            /*$location.path("/"+routePath+"/"+model+"/Create/"+uuid+"/"+$filter('encodeURI')('#/PreAuditReportList/0'));*/
                            $location.path("/" + routePath + "/" + model + "/Create/" + uuid);
                        }
                    }
                }
            };
        }])
        /******* 其他评审项目相关指令 ********/
        // 其他评审流程框
        .directive('bpmnPopWin', function (Window, CommonService) {
            return {
                restrrict: 'AE',
                templateUrl: BUSINESS_PATH + 'directive/business/bulletin/bpmnPopWin.html',
                replace: 'true',
                scope: {approve: '='},
                controller: function ($scope, $location, $http) {

                    $scope.callfunction = function (functionName) {
                        var func = eval(functionName);
                        //创建函数对象，并调用
                        return new func(arguments[1]);
                    }

                    var validCheckedFzr = function () {
                        debugger;
                        var result = {success: true, result_name: ""};
                        console.log($scope.$parent);
                        if ($scope.$parent.myTaskallocation == null || $scope.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.myTaskallocation.reviewLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择评审负责人！";
                        }
                        return result;
                    };

                    var validCheckedRiskFzr = function () {

                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.myTaskallocation == null || $scope.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.$parent.myTaskallocation.riskLeader.NAME == null || $scope.$parent.myTaskallocation.riskLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择风控负责人！";
                        }
                        return result;
                    };
                    $scope.showSelectPerson = function () {
                        $("#submitModal").modal('hide');
                        $("#userSinDialog").modal('show');
                    }
                    $scope.submitNext = function () {
                        if ($("#workOver").attr("checked")) {
                            $scope.workOver();
                        } else if ($("input[name='bpmnProcessOption']#change:checked").length > 0) {
                            $scope.changeWork();
                        } else if ("submit" == $scope.approve.operateType) {
                            $scope.submit();
                        } else if ("audit" == $scope.approve.operateType) {
                            $scope.auditSingle();
                        } else {
                            Window.alert("操作状态不明确！");
                        }
                    };

                    $scope.workOver = function () {
                        //人员验证
                        if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                            Window.alert("审批意见不能为空！");
                            return;
                        }
                        if ($scope.flowVariables.opinion.length > 650) {
                            Window.alert("审批意见不能超过650字！");
                            return;
                        }

                        //执行办结操作
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: srvUrl + "bulletinAudit/workOver.do",
                            data: $.param({
                                "businessId": $scope.approve.businessId,
                                "oldUser": $scope.$parent.curLog.OLDUSERID,
                                "taskId": $scope.$parent.curLog.TASKID,
                                "option": $scope.flowVariables.opinion
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    }

                    $scope.changeWork = function () {
                        //人员验证
                        if ($scope.$parent.checkedUser.NAME == null || $scope.$parent.checkedUser.NAME == '') {
                            Window.alert("请选择目标人员！");
                            return;
                        }
                        if ($scope.$parent.checkedUser.VALUE == $scope.$parent.$parent.credentials.UUID) {
                            Window.alert("不能转办给自己！");
                            return;
                        }
                        if ($scope.$parent.checkedUser.VALUE == $scope.$parent.curLog.AUDITUSERID) {
                            Window.alert("不能转办给最初人员！");
                            return;
                        }
                        if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                            Window.alert("审批意见不能为空！");
                            return;
                        }
                        if ($scope.flowVariables.opinion.length > 650) {
                            Window.alert("审批意见不能超过650字！");
                            return;
                        }
                        //执行转办操作
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: srvUrl + "bulletinAudit/changeWork.do",
                            data: $.param({
                                "businessId": $scope.approve.businessId,
                                "user": JSON.stringify($scope.$parent.checkedUser),
                                "taskId": $scope.$parent.curLog.TASKID,
                                "option": $scope.flowVariables.opinion
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });


                    }
                    $scope.submit = function () {
                        var url = srvUrl + "bulletinAudit/startSingleFlow.do";
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    $scope.auditSingle = function () {
                        if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                            console.log(Window);
                            Window.alert("审批意见不能为空！");
                            return;
                        }
                        if ($scope.flowVariables.opinion.length > 650) {
                            Window.alert("审批意见不能超过650字！");
                            return;
                        }

                        var url = srvUrl + "bulletinAudit/auditSingle.do";
                        var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.preAction) {
                                var preActionArr = docObj.preAction;
                                for (var i in preActionArr) {
                                    if (preActionArr[i].callback == 'validCheckedFzr') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存任务人员信息
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                                data: $.param({
                                                    "json": JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
                                                    "businessId": $scope.approve.businessId
                                                }),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedRiskFzr') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                                data: $.param({
                                                    "json": JSON.stringify($scope.$parent.myTaskallocation),
                                                    "businessId": $scope.approve.businessId
                                                }),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId,
                                "opinion": $scope.flowVariables.opinion,
                                "processOption": $("input[name='bpmnProcessOption']:checked").val()
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                }
            }
        })
        /******* 流程相关指令[Add By LiPan 2019-03-04] ********/
        /**提交审批弹窗指令**/
        .directive('approvePopWin', function (Window, CommonService) {
            return {
                restrict: 'AE',
                templateUrl: BUSINESS_PATH + 'directive/common/approvePopWin.html',
                replace: 'true',
                scope: {approve: '=', approvearr: '='},
                controller: function ($scope, $location) {
                    $scope.changeData = function (idx) {
                        $scope.submitInfo = $scope.approve[idx].submitInfo;
                        $scope.toNodeType = $scope.approve[idx].toNodeType;
                        $scope.redirectUrl = $scope.approve[idx].redirectUrl;
                    }

                    $scope.$watch("approvearr+approve", function () {
                        var str = JSON.stringify($scope.approvearr);
                        if (str != null) {
                            $scope.approve = JSON.parse(str);
                        }

                        if ($scope.approve != null && !Array.isArray($scope.approve)) {
                            $scope.isAllocateTask = false;
                            $scope.approve = $scope.approve.approve;
                            $scope.emergencyLevel = "一般";
                        }
                        if (typeof ($scope.approve) != 'undefined') {
                            for (var i = 0; i < $scope.approve.length; i++) {//添加默认的newTaskVar属性
                                var si = $scope.approve[i].submitInfo;
                                if (typeof (si.newTaskVar) == 'undefined') {
                                    $scope.approve[i].submitInfo.newTaskVar = {submitBy: $scope.$parent.credentials.userName};
                                } else {
                                    if (typeof (si.newTaskVar.submitBy) == 'undefined') {
                                        $scope.approve[i].submitInfo.newTaskVar.submitBy = $scope.$parent.credentials.userName;
                                    }
                                }
                            }
                            $scope.toNodeIndex = 0;
                            $scope.changeData($scope.toNodeIndex);
                        }
                    }, true);

                    $scope.changeToNodeIndex = function () {
                        var idx = $scope.toNodeIndex;
                        if ($scope.approve[idx].submitInfo.currentTaskVar == null || $scope.approve[idx].submitInfo.currentTaskVar.cesuanFileOpinion == null) {
                            $("#cesuanFileOpinionDiv").hide();
                            $("#tzProtocolOpinionDiv").hide();
                        } else {
                            $("#cesuanFileOpinionDiv").show();
                            $("#tzProtocolOpinionDiv").show();
                        }
                        $scope.changeData(idx);
                    }
                    $scope.submitNext = function () {
                        if (typeof($scope.submitInfo) == 'undefined') {
                            Window.alert("请选择下一环节！");
                            return;
                        }
                        if ($scope.submitInfo.runtimeVar != null && $scope.submitInfo.runtimeVar.inputUser == "") {
                            $('#submitModal').modal('hide');
                            Window.alert("请先分配任务！");
                            return;
                        }
                        if ($scope.submitInfo.runtimeVar != null && $scope.submitInfo.runtimeVar.legalReviewLeader == "") {
                            $('#submitModal').modal('hide');
                            Window.alert("请先分配任务！");
                            return;
                        }
                        if ($scope.$parent.$parent.showController != null && $scope.$parent.$parent.showController.isTask != null) {
                            //保存任务分配信息
                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.submitInfo.businessId;
                            if ($scope.$parent.$parent.wfInfo.processKey == 'preAssessment') {
                                $.ajax({
                                    type: 'post',
                                    url: srvUrl + "preInfo/saveTaskPerson.do",
                                    data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                    dataType: "json",
                                    async: false,
                                    success: function (result) {
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return false;
                                        }
                                    }
                                });
                            } else if ($scope.$parent.$parent.wfInfo.processKey == 'formalAssessment') {
                                $.ajax({
                                    type: 'post',
                                    url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                    data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                    dataType: "json",
                                    async: false,
                                    success: function (result) {
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return false;
                                        }
                                    }
                                });
                            }
                        }
                        var taskId = $scope.submitInfo.taskId;
                        var url = "bpm/WorkFlow/startProcess";
                        if (taskId && taskId != "") {
                            url = "bpm/WorkFlow/approve";
                        }
                        var cesuan = null;
                        var tz = null;
                        if ($scope.submitInfo.currentTaskVar != null && $scope.submitInfo.currentTaskVar.cesuanFileOpinion != null) {
                            cesuan = $scope.submitInfo.currentTaskVar.cesuanFileOpinion;
                            tz = $scope.submitInfo.currentTaskVar.tzProtocolOpinion;
                        }
                        if (cesuan != null && (cesuan == "" || tz == "")) {
                            Window.alert("测算文件和投资协议的意见必须填写！");
                            return;
                        }
                        if ($scope.emergencyLevel != null) {
                            $scope.submitInfo.emergencyLevel = $scope.emergencyLevel;
                        }
                        var auditUrl = $location.absUrl();
                        var preUrl = "";

                        if (auditUrl.indexOf("preAssessment") > 0) {
                            preUrl = "ProjectPreReviewView"
                        } else if (auditUrl.indexOf("formalAssessment") > 0) {
                            preUrl = "ProjectFormalReviewDetailView/View";
                        } else if (auditUrl.indexOf("NoticeOfDecision") > 0) {
                            preUrl = "NoticeOfDecision/view";
                        }
                        var redirectUrl = null;
                        if (preUrl != "") {
                            redirectUrl = preUrl + auditUrl.substring(auditUrl.lastIndexOf("/"));
                            redirectUrl = redirectUrl.replace(taskId, "");
                        }
                        $scope.redirectUrl = null;
                        if ($scope.submitInfo.currentTaskVar != null && $scope.submitInfo.currentTaskVar.opinion != null) {
                            $scope.submitInfo.runtimeVar.opinion = $scope.submitInfo.currentTaskVar.opinion;
                        }
                        CommonService.showMask();
                        $scope.$parent.httpData(url, $scope.submitInfo).success(function (data) {
                            CommonService.hideMask();
                            if (data.success) {
                                $scope.submitAllReady = true;
                                $('#submitModal').modal('hide');
                                $('#submitModalOld').modal('hide');
                                $('#oldSubmitModal').modal('hide');
                                if (typeof $scope.redirectUrl == 'string') {//如果配置的有跳转链接
                                    $location.path($scope.redirectUrl);
                                } else {
                                    Window.alert(data.result_name);
                                    if (redirectUrl != null) {
                                        $location.path(redirectUrl);
                                    }
                                }
                            } else {
                                Window.alert(data.result_name);
                            }
                        });

                    }
                    //设置常用意见
                    $scope.setOpinion = function (opinion) {
                        $scope.submitInfo.currentTaskVar.opinion = opinion;
                    }
                }
            }
        })
        /**正式审批流程提交弹窗**/
        .directive('formalAssessmentBpmnPopWin', function (Window, CommonService) {
            return {
                restrict: 'AE',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/formalAssessmentBpmnPopWin.html',
                replace: 'true',
                scope: {approve: '='},
                controller: function ($scope, $location, $http, Upload) {
                    //初始化提示信息框
                    $("[data-toggle='tooltip']").tooltip();

                    //验证任务人员
                    $scope.callfunction = function (functionName) {
                        var func = eval(functionName);
                        //创建函数对象，并调用
                        return new func(arguments[1]);
                    }

                    //判断是否显示toConfirm的打分项
                    $scope.showMarkMethod = function (documentation) {

                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.mark == "reviewPassMark") {
                                $scope.showReviewToConfirm = true;
                                $scope.showReviewConfirmToEnd = false;
                                $scope.showMark = true;
                            }
                            if (docObj.mark == "legalPassMark") {
                                $scope.showMark = true;
                                $scope.showLegalToConfirm = true;

                            }
                            if (docObj.mark == "toEnd") {
                                $scope.showMark = false;
                                $scope.showLegalToConfirm = false;
                                $scope.showReviewToConfirm = false;
                                $scope.showReviewConfirmToEnd = true;
                            }
                        } else {
                            $scope.showLegalToConfirm = false;
                            $scope.showReviewToConfirm = false;
                            $scope.showReviewConfirmToEnd = false;
                        }
                    }

                    $scope.checkMark = function () {
                        if ($scope.approve == null) {
                            return;
                        }
                        var processOptions = $scope.approve.processOptions;

                        if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
                            var docObj = JSON.parse(processOptions[0].documentation);

                            if (docObj.mark == "reviewPassMark") {
                                $scope.showReviewToConfirm = true;
                            }
                            if (docObj.mark == "legalPassMark") {
                                $scope.showLegalToConfirm = true;
                            }
                        }
                        //流程选项
                        for (var i in processOptions) {
                            var documentation = processOptions[i].documentation;
                            if (documentation != null && documentation != "") {
                                var docObj = JSON.parse(documentation);
                                if (docObj.mark == "toEnd") {
                                    $scope.newEndOption = true;
                                }
                                if (docObj.mark == "reviewPassMark" || docObj.mark == "legalPassMark") {
                                    if (i == 0) {
                                        $scope.showMark = true;
                                    }
                                    //查询后台的评价记录
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "formalMark/queryMarks.do",
                                        data: $.param({"businessId": $scope.approve.businessId}),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (result.success) {
                                                if (result.result_data == null) {
                                                    if (docObj.mark == "reviewPassMark") {
                                                        $scope.showReviewFirstMark = true;
                                                        $scope.showMark = true;
                                                    }
                                                    if (docObj.mark == "legalPassMark") {
                                                        $scope.showLegalFirstMark = true;
                                                        $scope.showMark = true;
                                                    }
                                                } else {
                                                    if (docObj.mark == "reviewPassMark") {
                                                        if (result.result_data.REVIEWFILEACCURACY == null || result.result_data.REVIEWFILEACCURACY == "" || result.result_data.FLOWMARK == null || result.result_data.FLOWMARK == '') {
                                                            $scope.showReviewFirstMark = true;
                                                        }
                                                    }
                                                    if (docObj.mark == "legalPassMark") {
                                                        if (result.result_data.LEGALFILEACCURACY == null || result.result_data.LEGALFILEACCURACY == "") {
                                                            $scope.showLegalFirstMark = true;
                                                        }
                                                    }
                                                }
                                            } else {
                                                Window.alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                    var validServiceType = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1401' && $scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1402') {
                            result.success = false;
                            result.result_name = "此项目非传统水务、水环境项目！无法选择该选项	！";
                        }
                        return result;
                    };
                    var validCheckedFzr = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.myTaskallocation == null || $scope.$parent.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.approve.showController.isSignLegal == null || $scope.approve.showController.isSignLegal == '') {
                            if ($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == "") {
                                result.success = false;
                                result.result_name = "请选择法律评审负责人！";
                            }
                        }
                        if ($scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择评审负责人！";
                        }
                        return result;
                    };
                    var validCheckedFLFzr = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.myTaskallocation == null || $scope.$parent.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择法律评审负责人！";
                        }
                        return result;
                    };

                    var validCheckedMajor = function () {
                        var result = {success: true, result_name: ""};

                        if ($scope.$parent.$parent.myTaskallocation.professionalReviewers == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers == "") {
                            result.success = false;
                            result.result_name = "请选择专业评审人员！";
                        }
                        return result;
                    };
                    $scope.submitInfo = {};
                    $scope.submitInfo.currentTaskVar = {};
                    $scope.submitNext = function () {
                        if ("submit" == $scope.approve.operateType) {
                            $scope.submit();
                        } else if ("audit" == $scope.approve.operateType) {
                            if ($scope.showReviewConfirmToEnd) {
                                $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？", function () {
                                    $scope.auditSingle();
                                });
                            } else if ($scope.showLegalToConfirm) {
                                $.confirm("您选择了评审负责人确认，意味着您已经和基层法务沟通完毕，流程将进入下一环节！是否确认？", function () {
                                    $scope.auditSingle();
                                });
                            } else if ($scope.showReviewToConfirm) {
                                $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？", function () {
                                    $scope.auditSingle();
                                });
                            } else {
                                $scope.auditSingle();
                            }
                        } else {
                            Window.alert("操作状态不明确！");
                        }
                    };
                    $scope.submit = function () {
                        var url = srvUrl + "formalAssessmentAudit/startSingleFlow.do";
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    //jquery判断是否对象非空
                    function isEmptyObject(e) {
                        var t;
                        for (t in e)
                            return !1;
                        return !0
                    }

                    $(".mark").keyup(function () {
                        if (this.value.length == 1) {
                            this.value = this.value.replace(/[^0-9]/g, '');
                        } else {
                            this.value = this.value.replace(/\D/g, '')
                        }
                        if (this.value > this.attributes.max.value * 1) {
                            this.value = null;
                        }
                    });

                    $scope.auditSingle = function () {
                        //打分项验证-start
                        if ($scope.showMark && ($scope.showReviewFirstMark || $scope.showReviewToConfirm || $scope.showLegalToConfirm)) {
                            if (!$scope.mark) {
                                Window.alert("请评分！");
                                return;
                            }
                            if ($scope.showReviewFirstMark) {
                                if ($scope.mark.flowMark == null) {
                                    Window.alert("请对审批流程熟悉度评分！");
                                    return;
                                }
                                if ($scope.mark.moneyCalculate == null) {
                                    Window.alert("请对核心财务测算能力评分！");
                                    return;
                                }
                                if ($scope.mark.reviewFileAccuracy == null) {
                                    Window.alert("请对资料的准确性评分！");
                                    return;
                                }
                                if ($scope.mark.planDesign == null) {
                                    Window.alert("请对核心的方案设计能力评分！");
                                    return;
                                }
                            }
                            if ($scope.showLegalFirstMark) {
                            }
                            if ($scope.showReviewToConfirm) {
                                if ($scope.mark.fileContent == null) {
                                    Window.alert("请对资料的完整性评分！");
                                    return;
                                }
                                if ($scope.mark.fileTime == null) {
                                    Window.alert("请对资料的及时性评分！");
                                    return;
                                }
                                if ($scope.mark.riskControl == null) {
                                    Window.alert("请对核心风险识别及规避能力评分！");
                                    return;
                                }

                            }
                            if ($scope.showLegalToConfirm) {
                                if ($scope.mark.legalFileAccuracy == null) {
                                    Window.alert("请对资料的准确性评分！");
                                    return;
                                }
                                if ($scope.mark.talks == null) {
                                    Window.alert("请对核心的协议谈判能力评分！");
                                    return;
                                }
                            }

                            //存分
                            $.ajax({
                                type: 'post',
                                url: srvUrl + "formalMark/saveOrUpdate.do",
                                data: $.param({
                                    "json": JSON.stringify($scope.mark),
                                    "businessId": $scope.approve.businessId
                                }),
                                dataType: "json",
                                async: false,
                                success: function (result) {
                                    if (!result.success) {
                                        Window.alert(result.result_name);
                                        return;
                                    }
                                }
                            });

                        }
                        //打分项验证-end

                        if ($scope.approve.showController.isServiewType) {
                            if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                                Window.alert("测算文件意见不能为空！");
                                return;
                            }
                            if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                                Window.alert("投资协议意见不能为空！");
                                return;
                            }
                            else {
                                //保存意见到mongo
                                $http({
                                    method: 'post',
                                    url: srvUrl + "formalAssessmentInfo/updateServiceTypeOpinion.do",
                                    data: $.param({
                                        "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
                                        "businessId": $scope.approve.businessId
                                    })
                                }).success(function (result) {

                                });
                            }
                        }
                        if ($scope.flowVariables == null || $scope.flowVariables == 'undefined' || $scope.flowVariables.opinion == undefined || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                            Window.alert("审批意见不能为空！");
                            return;
                        }
                        if ($scope.flowVariables.opinion.length > 650) {
                            Window.alert("审批意见不能超过650字！");
                            return;
                        }
                        if ($scope.approve.showController.isGroupMember) {
                            //保存小组成员意见到mongo
                            var json = {
                                "opinion": $scope.flowVariables.opinion,
                                "businessId": $scope.approve.businessId,
                                "user": $scope.$parent.$parent.credentials
                            };

                            $http({
                                method: 'post',
                                url: srvUrl + "formalAssessmentInfo/saveFixGroupOption.do",
                                data: $.param({"json": JSON.stringify(json)})
                            }).success(function (result) {
                            });
                        }
                        var url = srvUrl + "formalAssessmentAudit/auditSingle.do";
                        var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.preAction) {
                                var preActionArr = docObj.preAction;
                                for (var i in preActionArr) {
                                    if (preActionArr[i].callback == 'validServiceType') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedFzr') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存任务人员信息
                                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                                data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedMajor') {

                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存专业评审人员信息
                                            if ($scope.approve.showController.isTask && $scope.$parent.$parent.professionalReviewers.NAME == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers.NAME == "") {
                                                Window.alert("请选择专业评审人员！");
                                                return;
                                            }
                                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                                data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedFLFzr') {

                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存专业评审人员信息
                                            $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                                data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId,
                                "opinion": $scope.flowVariables.opinion,
                                "processOption": $("input[name='bpmnProcessOption']:checked").val()
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    $scope.$watch("approve", $scope.checkMark);
                }
            }
        })
        .directive('preReviewBpmnPopWin', function (Window, CommonService) {
            return {
                restrict: 'AE',
                templateUrl: BUSINESS_PATH + 'directive/business/pfr/preReviewBpmnPopWin.html',
                replace: 'true',
                scope: {approve: '='},
                controller: function ($scope, $location, $http, Upload) {

                    $scope.pre = {};
                    //默认不上会
                    $scope.pre.needMeeting = '0';
                    //默认不出报告
                    $scope.pre.needReport = '1';
                    $scope.pre.decisionOpinion = false;
                    //验证任务人员
                    $scope.callfunction = function (functionName) {
                        var func = eval(functionName);
                        //创建函数对象，并调用
                        return new func(arguments[1]);
                    }
                    var validServiceType = function () {
                        var result = {success: true, result_name: ""};
                        if ($scope.$parent.pre.oracle.SERVICETYPE_ID != "1401" && $scope.$parent.pre.oracle.SERVICETYPE_ID != "1402") {
                            result.success = false;
                            result.result_name = "此项目非传统水务、水环境项目！无法选择此选项！";
                        }
                        return result;
                    };
                    //验证任务人员信息(分配任务节点)
                    var validCheckedFzr = function () {
                        var result = {success: true, result_name: ""};
                        if ($scope.$parent.myTaskallocation == null || $scope.$parent.myTaskallocation == "") {
                            result.success = false;
                            result.result_name = "请分配负责人！";
                        }
                        if ($scope.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.myTaskallocation.reviewLeader.NAME == "") {
                            result.success = false;
                            result.result_name = "请选择评审负责人！";
                        }
                        return result;
                    };
                    //验证意见（投资中心/水环境）
                    var validOpinions = function () {
                        var result = {success: true, result_name: ""};
                        if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                            result.success = false;
                            result.result_name = "请填写测算文件意见！";
                        }
                        if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                            result.success = false;
                            result.result_name = "请填写投资协议意见！";
                        }
                        return result;
                    }
                    //判断是否显示toConfirm的打分项
                    $scope.showMarkMethod = function (documentation) {
                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.mark == "reviewPassMark") {
                                $scope.showReviewToConfirm = true;
                                $scope.showReviewConfirmToEnd = false;
                                $scope.showMark = true;
                            }
                            if (docObj.mark == "toEnd") {
                                $scope.showMark = false;
                                $scope.showReviewToConfirm = false;
                                $scope.showReviewConfirmToEnd = true;
                            }
                        } else {
                            $scope.showReviewToConfirm = false;
                            $scope.showReviewConfirmToEnd = false;
                        }
                    }
                    $scope.checkMark = function () {
                        if ($scope.approve == null) {
                            return;
                        }
                        var processOptions = $scope.approve.processOptions;

                        if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
                            var docObj = JSON.parse(processOptions[0].documentation);

                            if (docObj.mark == "reviewPassMark") {
                                $scope.showReviewToConfirm = true;
                            }
                            if (docObj.mark == "legalPassMark") {
                                $scope.showLegalToConfirm = true;
                            }
                        }
                        //流程选项
                        for (var i in processOptions) {
                            var documentation = processOptions[i].documentation;
                            if (documentation != null && documentation != "") {
                                var docObj = JSON.parse(documentation);
                                if (docObj.mark == "toEnd") {
                                    $scope.newEndOption = true;
                                }
                                if (docObj.mark == "reviewPassMark") {
                                    if (i == 0) {
                                        $scope.showMark = true;
                                    }
                                }
                            }
                        }
                    }
                    $scope.submitInfo = {};
                    $scope.submitInfo.currentTaskVar = {};
                    $scope.submitNext = function () {
                        if ("submit" == $scope.approve.operateType) {
                            $scope.submit();
                        } else if ("audit" == $scope.approve.operateType) {
                            if ($scope.showReviewConfirmToEnd) {
                                Window.confirm("确认", "您选择了终止项目，意味着您将废弃此项目！是否确认?").result.then(function () {
                                        $scope.auditSingle();
                                    }
                                );
                            } else if ($scope.showReviewToConfirm) {
                                Window.confirm("确认", "您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认?").result.then(function () {
                                    $scope.auditSingle();
                                });
                            } else {
                                $scope.auditSingle();
                            }
                        } else {
                            Window.alert("操作状态不明确！");
                        }
                    };
                    $scope.checkReport = function () {
                        $scope.pre.needReport = "1";
                        $scope.pre.decisionOpinion = null;
                    }
                    $scope.submit = function () {
                        var url = srvUrl + "preAudit/startSingleFlow.do";
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    //jquery判断是否对象非空
                    function isEmptyObject(e) {
                        var t;
                        for (t in e)
                            return !1;
                        return !0
                    }

                    $scope.auditSingle = function () {
                        //验证确认节点是否选择上会，与报告
                        if ($scope.approve.showController.isReviewLeaderConfirm) {
                            if ($scope.pre == null || $scope.pre.needMeeting == null) {
                                Window.alert("请选择是否需要上会！");
                                return;
                            }
                            if ($scope.pre.needReport == null) {
                                Window.alert("请选择是否需要出评审报告！");
                                return;
                            }

                            if ($scope.pre.needReport == 0) {
                                if ($scope.pre.noReportReason == null || $scope.pre.noReportReason == '') {
                                    Window.alert("请填写不出报告原因！");
                                    return;
                                }
                                if ($scope.pre.noReportReason.length > 200) {
                                    Window.alert("不出报告原因不能大于200字！");
                                    return;
                                }
                            }
                            if ($scope.pre.decisionOpinion) {
                                $scope.pre.decisionOpinion = '6';
                            } else {
                                $scope.pre.decisionOpinion = '5';
                            }
                            $.ajax({
                                type: 'post',
                                url: srvUrl + "preInfo/saveNeedMeetingAndNeedReport.do",
                                data: $.param({
                                    "pre": JSON.stringify($scope.pre),
                                    'businessId': $scope.approve.businessId
                                }),
                                dataType: "json",
                                async: false,
                                success: function (result) {
                                    if (!result.success) {
                                        Window.alert(result.result_name);
                                        return;
                                    }
                                }
                            });
                        }

                        if ($scope.flowVariables == null || $scope.flowVariables == 'undefined' || $scope.flowVariables.opinion == undefined || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                            Window.alert("审批意见不能为空！");
                            return;
                        }
                        if ($scope.flowVariables.opinion.length > 650) {
                            Window.alert("审批意见不能超过650字！");
                            return;
                        }
                        if ($scope.approve.showController.isGroupMember) {
                            //保存小组成员意见到mongo
                            var json = {
                                "opinion": $scope.flowVariables.opinion,
                                "businessId": $scope.approve.businessId,
                                "user": $scope.$parent.$parent.credentials
                            };

                            $http({
                                method: 'post',
                                url: srvUrl + "preInfo/saveFixGroupOption.do",
                                data: $.param({"json": JSON.stringify(json)})
                            }).success(function (result) {
                            });
                        }
                        var url = srvUrl + "preAudit/auditSingle.do";
                        var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                        if (documentation != null && documentation != "") {
                            var docObj = JSON.parse(documentation);
                            if (docObj.preAction) {
                                var preActionArr = docObj.preAction;
                                for (var i in preActionArr) {
                                    //validServiceType
                                    if (preActionArr[i].callback == 'validServiceType') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedFzr') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存任务人员信息
                                            $scope.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "preInfo/saveTaskPerson.do",
                                                data: $.param({
                                                    "task": JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
                                                }),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validOpinions') {
                                        var result = $scope.callfunction(preActionArr[i].callback);
                                        if (!result.success) {
                                            Window.alert(result.result_name);
                                            return;
                                        } else {
                                            //保存意见信息
                                            $.ajax({
                                                type: 'post',
                                                url: srvUrl + "preInfo/saveServiceTypeOpinion.do",
                                                data: $.param({
                                                    "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
                                                    "businessId": $scope.approve.businessId
                                                }),
                                                dataType: "json",
                                                async: false,
                                                success: function (result) {
                                                    if (!result.success) {
                                                        Window.alert(result.result_name);
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    } else if (preActionArr[i].callback == 'validCheckedMajorMember') {
                                    }
                                }
                            }
                        }
                        CommonService.showMask();
                        $http({
                            method: 'post',
                            url: url,
                            data: $.param({
                                "processKey": $scope.approve.processKey,
                                "businessId": $scope.approve.businessId,
                                "opinion": $scope.flowVariables.opinion,
                                "processOption": $("input[name='bpmnProcessOption']:checked").val()
                            })
                        }).success(function (result) {
                            CommonService.hideMask();
                            if ($scope.approve.callbackSuccess != null && result.success) {
                                $scope.approve.callbackSuccess(result);
                            } else if ($scope.approve.callbackFail != null && !result.success) {
                                $scope.approve.callbackFail(result);
                            } else {
                                Window.alert(result.result_name);
                            }
                        });
                    };
                    $scope.$watch("approve", $scope.checkMark);
                }
            }
        })
    /**其它指令[Add By LiPan 2019-03-04]**/
});