/**
 * name: tm.pagination
 * Version: 1.0.0 beta
 */
angular.module('paginationhome', []).directive('tmPaginationHome',[function(){
    return {
        restrict: 'EA',
        template: '<div class="page-list">' +
            '<ul class="pagination" ng-show="confhome.totalItems > 0">' +
            '<li ng-class="{disabled: confhome.currentPage == 1}" ng-click="prevPage()"><span>&laquo;</span></li>' +
            '<li ng-repeat="item in pageList track by $index" ng-class="{active: item == confhome.currentPage, separate: item == \'...\'}" ' +
            'ng-click="changeCurrentPage(item)">' +
            '<span>{{ item }}</span>' +
            '</li>' +
            '<li ng-class="{disabled: confhome.currentPage == confhome.numberOfPages}" ng-click="nextPage()"><span>&raquo;</span></li>' +
            '</ul>' +
            '<div class="page-total" ng-show="confhome.totalItems > 0">' +
            '每页<select ng-model="confhome.itemsPerPage" ng-options="option for option in confhome.perPageOptions " ng-change="changeItemsPerPage()"></select>' +
            '/共<strong>{{ confhome.totalItems }}</strong>条 ' +
            '跳转至<input type="text" ng-model="jumpPageNum" ng-keyup="jumpPageKeyUp($event)"/>' +
            '</div>' +
            '<div class="no-items" ng-show="confhome.totalItems <= 0">暂无数据</div>' +
            '</div>',
        replace: true,
        scope: {
            confhome: '='
        },
        link: function(scope, element, attrs) {
            
            var confhome = scope.confhome;

            // 默认分页长度
            var defaultPagesLength = 9;

            // 默认分页选项可调整每页显示的条数
            var defaultPerPageOptions = [10, 20, 30, 40];

            // 默认每页的个数
            var defaultPerPage = 10;

            // 获取分页长度
            if(confhome.pagesLength) {
                // 判断一下分页长度
                confhome.pagesLength = parseInt(confhome.pagesLength, 10);

                if(!confhome.pagesLength) {
                    confhome.pagesLength = defaultPagesLength;
                }

                // 分页长度必须为奇数，如果传偶数时，自动处理
                if(confhome.pagesLength % 2 === 0) {
                    confhome.pagesLength += 1;
                }

            } else {
                confhome.pagesLength = defaultPagesLength
            }

            // 分页选项可调整每页显示的条数
            if(!confhome.perPageOptions){
                confhome.perPageOptions = defaultPagesLength;
            }

            // pageList数组
            function getPagination(newValue, oldValue) {
                
                // confhome.currentPage
                if(confhome.currentPage) {
                    confhome.currentPage = parseInt(scope.confhome.currentPage, 10);
                }

                if(!confhome.currentPage) {
                    confhome.currentPage = 1;
                }

                // confhome.totalItems
                if(confhome.totalItems) {
                    confhome.totalItems = parseInt(confhome.totalItems, 10);
                }

                // confhome.totalItems
                if(!confhome.totalItems) {
                    confhome.totalItems = 0;
                    return;
                }
                
                // confhome.itemsPerPage
                if(confhome.itemsPerPage) {
                    confhome.itemsPerPage = parseInt(confhome.itemsPerPage, 10);
                }
                if(!confhome.itemsPerPage) {
                    confhome.itemsPerPage = defaultPerPage;
                }

                // numberOfPages
                confhome.numberOfPages = Math.ceil(confhome.totalItems/confhome.itemsPerPage);

                // 如果分页总数>0，并且当前页大于分页总数
                if(scope.confhome.numberOfPages > 0 && scope.confhome.currentPage > scope.confhome.numberOfPages){
                    scope.confhome.currentPage = scope.confhome.numberOfPages;
                }

                // 如果itemsPerPage在不在perPageOptions数组中，就把itemsPerPage加入这个数组中
                var perPageOptionsLength = scope.confhome.perPageOptions.length;

                // 定义状态
                var perPageOptionsStatus;
                for(var i = 0; i < perPageOptionsLength; i++){
                    if(confhome.perPageOptions[i] == confhome.itemsPerPage){
                        perPageOptionsStatus = true;
                    }
                }
                // 如果itemsPerPage在不在perPageOptions数组中，就把itemsPerPage加入这个数组中
                if(!perPageOptionsStatus){
                    confhome.perPageOptions.push(confhome.itemsPerPage);
                }

                // 对选项进行sort
                confhome.perPageOptions.sort(function(a, b) {return a - b});
                

                // 页码相关
                scope.pageList = [];
                if(confhome.numberOfPages <= confhome.pagesLength){
                    // 判断总页数如果小于等于分页的长度，若小于则直接显示
                    for(i =1; i <= confhome.numberOfPages; i++){
                        scope.pageList.push(i);
                    }
                }else{
                    // 总页数大于分页长度（此时分为三种情况：1.左边没有...2.右边没有...3.左右都有...）
                    // 计算中心偏移量
                    var offset = (confhome.pagesLength - 1) / 2;
                    if(confhome.currentPage <= offset){
                        // 左边没有...
                        for(i = 1; i <= offset + 1; i++){
                            scope.pageList.push(i);
                        }
                        scope.pageList.push('...');
                        scope.pageList.push(confhome.numberOfPages);
                    }else if(confhome.currentPage > confhome.numberOfPages - offset){
                        scope.pageList.push(1);
                        scope.pageList.push('...');
                        for(i = offset + 1; i >= 1; i--){
                            scope.pageList.push(confhome.numberOfPages - i);
                        }
                        scope.pageList.push(confhome.numberOfPages);
                    }else{
                        // 最后一种情况，两边都有...
                        scope.pageList.push(1);
                        scope.pageList.push('...');

                        for(i = Math.ceil(offset / 2) ; i >= 1; i--){
                            scope.pageList.push(confhome.currentPage - i);
                        }
                        scope.pageList.push(confhome.currentPage);
                        for(i = 1; i <= offset / 2; i++){
                            scope.pageList.push(confhome.currentPage + i);
                        }

                        scope.pageList.push('...');
                        scope.pageList.push(confhome.numberOfPages);
                    }
                }

                scope.$parent.confhome = confhome;
            }

            // prevPage
            scope.prevPage = function() {
                if(confhome.currentPage > 1){
                    confhome.currentPage -= 1;
                }
            };

            // nextPage
            scope.nextPage = function() {
                if(confhome.currentPage < confhome.numberOfPages){
                    confhome.currentPage += 1;
                }
            };

            // 变更当前页
            scope.changeCurrentPage = function(item) {
                
                if(item == '...'){
                    return;
                }else{
                    confhome.currentPage = item;
                    getPagination();
                    // confhome.onChange()函数
                    if(confhome.onChange) {
                        confhome.onChange();
                    }
                }
            };

            // 修改每页展示的条数
            scope.changeItemsPerPage = function() {

                // 一发展示条数变更，当前页将重置为1
                confhome.currentPage = 1;

                getPagination();
                // confhome.onChange()函数
                if(confhome.onChange) {
                    confhome.onChange();
                }
            };

            // 跳转页
            scope.jumpToPage = function() {
                num = scope.jumpPageNum;
                if(num.match(/\d+/)) {
                    num = parseInt(num, 10);
                
                    if(num && num != confhome.currentPage) {
                        if(num > confhome.numberOfPages) {
                            num = confhome.numberOfPages;
                        }

                        // 跳转
                        confhome.currentPage = num;
                        getPagination();
                        // confhome.onChange()函数
                        if(confhome.onChange) {
                            confhome.onChange();
                        }
                        scope.jumpPageNum = '';
                    }
                }
                
            };

            scope.jumpPageKeyUp = function(e) {
                var keycode = window.event ? e.keyCode :e.which;
                
                if(keycode == 13) {
                    scope.jumpToPage();
                }
            }

            scope.$watch('confhome.totalItems', function(value, oldValue) {
                
                // 在无值或值相等的时候，去执行onChange事件
                if(!value || value == oldValue) {
                    
                    if(confhome.onChange) {
                        confhome.onChange();
                    }
                }
                getPagination();
            })
            
        }
    };
}]);
