ctmApp.register.controller('OtherBiddingInfoPreview', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        // 预览传来的参数
        $scope.changeValue = 1;   // 标识切换的页面

        // 待决策项目审阅传来的参数
        $scope.waitId = $routeParams.id;
        $scope.flag = $routeParams.flag;

        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
            console.log(attach_list("formalReview", id, "formalAssessmentInfo"))
            $scope.newAttachment = attach_list("formalReview", id, "formalAssessmentInfo").result_data;
            for(var i in attachment){
                var file = attachment[i];
                for (var j in $scope.newAttachment){
                    if (file.fileId == $scope.newAttachment[j].fileid){
                        $scope.newAttachment[j].fileName = file.fileName;
                        $scope.newAttachment[j].type = file.type;
                        $scope.newAttachment[j].itemType = file.itemType;
                        $scope.newAttachment[j].programmed = file.programmed;
                        $scope.newAttachment[j].approved = file.approved;
                        $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                        $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                        $scope.newAttachment[j].isMettingAttachment = file.isMettingAttachment;
                        break;
                    }
                }
            }
            console.log($scope.newAttachment)
        };

        // 待决策项目传阅 初始化数据
        $scope.initUpdate = function (id) {
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

                // 处理附件
                $scope.reduceAttachment(data.result_data.Formal.attachmentList, id);

                //新增附件类型
                $scope.attach = data.result_data.attach;
                console.log($scope.attach)
                // 获取分数数据
                $scope.getMarks();
            })
        }

        $scope.initUpdate($scope.waitId);

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
            $scope.formalPreview = {};
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
            /*$scope.formalPreview.fileList = $scope.formalReport.policyDecision.fileList*/
            $scope.formalPreview.mark = $scope.mark // 分数
            console.log($scope.formalPreview);
        }

        // 滑动切换时，上面的过程跟着切换
        $scope.changeStyle = function (num) {
            var tabId = ['processReview', 'otherReport', 'score'];
            angular.forEach(tabId, function (data, index, array) {
                if (index != num) {
                    angular.element("#" + data).removeClass('chose');
                } else {
                    angular.element("#" + data).addClass('chose');
                }
            });
        }

        // 展开展示信息
        $scope.expandMore = function (parentId, val) {
            angular.element("#" + parentId).addClass('hideOpen');
            $scope[val] = true;
            angular.element("")
        }

    }]
);