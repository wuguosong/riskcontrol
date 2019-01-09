ctmApp.register.controller('FormalBiddingInfoPreview', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        // 预览传来的参数
        $scope.formalPreview = $routeParams.formalPreview;
        $scope.changeValue = 1;   // 标识切换的页面
        $scope.hideFlagR = false; // 正式评审报告隐藏标识
        $scope.hideFlagS = false; // 项目整体评分隐藏标识

        // 待决策项目审阅传来的参数
        $scope.waitId = $routeParams.id;
        // $scope.waitId = '5af296721063f4211810dc58';
        $scope.waitUrl = $routeParams.url;
        $scope.flag = $routeParams.flag;

        $scope.isWaitDecisionReviewList = function () {
            if ($scope.flag != undefined){
                $scope.initData();
            }
        }

        // 待决策项目传阅 初始化数据
        $scope.initData = function () {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/findFormalAndReport.do",
                data: $.param({"projectFormalId": $scope.waitId})
            }).success(function (data) {
                $scope.formalReport = data.result_data.Report;
                $scope.pfr = data.result_data.Formal;
                $scope.meetInfo = data.result_data.MeetInfo;
                $scope.applyDate = data.result_data.applyDate;
                $scope.stage = data.result_data.stage;
                $scope.projectSummary = data.result_data.summary;

                if ($scope.projectSummary == null || $scope.projectSummary == undefined){
                    $location.path("/FormalBiddingInfo_view/"+ $scope.waitId +"@view/" + $scope.waitUrl);
                    return;
                }

                //处理附件列表
                $scope.reduceAttachment(data.result_data.Formal.attachment);
                //新增附件类型
                $scope.attach = data.result_data.attach;
                //控制新增文件
                $scope.newPfr = data.result_data.Formal;
                $scope.formalID = $scope.formalReport.projectFormalId;
                var ptNameArr = [], fgNameArr = [], fgValueArr = [], investmentaNameArr = [];
                var pt = $scope.pfr.apply.projectType;
                if (null != pt && pt.length > 0) {
                    for (var i = 0; i < pt.length; i++) {
                        ptNameArr.push(pt[i].VALUE);
                    }
                    $scope.pfr.apply.projectType = ptNameArr.join(",");
                }

                // 获取分数数据
                $scope.getMarks();

                /*// 处理数据
                $scope.previewJson();*/

                $(".swiper-button-prev").addClass('noArrow');
                $(".swiper-button-next").addClass('noArrow');
            }).error(function (data, status, header, config) {
                $.alert(status);
            });
        }

        //处理附件列表
        $scope.reduceAttachment = function (attachment) {
            $scope.newAttachment = [];
            for (var i in attachment) {
                var files = attachment[i].files;
                if (files != null) {
                    var item_name = attachment[i].ITEM_NAME;
                    var uuid = attachment[i].UUID;
                    for (var j in files) {
                        files[j].ITEM_NAME = item_name;
                        files[j].UUID = uuid;
                        $scope.newAttachment.push(files[j]);
                    }
                }
            }
        }

        // 获取分数
        $scope.getMarks = function () {
            $http({
                method: 'post',
                url: srvUrl + "formalMark/queryMarks.do",
                data: $.param({"businessId": $scope.waitId})
            }).success(function (result) {
                if (result.success) {
                    var mark = result.result_data;
                    if (mark != null && mark != '') {
                        $scope.mark = {};
                        if (mark.FLOWMARK != null && mark.FLOWMARK != '') {
                            $scope.mark.flowMark = mark.FLOWMARK;
                        }
                        if (mark.FLOWMARKREASON != null && mark.FLOWMARKREASON != '') {
                            $scope.mark.flowMarkReason = mark.FLOWMARKREASON;
                        }
                        if (mark.FILECOPY != null && mark.FILECOPY != '') {
                            $scope.mark.fileCopy = mark.FILECOPY;
                        }
                        if (mark.FILECOPYREASON != null && mark.FILECOPYREASON != '') {
                            $scope.mark.fileCopyReason = mark.FILECOPYREASON;
                        }
                        if (mark.FILETIME != null && mark.FILETIME != '') {
                            $scope.mark.fileTime = mark.FILETIME;
                        }
                        if (mark.FILETIMEREASON != null && mark.FILETIMEREASON != '') {
                            $scope.mark.fileTimeReason = mark.FILETIMEREASON;
                        }
                        if (mark.FILECONTENT != null && mark.FILECONTENT != '') {
                            $scope.mark.fileContent = mark.FILECONTENT;
                        }
                        if (mark.FILECONTENTREASON != null && mark.FILECONTENTREASON != '') {
                            $scope.mark.fileContentReason = mark.FILECONTENTREASON;
                        }
                        if (mark.MONEYCALCULATE != null && mark.MONEYCALCULATE != '') {
                            $scope.mark.moneyCalculate = mark.MONEYCALCULATE;
                        }
                        if (mark.MONEYCALCULATEREASON != null && mark.MONEYCALCULATEREASON != '') {
                            $scope.mark.moneyCalculateReason = mark.MONEYCALCULATEREASON;
                        }
                        if (mark.REVIEWFILEACCURACY != null && mark.REVIEWFILEACCURACY != '') {
                            $scope.mark.reviewFileAccuracy = mark.REVIEWFILEACCURACY;
                        }
                        if (mark.REVIEWFILEACCURACYREASON != null && mark.REVIEWFILEACCURACYREASON != '') {
                            $scope.mark.reviewFileAccuracyReason = mark.REVIEWFILEACCURACYREASON;
                        }
                        if (mark.RISKCONTROL != null && mark.RISKCONTROL != '') {
                            $scope.mark.riskControl = mark.RISKCONTROL;
                        }
                        if (mark.RISKCONTROLREASON != null && mark.RISKCONTROLREASON != '') {
                            $scope.mark.riskControlReason = mark.RISKCONTROLREASON;
                        }
                        if (mark.PLANDESIGN != null && mark.PLANDESIGN != '') {
                            $scope.mark.planDesign = mark.PLANDESIGN;
                        }
                        if (mark.PLANDESIGNREASON != null && mark.PLANDESIGNREASON != '') {
                            $scope.mark.planDesignReason = mark.PLANDESIGNREASON;
                        }
                        if (mark.LEGALFILEACCURACY != null && mark.LEGALFILEACCURACY != '') {
                            $scope.mark.legalFileAccuracy = mark.LEGALFILEACCURACY;
                        }
                        if (mark.LEGALFILEACCURACYREASON != null && mark.LEGALFILEACCURACYREASON != '') {
                            $scope.mark.legalFileAccuracyReason = mark.LEGALFILEACCURACYREASON;
                        }
                        if (mark.TALKS != null && mark.TALKS != '') {
                            $scope.mark.talks = mark.TALKS;
                        }
                        if (mark.TALKSREASON != null && mark.TALKSREASON != '') {
                            $scope.mark.talksReason = mark.TALKSREASON;
                        }
                        if (mark.HEGUITOTALMARK != null && mark.HEGUITOTALMARK != '') {
                            $scope.mark.HEGUITOTALMARK = mark.HEGUITOTALMARK;
                        }
                        if (mark.FILETOTALMARK != null && mark.FILETOTALMARK != '') {
                            $scope.mark.FILETOTALMARK = mark.FILETOTALMARK;
                        }
                        if (mark.HEXINTOTALMARK != null && mark.HEXINTOTALMARK != '') {
                            $scope.mark.HEXINTOTALMARK = mark.HEXINTOTALMARK;
                        }
                        if (mark.ALLTOTALMARK != null && mark.ALLTOTALMARK != '') {
                            var vv = Math.pow(10, 1);
                            $scope.mark.ALLTOTALMARK = Math.round(mark.ALLTOTALMARK * vv) / vv;
                        }
                    }
                    // 处理数据
                    $scope.previewJson();
                } else {
                    $.alert(result.result_name);
                }
            });
        }

        $scope.previewJson = function () {
            $scope.formalPreview = $scope.projectSummary // 存入模板数据
            $scope.formalPreview.applyDate = $scope.applyDate
            $scope.formalPreview.apply = $scope.pfr.apply       // 存入项目信息
            $scope.formalPreview.taskallocation = $scope.pfr.taskallocation
            $scope.formalPreview.cesuanFileOpinion = $scope.pfr.cesuanFileOpinion
            $scope.formalPreview.tzProtocolOpinion = $scope.pfr.tzProtocolOpinion
            $scope.formalPreview.approveAttachment = $scope.pfr.approveAttachment  // 风控中心审批
            $scope.formalPreview.approveLegalAttachment = $scope.pfr.approveLegalAttachment  // 风控中心法律审批
            $scope.formalPreview.submit_date = $scope.formalReport.submit_date   // 评审报告出具时间
            $scope.formalPreview.projectRating = $scope.meetInfo.projectRating  // 评审等级
            $scope.formalPreview.filePath = $scope.formalReport.filePath
            $scope.formalPreview.projectName = $scope.formalReport.projectName
            $scope.formalPreview.fileList = $scope.formalReport.policyDecision.fileList
            $scope.formalPreview.mark = $scope.mark // 分数
        }


        // 文件下载
        $scope.downLoadAttachment = function (projectOverview) {
            var isExists = validFileExists(projectOverview.attachmentFile);
            if (!isExists) {
                $.alert("要下载的文件已经不存在了！");
                return;
            }
            var filePath = projectOverview.attachmentFile, fileName = projectOverview.attachmentValue;
            if (fileName != null && fileName.length > 22) {
                var extSuffix = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0, 22);
                fileName = fileName + extSuffix;
            }
            var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(fileName));
            var a = document.createElement('a');
            a.id = 'tagOpenWin';
            a.target = '_blank';
            a.href = url;
            document.body.appendChild(a);

            var e = document.createEvent('MouseEvent');
            e.initEvent('click', false, false);
            document.getElementById("tagOpenWin").dispatchEvent(e);
            $(a).remove();
        }

        $scope.downLoadFormalBiddingInfoFile = function (filePath, filename) {
            var isExists = validFileExists(filePath);
            if (!isExists) {
                $.alert("要下载的文件已经不存在了！");
                return false;
            }
            if (filename != null && filename.length > 12) {
                filename = filename.substring(0, 12) + "...";
            } else {
                filename = filename.substring(0, filename.lastIndexOf("."));
            }

            if (undefined != filePath && null != filePath) {
                var index = filePath.lastIndexOf(".");
                var str = filePath.substring(index + 1, filePath.length);
                var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(filename + "-正式评审报告.")) + str;

                var a = document.createElement('a');
                a.id = 'tagOpenWin';
                a.target = '_blank';
                a.href = url;
                document.body.appendChild(a);

                var e = document.createEvent('MouseEvent');
                e.initEvent('click', false, false);
                document.getElementById("tagOpenWin").dispatchEvent(e);
                $(a).remove();
            } else {
                $.alert("附件未找到！");
                return false;
            }
        }

        $scope.downLoadBiddingFile = function (idx) {
            var isExists = validFileExists(idx.filePath);
            if (!isExists) {
                $.alert("要下载的文件已经不存在了！");
                return;
            }
            var filePath = idx.filePath, fileName = idx.fileName;
            if (fileName != null && fileName.length > 22) {
                var extSuffix = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0, 22);
                fileName = fileName + extSuffix;
            }

            var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(fileName));
            var a = document.createElement('a');
            a.id = 'tagOpenWin';
            a.target = '_blank';
            a.href = url;
            document.body.appendChild(a);

            var e = document.createEvent('MouseEvent');
            e.initEvent('click', false, false);
            document.getElementById("tagOpenWin").dispatchEvent(e);
            $(a).remove();
        }

        $scope.validateVoidFile = function () {
            for (var i in $scope.newAttachment) {
                if ($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == '') {
                    return false;
                }
            }
            return true;
        }

        // 滑动切换时，上面的过程跟着切换
        $scope.changeStyle = function (num) {
            var tabId = ['processReview', 'template', 'formalReport', 'score'];
            angular.forEach(tabId, function (data, index, array) {
                if (index != num) {
                    angular.element("#"+data).removeClass('chose');
                } else {
                    angular.element("#"+data).addClass('chose');
                }
            });
        }

        // 展开展示信息
        $scope.expandMore = function (parentId, val) {
            debugger
            angular.element("#"+parentId).addClass('hideOpen');
            $scope[val] = true;
            angular.element("")
        }

        // 判断如何获取数据
        $scope.isWaitDecisionReviewList();
    }]
);