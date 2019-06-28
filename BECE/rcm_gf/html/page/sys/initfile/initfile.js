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
        $scope.unDifferent = false;// 查询同步的
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
        /*====查询不同文件名文件列表====*/
        $scope.queryDifferentFiles = function () {
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
                'unSynchronize': $scope.unSynchronize,
                'unDifferent': $scope.unDifferent
            });
            _showLoading("正在查询中，请稍等...");
            $scope.commonAjax(srvUrl + 'initfile/queryDifferentFiles.do', 'post', data, false, function (res) {
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
        /*====更新不同文件名文件列表====*/
        $scope.updateDifferentFiles = function () {
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
            $.confirm('确认更新模块文件吗？', function () {
                _showLoading("数据更新中，请稍等...")
                $scope.commonAjax(srvUrl + 'initfile/updateDifferentFiles.do', 'post', data, false, function (res) {
                    if (res.success) {
                        $.alert('更新完成！');
                        $scope.initFiles = res.data;
                        _hideLoading();
                    } else {
                        _hideLoading();
                        $.alert(res.data);
                    }
                });
            });
        };
        $scope.updateDifferentFile = function (file) {
            $.confirm('确认更新文件吗？', function () {
                _showLoading("文件更新中，请稍等...")
                $scope.commonAjax(srvUrl + 'initfile/updateDifferentFile.do', 'post', file, false, function (res) {
                    if (res.success) {
                        $.alert('更新完成！');
                        $scope.queryDifferentFiles();
                        _hideLoading();
                    } else {
                        _hideLoading();
                        $.alert(res.data);
                    }
                });
            });
        };
        /*================================================================在途数据end===============================*/
        /*================================================================上会附件start===============================*/
        $scope.meetingFiles = [];
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
                'fileTable': 'rcm_pre_info'
            }
        ];
        /*======查询上会附件=====*/
        $scope.queryMeetingFiles = function () {
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
                'skip': $scope.meetingSkip
            });
            console.log(data);
            $scope.commonAjax(srvUrl + 'initfile/queryMeetingFiles.do', 'post', data, false, function (res) {
                if (res.success) {
                    $scope.meetingFiles = res.data;
                    $scope.getMeetingCount();
                } else {
                    $.alert(res.data);
                }
            });
        };
        /*======同步上会附件-多个=====*/
        $scope.executeMeetingFiles = function () {
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
            $scope.commonAjax(srvUrl + 'initfile/executeMeetingFiles.do', 'post', data, false, function (res) {
                if (res.success) {
                    $.alert('同步完成！');
                    $scope.meetingFiles = res.data;
                } else {
                    $.alert(res.data);
                }
            });
        };
        /*======同步上会附件-单个=====*/
        $scope.executeMeetingFile = function (meeting) {
            $scope.commonAjax(srvUrl + 'initfile/executeMeetingFile.do', 'post', meeting, false, function (res) {
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
        /*====获取上会附件数量信息====*/
        $scope.getMeetingCount = function () {
            if ($scope.meetingType > -1) {
                var data = {};
                data.meeting = JSON.stringify($scope.meetingTypes[$scope.meetingType]);
                $scope.commonAjax(srvUrl + 'initfile/queryMeetingFiles.do', 'post', data, false, function (res) {
                    if (res.success) {
                        $scope.meetingInfo = '当前数量: ' + $scope.meetingFiles.length + ', 实际数量: ' + res.data.length;
                    } else {
                        $.alert(res.data);
                    }
                });
            }
        };
        $scope.open = function (url) {
            $window.open(url);
        };
        /*=======================================================上会附件end==========================================*/
        /*=================文件管理开始==================*/
        /*初始化项目类型*/
        $scope.projectTypes = [];
        $scope.initProjectTypes = function () {
            $.getJSON("page/sys/initfile/projecttypes.json", function (data) {
                $scope.projectTypes = data;
            });
        };
        $scope.initProjectTypes();
        /*初始化查询条件*/
        $scope.projectType = '';
        $scope.projectName = '';
        $scope.projectNumber = '';
        $scope.projectId = '';
        $scope.replaceReason = '';
        $scope.projectFile = {};
        /*清空查询条件*/
        $scope.clearQueryObj = function () {
            $scope.projectType = '';
            $scope.projectName = '';
            $scope.projectNumber = '';
            $scope.projectId = '';
            $scope.queryFileListByPage();
        };
        /*创建查询条件*/
        $scope.crateQueryObj = function () {
            $scope.fileManagerConfiguration.queryObj.projectType = $scope.projectType;
            $scope.fileManagerConfiguration.queryObj.projectName = $scope.projectName;
            $scope.fileManagerConfiguration.queryObj.projectNumber = $scope.projectNumber;
            $scope.fileManagerConfiguration.queryObj.projectId = $scope.projectId;
        };
        /*分页查询*/
        $scope.queryFileListByPage = function () {
            $scope.crateQueryObj();
            $http({
                method: 'post',
                url: srvUrl + "initfile/queryFileListByPage.do",
                data: $.param({
                    "page": JSON.stringify($scope.fileManagerConfiguration)
                })
                , async: false
            }).success(function (data) {
                $scope.fileManagerConfiguration.totalItems = data['result_data'].totalItems;
                $scope.projectFiles = data['result_data'].list;
            });
        };
        /*初始化分页条件*/
        $scope.fileManagerConfiguration = {};
        $scope.fileManagerConfiguration.queryObj = {};
        $scope.fileManagerConfiguration.itemsPerPage = 5;
        $scope.fileManagerConfiguration.perPageOptions = [5, 10, 15, 20, 25, 30];
        /*分页监听*/
        $scope.$watch('fileManagerConfiguration.currentPage + fileManagerConfiguration.itemsPerPage', $scope.queryFileListByPage);
        /*文件预览*/
        $scope.cloudPreview = function (fullPath) {
            $.ajax({
                url: srvUrl + 'cloud/getUrl.do',
                type: "POST",
                dataType: "json",
                data: {"type": 'preview', 'path': fullPath},
                async: true,
                success: function (data) {
                    if (!isEmpty(data)) {
                        $window.open(data);
                    }
                }
            });
        };
        /*替换文件*/
        $scope.replaceFile = function (replaceFile, projectFile) {
            var docType = projectFile['fileTypeCode'];
            var docCode = projectFile['projectId'];
            var pageLocation = projectFile['pageLocation'];
            var oldFileId = projectFile['cloudFileId'];
            var oldFileName = projectFile['cloudFileName'];
            var replaceReason = '管理员替换';
            if (!isEmpty($scope.replaceReason)) {
                replaceReason = $scope.replaceReason;
            }
            $scope.executeReplace(replaceFile, docType, docCode, pageLocation, oldFileId, oldFileName, replaceReason, function (data) {
                $scope.updateRelationResources(replaceFile, projectFile);
            });
        };
        /*
         * 替换文件后对Mongo的操作
         * replaceFile:替换的文件
         * projectFile:原始文件信息
         */
        $scope.updateRelationResources = function (replaceFile, projectFile) {
            show_Mask();
            // 只针对相关资源的操作
            var docType = projectFile['fileTypeCode'];
            var docCode = projectFile['projectId'];
            var pageLocation = projectFile['pageLocation'];
            var cloudFileId = projectFile['cloudFileId'];
            var addUrl = '';
            if (docType == 'preReview') {
                addUrl = "preInfoCreate/addAttachmengInfoToMongo.do";
            } else if (docType == 'formalReview') {
                addUrl = "formalAssessmentInfoCreate/addAttachmengInfoToMongo.do";
            } else if (docType == 'bulletin') {
                addUrl = "bulletinInfo/addAttachmengInfoToMongo.do";
            }
            if (!isEmpty(addUrl)) {
                // 查询替换了的文件信息
                var fileList = attach_list(docType, docCode, pageLocation)['result_data'];
                if (!isEmpty(fileList) && fileList.length > 0) {
                    var newFile = fileList[0];
                    // 从Mongo中获取附件列表信息
                    $scope.getFileListFromMongo(docType, docCode, function (list) {
                        // 通过原始附件id来判断到底替换的是哪一个附件
                        var item = null;
                        if (!isEmpty(list) && list.length > 0) {
                            for (var i = 0; i < list.length; i++) {
                                var js = list[i];
                                if (js['fileId'] == cloudFileId) {
                                    item = js;
                                    break;
                                }
                            }
                        }
                        if (!isEmpty(item)) {
                            // 更新附件变量信息
                            item.fileId = newFile['fileid'] + '';
                            item.lastUpdateBy = {
                                NAME: $scope.credentials['userName'],
                                VALUE: $scope.credentials['UUID']
                            };
                            item.lastUpdateData = getDate();
                            item.fileName = replaceFile['name'];
                            // 修改Mongo中相关数据
                            $http({
                                method: 'post',
                                url: srvUrl + addUrl,
                                data: $.param({
                                    "json": JSON.stringify({
                                        "businessId": docCode,
                                        "item": item,
                                        "oldFileName": replaceFile['name']
                                    })
                                })
                            }).success(function (data) {
                                if (data.success) {
                                    $.alert('替换成功！');
                                    $scope.queryFileListByPage();
                                    $scope.replaceDialogClose();
                                } else {
                                    $.alert(data.result_name);
                                }
                            });
                        }
                    });
                }
            } else {
                $.alert('替换成功！');
                $scope.queryFileListByPage();
                $scope.replaceDialogClose();
            }
            hide_Mask();
        };
        /*从Mongo中查询原始附件信息*/
        $scope.getFileListFromMongo = function (docType, docCode, callBack) {
            $.ajax({
                url: srvUrl + 'initfile/getFileListFromMongo.do',
                type: "POST",
                data: {
                    'docType': docType,
                    'docCode': docCode
                },
                async: true,
                success: function (data) {
                    if (data.success) {
                        if (!isEmpty(callBack) && typeof callBack == 'function') {
                            callBack(data['result_data']);
                        }
                    } else {
                        $.alert(data.result_name);
                    }
                }
            });
        };
        /*执行替换文件*/
        $scope.executeReplace = function (replaceFile, docType, docCode, pageLocation, oldFileId, oldFileName, replaceReason, successBack) {
            Upload.upload({
                url: srvUrl + 'cloud/replace.do',
                data: {
                    file: replaceFile,
                    "docType": docType,
                    'docCode': docCode,
                    'pageLocation': pageLocation,
                    "oldFileId": oldFileId,
                    "oldFileName": oldFileName,
                    "reason": replaceReason

                }
            }).then(function (resp) {
                if (resp.data['result_code'] == 'S') {
                    if (!isEmpty(successBack) && typeof successBack == 'function') {
                        successBack(resp.data);
                    }
                } else {
                    $.alert(resp.data['result_name']);
                }
            }, function (resp) {
            }, function (evt) {
            });
        };
        /*查看项目文件*/
        $scope.showProjectFile = function (file) {
            var url = '';
            var fileTypeCode = file['fileTypeCode'];
            var projectId = file['projectId'];
            var returnUrl = 'index.html#/initfile';
            if ('formalReview' == fileTypeCode ||
                'FormalReportInfo' == fileTypeCode ||
                'FormalDecisionDraftInfo' == fileTypeCode ||
                'FormalBiddingInfo' == fileTypeCode) {
                url += "#/projectInfoAllBoardView/";
            } else if ('preReview' == fileTypeCode ||
                'preReportInfo' == fileTypeCode) {
                url += "#/projectPreInfoAllBoardView/"
            } else {
                url += "#/projectBulletinInfoAllBoardView/"
            }
            url += projectId + "/" + $filter('encodeURI')(returnUrl);
            $window.open(url);
        };
        /*关闭弹窗*/
        $scope.replaceDialogClose = function () {
            $('#replaceDialog').modal('hide');
            $scope.replaceReason = '';
            $scope.projectFile = {};
        };
        /*打开弹窗*/
        $scope.replaceDialogOpen = function (projectFile) {
            $('#replaceDialog').modal('show');
            // 给每一行原始文件赋值
            $scope.projectFile = projectFile;
        }
        /*=================文件管理结束==================*/
    }]);
