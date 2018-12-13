ctmApp.register.controller('FormalBiddingInfo', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$filter',
    function ($http, $scope, $location, $routeParams, Upload, $filter) {
        $scope.selectFlag = 'false';
        $scope.oldUrl = $routeParams.url;
        //申请报告ID
        var complexId = $routeParams.id;
        var params = complexId.split("@");
        var objId = params[0];
        $scope.formalReport = {};
        $scope.formalReport.policyDecision = {};

        $scope.getDate = function () {
            var myDate = new Date();
            //获取当前年
            var year = myDate.getFullYear();
            //获取当前月
            var month = myDate.getMonth() + 1;
            //获取当前日
            var date = myDate.getDate();
            var h = myDate.getHours(); //获取当前小时数(0-23)
            var m = myDate.getMinutes(); //获取当前分钟数(0-59)
            var s = myDate.getSeconds();
            var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
            return now;
        }

        $scope.FormatDate = function () {
            var date = new Date();
            var paddNum = function (num) {
                num += "";
                return num.replace(/^(\d)$/, "0$1");
            }
            return date.getFullYear() + "-" + paddNum(date.getMonth() + 1) + "-" + date.getDate();
        }

        //初始化页面所需数据
        $scope.initData = function () {
            $scope.getSelectSUMMARY_TEMPLATE('SUMMARY_TEMPLATE');
            $scope.getSelectINVESTMENT_TYPE('INVESTMENT_TYPE');
            $scope.getByID(objId);
            $scope.getMarks(objId);
            $scope.getSelectTypeByCode("8");
            $scope.getSelectTypeByCodetype('14');
        }
        $scope.saveMarks = function () {
            if ($scope.mark != null && $scope.mark != "") {
                if ($scope.mark.flowMark == null) {
                    $.alert("请对审批流程熟悉度评分！");
                    return false;
                }
                if ($scope.mark.fileTime == null) {
                    $.alert("请对资料的及时性评分！");
                    return false;
                }
                if ($scope.mark.fileContent == null) {
                    $.alert("请对资料的完整性评分！");
                    return false;
                }
                if ($scope.mark.moneyCalculate == null) {
                    $.alert("请对核心财务测算能力评分！");
                    return false;
                }
                if ($scope.mark.reviewFileAccuracy == null) {
                    $.alert("请对资料的准确性评分！");
                    return false;
                }
                if ($scope.mark.riskControl == null) {
                    $.alert("请对核心风险识别及规避能力评分！");
                    return false;
                }
                if ($scope.mark.planDesign == null) {
                    $.alert("请对核心的方案设计能力评分！");
                    return false;
                }
                if ($scope.mark.legalFileAccuracy == null) {
                    $.alert("请对资料的准确性评分！");
                    return false;
                }
                if ($scope.mark.talks == null) {
                    $.alert("请对核心的协议谈判能力评分！");
                    return false;
                }

                //存分
                $.ajax({
                    type: 'post',
                    url: srvUrl + "formalMark/saveOrUpdate.do",
                    data: $.param({
                        "json": JSON.stringify($scope.mark),
                        "businessId": objId
                    }),
                    dataType: "json",
                    async: false,
                    success: function (result) {
                        if (!result.success) {
                            alert(result.result_name);
                            return false;
                        }
                    }
                });
                return true;
            } else {
                //没有项目评分
                return true;
            }
        }

        $scope.getMarks = function (objId) {

//		$(".mark").bind('input propertychange', function() {
//			if(this.value>this.attributes.max.value*1){
//				$.alert("此项最高分为"+this.attributes.max.value+"分！");
//				this.value=this.attributes.max.value*1;
//			}
//			if(this.value.indexOf(".")>0){
//				$.alert("此项只能填写整数！");
//				this.value = 0;
//			}
//		});
            $(".mark").keyup(function () {
                if (this.value.length == 1) {
                    this.value = this.value.replace(/[^1-9]/g, '');
                } else {
                    this.value = this.value.replace(/\D/g, '')
                }
                if (this.value > this.attributes.max.value * 1) {
                    this.value = null;
                }
            });

            //初始化提示信息框
            $("[data-toggle='tooltip']").tooltip();

            $http({
                method: 'post',
                url: srvUrl + "formalMark/queryMarks.do",
                data: $.param({"businessId": objId})
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
                } else {
                    $.alert(result.result_name);
                }
            });
        }

        // 模板选择框绑定值初始化
        $scope.formalReport.summaryTemplate = [];
        // 初始化提交决策会材料数据
        $scope.getByID = function (projectFormalId) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/findFormalAndReport.do",
                data: $.param({"projectFormalId": projectFormalId})
            }).success(function (data) {
                $scope.formalReport = data.result_data.Report;
                $scope.pfr = data.result_data.Formal;
                $scope.meetInfo = data.result_data.MeetInfo;
                $scope.applyDate = data.result_data.applyDate;
                $scope.stage = data.result_data.stage;

                // 模板选择框给默认值 初始化模板数据
                if (data.result_data.summary == null) {
                    $scope.formalReport.summaryTemplate = $scope.SUMMARY_TEMPLATE[0];
                    $scope.summaryTemplateChange($scope.formalReport.summaryTemplate);
                } else {
                    angular.forEach($scope.SUMMARY_TEMPLATE, function (data1, index, array) {
                        if (data.result_data.summary.summaryType == data1.ITEM_CODE) {
                            $scope.formalReport.summaryTemplate = data1;
                            $scope.projectSummary = data.result_data.summary;
                        }
                    });
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

                if ($scope.stage == '3.9') {
                    $scope.selectFlag = 'true';
                    $scope.hasWaiting = true;
                }
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

        $scope.getSelectTypeByCode = function (typeCode) {
            var url = 'common/commonMethod/getRoleuserByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code === 'S') {
                    $scope.userRoleListall = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
        }

        $scope.getSelectTypeByCodetype = function (typeCode) {
            var url = 'common/commonMethod/selectDataDictionByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code == 'S') {
                    $scope.projectlisttype = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
        }

        // 定义模板选项变量
        $scope.SUMMARY_TEMPLATE = [];
        $scope.getSelectSUMMARY_TEMPLATE = function (typeCode) {
            var url = 'common/commonMethod/selectDataDictionByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code == 'S') {
                    $scope.SUMMARY_TEMPLATE = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
        }

        // 定义投资类型
        $scope.INVESTMENT_TYPE = [];
        $scope.getSelectINVESTMENT_TYPE = function (typeCode) {
            var url = 'common/commonMethod/selectDataDictionByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code == 'S') {
                    $scope.INVESTMENT_TYPE = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
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

        $scope.saveOnly = function () {
            if (!$scope.saveMarks()) {
                return;
            }
            $scope.getMarks(objId);
            //验证空附件
            if (!$scope.validateVoidFile()) {
                $.alert("附件不能为空！");
                return;
            }
            var data = $scope.dataForSave();
            if (data == false) {
                return;
            }
            $scope.saveOrSubmit(data, "so");
        }

        $scope.submitSave = function () {
            // 验证必填项
            $scope.submitted = false
            if ($scope.formalReportForm.$invalid) {
                $scope.submitted = true;
                alert("模板内容项没有填写完整,请填写完整后再保存");
                return;
            }
            if (!$scope.saveMarks()) {
                return;
            }
            //验证空附件
            if (!$scope.validateVoidFile()) {
                $.alert("附件不能为空！");
                return;
            }
            if ($scope.validateFormalBiddingInfo()) {
                if ($scope.isAttachmentBeChosen()) {
                    var data = $scope.dataForSubmit();
                    if (data == false) {
                        return;
                    }
                    $scope.saveOrSubmit(data, "ss");
                }
            }
        }

        $scope.saveOrSubmit = function (pData, method) {
            console.log(pData);
            show_Mask();
            $http({
                method: 'post',
                url: srvUrl + "formalReport/addPolicyDecision.do",
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
                            $location.path("/FormalBiddingInfo_view/" + objId + "@view/" + $filter('encodeURI')('#/FormalBiddingInfoList/1'));
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

        $scope.dataForSave = function () {
            var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);

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

            $scope.formalReport.projectSummary = $scope.combProjectSummaryJson();

            console.log($scope.formalReport);

            return $scope.formalReport;
        }

        $scope.dataForSubmit = function () {

            var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);

            if (newAttachment == false) {
                return false;
            }

            var file = $scope.formalReport.policyDecision.fileList;

            var chk_list = $("input[name='choose']")
            var fid = "";
            var uuidarr = [], itemarr = [], programmedarr = [], approvedarr = [], fileNamearr = [], filePatharr = [],
                versionarr = [], upload_datearr = [], programmeIddarr = [], approvedIdarr = [];

            for (var i = 0; i < chk_list.length; i++) {
                var fid = chk_list[i].value;
                var arrfid = fid.split("||");

//            if(arrfid[2] == "" || arrfid[8] == ""){
//            	$.alert("编制人不能为空!");
//            	return false;
//            }

//            if(arrfid[3] == "" || arrfid[9] == ""){
//            	$.alert("审核人不能为空!");
//            	return false;
//            }
//
                if (arrfid[4] == "" || arrfid[4] == null) {
                    $.alert("请上传附件！");
                    return false;
                }
            }

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
                var fid = newFiles[i].value;
                var arrfid = fid.split("||");

                if (arrfid[4] == "" || arrfid[4] == null) {
                    $.alert("请上传附件！");
                    return false;
                }
            }

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

            return $scope.formalReport;
        }

        //处理附件列表,为提交包装数据
        $scope.reduceAttachmentForSubmit = function (attachment) {
            var newAttachment = $scope.newAttachment;

            var now = $scope.getDate();
            //根据uuid处理版本号，上传日期当前
            //获取之前uuid
            for (var j = 0 in $scope.newPfr.attachment) {
                for (var i = 0 in newAttachment) {
                    if (newAttachment[i].newFile) {
                        if (newAttachment[i].newItem == undefined) {
                            $.alert("资源名称不能为空!");
                            return false;
                        }
                        newAttachment[i].ITEM_NAME = newAttachment[i].newItem.ITEM_NAME;
                        newAttachment[i].UUID = newAttachment[i].newItem.UUID;

                        $scope.newAttachment[i].ITEM_NAME = newAttachment[i].newItem.ITEM_NAME;
                        $scope.newAttachment[i].UUID = newAttachment[i].newItem.UUID;

                        if (newAttachment[i].UUID == $scope.newPfr.attachment[j].UUID) {
                            //之前版本号
                            //console.log($scope.newPfr.attachment[j].files);
                            //处理版本号问题
                            if (undefined == $scope.newPfr.attachment[j].files) {
                                newAttachment[i].version = 1;
                            } else {
                                var versionNum = $scope.newPfr.attachment[j].files.length;
                                newAttachment[i].version = versionNum * 1 + 1;
                            }
                            newAttachment[i].newFile = false;
                            newAttachment[i].upload_date = now;
                            newAttachment[i].programmed = newAttachment[i].programmed;
//	    				newAttachment[i].programmed.name=$scope.credentials.userName;
//	    				newAttachment[i].programmed.value=$scope.credentials.UUID;
                            newAttachment[i].approved = newAttachment[i].approved;
//	    				newAttachment[i].approved.name=$scope.credentials.userName;
//	    				newAttachment[i].approved.value=$scope.credentials.UUID;
                            if (undefined == $scope.newPfr.attachment[j].files) {
                                $scope.newPfr.attachment[j].files = [];
                                $scope.newPfr.attachment[j].files.push(newAttachment[i]);
                            } else {
                                $scope.newPfr.attachment[j].files.push(newAttachment[i]);
                            }
                        }

                    }
                }
            }
            return $scope.newPfr.attachment;
        }

        //验证必填项
        $scope.validateFormalBiddingInfo = function () {
            var boolean = false;
            var jprojectType = $("#jprojectType").val();
            var jinvestmentAmount = $("#jinvestmentAmount").val();
            var jprojectSize = $("#jprojectSize").val();
            var rateOfReturn = $("#rateOfReturn").val();
            var fkPsResult = $("#fkPsResult").val();
            var fkRiskTip = $("#fkRiskTip").val();
            var newItem = $("select[class='newItem']");

            if (null == jprojectType || "" == jprojectType) {
                $.alert("请选择项目类型!");
                return boolean;
            } else if (null == jinvestmentAmount || "" == jinvestmentAmount) {
                $.alert("投资金额不能为空!");
                return boolean;
            } else if (null == jprojectSize || "" == jprojectSize) {
                $.alert("项目规模不能为空!");
                return boolean;
            } else if (null == rateOfReturn || "" == rateOfReturn) {
                $.alert("投资收益率不能为空!");
                return boolean;
            } else if (null == fkPsResult || "" == fkPsResult) {
                $.alert("风控中心评审结论不能为空!");
                return boolean;
            } else if (null == fkRiskTip || "" == fkRiskTip) {
                $.alert("风控重点风险提示不能为空!");
                return boolean;
            } else {
                boolean = true;
            }

            return boolean;
        }

        $scope.isAttachmentBeChosen = function () {
            var boolean = false;
            if ($("input[name='choose']:checked").length + $("input[name='choosem']:checked").length == 0) {
                $.alert("您没有选择要提交的附件!");
            } else {
                boolean = true;
            }

            return boolean;
        }

        //附件列表---->新增列表
        $scope.addFileList = function () {
            function addBlankRow(array) {
                var blankRow = {
                    file_content: ''
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.formalReport.policyDecision) {
                $scope.formalReport.policyDecision = {fileList: []};
            }
            if (undefined == $scope.formalReport.policyDecision.fileList) {
                $scope.formalReport.policyDecision.fileList = [];
            }
            addBlankRow($scope.formalReport.policyDecision.fileList);
        }

        //附件列表---->删除指定的列表
        $scope.commonDdelete = function () {
            var commentsObj = $scope.formalReport.policyDecision.fileList;
            if (commentsObj != null) {
                for (var i = 0; i < commentsObj.length; i++) {
                    if (commentsObj[i].selected) {
                        commentsObj.splice(i, 1);
                        i--;
                    }
                }
            }
        }

        //附件列表---->上传附件
        $scope.errorAttach = [];
        $scope.uploadReprot = function (file, errorFile, idx) {
            if (errorFile && errorFile.length > 0) {
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx] = {msg: errorMsg};
            } else if (file) {
                $scope.errorAttach[idx] = {msg: ''};
                Upload.upload({
                    url: srvUrl + 'file/uploadFile.do',
                    data: {file: file, folder: '', typeKey: 'formalAssessmentReportPath'}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope.formalReport.filePath = retData.filePath;
                    $.alert("文件替换成功！请执行保存操作！否则操作无效！");
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress" + idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            }
        };
        $scope.upload = function (file, errorFile, idx) {
            if (errorFile && errorFile.length > 0) {
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx] = {msg: errorMsg};
            } else if (file) {
                var fileFolder = "formalReport/";
                var dates = $scope.formalReport.create_date;
                var no = $scope.formalReport.projectNo;
                var strs = new Array(); //定义一数组
                strs = dates.split("-"); //字符分割
                dates = strs[0] + strs[1]; //分割后的字符输出
                fileFolder = fileFolder + dates + "/" + no;

                $scope.errorAttach[idx] = {msg: ''};
                Upload.upload({
                    url: srvUrl + 'file/uploadFile.do',
                    data: {file: file, folder: fileFolder}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope.formalReport.policyDecision.fileList[idx].files = retData;
                    $.alert("文件替换成功！请执行保存操作！否则操作无效！");
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress" + idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            }
        };

        //业务单位上报评审文件-投资部门提供---->新增列表
        $scope.addFileList1 = function () {
            function addBlankRow1(array) {
                var blankRow = {
                    newFile: true
                }
                var size = array.length;
                array[size] = blankRow;
            }

            if (undefined == $scope.newPfr.attachment) {
                $scope.newAttachment = {files: []};
            }
            addBlankRow1($scope.newAttachment);
        }

        //业务单位上报评审文件-投资部门提供---->删除指定的列表
        $scope.deleteFileList = function () {
            var i = 0;
            $(".deleteSelect:checked").each(function () {
                if (i > 0) {
                    $scope.newAttachment.splice(this.value - i, 1);
                } else {
                    $scope.newAttachment.splice(this.value, 1);
                }
                i++;
            });
            $(".deleteSelect:checked").attr("checked", false);
        }

        //业务单位上报评审文件-投资部门提供---->上传附件
        $scope.errorMsg = [];
        $scope.upload2 = function (file, errorFile, idx) {
            if (errorFile && errorFile.length > 0) {
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorMsg[idx] = {msg: errorMsg};
            } else if (file) {
                var fileFolder = "pfrAssessment/";
                var dates = $scope.formalReport.create_date;
                var no = $scope.formalReport.projectNo;
                var strs = new Array(); //定义一数组
                strs = dates.split("-"); //字符分割
                dates = strs[0] + strs[1]; //分割后的字符输出
                fileFolder = fileFolder + dates + "/" + no;
                Upload.upload({
                    url: srvUrl + 'file/uploadFile.do',
                    data: {file: file, folder: fileFolder}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    var aaa = $scope.newAttachment;
                    var bbb = $scope.newAttachment[idx];
                    $scope.newAttachment[idx].fileName = retData.fileName;
                    $scope.newAttachment[idx].filePath = retData.filePath;
                    $.alert("文件替换成功！请执行保存操作！否则操作无效！");
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress" + idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            }
        };

        //检查压缩文件
        $scope.checkFileZip = function (name, ev) {
            if (name == null) {
                var e = ev || window.event;//获取事件
                var ele = e.target || e.srcElement;//获取触发事件的元素
                ele.checked = false;
                $.alert("请上传文件！");
                return false;
            }
            var index = name.lastIndexOf('.');
            var suffix = name.substring(index + 1);
            if ("rar" == suffix || "zip" == suffix || "7z" == suffix) {
                var e = ev || window.event;//获取事件
                var ele = e.target || e.srcElement;//获取触发事件的元素
                ele.checked = false;
                $.alert("上会附件不能是压缩文件！");
                return false;
            }
            return true;
        }

        $scope.initData();

        // -------------------Sam Gao 2018-11-28日修改--------------------------


        // 常量（类型：1000,2000,3000,4000,5000,6000,7000,8000）==项目概况
        $scope.projectOverview = {
            orderno: null, // 排序编号从0开始
            code: null, // 行code
            value: null, // 行描述
            content: null, // 行内容
            attachmentFile: null, // 附件地址
            attachmentValue: null, // 附件名称
            start: null, // 是否启用 0：未启用；1：启用
            edit: null, // 是否可以修改行描述 0：不可修改； 1：可修改
            modify: null, // 是否可以编辑 0:不可以编辑；1：可编辑
            new: null // 是否可以新增 0:不可以新增；1:可以新增
        };

        // 常量（类型：1000,2000,3000,4000,5000,6000）==风险提示-重点关注/风险提示-一般关注
        $scope.risk = {
            orderno: null, // 排序编号从0开始
            riskType: null, // 风险类别
            riskContent: null // 风险事项
        };

        // 常量（类型：1000,2000,3000,4000,5000,6000）==投前条件/后续执行要求
        $scope.requirement = {
            orderno: null, // 排序编号从0开始
            requirementContent: null // 风险事项
        };

        // 常量（类型：4000）==项目收益
        $scope.projectIncome = {
            code: null, // 行code
            value: null, // 行描述
            originalReview: null, // 原评审情况
            actualOperation: null, // 实际运用情况
            afterTechnicalReform: null // 技改后
        };
        // 常量（类型：5000,6000）==项目收益
        $scope.projectIncome1 = {
            code: null, // 行code
            value: null, // 行描述
            indicator: null // 本项目指标
        }
        // 常量（类型：7000） ==还款计划变量
        $scope.repaymentPlanVar = {
            orderno: null, // 排序编号从0开始
            loanTime: null, // 借款时间
            loanAmount: null, // 借款金额
            repaymentPlan: null,// 还款计划
            repaymentMethod: null, // 还款方式
            repaymentSource: null // 还款来源
        }
        // 常量（类型：7000,8000）==原评审情况说明/特殊事项说明
        $scope.desciptionVar = {
            orderno: null, // 排序编号从0开始
            description: null // 说明
        };
        // 常量（类型：8000）==决策事项
        $scope.decisionMakingVar = {
            orderno: null, // 排序编号从0开始
            decisionMaking: null // 说明
        };

        // 删除数组对象
        $scope.deleteObj = function (key, variableList) {
            $scope.delObj = angular.copy(variableList[key]);
            variableList.splice(key, 1);
            angular.forEach(variableList, function (data, index, array) {
                if (data.orderno > $scope.delObj.orderno) {
                    data.orderno = data.orderno - 1;
                }
            });
        };

        $scope.summaryTemplateChange = function (type) {
            // 风控评审意见汇总 初始化模板数据
            $scope.projectSummary = {};
            if (type.ITEM_CODE == "1000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "regionalBackground",
                        value: "项目背景",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "projectType",
                        value: "项目模式",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "2",
                        code: "totaleInvestment",
                        value: "项目总投资",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "constructionContent",
                        value: "项目内容",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "regionalBackground",
                        value: "项目期限",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];

                $scope.projectSummary.seriousRisks = [
                    {
                        orderno: 0,
                        riskType: "这是重点关注1",
                        riskContent: "这是重点关注1这是重点关注1这是重点关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是重点关注2",
                        riskContent: "这是重点关注2这是重点关注2这是重点关注2"
                    }
                ];

                $scope.projectSummary.generalRisks = [
                    {
                        orderno: 0,
                        riskType: "这是一般关注1",
                        riskContent: "这是一般关注1这是一般关注1这是一般关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是一般关注2",
                        riskContent: "这是一般关注2这是一般关注2这是一般关注2"
                    }
                ];

                $scope.projectSummary.requirements = [
                    {
                        orderno: 0,
                        requirementContent: "投前条件11111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "投前条件22222"
                    }
                ];

                $scope.projectSummary.performs = [
                    {
                        orderno: 0,
                        requirementContent: "后续执行要求111111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "后续执行要求222222"
                    }
                ];

                $scope.projectSummary.project = {
                    investmentGlobale: null,
                    investmentInternal: null,
                    investmentStatic: null,
                    financialROE: null,
                    financialROI: null,
                    indicatorCash: null
                }

            } else if (type.ITEM_CODE == "2000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "regionalBackground",
                        value: "项目背景",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "projectType",
                        value: "项目模式",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "1"
                    },
                    {
                        orderno: "2",
                        code: "totaleInvestment",
                        value: "项目总投资",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "projectSubject",
                        value: "项目主体",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "constructionContent",
                        value: "项目内容",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "regionalBackground",
                        value: "项目期限",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "6",
                        code: "projectProgress",
                        value: "项目进展",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "7",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.seriousRisks = [
                    {
                        orderno: 0,
                        riskType: "这是重点关注1",
                        riskContent: "这是重点关注1这是重点关注1这是重点关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是重点关注2",
                        riskContent: "这是重点关注2这是重点关注2这是重点关注2"
                    }
                ];

                $scope.projectSummary.generalRisks = [
                    {
                        orderno: 0,
                        riskType: "这是一般关注1",
                        riskContent: "这是一般关注1这是一般关注1这是一般关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是一般关注2",
                        riskContent: "这是一般关注2这是一般关注2这是一般关注2"
                    }
                ];

                $scope.projectSummary.performs = [
                    {
                        orderno: 0,
                        requirementContent: "后续执行要求111111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "后续执行要求222222"
                    }
                ];

                $scope.projectSummary.project = {
                    investmentGlobale: null,
                    investmentInternal: null,
                    investmentStatic: null,
                    financialROE: null,
                    financialROI: null,
                    indicatorCash: null
                }

            } else if (type.ITEM_CODE == "3000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "regionalBackground",
                        value: "项目背景",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "projectType",
                        value: "项目模式",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "1"
                    },
                    {
                        orderno: "2",
                        code: "projectScale",
                        value: "项目规模",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "totaleInvestment",
                        value: "项目总投资",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "waterStandard",
                        value: "出水标准",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "waterPrice",
                        value: "水价",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "6",
                        code: "franchisePeriod",
                        value: "特许经营期",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "7",
                        code: "actoryStatus",
                        value: "水厂现状",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "8",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.seriousRisks = [
                    {
                        orderno: 0,
                        riskType: "这是重点关注1",
                        riskContent: "这是重点关注1这是重点关注1这是重点关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是重点关注2",
                        riskContent: "这是重点关注2这是重点关注2这是重点关注2"
                    }
                ];

                $scope.projectSummary.generalRisks = [
                    {
                        orderno: 0,
                        riskType: "这是一般关注1",
                        riskContent: "这是一般关注1这是一般关注1这是一般关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是一般关注2",
                        riskContent: "这是一般关注2这是一般关注2这是一般关注2"
                    }
                ];

                $scope.projectSummary.performs = [
                    {
                        orderno: 0,
                        requirementContent: "后续执行要求111111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "后续执行要求222222"
                    }
                ];

                $scope.projectSummary.project = {
                    investmentGlobale: null,
                    investmentInternal: null,
                    investmentStatic: null,
                    financialROE: null,
                    financialROI: null,
                    indicatorCash: null
                }

            } else if (type.ITEM_CODE == "4000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "regionalBackground",
                        value: "项目背景",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "acquisitionTime",
                        value: "项目取得时间",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "1"
                    },
                    {
                        orderno: "2",
                        code: "franchisePeriod",
                        value: "特许经营期",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "effluentStandard",
                        value: "合同/协议出水标准",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "process",
                        value: "工艺",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "operatingSituation",
                        value: "经营现状",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "6",
                        code: "chargeSituation",
                        value: "收费情况",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "7",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.projectOverviews1 = [
                    {
                        orderno: "0",
                        code: "technicalReformNecessity",
                        value: "技改必要性",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "technicalReformContent",
                        value: "技改内容",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "2",
                        code: "investmentAndFundingSources",
                        value: "技改投资及资金来源",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "afterEffluentStandard",
                        value: "技改后出水标准",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "adjustPriceAndWhy",
                        value: "技改是否调价及原因",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "other_100",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.projectIncomes = [
                    {
                        code: "fullInvestment",
                        value: "全投资IRR",
                        originalReview: null,
                        actualOperation: null,
                        afterTechnicalReform: null
                    },
                    {
                        code: "privateCapital",
                        value: "自有资金IRR",
                        originalReview: null,
                        actualOperation: null,
                        afterTechnicalReform: null
                    }
                ];
                $scope.projectSummary.seriousRisks = [
                    {
                        orderno: 0,
                        riskType: "这是重点关注1",
                        riskContent: "这是重点关注1这是重点关注1这是重点关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是重点关注2",
                        riskContent: "这是重点关注2这是重点关注2这是重点关注2"
                    }
                ];

                $scope.projectSummary.generalRisks = [
                    {
                        orderno: 0,
                        riskType: "这是一般关注1",
                        riskContent: "这是一般关注1这是一般关注1这是一般关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是一般关注2",
                        riskContent: "这是一般关注2这是一般关注2这是一般关注2"
                    }
                ];

                $scope.projectSummary.performs = [
                    {
                        orderno: 0,
                        requirementContent: "后续执行要求111111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "后续执行要求222222"
                    }
                ];

            } else if (type.ITEM_CODE == "5000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "operationMode",
                        value: "运作模式",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "constructionContent",
                        value: "建设内容",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "1"
                    },
                    {
                        orderno: "2",
                        code: "operationalContent",
                        value: "运营内容",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "regionalBackground",
                        value: "项目期限",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "totaleInvestment",
                        value: "总投资",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "fundsSources",
                        value: "资金来源",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "6",
                        code: "ownershipAndResponsibility",
                        value: "股权结构与责任分配",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "7",
                        code: "procedureRules",
                        value: "项目公司议事规则",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "8",
                        code: "incomeDistribution",
                        value: "收益分配",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "9",
                        code: "projectLand",
                        value: "项目用地及征地拆迁",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "10",
                        code: "constructionPeriodIncome",
                        value: "我方建设期的收益",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "11",
                        code: "performanceAppraisalMethod",
                        value: "运维绩效考核办法",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "12",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.projectIncomes = [
                    {
                        code: "fullInvestment",
                        value: "项目全投资IRR",
                        indicator: null
                    },
                    {
                        code: "privateCapital",
                        value: "自有资金IRR",
                        indicator: null
                    }
                ];
                $scope.projectSummary.seriousRisks = [
                    {
                        orderno: 0,
                        riskType: "这是重点关注1",
                        riskContent: "这是重点关注1这是重点关注1这是重点关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是重点关注2",
                        riskContent: "这是重点关注2这是重点关注2这是重点关注2"
                    }
                ];

                $scope.projectSummary.generalRisks = [
                    {
                        orderno: 0,
                        riskType: "这是一般关注1",
                        riskContent: "这是一般关注1这是一般关注1这是一般关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是一般关注2",
                        riskContent: "这是一般关注2这是一般关注2这是一般关注2"
                    }
                ];

                $scope.projectSummary.requirements = [
                    {
                        orderno: 0,
                        requirementContent: "投前条件11111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "投前条件22222"
                    }
                ];

                $scope.projectSummary.performs = [
                    {
                        orderno: 0,
                        requirementContent: "后续执行要求111111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "后续执行要求222222"
                    }
                ];
            } else if (type.ITEM_CODE == "6000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "serviceArea",
                        value: "服务范围",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "serviceTerm",
                        value: "服务期限",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "1"
                    },
                    {
                        orderno: "2",
                        code: "serviceFeeAmount",
                        value: "服务费金额",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "paymentMethods",
                        value: "付费方式",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "4",
                        code: "investmentAmount",
                        value: "投资金额",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "5",
                        code: "personnelPlan",
                        value: "人员方案",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "6",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.projectIncomes = [
                    {
                        code: "averageNetInterestRate",
                        value: "项目平均净利率",
                        indicator: null
                    },
                    {
                        code: "fullInvestment",
                        value: "全投资IRR",
                        indicator: null
                    }
                ];
                $scope.projectSummary.seriousRisks = [
                    {
                        orderno: 0,
                        riskType: "这是重点关注1",
                        riskContent: "这是重点关注1这是重点关注1这是重点关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是重点关注2",
                        riskContent: "这是重点关注2这是重点关注2这是重点关注2"
                    }
                ];

                $scope.projectSummary.generalRisks = [
                    {
                        orderno: 0,
                        riskType: "这是一般关注1",
                        riskContent: "这是一般关注1这是一般关注1这是一般关注1"
                    },
                    {
                        orderno: 1,
                        riskType: "这是一般关注2",
                        riskContent: "这是一般关注2这是一般关注2这是一般关注2"
                    }
                ];

                $scope.projectSummary.performs = [
                    {
                        orderno: 0,
                        requirementContent: "后续执行要求111111"
                    },
                    {
                        orderno: 1,
                        requirementContent: "后续执行要求222222"
                    }
                ];

                $scope.projectSummary.project = {
                    investmentGlobale: null,
                    investmentInternal: null,
                    investmentStatic: null,
                    financialROE: null,
                    financialROI: null,
                    indicatorCash: null
                }

            } else if (type.ITEM_CODE == "7000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "borrowingAmount",
                        value: "借款金额",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "borrowingPeriodAndRate",
                        value: "借款期限及利率",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "2",
                        code: "other_0",
                        value: "其他事项",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }];
                $scope.projectSummary.projectOverviews1 = [
                    {
                        orderno: "0",
                        code: "borrowingPurpose",
                        value: "借款目的",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "borrowingReason",
                        value: "借款原因",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "2",
                        code: "other_100",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.repaymentPlans = [
                    {
                        orderno: 0,
                        loanTime: null,
                        loanAmount: null,
                        repaymentPlan: null,
                        repaymnetMethod: null,
                        repaymentSource: null
                    }
                ];
                $scope.projectSummary.desciptions = [
                    {
                        orderno: 0,
                        description: null
                    }
                ];
            } else if (type.ITEM_CODE == "8000") {
                $scope.projectSummary.projectOverviews = [
                    {
                        orderno: "0",
                        code: "businessArea",
                        value: "业务范围",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "profitModel",
                        value: "盈利模式",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "1"
                    },
                    {
                        orderno: "2",
                        code: "governanceStructure",
                        value: "治理结构",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "3",
                        code: "other_0",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }];
                $scope.projectSummary.projectOverviews1 = [
                    {
                        orderno: "0",
                        code: "registeredCapital",
                        value: "注册资本金",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "1",
                        code: "equityRatio",
                        value: "股权比例",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "0",
                        modify: "1",
                        new: "0"
                    },
                    {
                        orderno: "2",
                        code: "other_100",
                        value: "其他",
                        content: null,
                        attachmentFile: null,
                        attachmentValue: null,
                        start: "1",
                        edit: "1",
                        modify: "1",
                        new: "1"
                    }
                ];
                $scope.projectSummary.decisionMakings = [
                    {
                        orderno: 0,
                        decisionMaking: null
                    }
                ];
                $scope.projectSummary.desciptions = [
                    {
                        orderno: 0,
                        description: null
                    }
                ];
            }
            angular.forEach($scope.projectSummary.projectOverviews, function (data, index) {
                data.orderno = parseInt(data.orderno);
            });
            angular.forEach($scope.projectSummary.projectOverviews1, function (data, index) {
                data.orderno = parseInt(data.orderno);
            });
        }

        // 风险提示新增按钮
        $scope.addRisk = function (variable) {
            $scope.newRisk = angular.copy($scope.risk);
            $scope.newRisk.orderno = $scope.projectSummary[variable].length;
            $scope.projectSummary[variable].push($scope.newRisk);
        };

        // 投前条件/后续执行要求--新增按钮
        $scope.addRequirement = function (variable) {
            $scope.newRequirement = angular.copy($scope.requirement);
            $scope.newRequirement.orderno = $scope.projectSummary[variable].length;
            $scope.projectSummary[variable].push($scope.newRequirement);
        };

        // 新增还款计划
        $scope.addRepaymentPlan = function () {
            $scope.newRepaymentPlan = angular.copy($scope.repaymentPlanVar);
            $scope.newRepaymentPlan.orderno = $scope.projectSummary.repaymentPlans.length;
            $scope.projectSummary.repaymentPlans.push($scope.newRepaymentPlan);
        }

        // 新增原评审情况说明/特殊事项说明
        $scope.addDesciption = function () {
            $scope.newDesciption = angular.copy($scope.desciptionVar);
            $scope.newDesciption.orderno = $scope.projectSummary.desciptions.length;
            $scope.projectSummary.desciptions.push($scope.newDesciption);
        }
        // 新增决策事项
        $scope.addDecisionMaking = function () {
            $scope.newDecisionMaking = angular.copy($scope.decisionMakingVar);
            $scope.newDecisionMaking.orderno = $scope.projectSummary.decisionMakings.length;
            $scope.projectSummary.decisionMakings.push($scope.newDecisionMaking);
        }

        // 升序方法
        $scope.descOrderno = function (projectOverview, variable) {
            if (projectOverview.orderno != 0) {
                $scope.old = angular.copy(projectOverview);
                angular.forEach($scope.projectSummary[variable], function (data, index, array) {
                    // 给上层序号+1
                    if (data.orderno == (projectOverview.orderno - 1)) {
                        data.orderno = data.orderno + 1;
                    }
                });
                // 给自己序号-1
                projectOverview.orderno = ($scope.old.orderno - 1);
            }
        };
        // 降序方法
        $scope.ascOrderno = function (projectOverview, variable) {
            if (projectOverview.orderno != ($scope.projectSummary[variable].length - 1)) {
                $scope.old = angular.copy(projectOverview);
                angular.forEach($scope.projectSummary[variable], function (data, index, array) {
                    // 给下层序号-1
                    if (data.orderno == (projectOverview.orderno + 1)) {
                        data.orderno = data.orderno - 1;
                    }
                });
                // 给自己序号+1
                projectOverview.orderno = ($scope.old.orderno + 1);
            }
        };
        // 启用或者关闭编辑界面
        $scope.closeOverview = function (projectOverview) {
            projectOverview.modify = projectOverview.modify == "0" ? "1" : "0";
        }
        // 新增projectOverview
        $scope.addOverview = function (projectOverview, variable) {
            $scope.newProjectOverview = angular.copy(projectOverview);
            // 名称逻辑（除 其他 外，剩余需新增的对象名称变成原名称+1）
            if (projectOverview.code.slice(0, 5) != "other") {
                $scope.str = projectOverview.value.split("_");
                if ($scope.str.length > 1) {
                    $scope.newProjectOverview.value = $scope.str[0] + "_" + (parseInt($scope.str[1]) + 1).toString();
                } else {
                    $scope.newProjectOverview.value = projectOverview.value + "_1";
                }
            }
            // code逻辑（新增对象code变成原code+1）
            $scope.str = projectOverview.code.split("_");
            if ($scope.str.length > 1) {
                $scope.newProjectOverview.code = $scope.str[0] + "_" + (parseInt($scope.str[1]) + 1).toString();
            } else {
                $scope.newProjectOverview.code = projectOverview.code + "_1";
            }
            $scope.newProjectOverview.orderno = projectOverview.orderno + 1;
            $scope.newProjectOverview.content = null;
            $scope.newProjectOverview.attachmentFile = null;
            $scope.newProjectOverview.attachmentValue = null;
            $scope.newProjectOverview.start = "1";
            $scope.newProjectOverview.modify = "1";
            angular.forEach($scope.projectSummary[variable], function (data, index, array) {
                if (data.orderno == projectOverview.orderno) {
                    // 隐藏原对象新增按钮
                    data.new = "0";
                } else {
                    // 给序号比原对象大的对象的序号加1
                    if (data.orderno > projectOverview.orderno) {
                        data.orderno = data.orderno + 1;
                    }
                }
            });
            $scope.projectSummary[variable].push($scope.newProjectOverview);
        };

        // 数组指定key值排序
        var compare = function (prop) {
            return function (obj1, obj2) {
                var val1 = obj1[prop];
                var val2 = obj2[prop];
                if (!isNaN(Number(val1)) && !isNaN(Number(val2))) {
                    val1 = Number(val1);
                    val2 = Number(val2);
                }
                if (val1 < val2) {
                    return -1;
                } else if (val1 > val2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        // 整理json对象数据
        $scope.combProjectSummaryJson = function () {
            // 模板通用属性
            $scope.projectSummary.reportlId = $scope.pfr.apply.investmentManager._id; //报告id
            $scope.projectSummary.projectFormalId = $scope.formalReport.projectFormalId; // 正式评审项目id
            $scope.projectSummary.projectName = $scope.formalReport.projectName; // 项目名称
            $scope.projectSummary.projectNo = $scope.formalReport.projectNo;  // 项目编号
            $scope.projectSummary.reportingUnit = $scope.formalReport.reportingUnit; // 所属大区
            // 不同模板存入不同的模板code
            if ($scope.formalReport.summaryTemplate.ITEM_CODE == "1000") {
                $scope.projectSummary.summaryType = "1000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "2000") {
                $scope.projectSummary.summaryType = "2000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "3000") {
                $scope.projectSummary.summaryType = "3000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "4000") {
                $scope.projectSummary.summaryType = "4000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "5000") {
                $scope.projectSummary.summaryType = "5000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "6000") {
                $scope.projectSummary.summaryType = "6000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "7000") {
                $scope.projectSummary.summaryType = "7000";
            } else if ($scope.formalReport.summaryTemplate.ITEM_CODE == "8000") {
                $scope.projectSummary.summaryType = "8000";
            }
            return $scope.projectSummary;
        };

        // 决策会材料提交暂存功能
        $scope.staging = function () {
            $scope.projectSummary = $scope.combProjectSummaryJson();

            $http({
                method: 'post',
                url: srvUrl + "formalReport/stagingFormalProjectSummary.do",
                data:
                    $.param({
                        "json": angular.toJson($scope.projectSummary),
                        "method": "sss"
                    })
            }).success(function (result) {
                console.log(result);
                alert("暂存成功");
            })
        }

        // 整理预览界面需要的json数据
        $scope.previewJson = function () {
            var formalPreview = $scope.projectSummary;  // 存入模板数据
            formalPreview.stage = $scope.stage;
            formalPreview.applyDate = $scope.applyDate
            formalPreview.apply = $scope.pfr.apply       // 存入项目信息
            formalPreview.taskallocation = $scope.pfr.taskallocation
            formalPreview.cesuanFileOpinion = $scope.pfr.cesuanFileOpinion
            formalPreview.tzProtocolOpinion = $scope.pfr.tzProtocolOpinion
            formalPreview.approveAttachment = $scope.pfr.approveAttachment  // 风控中心审批
            formalPreview.approveLegalAttachment = $scope.pfr.approveLegalAttachment  // 风控中心法律审批
            formalPreview.submit_date = $scope.formalReport.submit_date   // 评审报告出具时间
            formalPreview.projectRating = $scope.meetInfo.projectRating  // 评审等级
            formalPreview.filePath = $scope.formalReport.filePath
            formalPreview.projectName = $scope.formalReport.projectName
            formalPreview.newAttachment = $scope.newAttachment

            formalPreview.mark = $scope.mark // 分数

            return formalPreview;
        }

        // 进入预览页面
        $scope.toPreview = function () {
            $location.path("/FormalBiddingInfoPreview").search({formalPreview: $scope.previewJson()});
        }

        // 附件管理
        $scope.errorAttach = [];
        // 文件上传
        $scope.uploadAttachment = function (file, errorFile, idx, projectOverview) {
            if (errorFile && errorFile.length > 0) {
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx] = {msg: errorMsg};
            } else if (file) {
                $scope.errorAttach[idx] = {msg: ''};
                Upload.upload({
                    url: srvUrl + 'common/RcmFile/upload',
                    data: {file: file, folder: '', typeKey: 'formalSummaryPath'}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    projectOverview.attachmentFile = retData.filePath;
                    projectOverview.attachmentValue = retData.fileName;
                    console.log(projectOverview);
                }, function (resp) {

                }, function (evt) {

                });
            }
        };

        // 文件下载
        $scope.downLoadFile = function (projectOverview) {
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
    }]);