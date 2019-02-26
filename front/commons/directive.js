/**
 * Created by gaohe on 2016/06/06.
 */
define(['app'], function (app) {
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
                    console.log(conf);

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
        /***用户多选弹窗指令开始[Add By LiPan 2019-02-26]***/
        .directive('directUserMultiDialog', function () {
            return {
                restrict: 'E',
                templateUrl: BUSINESS_PATH + 'directive/directUserMultiDialog.html',
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
                            url: SRV_URL + $scope.url,
                            data: $.param({"page": JSON.stringify($scope.paginationConf)})
                        }).success(function (data) {
                            if (data.success) {
                                $scope.users = data.result_data.list;
                                $scope.paginationConf.totalItems = data.result_data.totalItems;
                            } else {
                                $.alert(data.result_name);
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
});