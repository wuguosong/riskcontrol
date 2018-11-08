/**
 * name: tm.pagination
 * Version: 1.0.0 beta
 */
angular.module('paginationes', []).directive('tmPaginationes',[function(){
    return {
        restrict: 'EA',
        template: '<div class="page-list">' +
            '<ul class="pagination" ng-show="confes.totalItems > 0">' +
            '<li ng-class="{disabled: confes.currentPage == 1}" ng-click="prevPage()"><span>&laquo;</span></li>' +
            '<li ng-repeat="item in pageList track by $index" ng-class="{active: item == confes.currentPage, separate: item == \'...\'}" ' +
            'ng-click="changeCurrentPage(item)">' +
            '<span>{{ item }}</span>' +
            '</li>' +
            '<li ng-class="{disabled: confes.currentPage == confes.numberOfPages}" ng-click="nextPage()"><span>&raquo;</span></li>' +
            '</ul>' +
            '<div class="page-total" ng-show="confes.totalItems > 0">' +
            '每页<select ng-model="confes.itemsPerPage" ng-options="option for option in confes.perPageOptions " ng-change="changeItemsPerPage()"></select>' +
            '/共<strong>{{ confes.totalItems }}</strong>条 ' +
            '跳转至<input type="text" ng-model="jumpPageNum" ng-keyup="jumpPageKeyUp($event)"/>' +
            '</div>' +
            '<div class="no-items" ng-show="confes.totalItems <= 0">暂无数据</div>' +
            '</div>',
        replace: true,
        scope: {
            confes: '='
        },
        link: function(scope, element, attrs) {
            
            var confes = scope.confes;

            // 默认分页长度
            var defaultPagesLength = 9;

            // 默认分页选项可调整每页显示的条数
            var defaultPerPageOptions = [10, 20, 30, 40];

            // 默认每页的个数
            var defaultPerPage = 10;

            // 获取分页长度
            if(confes.pagesLength) {
                // 判断一下分页长度
                confes.pagesLength = parseInt(confes.pagesLength, 10);

                if(!confes.pagesLength) {
                    confes.pagesLength = defaultPagesLength;
                }

                // 分页长度必须为奇数，如果传偶数时，自动处理
                if(confes.pagesLength % 2 === 0) {
                    confes.pagesLength += 1;
                }

            } else {
                confes.pagesLength = defaultPagesLength
            }

            // 分页选项可调整每页显示的条数
            if(!confes.perPageOptions){
                confes.perPageOptions = defaultPagesLength;
            }

            // pageList数组
            function getPagination(newValue, oldValue) {
                
                // confes.currentPage
                if(confes.currentPage) {
                    confes.currentPage = parseInt(scope.confes.currentPage, 10);
                }

                if(!confes.currentPage) {
                    confes.currentPage = 1;
                }

                // confes.totalItems
                if(confes.totalItems) {
                    confes.totalItems = parseInt(confes.totalItems, 10);
                }

                // confes.totalItems
                if(!confes.totalItems) {
                    confes.totalItems = 0;
                    return;
                }
                
                // confes.itemsPerPage
                if(confes.itemsPerPage) {
                    confes.itemsPerPage = parseInt(confes.itemsPerPage, 10);
                }
                if(!confes.itemsPerPage) {
                    confes.itemsPerPage = defaultPerPage;
                }

                // numberOfPages
                confes.numberOfPages = Math.ceil(confes.totalItems/confes.itemsPerPage);

                // 如果分页总数>0，并且当前页大于分页总数
                if(scope.confes.numberOfPages > 0 && scope.confes.currentPage > scope.confes.numberOfPages){
                    scope.confes.currentPage = scope.confes.numberOfPages;
                }

                // 如果itemsPerPage在不在perPageOptions数组中，就把itemsPerPage加入这个数组中
                var perPageOptionsLength = scope.confes.perPageOptions.length;

                // 定义状态
                var perPageOptionsStatus;
                for(var i = 0; i < perPageOptionsLength; i++){
                    if(confes.perPageOptions[i] == confes.itemsPerPage){
                        perPageOptionsStatus = true;
                    }
                }
                // 如果itemsPerPage在不在perPageOptions数组中，就把itemsPerPage加入这个数组中
                if(!perPageOptionsStatus){
                    confes.perPageOptions.push(confes.itemsPerPage);
                }

                // 对选项进行sort
                confes.perPageOptions.sort(function(a, b) {return a - b});
                

                // 页码相关
                scope.pageList = [];
                if(confes.numberOfPages <= confes.pagesLength){
                    // 判断总页数如果小于等于分页的长度，若小于则直接显示
                    for(i =1; i <= confes.numberOfPages; i++){
                        scope.pageList.push(i);
                    }
                }else{
                    // 总页数大于分页长度（此时分为三种情况：1.左边没有...2.右边没有...3.左右都有...）
                    // 计算中心偏移量
                    var offset = (confes.pagesLength - 1) / 2;
                    if(confes.currentPage <= offset){
                        // 左边没有...
                        for(i = 1; i <= offset + 1; i++){
                            scope.pageList.push(i);
                        }
                        scope.pageList.push('...');
                        scope.pageList.push(confes.numberOfPages);
                    }else if(confes.currentPage > confes.numberOfPages - offset){
                        scope.pageList.push(1);
                        scope.pageList.push('...');
                        for(i = offset + 1; i >= 1; i--){
                            scope.pageList.push(confes.numberOfPages - i);
                        }
                        scope.pageList.push(confes.numberOfPages);
                    }else{
                        // 最后一种情况，两边都有...
                        scope.pageList.push(1);
                        scope.pageList.push('...');

                        for(i = Math.ceil(offset / 2) ; i >= 1; i--){
                            scope.pageList.push(confes.currentPage - i);
                        }
                        scope.pageList.push(confes.currentPage);
                        for(i = 1; i <= offset / 2; i++){
                            scope.pageList.push(confes.currentPage + i);
                        }

                        scope.pageList.push('...');
                        scope.pageList.push(confes.numberOfPages);
                    }
                }

                scope.$parent.confes = confes;
            }

            // prevPage
            scope.prevPage = function() {
                if(confes.currentPage > 1){
                    confes.currentPage -= 1;
                }
            };

            // nextPage
            scope.nextPage = function() {
                if(confes.currentPage < confes.numberOfPages){
                    confes.currentPage += 1;
                }
            };

            // 变更当前页
            scope.changeCurrentPage = function(item) {
                
                if(item == '...'){
                    return;
                }else{
                    confes.currentPage = item;
                    getPagination();
                    // confes.onChange()函数
                    if(confes.onChange) {
                        confes.onChange();
                    }
                }
            };

            // 修改每页展示的条数
            scope.changeItemsPerPage = function() {

                // 一发展示条数变更，当前页将重置为1
                confes.currentPage = 1;

                getPagination();
                // confes.onChange()函数
                if(confes.onChange) {
                    confes.onChange();
                }
            };

            // 跳转页
            scope.jumpToPage = function() {
                num = scope.jumpPageNum;
                if(num.match(/\d+/)) {
                    num = parseInt(num, 10);
                
                    if(num && num != confes.currentPage) {
                        if(num > confes.numberOfPages) {
                            num = confes.numberOfPages;
                        }

                        // 跳转
                        confes.currentPage = num;
                        getPagination();
                        // confes.onChange()函数
                        if(confes.onChange) {
                            confes.onChange();
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

            scope.$watch('confes.totalItems', function(value, oldValue) {
                
                // 在无值或值相等的时候，去执行onChange事件
                if(!value || value == oldValue) {
                    
                    if(confes.onChange) {
                        confes.onChange();
                    }
                }
                getPagination();
            })
            
        }
    };
}]);
