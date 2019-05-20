ctmApp.register.controller('OtherBidding', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout',
    function ($http, $scope, $location, $routeParams, Upload, $timeout) {
        //初始化
        $scope.oldUrl = $routeParams.url;
        $scope.tabIndex = $routeParams.tabIndex;
        $scope.controller_val = $location.$$url.split("/")[1];
        var complexId = $routeParams.id;
        $scope.paramId = complexId;
        var params = complexId.split("@");
        if (null != params[1] && "" != params[1]) {
            $scope.flag = params[1];
        }
        if (null != params[2] && "" != params[2] && null != params[3] && "" != params[3] && null != params[4] && "" != params[4] && null != params[5] && "" != params[5] && null != params[6] && "" != params[6]) {
            $scope.reportReturnId = params[2] + "@" + params[3] + "@" + params[4] + "@" + params[5] + "@" + params[6];
        }
        var objId = params[0];
        $scope.formalReport = {};
        $scope.projectSummary = {};

        var action = $routeParams.action;

        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
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
        };

        // 初始化提交决策会材料数据
        $scope.initUpdate = function (projectFormalId) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/findFormalAndReport.do",
                data: $.param({"projectFormalId": projectFormalId})
            }).success(function (data) {
                $scope.formalReport = data.result_data.Report;
                $scope.pfr = data.result_data.Formal;
                // 处理附件需要的数据
                $scope.projectModel = angular.copy($scope.pfr.apply.projectModel);
                $scope.serviceType = angular.copy($scope.pfr.apply.serviceType);
                // 处理附件
                $scope.reduceAttachment(data.result_data.Formal.attachmentList, projectFormalId);
                hide_Mask();
            })
        }

        // 验证附件
        $scope.validateVoidFile = function () {
            for (var i in $scope.newAttachment) {
                if ($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == '') {
                    return false;
                }
            }
            return true;
        }

        $scope.initData = function () {
            $scope.initUpdate(objId);
            if (action == "Create") {
                $scope.title = "正式评审决策会材料-新增";
                $scope.isEdite = true;
                // $scope.getProjectFormalReviewByID(objId);
                $scope.formalReport.create_by = $scope.credentials.UUID;
                $scope.formalReport.create_name = $scope.credentials.userName;
                $("#wordbtn").hide();
            } else if (action == "Update") {
                $scope.isEdite = true;
                $scope.title = "正式评审决策会材料-修改";
                // $scope.getSummaryPPTByID(objId);
            } else if (action == "View") {
                $scope.title = "正式评审决策会材料-查看";
                // $scope.getSummaryPPTByID(objId);
                $scope.isEdite = false;
            }
            console.log($scope.isEdite)
        };

        $scope.getSummaryPPTByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + 'formalReport/getSummaryPPTByID.do',
                data: $.param({"businessId": id, "type": "pfr"})
            }).success(function (data) {
                $scope.formalReport = data.result_data.project;
                $scope.formalReport.projectName = $scope.formalReport.PROJECTNAME;
                $scope.formalReport.projectFormalId = $scope.formalReport.BUSINESSID;
                $scope.projectSummary = data.result_data.summary;
                if (action == "View") {
                    $('button').attr("disabled", "disabled");
                    $("#submitbnt").attr("disabled", false);
                    $('#wordbtn').attr("disabled", false);
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        $scope.getProjectFormalReviewByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/getProjectFormalReviewByID.do",
                data: $.param({"id": id})
            }).success(function (data) {
                $scope.pfr = data.result_data;
                $scope.formalReport.projectFormalId = $scope.pfr.id;
                $scope.formalReport.projectName = $scope.pfr.apply.projectName;
                $scope.formalReport.projectNo = $scope.pfr.apply.projectNo;
                if (null != $scope.pfr.apply.reportingUnit) {
                    $scope.formalReport.reportingUnit = $scope.pfr.apply.reportingUnit.name;
                }
                var ptNameArr = [], pmNameArr = [];
                var pt = $scope.pfr.apply.projectType;
                if (null != pt && pt.length > 0) {
                    for (var i = 0; i < pt.length; i++) {
                        ptNameArr.push(pt[i].VALUE);
                    }
                    $scope.formalReport.projectTypeName = ptNameArr.join(",");
                }
                $scope.formalReport.controllerVal = $scope.controller_val;
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        $scope.initData();

        $scope.saveOnly = function () {
            //验证空附件
            if (!$scope.validateVoidFile()) {
                $.alert("附件不能为空！");
                return;
            }

            var data = $scope.dataForSave();
            console.log(data)
            // $scope.saveOrSubmit(data, "so");
        };

        $scope.submitSave = function () {
            var data = $scope.dataForSave();
            $scope.saveOrSubmit(data, "ss");
        };

        $scope.dataForSave = function () {

            var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);
            console.log(newAttachment);
            if (newAttachment == false) {
                return false;
            }

            var chk_list = $("input[name='choose']");
            var fid = "";
            var uuidarr = [], itemarr = [], programmedarr = [], approvedarr = [], fileNamearr = [], filePatharr = [],
                versionarr = [], upload_datearr = [], programmeIddarr = [], approvedIdarr = [];
            for (var i = 0; i < chk_list.length; i++) {
                if (chk_list[i].checked) {
                    fid = chk_list[i].value;
                    var arrfid = fid.split("||");
                    if (arrfid[0] == null || arrfid[0] == "") {
                        uuidarr.push($scope.newAttachment[i].newItem.UUID);
                    } else {
                        uuidarr.push(arrfid[0]);
                    }
                    if (arrfid[1] == null || arrfid[1] == "") {
                        itemarr.push($scope.newAttachment[i].newItem.ITEM_NAME);
                    } else {
                        itemarr.push(arrfid[1]);
                    }
                    programmedarr.push(arrfid[2]);
                    approvedarr.push(arrfid[3]);
                    fileNamearr.push(arrfid[4]);
                    filePatharr.push(arrfid[5]);
                    versionarr.push(arrfid[6]);
                    upload_datearr.push(arrfid[7]);
                    programmeIddarr.push(arrfid[8]);
                    approvedIdarr.push(arrfid[9]);
                }
            }

            var newFiles = $("input[name='choosem']")
            for (var i = 0; i < newFiles.length; i++) {
                if (newFiles[i].checked) {
                    fid = newFiles[i].value;
                    var arrfid = fid.split("||");
                    uuidarr.push(arrfid[0]);
                    itemarr.push(arrfid[1]);
                    programmedarr.push(arrfid[2]);
                    approvedarr.push(arrfid[3]);
                    fileNamearr.push(arrfid[4]);
                    filePatharr.push(arrfid[5]);
                    versionarr.push(arrfid[6]);
                    upload_datearr.push(arrfid[7]);
                    programmeIddarr.push(arrfid[8]);
                    approvedIdarr.push(arrfid[9]);
                }
            }

            var array = [];
            if (undefined == $scope.formalReport.policyDecision) {
                $scope.formalReport.policyDecision = {};
            }
            $scope.formalReport.policyDecision.submitName = $scope.credentials.userName;
            $scope.formalReport.policyDecision.submitDate = $scope.FormatDate();
            if (undefined == $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles) {
                $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles = [];
            }

            for (var j = 0; j < fileNamearr.length; j++) {
                $scope.vvvv = {};
                $scope.vvvv.UUID = uuidarr[j];
                $scope.vvvv.ITEM_NAME = itemarr[j];
                $scope.vvvv.programmed = programmedarr[j];
                $scope.vvvv.approved = approvedarr[j];
                $scope.vvvv.fileName = fileNamearr[j];
                $scope.vvvv.filePath = filePatharr[j];
                $scope.vvvv.version = versionarr[j];
                if (upload_datearr[j] == "") {
                    upload_datearr[j] = $scope.getDate();
                }
                $scope.vvvv.upload_date = upload_datearr[j];
                $scope.vvvv.programmedId = programmeIddarr[j];
                $scope.vvvv.approvedID = approvedIdarr[j];
                array.push($scope.vvvv);
            }
            $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles = array;

            $scope.formalReport.ac_attachment = $scope.pfr.attachment;

            $scope.formalReport.projectFormalId = objId; // 正式评审项目id
            // $scope.formalReport.projectSummary = $scope.projectSummary;
            return $scope.formalReport;
        };

        $scope.saveOrSubmit = function (pData, method) {
            console.log(pData);
            show_Mask();
            $http({
                method: 'post',
                url: srvUrl + "formalReport/addPptecision.do",
                data: $.param({"json": angular.toJson(pData), "method": method})
            }).success(function (data) {
                hide_Mask();
                if (data.success) {
                    var alertData = "";
                    if (method == "so") {
                        alertData = "保存成功!";
                    } else if (method == "ss") {
                        if (data.result_data) {
                            alertData = "提交成功!";
                            $("#savebtn").hide();
                            $("#btnfile").attr("disabled", "disabled");
                            // $location.path("/FormalBiddingInfoPreview/" + $scope.businessId + "/" + $filter('encodeURI') + "/2");
                        } else {
                            $.alert("请确保参会信息已填写完毕!");
                            return false;
                        }
                    }
                    $.alert(alertData);
                } else {
                    $.alert(data.result_name);
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }


    }]);
