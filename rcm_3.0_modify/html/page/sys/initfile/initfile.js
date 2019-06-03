ctmApp.register.controller('initFileCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        $scope.isLimit = false;
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
        /*======模块同步查询======*/
        $scope.querySynchronize = function () {
            if (!$scope.validateFileTypesIndex()) {
                return;
            }
            ;
            if (!$scope.validateCondition()) {
                return;
            }
            ;
            var data = {};
            data.list = JSON.stringify([$scope.fileTypes[$scope.fileTypesIndex]]);
            data.condition = JSON.stringify({
                'limit': $scope.fileLimit,
                'skip': $scope.fileSkip
            });
            _showLoading("正在查询中，请稍等...");
            $scope.commonAjax(srvUrl + 'initfile/querySynchronize.do', 'post', data, false, function (res) {
                if (res.success) {
                    $scope.initFiles = res.data;
                    if (!isEmpty($scope.initFiles) && $scope.initFiles.length > 0) {
                        $scope.info = '当前数量 ' + $scope.initFiles.length + ' 实际数量 ' + $scope.initFiles[0].total;
                    } else {
                        $scope.info = '当前数量 0 实际数量 0';
                    }
                } else {
                    $.alert(res.data);
                }
                _hideLoading();
            });
        };
        /*======单个同步======*/
        $scope.executeSynchronize = function (file) {
            $.confirm('确认同步该文件吗？', function(){
                _showLoading("数据同步中，请稍等...");
                $scope.commonAjax(srvUrl + 'initfile/executeSynchronize.do', 'post', file, false, function (res) {
                    if (res.success) {
                        $.alert('同步完成！');
                        $scope.querySynchronize();
                    } else {
                        $.alert(res.data);
                    }
                    _hideLoading();
                });
            });
        };
        /*======模块同步======*/
        $scope.executeSynchronizeModule = function () {
            if (!$scope.validateFileTypesIndex()) {
                return;
            }
            if ($scope.isLimit) {
                if (!$scope.validateCondition()) {
                    return;
                }
            }
            var data = {};
            data.list = JSON.stringify([$scope.fileTypes[$scope.fileTypesIndex]]);
            if ($scope.isLimit) {
                data.condition = JSON.stringify({
                    'limit': $scope.fileLimit,
                    'skip': $scope.fileSkip
                });
            }
            $.confirm('确认同步模块文件吗？', function(){
                _showLoading("数据同步中，请稍等...")
                $scope.commonAjax(srvUrl + 'initfile/executeSynchronizeModule.do', 'post', data, false, function (res) {
                    if (res.success) {
                        $.alert('同步完成！');
                        $scope.querySynchronize();
                    } else {
                        $.alert(res.data);
                    }
                    _hideLoading();
                });
            });
        };
        /*====条件校验====*/
        $scope.validateFileTypesIndex = function () {
            if ($scope.fileTypesIndex == -1) {
                $('#fileTypesIndex').focus();
                return false;
            }
            return true;
        };
        $scope.validateCondition = function () {
            if (isEmpty($('#Skip').val())) {
                $('#Skip').focus();
                return false;
            }
            if (isEmpty($('#Limit').val())) {
                $('#Limit').focus();
                return false;
            }
            return true;
        };
    }]);