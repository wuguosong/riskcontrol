/**
 * Created by gaohe on 2016/06/06.
 */
define(['app', 'ztree-core'], function (app) {
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
        .directive('directUserMultiSelect', function () {
            return {
                restrict: 'E',
                templateUrl:BUSINESS_PATH + 'directive/common/directUserMultiSelect.html',
                replace: true,
                scope:{
                    //必填,该指令所在modal的id，在当前页面唯一
                    id: "@",
                    //对话框的标题，如果没设置，默认为“人员选择”
                    title: "@",
                    //查询参数
                    queryParams: "=",
                    isEditable:"=",
                    //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
                    checkedUsers: "=",
                    //映射的key，value，{nameField:'username',valueField:'uuid'}，
                    //默认为{nameField:'NAME',valueField:'VALUE'}
                    mappedKeyValue: "=",
                    callback: "="
                },
                controller:function($scope,$http,$element){
                    if($scope.mappedKeyValue == null){
                        $scope.mappedKeyValue = {nameField:'NAME',valueField:'VALUE'};
                    }
                    if($scope.checkedUsers == null){
                        $scope.checkedUsers = [];
                    }
                    $scope.initDefaultData = function(){
                        if($scope.title==null){
                            $scope.title = "人员选择";
                        }
                        if($scope.isEditable==null|| ($scope.isEditable!="true" && $scope.isEditable!="false")){
                            $scope.isEditable = "true";
                        }
                    };
                    $scope.initDefaultData();
                    $scope.removeSelectedUser = function(user){
                        for(var i = 0; i < $scope.checkedUsers.length; i++){
                            if(user[$scope.mappedKeyValue.valueField] == $scope.checkedUsers[i][$scope.mappedKeyValue.valueField]){
                                $scope.checkedUsers.splice(i, 1);
                                break;
                            }
                        }
                    };
                }
            };
        })
        /***用户多选弹窗指令开始[Add By LiPan 2019-02-26]***/
        .directive('directUserMultiDialog', function () {
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
        .directive('directReportOrgDialog', function () {
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
                     $.alert(data.result_name);
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
});