ctmApp.register.controller('initFileCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        $scope.fileLimit = null;
        $scope.fileSkip = null;
        $scope.fileTypes = [];
        $scope.fileTypesIndex = -1;
        $scope.initFiles = [];
        $scope.initFileType = function () {
            $.getJSON("page/sys/initfile/initfile.json", function (data) {
                $scope.fileTypes = data;
            });
        };
        $scope.initFileType();
        /*=====公共调用方法=====*/
        $scope.commonAjax = function (url, type, data, async, callback) {
            jQuery.ajax({
                type: type,
                url: url,
                data: data,
                success: function (res) {
                    console.log(res.data);
                    if (!isEmpty(callback) && typeof callback === 'function') {
                        callback(res);
                    }
                },
                error: function () {
                    $.alert("超时或服务器其他错误！");
                    _hideLoading();
                },
                async: async
            });
        };
        /*=====查询Mongo=====*/
        $scope.queryAllMongo = function (url, type, data, async, callback) {
            if (isEmpty(data)) {
                $.alert('请选择在途数据来源！');
            }
            var condition = {
                'limit': $scope.fileLimit,
                'skip': $scope.fileSkip
            };
            console.log('limit:' + $scope.fileLimit + ",skip:" + $scope.fileSkip);
            $scope.commonAjax(url, type, {
                'list': JSON.stringify(data),
                'condition': JSON.stringify(condition)
            }, async, callback);
        };
        $scope.queryMongo = function () {
            _showLoading("正在查询中，请稍等...");
            $scope.queryAllMongo(srvUrl + 'initfile/queryMongo.do', 'post', [$scope.fileTypes[$scope.fileTypesIndex]], false, function (res) {
                if (res.success) {
                    $scope.initFiles = res.data;
                } else {
                    $.alert(res.data);
                }
                _hideLoading();
            });
        };
        /*======查询同步======*/
        $scope.querySynchronize = function () {
            _showLoading("正在查询中，请稍等...");
            $scope.queryAllMongo(srvUrl + 'initfile/querySynchronize.do', 'post', [$scope.fileTypes[$scope.fileTypesIndex]], false, function (res) {
                if (res.success) {
                    $scope.initFiles = res.data;
                } else {
                    $.alert(res.data);
                }
                _hideLoading();
            });
        };
        /*======单个同步======*/
        $scope.executeSynchronize = function (file) {
            _showLoading("数据同步中，请稍等...")
            $scope.commonAjax(srvUrl + 'initfile/executeSynchronize.do', 'post', file, false, function (res) {
                if (res.success) {
                    console.log(res.data);
                } else {
                    $.alert(res.data);
                }
                _hideLoading();
            });
        };
        /*======模块同步======*/
        $scope.executeSynchronizeModule = function () {
            _showLoading("数据同步中，请稍等...")
            $scope.commonAjax(srvUrl + 'initfile/executeSynchronizeModule.do', 'post', {
                'list': JSON.stringify([$scope.fileTypes[$scope.fileTypesIndex]])
            }, false, function (res) {
                if (res.success) {
                    console.log(res.data);
                } else {
                    $.alert(res.data);
                }
                _hideLoading();
            });
        };
    }]);