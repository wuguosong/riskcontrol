ctmApp.register.controller('initFileCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        /*========================================================在途数据start=======================================*/
        $scope.unSynchronize = false;// 仅查询同步的
        $scope.isLimit = false;// 仅查询当前页面的
        $scope.fileSkip = null;// 从
        $scope.fileLimit = null;// 至
        $scope.fileTypes = [];// 附件来源
        $scope.fileTypesIndex = -1;// 附件来源下标
        $scope.initFiles = [];// 查询结果列表
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
            if (!$scope.validateCondition()) {
                return;
            }
            var data = {};
            data.list = JSON.stringify([$scope.fileTypes[$scope.fileTypesIndex]]);
            data.condition = JSON.stringify({
                'limit': $scope.fileLimit,
                'skip': $scope.fileSkip,
                'unSynchronize': $scope.unSynchronize
            });
            _showLoading("正在查询中，请稍等...");
            $scope.commonAjax(srvUrl + 'initfile/querySynchronize.do', 'post', data, false, function (res) {
                if (res.success) {
                    _hideLoading();
                    $scope.initFiles = res.data;
                    $scope.setCountInfo();
                } else {
                    _hideLoading();
                    $.alert(res.data);
                }
            });
        };
        /*======单个同步======*/
        $scope.executeSynchronize = function (file) {
            $.confirm('确认同步该文件吗？', function () {
                _showLoading("数据同步中，请稍等...");
                $scope.commonAjax(srvUrl + 'initfile/executeSynchronize.do', 'post', file, false, function (res) {
                    if (res.success) {
                        _hideLoading();
                        $scope.querySynchronize();
                        $.alert('同步完成！');
                    } else {
                        _hideLoading();
                        $.alert(res.data);
                    }
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
            $.confirm('确认同步模块文件吗？', function () {
                _showLoading("数据同步中，请稍等...")
                $scope.commonAjax(srvUrl + 'initfile/executeSynchronizeModule.do', 'post', data, false, function (res) {
                    if (res.success) {
                        $.alert('同步完成！');
                        $scope.querySynchronize();
                        _hideLoading();
                    } else {
                        _hideLoading();
                        $.alert(res.data);
                    }
                });
            });
        };
        /*====条件校验:来源====*/
        $scope.validateFileTypesIndex = function () {
            if ($scope.fileTypesIndex == -1) {
                $('#fileTypesIndex').focus();
                return false;
            }
            return true;
        };
        /*====条件校验:Skip和Limit====*/
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
        /*====设置数量信息====*/
        $scope.setCountInfo = function () {
            if ($scope.fileTypesIndex != -1) {
                var data = {};
                data.list = JSON.stringify([$scope.fileTypes[$scope.fileTypesIndex]]);
                $scope.commonAjax(srvUrl + 'initfile/queryMongo.do', 'post', data, false, function (res) {
                    if (res.success) {
                        $scope.info = '当前数量: ' + $scope.initFiles.length + ', 实际数量: ' + res.data.length;
                    }
                });
            }
        };
        /*================================================================在途数据end===============================*/
        /*================================================================上会附件start===============================*/
        $scope.meetingFiles = [];
        $scope.meetingUnSynchronize = false;// 仅查询同步的
        $scope.meetingIsLimit = false;// 仅查询当前页面的
        $scope.meetingSkip = null;// 从
        $scope.meetingLimit = null;// 至
        $scope.meetingType = -1;
        $scope.meetingTypes = [
            {
                'dataName': '正式评审上会附件',
                'dataTable': 'rcm_formalAssessment_info',
                'fileTable': 'rcm_formalReport_info'
            },
            {
                'dataName': '投标评审上会附件',
                'dataTable': 'rcm_pre_info',
                'fileTable': 'rcm_formalReport_info'
            }
        ];
        /*======查询上会附件=====*/
        $scope.queryMeetingSynchronize = function () {
            if (!$scope.validateMeetingType()) {
                return;
            }
            if (!$scope.validateMeetingCondition()) {
                return;
            }
            var data = {};
            data.meeting = JSON.stringify($scope.meetingTypes[$scope.meetingType]);
            data.condition = JSON.stringify({
                'limit': $scope.meetingLimit,
                'skip': $scope.meetingSkip,
                'unSynchronize': $scope.meetingUnSynchronize
            });
            console.log(data);
            $scope.commonAjax(srvUrl + 'initfile/queryMeetingSynchronize.do', 'post', data, false, function (res) {
                if (res.success) {
                    $scope.meetingFiles = res.data;
                } else {
                    $.alert(res.data);
                }
            });
        };
        /*======同步上会附件=====*/
        $scope.executeMeetingSynchronize = function () {
            if (!$scope.validateMeetingType()) {
                return;
            }
            if ($scope.meetingIsLimit) {
                if (!$scope.validateMeetingCondition()) {
                    return;
                }
            }
            var data = {};
            data.meeting = JSON.stringify($scope.meetingTypes[$scope.meetingType]);
            if ($scope.meetingIsLimit) {
                data.condition = JSON.stringify({
                    'limit': $scope.meetingLimit,
                    'skip': $scope.meetingSkip
                });
            }
            console.log(data);
            $scope.commonAjax(srvUrl + 'initfile/executeMeetingSynchronize.do', 'post', data, false, function (res) {
                if (res.success) {
                    $.alert('同步完成！');
                } else {
                    $.alert(res.data);
                }
            });
        };
        /*====条件校验:来源====*/
        $scope.validateMeetingType = function () {
            if ($scope.meetingType == -1) {
                $('#meetingType').focus();
                return false;
            }
            return true;
        };
        /*====条件校验:Skip和Limit====*/
        $scope.validateMeetingCondition = function () {
            if (isEmpty($('#meetingSkip').val())) {
                $('#meetingSkip').focus();
                return false;
            }
            if (isEmpty($('#meetingLimit').val())) {
                $('#meetingLimit').focus();
                return false;
            }
            return true;
        };
        $scope.getMeetingCount = function(){
            if ($scope.meetingType > -1) {
                var data = {};
                data.meeting = JSON.stringify($scope.meetingTypes[$scope.meetingType]);
                $scope.commonAjax(srvUrl + 'initfile/queryMeetingSynchronize.do', 'post', data, false, function (res) {
                    if (res.success) {
                        $scope.meetingInfo = '当前数量: ' + $scope.meetingFiles.length + ', 实际数量: ' + res.data.length;
                    } else {
                        $.alert(res.data);
                    }
                });
            }
        };
        /*=======================================================上会附件end==========================================*/
    }]);