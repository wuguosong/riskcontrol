ctmApp.register.controller('PreBiddingInfo', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        $scope.selectFlag = 'false';
        $scope.oldUrl = $routeParams.url;
        var flag = $routeParams.flag;
        //申请报告ID
        var complexId = $routeParams.id;
        var params = complexId.split("@");
        $scope.businessId = params[0];
        $scope.preBidding={};
        $scope.preBidding.policyDecision={};

        $scope.getDate = function(){
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
        };

        $scope.FormatDate = function() {
            var date = new Date();
            var paddNum = function(num){
                num += "";
                return num.replace(/^(\d)$/,"0$1");
            }
            return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+date.getDate();
        };

        //初始化页面所需数据
        $scope.initData = function () {
            $scope.currentUser = {NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
            $scope.initUpdate($scope.businessId);
            $scope.getMarks($scope.businessId);
            $scope.getSelectTypeByCode("8");
            $scope.getSelectTypeByCodetype('14');
            $scope.dic2=[];
            $scope.dic2.nameValue=[{name:'A级（困难评审)',value:1},{name:'B级（中级评审)',value:2},{name:'C级（简单评审)',value:3}]
            $scope.getSelectINVESTMENT_TYPE('INVESTMENT_TYPE');
            $scope.hasWaiting = false;
        };

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
                    url: srvUrl + "preMark/saveOrUpdate.do",
                    data: $.param({
                        "json": JSON.stringify($scope.mark),
                        "$scope.businessId": $scope.businessId
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
        };

        $scope.getMarks = function () {
            $(".mark").keyup(function () {
                if (this.value.length == 1) {
                    this.value = this.value.replace(/[^0-9]/g, '');
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
                url: srvUrl + "preMark/queryMarks.do",
                data: $.param({"businessId": $scope.businessId})
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
        };

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
            /*$scope.INVESTMENT_TYPE = selectDocItem(typeCode);*/
        };

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
            /* $scope.projectlisttype = selectDocItem(typeCode);*/
        };

        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
            $scope.newAttachment = attach_list("preReview", id, "preInfo").result_data;
            for(var i in attachment){
                var file = attachment[i];
                console.log(file);
                for (var j in $scope.newAttachment){
                    if (file.fileId == $scope.newAttachment[j].fileid){
                        $scope.newAttachment[j].fileName = file.fileName;
                        $scope.newAttachment[j].type = file.type;
                        $scope.newAttachment[j].itemType = file.itemType;
                        $scope.newAttachment[j].programmed = file.programmed;
                        $scope.newAttachment[j].approved = file.approved;
                        $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                        $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                        break;
                    }
                }

            }
        };

        $scope.initUpdate = function(businessId){
            $http({
                method:'post',
                url:srvUrl+"preBidding/getByBusinessId.do",
                data: $.param({"businessId":businessId})
            }).success(function(data){
                $scope.pfr  = data.result_data.preMongo;
                $scope.preBidding  = $scope.pfr;
                $scope.preBidding.id = $scope.businessId;
                $scope.applyDate = data.result_data.applyDate;
                $scope.stage = data.result_data.stage;
                $scope.reportOracle = data.result_data.reportOracle;

                $scope.reduceAttachment($scope.pfr.attachmentList, $scope.businessId);

                if(flag == 1){
                    var storage = window.localStorage;
                    if ($scope.preBidding.meetingInfo == null) {
                        $scope.preBidding.meetingInfo = [];
                    }
                    if (storage.fileList != "undefined"  && storage.fileList != "" && storage.fileList != null) {
                        $scope.preBidding.policyDecision.fileList  = angular.copy(JSON.parse(storage.fileList));
                    }
                    if (storage.newAttachment != "undefined"  && storage.newAttachment != "" && storage.newAttachment != null){
                        $scope.newAttachment = angular.copy(JSON.parse(storage.newAttachment));
                    }
                    $scope.mark = angular.copy(JSON.parse(storage.mark));
                    $scope.preBidding.meetingInfo.investmentType = angular.copy(JSON.parse(storage.investmentType));
                    $scope.preBidding.meetingInfo.projectRating = angular.copy(JSON.parse(storage.projectRating));
                    $scope.preBidding.meetingInfo.ratingReason = angular.copy(storage.ratingReason);
                    if(storage.projectType1 == 'true'){
                        $scope.preBidding.meetingInfo.projectType1 = true;
                    }else{
                        $scope.preBidding.meetingInfo.projectType1 = false;
                    }
                    if(storage.projectType2 == 'true'){
                        $scope.preBidding.meetingInfo.projectType2 = true;
                    }else{
                        $scope.preBidding.meetingInfo.projectType2 = false;
                    }
                    if(storage.projectType3 == 'true'){
                        $scope.preBidding.meetingInfo.projectType3 = true;
                    }else{
                        $scope.preBidding.meetingInfo.projectType3 = false;
                    }
                    $scope.preBidding.meetingInfo.isUrgent = angular.copy(storage.isUrgent);
                }

               /* //处理附件列表
                $scope.reduceAttachment($scope.pfr.attachment);*/
                //新增附件类型
                $scope.attach  = data.result_data.attach;
                //控制新增文件
                $scope.newPfr  = $scope.pfr;
                var ptNameArr=[],fgNameArr=[],fgValueArr=[],investmentaNameArr=[];
                var pt=$scope.pfr.apply.projectType;
                if(null!=pt && pt.length>0){
                    for(var i=0;i<pt.length;i++){
                        ptNameArr.push(pt[i].VALUE);
                    }
                    $scope.pfr.apply.projectType=ptNameArr.join(",");
                }

                if($scope.stage == '3' || $scope.stage == '3.5' || $scope.stage == '3.7' || $scope.stage == '3.9'){
                    $scope.selectFlag = 'true';
                }
            }).error(function(data,status,header,config){
                $.alert(status);
            });
        }

        /*//处理附件列表
        $scope.reduceAttachment = function(attachment){
            $scope.newAttachment = [];
            for(var i in attachment){
                var files = attachment[i].files;
                if(files!=null){
                    var item_name = attachment[i].ITEM_NAME;
                    var uuid = attachment[i].UUID;
                    for(var j in files){
                        files[j].ITEM_NAME=item_name;
                        files[j].UUID=uuid;
                        $scope.newAttachment.push(files[j]);
                    }
                }
            }
        }*/

        $scope.downLoadFormalBiddingInfoFile = function(filePath,filename){
            var isExists = validFileExists(filePath);
            if(!isExists){
                $.alert("要下载的文件已经不存在了！");
                return false;
            }
            if(filename!=null && filename.length>12){
                filename = filename.substring(0, 12)+"...";
            }else{
                filename = filename.substring(0,filename.lastIndexOf("."));
            };

            if(undefined!=filePath && null!=filePath){
                var index = filePath.lastIndexOf(".");
                var str = filePath.substring(index + 1, filePath.length);
                var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(filename + "-正式评审报告.")) + str;

                var a = document.createElement('a');
                a.id = 'tagOpenWin';
                a.target = '_blank';
                a.href = url;
                document.body.appendChild(a);

                var e = document.createEvent('MouseEvent');
                e.initEvent('click', false, false);
                document.getElementById("tagOpenWin").dispatchEvent(e);
                $(a).remove();
            }else{
                $.alert("附件未找到！");
                return false;
            }
        }

        $scope.downLoadBiddingFile = function(idx){
            var isExists = validFileExists(idx.filePath);
            if(!isExists){
                $.alert("要下载的文件已经不存在了！");
                return;
            }
            var filePath = idx.filePath, fileName = idx.fileName;
            if(fileName!=null && fileName.length>22){
                var extSuffix = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0, 22);
                fileName = fileName + extSuffix;
            }

            var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
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

        $scope.validateVoidFile = function(){
            for(var i in $scope.newAttachment){
                if($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == ''){
                    return false;
                }
            }
            return true;
        }

        $scope.saveOnly = function(){
            if (!$scope.saveMarks()) {
                return;
            }
            //验证空附件
            if(!$scope.validateVoidFile()){
                $.alert("附件不能为空！");
                return;
            }
            var data = $scope.dataForSave();
            if(data == false){
                return;
            }
            $scope.saveOrSubmit(data,"so");
        }

        $scope.submitSave = function() {
            if (!$scope.saveMarks()) {
                $.alert("分数保存出错请检查！");
                return;
            }
            //验证空附件
            if(!$scope.validateVoidFile()){
                $.alert("附件不能为空！");
                return;
            }
            if($scope.validateFormalBiddingInfo()){
                if($scope.isAttachmentBeChosen()){
                    var data = $scope.dataForSubmit();
                    if(data == false){
                        return;
                    }
                    $scope.saveOrSubmit(data,"ss");
                }
            }
        };

        $scope.saveOrSubmit = function(pData,method){
            console.log(pData)
            show_Mask();
            $http({
                method:'post',
                url:srvUrl+"preBidding/addPolicyDecision.do",
                data:  $.param({"json":JSON.stringify(pData),"method":method})
            }).success(function(data){
                if(data.success){
                    var alertData = "";
                    if(method == "so"){
                        hide_Mask();
                        alertData = "保存成功!";
                    }else if(method == "ss"){
                        if(data.result_data){
                            hide_Mask();
                            alertData = "提交成功!";
                            $location.path("/PreBiddingInfoView/"+$scope.businessId+"@view/"+$filter('encodeURI')('#/PreBiddingInfoList/1'));
                        }else{
                            hide_Mask();
                            $.alert("请确保参会信息已填写完毕!");
                            return false;
                        }
                    }
                    $.alert(alertData);
                }else{
                    $.alert(data.result_data);
                }
            }).error(function(data,status,headers, config){
                $.alert(status);
            });
        }

        $scope.dataForSave = function(){
            var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);

            if(newAttachment == false){
                return false;
            }

            var chk_list = $("input[name='choose']");
            var fid = "";
            var uuidarr=[],itemarr=[],programmedarr=[],approvedarr=[],fileNamearr=[],filePatharr=[],versionarr=[],upload_datearr=[],programmeIddarr=[],approvedIdarr=[];
            for(var i=0;i<chk_list.length;i++) {
                if(chk_list[i].checked)
                {
                    fid = chk_list[i].value;
                    var arrfid=fid.split("||");
                    if(arrfid[0]==null || arrfid[0]==""){
                        uuidarr.push($scope.newAttachment[i].newItem.UUID);
                    }else{
                        uuidarr.push(arrfid[0]);
                    }
                    if(arrfid[1]==null || arrfid[1]==""){
                        itemarr.push($scope.newAttachment[i].newItem.ITEM_NAME);
                    }else{
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
            for(var i=0;i<newFiles.length;i++) {
                if(newFiles[i].checked)
                {
                    fid = newFiles[i].value;
                    var arrfid=fid.split("||");
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

            var array=[];
            if(undefined==$scope.preBidding.policyDecision){
                $scope.preBidding.policyDecision={};
            }
            $scope.preBidding.policyDecision.submitName=$scope.credentials.userName;
            $scope.preBidding.policyDecision.submitDate = $scope.FormatDate();
            if(undefined==$scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles){
                $scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles=[];
            }

            for(var j=0;j<fileNamearr.length;j++) {
                $scope.vvvv = {};
                $scope.vvvv.UUID =uuidarr[j];
                $scope.vvvv.ITEM_NAME =itemarr[j];
                $scope.vvvv.programmed =programmedarr[j];
                $scope.vvvv.approved =approvedarr[j];
                $scope.vvvv.fileName =fileNamearr[j];
                $scope.vvvv.filePath =filePatharr[j];
                $scope.vvvv.version =versionarr[j];
                if(upload_datearr[j]==""){
                    upload_datearr[j] = $scope.getDate();
                }
                $scope.vvvv.upload_date =upload_datearr[j];
                $scope.vvvv.programmedId =programmeIddarr[j];
                $scope.vvvv.approvedID =approvedIdarr[j];
                array.push($scope.vvvv);
            }
            $scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles = array;

            $scope.preBidding.ac_attachment = $scope.pfr.attachment;
            $scope.preBidding.reviewReport = $scope.pfr.reviewReport;
            return  $scope.preBidding;
        }

        $scope.dataForSubmit = function(){

            var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);

            if(newAttachment == false){
                return false;
            }

            var file=$scope.preBidding.policyDecision.fileList;

            var chk_list = $("input[name='choose']")
            var fid = "";
            var uuidarr=[],itemarr=[],programmedarr=[],approvedarr=[],fileNamearr=[],filePatharr=[],versionarr=[],upload_datearr=[],programmeIddarr=[],approvedIdarr=[];

            for(var i = 0;i<chk_list.length;i++){
                var fid = chk_list[i].value;
                var arrfid=fid.split("||");

//            if(arrfid[2] == "" || arrfid[8] == ""){
//            	$.alert("编制人不能为空!");
//            	return false;
//            }

//            if(arrfid[3] == "" || arrfid[9] == ""){
//            	$.alert("审核人不能为空!");
//            	return false;
//            }
//
                if(arrfid[4] == "" || arrfid[4] == null){
                    $.alert("请上传附件！");
                    return false;
                }
            }

            for(var i=0;i<chk_list.length;i++) {
                if(chk_list[i].checked)
                {
                    fid = chk_list[i].value;
                    var arrfid=fid.split("||");
                    if(arrfid[0]==null || arrfid[0]==""){
                        uuidarr.push($scope.newAttachment[i].newItem.UUID);
                    }else{
                        uuidarr.push(arrfid[0]);
                    }
                    if(arrfid[1]==null || arrfid[1]==""){
                        itemarr.push($scope.newAttachment[i].newItem.ITEM_NAME);
                    }else{
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

            for(var i = 0;i<newFiles.length;i++){
                var fid = newFiles[i].value;
                var arrfid=fid.split("||");

                if(arrfid[4] == "" || arrfid[4] == null){
                    $.alert("请上传附件！");
                    return false;
                }
            }

            for(var i=0;i<newFiles.length;i++) {
                if(newFiles[i].checked)
                {
                    fid = newFiles[i].value;
                    var arrfid=fid.split("||");
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

            var array=[];
            if(undefined==$scope.preBidding.policyDecision){
                $scope.preBidding.policyDecision={};
            }
            $scope.preBidding.policyDecision.submitName=$scope.credentials.userName;
            $scope.preBidding.policyDecision.submitDate=$scope.FormatDate();
            if(undefined==$scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles){
                $scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles=[];
            }

            for(var j=0;j<fileNamearr.length;j++) {

                $scope.vvvv = {};
                $scope.vvvv.UUID = uuidarr[j];
                $scope.vvvv.ITEM_NAME = itemarr[j];
                $scope.vvvv.programmed = programmedarr[j];
                $scope.vvvv.approved = approvedarr[j];
                $scope.vvvv.fileName = fileNamearr[j];
                $scope.vvvv.filePath = filePatharr[j];
                $scope.vvvv.version = versionarr[j];
                if(upload_datearr[j] == ""){
                    upload_datearr[j] = $scope.getDate();
                }
                $scope.vvvv.upload_date = upload_datearr[j];
                $scope.vvvv.programmedId = programmeIddarr[j];
                $scope.vvvv.approvedID = approvedIdarr[j];
                array.push($scope.vvvv);
            }
            $scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles = array;

            $scope.preBidding.ac_attachment = $scope.pfr.attachment;
            return $scope.preBidding;
        }

        //处理附件列表,为提交包装数据
        $scope.reduceAttachmentForSubmit = function(attachment){
            var newAttachment = $scope.newAttachment;

            var now = $scope.getDate();
            //根据uuid处理版本号，上传日期当前
            //获取之前uuid
            for(var j  = 0 in $scope.newPfr.attachment){
                for(var i = 0 in newAttachment){
                    if(newAttachment[i].newFile){
                        if(newAttachment[i].newItem == undefined){
                            $.alert("资源名称不能为空!");
                            return false;
                        }
                        newAttachment[i].ITEM_NAME=newAttachment[i].newItem.ITEM_NAME;
                        newAttachment[i].UUID=newAttachment[i].newItem.UUID;

                        $scope.newAttachment[i].ITEM_NAME=newAttachment[i].newItem.ITEM_NAME;
                        $scope.newAttachment[i].UUID=newAttachment[i].newItem.UUID;

                        if(newAttachment[i].UUID==$scope.newPfr.attachment[j].UUID){
                            //之前版本号
                            //console.log($scope.newPfr.attachment[j].files);
                            //处理版本号问题
                            if(undefined==$scope.newPfr.attachment[j].files){
                                newAttachment[i].version=1;
                            }else{
                                var versionNum = $scope.newPfr.attachment[j].files.length;
                                newAttachment[i].version=versionNum*1+1;
                            }
                            newAttachment[i].newFile=false;
                            newAttachment[i].upload_date=now;
                            newAttachment[i].programmed=newAttachment[i].programmed;
//	    				newAttachment[i].programmed.NAME=newAttachment[i];
//	    				newAttachment[i].programmed.VALUE=$scope.credentials.UUID;
                            newAttachment[i].approved=newAttachment[i].approved;
//	    				newAttachment[i].approved.NAME=$scope.credentials.userName;
//	    				newAttachment[i].approved.VALUE=$scope.credentials.UUID;
                            if(undefined==$scope.newPfr.attachment[j].files){
                                $scope.newPfr.attachment[j].files=[];
                                $scope.newPfr.attachment[j].files.push(newAttachment[i]);
                            }else{
                                $scope.newPfr.attachment[j].files.push(newAttachment[i]);
                            }
                        }

                    }
                }
            }
            return $scope.newPfr.attachment;
        }

        //验证必填项
        $scope.validateFormalBiddingInfo = function(){
            var boolean = false;
            var jprojectType =  $("#jprojectType").val();
            var jinvestmentAmount = $("#jinvestmentAmount").val();
            var jprojectSize = $("#jprojectSize").val();
            var rateOfReturn =  $("#rateOfReturn").val();
            var fkPsResult = $("#fkPsResult").val();
            var fkRiskTip =  $("#fkRiskTip").val();
            var newItem = $("select[class='newItem']");

//    	if(null == jprojectType || "" == jprojectType){
//     	   $.alert("请选择项目类型!"); return boolean;
//     	}else if(null == jinvestmentAmount || "" == jinvestmentAmount){
//    	    $.alert("投资金额不能为空!"); return boolean;
//    	}else if(null == jprojectSize || "" == jprojectSize){
//    	   $.alert("项目规模不能为空!"); return boolean;
//    	}else if(null == rateOfReturn || "" == rateOfReturn){
//    	   $.alert("投资收益率不能为空!"); return boolean;
//    	}else
            if(null == fkPsResult || "" == fkPsResult){
                $.alert("风控中心评审结论不能为空!"); return boolean;
            }else if(null == fkRiskTip || "" == fkRiskTip){
                $.alert("风控重点风险提示不能为空!"); return boolean;
            }else{
                boolean = true;
            }

            return boolean;
        }

        $scope.isAttachmentBeChosen = function(){
            var boolean  = false;
            if($("input[name='choose']:checked").length+$("input[name='choosem']:checked").length==0){
                $.alert("您没有选择要提交的附件!");
            }else{
                boolean = true;
            }

            return boolean;
        }

        //附件列表---->新增列表
        $scope.addFileList = function(){
            function addBlankRow(array){
                var blankRow = {
                    file_content:''
                }
                var size=0;
                for(attr in array){
                    size++;
                }
                array[size]=blankRow;
            }
            if(undefined==$scope.preBidding.policyDecision){
                $scope.preBidding.policyDecision={fileList:[]};
            }
            if(undefined==$scope.preBidding.policyDecision.fileList){
                $scope.preBidding.policyDecision.fileList=[];
            }
            addBlankRow($scope.preBidding.policyDecision.fileList);
        }

        //附件列表---->删除指定的列表
        $scope.commonDdelete = function(){
            var commentsObj = $scope.preBidding.policyDecision.fileList;
            if(commentsObj!=null){
                for(var i=0;i<commentsObj.length;i++){
                    if(commentsObj[i].selected){
                        commentsObj.splice(i,1);
                        i--;
                    }
                }
            }
        }

        //附件列表---->上传附件
        $scope.errorAttach=[];
        $scope.upload = function (file,errorFile, idx) {
            if(errorFile && errorFile.length>0){
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx]={msg:errorMsg};
            }else if(file){
                var fileFolder = "preBidding/";
                var dates=$scope.preBidding.create_date;
                var no=$scope.preBidding.projectNo;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates+"/"+no;

                $scope.errorAttach[idx]={msg:''};
                Upload.upload({
                    url:srvUrl+'file/uploadFile.do',
                    data: {file: file, folder:fileFolder}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope.preBidding.policyDecision.fileList[idx].files=retData;
                    $.alert("文件替换成功！请执行保存操作！否则操作无效！");
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                });
            }
        };

        //业务单位上报评审文件-投资部门提供---->新增列表
        $scope.addFileList1 = function(){
            function addBlankRow1(array){
                var blankRow = {
                    newFile:true
                }
                var size = array.length;
                array[size]=blankRow;
            }
            if(undefined==$scope.newPfr.attachment){
                $scope.newAttachment={files:[]};
            }
            addBlankRow1($scope.newAttachment);
        }

        //业务单位上报评审文件-投资部门提供---->删除指定的列表
        $scope.deleteFileList = function(){
            var i  = 0 ;
            $(".deleteSelect:checked").each(function(){
                if(i>0){
                    $scope.newAttachment.splice(this.value-i,1);
                }else{
                    $scope.newAttachment.splice(this.value,1);
                }
                i++;
            });
            $(".deleteSelect:checked").attr("checked",false);
        }

        //业务单位上报评审文件-投资部门提供---->上传附件
        $scope.errorMsg=[];
        $scope.upload2 = function (file,errorFile, idx) {
            if(errorFile && errorFile.length>0){
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorMsg[idx]={msg:errorMsg};
            }else if(file){
                var fileFolder = "pfrAssessment/";
                var dates=$scope.preBidding.create_date;
                var no=$scope.preBidding.projectNo;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates+"/"+no;
                Upload.upload({
                    url:srvUrl+'file/uploadFile.do',
                    data: {file: file, folder:fileFolder}
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
                    $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                });
            }
        };
        //报告上传
        $scope.uploadReprot = function (file,errorFile, idx) {
            if(errorFile && errorFile.length>0){
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx]={msg:errorMsg};
            }else if(file){
                $scope.errorAttach[idx]={msg:''};
                Upload.upload({
                    url:srvUrl+'file/uploadFile.do',
                    data: {file: file, folder:'',typeKey:'preAssessmentReportPath'}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope.pfr.reviewReport.filePath = retData.filePath;
                    $.alert("文件替换成功！请执行保存操作！否则操作无效！");
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                });
            }
        };
        //检查压缩文件
        $scope.checkFileZip = function(name,ev){
            if(name == null){
                var e=ev||window.event;//获取事件
                var ele=e.target||e.srcElement;//获取触发事件的元素
                ele.checked=false;
                $.alert("请上传文件！");
                return false;
            }
            var index = name.lastIndexOf('.');
            var suffix  = name.substring(index+1);
            if("rar" == suffix || "zip" == suffix || "7z" == suffix){
                var e=ev||window.event;//获取事件
                var ele=e.target||e.srcElement;//获取触发事件的元素
                ele.checked=false;
                $.alert("上会附件不能是压缩文件！");
                return false;
            }
            return true;
        };

        // 整理预览界面需要的json数据
        $scope.previewJson = function () {
            var prePreview = $scope.preBidding.apply;
            prePreview.stage = $scope.stage;
            prePreview.applyDate = $scope.applyDate;
            prePreview.apply = $scope.preBidding.apply;      // 存入项目信息
            prePreview.taskallocation = $scope.pfr.taskallocation;
            prePreview.cesuanFileOpinion = $scope.pfr.cesuanFileOpinion;
            prePreview.tzProtocolOpinion = $scope.pfr.tzProtocolOpinion;
            prePreview.approveAttachment = $scope.pfr.approveAttachment;  // 风控中心审批
            prePreview.approveLegalAttachment = $scope.pfr.approveLegalAttachment;  // 风控中心法律审批
            prePreview.submit_date = $scope.preBidding.submit_date;   // 评审报告出具时间
            if ($scope.preBidding.meetingInfo != null) {
                prePreview.projectRating = $scope.preBidding.meetingInfo.projectRating; // 评审等级
                prePreview.investmentType = $scope.preBidding.meetingInfo.investmentType; // 投资建议
            } else {
                prePreview.projectRating = '';
                prePreview.investmentType = '';
            }
            prePreview.filePath = $scope.preBidding.filePath;
            prePreview.projectName = $scope.preBidding.apply.projectName;
            if ($scope.preBidding.policyDecision != undefined) {
                prePreview.fileList = $scope.preBidding.policyDecision.fileList;
            } else {
                prePreview.fileList = [];
            }
            if ($scope.newAttachment != undefined) {
                prePreview.newAttachment = $scope.newAttachment;
            } else {
                prePreview.newAttachment = [];
            }

            prePreview.mark = $scope.mark; // 分数

            prePreview.url = $scope.oldUrl;
            prePreview.id = complexId;

            return prePreview;
        }

        // 进入预览页面
        $scope.toPreview = function () {
            $scope.saveDataToLocalStorage();
            $location.path("/PreBiddingInfoPreview").search({ prePreview: $scope.previewJson()});
        }

        // 预览时将数据存入浏览器缓存，以便退出预览时使用
        $scope.saveDataToLocalStorage = function () {
            if(!window.localStorage){
                alert("浏览器不支持localstorage");
                return false;
            }else{
                var storage = window.localStorage;
                storage.projectName = $scope.preBidding.projectName;
                if ($scope.preBidding.policyDecision != undefined) {
                    storage.fileList = JSON.stringify($scope.preBidding.policyDecision.fileList);
                } else {
                    storage.fileList = [];
                }
                if ($scope.newAttachment != undefined){
                    storage.newAttachment = JSON.stringify($scope.newAttachment);
                }
                storage.mark = JSON.stringify($scope.mark);
                if ($scope.preBidding.meetingInfo != null) {
                    storage.investmentType = JSON.stringify($scope.preBidding.meetingInfo.investmentType);
                    storage.projectRating = JSON.stringify($scope.preBidding.meetingInfo.projectRating);
                    storage.ratingReason = $scope.preBidding.meetingInfo.ratingReason;
                    storage.projectType1 = $scope.preBidding.meetingInfo.projectType1;
                    storage.projectType2 = $scope.preBidding.meetingInfo.projectType2;
                    storage.projectType3 = $scope.preBidding.meetingInfo.projectType3;
                    storage.isUrgent = $scope.preBidding.meetingInfo.isUrgent;
                } else {
                    storage.investmentType = '';
                    storage.projectRating = '';
                    storage.ratingReason = '';
                    storage.projectType1 = '';
                    storage.projectType2 = '';
                    storage.projectType3 = '';
                    storage.isUrgent = '';
                }
            }
        }

        $scope.initData();
    }]);